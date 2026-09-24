package co.icesi.buscaminas.controllers;

import co.icesi.buscaminas.model.Cell;
import co.icesi.buscaminas.services.ServicesImpl;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPController {

    private final int port;

    private final Gson gson;

    private final ServicesImpl services;

    private final ExecutorService threadPool;

    private ServerSocket serverSocket;

    public TCPController(int port) {

        this.port = port;

        this.gson = new Gson();

        this.services = new ServicesImpl();

        this.threadPool =
                Executors.newFixedThreadPool(5);
    }

    public void start() throws IOException {

        serverSocket =
                new ServerSocket(
                        port,
                        50,
                        InetAddress.getByName(
                                "0.0.0.0"
                        )
                );

        System.out.println(
                "================================="
        );

        System.out.println(
                "Servidor Buscaminas TCP iniciado"
        );

        System.out.println(
                "Puerto: " + port
        );

        System.out.println(
                "================================="
        );

        while (!serverSocket.isClosed()) {

            Socket clientSocket =
                    serverSocket.accept();

            threadPool.execute(
                    new TCPClientHandler(
                            clientSocket
                    )
            );
        }
    }

    private class TCPClientHandler
            implements Runnable {

        private final Socket socket;

        public TCPClientHandler(
                Socket socket
        ) {

            this.socket = socket;
        }

        @Override
        public void run() {

            System.out.println(
                    "Cliente atendido por: "
                            + Thread.currentThread()
                            .getName()
            );

            try (
                    Socket client = socket;

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            client.getInputStream()
                                    )
                            );

                    BufferedWriter writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            client.getOutputStream()
                                    )
                            )
            ) {

                String jsonIn =
                        reader.readLine();

                if (jsonIn == null) {
                    return;
                }

                Request request =
                        gson.fromJson(
                                jsonIn,
                                Request.class
                        );

                Response response =
                        processRequest(request);

                String jsonOut =
                        gson.toJson(response);

                writer.write(jsonOut);

                writer.newLine();

                writer.flush();

            } catch (Exception e) {

                System.err.println(
                        "Error atendiendo cliente: "
                                + e.getMessage()
                );
            }
        }
    }

    private Response processRequest(
            Request request
    ) {

        Response response =
                new Response();

        try {

            if (request == null
                    || request.action == null) {

                throw new IllegalArgumentException(
                        "Petición inválida."
                );
            }

            Map<String, String> data =
                    request.data != null
                            ? request.data
                            : new HashMap<>();

            Cell[][] board;

            switch (request.action) {

                case "INIT_GAME":

                    int n =
                            Integer.parseInt(
                                    data.get("n")
                            );

                    int m =
                            Integer.parseInt(
                                    data.get("m")
                            );

                    int mines =
                            Integer.parseInt(
                                    data.get("minas")
                            );

                    board =
                            services.initGame(
                                    n,
                                    m,
                                    mines
                            );

                    response.status = "OK";

                    response.data.put(
                            "board",
                            board
                    );

                    response.data.put(
                            "win",
                            services.isWin()
                    );

                    response.data.put(
                            "gameEnd",
                            services.isGameEnd()
                    );

                    break;

                case "SELECT_CELL":

                    int i =
                            Integer.parseInt(
                                    data.get("i")
                            );

                    int j =
                            Integer.parseInt(
                                    data.get("j")
                            );

                    board =
                            services.selectCell(
                                    i,
                                    j
                            );

                    response.status = "OK";

                    response.data.put(
                            "board",
                            board
                    );

                    response.data.put(
                            "win",
                            services.isWin()
                    );

                    response.data.put(
                            "gameEnd",
                            services.isGameEnd()
                    );

                    break;

                case "MARK_CELL":

                    int mi =
                            Integer.parseInt(
                                    data.get("i")
                            );

                    int mj =
                            Integer.parseInt(
                                    data.get("j")
                            );

                    services.markCell(
                            mi,
                            mj
                    );

                    response.status = "OK";

                    board =
                            services.printBoard();

                    response.data.put(
                            "board",
                            board
                    );

                    break;

                case "GET_BOARD":

                    board =
                            services.printBoard();

                    response.status = "OK";

                    response.data.put(
                            "board",
                            board
                    );

                    response.data.put(
                            "win",
                            services.isWin()
                    );

                    response.data.put(
                            "gameEnd",
                            services.isGameEnd()
                    );

                    break;

                case "SOW_ALL":

                    board =
                            services.sowAll();

                    response.status = "OK";

                    response.data.put(
                            "board",
                            board
                    );

                    response.data.put(
                            "win",
                            services.isWin()
                    );

                    response.data.put(
                            "gameEnd",
                            services.isGameEnd()
                    );

                    break;

                default:

                    throw new IllegalArgumentException(
                            "Acción no soportada: "
                                    + request.action
                    );
            }

        } catch (Exception e) {

            response.status = "ERROR";

            response.data.put(
                    "message",
                    e.getMessage()
            );
        }

        return response;
    }

    private static class Request {

        String action;

        Map<String, String> data;
    }

    private static class Response {

        String status;

        Map<String, Object> data =
                new HashMap<>();
    }
}
