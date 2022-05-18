package com.prox.docxreader.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.prox.docxreader.database.DocumentDAO;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.util.List;

public class DocumentRepository {
    private final DocumentDAO documentDAO;

    public DocumentRepository(Application application) {
        DocumentDatabase database = DocumentDatabase.getInstance(application);
        documentDAO = database.documentDAO();
    }

    public MutableLiveData<List<Document>> getDOCX() {
        MutableLiveData<List<Document>> data = new MutableLiveData<>();
        data.setValue(documentDAO.getDOCX());
        return data;
    }

    public MutableLiveData<List<Document>> getDOCXFavorite() {
        MutableLiveData<List<Document>> data = new MutableLiveData<>();
        data.setValue(documentDAO.getDOCXFavorite());
        return data;
    }

    public MutableLiveData<List<Document>> getXLSX() {
        MutableLiveData<List<Document>> data = new MutableLiveData<>();
        data.setValue(documentDAO.getXLSX());
        return data;
    }

    public MutableLiveData<List<Document>> getPDF() {
        MutableLiveData<List<Document>> data = new MutableLiveData<>();
        data.setValue(documentDAO.getPDF());
        return data;
    }

    public MutableLiveData<List<Document>> getPPTX() {
        MutableLiveData<List<Document>> data = new MutableLiveData<>();
        data.setValue(documentDAO.getPPTX());
        return data;
    }

    public Document check(String path) {
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
