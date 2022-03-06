package com.prox.docxreader.database;

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
    void insertDocument(Document document);

    @Query("SELECT * FROM document")
    List<Document> getDocuments();

    @Query("SELECT * FROM document WHERE path= :path")
    List<Document> checkDocument(String path);

    @Update
    void updateDocument(Document document);

    @Delete
    void deleteDocument(Document document);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' ORDER BY title ASC")
    List<Document> sortDocumentByName(String title);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' ORDER BY timeCreate DESC")
    List<Document> sortDocumentByTimeCreate(String title);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' ORDER BY timeAccess DESC")
    List<Document> sortDocumentByTimeAccess(String title);


    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' AND isFavorite = 1 ORDER BY title ASC")
    List<Document> sortDocumentFavoriteByName(String title);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' AND isFavorite = 1 ORDER BY timeCreate DESC")
    List<Document> sortDocumentFavoriteByTimeCreate(String title);

    @Query("SELECT * FROM document WHERE title LIKE '%' || :title || '%' AND isFavorite = 1 ORDER BY timeAccess DESC")
    List<Document> sortDocumentFavoriteByTimeAccess(String title);
}
