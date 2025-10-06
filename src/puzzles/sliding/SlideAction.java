/**
 * Project: Game Hub
 * File: SlideAction.java
 * Purpose: User intends to slide a tile with this value into the blank.
 */
package puzzles.sliding;

public final class SlideAction {
    public final int tileValue;
    public SlideAction(int tileValue) { this.tileValue = tileValue; }
    @Override public String toString() { return "slide " + tileValue; }
}
