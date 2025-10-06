/**
 * Project: Game Hub
 * File: Board.java
 * Purpose: Generic 2D board abstraction.
 */
package game.core;

public interface Board<T> {
    int rows();
    int cols();
    T get(int r, int c);
    void set(int r, int c, T value);
    default boolean inBounds(int r, int c) { return r>=0 && r<rows() && c>=0 && c<cols(); }
}
