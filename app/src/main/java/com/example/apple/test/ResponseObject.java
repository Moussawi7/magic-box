package com.example.apple.test;

class ResponseObject{
    private final String id;
    private final String title;
    private final String createdOn;
    private int rating;

    public ResponseObject(String id, String title,String createdOn, int rating){
        this.id = id;
        this.title = title;
        this.createdOn = createdOn;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

}