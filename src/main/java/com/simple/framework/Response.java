package com.simple.framework;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private BufferedWriter out;
    private Map<String, String> responseMap;
    private String contentType;
    private HttpStatus code;

    public Response(BufferedWriter out) {
        this.out = out;
        this.responseMap = new HashMap<>();
    }

    public Response status(HttpStatus code) {
        this.code = code;
        this.responseMap.put("Status", "HTTP/1.1 " + code.getDetails());
        return this;
    }

    public void sendStatus(HttpStatus code) throws IOException {
        this.status(HttpStatus.HTTP_200).build();
    }

    public void send(String body) throws IOException {
        if(this.code == null){
            this.status(HttpStatus.HTTP_200);
        }
        this.responseMap.put("Body", body);
        this.build();
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
    }
}
