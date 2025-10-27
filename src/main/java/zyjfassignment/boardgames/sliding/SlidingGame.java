package zyjfassignment.boardgames.sliding;

import zyjfassignment.boardgames.core.GameEngine;

import java.util.Random;
import java.util.Scanner;

/**
 * - Opening questions: board size N, scramble method, and number of moves
 * - Operations: w/a/s/d or enter a tile number, r to re-scramble, q to quit
 */
public class SlidingGame implements GameEngine {
    private SlidingBoard board;

    public SlidingGame(SlidingBoard board) {
        if (board == null) throw new IllegalArgumentException("board == null");
        this.board = board;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public void playLoop(Scanner in) {
        System.out.println("Welcome to Sliding Puzzle! (q to quit)");
        Random rnd = new Random();

        System.out.println("currentsize N = " + board.rows() + "or type new N（>=2）：");
        int n = readOptionalInt(in, board.rows());
        if (n != board.rows()) {
            board.resetSolved(n);
        }
        askAndShuffle(in, rnd);
        System.out.println("\nInitial position:");
        board.print();

        // main loop
        while (true) {
            if (board.isSolved()) {
                System.out.println("Solved!");
                return;
            }

            System.out.print("\nEnter command [w/a/s/d or number], r=re-shuffle, q=quit > ");
            String s = in.nextLine().trim();
            if (s.equalsIgnoreCase("q")) {
                System.out.println("Bye!");
                return;
            }
            if (s.equalsIgnoreCase("r")) {
                askAndShuffle(in, rnd);
                System.out.println("\nThe situation after reshuffling:");
                board.print();
                continue;
            }

            boolean ok = board.move(s);
            if (!ok) {
                System.out.println("Invalid move, please try again.");
                continue;
            }
            board.print();
        }
    }

    /* ============= shuffle ============= */

    private void askAndShuffle(Scanner in, Random rnd) {
        System.out.println("\nSelect the scramble method:");
        System.out.println(" 1.Take a number of random steps (guaranteed to be solvable)");
        System.out.println("  2. Randomly arrange and force correction to be solvable");
        System.out.println("  3) Do not disrupt (keep the answer board)");
        System.out.print("Input 1/2/3 (default 1):");
        String opt = in.nextLine().trim();
        if (opt.isEmpty()) opt = "1";

        if ("1".equals(opt)) {
            System.out.print("Please enter the number of shuffle steps k (recommended 100-500, default 200): ");
            int k = readOptionalInt(in, 200);
            board.shuffleByRandomWalk(k, rnd);
        } else if ("2".equals(opt)) {
            board.shuffleSolvable(rnd);
        } else {
            // 3 or others: skip
        }
    }

    /* ============= helper ============= */

    private static int readOptionalInt(Scanner in, int def) {
        String s = in.nextLine().trim();
        if (s.isEmpty()) return def;
        try {
            int v = Integer.parseInt(s);
            return (v >= 2) ? v : def;
        } catch (Exception e) {
            return def;
        }
    }
}
