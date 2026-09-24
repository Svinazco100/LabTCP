package co.icesi.buscaminas.client;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainClient {

    private static final Scanner scanner =
            new Scanner(System.in);

    private static final BuscaminasTCPClient client =
            new BuscaminasTCPClient();

    private static String host =
            "localhost";

    private static int port =
            12345;

    private static final String RESET =
            "\u001B[0m";

    private static final String YELLOW =
            "\u001B[33m";

    private static final String RED =
            "\u001B[31m";

    public static void main(String[] args) {

        if (args.length >= 1) {

            host = args[0];
        }

        if (args.length >= 2) {

            try {

                port =
                        Integer.parseInt(
                                args[1]
                        );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Puerto inválido. Se utilizará 12345."
                );
            }
        }

        boolean running = true;

        while (running) {

            showMenu();

            int option =
                    readInt(
                            "Seleccione una opción: "
                    );

            try {

                switch (option) {

                    case 1:

                        initGame();

                        break;

                    case 2:

                        selectCell();

                        break;

                    case 3:

                        markCell();

                        break;

                    case 4:

                        getBoard();

                        break;

                    case 5:

                        surrender();

                        break;

                    case 6:

                        running = false;

                        System.out.println(
                                "Cliente finalizado."
                        );

                        break;

                    default:

                        System.out.println(
                                "Opción inválida."
                        );
                }

            } catch (IOException e) {

                System.out.println(
                        "Error de comunicación con el servidor:"
                );

                System.out.println(
                        e.getMessage()
                );
            }
        }

        scanner.close();
    }

    private static void showMenu() {

        System.out.println();

        System.out.println(
                "============================================="
        );

        System.out.println(
                " BUSCAMINAS DISTRIBUIDO - CLIENTE TCP"
        );

        System.out.println(
                "============================================="
        );

        System.out.println(
                "[1] Iniciar nueva partida"
        );

        System.out.println(
                "[2] Destapar celda"
        );

        System.out.println(
                "[3] Marcar / Desmarcar bandera"
        );

        System.out.println(
                "[4] Consultar estado actual del tablero"
        );

        System.out.println(
                "[5] Rendirse y revelar tablero completo"
        );

        System.out.println(
                "[6] Salir"
        );

        System.out.println();
    }

    private static void initGame()
            throws IOException {

        int rows =
                readInt(
                        "Filas: "
                );

        int columns =
                readInt(
                        "Columnas: "
                );

        int mines =
                readInt(
                        "Número de minas: "
                );

        Map<String, String> data =
                new HashMap<>();

        data.put(
                "n",
                String.valueOf(rows)
        );

        data.put(
                "m",
                String.valueOf(columns)
        );

        data.put(
                "minas",
                String.valueOf(mines)
        );

        Request request =
                new Request(
                        "INIT_GAME",
                        data
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        processResponse(response);
    }

    private static void selectCell()
            throws IOException {
        int i =
                readInt(
                        "Fila: "
                );

        int j =
                readInt(
                        "Columna: "
                );

        Map<String, String> data =
                new HashMap<>();

        data.put(
                "i",
                String.valueOf(i)
        );

        data.put(
                "j",
                String.valueOf(j)
        );

        Request request =
                new Request(
                        "SELECT_CELL",
                        data
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        if (processResponse(response)) {

            checkGameEnd(response);
        }
    }

    private static void markCell()
            throws IOException {

        int i =
                readInt(
                        "Fila: "
                );

        int j =
                readInt(
                        "Columna: "
                );

        Map<String, String> data =
                new HashMap<>();

        data.put(
                "i",
                String.valueOf(i)
        );

        data.put(
                "j",
                String.valueOf(j)
        );

        Request request =
                new Request(
                        "MARK_CELL",
                        data
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        processResponse(response);
    }

    private static void getBoard()
            throws IOException {

        Request request =
                new Request(
                        "GET_BOARD"
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        processResponse(response);
    }

    private static void surrender()
            throws IOException {

        Request request =
                new Request(
                        "SOW_ALL"
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        System.out.println();

        System.out.println(
                "Te rendiste. Tablero completo:"
        );

        processResponse(response);
    }

    private static boolean processResponse(
            Response response
    ) {

        if (response == null) {

            System.out.println(
                    "El servidor no devolvió una respuesta."
            );

            return false;
        }

        if ("ERROR".equalsIgnoreCase(
                response.status
        )) {

            System.out.println(
                    "ERROR DEL SERVIDOR"
            );

            if (response.data != null
                    && response.data.message != null) {

                System.out.println(
                        response.data.message
                );
            }

            return false;
        }

        if (response.data != null
                && response.data.board != null) {

            printBoard(
                    response.data.board
            );
        }

        if (response.data != null
                && response.data.message != null) {

            System.out.println(
                    response.data.message
            );
        }

        return true;
    }

    private static void checkGameEnd(
            Response response
    ) throws IOException {

        if (response.data == null
                || response.data.gameEnd == null) {

            return;
        }

        if (!response.data.gameEnd) {

            return;
        }

        if (Boolean.TRUE.equals(
                response.data.win
        )) {

            System.out.println();

            System.out.println(
                    "🏆 ¡GANASTE LA PARTIDA!"
            );

        } else {

            System.out.println();

            System.out.println(
                    "💥 ¡PISASTE UNA MINA!"
            );

            System.out.println(
                    "Fin de la partida."
            );

            revealBoard();
        }
    }

    private static void revealBoard()
            throws IOException {

        Request request =
                new Request(
                        "SOW_ALL"
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        processResponse(response);
    }

    private static void printBoard(
            Cell[][] board
    ) {

        if (board == null
                || board.length == 0) {

            return;
        }

        System.out.println();

        System.out.print("     ");

        for (int j = 0;
             j < board[0].length;
             j++) {

            System.out.printf(
                    "%3d ",
                    j
            );
        }

        System.out.println();

        for (int i = 0;
             i < board.length;
             i++) {

            System.out.printf(
                    "%3d  ",
                    i
            );

            for (int j = 0;
                 j < board[i].length;
                 j++) {

                Cell cell =
                        board[i][j];

                if (cell.isMarked
                        && !cell.showAll) {

                    System.out.print(
                            "["
                                    + YELLOW
                                    + "M"
                                    + RESET
                                    + "] "
                    );

                } else if (cell.hide
                        && !cell.showAll) {

                    System.out.print(
                            "[.] "
                    );

                } else if (cell.isLandMine) {

                    System.out.print(
                            "["
                                    + RED
                                    + "*"
                                    + RESET
                                    + "] "
                    );

                } else if (cell.value == 0) {

                    System.out.print(
                            "[ ] "
                    );

                } else {

                    System.out.print(
                            "["
                                    + cell.value
                                    + "] "
                    );
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    private static int readInt(
            String message
    ) {

        while (true) {

            System.out.print(message);

            String value =
                    scanner.nextLine();

            try {

                return Integer.parseInt(
                        value
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Ingrese un número válido."
                );
            }
        }
    }
}
