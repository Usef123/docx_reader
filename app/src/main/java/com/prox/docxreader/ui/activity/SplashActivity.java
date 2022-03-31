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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        ProxAds.getInstance().configure(this, BuildConfig.appId, "vz3ebfacd56a34480da8");
        ProxAds.getInstance().showSplash(this, new AdsCallback() {
            @Override
            public void onShow() {
                Log.d("showSplash", "onShow");
            }

            @Override
            public void onClosed() {
                Log.d("showSplash", "onClosed");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError() {
                Log.d("showSplash", "onError");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }, BuildConfig.interstitial_splash, null, 12000);
    }

    @Override
    public void onBackPressed() {
    }
}