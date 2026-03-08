package com.simple.framework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.simple.framework.HttpStatus.*;

public class HttpFramework {
    private final Map<String, Handler> getHandlers;
    private final Map<String, Handler> postHandlers;
    private final HttpParser parser;
    private String staticFolderPath;
    public int THREAD_POOL_SIZE = 10;

    public HttpFramework(){
        this.getHandlers = new HashMap<>();
        this.postHandlers = new HashMap<>();
        this.parser = new HttpParser();
    }

    public void get(String path, Handler serverHandler){
        System.out.println(path);
        String processedPath = this.parser.extractParams(path, true);
        this.getHandlers.put(processedPath, serverHandler);
    }

    public void post(String path, Handler serverHandler){
        System.out.println(path);
        String processedPath = this.parser.extractParams(path, true);
        this.postHandlers.put(processedPath, serverHandler);
    }

    public void setStaticPath(String path){
        this.staticFolderPath = path;

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.filter(Files::isRegularFile)
                    .forEach((file) -> {
                        String filePath = file.toString();
                        filePath = filePath.replaceAll("\\\\", "/");
                        filePath = "/"+ filePath.replaceAll(path, "");
                        System.out.println(filePath);
                        String processedPath = parser.extractParams(filePath, true);
                        String finalFilePath = filePath;
                        System.out.println(filePath);
                        this.getHandlers.put(processedPath, (req, res) -> res.sendFile(finalFilePath));
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        File dir = new File(path);
//
//        File[] files = dir.listFiles();
//        for (int i = 0; i < files.length; i++){
//            if (files[i].isFile()){
//               String filePath = files[i].getPath();
//                System.out.println(filePath);
//                System.out.println(filePath.substring(filePath.lastIndexOf("\\")));
//                filePath = "/" + filePath.substring(filePath.lastIndexOf("\\") + 1);
//                String processedPath = parser.extractParams(filePath, true);
//                String finalFilePath = filePath;
//                //System.out.println(filePath);
//                this.getHandlers.put(processedPath, (req, res) -> res.sendFile(finalFilePath));
//            }
//        }
    }


    public void handleClient(Socket clientSocket){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())){

            HttpParser par = new HttpParser(this.parser);

            Map<String, String> requestMap = par.parseRequest(in);

            Response res = new Response(out, this.staticFolderPath);
            Request req = new Request(requestMap);

            String method = requestMap.get("Method");
            String endpoint = requestMap.get("URL");

            if(par.hasMatch(endpoint, getHandlers) && method.equals("GET")){
                endpoint = par.getMatched();
                req.setParamMap(par.getParamsMap());
                Handler serverHandler = this.getHandlers.get(endpoint);
                serverHandler.execute(req, res);
            } else if(par.hasMatch(endpoint, getHandlers) && method.equals("POST")){
                endpoint = par.getMatched();
                req.setParamMap(par.getParamsMap());
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
        try(ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            System.out.println("Server listening on port " + port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                //using threads
                //Thread t = new Thread(() -> this.handleClient(clientSocket));

                //using thread pool
                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
