/**
 * Project: Game Hub
 * File: SlidingFactory.java
 * Purpose: Build a sliding puzzle with difficulty + optional seed.
 */
package puzzles.sliding;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.GameFactory;

/** Sliding: factory now just returns a game shell; config happens on Play. */
public final class SlidingFactory implements GameFactory {
    // session bests per factory instance
    private final SessionBest best = new SessionBest();

    @Override public String name() { return "Sliding Puzzle"; }

    @Override public Game create(ConsoleIO io) {
        return new SlidingGame(io, best); // no sizing/difficulty here
    }
}
