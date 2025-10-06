/**
 * Project: Game Hub
 * File: Rules.java
 * Purpose: Game rules contract (terminal, validation, state transition).
 */
package game.core;

public interface Rules<S, A> {
    /** @return true if the state is terminal (win/loss/draw). */
    boolean isTerminal(S state);

    /** @return true if the action is valid in the given state. */
    boolean isValid(S state, A action);

    /**
     * Apply the action and return the next state.
     * @return next state after applying action
     */
    S apply(S state, A action);

    /** @return user-friendly error reason for invalid action. */
    String validationError(S state, A action);
}
