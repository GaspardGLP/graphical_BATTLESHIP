import javax.swing.*;

// Represents a player in the Battleship game
public class Player {
    private final Board board; // Player's board containing ships and game logic

    // Constructor initializes a new board for the player
    public Player() {
        board = new Board();
    }

    // Method to place a ship on the player's board
    public boolean placeShip(int row, int col, int size, boolean horizontal, JButton[][] grid) {
        return board.placeShip(row, col, size, horizontal, grid, true); // Delegate to Board class
    }

    // Method to attack a specific cell on the opponent's board
    public boolean attack(int row, int col, JButton[][] grid, Player opponent) {
        return board.attack(row, col, grid); // Delegate to Board class
    }

    // Method to place ships automatically for the opponent
    public void placeOpponentShips(int[] shipSizes, JButton[][] grid) {
        board.placeOpponentShips(shipSizes, grid); // Delegate to Board class
    }

    // Resets the player's board for a new game
    public void reset() {
        board.clear(); // Clear the board
    }

    // Returns the number of ship cells remaining (not hit)
    public int getShipCells() {
        return board.getRemainingShipCells();
    }
}
