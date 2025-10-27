package puzzles.quoridor;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.Player;
import game.core.Renderer;
import game.core.TurnAgent;

import java.util.Arrays;
import java.util.List;

public final class QuoridorGame implements Game {
    private final ConsoleIO io;
    private final QuoridorRules rules = new QuoridorRules();

    public QuoridorGame(ConsoleIO io) { this.io = io; }

    @Override
    public void run() {
        while (true) {
            io.println("\n=== Quoridor ===");
            io.println("1) Play");
            io.println("2) Rules");
            io.println("3) High Scores");
            io.println("4) Back");
            io.print("Choice: ");
            String line = io.nextLine();
            if (line == null) return;
            line = line.trim();

            if ("1".equals(line)) {
                playRound();
            } else if ("2".equals(line)) {
                io.println(QuoridorRulesText.text());
            } else if ("3".equals(line)) {
                io.println(QuoridorStats.get().summary());
            } else if ("4".equals(line)) {
                return; // back to main menu
            } else {
                io.println("Please choose 1-4.");
            }
        }
    }

    // --- One full game round -------------------------------------------------
    private void playRound() {
        // Size
        int n = 9, m = 9;
        io.print("Enter board size (rows cols, default 9 9): ");
        String sizeLine = io.nextLine();
        if (sizeLine != null && !sizeLine.trim().isEmpty()) {
            try {
                String[] p = sizeLine.trim().split("\\s+");
                if (p.length == 1) {
                    int k = Integer.parseInt(p[0]);
                    if (k >= 3) { n = k; m = k; }
                } else if (p.length >= 2) {
                    n = Math.max(3, Integer.parseInt(p[0]));
                    m = Math.max(3, Integer.parseInt(p[1]));
                }
            } catch (Exception ignore) {}
        }

        QuoridorState state = new QuoridorState(n, m);

        // Names
        state.name1 = readLineOrDefault("Player 1 name [A]: ", "A");
        state.name2 = readLineOrDefault("Player 2 name [B]: ", "B");

        // Agents (use List, not generic array -> no unchecked warnings)
        List<TurnAgent<QuoridorState, QuoridorAction>> agents = Arrays.asList(
            new QuoridorHuman(new Player(1, state.name1), io),
            new QuoridorHuman(new Player(2, state.name2), io)
        );

        Renderer<QuoridorState> renderer = new QuoridorRenderer(true, true, true);
        long t0 = System.currentTimeMillis();
        int wallsBeforeP1 = state.walls1, wallsBeforeP2 = state.walls2;

        io.println(renderer.render(state));

        while (!rules.isTerminal(state)) {
            TurnAgent<QuoridorState, QuoridorAction> agent = agents.get(state.turn - 1);
            QuoridorAction a = agent.chooseAction(state);
            if (a == null) {
                io.println("Quit.");
                return;
            }

            String err = rules.validationError(state, a);
            if (err != null) {
                io.println("Invalid: " + err);
                continue;
            }

            state = rules.apply(state, a);
            io.println(renderer.render(state));
        }

        String winner = (state.p1.r == state.rows - 1) ? state.name1 : state.name2;
        io.println(winner + " wins!");

        QuoridorStats.get().onGameEnd(
            state.name1, state.name2, winner,
            state.moveCount,
            wallsBeforeP1 - state.walls1, wallsBeforeP2 - state.walls2,
            state.jumps1, state.jumps2,
            System.currentTimeMillis() - t0
        );

        // After a round, offer to show session stats quickly
        io.println("\n=== Session Summary ===");
        io.println("[Session] " + QuoridorStats.get().quickLine());
    }

    private String readLineOrDefault(String prompt, String def) {
        io.print(prompt);
        String s = io.nextLine();
        if (s == null) return def;
        s = s.trim();
        return s.isEmpty() ? def : s;
    }
}
