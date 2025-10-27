package app;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.GameFactory;
import game.core.GameRegistry;

public final class AppMenu {
    private final ConsoleIO io;
    private final GameRegistry reg;

    public AppMenu(ConsoleIO io, GameRegistry reg) {
        this.io = io; this.reg = reg;
    }

    public void run() {
        while (true) {
            io.println("\nSelect a game:");
            for (int i = 0; i < reg.list().size(); i++) {
                io.println((i + 1) + ") " + reg.list().get(i).name());
            }
            io.println((reg.list().size() + 1) + ") Quit");
            io.print("Choice: ");
            String s = io.nextLine().trim();
            int ch;
            try { ch = Integer.parseInt(s); } catch (Exception e) { continue; }
            if (ch == reg.list().size() + 1) return;
            if (ch < 1 || ch > reg.list().size()) continue;

            GameFactory f = reg.list().get(ch - 1);
            Game g = f.create(io); // Factory wires rules/renderer/state/stats
            g.run();
        }
    }
}
