package com.simple.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
    private Map<String, String> requestMap;
    private Map<String, String> urlMap;
    private Map<String,String[]> urlVariables = new HashMap<>();
    private Map<String, String[]> urlValues = new HashMap<>();
    private Map<String, String> paramsMap = new HashMap<>();
    private String endpoint;


    private String request;

    public HttpParser(){
        this.requestMap = new HashMap<>();
    }


    public Map<String, String> parseRequest(BufferedReader in) throws IOException {
        this.requestMap.clear();

        String line;
        line = in.readLine();
        String[] firstLine = line.split("\\s+");

        this.requestMap.put("Method", firstLine[0]);
        String url = firstLine[1];

        if(url.contains("?")){
            String[] splitUrl = url.split("\\?");
            this.requestMap.put("Query", splitUrl[1].trim());
            this.requestMap.put("URL", splitUrl[0]);
        } else {
            this.requestMap.put("URL", url);
        }
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


//    public void parseParams(String url){
//        String[] paths = url.split("/");
//        for(path)
//    }

    public String extractParams(String urlTemplate, boolean isInit){
        boolean isNonStatic = false;
        urlTemplate = urlTemplate.trim();
        List<String> matchesList = new ArrayList<>();
        Matcher paramMatcher = Pattern.compile("\\{([^}]+)\\}").matcher(urlTemplate);


        if(isInit){
            while (paramMatcher.find()) {
                isNonStatic = true;
                matchesList.add(paramMatcher.group(1));
            }
        } else {
            isNonStatic = paramMatcher.find();
        }


        matchesList.forEach((i) -> System.out.println(i));
        String regexPath = urlTemplate.replaceAll("\\{[^}]+\\}", "([^/]+)");

        if(isNonStatic){
            regexPath = "^" + regexPath + "$";
            urlVariables.put(regexPath, matchesList.toArray(new String[0]));
        } else {
            regexPath = "^" + regexPath + "/?$";
        }

        //System.out.println(regexPath);

        return regexPath;
    }

    public String getMatched() {
        return this.endpoint;
    }

    public Map<String, String> getParamsMap() {
        return this.paramsMap;
    }

    public void extractValues(String path, Matcher matcher){
        paramsMap = new HashMap<>();
        List<String> extractedValues = new ArrayList<>();
        this.endpoint = path;
        if (this.endpoint.contains("([^/]+)")) {
            for (int i = 0; i < this.urlVariables.get(endpoint).length; i++) {
                extractedValues.add(matcher.group(i+1));
            }

            String[] variableNames = this.urlVariables.get(path);
            for(int i = 0; i < variableNames.length; i++){
                paramsMap.put(variableNames[i], extractedValues.get(i));
            }
        }
    }

    public boolean hasMatch(String url, Map<String, Handler> endpoints) {
        this.endpoint = "";
        boolean found = false;
        for (String path : endpoints.keySet()) {
            Pattern pattern = Pattern.compile(path);
            Matcher matcher = pattern.matcher(url);
            found = matcher.matches();
            if (found) {
                this.extractValues(path, matcher);
                break;
            }
        }
        return found;
    }

}
