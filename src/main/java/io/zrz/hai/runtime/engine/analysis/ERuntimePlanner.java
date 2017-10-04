package io.zrz.hai.runtime.engine.analysis;

/**
 * The planner takes care of resolution and ordering, however some details of a
 * plan remain left unknown until execution time. This step performs a fast
 * optimization step on a plan at execution time when input parameters are
 * available.
 *
 * This pass merges expressions which are equal, for example two connection
 * index accesses using different variables but have the same input value.
 *
 * In addition to happening at the start of an execution, it can happen on a per
 * nested scan basis too, as there may be some information available once we've
 * resolved some details which can help with performance.
 *
 */

public class ERuntimePlanner {

}
