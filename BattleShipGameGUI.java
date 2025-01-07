import javax.swing.*;

// Main class to launch the Battleship game GUI
public class BattleShipGameGUI {
    public static void main(String[] args) {
        // Run the GameWindow class on the Swing event dispatch thread
        SwingUtilities.invokeLater(GameWindow::new);
    }
}
