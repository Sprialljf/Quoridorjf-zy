package zyjfassignment.boardgames.core.strategy;

import zyjfassignment.boardgames.quoridor.QuoridorBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Random-move Quoridor bot.
 */
public class EasyQuoridorStrategy implements QuoridorStrategy {
    private final Random rng = new Random();

    @Override
    public String decideMove(QuoridorBoard board, char playerChar) {
        List<String> validActions = new ArrayList<>();
        // 50% move，50% wall
        int[] pos = board.getPos(playerChar);
        int r= pos[0];
        int c = pos[1];
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for(int[] d : dirs){
            int nr = r+d[0];
            int nc = c+d[1];
            if (board.canMoveTo(playerChar,nr,nc)){
                validActions.add("MOVE " + nr + " " + nc);
            }
        }
        for(int i =0; i<board.getRows() ; i++){
            for(int j =0; j<board.getCols() ; j++){
                if(i<board.getRows()-1 && board.canPlaceWall('H', i, j)){
                    validActions.add("WALL H "+i+" "+j);
                }
                if(j<board.getCols()-1 && board.canPlaceWall('V', i, j)){
                    validActions.add("WALL V "+i+" "+ j);
                }
            }     
        }
        if(validActions.isEmpty()) return "PASS";
        return validActions.get(new Random().nextInt(validActions.size()));
    }
}
