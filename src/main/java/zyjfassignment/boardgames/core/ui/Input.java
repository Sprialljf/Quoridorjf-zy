/**
 * Utility class for safe user input.
 */
package zyjfassignment.boardgames.core.ui;

import java.util.Scanner;

public class Input {
    private static final Scanner in = new Scanner(System.in);

    public static int readInt(String prompt, int def) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            if (s.isEmpty()) return def;
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Invalid integer."); }
        }
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }
}
