package com.prox.docxreader.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.prox.docxreader.modul.Document;

import java.util.List;

@Dao
public interface DocumentDAO {

    @Insert
    void insert(Document document);

    @Query("SELECT * FROM document WHERE path= :path")
    List<Document> check(String path);

    @Update
    void update(Document document);

    @Query("UPDATE document SET isExist = 0")
    void updateIsExist();

    @Delete
    void delete(Document document);

    @Query("DELETE FROM document WHERE isExist = 0")
    void deleteNotExist();

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' AND (isFavorite = :isFavorite OR isFavorite = 1) ORDER BY title ASC")
    LiveData<List<Document>> getDocumentByName(boolean isFavorite, String title);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' AND (isFavorite = :isFavorite OR isFavorite = 1) ORDER BY timeCreate DESC")
    LiveData<List<Document>> getDocumentByTimeCreate(boolean isFavorite, String title);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' AND (isFavorite = :isFavorite OR isFavorite = 1) ORDER BY timeAccess DESC")
    LiveData<List<Document>> getDocumentByTimeAccess(boolean isFavorite, String title);
}
