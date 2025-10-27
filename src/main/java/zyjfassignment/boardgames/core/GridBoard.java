
package zyjfassignment.boardgames.core;

public abstract class GridBoard {
    protected final int R, C;

    public GridBoard(int rows, int cols) {
        if (rows < 2 || cols < 2) throw new IllegalArgumentException("rows/cols must be >= 2");
        this.R = rows; this.C = cols;
    }

    public int getRows() {
         return R; 
        }
    public int getCols() {
         return C; 
        }

    public abstract void print();
}
