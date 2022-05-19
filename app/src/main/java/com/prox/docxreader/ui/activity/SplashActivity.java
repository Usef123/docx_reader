package com.prox.docxreader.ui.activity;

import static com.prox.docxreader.DocxReaderApp.TAG;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.ActivitySplashBinding;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.utils.FirebaseUtils;
import com.prox.docxreader.utils.LanguageUtils;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;
import com.proxglobal.purchase.ProxPurchase;

import java.io.File;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    public static final String SPLASH_TO_MAIN = "SPLASH_TO_MAIN";
    public static final String OPEN_OUTSIDE = "OPEN_OUTSIDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SplashActivity onCreate");

        //Load ngôn ngữ
        LanguageUtils.loadLanguage(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.white));

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ProxPurchase.getInstance().checkPurchased()) {
            binding.tittleSplash.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        if (intent == null) {
            Log.d(TAG, "SplashActivity intent null");
            goToMainActivity();
            return;
        }

        DocxReaderApp.instance.initInterstitial(this, BuildConfig.interstitial_global, null, "insite");
        DocxReaderApp.instance.initInterstitial(this, BuildConfig.interstitial_menu, null, "menu");

        String action = getIntent().getAction();
        new Handler().postDelayed(() -> {
            if (action == null) {
                Log.d(TAG, "SplashActivity action null");
                goToMainActivity();
            } else if (action.equals(Intent.ACTION_MAIN)) {
                Log.d(TAG, "SplashActivity action ACTION_MAIN");
                showInterSplash();
            } else if (action.equals(Intent.ACTION_VIEW)) {
                Log.d(TAG, "SplashActivity action ACTION_VIEW");
                showInterOutside();
            } else {
                goToMainActivity();
            }
        }, 1000);
    }

    private void showInterSplash() {
        DocxReaderApp.instance.showSplash(this, new AdsCallback() {
            @Override
            public void onShow() {
                Log.d(TAG, "SplashActivity Ads onShow");
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "SplashActivity Ads onClosed");
                goToMainActivity();
            }

            @Override
            public void onError() {
                Log.d(TAG, "SplashActivity Ads onError");
                goToMainActivity();
            }
        }, BuildConfig.interstitial_splash, null, 12000);
    }

    private void showInterOutside() {
        DocxReaderApp.instance.showSplash(this, new AdsCallback() {
            @Override
            public void onShow() {
                Log.d(TAG, "SplashActivity Ads onShow");
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "SplashActivity Ads onClosed");
                goToReaderActivity();
            }

            @Override
            public void onError() {
                Log.d(TAG, "SplashActivity Ads onError");
                goToReaderActivity();
            }
        }, BuildConfig.interstitial_open_outside, null, 12000);
    }

    private void goToMainActivity() {
        Log.d(TAG, "SplashActivity goToMainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(SPLASH_TO_MAIN);
        startActivity(intent);
        finish();
    }


    private void goToReaderActivity() {
        Log.d(TAG, "SplashActivity goToReaderActivity");
        Uri data = getIntent().getData();
        String path = FileUtils.getRealPath(this, data);

        FirebaseUtils.sendEventOpenFile(this, data, path);

        if (path == null){
            Log.d(TAG, "SplashActivity path null");
            goToMainActivity();
            Toast.makeText(this, R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
        }else if (new File(path).exists()) {
            Log.d(TAG, "SplashActivity path "+path);
            Intent intent = new Intent(this, ReaderActivity.class);
            intent.putExtra(FILE_PATH, path);
            intent.putExtra(OPEN_OUTSIDE, true);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "SplashActivity file not exist");
            goToMainActivity();
            Toast.makeText(this, R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}