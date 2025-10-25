package app;

import game.core.ConsoleIO;
import game.core.GameRegistry;
import game.core.SessionStatsHub; // new (below)

// existing factories
import puzzles.sliding.SlidingFactory;
import puzzles.dots.DotsAndBoxesFactory;
// NEW:
import puzzles.quoridor.QuoridorFactory;

public final class Main {
    public static void main(String[] args) {
        ConsoleIO io = new ConsoleIO();
        GameRegistry reg = new GameRegistry();

        // one-liners: register factories; no logic here
        reg.register(new SlidingFactory());
        reg.register(new DotsAndBoxesFactory());
        reg.register(new QuoridorFactory()); //

        // optional: print a single-session summary on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            io.println("\n" + SessionStatsHub.get().summaryAll())
        ));

        new AppMenu(io, reg).run();
    }
}
