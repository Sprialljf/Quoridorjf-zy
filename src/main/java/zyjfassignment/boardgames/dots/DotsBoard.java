
package zyjfassignment.boardgames.dots;

import zyjfassignment.boardgames.core.GridBoard;
import zyjfassignment.boardgames.core.ui.TextUtil;

import java.util.ArrayList;
import java.util.List;
import static zyjfassignment.boardgames.core.ui.TextUtil.*;

/**
 * DotsBoard
 * Responsible for:
 *  - Edge bookkeeping
 *  - Box ownership updates
 *  - Pretty text rendering with aligned cells
 *
 * Conventions:
 *  - Horizontal edges ids: [0, H)
 *  - Vertical edges ids:   [H, H+V)
 */
public class DotsBoard extends GridBoard {
    private final int H, V;          // total horizontal / vertical edges
    private final Edge[] edges;      // edges[0 .. H+V-1]
    private final int[][] boxOwner;  // (R-1) x (C-1) 0=none, 1/2=player
    private final int cellW = 5;     // width for each box cell
    private final int vSlotW;        // width reserved for vertical edge column (dynamic)

    public DotsBoard(int rows, int cols) {
        super(rows, cols);
        H = R * (C - 1);
        V = (R - 1) * C;
        edges = new Edge[H + V];

        // Compute vertical slot width so that vertical edge ids are fully visible
        int maxIdLen = String.valueOf(H + V - 1).length();
        vSlotW = Math.max(3, maxIdLen); // at least 3 for good readability

        // Build horizontal edges
        int id = 0;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C - 1; c++) {
                edges[id] = new Edge(id, true, r, c);
                id++;
            }
        }
        // Build vertical edges
        for (int r = 0; r < R - 1; r++) {
            for (int c = 0; c < C; c++) {
                edges[id] = new Edge(id, false, r, c);
                id++;
            }
        }
        boxOwner = new int[R - 1][C - 1];
    }

    /** @return list of all unclaimed edge ids */
    public List<Integer> getAvailableEdgeIds() {
        List<Integer> list = new ArrayList<>();
        for (Edge e : edges) if (!e.isClaimed()) list.add(e.id());
        return list;
    }

    /** @return whether claiming this edge would immediately complete at least one box */
    public boolean isFinishingMove(int edgeId) { return boxesCompletedIfClaim(edgeId) > 0; }

    /** @return whether claiming this edge would create a box with exactly 3 claimed edges (risky) */
    public boolean wouldCreateThirdEdge(int edgeId) {
        for (int[] b : boxesTouchedByEdge(edgeId))
            if (edgesClaimedCountForBox(b[0], b[1]) == 2) return true;
        return false;
    }

    /**
     * Claim an edge for a player.
     * @return number of boxes completed by this move, or -1 if the move is illegal.
     */
    public int claimEdge(int edgeId, int playerId) {
        if (edgeId < 0 || edgeId >= edges.length) return -1;
        Edge e = edges[edgeId];
        if (e.isClaimed()) return -1;
        e.setOwner(playerId);
        int made = 0;
        for (int[] b : boxesTouchedByEdge(edgeId)) {
            if (edgesClaimedCountForBox(b[0], b[1]) == 4 && boxOwner[b[0]][b[1]] == 0) {
                boxOwner[b[0]][b[1]] = playerId; made++;
            }
        }
        return made;
    }

    /** @return whether all edges have been claimed */
    public boolean isFull() {
        for (Edge e : edges) if (!e.isClaimed()) return false;
        return true;
    }

    /** @return total number of boxes owned by a given player */
    public int scoreForPlayer(int id) {
        int s = 0;
        for (int r = 0; r < R - 1; r++)
            for (int c = 0; c < C - 1; c++)
                if (boxOwner[r][c] == id) s++;
        return s;
    }

    /** Pretty-print the board using aligned cells. */
    @Override public void print() {
        int maxIdLen = String.valueOf(H + V - 1).length();
        int vSlotW = Math.max(3, maxIdLen-1);   // slightly widen vertical slot
        int cellWAdj = Math.max(cellW, vSlotW + 2); // widen horizontal cells to visually match
        for (int r = 0; r < R; r++) {
            // 1) Dots + horizontal edges row
            StringBuilder row1 = new StringBuilder();
            for (int c = 0; c < C; c++) {
                row1.append(" •");
                if (c < C - 1) {
                    int eid = hEdgeId(r, c);
                    Edge e = edges[eid];
                    if (e.isClaimed()) {
                        // 原: row1.append(color(e.owner())).append("─".repeat(cellWAdj)).append(RESET);
                        row1.append(color(e.owner())).append(TextUtil.rep("─", cellWAdj)).append(RESET);
                    } else {
                        row1.append(padCenter(String.valueOf(eid), cellWAdj));
                    }
                }
            }
            System.out.println(row1);

            if (r == R - 1) break;

            // 2) vertical edges + box centers row
            StringBuilder row2 = new StringBuilder();
            for (int c = 0; c < C; c++) {
                int ve = vEdgeId(r, c);
                Edge v = edges[ve];

                if (v.isClaimed()) {
                    // Use glyphs instead of coloring digits: P1='┃', P2='║'
                    String glyph = (v.owner() == 1) ? "┃" : "║";
                    row2.append(padCenterAnsi(color(v.owner()) + glyph + RESET, vSlotW));
                } else {
                    // Show FULL vertical edge id centered in the vertical slot
                    row2.append(padCenter(String.valueOf(ve), vSlotW-1));
                }

                if (c < C - 1) {
                    int owner = boxOwner[r][c];
                    String center = (owner == 0) ? "" : (color(owner) + "●" + RESET);
                    row2.append(padCenterAnsi(center, cellWAdj));
                }
            }
            System.out.println(row2);
        }
    }


    // ---- Helpers: indexing and box-edge relations ----
    private int hEdgeId(int r, int c) { return r * (C - 1) + c; }
    private int vEdgeId(int r, int c) { return H + r * C + c; }

    /** @return list of (br,bc) boxes adjacent to the given edge */
    private List<int[]> boxesTouchedByEdge(int edgeId) {
        List<int[]> res = new ArrayList<>();
        if (edgeId < H) {
            int r = edgeId / (C - 1), c = edgeId % (C - 1);
            if (r > 0) res.add(new int[]{r - 1, c});
            if (r < R - 1) res.add(new int[]{r, c});
        } else {
            int local = edgeId - H;
            int r = local / C, c = local % C;
            if (c > 0) res.add(new int[]{r, c - 1});
            if (c < C - 1) res.add(new int[]{r, c});
        }
        return res;
    }

    /** @return how many edges of a box are already claimed */
    private int edgesClaimedCountForBox(int br, int bc) {
        int top    = hEdgeId(br, bc);
        int bottom = hEdgeId(br + 1, bc);
        int left   = vEdgeId(br, bc);
        int right  = vEdgeId(br, bc + 1);
        int cnt = 0;
        if (edges[top].isClaimed()) cnt++;
        if (edges[bottom].isClaimed()) cnt++;
        if (edges[left].isClaimed()) cnt++;
        if (edges[right].isClaimed()) cnt++;
        return cnt;
    }

    /** @return number of boxes that would be completed if the edge were claimed */
    private int boxesCompletedIfClaim(int edgeId) {
        int made = 0;
        for (int[] b : boxesTouchedByEdge(edgeId))
            if (edgesClaimedCountForBox(b[0], b[1]) == 3) made++;
        return made;
    }
}
