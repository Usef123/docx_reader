package com.prox.docxreader;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.proxglobal.proxads.ProxUtils;
import com.proxglobal.proxads.ads.ProxInterstitialAd;

public class MyAds{
    private static ProxInterstitialAd inter;
    private static AdRequest adRequest;
    public static int numberBack = 1;

    public static ProxInterstitialAd getInter(Activity activity) {
        if (inter==null) {
            inter = ProxUtils.INSTANCE.createInterstitialAd (activity, ProxUtils.TEST_INTERSTITIAL_ID);
            inter.load();
        }
        return inter;
    }

    public static AdRequest getAdRequest() {
        if (adRequest==null) {
            adRequest = new AdRequest.Builder().build();
        }
        return adRequest;
    }
}
