/**
 * Project: Game Hub
 * File: GameRegistry.java
 * Purpose: Registry of available games for the launcher.
 */
package game.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameRegistry {
    private final List<GameFactory> factories = new ArrayList<>();

    /** Register a new game factory. */
    public GameRegistry register(GameFactory f) { factories.add(f); return this; }

    /** Immutable list of factories. */
    public List<GameFactory> list() { return Collections.unmodifiableList(factories); }
}
