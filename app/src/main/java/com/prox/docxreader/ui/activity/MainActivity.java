package com.prox.docxreader.ui.activity;

import static com.prox.docxreader.DocxReaderApp.TAG;
import static com.prox.docxreader.utils.PermissionUtils.REQUEST_PERMISSION_MANAGE;
import static com.prox.docxreader.utils.PermissionUtils.REQUEST_PERMISSION_READ_WRITE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.ActivityMainBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.utils.FirebaseUtils;
import com.prox.docxreader.utils.LanguageUtils;
import com.prox.docxreader.utils.NetworkUtils;
import com.prox.docxreader.utils.NotificationUtils;
import com.prox.docxreader.utils.PermissionUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;
import com.proxglobal.purchase.ProxPurchase;

import java.io.File;
import java.util.Stack;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DocumentViewModel model;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Observer<Document> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate");

        LanguageUtils.loadLanguage(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = new ViewModelProvider(this).get(DocumentViewModel.class);
        model.getInsertDB().observe(this, insertDB -> {
            if (insertDB){
                model.setValue();
            }
        });

        init();

        observer = getObserver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity onStart");
        if (PermissionUtils.permission(this)) {
            getObservable().observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        } else {
            PermissionUtils.requestPermissions(this, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop");
        PermissionUtils.cancelDialogAccessAllFile();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity onDestroy");
        appBarConfiguration = null;
        navController = null;
        binding = null;
        disposable.clear();
        super.onDestroy();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_WRITE) {
            FirebaseUtils.sendEventRequestPermission(this);

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getObservable().observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            } else {
                PermissionUtils.openDialogAccessAllFile(this, this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_MANAGE) {
            FirebaseUtils.sendEventRequestPermission(this);
        } else if (requestCode == REQUEST_PERMISSION_READ_WRITE) {
            FirebaseUtils.sendEventRequestPermission(this);
        }
    }

    //Tạo UI
    private void init() {
        Log.d(TAG, "MainActivity init");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
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
            if (navDestination.getId() == R.id.languageFragment) {
                binding.bannerAds.setVisibility(View.GONE);
                binding.bottomNav.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.toolbar.setTitle(getResources().getString(R.string.language));
            } else if (navDestination.getId() == R.id.premiumFragment) {
                binding.bannerAds.setVisibility(View.GONE);
                binding.bottomNav.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.GONE);
                binding.toolbar.setTitle("");
            } else if (navDestination.getId() == R.id.xlsFragment
                    || navDestination.getId() == R.id.pdfFragment
                    || navDestination.getId() == R.id.pptFragment ) {
                if (ProxPurchase.getInstance().checkPurchased() || !NetworkUtils.isNetworkAvailable(this)) {
                    binding.bannerAds.setVisibility(View.GONE);
                } else {
                    binding.bannerAds.setVisibility(View.VISIBLE);
                }
                binding.bottomNav.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.GONE);
                binding.toolbar.setTitle("");
            } else {
                if (ProxPurchase.getInstance().checkPurchased() || !NetworkUtils.isNetworkAvailable(this)) {
                    binding.bannerAds.setVisibility(View.GONE);
                } else {
                    binding.bannerAds.setVisibility(View.VISIBLE);
                }
                binding.bottomNav.setVisibility(View.VISIBLE);
                binding.toolbar.setVisibility(View.GONE);
                binding.toolbar.setTitle("");
            }
        });

        NotificationUtils.createChannelFireBase(this);

        if (ProxPurchase.getInstance().checkPurchased()
                || !NetworkUtils.isNetworkAvailable(this)) {
            binding.bannerAds.setVisibility(View.GONE);
        }

        DocxReaderApp.instance.showBanner(
                this,
                binding.bannerAds,
                BuildConfig.banner,
                new AdsCallback() {
                    @Override
                    public void onShow() {
                        super.onShow();
                        Log.d(TAG, "MainActivity Ads onShow");
                    }

                    @Override
                    public void onClosed() {
                        super.onClosed();
                        Log.d(TAG, "MainActivity Ads onClosed");
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        Log.d(TAG, "MainActivity Ads onError");
                    }
                }
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private Observable<Document> getObservable() {
        Stack<File> folderStack = new Stack<>();
        folderStack.add(Environment.getExternalStorageDirectory());

        return Observable.create(emitter -> {
            while (!folderStack.empty()){
                File[] files = folderStack.pop().listFiles();
                if (files != null){
                    for (File f : files){
                        if (f.isDirectory()){
                            folderStack.add(f);
                        }else {
                            if (f.getName().endsWith("doc")
                                    || f.getName().endsWith("dot")
                                    || f.getName().endsWith("docx")
                                    || f.getName().endsWith("dotx")

                                    || f.getName().endsWith("xls")
                                    || f.getName().endsWith("xlsx")
                                    || f.getName().endsWith("xltm")
                                    || f.getName().endsWith("xltx")
                                    || f.getName().endsWith("csv")

                                    || f.getName().endsWith("ppt")
                                    || f.getName().endsWith("pptx")

                                    || f.getName().endsWith("pdf")){
                                Document document = new Document();
                                document.setPath(f.getPath());
                                document.setTitle(f.getName());
                                document.setTimeCreate(f.lastModified());
                                document.setTimeAccess(f.lastModified());
                                document.setFavorite(false);
                                document.setExist(true);
                                if (!emitter.isDisposed()){
                                    emitter.onNext(document);
                                }
                            }
                        }
                    }
                }
            }
            if (!emitter.isDisposed()){
                emitter.onComplete();
            }
        });
    }

    private Observer<Document> getObserver() {
        return new Observer<Document>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Document document) {
                Log.d(TAG, "MainActivity onNext: " + document.getPath());
                Document documentCheck = model.check(document.getPath());
                if (documentCheck != null) {
                    documentCheck.setExist(true);
                    model.updateBG(documentCheck);
                }else {
                    model.insertBG(document);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "MainActivity onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "MainActivity onComplete");
                model.deleteNotExistBG();
                model.updateIsExistBG();
                model.setInsertDB(true);
            }
        };
    }
}