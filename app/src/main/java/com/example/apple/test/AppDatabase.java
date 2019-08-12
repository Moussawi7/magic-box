package com.example.apple.test;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.apple.test.ItemDAO;
import com.example.apple.test.models.Item;

@Database(entities = {Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ItemDAO getItemDAO();
}
