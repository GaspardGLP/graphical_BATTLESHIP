import java.util.Random;

public class Board {
    private char[][] grid;
    private int[][] ships;  // Matrice représentant la position des bateaux

    public Board() {
        this.grid = new char[10][10];
        this.ships = new int[10][10];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = '~'; // ~ = eau
                ships[i][j] = 0;  // 0 = pas de bateau
            }
        }
    }

    public int[][] getShips() {
        return ships;
    }

    public void placeShip(int size) {
        Random random = new Random();
        boolean placed = false;

        while (!placed) {
            int row = random.nextInt(10);    // Choix aléatoire de la ligne
            int col = random.nextInt(10);    // Choix aléatoire de la colonne
            boolean horizontal = random.nextBoolean();  // Choix aléatoire horizontal/vertical

            // Vérifie si le bateau peut être placé à cette position
            if (canPlaceShip(row, col, size, horizontal)) {
                // Si possible, place le bateau
                for (int i = 0; i < size; i++) {
                    if (horizontal) {
                        ships[row][col + i] = 1;  // Placer le bateau horizontalement
                    } else {
                        ships[row + i][col] = 1;  // Placer le bateau verticalement
                    }
                }
                placed = true;  // Le bateau a été placé
            }
        }
    }

    // Vérifie si un bateau peut être placé à une position donnée
    private boolean canPlaceShip(int row, int col, int size, boolean horizontal) {
        if (horizontal) {
            if (col + size > 10) return false; // Dépassement de la grille
            for (int i = 0; i < size; i++) {
                if (ships[row][col + i] == 1) return false; // Il y a déjà un bateau
            }
        } else {
            if (row + size > 10) return false; // Dépassement de la grille
            for (int i = 0; i < size; i++) {
                if (ships[row + i][col] == 1) return false; // Il y a déjà un bateau
            }
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean shoot(int row, int col) {
        if (ships[row][col] == 1) {
            grid[row][col] = 'X';  // Marquer un tir réussi
            ships[row][col] = 0;   // Le bateau est coulé
            return true;
        } else {
            grid[row][col] = 'O';  // Marquer un tir raté
            return false;
        }
    }

    // Ajout d'une méthode pour effacer le plateau (utile pour réinitialiser le jeu)
    public void clearBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                ships[i][j] = 0;
                grid[i][j] = '~';  // Réinitialiser l'état de la grille
            }
        }
    }
}
