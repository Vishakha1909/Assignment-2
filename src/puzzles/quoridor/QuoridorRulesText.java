package puzzles.quoridor;

final class QuoridorRulesText {
    private QuoridorRulesText() {}

    static String text() {
        return new StringBuilder()
            .append("\n--- Quoridor Rules ---\n")
            .append("Goal: Reach the opposite side first.\n\n")
            .append("Board:\n")
            .append("  - Rectangular grid (default 9x9).\n")
            .append("  - Each player starts centered on their first/last row.\n")
            .append("\nOn your turn, do ONE:\n")
            .append("  1) move r c  -> move your pawn to a reachable adjacent cell,\n")
            .append("                  or jump over the opponent if they are adjacent.\n")
            .append("                  If jumping straight is blocked by a wall, you\n")
            .append("                  may side-step diagonally around the opponent.\n")
            .append("  2) wall H r c -> place a 2-segment horizontal wall (under row r)\n")
            .append("  3) wall V r c -> place a 2-segment vertical wall (to the right of col c)\n")
            .append("\nWalls:\n")
            .append("  - Cannot overlap or cross other walls.\n")
            .append("  - Must not block ALL paths to the goal for either player.\n")
            .append("  - Each player has a limited number of walls (~ min(rows,cols)+1).\n")
            .append("\nWin:\n")
            .append("  - P1 wins by reaching the last row.\n")
            .append("  - P2 wins by reaching the first row.\n")
            .append("\nTips:\n")
            .append("  - Use 'help' during your turn to see commands.\n")
            .append("  - Coordinates are 0-based; use the printed indices on the grid.\n")
            .toString();
    }
}
