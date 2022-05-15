package com.prox.docxreader.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.prox.docxreader.database.DocumentDAO;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.util.List;

public class DocumentRepository {
    public static final int SORT_NAME = 1;
    public static final int SORT_TIME_CREATE = 2;
    public static final int SORT_TIME_ACCESS = 3;

    private final DocumentDAO documentDAO;
    private LiveData<List<Document>> documents;

    public DocumentRepository(Application application) {
        DocumentDatabase database = DocumentDatabase.getInstance(application);
        documentDAO = database.documentDAO();
    }

    public LiveData<List<Document>> getDocuments(boolean isFavorite, int typeSort, String search) {
        setDocuments(isFavorite, typeSort, search);
        return documents;
    }

    public void setDocuments(boolean isFavorite, int typeSort, String search) {
        switch (typeSort){
            case SORT_NAME:
                documents = documentDAO.getDocumentByName(isFavorite, search);
                break;
            case SORT_TIME_CREATE:
                documents = documentDAO.getDocumentByTimeCreate(isFavorite, search);
                break;
            case SORT_TIME_ACCESS:
                documents = documentDAO.getDocumentByTimeAccess(isFavorite, search);
                break;
        }
    }

    public Document check(String path){
        return documentDAO.check(path);
    }

    public void insert(Document document){
        documentDAO.insert(document);
    }

    public void update(Document document){
        documentDAO.update(document);
    }

    public void updateIsExist(){
        documentDAO.updateIsExist();
    }

    public void delete(Document document){
        documentDAO.delete(document);
    }

    public void deleteNotExist(){
        documentDAO.deleteNotExist();
    }
}
