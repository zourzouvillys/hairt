package io.zrz.hai.runtime.engine.concurrency;

public enum ChangeState {

  /**
   * change is open and still active.
   */

  ACTIVE,

  /**
   * a peer change has committed which conflicts with this one, so it has been
   * aborted by the conflict manager. it may not be committed, and must be
   * retried.
   */

  CONFLICT,

  /**
   * the changeset was abandoned - either due to an application error, or
   * cancellation by the user.
   */

  ABANDONED,

  /**
   * the changeset has been committed, but not yet merged into the global store.
   * it is visible to all, but doesn't have a global sequence number and must thus
   * be referred to using its clock.
   */

  COMMITED,

  /**
   * the changeset has been merged into the global store. It has a global sequence
   * number, which will encapsulate this specific change and all before it.
   */

  MERGED

}
