package zyjfassignment.boardgames.sliding;

import java.util.Arrays;
import java.util.Random;

public class SlidingBoard {
    private int N;            // 正方形边长 N x N
    private int[][] a;        // 棋盘
    private int zr, zc;       // 空格(0)的位置

    /** constructor：N x N */
    public SlidingBoard(int n) {
        if (n < 2) throw new IllegalArgumentException("N must be >= 2");
        resetSolved(n);
    }

    /** Construction: rows x cols -> Here it must be square according to the slider rule. If it is not a square, take min(rows, cols) */
    public SlidingBoard(int rows, int cols) {
        this(Math.max(2, Math.min(rows, cols)));
    }

    /** reconstruct */
    public void resetSolved(int n) {
        this.N = n;
        this.a = new int[N][N];
        int v = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == N - 1 && j == N - 1) {
                    a[i][j] = 0;
                    zr = i; zc = j;
                } else {
                    a[i][j] = v++;
                }
            }
        }
    }

    public int rows() { return N; }
    public int cols() { return N; }

    /** print board */
    public void print() {
        int max = N * N - 1;
        int w = String.valueOf(max).length();
        String fmt = "%" + w + "s";
        for (int i = 0; i < N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                String s = (a[i][j] == 0) ? " " : String.valueOf(a[i][j]);
                sb.append(String.format(fmt, s));
                if (j + 1 < N) sb.append(" ");
            }
            System.out.println(sb);
        }
    }

    /** if it is solved */
    public boolean isSolved() {
        int v = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == N - 1 && j == N - 1) return a[i][j] == 0;
                if (a[i][j] != v++) return false;
            }
        }
        return true;
    }

    /** blank move */
    public boolean move(char dir) {
        int dr = 0, dc = 0;
        switch (dir) {
            case 'w': case 'W': dr = -1; dc = 0; break;
            case 's': case 'S': dr =  1; dc = 0; break;
            case 'a': case 'A': dr =  0; dc = -1; break;
            case 'd': case 'D': dr =  0; dc =  1; break;
            default: return false;
        }
        int nr = zr + dr, nc = zc + dc;
        if (0 <= nr && nr < N && 0 <= nc && nc < N) {
            swap(zr, zc, nr, nc);
            zr = nr; zc = nc;
            return true;
        }
        return false;
    }

    /** blank move by number */
    public boolean move(int tile) {
        if (tile <= 0 || tile >= N * N) return false;
        int tr = -1, tc = -1;
        outer:
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] == tile) { tr = i; tc = j; break outer; }
            }
        }
        if (tr < 0) return false;
        if (isAdjacent(tr, tc, zr, zc)) {
            swap(tr, tc, zr, zc);
            zr = tr; zc = tc;
            return true;
        }
        return false;
    }

    /** Compatible with string input (e.g. "w" / "12") */
    public boolean move(String s) {
        if (s == null || s.isEmpty()) return false;
        char c = s.trim().charAt(0);
        if (Character.isLetter(c)) return move(c);
        try {
            int t = Integer.parseInt(s.trim());
            return move(t);
        } catch (Exception ignore) { return false; }
    }

    /** Use "random k-step walk on spaces" to shuffle (definitely solvable) */
    public void shuffleByRandomWalk(int steps, Random rnd) {
        if (rnd == null) rnd = new Random();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int s = 0; s < steps; s++) {
            // 枚举当前合法方向
            int[] idx = new int[4];
            int cnt = 0;
            for (int d = 0; d < 4; d++) {
                int nr = zr + dr[d], nc = zc + dc[d];
                if (0 <= nr && nr < N && 0 <= nc && nc < N) idx[cnt++] = d;
            }
            if (cnt == 0) continue;
            int d = idx[rnd.nextInt(cnt)];
            int nr = zr + dr[d], nc = zc + dc[d];
            swap(zr, zc, nr, nc);
            zr = nr; zc = nc;
        }
    }

    /**
     * Directly generate a random permutation and enforce correct solvability (generalization of the 15-puzzle rule).
     * For odd N, solvability <=> the number of inversions is even; for even N, consider the number of blank rows from the bottom.
     */
    public void shuffleSolvable(Random rnd) {
        if (rnd == null) rnd = new Random();
        int size = N * N;
        int[] flat = new int[size];
        for (int i = 0; i < size; i++) flat[i] = i; // 0..N*N-1
        // Fisher–Yates
        for (int i = size - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = flat[i]; flat[i] = flat[j]; flat[j] = t;
        }
        // If the solution is unsolvable, swap two non-zero tiles to correct the parity.
        if (!isPermutationSolvable(flat)) {
            // 找两张非 0 牌
            int i = 0, j = 1;
            if (flat[i] == 0 || flat[j] == 0) { i = 2; j = 3; }
            int t = flat[i]; flat[i] = flat[j]; flat[j] = t;
        }
        // Write back the board and update the space coordinates
        for (int r = 0, p = 0; r < N; r++) {
            for (int c = 0; c < N; c++, p++) {
                a[r][c] = flat[p];
                if (a[r][c] == 0) { zr = r; zc = c; }
            }
        }
    }

    /* ================== 辅助 ================== */

    private void swap(int r1, int c1, int r2, int c2) {
        int tmp = a[r1][c1]; a[r1][c1] = a[r2][c2]; a[r2][c2] = tmp;
    }
    private boolean isAdjacent(int r1, int c1, int r2, int c2) {
        return (r1 == r2 && Math.abs(c1 - c2) == 1) || (c1 == c2 && Math.abs(r1 - r2) == 1);
    }

    private boolean isPermutationSolvable(int[] flat) {
        int size = flat.length;
        // 统计逆序数（忽略 0）
        int inv = 0, zeroIdx = -1;
        for (int i = 0; i < size; i++) {
            if (flat[i] == 0) { zeroIdx = i; continue; }
            for (int j = i + 1; j < size; j++) {
                if (flat[j] != 0 && flat[i] > flat[j]) inv++;
            }
        }
        if (N % 2 == 1) {
            // 奇数 N：逆序数偶数可解
            return (inv % 2 == 0);
        } else {
            // 偶数 N：空格距离底部的行数（从 1 开始）
            int rowFromBottom = N - (zeroIdx / N);
            // 标准规则：rowFromBottom 偶数时需 inv 为奇；rowFromBottom 奇数时需 inv 为偶
            boolean need = (rowFromBottom % 2 == 0); // 需要 inv%2==1
            return need ? (inv % 2 == 1) : (inv % 2 == 0);
        }
    }
}
