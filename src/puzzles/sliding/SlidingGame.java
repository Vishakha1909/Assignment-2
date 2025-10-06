/**
 * Project: Game Hub
 * File: SlidingGame.java
 * Purpose: Terminal loop for sliding puzzle (render, input, validate, apply).
 */
package puzzles.sliding;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.Renderer;

/** Sliding Puzzle with mini-menu; config asked only when you pick Play. */
public final class SlidingGame implements Game {
    private final ConsoleIO io;
    private final SlidingRules rules = new SlidingRules();
    private final Renderer<SlidingState> renderer = new SlidingRenderer();
    private final SessionBest best;   // session best per size

    public SlidingGame(ConsoleIO io, SessionBest best) {
        this.io = io; this.best = best;
    }

    @Override public void run() {
        while (true) {
            io.println("\n=== Sliding Puzzle ===");
            io.println("1) Play");
            io.println("2) Rules");
            io.println("3) Best (fewest moves) by size");
            io.println("4) Back");
            io.print("Choice: ");
            String s = io.nextLine().trim();
            if (s.equals("4")) return;
            if (s.equals("2")) { showRules(); continue; }
            if (s.equals("3")) { showBest(); continue; }
            if (s.equals("1")) { playOnce(); continue; }
        }
    }

    private void playOnce() {
        // ask for config NOW (after Play)
        int rows = askInt("Rows (>=2) [3]: ", 3, 2, 50);
        int cols = askInt("Cols (>=2) [3]: ", 3, 2, 50);
        io.println("Difficulty: 1) Easy  2) Medium  3) Hard");
        int d = askInt("Choice [2]: ", 2, 1, 3);
        int steps = (d==1? 10 : d==3? 200 : 60);
        io.print("Seed (Enter for random): ");
        String s = io.nextLine().trim();
        long seed = (s.isEmpty() ? System.currentTimeMillis() : parseLong(s, System.currentTimeMillis()));

        String key = rows + "x" + cols;
        SlidingState start = SolverUtils.randomSolvable(rows, cols, seed);
        Shuffler shuffler = new RandomMoveShuffler(seed);
        SlidingState state = shuffler.shuffle(start, steps);

        io.println("Enter a tile number to slide, or 'q' to quit the round.");
        while (true) {
            io.println(renderer.render(state));
            if (rules.isTerminal(state)) {
                io.println("Solved in " + state.moves + " moves!");
                best.consider(key, state.moves);
                break;
            }
            io.print("Tile (or q): ");
            String in = io.nextLine().trim();
            if (in.equalsIgnoreCase("q")) break;
            int v; try { v = Integer.parseInt(in); } catch (Exception e) { io.println("Enter a number."); continue; }
            SlideAction a = new SlideAction(v);
            if (!rules.isValid(state, a)) { io.println("Invalid: " + rules.validationError(state, a)); continue; }
            state = rules.apply(state, a);
        }
    }

    private void showRules() {
        io.println("\nRules:");
        io.println("- Slide a tile adjacent to the blank into the blank.");
        io.println("- Goal: arrange tiles 1..N with the blank at bottom-right.");
        io.println("- Boards are generated solvable (parity test).");
        io.println("- Difficulty controls how many random moves scramble the board.");
    }

    private void showBest() {
        io.println("\nSession Bests (fewest moves):");
        // We don’t store all keys; just tell user it updates after you solve.
        io.println("- After you solve a size, its best score will be tracked for this session.");
    }

    private int askInt(String prompt, int def, int min, int max) {
        io.print(prompt);
        String s = io.nextLine().trim();
        if (s.isEmpty()) return def;
        try { int v = Integer.parseInt(s); if (v<min) v=min; if (v>max) v=max; return v; }
        catch (Exception e){ return def; }
    }
    private static long parseLong(String s, long def){ try { return Long.parseLong(s); } catch(Exception e){ return def; } }
}
