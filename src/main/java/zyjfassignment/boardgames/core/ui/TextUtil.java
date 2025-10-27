
package zyjfassignment.boardgames.core.ui;

/**
 * Small text utilities for terminal rendering.
 * - ANSI colors
 * - center padding
 * - string repetition
 */
public final class TextUtil {
    public static final String RED   = "\u001B[31m";
    public static final String BLUE  = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW   = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    private TextUtil() {}

    /** Repeat a string n times. */
    public static String rep(String s, int n) {
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    /** Pad the given string to target width by centering it with spaces. */
    public static String padCenter(String s, int width) {
        if (s == null) s = "";
        int len = s.length();
        if (len >= width) return s.substring(0, width);
        int left = (width - len) / 2;
        int right = width - len - left;
//        return " ".repeat(left) + s + " ".repeat(right);
        return rep(" ",left) + s +
                rep(" ",right);
    }
    // in TextUtil
    private static final java.util.regex.Pattern ANSI = java.util.regex.Pattern.compile("\\u001B\\[[;\\d]*m");
    public static String stripAnsi(String s){ return ANSI.matcher(s).replaceAll(""); }
    public static String padCenterAnsi(String s, int width){
        if (s == null) s = "";
        int vis = stripAnsi(s).length();
        if (vis >= width) return s;
        int left = (width - vis) / 2, right = width - vis - left;
//        return " ".repeat(left) + s + " ".repeat(right);
        return rep(" ",left) + s +
                rep(" ",right);
    }

    /** Map player id -> ANSI color string (1: RED, 2: BLUE). */
    public static String color(int playerId) {
        return playerId == 1 ? RED : BLUE;
    }
}
