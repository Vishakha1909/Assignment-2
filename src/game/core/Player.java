package game.core;

/** Minimal player identity shared by all games. */
public final class Player {
    private final int id;           // 1, 2, ...
    private final String name;

    public Player(int id, String name) {
        this.id = id; this.name = name == null ? ("Player" + id) : name;
    }
    public int id()      { return id; }
    public String name() { return name; }
}
