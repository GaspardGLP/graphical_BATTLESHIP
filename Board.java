import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private final List<List<Point>> ships = new ArrayList<>();
    private int remainingShipCells = 0;

    public boolean placeShip(int row, int col, int size, boolean horizontal, JButton[][] grid, boolean isPlayerGrid) {
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= 10 || c >= 10 || (Boolean) grid[r][c].getClientProperty("hasShip")) {
                return false;
            }
        }

        List<Point> ship = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            grid[r][c].putClientProperty("hasShip", true);
            if (isPlayerGrid) {
                grid[r][c].setBackground(Color.BLUE);
            }
            ship.add(new Point(r, c));
        }

        ships.add(ship);
        remainingShipCells += size;
        return true;
    }

    public boolean attack(int row, int col, JButton[][] grid) {
        if ((Boolean) grid[row][col].getClientProperty("hasShip")) {
            grid[row][col].setBackground(Color.RED);
            if (!(Boolean) grid[row][col].getClientProperty("hit")) {
                grid[row][col].putClientProperty("hit", true);
                remainingShipCells--;
            }
            return true;
        } else {
            grid[row][col].setBackground(Color.WHITE);
            return false;
        }
    }

    public void placeOpponentShips(int[] shipSizes, JButton[][] grid) {
        Random random = new Random();
        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean horizontal = random.nextBoolean();
                if (placeShip(row, col, size, horizontal, grid, false)) {
                    placed = true;
                }
            }
        }
    }

    public int getRemainingShipCells() {
        return remainingShipCells;
    }

    public void clear() {
        ships.clear();
        remainingShipCells = 0;
    }
}
