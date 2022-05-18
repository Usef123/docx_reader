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
    void insert(Document document);

    @Query("SELECT * FROM document WHERE path= :path")
    Document check(String path);

    @Update
    void update(Document document);

    @Query("UPDATE document SET isExist = 0")
    void updateIsExist();

    @Delete
    void delete(Document document);

    @Query("DELETE FROM document WHERE isExist = 0")
    void deleteNotExist();

    @Query("SELECT * FROM document WHERE title LIKE '%doc' OR title LIKE '%dot' OR title LIKE '%docx' OR title LIKE '%dotx'")
    List<Document> getDOCX();

    @Query("SELECT * FROM document WHERE (title LIKE '%doc' OR title LIKE '%dot' OR title LIKE '%docx' OR title LIKE '%dotx') AND isFavorite = 1")
    List<Document> getDOCXFavorite();

    @Query("SELECT * FROM document WHERE title LIKE '%xls' OR title LIKE '%xlsx' OR title LIKE '%xltm' OR title LIKE '%xltx' OR title LIKE '%csv'")
    List<Document> getXLSX();

    @Query("SELECT * FROM document WHERE title LIKE '%pdf'")
    List<Document> getPDF();

    @Query("SELECT * FROM document WHERE title LIKE '%ppt' OR title LIKE '%pttx'")
    List<Document> getPPTX();
}
