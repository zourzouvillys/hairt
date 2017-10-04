package io.zrz.hai.runtime.engine.concurrency;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import org.eclipse.collections.api.set.primitive.ImmutableLongSet;
import org.eclipse.collections.impl.factory.primitive.LongSets;

import com.google.common.collect.ImmutableSet;

/**
 * handles the concurrency management - e.g, transactional visibility and
 * tracking.
 *
 * we have an incrementing sequence number that identifies the point in time a
 * transaction started. A transaction which is in progress only sees all other
 * transactions which were committed at the time the transaction began.
 *
 * at commit time, we reference the parent commit which we based the changes on.
 * This reference allows us to re-execute queries at that point in time, without
 * other parallel transactions that have completed showing up in the results.
 *
 * This reference is valid for as long as the transaction read handle is open.
 * Once all references to it are closed, we serialise the transaction in to the
 * transactional sequences, and it receives a stable commit ID which includes
 * the transaction as well as any other previous ones.
 *
 * without this, we can't perform mutations across multiple servers, in a
 * transactional safe way with repeatable reads.
 *
 * @author theo
 *
 */

public class ChangeManager {

  /**
   * the base sequence identifier.
   */

  private final AtomicLong baseId = new AtomicLong(0);

  /**
   * a sequence which is incremented in order with every mutation changeset,
   * irregardless of being committed or not.
   */

  private final AtomicLong changesetAllocator = new AtomicLong(0);

  /**
   * the changesets which have been committed but not yet merged into the global
   * sequence.
   *
   * when a changeset is added to this, any parents are removed. the result is a
   * set of changes which encapsulate all committed changes.
   *
   */

  private final Set<ChangeHandle> headCommits = new HashSet<>();

  /**
   * an ordered list of changesets which have not yet been folded into the head,
   * occurring in the order they were committed.
   *
   * as they are merged into the base, they are removed from here.
   *
   */

  private final LinkedList<ChangeHandle> orderedCommits = new LinkedList<>();

  /**
   * returns the set of head changes that are committed but unmerged which
   * encapsulate all changes that are globally visible. This is the set of changes
   * for which there is still an active read snapshot handle active.
   */

  public ImmutableSet<ChangeHandle> heads() {
    return ImmutableSet.copyOf(this.headCommits);
  }

  public ImmutableSet<ChangeHandle> unmerged() {
    return ImmutableSet.copyOf(this.orderedCommits);
  }

  /**
   * create a snapshot that includes at least the given given
   */

  public ChangeHandle open() {
    return new ChangeHandle(this.changesetAllocator.incrementAndGet(), this.baseId.get(), this.heads());
  }

  /**
   * commits a changeset so it is globally visible to any new changes started
   * after this is called.
   */

  public SnapshotToken commit(ChangeHandle change) {

    change.state = ChangeState.COMMITED;

    // remove any change in the current head commit set which is encapsulated by
    // this change, as any new changes will see them through this one.

    final ImmutableLongSet encapsulates = LongSets.immutable.withAll(change.commits());

    // locked: {
    this.headCommits.removeIf(x -> encapsulates.contains(x.cid));
    this.headCommits.add(change);
    change.bid = this.baseId.incrementAndGet();

    // find the changesets which have been committed since this changeset started,
    // and could potentially conflict.

    System.err.println("Commiting " + change);

    LongStream.range(change.base + 1, change.bid)
        .forEach(x -> {
          System.err.println(" -> Potential conflict: " + x);
        });

    this.orderedCommits.add(change);
    // }

    return new SnapshotToken(change.bid);

  }

  /**
   * open a snapshot handle for reading, which will include at least the provided
   * token as the minimum version.
   */

  public SnapshotHandle open(SnapshotToken atleast) {
    return new SnapshotHandle();
  }

}
