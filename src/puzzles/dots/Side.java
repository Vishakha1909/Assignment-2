package puzzles.dots;

/** Box sides for user-friendly input. */
enum Side {
    TOP, BOTTOM, LEFT, RIGHT;
    static Side parse(String s) {
        String x = s.toLowerCase();
        if (x.equals("t") || x.equals("top")) return TOP;
        if (x.equals("b") || x.equals("bottom")) return BOTTOM;
        if (x.equals("l") || x.equals("left")) return LEFT;
        if (x.equals("r") || x.equals("right")) return RIGHT;
        return null;
    }
}
