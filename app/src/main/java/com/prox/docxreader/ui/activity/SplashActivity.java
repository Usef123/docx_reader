package com.prox.docxreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.databinding.ActivitySplashBinding;
import com.proxglobal.proxads.adsv2.ads.ProxAds;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

public class SplashActivity extends AppCompatActivity {
    public static final String MAIN_TO_SPLASH = "MAIN_TO_SPLASH";
    public static final String VIEW_TO_SPLASH = "VIEW_TO_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProxAds.getInstance().initInterstitial(this, BuildConfig.interstitial_global, null, "insite");

        String type = "";
        if(getIntent().getAction().equals(MAIN_TO_SPLASH)){
            type = BuildConfig.interstitial_splash;
        }else if(getIntent().getAction().equals(VIEW_TO_SPLASH)){
            type = BuildConfig.interstitial_open_outside;
        }

        ProxAds.getInstance().showSplash(this, new AdsCallback() {
            @Override
            public void onShow() {
                Log.d("showSplash", "onShow");
            }

            @Override
            public void onClosed() {
                Log.d("showSplash", "onClosed");
                resultIntent();
            }

            @Override
            public void onError() {
                Log.d("showSplash", "onError");
                resultIntent();
            }
        }, type, null, 12000);
    }

    private void resultIntent() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}