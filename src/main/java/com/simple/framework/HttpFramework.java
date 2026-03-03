package com.simple.framework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import static com.simple.framework.HttpStatus.*;

public class HttpFramework {
    private Map<String, Handler> endpointHandlers = new HashMap<>();
    private Map<String, String> requestMap;
    private int port = 8080;
    private HttpParser parser;

    public HttpFramework(){
        this.parser = new HttpParser();
    }

    public void get(String path, Handler serverHandler){
        System.out.println(path);
        this.endpointHandlers.put(path.trim(), serverHandler);
    }

    public void handleClient(Socket clientSocket){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))){

            Response res = new Response(out);
            Request req = new Request();

            String request = this.readRequest(in);
            this.requestMap = this.parser.parseRequest(request);

            String endpoint = this.requestMap.get("URL");

            if(this.endpointHandlers.containsKey(endpoint)){
                Handler serverHandler = this.endpointHandlers.get(endpoint);
                serverHandler.execute(req, res);
            } else {
                res.status(HTTP_404).build();
            }
        } catch (IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public String readRequest(BufferedReader in) throws IOException {
        StringBuilder request = new StringBuilder();
        String line;
        while((line = in.readLine()) != null && !line.isEmpty()){
            request.append(line);
            request.append("\r\n");
        }
        request.append("\r\n");
        return request.toString();
    }

    public void listen(int port){
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
