package zyjfassignment.boardgames.core.strategy;

import zyjfassignment.boardgames.quoridor.QuoridorBoard;
import java.util.*;

/**
 * Hard Quoridor Strategy: BFS shortest path + strategic wall placement
 */
public class HardQuoridorStrategy implements QuoridorStrategy {
    private final Random rng = new Random();

    @Override
    public String decideMove(QuoridorBoard board, char playerChar) {
        List<Character> players = new ArrayList<>(board.getPlayerPositions().keySet());
        int idx = players.indexOf(playerChar);
        if (idx == -1) return "PASS";

        char opponent = players.get((idx + 1) % players.size());
        int[] selfPos = board.getPlayerPosition(playerChar);
        int[] oppPos = board.getPlayerPosition(opponent);
        if (selfPos == null || oppPos == null) return "PASS";

        // shorest path
        int selfDist = shortestPathLength(board, playerChar, selfPos);
        int oppDist = shortestPathLength(board, opponent, oppPos);

        if (board.remainingWalls(playerChar) > 0 && oppDist <= selfDist) {
            String wallCmd = placeStrategicWall(board, oppPos, playerChar);
            if (wallCmd != null) return wallCmd;
        }

       
        int[] nextMove = nextStepAlongShortestPath(board, playerChar, selfPos);
        if (nextMove != null) return "MOVE " + nextMove[0] + " " + nextMove[1];

        //
        int[] fallback = randomValidMove(board, playerChar);
        if (fallback != null) return "MOVE " + fallback[0] + " " + fallback[1];

        if (board.remainingWalls(playerChar) > 0) {
            String wallCmd = placeStrategicWall(board, oppPos, playerChar);
            if (wallCmd != null) return wallCmd;
        }

        return "PASS";
    }

    // ---------------- random fallback move ----------------
    private int[] randomValidMove(QuoridorBoard board, char player) {
        int[] pos = board.getPlayerPosition(player);
        List<int[]> moves = new ArrayList<>();
        for (int[] d : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
            int nr = pos[0] + d[0];
            int nc = pos[1] + d[1];
            if (board.canMoveTo(player, nr, nc)) moves.add(new int[]{nr, nc});
        }
        if (moves.isEmpty()) return null;
        return moves.get(rng.nextInt(moves.size()));
    }

    // ---------------- BFS shortest path ----------------
    private int shortestPathLength(QuoridorBoard board, char player, int[] start) {
        Queue<int[]> q = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        q.add(start);
        visited.add(start[0] + "," + start[1]);
        int dist = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                int[] cur = q.poll();
                int r = cur[0], c = cur[1];
                if (reachedGoal(player, r, c)) return dist;

                for (int[] d : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                    int nr = r + d[0], nc = c + d[1];
                    if (board.canMoveTo(player, nr, nc)) {
                        String key = nr + "," + nc;
                        if (!visited.contains(key)) {
                            visited.add(key);
                            q.add(new int[]{nr, nc});
                        }
                    }
                }
            }
            dist++;
        }
        return Integer.MAX_VALUE;
    }

    private boolean reachedGoal(char player, int r, int c) {
        switch (player) {
            case 'A': return r == 0;
            case 'B': return r == 8;
            case 'C': return c == 8;
            case 'D': return c == 0;
            default: return false;
        }
    }

    // ---------------- next step along shortest path ----------------
    public int[] nextStepAlongShortestPath(QuoridorBoard board, char player, int[] start) {
        Queue<int[]> q = new LinkedList<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        q.add(start);
        visited.add(start[0] + "," + start[1]);
        String goalKey = null;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1];
            if (reachedGoal(player, r, c)) {
                goalKey = r + "," + c;
                break;
            }
            for (int[] d : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                int nr = r + d[0], nc = c + d[1];
                if (board.canMoveTo(player, nr, nc)) {
                    String key = nr + "," + nc;
                    if (!visited.contains(key)) {
                        visited.add(key);
                        prev.put(key, r + "," + c);
                        q.add(new int[]{nr, nc});
                    }
                }
            }
        }
        if (goalKey == null) return null;
        // find next step
        String curKey = goalKey;
        String parentKey = prev.get(curKey);
        while (parentKey != null && !parentKey.equals(start[0] + "," + start[1])) {
            curKey = parentKey;
            parentKey = prev.get(curKey);
        }
        String[] parts = curKey.split(",");
        if (parts.length != 2) return null;
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    // ---------------- strategic wall placement ----------------
    private String placeStrategicWall(QuoridorBoard board, int[] oppPos, char player) {
        if (oppPos == null) return null;
        int[][] offsets = {{0,0},{0,1},{1,0},{1,1},{-1,0},{0,-1},{-1,-1},{-1,1},{1,-1}};
        for (char orient : new char[]{'H','V'}) {
            for (int[] off : offsets) {
                int r = oppPos[0] + off[0];
                int c = oppPos[1] + off[1];
                if (r >= 0 && r < 8 && c >= 0 && c < 8 && board.canPlaceWall(orient, r, c)) {
                    return String.format("WALL %c %d %d", orient, r, c);
                }
            }
        }
        return null;
    }
}

