package com.simple.framework;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static com.simple.framework.HttpStatus.*;


public class Response {
    private BufferedWriter out;
    private Map<String, String> responseMap;
    private String contentType;
    private HttpStatus code;
    private ClassLoader classLoader;

    public Response(BufferedWriter out) {
        this.out = out;
        this.responseMap = new LinkedHashMap<>();
    }

    public Response status(HttpStatus code) {
        this.code = code;
        this.responseMap.put("Status", "HTTP/1.1 " + code.getDetails());
        return this;
    }

    public void sendStatus(HttpStatus code) throws IOException {
        this.status(HTTP_200).build();
    }

    public void send(String body) throws IOException {
        if(this.code == null){
            this.code = HTTP_200;
            this.responseMap.put("Status","HTTP/1.1 " + this.code.getDetails());
        }
        this.responseMap.put("Content-Type", "text/plain");
        this.responseMap.put("Content-Length", String.valueOf(body.length()));
        this.responseMap.put("Body", body);
        this.build();
    }

    public void sendHTML(String fileName) {
        try(BufferedReader in = new BufferedReader(new FileReader("src/main/resources/" + fileName))){
            this.classLoader = getClass().getClassLoader();
            File htmlFile = new File("src/main/resources/" + fileName);
            if(htmlFile.exists()){
                if(this.code == null){
                    this.code = HTTP_200;
                    this.responseMap.put("Status", "HTTP/1.1 " + this.code.getDetails());
                }

                StringBuilder htmlBody = new StringBuilder();
                String line;
                while((line = in.readLine()) != null){
                    htmlBody.append(line).append("\n");
                }

                this.responseMap.put("Content-Type", "text/html; charset=UTF-8");
                this.responseMap.put("Content-Length", String.valueOf(htmlBody.length()));
                this.responseMap.put("Body", htmlBody.toString());

                this.build();
            } else {
                this.sendStatus(HTTP_500);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public void build() throws IOException {
        if(!this.responseMap.containsKey("Body")){
            this.responseMap.put("Content-Type", "text/plain");
            this.responseMap.put("Content-Length", String.valueOf(this.code.getDetails().length()));
            this.responseMap.put("Body", this.code.getDetails());
        }

        StringBuilder response = new StringBuilder();
        String CRLF = "\r\n";
        for(String key : this.responseMap.keySet()){
            if(!key.equals("Body")){
                if(key.equals("Status")){
                    response.append(this.responseMap.get("Status")).append(CRLF);
                } else {
                    response.append(key)
                            .append(": ")
                            .append(this.responseMap.get(key))
                            .append(CRLF);
                }
            }
        }
        response.append(CRLF).append(this.responseMap.get("Body"));

        out.write(response.toString());
        out.flush();
    }
}
