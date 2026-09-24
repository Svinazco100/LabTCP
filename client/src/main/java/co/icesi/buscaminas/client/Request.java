package co.icesi.buscaminas.client;

import java.util.HashMap;
import java.util.Map;

public class Request {

    public String action;

    public Map<String, String> data;

    public Request(String action) {

        this.action = action;

        this.data = new HashMap<>();
    }

    public Request(
            String action,
            Map<String, String> data
    ) {

        this.action = action;

        this.data = data;
    }
}
