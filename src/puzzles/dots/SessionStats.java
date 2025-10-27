package puzzles.dots;

/** Session-only stats (resets each run). */
public final class SessionStats {
    public int winsA = 0, winsB = 0, ties = 0;

    public String summary() {
        return "Wins A =" + winsA + ", Wins B =" + winsB + ", Ties =" + ties;
    }
}
