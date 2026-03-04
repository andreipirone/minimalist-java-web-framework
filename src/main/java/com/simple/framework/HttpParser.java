package com.simple.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {
    private Map<String, String> requestMap = new HashMap<>();
    private String request;

    public HttpParser(){}

//    public Map<String, String> parseRequest(String request){
//        String[] requestLines = request.split("\r\n");
//        String[] firstLine = requestLines[0].split("\\s+");
//
//        this.requestMap.put("Method", firstLine[0]);
//        this.requestMap.put("URL", firstLine[1]);
//        this.requestMap.put("Protocol Version", firstLine[2]);
//
//        for(int i = 1; i < requestLines.length; i++){
//            String[] tempLine = requestLines[i].split(":", 2);
//            this.requestMap.put(tempLine[0], tempLine[1]);
//        }
//
//        return this.requestMap;
//    }

    public Map<String, String> parseRequest(BufferedReader in) throws IOException {
        String line;
        line = in.readLine();
        String[] firstLine = line.split("\\s+");

        this.requestMap.put("Method", firstLine[0]);
        this.requestMap.put("URL", firstLine[1]);
        this.requestMap.put("Protocol Version", firstLine[2]);

        while((line = in.readLine()) != null && !line.isEmpty()){
            String[] tempLine = line.split(":", 2);
            this.requestMap.put(tempLine[0].trim(), tempLine[1].trim());
        }

        if(this.requestMap.containsKey("Content-Length")){
            int bodySize = Integer.parseInt(this.requestMap.get("Content-Length"));
            int tempSize = 0;

            char[] body = new char[bodySize];
            in.read(body, 0, bodySize);
            this.requestMap.put("Body", String.valueOf(body));
        }

        return this.requestMap;
    }

}
