
package zyjfassignment.boardgames.core.strategy;

import zyjfassignment.boardgames.dots.DotsBoard;

public class HardStrategy implements EdgeChoiceStrategy {
    @Override public int choose(DotsBoard board, int selfId) {
        for (int id : board.getAvailableEdgeIds()) if (board.isFinishingMove(id)) return id;
        for (int id : board.getAvailableEdgeIds()) if (!board.wouldCreateThirdEdge(id)) return id;
        return board.getAvailableEdgeIds().isEmpty() ? -1 : board.getAvailableEdgeIds().get(0);
    }
}
