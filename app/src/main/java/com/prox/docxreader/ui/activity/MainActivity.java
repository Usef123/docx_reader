package com.prox.docxreader.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.R;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocaleHelper.loadLanguage(this);

        requestPermissions();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomNavigationView = null;
        appBarConfiguration = null;
        navController=null;
    }

    private void requestPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        requestPermission();
                    }else{
                        insertDatabase();
                    }
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.notification_permission_error), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        insertDatabase();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.notification_permission_error), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void init() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.favoriteFragment,
                R.id.settingFragment).build();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController,
                                             @NonNull NavDestination navDestination,
                                             @Nullable Bundle bundle) {
                if (navDestination.getId()==R.id.languageFragment){
                    bottomNavigationView.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle(getResources().getString(R.string.language));
                } else{
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
                    toolbar.setTitle("");
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                ||super.onSupportNavigateUp();
    }

    public void insertDatabase() {
        Uri uri = MediaStore.Files.getContentUri("external");

        final String[] columns = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,   //tên file
                MediaStore.Files.FileColumns.DATE_ADDED,     //date tạo
                MediaStore.Files.FileColumns.MIME_TYPE,      //kiểu: docx
                MediaStore.Files.FileColumns.DATA};          //path file

        String selection = "_data LIKE '%.doc' OR _data LIKE '%.docx'";

        Cursor cursor = this.getContentResolver().query(uri, columns, selection, null, null);
        Log.d("database", "number: "+cursor.getCount());


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
                    Log.d("database", "insert "+document.getPath());
                }
            }
            cursor.close();
        }
    }

    private boolean isDocumentExist(Document document) {
        List<Document> documents = DocumentDatabase.getInstance(this).documentDAO().checkDocument(document.getPath());
        return documents != null && !documents.isEmpty();
    }
}