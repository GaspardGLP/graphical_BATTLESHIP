import javax.swing.*;

public class Player {
    private final Board board;

    public Player() {
        board = new Board();
    }

    public boolean placeShip(int row, int col, int size, boolean horizontal, JButton[][] grid) {
        return board.placeShip(row, col, size, horizontal, grid, true);
    }

    public boolean attack(int row, int col, JButton[][] grid, Player opponent) {
        return board.attack(row, col, grid);
    }

    public void placeOpponentShips(int[] shipSizes, JButton[][] grid) {
        board.placeOpponentShips(shipSizes, grid);
    }

    public void reset() {
        board.clear();
    }

    public int getShipCells() {
        return board.getRemainingShipCells();
    }
}
