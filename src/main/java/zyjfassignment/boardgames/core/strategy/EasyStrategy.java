
package zyjfassignment.boardgames.core.strategy;

import zyjfassignment.boardgames.dots.DotsBoard;
import java.util.List;
import java.util.Random;

public class EasyStrategy implements EdgeChoiceStrategy {
    private final Random rng = new Random();
    @Override public int choose(DotsBoard board, int selfId) {
        List<Integer> avail = board.getAvailableEdgeIds();
        if (avail.isEmpty()) return -1;
        return avail.get(rng.nextInt(avail.size()));
    }
}
