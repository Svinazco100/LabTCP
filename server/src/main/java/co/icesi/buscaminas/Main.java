package co.icesi.buscaminas;

import co.icesi.buscaminas.controllers.TCPController;

public class Main {

    public static void main(String[] args) {

        int port = 12345;

        if (args.length > 0) {

            try {

                port =
                        Integer.parseInt(
                                args[0]
                        );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Puerto inválido. Se utilizará 12345."
                );
            }
        }

        try {

            TCPController controller =
                    new TCPController(port);

            controller.start();

        } catch (Exception e) {

            System.err.println(
                    "No fue posible iniciar el servidor:"
            );

            System.err.println(
                    e.getMessage()
            );
        }
    }
}