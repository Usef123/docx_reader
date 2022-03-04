package com.prox.docxreader.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prox.docxreader.R;
import com.prox.docxreader.service.DocumentManagerService;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private static final int MY_REQUEST_PERMISSION = 1;

    private BottomNavigationView bottomNavigationView;

    private DocumentManagerService documentManagerService;
    private boolean isConnecting;

    private Handler handler;
    private Runnable updateFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        setupBottomNav();
        startService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateFile);
        handler=null;
        unbindService(this);
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

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
        }else if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
        }else{
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
            }
        }
    }

    private void setupBottomNav() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.favoriteFragment,
                R.id.settingFragment).build();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        DocumentManagerService.MyBinder myBinder = (DocumentManagerService.MyBinder) iBinder;
        documentManagerService = myBinder.getDocumentManagerService();
        isConnecting = true;
        documentManagerService.insertDatabase();
        documentManagerService.updateDatabase();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        documentManagerService = null;
        isConnecting = false;
    }
}