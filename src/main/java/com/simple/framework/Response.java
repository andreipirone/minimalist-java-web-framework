package com.simple.framework;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private BufferedWriter out;
    private Map<String, String> responseMap;
    private final String CRLF = "\r\n";
    private HttpStatus code ;
    private String contentType;
    private String body;

    public Response(BufferedWriter out) {
        this.out = out;
        this.responseMap = new HashMap<>();
    }

    public Response status(HttpStatus code) throws IOException {
        this.code = code;
        this.responseMap.put("Status", "HTTP/1.1 " + this.code.getDetails());
        return this;
    }

    public void build() throws IOException {
        if(this.body.isEmpty()){
            this.responseMap.put("Content-Type", "text/plain");
            this.responseMap.put("Content-Length", String.valueOf(this.code.getDetails().length()));
            this.responseMap.put("Body", this.code.getDetails());
        }

        StringBuilder response = new StringBuilder();
        response.append(this.responseMap.get("Status")).append(CRLF);
        for(String key : this.responseMap.keySet()){
            if(!key.equals("Body")){
                response.append(key)
                        .append(": ")
                        .append(this.responseMap.get(key))
                        .append(CRLF);
            }
        }
        response.append(CRLF).append(this.responseMap.get("Body"));

        out.write(response.toString());
    }
}
