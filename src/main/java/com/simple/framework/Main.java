package com.simple.framework;
import static com.simple.framework.HttpStatus.*;

public class Main {
    public static void main( String[] args ) {
        HttpFramework app = new HttpFramework();

        app.get("/", (req, res) -> {
            res.status(HTTP_200).build();
        });

        app.get("/msg/{id}", (req, res) -> {
            res.status(HTTP_200).build();
        });

        app.listen(4221);
    }
}
