package my.web.app;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class PersonRepository {
    private Connection conn;
    private final String url;
    private final String user;
    private final String password;

    public PersonRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void initDB() {
        try{
            Class.forName("org.postgresql.Driver");
            this.conn = DriverManager.getConnection(this.url,this.user, this.password);
            Statement stmt = conn.createStatement();
            String tableSql = "CREATE TABLE IF NOT EXISTS persons (" +
                    "    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                    "    first_name VARCHAR(50) NOT NULL," +
                    "    last_name VARCHAR(50) NOT NULL," +
                    "    age INTEGER\n" +
                    ");";
            stmt.execute(tableSql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertValues(String name, String lastName, int age) throws SQLException {
        Statement stmt = conn.createStatement();
        String insertCommand = String.format("INSERT INTO persons (first_name, last_name, age) VALUES ('%s', '%s', %d);", name, lastName, age);
        stmt.execute(insertCommand);
        stmt.close();
    }

    public JSONArray getAll(){
        String selectSql = "SELECT * FROM persons";
        JSONArray personArray = new JSONArray();
        try (Statement stmt = conn.createStatement(); ResultSet resultSet = stmt.executeQuery(selectSql)) {
            while (resultSet.next()) {
                PersonModel person = new PersonModel();
                person.setId(resultSet.getInt("id"));
                person.setFirstName(resultSet.getString("first_name"));
                person.setLastName(resultSet.getString("last_name"));
                person.setAge(resultSet.getInt("age"));
                JSONObject personJson = new JSONObject(person);
                personArray.put(personJson);
            }
            System.out.println(personArray);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return personArray;
    }

    public JSONObject getOne(int id){
        String query = "SELECT * FROM PERSONS WHERE ID = " + id;
        JSONObject personJson = null;
        try (Statement stmt = conn.createStatement(); ResultSet resultSet = stmt.executeQuery(query)){
            while(resultSet.next()){
                PersonModel person = new PersonModel();
                person.setId(resultSet.getInt("id"));
                person.setFirstName(resultSet.getString("first_name"));
                person.setLastName(resultSet.getString("last_name"));
                person.setAge(resultSet.getInt("age"));
                personJson = new JSONObject(person);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
        return personJson;
    }
}
