
package zyjfassignment.boardgames.core;

import java.util.Scanner;

public interface GameEngine {
    String name();
    void playLoop(Scanner in);
}
