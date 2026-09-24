package co.icesi.buscaminas.services;

import co.icesi.buscaminas.model.BoardGame;
import co.icesi.buscaminas.model.Cell;

public class ServicesImpl {

    private final BoardGame game;

    public ServicesImpl() {

        this.game = new BoardGame();
    }

    public Cell[][] initGame(
            int n,
            int m,
            int mines
    ) {

        game.initGame(n, m, mines);

        return game.getBoard();
    }

    public Cell[][] selectCell(
            int i,
            int j
    ) {

        game.selectCell(i, j);

        return game.getBoard();
    }

    public Cell[][] markCell(
            int i,
            int j
    ) {

        game.markCell(i, j);

        return game.getBoard();
    }

    public Cell[][] printBoard() {

        return game.getBoard();
    }

    public Cell[][] sowAll() {

        game.sowAll();

        return game.getBoard();
    }

    public boolean isWin() {

        return game.isWin();
    }

    public boolean isGameEnd() {

        return game.isGameEnd();
    }
}
