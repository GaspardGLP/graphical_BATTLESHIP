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
        // Vérifier si c'est le tour du joueur
        if (!playerTurn) {
            JOptionPane.showMessageDialog(this, "Ce n'est pas votre tour !");
            return;  // Si ce n'est pas le tour du joueur, on ignore l'action
        }

        // Le joueur attaque l'adversaire
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (source == opponentGrid[i][j]) {
                    // L'attaque est effectuée ici
                    player.attack(i, j, opponentGrid, opponent);

                    // Vérification si l'adversaire a perdu toutes ses cellules de bateau
                    if (opponent.getShipCells() == 0) {
                        JOptionPane.showMessageDialog(this, "Vous avez gagné !");
                        resetGame();
                        return;
                    }

                    // L'attaque est terminée, c'est maintenant au tour de l'IA
                    playerTurn = false;
                    opponentMove();  // L'IA joue son tour
                    return;
                }
            }
        }
    }


    private void opponentMove() {
        // L'IA effectue un seul coup
        if (player.getShipCells() == 0) {
            // Le joueur a perdu, le jeu est terminé
            JOptionPane.showMessageDialog(this, "Vous avez perdu !");
            resetGame();
            return;
        }

        // L'IA choisit une case aléatoire pour attaquer
        int row = (int) (Math.random() * 10);
        int col = (int) (Math.random() * 10);

        // L'IA effectue l'attaque
        boolean hit = player.attack(row, col, playerGrid, opponent);

        // Si le joueur perd, réinitialisation du jeu
        if (player.getShipCells() == 0) {
            JOptionPane.showMessageDialog(this, "Vous avez perdu !");
            resetGame();
            return;
        }

        // Mise à jour de l'état du jeu, le tour passe au joueur
        playerTurn = true;

        // Repeindre la fenêtre pour que l'état de l'interface soit bien mis à jour
        SwingUtilities.invokeLater(() -> {
            repaint();  // Repeindre pour signaler que c'est le tour du joueur
        });
    }
}
