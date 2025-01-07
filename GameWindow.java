import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Main game window for Battleship
public class GameWindow extends JFrame implements ActionListener {
    // Player and opponent grids (10x10 buttons each)
    private final JButton[][] playerGrid = new JButton[10][10];
    private final JButton[][] opponentGrid = new JButton[10][10];

    // Game state variables
    private boolean placingShips = true; // True if player is placing ships
    private int shipsPlaced = 0; // Number of ships placed by the player
    private final int[] shipSizes = {5, 4, 3, 3, 2}; // Sizes of the ships
    private boolean horizontal = true; // Orientation of the ship being placed (horizontal/vertical)
    private boolean playerTurn = true; // True if it's the player's turn

    private final Player player; // Player object
    private final Player opponent; // Opponent object

    // Constructor to initialize the game window
    public GameWindow() {
        player = new Player();
        opponent = new Player();

        // Window properties
        setTitle("Bataille Navale");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Top panel for control buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton rotateButton = new JButton("Rotate Ship");
        JButton resetButton = new JButton("Reset");
        JButton quitButton = new JButton("Quit");
        topPanel.add(rotateButton);
        topPanel.add(resetButton);
        topPanel.add(quitButton);

        // Button actions
        rotateButton.addActionListener(e -> horizontal = !horizontal); // Toggle ship orientation
        resetButton.addActionListener(e -> resetGame()); // Reset the game
        quitButton.addActionListener(e -> System.exit(0)); // Quit the game

        // Split panel for player and opponent grids
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setEnabled(false); // Disable resizing
        splitPane.setDividerLocation(500); // Divide space equally

        // Panels for grids
        JPanel playerPanel = new JPanel(new GridLayout(10, 10));
        JPanel opponentPanel = new JPanel(new GridLayout(10, 10));

        // Initialize both grids
        initializeGrid(playerGrid, playerPanel, true); // Player grid
        initializeGrid(opponentGrid, opponentPanel, false); // Opponent grid

        splitPane.setLeftComponent(playerPanel);
        splitPane.setRightComponent(opponentPanel);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Center the window on the screen and make it visible
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Initialize the grid of buttons
    private void initializeGrid(JButton[][] grid, JPanel panel, boolean isPlayerGrid) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = new JButton(); // Create button
                grid[i][j].setBackground(isPlayerGrid ? Color.CYAN : Color.LIGHT_GRAY); // Set default color
                grid[i][j].addActionListener(this); // Add action listener
                grid[i][j].putClientProperty("hasShip", false); // Property to track if a ship is present
                grid[i][j].putClientProperty("hit", false); // Property to track if the cell was hit
                panel.add(grid[i][j]); // Add button to the panel
            }
        }
    }

    // Reset the game state and grids
    private void resetGame() {
        shipsPlaced = 0;
        placingShips = true;
        playerTurn = true;
        player.reset(); // Reset player's board
        opponent.reset(); // Reset opponent's board
        clearGrid(playerGrid); // Clear player grid
        clearGrid(opponentGrid); // Clear opponent grid
        JOptionPane.showMessageDialog(this, "Game reset.");
    }

    // Clear the grid (remove ships, reset colors and properties)
    private void clearGrid(JButton[][] grid) {
        for (JButton[] row : grid) {
            for (JButton button : row) {
                button.setBackground(Color.CYAN); // Reset color
                button.setEnabled(true); // Enable button
                button.putClientProperty("hasShip", false); // Remove ship property
                button.putClientProperty("hit", false); // Remove hit property
            }
        }
    }

    // Handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();

        if (placingShips) {
            handlePlacingShips(source); // Handle placing ships
        } else {
            handlePlayerTurn(source); // Handle player's turn
        }
    }

    // Handle ship placement
    private void handlePlacingShips(JButton source) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (source == playerGrid[i][j]) { // Find the clicked button
                    if (player.placeShip(i, j, shipSizes[shipsPlaced], horizontal, playerGrid)) {
                        shipsPlaced++;
                        if (shipsPlaced == shipSizes.length) { // All ships placed
                            placingShips = false;
                            opponent.placeOpponentShips(shipSizes, opponentGrid); // Place opponent's ships
                            JOptionPane.showMessageDialog(this, "All ships placed. The game begins!");
                        }
                    }
                    return;
                }
            }
        }
    }

    // Handle player's attack on the opponent's grid
    private void handlePlayerTurn(JButton source) {
        if (!playerTurn) { // Ignore if it's not the player's turn
            JOptionPane.showMessageDialog(this, "It's not your turn!");
            return;
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (source == opponentGrid[i][j]) { // Find the clicked cell
                    if (!player.attack(i, j, opponentGrid, opponent)) {
                        return; // Ignore already attacked cells
                    }
                    if (opponent.getShipCells() == 0) { // Check for win condition
                        JOptionPane.showMessageDialog(this, "You win!");
                        resetGame();
                        return;
                    }
                    playerTurn = false; // End player's turn
                    opponentMove(); // Let opponent take their turn
                    return;
                }
            }
        }
    }

    // Simulate opponent's move
    private void opponentMove() {
        while (true) {
            int row = (int) (Math.random() * 10);
            int col = (int) (Math.random() * 10);
            if (player.attack(row, col, playerGrid, opponent)) {
                break; // Exit when a valid move is made
            }
        }

        if (player.getShipCells() == 0) { // Check for loss condition
            JOptionPane.showMessageDialog(this, "You lose!");
            resetGame();
        } else {
            playerTurn = true; // Switch turn back to player
        }
    }
}
