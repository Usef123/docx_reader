package com.prox.docxreader.modul;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "document")
public class Document{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String path;
    private String title;
    private long timeCreate;
    private long timeAccess;
    private boolean isFavorite;
    private boolean isExist;

    public Document() {
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

    public long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    public long getTimeAccess() {
        return timeAccess;
    }

    public void setTimeAccess(long timeAccess) {
        this.timeAccess = timeAccess;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }
}
