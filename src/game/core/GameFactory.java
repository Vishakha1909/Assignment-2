/**
 * Project: Game Hub
 * File: GameFactory.java
 * Purpose: Factory for building a configured game instance.
 */
package game.core;

public interface GameFactory {
    /** Human-readable name for the launcher menu. */
    String name();

    /**
     * Create a game instance, gathering any necessary configuration via the provided IO.
     * @param io console IO helper
     * @return a ready-to-run Game
     */
    Game create(ConsoleIO io);
}
