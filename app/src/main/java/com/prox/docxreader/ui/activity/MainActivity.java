package com.prox.docxreader.ui.activity;

import static com.prox.docxreader.ui.activity.SplashActivity.SPLASH_TO_MAIN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.R;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.databinding.ActivityMainBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.proxglobal.proxads.adsv2.ads.ProxAds;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;
import com.proxglobal.purchase.ProxPurchase;
import com.proxglobal.rate.ProxRateDialog;
import com.proxglobal.rate.RatingDialogListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final int REQUEST_PERMISSION_MANAGE = 10;
    private static final int REQUEST_PERMISSION_READ_WRITE = 11;

    private ActivityMainBinding binding;

    private DocumentViewModel viewModel;

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    private AlertDialog dialogRequest;
    private boolean isBackPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Load ngôn ngữ
        LocaleHelper.loadLanguage(this);

        viewModel = new ViewModelProvider(this).get(DocumentViewModel.class);

        //Tạo UI
        init();

        String action = getIntent().getAction();
        if (action.equals(SPLASH_TO_MAIN)){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int openApp = preferences.getInt("open_app", 1);
            Log.d("openApp", String.valueOf(openApp));
            if (openApp == 1){
                preferences.edit().putInt("open_app", openApp+1).apply();
            }else{
                ProxRateDialog.showIfNeed(this, getSupportFragmentManager());
            }
        }

        ProxAds.getInstance().initInterstitial(this, BuildConfig.interstitial_global, null, "insite");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Cấp quyền
        if(permission()){
            new InsertDBAsyncTask(this).execute();
        }else {
            requestPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialogRequest != null && dialogRequest.isShowing()){
            dialogRequest.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        appBarConfiguration = null;
        navController = null;
        binding = null;
        dialogRequest = null;
        super.onDestroy();
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void openDialogAccessAllFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.txt_ok, (dialog, id) -> requestAccessAllFile());
        builder.setNegativeButton(R.string.txt_cancel, (dialog, id) -> finish());

        dialogRequest = builder.create();
        dialogRequest.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestAccessAllFile() {
        try {
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri);
            startActivityForResult(intent, REQUEST_PERMISSION_MANAGE);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
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
                new InsertDBAsyncTask(this).execute();
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
                    new InsertDBAsyncTask(this).execute();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(onSupportNavigateUp()){
           return;
        }
        isBackPress = true;
        ProxRateDialog.showIfNeed(this, getSupportFragmentManager());

        SharedPreferences sp = this.getSharedPreferences("prox", Context.MODE_PRIVATE);
        if (sp.getBoolean("isRated", false)){
            super.onBackPressed();
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
            }else if (navDestination.getId()==R.id.premiumFragment){
                binding.bannerAds.setVisibility(View.GONE);
                binding.bottomNav.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.GONE);
                binding.toolbar.setTitle("");
            } else{
                binding.bottomNav.setVisibility(View.VISIBLE);
                binding.toolbar.setVisibility(View.GONE);
                binding.toolbar.setTitle("");
            }
        });

        if (ProxPurchase.getInstance().checkPurchased()){
            binding.bannerAds.setVisibility(View.GONE);
        }

        ProxAds.getInstance().showBanner(this, binding.bannerAds, BuildConfig.banner, new AdsCallback() {
                    @Override
                    public void onShow() {
                        super.onShow();
                        Log.d("bannerAds", "onShow");
                    }

                    @Override
                    public void onClosed() {
                        super.onClosed();
                        Log.d("bannerAds", "onClosed");
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        Log.d("bannerAds", "onError");
                    }
                }
        );

        ProxRateDialog.Config config = new ProxRateDialog.Config();
        config.setListener(new RatingDialogListener() {
            @Override
            public void onSubmitButtonClicked(int rate, String comment) {
                Log.d("rate_app", "onSubmitButtonClicked " + rate + comment);
                Bundle bundle = new Bundle();
                bundle.putString("event_type", "rated");
                bundle.putString("comment", comment);
                bundle.putString("star", rate + " star");
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("prox_rating_layout", bundle);
            }

            @Override
            public void onLaterButtonClicked() {
                Log.d("rate_app", "onLaterButtonClicked");
                Bundle bundle = new Bundle();
                bundle.putString("event_type", "cancel");
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("prox_rating_layout", bundle);
                if (isBackPress) {
                    finish();
                }
            }

            @Override
            public void onChangeStar(int rate) {
                Log.d("rate_app", "onChangeStar " + rate);
                if (rate >= 4) {
                    Bundle bundle = new Bundle();
                    bundle.putString("event_type", "rated");
                    bundle.putString("star", rate + " star");
                    FirebaseAnalytics.getInstance(MainActivity.this).logEvent("prox_rating_layout", bundle);
                }
            }

            @Override
            public void onDone() {
                Log.d("rate_app", "onDone");
                if(isBackPress){
                    finish();
                }
            }
        });
        ProxRateDialog.init(config);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                ||super.onSupportNavigateUp();
    }

    @SuppressLint("StaticFieldLeak")
    private class InsertDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;

        private InsertDBAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Document> documents = getDocuments();
            List<Document> documentCheck;
            for (Document document : documents){
                Log.d("viewmodel", "check: "+document.getPath());
                documentCheck = DocumentDatabase.getInstance(context).documentDAO().check(document.getPath());
                if (!documentCheck.isEmpty()){
                    documentCheck.get(0).setExist(true);
                    viewModel.update(documentCheck.get(0));
                }else {
                    viewModel.insert(document);
                }
            }

            viewModel.deleteNotExist();
            viewModel.updateIsExist();
            return null;
        }

        //Lấy list file
        private List<Document> getDocuments() {
            List<Document> documents = new ArrayList<>();

            Uri uri = MediaStore.Files.getContentUri("external");

            String[] columns = {
                    MediaStore.Files.FileColumns.DISPLAY_NAME,   //tên file
                    MediaStore.Files.FileColumns.DATE_ADDED,     //date tạo
                    MediaStore.Files.FileColumns.DATA};          //path file

            String selection = "_data LIKE '%.doc' OR _data LIKE '%.docx'";

            Cursor cursor = context.getContentResolver().query(uri, columns, selection, null, null);

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
                document.setExist(true);

                if (!document.getPath().contains(".Trash")){
                    documents.add(document);
                }
            }
            cursor.close();
            return documents;
        }
    }
}