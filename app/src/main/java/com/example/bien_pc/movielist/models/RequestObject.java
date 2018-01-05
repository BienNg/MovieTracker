package com.example.bien_pc.movielist.models;

/**
 * Created by Bien-PC on 05.01.2018.
 */

public class RequestObject {
    private String request;
    private String title;

    public RequestObject(String request, String title) {
        this.request = request;
        this.title = title;
    }
    public RequestObject(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public String getTitle() {
        return title;
    }
}
