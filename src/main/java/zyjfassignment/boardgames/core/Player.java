package zyjfassignment.boardgames.core;

import static zyjfassignment.boardgames.core.ui.TextUtil.*;

public class Player {
    protected final String name;
    protected final int id;      // 1 or 2
    protected final boolean human;

    public Player(String name, int id, boolean human) {
        this.name = name; 
        this.id = id; 
        this.human = human;
    }

    public String getName() {
         return name; 
        }

    public String colorAnsi() { 
        return color(id); 
    }

    @Override public String toString() { 
        return colorAnsi() + name + RESET; 
    }
}
