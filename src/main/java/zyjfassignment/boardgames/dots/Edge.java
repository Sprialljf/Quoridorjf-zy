
package zyjfassignment.boardgames.dots;

public class Edge {
    private final int id;
    private final boolean horizontal;
    private final int r, c;
    private int owner; // 0 none, 1 or 2

    public Edge(int id, boolean horizontal, int r, int c) {
        this.id = id; this.horizontal = horizontal; this.r = r; this.c = c;
        this.owner = 0;
    }

    public int id() { return id; }
    public boolean isHorizontal() { return horizontal; }
    public int row() { return r; }
    public int col() { return c; }
    public int owner() { return owner; }
    public boolean isClaimed() { return owner != 0; }

    public void setOwner(int playerId) {
        if (isClaimed()) throw new IllegalStateException("Edge already claimed");
        if (playerId != 1 && playerId != 2) throw new IllegalArgumentException("playerId must be 1 or 2");
        owner = playerId;
    }
}
