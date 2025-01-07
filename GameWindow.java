import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame implements ActionListener {
    private final JButton[][] playerGrid = new JButton[10][10];
    private final JButton[][] opponentGrid = new JButton[10][10];
    private boolean placingShips = true;
    private int shipsPlaced = 0;
    private final int[] shipSizes = {5, 4, 3, 3, 2}; // Longueurs des bateaux
    private boolean horizontal = true;
    private boolean playerTurn = true;

    private final Player player;
    private final Player opponent;

    public GameWindow() {
        player = new Player();
        opponent = new Player();

        setTitle("Bataille Navale");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel topPanel = new JPanel(new FlowLayout());
        JButton rotateButton = new JButton("Tourner Bateau");
        JButton resetButton = new JButton("Réinitialiser");
        JButton quitButton = new JButton("Quitter");
        topPanel.add(rotateButton);
        topPanel.add(resetButton);
        topPanel.add(quitButton);

        rotateButton.addActionListener(e -> horizontal = !horizontal);
        resetButton.addActionListener(e -> resetGame());
        quitButton.addActionListener(e -> System.exit(0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setEnabled(false);
        splitPane.setDividerLocation(500);

        JPanel playerPanel = new JPanel(new GridLayout(10, 10));
        JPanel opponentPanel = new JPanel(new GridLayout(10, 10));

        initializeGrid(playerGrid, playerPanel, true);
        initializeGrid(opponentGrid, opponentPanel, false);

        splitPane.setLeftComponent(playerPanel);
        splitPane.setRightComponent(opponentPanel);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeGrid(JButton[][] grid, JPanel panel, boolean isPlayerGrid) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = new JButton();
                grid[i][j].setBackground(isPlayerGrid ? Color.CYAN : Color.LIGHT_GRAY);
                grid[i][j].addActionListener(this);
                grid[i][j].putClientProperty("hasShip", false);
                panel.add(grid[i][j]);
            }
        }
    }

    private void resetGame() {
        shipsPlaced = 0;
        placingShips = true;
        playerTurn = true;
        player.reset();
        opponent.reset();
        clearGrid(playerGrid);
        clearGrid(opponentGrid);
        JOptionPane.showMessageDialog(this, "Jeu réinitialisé.");
    }

    private void clearGrid(JButton[][] grid) {
        for (JButton[] row : grid) {
            for (JButton button : row) {
                button.setBackground(Color.CYAN);
                button.setEnabled(true);
                button.putClientProperty("hasShip", false);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();

        if (placingShips) {
            handlePlacingShips(source);
        } else {
            handlePlayerTurn(source);
        }
    }

    private void handlePlacingShips(JButton source) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (source == playerGrid[i][j]) {
                    // Essayer de placer le bateau sur la grille
                    if (player.placeShip(i, j, shipSizes[shipsPlaced], horizontal, playerGrid)) {
                        // Colorier les cases où le bateau est placé
                        for (int k = 0; k < shipSizes[shipsPlaced]; k++) {
                            int r = i + (horizontal ? 0 : k);
                            int c = j + (horizontal ? k : 0);
                            playerGrid[r][c].setBackground(Color.DARK_GRAY); // Changer la couleur pour afficher le bateau
                            playerGrid[r][c].setEnabled(false); // Empêcher de placer d'autres bateaux sur ces cases
                        }
                        shipsPlaced++;
                        if (shipsPlaced == shipSizes.length) {
                            placingShips = false;
                            opponent.placeOpponentShips(shipSizes, opponentGrid);
                            JOptionPane.showMessageDialog(this, "Tous les bateaux sont placés. Le jeu commence !");
                        }
                    }
                    return;
                }
            }
        }
    }


    private void handlePlayerTurn(JButton source) {
        if (!playerTurn) {
            JOptionPane.showMessageDialog(this, "Ce n'est pas votre tour !");
            return;  // Ne pas permettre de cliquer pendant le tour de l'IA
        }
        // Logique de l'attaque du joueur
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (source == opponentGrid[i][j]) {
                    player.attack(i, j, opponentGrid, opponent);
                    if (opponent.getShipCells() == 0) {
                        JOptionPane.showMessageDialog(this, "Vous avez gagné !");
                        resetGame();
                        return;
                    }
                    // Tour de l'IA après le coup du joueur
                    playerTurn = false;
                    opponentMove();  // L'IA joue son tour
                    return;
                }
            }
        }
    }



    private void opponentMove() {
        // Créer un SwingWorker pour gérer le mouvement de l'IA dans un thread séparé
        SwingWorker<Void, Void> opponentWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // L'IA ne joue qu'un seul coup
                int row = (int) (Math.random() * 10);
                int col = (int) (Math.random() * 10);
                boolean hit = false;

                // L'IA continue à attaquer jusqu'à ce qu'elle touche un bateau
                while (!hit) {
                    // Essaie de toucher un bateau du joueur
                    hit = player.attack(row, col, playerGrid, opponent);
                }

                // Vérification si le joueur a perdu
                if (player.getShipCells() == 0) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(GameWindow.this, "Vous avez perdu !"));
                    resetGame();
                } else {
                    // Mettre à jour `playerTurn` pour permettre au joueur de jouer
                    SwingUtilities.invokeLater(() -> playerTurn = true);
                }

                return null;
            }
        };

        // Exécuter le travail de l'IA en arrière-plan
        opponentWorker.execute();
    }
}
