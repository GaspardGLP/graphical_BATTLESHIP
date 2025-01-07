import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Represents the logical board of a player, managing ship placement and attacks
public class Board {
    private final List<List<Point>> ships = new ArrayList<>(); // List of ships and their coordinates
    private int remainingShipCells = 0; // Total cells occupied by ships

    // Places a ship on the board
    public boolean placeShip(int row, int col, int size, boolean horizontal, JButton[][] grid, boolean isPlayerGrid) {
        // Check if the ship can be placed within bounds and does not overlap with existing ships
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= 10 || c >= 10 || (Boolean) grid[r][c].getClientProperty("hasShip")) {
                return false; // Ship placement invalid
            }
        }

        // Place the ship on the grid and add its coordinates to the ship list
        List<Point> ship = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            grid[r][c].putClientProperty("hasShip", true); // Mark cell as having a ship
            if (isPlayerGrid) {
                grid[r][c].setBackground(Color.BLUE); // Color the ship for the player
            }
            ship.add(new Point(r, c)); // Add ship part to the list
        }

        ships.add(ship); // Add the ship to the list of all ships
        remainingShipCells += size; // Increase the count of occupied cells
        return true; // Ship successfully placed
    }

    // Handles an attack on the board
    public boolean attack(int row, int col, JButton[][] grid) {
        // Check if the cell contains a ship
        if ((Boolean) grid[row][col].getClientProperty("hasShip")) {
            grid[row][col].setBackground(Color.RED); // Mark as hit
            if (!(Boolean) grid[row][col].getClientProperty("hit")) { // Check if it wasn't already hit
                grid[row][col].putClientProperty("hit", true); // Mark as hit
                remainingShipCells--; // Decrease remaining ship cells
            }
            return true; // Attack was successful
        } else {
            grid[row][col].setBackground(Color.WHITE); // Mark as a missed shot
            return false; // Attack was a miss
        }
    }

    // Automatically places ships on the board for the opponent
    public void placeOpponentShips(int[] shipSizes, JButton[][] grid) {
        Random random = new Random();
        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(10); // Random row
                int col = random.nextInt(10); // Random column
                boolean horizontal = random.nextBoolean(); // Random orientation
                if (placeShip(row, col, size, horizontal, grid, false)) { // Attempt to place the ship
                    placed = true; // Ship placed successfully
                }
            }
        }
    }

    // Returns the total number of ship cells still not hit
    public int getRemainingShipCells() {
        return remainingShipCells;
    }

    // Clears the board for a new game
    public void clear() {
        ships.clear(); // Remove all ships
        remainingShipCells = 0; // Reset the count of ship cells
    }
}
