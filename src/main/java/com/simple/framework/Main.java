package com.simple.framework;
import java.util.Map;

import static com.simple.framework.HttpStatus.*;

public class Main {
    public static void main( String[] args ) {
        HttpFramework app = new HttpFramework();

        app.get("/", (req, res) -> {
            res.sendHTML("index.html");
            res.send("app run");
        });

        app.post("/", (req, res) -> {
            res.send("info sent");
            Map<String, String> body = req.getBody();
        });

        app.get("/home", (req, res) -> {
            res.send("hey");
        });

        app.get("/msg/{id}", (req, res) -> {
            res.sendStatus(HTTP_200);
        });

        app.start(4221);
    }
}
