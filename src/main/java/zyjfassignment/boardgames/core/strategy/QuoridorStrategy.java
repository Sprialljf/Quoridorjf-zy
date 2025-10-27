
package zyjfassignment.boardgames.core.strategy;

import zyjfassignment.boardgames.quoridor.QuoridorBoard;

/**
 * Interface for Quoridor AI move selection.
 */
public interface QuoridorStrategy {
    /**
     * Decide what move or wall to play.
     * @param board current board
     * @param playerChar 'A', 'B', etc.
     * @return String command, e.g. "MOVE 4 5" or "WALL V 3 4"
     */
    String decideMove(QuoridorBoard board, char playerChar);
}

