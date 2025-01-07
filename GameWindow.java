import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow {
    private JFrame frame;
    private Board board1, board2;
    private JButton[][] buttons1, buttons2;
    private Player player1, player2;
    private boolean player1Turn;
    private JLabel statusLabel;

    public GameWindow() {
        frame = new JFrame("Bataille Navale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        player1 = new Player("Joueur 1");
        player2 = new Player("Joueur 2");
        board1 = player1.getBoard();
        board2 = player2.getBoard();

        buttons1 = new JButton[10][10];
        buttons2 = new JButton[10][10];

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(1, 2));

        JPanel panel1 = createBoardPanel(board1, buttons1, true);  // Affiche les bateaux de ce joueur
        JPanel panel2 = createBoardPanel(board2, buttons2, false); // Affiche les bateaux de l'autre joueur

        gamePanel.add(panel1);
        gamePanel.add(panel2);

        frame.add(gamePanel, BorderLayout.CENTER);

        // Status label (affiche qui joue)
        statusLabel = new JLabel("C'est au tour de " + player1.getName(), JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);
    }

    private JPanel createBoardPanel(Board board, JButton[][] buttons, boolean showShips) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 10));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                buttons[i][j] = new JButton("~");
                if (showShips && board.getShips()[i][j] == 1) {
                    buttons[i][j].setBackground(Color.GRAY); // Affiche un bateau pour le joueur courant
                } else {
                    buttons[i][j].setBackground(Color.BLUE);
                }
                panel.add(buttons[i][j]);

                final int row = i;
                final int col = j;
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (player1Turn && !showShips) {
                            handlePlayerTurn(board2, buttons2, row, col);
                        } else if (!player1Turn && !showShips) {
                            handlePlayerTurn(board1, buttons1, row, col);
                        }
                    }
                });
            }
        }
        return panel;
    }

    private void handlePlayerTurn(Board opponentBoard, JButton[][] opponentButtons, int row, int col) {
        boolean hit = opponentBoard.shoot(row, col);

        // Affichage du résultat du tir
        opponentButtons[row][col].setText(hit ? "X" : "O");
        opponentButtons[row][col].setBackground(hit ? Color.GREEN : Color.RED);

        // Vérification de la fin du jeu
        if (isGameOver(opponentBoard)) {
            JOptionPane.showMessageDialog(frame, "Le joueur " + (player1Turn ? player1.getName() : player2.getName()) + " a gagné !");
            resetGame();
        } else {
            switchTurn();
        }
    }

    private void switchTurn() {
        player1Turn = !player1Turn;
        statusLabel.setText("C'est au tour de " + (player1Turn ? player1.getName() : player2.getName()));
    }

    private boolean isGameOver(Board opponentBoard) {
        int sunkShips = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (opponentBoard.getShips()[i][j] == 1) {
                    sunkShips++;
                }
            }
        }
        return sunkShips == 0;
    }

    public void startGame() {
        player1.getBoard().placeShip(5);
        player1.getBoard().placeShip(4);
        player1.getBoard().placeShip(3);
        player1.getBoard().placeShip(3);
        player1.getBoard().placeShip(2);

        player2.getBoard().placeShip(5);
        player2.getBoard().placeShip(4);
        player2.getBoard().placeShip(3);
        player2.getBoard().placeShip(3);
        player2.getBoard().placeShip(2);

        player1Turn = true;
        frame.setVisible(true);
    }

    private void resetGame() {
        player1.getBoard().clearBoard();
        player2.getBoard().clearBoard();
        startGame();
    }
}
