package com.prox.docxreader.modul;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "document")
public class Document implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String path;
    private String title;
    private int timeCreate;
    private int timeAccess;
    private boolean isFavorite;

    public Document() {
    }

    public Document(String path, String title, int timeCreate, int timeAccess, boolean isFavorite) {
        this.path = path;
        this.title = title;
        this.timeCreate = timeCreate;
        this.timeAccess = timeAccess;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(int timeCreate) {
        this.timeCreate = timeCreate;
    }

    public int getTimeAccess() {
        return timeAccess;
    }

    public void setTimeAccess(int timeAccess) {
        this.timeAccess = timeAccess;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
