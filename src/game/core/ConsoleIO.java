/**
 * Project: Game Hub
 * File: ConsoleIO.java
 * Purpose: Simple wrapper for console input/output.
 */
package game.core;

import java.util.Scanner;

public final class ConsoleIO {
    private final Scanner in = new Scanner(System.in);

    /** Print a line. */
    public void println(String s) { System.out.println(s); }

    /** Print without newline. */
    public void print(String s) { System.out.print(s); }

    /** Read one input line. */
    public String nextLine() { return in.nextLine(); }
}
