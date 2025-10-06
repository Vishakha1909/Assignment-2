/**
 * Project: Game Hub
 * File: SlidingRenderer.java
 * Purpose: ASCII renderer for any m×n sliding puzzle board.
 */
package puzzles.sliding;

import game.core.Renderer;

public final class SlidingRenderer implements Renderer<SlidingState> {
    @Override public String render(SlidingState s) {
        StringBuilder sb = new StringBuilder();
        int rows = s.board.rows(), cols = s.board.cols();

        for (int r=0;r<rows;r++) {
            for (int c=0;c<cols;c++) {
                sb.append("+---");
            }
            sb.append("+\n");
            for (int c=0;c<cols;c++) {
                String lab = s.board.get(r,c).label();
                if (lab.length()==1) lab = " " + lab + " ";
                else if (lab.length()==2) lab = " " + lab;
                sb.append("|").append(lab);
            }
            sb.append("|\n");
        }
        for (int c=0;c<cols;c++) sb.append("+---");
        sb.append("+\n");
        sb.append("Moves: ").append(s.moves).append("\n");
        return sb.toString();
    }
}
