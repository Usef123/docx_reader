package com.prox.docxreader.viewmodel;

import static com.prox.docxreader.DocxReaderApp.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prox.docxreader.modul.Document;
import com.prox.docxreader.repository.DocumentRepository;

import java.util.List;

public class DocumentViewModel extends AndroidViewModel {
    private final DocumentRepository repository;
    private LiveData<List<Document>> documents;

    public DocumentViewModel(@NonNull Application application) {
        super(application);
        repository = new DocumentRepository(application);
    }

    public Document check(String path){
        return repository.check(path);
    }

    public void insert(Document document){
        Log.d(TAG, "DocumentViewModel insert "+document.getPath());
        repository.insert(document);
    }

    public void updateIsExist(){
        Log.d(TAG, "DocumentViewModel updateIsExist");
        repository.updateIsExist();
    }

    public void update(Document document){
        Log.d(TAG, "DocumentViewModel update "+document.getPath());
        repository.update(document);
    }

    public void delete(Document document){
        Log.d(TAG, "DocumentViewModel delete "+document.getPath());
        repository.delete(document);
    }

    public void deleteNotExist(){
        Log.d(TAG, "DocumentViewModel deleteNotExist");
        repository.deleteNotExist();
    }

    public LiveData<List<Document>> getDocuments(boolean isFavorite, int typeSort, String search) {
        setDocuments(isFavorite, typeSort, search);
        return documents;
    }

    private void setDocuments(boolean isFavorite, int typeSort, String search) {
        documents = repository.getDocuments(isFavorite, typeSort, search);
    }
}
