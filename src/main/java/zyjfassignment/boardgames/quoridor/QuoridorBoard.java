package zyjfassignment.boardgames.quoridor;
import zyjfassignment.boardgames.core.GridBoard;
import java.util.*;

/**
 * QuoridorBoard (2–4 Players)
 */
public class QuoridorBoard extends GridBoard {
    private final char[][] cells;
    private final char[][] vWalls;
    private final char[][] hWalls;
    private final Map<Character, int[]> playerPos = new LinkedHashMap<>(); // player position
    private int playerCount = 2;
    private final Map<Character, Integer> remainingWalls = new HashMap<>();
    public QuoridorBoard(int players) {
        super(9, 9);
        if (players < 2 || players > 4) throw new IllegalArgumentException("Players must be 2–4");
        this.playerCount = players;
        cells = new char[R][C];
        vWalls = new char[R][C - 1];
        hWalls = new char[R - 1][C];
        reset();
    }

    /* ---------------- Initialize ---------------- */
    private void reset() {
        for (int r = 0; r < R; r++) Arrays.fill(cells[r], '.');
        for (int r = 0; r < R; r++) Arrays.fill(vWalls[r], '.');
        for (int r = 0; r < R - 1; r++) Arrays.fill(hWalls[r], '.');
        playerPos.clear();
        remainingWalls.clear();
        // Assign initial position
        playerPos.put('A', new int[]{8, 4}); 
        playerPos.put('B', new int[]{0, 4}); 
        if (playerCount >= 3) playerPos.put('C', new int[]{4, 0}); 
        if (playerCount == 4) playerPos.put('D', new int[]{4, 8}); 

        for (Map.Entry<Character, int[]> e : playerPos.entrySet()) {
            int[] p = e.getValue();
            cells[p[0]][p[1]] = e.getKey();
        }
    }

    /* ---------------- helper ---------------- */
    public int[] getPos(char p) {
        return playerPos.get(p);
    }

    public int[] getPlayerPosition(char p) {
        return getPos(p);
    }
    public Map<Character, int[]> getPlayerPositions() {
        return Collections.unmodifiableMap(playerPos);
    }
    public void setRemainingWalls(char player, int count) {
       remainingWalls.put(player, count);
    }
    public void decreaseWall(char player) {
        remainingWalls.put(player, Math.max(0, remainingWalls.getOrDefault(player, 0) - 1));
    }
    public boolean isFree(int r, int c) {
        return r >= 0 && r < R && c >= 0 && c < C && cells[r][c] == '.';
    }

    public boolean canStep(int r, int c, int nr, int nc) {
        if (Math.abs(nr - r) + Math.abs(nc - c) != 1) return false;
        if (nr == r && nc == c + 1) return vWalls[r][c]=='.';
        if (nr == r && nc == c - 1) return (c - 1 >= 0) && vWalls[r][c - 1]=='.';
        if (nr == r + 1 && nc == c) return hWalls[r][c] == '.';
        if (nr == r - 1 && nc == c) return (r - 1 >= 0) && hWalls[r - 1][c]=='.';
        return false;
    }

    public boolean canMoveTo(char player, int targetRow, int targetCol) {
        int[] pos = getPos(player);
        return isFree(targetRow, targetCol) && canStep(pos[0], pos[1], targetRow, targetCol);
    }

    /* ---------------- operation ---------------- */
    public boolean move(char player, int nr, int nc) {
        int[] pos = getPos(player);
        int r = pos[0], c = pos[1];
        if (!isFree(nr, nc) || !canStep(r, c, nr, nc)) return false;

        cells[r][c] = '.';
        cells[nr][nc] = player;
        playerPos.put(player, new int[]{nr, nc});
        return true;
    }
    //AI(Bot) use
    public boolean canPlaceWall(char orient, int r, int c) {
        if (orient == 'v' || orient == 'V') {
            return r >= 0 && r < R && c >= 0 && c < C - 1 && vWalls[r][c]=='.';
        } else if (orient == 'h' || orient == 'H') {
            return r >= 0 && r < R - 1 && c >= 0 && c < C && hWalls[r][c]=='.';
        }
        return false;
    }


    public boolean placeWall(char orient, int r, int c,char player) {
        if (orient == 'v' || orient == 'V') {
            if (r < 0 || r >= R || c < 0 || c >= C - 1 || vWalls[r][c]!='.') return false;
            vWalls[r][c] = player;
            return true;
        } else if (orient == 'h' || orient == 'H') {
            if (r < 0 || r >= R - 1 || c < 0 || c >= C || hWalls[r][c]!='.') return false;
            hWalls[r][c] = player;
            return true;
        }
        return false;
    }
    public int remainingWalls(char player) {
        return remainingWalls.getOrDefault(player, 0);
    }
    /* ---------------- Judgment of victory or defeat ---------------- */
    public boolean hasWon(char p) {
        int[] pos = getPos(p);
        int r = pos[0], c = pos[1];
        switch (p) {
            case 'A': return r == 0;   
            case 'B': return r == R - 1; 
            case 'C': return c == C - 1; 
            case 'D': return c == 0;    
        }
        return false;
    }
    public String getColor(char player){
        switch (player){
             case 'A':
                 return "\u001B[31m"; // Red
             case 'B':
                 return "\u001B[34m"; 
             case 'C':
                 return "\u001B[32m"; 
             case 'D':
                 return "\u001B[33m";
             default: 
                 return "";  
        }
        
    }
    public int getRows() {
        return R; 
    }
    public int getCols() {
        return C; 
    }
     
     
    /* ---------------- print ---------------- */
    @Override
    public void print() {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String BLUE = "\u001B[34m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";

        System.out.print("  ");
        for (int c = 0; c < C; c++) System.out.printf("%3d", c);
        System.out.println();

        for (int r = 0; r < R; r++) {
            System.out.printf("%2d ", r);
            for (int c = 0; c < C; c++) {
                char ch = cells[r][c];
                String cellStr;
                switch (ch) {
                    case 'A': cellStr = RED + "A" + RESET; break;
                    case 'B': cellStr = BLUE + "B" + RESET; break;
                    case 'C': cellStr = GREEN + "C" + RESET; break;
                    case 'D': cellStr = YELLOW + "D" + RESET; break;
                    default:  cellStr = ".";
                }
                System.out.printf(" %s", cellStr);

                // vertical wall
                if (c < C - 1) {
                    char owner=vWalls[r][c];
                    if (owner=='.') System.out.print(" ");
                    else System.out.print(getColor(owner) + "|" + RESET);
                }
            }
            System.out.println();

            // horizontal wall
            if (r < R - 1) {
                System.out.print("    ");
                for (int c = 0; c < C; c++) {
                    char owner=hWalls[r][c];
                    if (owner=='.') System.out.print("  ");
                    else System.out.print(getColor(owner) + "--" + RESET);
                }
                System.out.println();
            }
        }
    }
    public int getPlayerCount() {
        return playerPos.size();
    }

}
