package co.icesi.buscaminas.client;

public class Response {

    public String status;

    public Data data;

    public static class Data {

        public Cell[][] board;

        public Boolean win;

        public Boolean gameEnd;

        public String message;
    }
}
