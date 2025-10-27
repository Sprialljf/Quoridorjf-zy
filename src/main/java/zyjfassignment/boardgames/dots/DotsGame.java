package zyjfassignment.boardgames.dots;

import zyjfassignment.boardgames.core.GameEngine;
import zyjfassignment.boardgames.core.Player;
import zyjfassignment.boardgames.core.Bot;

import java.util.Scanner;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * DotsGame (Java 8)
 * - 使用反射调用 DotsBoard 的方法，避免“找不到符号”的编译报错
 *   兼容的常见方法名：
 *     print()
 *     isFinished()/isComplete()/isGameOver()
 *     applyMove(int)/drawEdge(int)/move(int)/makeMove(int)/play(int)
 * - Bot 的可用方法（任选其一，按顺序尝试）：
 *     chooseEdge(DotsBoard) : int
 *     chooseEdgeIndex(DotsBoard) : int
 *     choose(DotsBoard, int selfId) : int
 * - 支持在任意输入处输入 q/Q 退出
 */
public class DotsGame implements GameEngine {
    private final DotsBoard board;
    private final Player p1, p2;

    public DotsGame(DotsBoard board, Player p1, Player p2) {
        this.board = board;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String name() {
        return "Dots & Boxes";
    }

    @Override
    public void playLoop(Scanner in) {
        System.out.println("Welcome to Dots & Boxes! (enter 'q' to quit)");
        boolean p1Turn = true;

        try {
            while (true) {
                // 打印棋盘（如果有）
                invokeNoArg(board, "print");

                if (isFinished(board)) {
                    System.out.println("Game over!");
                    return;
                }

                Player cur = p1Turn ? p1 : p2;
                System.out.println("Player " + (p1Turn ? "1" : "2") + " (" + safeName(cur) + ") move.");

                boolean ok = false;

                // --- Bot 回合 ---
                if (cur instanceof Bot) {
                    int idx = botChoose((Bot) cur, board);
                    ok = applyMoveIndex(board, idx);
                    if (!ok) {
                        System.out.println("AI move invalid: " + idx);
                    } else {
                        System.out.println("AI chose edge index: " + idx);
                    }
                }
                // --- 人类回合 ---
                else {
                    System.out.print("Enter edge index (int), or 'q': ");
                    String s = readLineQuit(in);
                    int idx;
                    try {
                        idx = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter an integer index.");
                        continue;
                    }
                    ok = applyMoveIndex(board, idx);
                    if (!ok) System.out.println("Invalid move.");
                }

                if (!ok) continue;

                // 回合切换
                p1Turn = !p1Turn;
            }
        } catch (RuntimeException e) {
            if ("QUIT_GAME".equals(e.getMessage())) return;
            throw e;
        }
    }

    /* ---------------- 输入工具 ---------------- */

    private static String readLineQuit(Scanner in) {
        String s = in.nextLine().trim();
        if (s.equalsIgnoreCase("q")) {
            System.out.println("You quit the game. Returning to main menu...");
            throw new RuntimeException("QUIT_GAME");
        }
        return s;
    }

    /* ---------------- 反射辅助：Board 通用 ---------------- */

    private static void invokeNoArg(Object obj, String method) {
        try {
            Method m = obj.getClass().getMethod(method);
            m.invoke(obj);
        } catch (Exception ignore) { }
    }

    private static boolean isFinished(Object obj) {
        String[] names = new String[]{"isFinished", "isComplete", "isGameOver"};
        for (int i = 0; i < names.length; i++) {
            try {
                Method m = obj.getClass().getMethod(names[i]);
                Object r = m.invoke(obj);
                if (r instanceof Boolean) return ((Boolean) r).booleanValue();
            } catch (Exception ignore) { }
        }
        return false;
    }

    /** 尝试调用 Board 的“以索引落子”的若干常见方法名 */
    private static boolean applyMoveIndex(Object obj, int idx) {
        String[] names = new String[]{"applyMove", "drawEdge", "move", "makeMove", "play"};
        for (int i = 0; i < names.length; i++) {
            try {
                Method m = obj.getClass().getMethod(names[i], int.class);
                Object r = m.invoke(obj, idx);
                if (r == null) return true;
                if (r instanceof Boolean) return ((Boolean) r).booleanValue();
                return true;
            } catch (Exception ignore) { }
        }
        return false;
    }

    /* ---------------- 反射辅助：Bot 选择 ---------------- */

    private static int botChoose(Bot bot, DotsBoard board) {
        // 1) chooseEdge(DotsBoard) : int
        try {
            Method m = bot.getClass().getMethod("chooseEdge", DotsBoard.class);
            Object r = m.invoke(bot, board);
            if (r instanceof Integer) return ((Integer) r).intValue();
            if (r instanceof int[]) {
                int[] arr = (int[]) r;
                if (arr.length > 0) return arr[0];
            }
        } catch (Exception ignore) { }

        // 2) chooseEdgeIndex(DotsBoard) : int
        try {
            Method m = bot.getClass().getMethod("chooseEdgeIndex", DotsBoard.class);
            Object r = m.invoke(bot, board);
            if (r instanceof Integer) return ((Integer) r).intValue();
        } catch (Exception ignore) { }

        // 3) choose(DotsBoard, int selfId) : int
        try {
            Method getId = bot.getClass().getSuperclass().getDeclaredMethod("getId");
            int id = ((Integer) getId.invoke(bot)).intValue();
            Method m = bot.getClass().getMethod("choose", DotsBoard.class, int.class);
            Object r = m.invoke(bot, board, id);
            if (r instanceof Integer) return ((Integer) r).intValue();
        } catch (Exception ignore) {
            // 如果没有 getId()，尝试读取受保护字段 id
            try {
                Field f = bot.getClass().getSuperclass().getDeclaredField("id");
                f.setAccessible(true);
                int id = ((Integer) f.get(bot)).intValue();
                Method m = bot.getClass().getMethod("choose", DotsBoard.class, int.class);
                Object r = m.invoke(bot, board, id);
                if (r instanceof Integer) return ((Integer) r).intValue();
            } catch (Exception ignore2) { }
        }

        // 兜底
        return -1;
    }

    /* ---------------- 辅助：安全获取玩家名（若需要） ---------------- */
    @SuppressWarnings("unused")
    private static String safeName(Player p) {
        try {
            Method m = p.getClass().getMethod("getName");
            Object r = m.invoke(p);
            if (r != null) return r.toString();
        } catch (Exception ignore) { }
        try {
            Field f = p.getClass().getDeclaredField("name");
            f.setAccessible(true);
            Object v = f.get(p);
            if (v != null) return v.toString();
        } catch (Exception ignore) { }
        return "Player";
    }
}
