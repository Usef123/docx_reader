package com.prox.docxreader.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.R;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.databinding.ActivityMainBinding;
import com.prox.docxreader.modul.Document;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final int REQUEST_PERMISSION_MANAGE = 123;
    private static final int REQUEST_PERMISSION_READ_WRITE = 456;
    private ActivityMainBinding binding;

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Load ngôn ngữ
        LocaleHelper.loadLanguage(this);

        //Tạo UI
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Cấp quyền
        if(permission()){
            insertDatabase();
        }else {
            requestPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentDatabase.getInstance(this).documentDAO().deleteAllDocument();
        Log.d("database", "delete all");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        appBarConfiguration = null;
        navController = null;
    }

    //Kiểm tra quyền
    private boolean permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else{
            int write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return (write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED);
        }
    }

    //Cấp quyền
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openDialogAccessAllFile();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_PERMISSION_READ_WRITE);
        }
    }

    private void openDialogAccessAllFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void onClick(DialogInterface dialog, int id) {
                requestAccessAllFile();
            }
        });
        builder.setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), R.string.notification_permission_error, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestAccessAllFile() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            startActivityForResult(intent, REQUEST_PERMISSION_MANAGE);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, REQUEST_PERMISSION_MANAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_WRITE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                insertDatabase();
            } else {
                Toast.makeText(this, R.string.notification_permission_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_MANAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    insertDatabase();
                } else {
                    Toast.makeText(this, R.string.notification_permission_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Tạo UI
    private void init() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null){
            return;
        }
        navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.favoriteFragment,
                R.id.settingFragment).build();

        binding.bottomNav.setItemIconTintList(null);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination.getId()==R.id.languageFragment) {
                binding.bottomNav.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.toolbar.setTitle(getResources().getString(R.string.language));
            }else if (navDestination.getId()==R.id.policyFragment){
                binding.bottomNav.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.toolbar.setTitle(getResources().getString(R.string.privacy_policy));
            } else{
                binding.bottomNav.setVisibility(View.VISIBLE);
                binding.toolbar.setVisibility(View.GONE);
                binding.toolbar.setTitle("");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                ||super.onSupportNavigateUp();
    }

    //Đẩy list file vào DB
    public void insertDatabase() {
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] columns = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,   //tên file
                MediaStore.Files.FileColumns.DATE_ADDED,     //date tạo
                MediaStore.Files.FileColumns.DATA};          //path file

        String selection = "_data LIKE '%.doc' OR _data LIKE '%.docx'";

        Cursor cursor = this.getContentResolver().query(uri, columns, selection, null, null);

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

            Log.d("database", "check "+document.getPath());
            if (new File(document.getPath()).exists()
                    && !isDocumentExist(document)
                    && !document.getPath().contains("Trash")){
                DocumentDatabase.getInstance(this).documentDAO().insertDocument(document);
                Log.d("database", "insert "+document.getPath());
            }
        }
        cursor.close();
    }

    private boolean isDocumentExist(Document document) {
        List<Document> documents = DocumentDatabase.getInstance(this).documentDAO().checkDocument(document.getPath());
        return documents != null && !documents.isEmpty();
    }
}