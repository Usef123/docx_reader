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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.R;
import com.prox.docxreader.service.DocumentManagerService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView bottomNavigationView;

    private DocumentManagerService documentManagerService;
    private boolean isConnecting;

    private Handler handler;
    private Runnable updateFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocaleHelper.loadLanguage(this);

        requestPermissions();
        setupUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!= null){
            if (updateFile!=null){
                handler.removeCallbacks(updateFile);
                updateFile = null;
            }
            handler=null;
        }
        if (isConnecting){
            unbindService(this);
            isConnecting = false;
        }
    }

    private void startService() {
        Intent intentService = new Intent(MainActivity.this, DocumentManagerService.class);
        startService(intentService);
        bindService(intentService, this, Context.BIND_AUTO_CREATE);

        handler = new Handler();
        updateFile = new Runnable() {
            @Override
            public void run() {
                if (isConnecting){
                    documentManagerService.insertDatabase();
                    documentManagerService.updateDatabase();
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateFile, 1000);
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
                        startService();
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
                        startService();
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

    private void setupUI() {
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
                    toolbar.setTitle(R.string.language);
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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        DocumentManagerService.MyBinder myBinder = (DocumentManagerService.MyBinder) iBinder;
        documentManagerService = myBinder.getDocumentManagerService();
        isConnecting = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        documentManagerService = null;
        isConnecting = false;
    }
}