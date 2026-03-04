package com.simple.framework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import static com.simple.framework.HttpStatus.*;

public class HttpFramework {
    private Map<String, Handler> getHandlers;
    private Map<String, Handler> postHandlers;
    private Map<String, String> requestMap;
    private int port = 8080;

    public HttpFramework(){
        this.getHandlers = new HashMap<>();
        this.postHandlers = new HashMap<>();
    }

    public void get(String path, Handler serverHandler){
        System.out.println(path);
        this.getHandlers.put(path.trim(), serverHandler);
    }

    public void post(String path, Handler serverHandler){
        System.out.println(path);
        this.postHandlers.put(path.trim(), serverHandler);
    }

    public void handleClient(Socket clientSocket){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())){

            HttpParser parser = new HttpParser();
            this.requestMap = parser.parseRequest(in);
            Response res = new Response(out);
            Request req = new Request(this.requestMap);

            String method = this.requestMap.get("Method");
            String endpoint = this.requestMap.get("URL");

            if(this.getHandlers.containsKey(endpoint) && method.equals("GET")){
                Handler serverHandler = this.getHandlers.get(endpoint);
                serverHandler.execute(req, res);
            } else if(this.postHandlers.containsKey(endpoint) && method.equals("POST")){
                Handler serverHandler = this.postHandlers.get(endpoint);
                serverHandler.execute(req, res);
            } else  {
                res.sendStatus(HTTP_404);
            }

        } catch (IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                Thread t = new Thread(() -> this.handleClient(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
