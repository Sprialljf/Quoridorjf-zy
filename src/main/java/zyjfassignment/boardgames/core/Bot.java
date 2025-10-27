
package zyjfassignment.boardgames.core;

import zyjfassignment.boardgames.core.strategy.EdgeChoiceStrategy;
import zyjfassignment.boardgames.dots.DotsBoard;

public class Bot extends Player {
    private final EdgeChoiceStrategy strategy;

    public Bot(String name, int id, EdgeChoiceStrategy strategy) {
        super(name, id, false);
        this.strategy = strategy;
    }

    public int chooseEdge(DotsBoard board) {
        return strategy.choose(board, id);
    }
}
