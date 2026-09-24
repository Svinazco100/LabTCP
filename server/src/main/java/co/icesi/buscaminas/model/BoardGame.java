package co.icesi.buscaminas.model;

import java.util.Random;

public class BoardGame {

    private Cell[][] board;

    private int rows;
    private int columns;
    private int mines;

    private boolean win;
    private boolean gameEnd;

    private final Random random;

    public BoardGame() {
        this.random = new Random();
    }

    public synchronized void initGame(int n, int m, int mines) {

        if (n <= 0 || m <= 0) {
            throw new IllegalArgumentException(
                    "Las dimensiones deben ser mayores que cero."
            );
        }

        if (mines <= 0 || mines >= n * m) {
            throw new IllegalArgumentException(
                    "Cantidad de minas inválida."
            );
        }

        this.rows = n;
        this.columns = m;
        this.mines = mines;

        this.win = false;
        this.gameEnd = false;

        board = new Cell[rows][columns];

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                board[i][j] = new Cell();
            }
        }

        placeMines();

        calculateValues();
    }

    private void placeMines() {

        int placed = 0;

        while (placed < mines) {

            int i = random.nextInt(rows);
            int j = random.nextInt(columns);

            if (!board[i][j].isLandMine()) {

                board[i][j].setLandMine(true);

                placed++;
            }
        }
    }

    private void calculateValues() {

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                if (!board[i][j].isLandMine()) {

                    board[i][j].setValue(
                            countAdjacentMines(i, j)
                    );
                }
            }
        }
    }

    private int countAdjacentMines(int row, int column) {

        int count = 0;

        for (int i = row - 1; i <= row + 1; i++) {

            for (int j = column - 1; j <= column + 1; j++) {

                if (isInside(i, j)
                        && !(i == row && j == column)
                        && board[i][j].isLandMine()) {

                    count++;
                }
            }
        }

        return count;
    }

    public synchronized void selectCell(int i, int j) {

        ensureGame();

        validatePosition(i, j);

        if (gameEnd) {
            throw new IllegalStateException(
                    "La partida ya terminó."
            );
        }

        Cell cell = board[i][j];

        if (cell.isMarked()) {
            return;
        }

        if (!cell.isHide()) {
            return;
        }

        if (cell.isLandMine()) {

            cell.setHide(false);

            gameEnd = true;
            win = false;

            revealAll();

            return;
        }

        showCells(i, j);

        checkWin();
    }

    private void showCells(int i, int j) {

        if (!isInside(i, j)) {
            return;
        }

        Cell cell = board[i][j];

        if (!cell.isHide()) {
            return;
        }

        if (cell.isMarked()) {
            return;
        }

        if (cell.isLandMine()) {
            return;
        }

        cell.setHide(false);

        if (cell.getValue() != 0) {
            return;
        }

        for (int row = i - 1; row <= i + 1; row++) {

            for (int column = j - 1;
                 column <= j + 1;
                 column++) {

                if (!(row == i && column == j)) {

                    showCells(row, column);
                }
            }
        }
    }

    public synchronized void markCell(int i, int j) {

        ensureGame();

        validatePosition(i, j);

        if (gameEnd) {
            throw new IllegalStateException(
                    "La partida ya terminó."
            );
        }

        Cell cell = board[i][j];

        if (!cell.isHide()) {
            return;
        }

        cell.setMarked(
                !cell.isMarked()
        );
    }

    public synchronized void sowAll() {

        ensureGame();

        if (!win) {
            gameEnd = true;
            win = false;
        }

        revealAll();
    }

    private void revealAll() {

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                board[i][j].setShowAll(true);
                board[i][j].setHide(false);
            }
        }
    }

    private void checkWin() {

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                Cell cell = board[i][j];

                if (!cell.isLandMine()
                        && cell.isHide()) {

                    return;
                }
            }
        }

        win = true;
        gameEnd = true;

        revealAll();
    }

    public synchronized Cell[][] getBoard() {

        ensureGame();

        Cell[][] copy =
                new Cell[rows][columns];

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                copy[i][j] =
                        new Cell(board[i][j]);
            }
        }

        return copy;
    }

    public synchronized boolean isWin() {
        return win;
    }

    public synchronized boolean isGameEnd() {
        return gameEnd;
    }

    private boolean isInside(int i, int j) {

        return i >= 0
                && i < rows
                && j >= 0
                && j < columns;
    }

    private void validatePosition(int i, int j) {

        if (!isInside(i, j)) {

            throw new IllegalArgumentException(
                    "La posición está fuera del tablero."
            );
        }
    }

    private void ensureGame() {

        if (board == null) {

            throw new IllegalStateException(
                    "Primero debe iniciar una partida."
            );
        }
    }
}