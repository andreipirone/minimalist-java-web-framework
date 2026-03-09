package com.simple.framework;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpParser {
    private Map<String, String> requestMap;
    private Map<String,String[]> urlVariables = new HashMap<>();
    private Map<String, String> paramsMap = new HashMap<>();
    private String endpoint;

    public HttpParser(){
        this.requestMap = new HashMap<>();
    }

    public HttpParser(HttpParser other){
        this.requestMap = new HashMap<>(other.requestMap);
        this.urlVariables = new HashMap<>();
        this.paramsMap = new HashMap<>(other.paramsMap);
        this.endpoint = other.endpoint;

        for (Map.Entry<String, String[]> entry : other.urlVariables.entrySet()) {
            this.urlVariables.put(entry.getKey(), entry.getValue().clone());
        }
    }

    public Map<String, String> parseRequest(BufferedReader in) throws IOException {
        this.requestMap.clear();

        String line;
        line = in.readLine();
        String[] firstLine = line.split("\\s+");
        //System.out.println(line);

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
            //System.out.println(line);
        }

        if(this.requestMap.containsKey("Content-Length")){
            if(!this.requestMap.get("Content-Type").contains("multipart/form-data")) {
                int bodySize = Integer.parseInt(this.requestMap.get("Content-Length"));

                char[] body = new char[bodySize];
                in.read(body, 0, bodySize);
                //System.out.println(String.valueOf(body));
                this.requestMap.put("Body", String.valueOf(body));
//            } else {
//                JSONArray multipartJSON;
//                String boundary = this.requestMap.get("Content-Type").split(" ")[1].split("=")[1];
//                int readSize = 0;
//                int bodySize = Integer.parseInt(this.requestMap.get("Content-Length"));
//                while(readSize < bodySize) {
//                    Map<String, String> tempMap = new HashMap<>();
//                    //String boundary = in.readLine();
//
//                    while ((line = in.readLine()) != null && !line.isEmpty()) {
//                        String[] tempLine = line.split(":", 2);
//                        if(tempLine[0].equals("Content-Disposition")){
//                            String[] data = tempLine[1].trim().split("; ");
//                            for(int i = 1; i < data.length; i++){
//                                String[] pair = data[i].split("=");
//                                tempMap.put(pair[0], pair[1]);
//                            }
//                        } else if (tempLine[0].equals("Content-Type")) {
//                            tempMap.put(tempLine[0], tempLine[1]);
//                        }
//
//                        if(tempMap.get("Content-Type").contains("image/jpeg")){
//                            while ((line = in.readLine()) != null && !line.isEmpty()) {
//                                byte[] body = new byte[bodySize];
//
//                                int bytesRead = 0;
//                                  while (bytesRead < bodySize) {
//                                      int result = in.read(body, bytesRead, bodySize - bytesRead);
//                                      if (result == -1) break; // Stream ended early
//                                      bytesRead += result;
//                                  }
//                            }
//                        }
//                    }
//                }
            }
        }

        return this.requestMap;
    }

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


        //matchesList.forEach((i) -> System.out.println(i));
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
