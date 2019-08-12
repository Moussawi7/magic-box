package com.example.apple.test.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey
    @NonNull  private String id;
    private int rating;

    public void setId(String id){
        this.id=id;
    }

    public void setRating(int rating){
        this.rating=rating;
    }

    public String getId(){
        return id;
    }
    public int getRating(){
        return rating;
    }
}
