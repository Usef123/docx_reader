package com.prox.docxreader.utils;

import static com.prox.docxreader.DocxReaderApp.TAG;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.prox.docxreader.R;
import com.prox.docxreader.modul.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileUtils {
//    private static Uri contentUri = null;

    public static ArrayList<Document> getDocuments(File file) {
        ArrayList<Document> documents = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null){
            for (File f : files){
                if (f.isDirectory()){
                    documents.addAll(getDocuments(f));
                }else {
                    if (f.getName().endsWith("doc")
                            || f.getName().endsWith("docx")){
                        Document document = new Document();
                        document.setPath(f.getPath());
                        document.setTitle(f.getName());
                        document.setTimeCreate(f.lastModified());
                        document.setTimeAccess(f.lastModified());
                        document.setFavorite(false);
                        document.setExist(true);
                        documents.add(document);
                    }
                }
            }
        }
        return documents;
    }

    public static String renameFile(Context context, String path, String name) {
        String type = getType(path);
        String oldName = getName(path);
        File oldFile = new File(path);

        String newName = name.trim();
        String newPath = getRoot(path) + newName + "." +type;
        File newFile = new File(newPath);

        //Tên để trống
        if (newName.isEmpty()) {
            Log.d(TAG, "renameFile: false");
            Toast.makeText(context, R.string.notification_rename_empty, Toast.LENGTH_SHORT).show();
            return null;
        } else if (newName.equals(oldName)) {
            Log.d(TAG, "renameFile: false");
            Toast.makeText(context, R.string.notification_not_rename, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (new File(path).exists()) {
            if (new File(newPath).exists()) {
                Log.d(TAG, "renameFile: false");
                Toast.makeText(context, R.string.notification_file_duplicate, Toast.LENGTH_SHORT).show();
                return null;
            }

            if (oldFile.renameTo(newFile)) {
                broadcastScanFile(context, oldFile.getPath());
                broadcastScanFile(context, newFile.getPath());
                Toast.makeText(context, R.string.notification_rename_success, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "renameFile: "+oldFile.getPath()+" --> "+newFile.getPath());
                return newFile.getPath();
            }
        }
        Log.d(TAG, "renameFile: false");
        Toast.makeText(context, R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
        return null;
    }

    public static boolean deleteFile(Context context, String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                broadcastScanFile(context, path);
                Log.d(TAG, "deleteFile: true");
                return true;
            }
        }
        Log.d(TAG, "deleteFile: false");
        Toast.makeText(context, R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
        return false;
    }

    public static void shareFile(Context context, String path) {
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(context, "com.prox.docxreader.fileprovider", file);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        String titleFull = getName(path)+"."+getType(path);

        intentShareFile.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(getType(path)));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intentShareFile, titleFull);

        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        context.startActivity(chooser);
    }

    public static String getRoot(String path){
        return path.substring(0, path.lastIndexOf("/") + 1);
    }

    public static String getName(String path){
        return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
    }

    public static String getType(String path){
        return path.substring(path.lastIndexOf('.')+1);
    }

    public static String getRealPath(Context context, Uri uri) {
        String path;
        if (uri == null) return null;

        // DocumentProvider

        try {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        if (split.length > 1) {
                            path = Environment.getExternalStorageDirectory().toString() + "/" + split[1];
                        } else {
                            path = Environment.getExternalStorageDirectory().toString() + "/";
                        }
                    } else {
                        File[] external = context.getExternalMediaDirs();
                        for (File f: external) {
                            String filePath = f.getAbsolutePath();

                            if (filePath.contains(type)) {
                                return filePath.substring(0, filePath.indexOf("Android")) + split[1];
                            }
                        }
                        return "storage" + "/" + docId.replace(":", "/");
                    }
                } else if (isMediaDocument(uri)) {
                    path = getDownloadsDocumentPath(context, uri, true);
                } else if (isRawDownloadsDocument(uri)) {
                    path = getDownloadsDocumentPath(context, uri, true);
                } else if (isDownloadsDocument(uri)) {
                    path = getDownloadsDocumentPath(context, uri, false);
                } else {
                    path = loadToCacheFile(context, uri);
                }
            } else {
                path = loadToCacheFile(context, uri);
            }
        } catch (Exception e) {
            return null;
        }

        return path;
    }

    private static String loadToCacheFile(Context context, Uri uri) {
        try {
            if (uri == null) return null;

            String pathFile = uri.getPath();
//            if (FileUtils.INSTANCE.checkFileExist(pathFile)) {
//                return pathFile;
//            }

            pathFile = getPathFile(context.getContentResolver(), uri);
            if (checkFileExist(pathFile)) {
                return pathFile;
            }

            String nameFile = getNameFile(context.getContentResolver(), uri);

            if (nameFile == null || nameFile.length() == 0) {
                return null;
            }

            String suffix = "";
            if (nameFile.contains(".")) {
                try {
                    suffix = nameFile.substring(nameFile.lastIndexOf("."));
                    nameFile = nameFile.substring(0, nameFile.lastIndexOf("."));
                } catch (Exception ignored) {

                }
            }

            nameFile = nameFile + "_";

            if(nameFile.length() < 4) {
                nameFile += NanoIdUtils.randomNanoId(new SecureRandom(),
                        "01234".toCharArray(), 4 - nameFile.length());
            }

            File rootDir = context.getFilesDir();
            File containTempFileDir = new File(rootDir, "Temp_folder_123123");
            if (containTempFileDir.exists()) {
                deleteRecursive(containTempFileDir);
            }

            if (!containTempFileDir.isDirectory() || !containTempFileDir.exists()) {
                try {
                    if (!containTempFileDir.mkdir()) {
                        return null;
                    }
                } catch (Exception ignored) {
                    return null;
                }
            }

            File newFile;

            newFile = File.createTempFile(nameFile, suffix, containTempFileDir);

            if (createFileFromStream(context, uri, newFile)) {
                return newFile.getAbsolutePath();
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    private static String getNameFile(final ContentResolver cr, final Uri uri) {
        try {
            @SuppressLint("Recycle")
            final Cursor c = cr.query(uri, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
                final int fileNameColumnId = c.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (fileNameColumnId >= 0) {
                    final String attachmentFileName = c.getString(fileNameColumnId);
                    return attachmentFileName == null || attachmentFileName.length() == 0 ? null : attachmentFileName;
                }
            }

        } catch (Exception ignored) {

        }
        return null;
    }

    private static String getPathFile(final ContentResolver cr, final Uri uri) {
        try {
            @SuppressLint("Recycle")
            final Cursor c = cr.query(uri, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
                final int fileNameColumnId = c.getColumnIndex(MediaStore.MediaColumns.DATA);
                if (fileNameColumnId >= 0) {
                    final String attachmentFileDir = c.getString(fileNameColumnId);
                    return attachmentFileDir == null || attachmentFileDir.length() == 0 ? null : attachmentFileDir;
                }
            }

        } catch (Exception ignored) {}
        return null;
    }

    public static boolean createFileFromStream(Context context, Uri sourceUri, File destination) {
        try (InputStream ins = context.getContentResolver().openInputStream(sourceUri)) {
            OutputStream os = new FileOutputStream(destination);
            byte[] buffer = new byte[4096];
            int length;

            if (ins != null) {
                while ((length = ins.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();

                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Get a file path from an Uri that points to the Downloads folder.
     *
     * @param context       The context
     * @param uri           The uri to query
     * @param hasSubFolders The flag that indicates if the file is in the root or in a subfolder
     * @return The absolute file path
     */
    private static String getDownloadsDocumentPath(Context context, Uri uri, boolean hasSubFolders) {
        String fileName = getFilePath(context, uri);
        String subFolderName = hasSubFolders ? getSubFolders(uri) : "";

        String filePath = "";

        if (fileName != null) {
            if (subFolderName != null)
                filePath = Environment.getExternalStorageDirectory().toString() +
                        "/Download/" + subFolderName + fileName;
            else
                filePath = Environment.getExternalStorageDirectory().toString() +
                        "/Download/" + fileName;
        }

        if (filePath.length() > 0 && checkFileExist(filePath)) {
            return filePath;
        }

        final String id = DocumentsContract.getDocumentId(uri);

        String path = null;
        if (!TextUtils.isEmpty(id)) {
            if (id.startsWith("raw:")) {
                return id.replaceFirst("raw:", "");
            }
            List<String> contentUriPrefixesToTry = Arrays.asList("content://downloads/public_downloads",
                    "content://downloads/my_downloads");

            for (String contentUriPrefix: contentUriPrefixesToTry) {
                try {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse(contentUriPrefix), Long.parseLong(id));
                    path = getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }

    /**
     * Get all the subfolders from an Uri.
     *
     * @param uri The uri
     * @return A string containing all the subfolders that point to the final file path
     */
    private static String getSubFolders(Uri uri) {
        String replaceChars = String.valueOf(uri).replace("%2F", "/")
                .replace("%20", " ").replace("%3A", ":");
        // searches for "Download" to get the directory path
        // for example, if the file is inside a folder "test" in the Download folder, this method
        // returns "test/"
        String[] components = replaceChars.split("/");
        String sub5 = "", sub4 = "", sub3 = "", sub2 = "", sub1 = "";

        if (components.length >= 2) {
            sub5 = components[components.length - 2];
        }
        if (components.length >= 3) {
            sub4 = components[components.length - 3];
        }
        if (components.length >= 4) {
            sub3 = components[components.length - 4];
        }
        if (components.length >= 5) {
            sub2 = components[components.length - 5];
        }
        if (components.length >= 6) {
            sub1 = components[components.length - 6];
        }
        if (sub1.equals("Download")) {
            return sub2 + "/" + sub3 + "/" + sub4 + "/" + sub5 + "/";
        } else if (sub2.equals("Download")) {
            return sub3 + "/" + sub4 + "/" + sub5 + "/";
        } else if (sub3.equals("Download")) {
            return sub4 + "/" + sub5 + "/";
        } else if (sub4.equals("Download")) {
            return sub5 + "/";
        } else {
            return null;
        }
    }

    /**
     * Get the file path (without subfolders if any)
     *
     * @param context The context
     * @param uri     The uri to query
     * @return The file path
     */
    private static String getFilePath(Context context, Uri uri) {
        final String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };
        String path = null;
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                path = cursor.getString(index);
            }
        } catch (Exception e) {
            Log.e("Error", " " + e.getMessage());
        }
        return path;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * This function is used to check for a drive file URI.
     *
     * @param uri - input uri
     * @return true, if is google drive uri, otherwise false
     */
    public boolean isDriveFile(Uri uri) {
        if ("com.google.android.apps.docs.storage".equals(uri.getAuthority()))
            return true;
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check
     * @return True if is a raw downloads document, otherwise false
     */
    private static boolean isRawDownloadsDocument(Uri uri) {
        String uriToString = String.valueOf(uri);
        return uriToString.contains("com.android.providers.downloads.documents/document/raw");
    }

    private static boolean isMediaDocument(Uri uri) {
        String uriToString = String.valueOf(uri);
        return uriToString.contains("com.android.providers.media.documents");
    }

    public boolean isWhatsAppFile(Uri uri){
        return "com.whatsapp.provider.media".equals(uri.getAuthority());
    }

    private static boolean checkFileExist(String path){
        if(path == null){
            return false;
        }
        File file = new File(path);

        return file.exists() && file.length() > 0;
    }

    private static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory()) {
                for (File f : Objects.requireNonNull(fileOrDirectory.listFiles())) {
                    deleteRecursive(f);
                }
            }
            fileOrDirectory.delete();
        } catch (Exception e) {

        }
    }



//    @SuppressLint("NewApi")
//    public static String getPath( final Uri uri, Context context) {
//        // check here to KITKAT or new version
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//        String selection = null;
//        String[] selectionArgs = null;
//        // DocumentProvider
//        if (isKitKat ) {
//            // ExternalStorageProvider
//
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                String fullPath = getPathFromExtSD(split);
//                if (fullPath != "") {
//                    return fullPath;
//                } else {
//                    return null;
//                }
//            }
//
//
//            // DownloadsProvider
//
//            if (isDownloadsDocument(uri)) {
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    final String id;
//                    Cursor cursor = null;
//                    try {
//                        cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
//                        if (cursor != null && cursor.moveToFirst()) {
//                            String fileName = cursor.getString(0);
//                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
//                            if (!TextUtils.isEmpty(path)) {
//                                return path;
//                            }
//                        }
//                    }
//                    finally {
//                        if (cursor != null)
//                            cursor.close();
//                    }
//                    id = DocumentsContract.getDocumentId(uri);
//                    if (!TextUtils.isEmpty(id)) {
//                        if (id.startsWith("raw:")) {
//                            return id.replaceFirst("raw:", "");
//                        }
//                        String[] contentUriPrefixesToTry = new String[]{
//                                "content://downloads/public_downloads",
//                                "content://downloads/my_downloads"
//                        };
//                        for (String contentUriPrefix : contentUriPrefixesToTry) {
//                            try {
//                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
//
//
//                                return getDataColumn(context, contentUri, null, null);
//                            } catch (NumberFormatException e) {
//                                //In Android 8 and Android P the id is not a number
//                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
//                            }
//                        }
//
//
//                    }
//                }
//                else {
//                    final String id = DocumentsContract.getDocumentId(uri);
//
//                    if (id.startsWith("raw:")) {
//                        return id.replaceFirst("raw:", "");
//                    }
//                    try {
//                        contentUri = ContentUris.withAppendedId(
//                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                    }
//                    catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//                    if (contentUri != null) {
//
//                        return getDataColumn(context, contentUri, null, null);
//                    }
//                }
//            }
//
//
//            // MediaProvider
//            if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                Uri contentUri = null;
//
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                selection = "_id=?";
//                selectionArgs = new String[]{split[1]};
//
//
//                return getDataColumn(context, contentUri, selection,
//                        selectionArgs);
//            }
//
//            if (isGoogleDriveUri(uri)) {
//                return getDriveFilePath(uri, context);
//            }
//
//            if(isWhatsAppFile(uri)){
//                return getFilePathForWhatsApp(uri, context);
//            }
//
//
//            if ("content".equalsIgnoreCase(uri.getScheme())) {
//
//                if (isGooglePhotosUri(uri)) {
//                    return uri.getLastPathSegment();
//                }
//                if (isGoogleDriveUri(uri)) {
//                    return getDriveFilePath(uri, context);
//                }
//                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//                {
//
//                    // return getFilePathFromURI(context,uri);
//                    return copyFileToInternalStorage(uri,"userfiles", context);
//                    // return getRealPathFromURI(context,uri);
//                }
//                else
//                {
//                    return getDataColumn(context, uri, null, null);
//                }
//
//            }
//            if ("file".equalsIgnoreCase(uri.getScheme())) {
//                return uri.getPath();
//            }
//        }
//        else {
//
//            if(isWhatsAppFile(uri)){
//                return getFilePathForWhatsApp(uri, context);
//            }
//
//            if ("content".equalsIgnoreCase(uri.getScheme())) {
//                String[] projection = {
//                        MediaStore.Images.Media.DATA
//                };
//                Cursor cursor = null;
//                try {
//                    cursor = context.getContentResolver()
//                            .query(uri, projection, selection, selectionArgs, null);
//                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    if (cursor.moveToFirst()) {
//                        return cursor.getString(column_index);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//
//
//        return null;
//    }
//
//    private static boolean fileExists(String filePath) {
//        File file = new File(filePath);
//
//        return file.exists();
//    }
//
//    private static String getPathFromExtSD(String[] pathData) {
//        final String type = pathData[0];
//        final String relativePath = "/" + pathData[1];
//        String fullPath = "";
//
//        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
//        // something like "71F8-2C0A", some kind of unique id per storage
//        // don't know any API that can get the root path of that storage based on its id.
//        //
//        // so no "primary" type, but let the check here for other devices
//        if ("primary".equalsIgnoreCase(type)) {
//            fullPath = Environment.getExternalStorageDirectory() + relativePath;
//            if (fileExists(fullPath)) {
//                return fullPath;
//            }
//        }
//
//        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
//        // so we cannot relay on it.
//        //
//        // instead, for each possible path, check if file exists
//        // we'll start with secondary storage as this could be our (physically) removable sd card
//        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
//        if (fileExists(fullPath)) {
//            return fullPath;
//        }
//
//        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
//        if (fileExists(fullPath)) {
//            return fullPath;
//        }
//
//        return fullPath;
//    }
//
//    private static String getDriveFilePath(Uri uri, Context context) {
//        Uri returnUri = uri;
//        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
//        /*
//         * Get the column indexes of the data in the Cursor,
//         *     * move to the first row in the Cursor, get the data,
//         *     * and display it.
//         * */
//        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//        returnCursor.moveToFirst();
//        String name = (returnCursor.getString(nameIndex));
//        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
//        File file = new File(context.getCacheDir(), name);
//        try {
//            InputStream inputStream = context.getContentResolver().openInputStream(uri);
//            FileOutputStream outputStream = new FileOutputStream(file);
//            int read = 0;
//            int maxBufferSize = 1 * 1024 * 1024;
//            int bytesAvailable = inputStream.available();
//
//            //int bufferSize = 1024;
//            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//            final byte[] buffers = new byte[bufferSize];
//            while ((read = inputStream.read(buffers)) != -1) {
//                outputStream.write(buffers, 0, read);
//            }
//            Log.e("File Size", "Size " + file.length());
//            inputStream.close();
//            outputStream.close();
//            Log.e("File Path", "Path " + file.getPath());
//            Log.e("File Size", "Size " + file.length());
//        } catch (Exception e) {
//            Log.e("Exception", e.getMessage());
//        }
//        return file.getPath();
//    }
//
//    /***
//     * Used for Android Q+
//     * @param uri
//     * @param newDirName if you want to create a directory, you can set this variable
//     * @return
//     */
//    private static String copyFileToInternalStorage(Uri uri, String newDirName, Context context) {
//        Uri returnUri = uri;
//
//        Cursor returnCursor = context.getContentResolver().query(returnUri, new String[]{
//                OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
//        }, null, null, null);
//
//
//        /*
//         * Get the column indexes of the data in the Cursor,
//         *     * move to the first row in the Cursor, get the data,
//         *     * and display it.
//         * */
//        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//        returnCursor.moveToFirst();
//        String name = (returnCursor.getString(nameIndex));
//        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
//
//        File output;
//        if(!newDirName.equals("")) {
//            File dir = new File(context.getFilesDir() + "/" + newDirName);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            output = new File(context.getFilesDir() + "/" + newDirName + "/" + name);
//        }
//        else{
//            output = new File(context.getFilesDir() + "/" + name);
//        }
//        try {
//            InputStream inputStream = context.getContentResolver().openInputStream(uri);
//            FileOutputStream outputStream = new FileOutputStream(output);
//            int read = 0;
//            int bufferSize = 1024;
//            final byte[] buffers = new byte[bufferSize];
//            while ((read = inputStream.read(buffers)) != -1) {
//                outputStream.write(buffers, 0, read);
//            }
//
//            inputStream.close();
//            outputStream.close();
//
//        }
//        catch (Exception e) {
//
//            Log.e("Exception", e.getMessage());
//        }
//
//        return output.getPath();
//    }
//
//    private static String getFilePathForWhatsApp(Uri uri, Context context){
//        return  copyFileToInternalStorage(uri,"whatsapp", context);
//    }
//
//    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//
//        try {
//            cursor = context.getContentResolver().query(uri, projection,
//                    selection, selectionArgs, null);
//
//            if (cursor != null && cursor.moveToFirst()) {
//                final int index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(index);
//            }
//        }catch(Exception e){
//            return getDriveFilePath(uri, context);
//        }
//        finally {
//            if (cursor != null)
//                cursor.close();
//        }
//
//        return null;
//    }
//
//    private static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    private static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    private static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    private static boolean isGooglePhotosUri(Uri uri) {
//        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
//    }
//
//    public static boolean isWhatsAppFile(Uri uri){
//        return "com.whatsapp.provider.media".equals(uri.getAuthority());
//    }
//
//    private static boolean isGoogleDriveUri(Uri uri) {
//        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
//    }
//
    @SuppressLint("IntentReset")
    private static void broadcastScanFile(Context context, String path) {
        Intent intentNotify = new Intent();
        String type = getType(path);
        intentNotify.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type));
        intentNotify.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentNotify.setData(Uri.fromFile(new File(path)));
        context.sendBroadcast(intentNotify);
    }
}
