package com.prox.docxreader.ui.activity;

import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.FileUtils;
import com.prox.docxreader.databinding.ActivitySplashBinding;
import com.proxglobal.proxads.adsv2.ads.ProxAds;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;
import com.proxglobal.purchase.ProxPurchase;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    public static final String SPLASH_TO_MAIN = "SPLASH_TO_MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (ProxPurchase.getInstance().checkPurchased()){
            binding.tittleSplash.setVisibility(View.GONE);
        }

        String action = getIntent().getAction();
        new Handler().postDelayed(() -> {
            if(action.equals(Intent.ACTION_MAIN)){
                showInterSplash();
            }else if(getIntent().getAction().equals(Intent.ACTION_VIEW)){
                showInterOutside();
            }
        }, 1000);
    }

    private void showInterSplash() {
        ProxAds.getInstance().showSplash(this, new AdsCallback() {
            @Override
            public void onShow() {
                Log.d("interstitial_splash", "onShow");
            }

            @Override
            public void onClosed() {
                Log.d("interstitial_splash", "onClosed");
                goToMainActivity();
            }

            @Override
            public void onError() {
                Log.d("interstitial_splash", "onError");
                goToMainActivity();
            }
        }, BuildConfig.interstitial_splash, null, 10000);
    }

    private void showInterOutside() {
        ProxAds.getInstance().showSplash(this, new AdsCallback() {
            @Override
            public void onShow() {
                Log.d("interstitial_outside", "onShow");
            }

            @Override
            public void onClosed() {
                Log.d("interstitial_outside", "onClosed");
                goToReaderActivity();
            }

            @Override
            public void onError() {
                Log.d("interstitial_outside", "onError");
                goToReaderActivity();
            }
        }, BuildConfig.interstitial_open_outside, null, 12000);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(SPLASH_TO_MAIN);
        startActivity(intent);
        finish();
    }


    private void goToReaderActivity() {
        Uri data = getIntent().getData();
        String filePath = FileUtils.getFilePathForN(data, this);

        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}