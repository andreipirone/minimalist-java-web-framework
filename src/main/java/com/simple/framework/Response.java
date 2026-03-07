package com.simple.framework;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.*;
import static com.simple.framework.HttpStatus.*;


public class Response {
    private BufferedOutputStream out;
    private Map<String, String> responseMap;
    private String contentType;
    private HttpStatus code;

    public Response(BufferedOutputStream out) {
        this.out = out;
        this.responseMap = new LinkedHashMap<>();
    }

    public Response status(HttpStatus code) {
        this.code = code;
        this.responseMap.put("Status", "HTTP/1.1 " + code.getDetails());
        return this;
    }

    public void sendStatus(HttpStatus code) throws IOException {
        this.status(code).build();
    }

    public void send(String body) throws IOException {
        if(this.code == null){
            this.code = HTTP_200;
        }

        this.responseMap.put("Status","HTTP/1.1 " + this.code.getDetails());
        this.responseMap.put("Content-Type", "text/plain");
        this.responseMap.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        this.responseMap.put("Body", body);
        this.build();
    }

    public void sendJson(JSONArray body) throws IOException {
        if(this.code == null){
            this.code = HTTP_200;
        }
        String jsonBody = body.toString();
        this.responseMap.put("Status","HTTP/1.1 " + this.code.getDetails());
        this.responseMap.put("Content-Type", "application/json");
        this.responseMap.put("Content-Length", String.valueOf(jsonBody.getBytes(StandardCharsets.UTF_8).length));
        this.responseMap.put("Body", jsonBody);
        System.out.println(jsonBody);
        this.build();
    }

    public void sendJson(JSONObject body) throws IOException {
        if(this.code == null){
            this.code = HTTP_200;
        }
        String jsonBody = body.toString();
        this.responseMap.put("Status","HTTP/1.1 " + this.code.getDetails());
        this.responseMap.put("Content-Type", "application/json");
        this.responseMap.put("Content-Length", String.valueOf(jsonBody.getBytes(StandardCharsets.UTF_8).length));
        this.responseMap.put("Body", jsonBody);
        System.out.println(jsonBody);
        this.build();
    }

    public void sendHTML(String fileName) {
        try(BufferedReader in = new BufferedReader(new FileReader("src/main/resources/" + fileName))){
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

        String status = this.responseMap.get("Status");
        if (status == null) {
            // Fallback if send() didn't set it
            status = "HTTP/1.1 200 OK";
        }
        response.append(status).append(CRLF);

        for(String key : this.responseMap.keySet()){
            if(!key.equals("Body") && !key.equals("Status")){
                response.append(key)
                        .append(": ")
                        .append(this.responseMap.get(key))
                        .append(CRLF);
                }
            }

        response.append(CRLF);
        response.append(this.responseMap.get("Body"));

        byte[] rawData = response.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        out.write(rawData);
        out.flush();
    }
}
