package com.prox.docxreader.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentManagerService extends Service {
    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public DocumentManagerService getDocumentManagerService(){
            return DocumentManagerService.this;
        }
    }

    public DocumentManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public ArrayList<File> findDocument(File file){
        ArrayList<File> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File singleFile : files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                list.addAll(findDocument(singleFile));
            }else if (singleFile.getName().endsWith("docx")){
                list.add(singleFile);
            }
        }
        return list;
    }

    public void insertDatabase() {
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] mimes = new String[]{
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx")
        };

        final String[] columns = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,   //tên file
                MediaStore.Files.FileColumns.DATE_ADDED,     //date tạo
                MediaStore.Files.FileColumns.MIME_TYPE,      //kiểu: docx
                MediaStore.Files.FileColumns.DATA};          //path file

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        Cursor cursor = contentResolver.query(uri, columns, selectionMimeType, mimes, null);
        Log.d("myservice", "number: "+cursor.getCount());


        if (cursor != null) {
            int title = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
            int date_add = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
            int path = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

            while (cursor.moveToNext()) {
                String str_title = cursor.getString(title);
                String str_path = cursor.getString(path);
                String str_date_add = cursor.getString(date_add);

                Document document = new Document();
                document.setPath(str_path);
                document.setTitle(str_title);
                document.setTimeCreate(Integer.parseInt(str_date_add));
                document.setTimeAccess(Integer.parseInt(str_date_add));
                document.setFavorite(false);

                if (!isDocumentExist(document)){
                    DocumentDatabase.getInstance(this).documentDAO().insertDocument(document);
                    Log.d("myservice", "insert");
                }
            }
            cursor.close();
        }
    }

    private boolean isDocumentExist(Document document) {
        List<Document> documents = DocumentDatabase.getInstance(this).documentDAO().checkDocument(document.getPath());
        return documents != null && !documents.isEmpty();
    }

    public void updateDatabase(){
        Log.d("myservice", "update");
        for (Document document: DocumentDatabase.getInstance(this).documentDAO().getDocuments()){
            if (!(new File(document.getPath()).exists())){
                Log.d("service", "delete");
                DocumentDatabase.getInstance(this).documentDAO().deleteDocument(document);
            }
        }
    }
}