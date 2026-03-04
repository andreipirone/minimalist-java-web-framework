package com.simple.framework;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> bodyMap;
    private Map<String, String> requestMap;

    public Request(Map<String, String> requestMap){
        this.requestMap = requestMap;
    }

    public Map<String, String> getBody(){
        String body = this.requestMap.get("Body");
        this.bodyMap = new HashMap<>();
        System.out.println(body);
        if(this.requestMap.get("Content-Type").equals("application/x-www-form-urlencoded")){
            String[] variables = body.split("&");

            for(String pair : variables){
                String[] value = pair.split("=");
                this.bodyMap.put(value[0].trim(), value[1].trim());
            }
        }

        return this.bodyMap;
    }

    public void getParams(){

    }
}
