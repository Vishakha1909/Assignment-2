/**
 * Project: Game Hub (Sliding Puzzle + Dots & Boxes)
 * File: Main.java
 * Purpose: Concise, generic launcher (no game-specific logic).
 * Author: Your Name
 * Partner: Partner Name
 * Course: CS 611 – Assignment 2 (Java 8)
 * Notes: Select a game, the chosen GameFactory builds a Game, then run().
 */
package app;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.GameFactory;
import game.core.GameRegistry;
import puzzles.dots.DotsAndBoxesFactory;
import puzzles.sliding.SlidingFactory;

public final class Main {
    public static void main(String[] args) {
        ConsoleIO io = new ConsoleIO();
        GameRegistry reg = new GameRegistry()
                .register(new SlidingFactory())
                .register(new DotsAndBoxesFactory());

        io.println("=== Welcome to the Game Hub ===");
        while (true) {
            io.println("\nSelect a game:");
            int i = 1;
            for (GameFactory f : reg.list()) io.println((i++) + ") " + f.name());
            io.println(i + ") Quit");
            io.print("Choice: ");

            int choice;
            try { choice = Integer.parseInt(io.nextLine().trim()); }
            catch (Exception e) { io.println("Enter a number."); continue; }

            if (choice == i) { io.println("Goodbye!"); break; }
            if (choice < 1 || choice > reg.list().size()) { io.println("Invalid menu choice."); continue; }

            GameFactory selected = reg.list().get(choice - 1);
            Game game = selected.create(io);
            game.run();  // returns to the hub after a round
        }
    }
}
