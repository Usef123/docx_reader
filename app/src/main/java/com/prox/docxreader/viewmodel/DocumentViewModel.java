package com.prox.docxreader.viewmodel;

import static com.prox.docxreader.DocxReaderApp.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prox.docxreader.modul.Document;
import com.prox.docxreader.repository.DocumentRepository;

import java.util.List;

public class DocumentViewModel extends AndroidViewModel {
    private final DocumentRepository repository;

    private final MutableLiveData<List<Document>> docx;
    private final MutableLiveData<List<Document>> docxFavorite;
    private final MutableLiveData<List<Document>> xlsx;
    private final MutableLiveData<List<Document>> pdf;
    private final MutableLiveData<List<Document>> pptx;

    private final MutableLiveData<Boolean> insertDB = new MutableLiveData<>();

    public DocumentViewModel(@NonNull Application application) {
        super(application);
        repository = new DocumentRepository(application);

        docx = repository.getDOCX();
        docxFavorite = repository.getDOCXFavorite();
        xlsx = repository.getXLSX();
        pdf = repository.getPDF();
        pptx = repository.getPPTX();
    }

    public Document check(String path){
        return repository.check(path);
    }

    public void insertBG(Document document){
        Log.d(TAG, "DocumentViewModel insert "+document.getPath());
        repository.insert(document);
    }

    public void updateIsExistBG(){
        Log.d(TAG, "DocumentViewModel updateIsExist");
        repository.updateIsExist();
    }

    public void update(Document document){
        Log.d(TAG, "DocumentViewModel update "+document.getPath());
        repository.update(document);
        setValue();
    }

    public void updateBG(Document document){
        Log.d(TAG, "DocumentViewModel update "+document.getPath());
        repository.update(document);
    }

    public void delete(Document document){
        Log.d(TAG, "DocumentViewModel delete "+document.getPath());
        repository.delete(document);
        setValue();
    }

    public void deleteNotExistBG(){
        repository.deleteNotExist();
    }

    public LiveData<List<Document>> getDOCX() {
        return docx;
    }

    public LiveData<List<Document>> getDOCXFavorite() {
        return docxFavorite;
    }

    public LiveData<List<Document>> getXLSX() {
        return xlsx;
    }

    public LiveData<List<Document>> getPDF() {
        return pdf;
    }

    public LiveData<List<Document>> getPPTX() {
        return pptx;
    }

    public void setValue(){
        docx.setValue(repository.getDOCX().getValue());
        docxFavorite.setValue(repository.getDOCXFavorite().getValue());
        xlsx.setValue(repository.getXLSX().getValue());
        pdf.setValue(repository.getPDF().getValue());
        pptx.setValue(repository.getPPTX().getValue());
    }

    public MutableLiveData<Boolean> getInsertDB() {
        return insertDB;
    }

    public void setInsertDB(Boolean b){
        insertDB.postValue(b);
    }
}
