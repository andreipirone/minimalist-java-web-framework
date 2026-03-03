package com.simple.framework;

public enum HttpStatus {
    HTTP_200("200 OK"), HTTP_404("404 Not Found"), HTTP_500("500 Internal Server Error");

    private String message;
    HttpStatus(String message){
        this.message = message;
    }

    public String getDetails(){
        return this.message;
    }
}
