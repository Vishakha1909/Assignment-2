/**
 * Project: Game Hub
 * File: GoalStrategy.java
 * Purpose: Strategy for detecting solved state.
 */
package puzzles.sliding;

public interface GoalStrategy {
    boolean isGoal(SlidingState s);
}
