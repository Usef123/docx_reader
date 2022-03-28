package com.prox.docxreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.MyAds;
import com.prox.docxreader.databinding.ActivitySplashBinding;
import com.proxglobal.proxads.ads.callback.AdCallback;

public class SplashActivity extends AppCompatActivity {
    public static final String CLOSE_SPLASH = "CLOSE_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyAds.getInter(this).loadSplash(10000, new AdCallback() {
            @Override
            public void onAdClose() {
                Intent intent = new Intent();
                intent.putExtra(CLOSE_SPLASH, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}