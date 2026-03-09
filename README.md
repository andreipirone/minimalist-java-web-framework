# Minimalist Java Web Framework
A lightweight web framework built from scratch using Java's `ServerSocket`. This project was developed as a deep-dive into the HTTP protocol, request/response cycles, and modern Java syntax.

### Prerequisites
* JDK 11 or higher
* Maven

### Usage
```java
import com.simple.framework.HttpFramework;

public class Main {
    public static void main(String[] args) {
        HttpFramework app = new HttpFramework();

        // Configure static files directory
        app.setStaticPath("src/main/resources/");

        // Simple GET route
        app.get("/", (req, res) -> {
            res.send("Hello World");
        });

        // Path parameters (e.g., /user/123)
        app.get("/user/{id}", (req, res) -> {
            String userId = req.getParam("id");
            res.send("User Profile for ID: " + userId);
        });

        // JSON Response
        app.get("/api/data", (req, res) -> {
            MyData data = new MyData("Status OK", 200);
            res.sendJson(data);
        });

        // Start the server
        app.start(8080);
    }
}
```

### Features
- [x] ExpressJS-like syntax
- [x] Dynamic path parameters parsing
- [x] Query and body parsing
- [x] Static file serving
- [x] Concurrency
- [x] Partial error handling
- [ ] multipart/form-data parsing (in progress)
- [ ] SSL/TLS Support
- [ ] Auth/Logging Middleware
- [ ] Cookies

### Examples
1. Adding data to the database.

![example1](https://github.com/andreipirone/minimalist-java-web-framework/blob/main/media/example1.gif)

2. Receiving static files

![example2](https://github.com/andreipirone/minimalist-java-web-framework/blob/main/media/example2.gif)

## WARNING!

> This framework is currently intended for educational purposes only. It does not currently support SSL/TLS (HTTPS). All data sent is unencrypted and visible to anyone on the same network. Do not use this to handle sensitive data (passwords, PII, etc.).
