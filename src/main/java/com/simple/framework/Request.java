package com.simple.framework;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    private Map<String, String> bodyMap;
    private Map<String, String> queryMap;
    private Map<String, String> requestMap;
    private Map<String, String> paramMap;

    public Request(Map<String, String> requestMap){
        this.requestMap = requestMap;
    }

//    public Request(Map<String, String> requestMap, )

    public Map<String, String> getBody(){
        String body = this.requestMap.get("Body");
        System.out.println(body);
        if(this.requestMap.get("Content-Type").contains("application/x-www-form-urlencoded")){
            this.bodyMap = urlencoded(body);
        } else if (this.requestMap.get("Content-Type").contains("application/json")) {
            this.bodyMap = this.json(body);
        }

        return this.bodyMap;
    }

    public Map<String, String> urlencoded(String body){
        Map<String, String> tempMap = new HashMap<>();
        String[] variables = body.split("&");

        for(String pair : variables){
            String[] value = pair.split("=");
            tempMap.put(value[0].trim(), value[1].trim());
        }

        return  tempMap;
    }

    public Map<String, String> json(String body){
        Map<String, String> tempMap = new HashMap<>();
        JSONObject tempJson = new JSONObject(body);
        for(String key : tempJson.keySet()) {
            tempMap.put(key, String.valueOf(tempJson.get(key)));
        }

        return tempMap;
    }

    public Map<String, String> getQuery(){
        try{
            String query = this.requestMap.get("Query");
            this.queryMap = new LinkedHashMap<>();
            String[] variables = query.split("&");

            for(String pair : variables){
                String[] value = pair.split("=");
                this.queryMap.put(value[0].trim(), value[1].trim());
            }
        } catch (NullPointerException e) {
            System.out.println("There's no query in the url");
        }

        return this.queryMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public Map<String, String> getRouteParams(){
        return this.paramMap;
    }


}
