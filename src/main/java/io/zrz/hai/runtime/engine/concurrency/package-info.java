
/**
 * Handles conflict detection and optimistic concurrency.
 *
 * each read or write is recorded in a context, which is then exported into a
 * changeset.
 *
 * at commit time, we validate the read dependencies, ensure there are no write
 * conflicts, then commit.
 *
 * Any data operation is based on a set of parent commits, which includes all
 * transactions that had been committed at the time the operation started.
 *
 * a transaction can not commit unless it can be merged with any changes that
 * have committed since the transaction started.
 *
 * within a change set, there is a sequence number too, which identifies the
 * state at that point in time.
 *
 * a clock includes 3 different sets: (1) the global resolved sequence number.
 * (2) local node commit IDs, (3) uncommitted changeset IDs.
 *
 */

package io.zrz.hai.runtime.engine.concurrency;
