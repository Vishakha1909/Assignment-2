package puzzles.quoridor;

import game.core.ConsoleIO;
import game.core.Player;
import game.core.Position;
import game.core.TurnAgent;

public final class QuoridorHuman implements TurnAgent<QuoridorState, QuoridorAction> {
    private final Player player;
    private final ConsoleIO io;

    public QuoridorHuman(Player p, ConsoleIO io) {
        this.player = p; this.io = io;
    }

    @Override public Player player() { return player; }

    @Override
    public QuoridorAction chooseAction(QuoridorState s) {
        String prompt = player.name() + " (P" + player.id() + ")> ";
        while (true) {
            io.print(prompt);
            String line = io.nextLine();
            if (line == null) return null;
            line = line.trim();
            if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) return null;
            if (line.equalsIgnoreCase("help")) {
                io.println("Commands: move r c  |  wall H r c  |  wall V r c  |  q");
                continue;
            }
            try {
                String[] t = line.split("\\s+");
                String cmd = t[0].toLowerCase();
                if ("move".equals(cmd)) {
                    int r = Integer.parseInt(t[1]), c = Integer.parseInt(t[2]);
                    return QuoridorAction.move(s.currentPawn(), new Position(r, c));
                } else if ("wall".equals(cmd)) {
                    String hv = t[1].toLowerCase();
                    int r = Integer.parseInt(t[2]), c = Integer.parseInt(t[3]);
                    return "h".equals(hv) ? QuoridorAction.wallH(r, c) : QuoridorAction.wallV(r, c);
                } else {
                    io.println("Unknown. Try: move r c | wall H r c | wall V r c | q");
                }
            } catch (Exception e) {
                io.println("Parse error. Try: move r c | wall H r c | wall V r c");
            }
        }
    }
}
