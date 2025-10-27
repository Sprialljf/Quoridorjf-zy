package zyjfassignment.boardgames.quoridor;

import zyjfassignment.boardgames.core.GameEngine;
import zyjfassignment.boardgames.core.strategy.*;

import java.util.*;

public class QuoridorGame implements GameEngine {
    private final QuoridorBoard board;
    private final Scanner scanner = new Scanner(System.in);
    private final List<Character> players = new ArrayList<>();
    private final Map<Character, QuoridorStrategy> aiMap = new HashMap<>();
    private final Map<Character, Integer> wallCount = new HashMap<>();

    public QuoridorGame(QuoridorBoard board) {
        this.board = board;
    }

    @Override
    public String name() {
        return "Quoridor";
    }
    @Override
    public void playLoop(Scanner in) {
        System.out.println("Welcome to Quoridor!");
        int n = board.getPlayerCount();
        System.out.println("Players detected: " + n);
        // Initialize player and bot
        for (int i = 0; i < n; i++) {
            char sym = (char) ('A' + i);
            String type = "";
            boolean valid = false;
            while (!valid) {
                System.out.print("Is player " + sym + " an AI?  (1-none / 2-easy / 3-hard): ");
                type = in.nextLine().trim().toLowerCase();
                if (type.equals("1")) type = "none";
                else if (type.equals("2")) type = "easy";
                else if (type.equals("3")) type = "hard";
                if (type.equals("none") || type.equals("easy") || type.equals("hard")) valid = true;
            }
            if (type.equals("easy")) aiMap.put(sym, new EasyQuoridorStrategy());
            else if (type.equals("hard")) aiMap.put(sym, new HardQuoridorStrategy());
            else aiMap.put(sym, null);
            players.add(sym);
            wallCount.put(sym, 10);
            board.setRemainingWalls(sym, 10);
        }
        boolean running = true;
        int turn = 0;
        while (running) {
            board.print();
            char sym = players.get(turn);
            System.out.println("\nPlayer " + sym + "'s turn (walls: " + wallCount.get(sym) + ")");
            QuoridorStrategy ai = aiMap.get(sym);
            boolean turnCompleted = false;
            while (!turnCompleted) {
                String command;
                if (ai != null) {
                    command = ai.decideMove(board, sym);
                    System.out.println("AI plays: " + command);
                } else {
                    System.out.print("Enter move (e.g. MOVE 3 4 or WALL H/V 3 4 or PASS): ");
                    command = in.nextLine().trim().toUpperCase();
                }
                boolean success = false;
                try {
                    String[] parts = command.split("\\s+");
                    if (parts[0].equals("MOVE")) {
                        int r = Integer.parseInt(parts[1]);
                        int c = Integer.parseInt(parts[2]);
                        success = board.move(sym, r, c);
                    } else if (parts[0].equals("WALL")) {
                        if (wallCount.get(sym) <= 0) {
                            success = false;
                        } else {
                            char orient = parts[1].charAt(0);
                            int r = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);
                            success = board.placeWall(orient, r, c, sym);
                            if (success) {
                                wallCount.put(sym, wallCount.get(sym) - 1);
                                board.decreaseWall(sym);
                            }
                        }
                    } else if (parts[0].equals("PASS")) {
                        success = true;
                    } else {
                        success = false;
                    }
                } catch (Exception e) {
                    success = false;
                }

                if (!success && ai == null) {
                    System.out.println("Invalid move. Try again.");
                }

                if (success) turnCompleted = true;
            }
            // check win？
            if (board.hasWon(sym)) {
                board.print();
                System.out.println("\nPlayer " + sym + " wins!");
                running = false;
                break;
            }
            turn = (turn + 1) % players.size();
        }
    }
}
