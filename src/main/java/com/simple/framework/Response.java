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
    private final String staticPath;

    public Response(BufferedOutputStream out, String staticPath) {
        this.out = out;
        this.staticPath = staticPath;
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
        this.json(jsonBody);
        this.build();
    }

    private void json(String jsonBody) {
        this.responseMap.put("Status","HTTP/1.1 " + this.code.getDetails());
        this.responseMap.put("Content-Type", "application/json");
        this.responseMap.put("Content-Length", String.valueOf(jsonBody.getBytes(StandardCharsets.UTF_8).length));
        this.responseMap.put("Body", jsonBody);
        //System.out.println(jsonBody);
    }

    public void sendJson(JSONObject body) throws IOException {
        if(this.code == null){
            this.code = HTTP_200;
        }
        String jsonBody = body.toString();
        this.json(jsonBody);
        this.build();
    }

    public void sendFile(String fileName) {
        try(BufferedReader in = new BufferedReader(new FileReader(this.staticPath + fileName))){
            File staticFile = new File(this.staticPath + fileName);
            if(staticFile.exists()){
                if(this.code == null){
                    this.code = HTTP_200;
                    this.responseMap.put("Status", "HTTP/1.1 " + this.code.getDetails());
                }

                String extension = fileName.split("\\.")[1];
                switch (extension) {
                    case "html" -> {
                        this.responseMap.put("Content-Type", "text/html; charset=UTF-8");
                        txt(in);
                    }
                    case "js" -> {
                        this.responseMap.put("Content-Type", "text/javascript; charset=UTF-8");
                        txt(in);
                    }
                    case "css" -> {
                        this.responseMap.put("Content-Type", "text/css; charset=UTF-8");
                        txt(in);
                    }
                    case "jpg", "jpeg" -> {
                        this.responseMap.put("Content-Type", "image/jpg");
                        this.image(this.staticPath + fileName);
                    }
                }
            } else {
                this.sendStatus(HTTP_500);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private void txt(BufferedReader in) throws IOException {
        StringBuilder fileBody = new StringBuilder();
        String line;
        while((line = in.readLine()) != null){
            fileBody.append(line).append("\n");
        }

        this.responseMap.put("Content-Length", String.valueOf(fileBody.length()));
        this.responseMap.put("Body", fileBody.toString());

        this.build();
    }

    private void image(String imgPath) throws IOException {
        File imageFile = new File(imgPath);

        try (FileInputStream fis = new FileInputStream(imageFile)) {
            responseMap.put("Content-Length", String.valueOf(imageFile.length()));

            StringBuilder response = new StringBuilder();
            String CRLF = "\r\n";

            String status = this.responseMap.get("Status");
            if (status == null) {
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

            out.write(response.toString().getBytes(StandardCharsets.UTF_8));

            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());;
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
        out.write(response.toString().getBytes(StandardCharsets.UTF_8));

        out.write(this.responseMap.get("Body").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

}
