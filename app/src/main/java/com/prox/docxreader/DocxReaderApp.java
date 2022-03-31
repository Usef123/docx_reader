package com.prox.docxreader;

import android.app.Application;

import com.proxglobal.purchase.ProxPurchase;

import java.util.Arrays;
import java.util.List;

public class DocxReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        List<String> listINAPId = Arrays.asList(BuildConfig.purchase);
        List<String> listSubsId = Arrays.asList();
        ProxPurchase.getInstance().initBilling(this, listINAPId, listSubsId);
    }
}
