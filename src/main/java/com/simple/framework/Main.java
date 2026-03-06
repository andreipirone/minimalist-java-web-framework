package com.simple.framework;
import java.util.Map;

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
            System.out.print(body.get("lname") + " ");
            System.out.println(body.get("fname"));
        });

        app.get("/home", (req, res) -> {
            res.send("hey");
            Map<String, String> query = req.getQuery();
            for(String key : query.keySet()){
                System.out.println(key + " = " + query.get(key));
            }
        });

        app.get("/details/{id}", (req, res) -> {
            res.send("amogus");
            Map<String, String> params = req.getRouteParams();
            for(String key : params.keySet()){
                System.out.println(key + " = " + params.get(key));
            }
        });

        app.start(4221);
    }
}
