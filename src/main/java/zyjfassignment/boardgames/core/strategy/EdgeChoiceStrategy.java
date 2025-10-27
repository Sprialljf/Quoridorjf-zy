
package zyjfassignment.boardgames.core.strategy;

import zyjfassignment.boardgames.dots.DotsBoard;

public interface EdgeChoiceStrategy {
    int choose(DotsBoard board, int selfId);
}
