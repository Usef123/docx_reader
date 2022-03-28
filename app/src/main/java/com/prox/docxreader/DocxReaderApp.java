package com.prox.docxreader;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.proxglobal.purchase.ProxPurchase;

public class DocxReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ProxPurchase.getInstance().initBilling(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }
}
