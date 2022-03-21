package com.prox.docxreader;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prox.docxreader.modul.Document;
import com.prox.docxreader.repository.DocumentRepository;

import java.util.List;

public class DocumentViewModel extends AndroidViewModel {
    public static final int SORT_NAME = 1;
    public static final int SORT_TIME_CREATE = 2;
    public static final int SORT_TIME_ACCESS = 3;

    private final DocumentRepository repository;
    private LiveData<List<Document>> documents;

    public DocumentViewModel(@NonNull Application application) {
        super(application);
        repository = new DocumentRepository(application);
    }

    public void insert(Document document){
        Log.d("viewmodel", "insert: "+document.getPath());
        repository.insert(document);
    }

    public void updateIsExist(){
        repository.updateIsExist();
        Log.d("viewmodel", "updateIsExist");
    }

    public void update(Document document){
        repository.update(document);
        Log.d("viewmodel", "update: "+document.getPath());
    }

    public void delete(Document document){
        repository.delete(document);
        Log.d("viewmodel", "delete: "+document.getPath());
    }

    public void deleteNotExist(){
        repository.deleteNotExist();
        Log.d("viewmodel", "deleteNotExist");
    }

    public LiveData<List<Document>> getDocuments(boolean isFavorite, int typeSort, String search) {
        setDocuments(isFavorite, typeSort, search);
        return documents;
    }

    private void setDocuments(boolean isFavorite, int typeSort, String search) {
        documents = repository.getDocuments(isFavorite, typeSort, search);
    }
}
