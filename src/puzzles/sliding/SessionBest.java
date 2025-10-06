package puzzles.sliding;

import java.util.HashMap;
import java.util.Map;

/** Fewest moves per size, session-only. Key: "RxC" -> best moves. */
public final class SessionBest {
    private final Map<String,Integer> best = new HashMap<String,Integer>();

    public void consider(String key, int moves) {
        Integer cur = best.get(key);
        if (cur == null || moves < cur) best.put(key, moves);
    }

    public String get(String key) {
        Integer cur = best.get(key);
        return cur == null ? "—" : String.valueOf(cur);
    }
}
