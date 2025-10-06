/**
 * Project: Game Hub
 * File: SolvabilityPolicy.java
 * Purpose: Policy for checking permutation solvability.
 */
package puzzles.sliding;

import java.util.List;

public interface SolvabilityPolicy {
    boolean isSolvable(List<Integer> perm, int rows, int cols);
}
