/**
 * Project: Game Hub
 * File: Renderer.java
 * Purpose: Presentation layer (state -> string).
 */
package game.core;

public interface Renderer<S> {
    /** Render the current state as a string for terminal output. */
    String render(S state);
}
