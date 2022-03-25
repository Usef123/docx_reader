package com.prox.docxreader.repository;

import static com.prox.docxreader.viewmodel.DocumentViewModel.SORT_NAME;
import static com.prox.docxreader.viewmodel.DocumentViewModel.SORT_TIME_ACCESS;
import static com.prox.docxreader.viewmodel.DocumentViewModel.SORT_TIME_CREATE;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.prox.docxreader.database.DocumentDAO;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.util.List;

public class DocumentRepository {
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

    private void setDocuments(boolean isFavorite, int typeSort, String search) {
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

    public void insert(Document document){
        new InsertDocumentAsyncTask(documentDAO).execute(document);
    }

    public void update(Document document){
        new UpdateDocumentAsyncTask(documentDAO).execute(document);
    }

    public void updateIsExist(){
        new UpdateIsExistDocumentAsyncTask(documentDAO).execute();
    }

    public void delete(Document document){
        new DeleteDocumentAsyncTask(documentDAO).execute(document);
    }

    public void deleteNotExist(){
        new DeleteNotExistDocumentAsyncTask(documentDAO).execute();
    }

    private static class InsertDocumentAsyncTask extends AsyncTask<Document, Void, Void>{
        private final DocumentDAO documentDAO;

        private InsertDocumentAsyncTask(DocumentDAO documentDAO) {
            this.documentDAO = documentDAO;
        }

        @Override
        protected Void doInBackground(Document... documents) {
            documentDAO.insert(documents[0]);
            return null;
        }
    }

    private static class UpdateDocumentAsyncTask extends AsyncTask<Document, Void, Void>{
        private final DocumentDAO documentDAO;

        private UpdateDocumentAsyncTask(DocumentDAO documentDAO) {
            this.documentDAO = documentDAO;
        }

        @Override
        protected Void doInBackground(Document... documents) {
            documentDAO.update(documents[0]);
            return null;
        }
    }

    private static class UpdateIsExistDocumentAsyncTask extends AsyncTask<Void, Void, Void>{
        private final DocumentDAO documentDAO;

        private UpdateIsExistDocumentAsyncTask(DocumentDAO documentDAO) {
            this.documentDAO = documentDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            documentDAO.updateIsExist();
            return null;
        }
    }

    private static class DeleteDocumentAsyncTask extends AsyncTask<Document, Void, Void>{
        private final DocumentDAO documentDAO;

        private DeleteDocumentAsyncTask(DocumentDAO documentDAO) {
            this.documentDAO = documentDAO;
        }

        @Override
        protected Void doInBackground(Document... documents) {
            documentDAO.delete(documents[0]);
            return null;
        }
    }

    private static class DeleteNotExistDocumentAsyncTask extends AsyncTask<Void, Void, Void>{
        private final DocumentDAO documentDAO;

        private DeleteNotExistDocumentAsyncTask(DocumentDAO documentDAO) {
            this.documentDAO = documentDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            documentDAO.deleteNotExist();
            return null;
        }
    }
}
