package my.web.app;
import com.simple.framework.HttpFramework;
import com.simple.framework.HttpStatus;

import java.sql.SQLException;
import java.util.Map;

public class Main {
    public static void main( String[] args ) {
        HttpFramework app = new HttpFramework();
        PersonRepository db = new PersonRepository("jdbc:postgresql://localhost:5432/testdb", "postgres","1234");
        db.initDB();

        app.get("/", (req, res) -> {
            res.sendHTML("index.html");
        });

        app.post("/", (req, res) -> {
            try{
                Map<String, String> body = req.getBody();
                String firstName = body.get("fname");
                String lastName = body.get("lname");
                int age = Integer.parseInt(body.get("age"));
                db.insertValues(firstName, lastName, age);
                res.sendStatus(HttpStatus.HTTP_200);
            } catch (SQLException | NullPointerException e) {
                res.sendStatus(HttpStatus.HTTP_500);
            }
        });

        app.get("/all", (req, res) -> {
            res.sendJson(db.getAll());
        });

        app.get("/details/{id}", (req, res) -> {
            Map<String, String> params = req.getRouteParams();
            res.sendJson(db.getOne(Integer.parseInt(params.get("id"))));
        });

        app.start(4221);
    }
}