package com.prox.docxreader.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.prox.docxreader.modul.Document;

@Database(entities = {Document.class}, version = 1)
public abstract class DocumentDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "document.db";
    private static DocumentDatabase instance;

    public static synchronized DocumentDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(), DocumentDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract DocumentDAO documentDAO();
}
