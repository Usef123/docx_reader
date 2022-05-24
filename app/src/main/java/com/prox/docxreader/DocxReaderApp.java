package com.prox.docxreader;

import android.app.Application;

import com.proxglobal.proxads.adsv2.ads.ProxAds;
import com.proxglobal.purchase.ProxPurchase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DocxReaderApp extends Application {
    public static final String TAG = "ntduc";
    public static final ProxAds instance = ProxAds.getInstance();
    public static final ProxPurchase purchase = ProxPurchase.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        List<String> listINAPId = Collections.emptyList();
        List<String> listSubsId = Arrays.asList(BuildConfig.id_subs_month, BuildConfig.id_subs_year);
        ProxPurchase.getInstance().initBilling(this, listINAPId, listSubsId);
    }
}
