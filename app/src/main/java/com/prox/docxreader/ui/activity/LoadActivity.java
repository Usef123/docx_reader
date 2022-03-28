package com.prox.docxreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.MyAds;
import com.prox.docxreader.databinding.ActivityLoadBinding;
import com.proxglobal.proxads.ads.callback.AdCallback;

public class LoadActivity extends AppCompatActivity {
    public static final String CLOSE_LOAD = "CLOSE_LOAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoadBinding binding = ActivityLoadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyAds.getInter(this).loadSplash(10000, new AdCallback() {
            @Override
            public void onAdClose() {
                Intent intent = new Intent();
                intent.putExtra(CLOSE_LOAD, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}