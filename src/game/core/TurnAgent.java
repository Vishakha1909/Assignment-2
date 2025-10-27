package game.core;

/** A controller that picks an action for a given state (human or AI). */
public interface TurnAgent<S, A> {
    /** The player this agent controls. */
    Player player();

    /** Produce an action for the current state. Must not mutate the state. */
    A chooseAction(S state);
}
