/**
 * Some notes on planning and execution:
 *
 * a tree is generated that maps to the operation, merged with the model. Each
 * selection is added to a stage, which references a source expression.
 *
 * each HAI method is flattened out, so we end up with the full tree in the
 * plan.
 *
 * after that, we create a set of execution stages that when executed will
 * generate the result set. The stages come in the form of store reads, HAI
 * executions, store writes, and results. Some stages may be run in parallel, in
 * which case they are wrapped in a EParallelStage.
 *
 * Because execution may require traversing multiple servers, stages are self
 * contained and may be serialised. As execution moves through the pipeline,
 * synchronisation steps are triggered which hands execution off to the next
 * stage. Each service node that processes query stages has a full copy of the
 * same version of the model, so expressions which are not inline can be
 * referenced by their handle. This allows execution to move forward as well as
 * executed on each node, rather than needing to move data forward and backward.
 * When results need some sort of merging, we do so in the most lightweight for
 * network and latency - e.g, by reducing as much as we can before sending the
 * results.
 *
 * There is a final stage which collects the results. Some execution stages may
 * send data directly to it, others may go through multiple other stages to
 * build the result set. The results receiver stage maps incoming data to the
 * output, and may batch, defer, or stream results as they come in depending on
 * the configuration.
 *
 * There is also optionally a live receiver stage, which is for @live queries.
 * These attach themselves to the graph as it is processed, and keeps a channel
 * open to the result stage until it is closed.
 *
 * Mutations are handled almost the same as queries, except that they will only
 * execute in parallel if they do not potentially modify data the others will
 * read. For those that do, they are executed sequentially. Also, the selections
 * on the results of a mutation are performed at the point in time of the
 * completion of the mutation, however they do not interrupt execution of any
 * other mutations - which we want to happen as quickly as possible to avoid the
 * risk of conflicts.
 *
 * Any read made during a mutation is stored in the execution state. At the time
 * of commit, the dependencies are checked before persisting. Any changes will
 * result in a concurrency error, and the mutation retried.
 *
 * The next pass at planning is to convert the completed stage to a set of
 * datastore operations. This may be by generating SQL queries, Neo4j
 * traversals, or whatever else is needed based on the store du jour. The
 * backing data store is mostly irrelevant at this stage. We just assume some
 * simple primitives and commands such as locking a base commit (by using a
 * clock), fetching data by path (link/connection), fetching properties,
 * querying indexes, etc.
 *
 * Optimisation and execution planning can merge or split traversals that refer
 * to the same nodes. a traversal of viewer.root.user['theo'] that is referred
 * to in multiple selections may be executed just once, and the node identifier
 * passed through instead. This is especially beneficial if the traversal is
 * inside an edge scan, to avoid lookups multiple times.
 *
 * we can also perform reverse scans. if a edge scan will only continue if there
 * is a node which matches something that is static (e.g, 'viewer.user ==
 * node.owner'), the cost of both sides can be evaluated based on runtime
 * statistics. If the connection being scanned is larger than the reverse path,
 * we can use that to perform the scan - e.g, find the reverse relationships
 * from the user to the node rather than scan every node to see if it matches.
 *
 * we generate runtime statistics that can be used by background optimisation.
 * If we're constantly traversing multiple nodes that results in a slow query
 * because of a lacking index, we can build it automatically. Likewise, over
 * time it can be dropped if it is no longer being used. The same is true for
 * accessing connections by a specific key - e.g, root.users[username: 'theo']
 * can build an index on the username. If these indexes are used on nodes which
 * can be on other instances, we can synchronise them too.
 *
 * Mutation data dependency tracking is optimal where it can be. Incrementing
 * without querying the value will result in no risk of conflict. Branching
 * based on an expression will only fail if that expression no longer holds
 * true, so "if (username != 'theo')" and the username changes from 'bob' to
 * 'alice', then it will be conflict free. we are careful to ensure that
 * logging/debug messages which are not part of the transaction itself do not
 * affect the read dependencies - but mark the value as conflicted in the log
 * with both the new and old value if it changed during the transaction.
 *
 * To commit over multiple nodes, we soft lock the specific fields on each
 * affected node/edge/connection while consistency checks are performed on all
 * nodes. Once completed, we prepare which performs a hard lock, and then
 * coordinate the actual commit, with a timeout.
 *
 * The result is a commit identifier from each node, as well as a clock that
 * includes all of the previous transactions which were part of the mutation.
 * This clock recycled fairly quickly by the background processor, which merges
 * commits and builds a stable snapshot view that includes all previous commits.
 * This ensures that we can perform parallel commits, and not have crazy large
 * vector clocks.
 *
 * For live data, we use execution time feedback to calculate the best
 * subscription mechanism. The size of each edge is calculated, which provides
 * info to let us know where best to place the change markers. After the
 * selection has completed and we know the edge sizes, it is passed to the
 * subscription planner to plant the subscription markers. Each write change is
 * passed to the notification planner which then calculates the notifications
 * which should be raised. between the two, we can optimise subscriptions pretty
 * well.
 *
 *
 * Optimistic conflict detection depends on the engine in use; the basic premise
 * though is to record the state of any dependencies in the generation of the
 * mutation. When commit time comes along, each of those state points are
 * temporarily write-locked and checked. If they all pass, then the commit is
 * allowed through. We use a local in-memory locking system rather than the
 * backing datastore, so it is shared across all stores.
 *
 */

package io.zrz.hai.runtime.engine;
