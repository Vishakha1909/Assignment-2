/**
 * Project: Game Hub
 * File: RandomMoveShuffler.java
 * Purpose: Shuffle by performing random valid moves (guarantees solvable).
 */
package puzzles.sliding;

import game.core.Direction;
import game.core.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomMoveShuffler implements Shuffler {
    private final Random rng;
    public RandomMoveShuffler(long seed) { this.rng = new Random(seed); }

    @Override public SlidingState shuffle(SlidingState start, int steps) {
        SlidingRules rules = new SlidingRules();
        SlidingState s = copy(start);
        for (int i=0;i<steps;i++) {
            List<Direction> ds = legalDirs(s);
            if (ds.isEmpty()) break;
            Direction d = ds.get(rng.nextInt(ds.size()));
            int rr = s.empty.r + d.dr, cc = s.empty.c + d.dc;
            int tile = s.board.get(rr, cc).value();
            s = rules.apply(s, new SlideAction(tile));
        }
        return new SlidingState(s.board, s.empty, 0);
    }

    private List<Direction> legalDirs(SlidingState s) {
        List<Direction> ds = new ArrayList<Direction>(4);
        int r = s.empty.r, c = s.empty.c;
        for (Direction d : Direction.values()) {
            int rr = r + d.dr, cc = c + d.dc;
            if (rr>=0 && rr<s.board.rows() && cc>=0 && cc<s.board.cols()) ds.add(d);
        }
        return ds;
    }

    private SlidingState copy(SlidingState s) {
        GridBoard b = new GridBoard(s.board.rows(), s.board.cols());
        for (int i=0;i<b.rows();i++)
            for (int j=0;j<b.cols();j++)
                b.set(i,j,new SlidingTile(s.board.get(i,j).value()));
        return new SlidingState(b, new Position(s.empty.r, s.empty.c), 0);
    }
}
