package com.prox.docxreader.ui.activity;

import static com.prox.docxreader.ui.activity.ReaderActivity.ACTION_OPEN_INSITE;
import static com.prox.docxreader.ui.activity.ReaderActivity.ACTION_OPEN_OUTSITE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.databinding.ActivityLoadBinding;
import com.proxglobal.proxads.adsv2.ads.ProxAds;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

public class LoadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoadBinding binding = ActivityLoadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent.getAction().equals(ACTION_OPEN_OUTSITE)){
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
            }, BuildConfig.interstitial_open_outside, null, 12000);
        }else if (intent.getAction().equals(ACTION_OPEN_INSITE)){
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
            }, BuildConfig.interstitial_global,  null, 12000);
        }
    }

    @Override
    public void onBackPressed() {
    }
}