/**
 * Base class for movable pieces.
 */
package zyjfassignment.boardgames.core;

public class Piece {
    private final String owner;
    private final char symbol;

    public Piece(String owner, char symbol) {
        this.owner = owner;
        this.symbol = symbol;
    }

    public String getOwner() {
         return owner; 
        }
    public char getSymbol() {
         return symbol; 
        }
}
