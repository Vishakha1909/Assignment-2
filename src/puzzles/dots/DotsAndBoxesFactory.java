/**
 * Project: Game Hub
 * File: DotsAndBoxesFactory.java
 * Purpose: Difficulty + user-friendly custom sizing; builds a DotsGame.
 */
package puzzles.dots;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.GameFactory;

/** Dots: factory now just returns a game shell; config happens on Play. */
public final class DotsAndBoxesFactory implements GameFactory {
    private final SessionStats stats = new SessionStats();

    @Override public String name() { return "Dots & Boxes"; }

    @Override public Game create(ConsoleIO io) {
        return new DotsGame(io, stats); // size & players asked on Play
    }
}

