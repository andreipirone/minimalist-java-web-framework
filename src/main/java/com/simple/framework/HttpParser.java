package com.simple.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParser {
    private Map<String, String> requestMap = new HashMap<>();
    private String request;

    public HttpParser(){}

    public Map<String, String> parseRequest(String request){
        String[] requestLines = request.split("\r\n");
        String[] firstLine = requestLines[0].split("\\s+");

        this.requestMap.put("Method", firstLine[0]);
        this.requestMap.put("URL", firstLine[1]);
        this.requestMap.put("Protocol Version", firstLine[2]);

        for(int i = 1; i < requestLines.length; i++){
            String[] tempLine = requestLines[i].split(":", 2);
            this.requestMap.put(tempLine[0], tempLine[1]);
        }

        return this.requestMap;
    }

}
