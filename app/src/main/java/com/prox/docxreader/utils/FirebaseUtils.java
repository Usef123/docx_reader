package com.prox.docxreader.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.prox.docxreader.ui.activity.ReaderActivity;

public class FirebaseUtils {

    public static void sendEventOpenFile(Context context, Uri data, String path){
        Bundle bundle = new Bundle();
        bundle.putString("event_type", "open_file");
        bundle.putString("source_app", data.toString());
        bundle.putString("file_name", FileUtils.getName(path));
        bundle.putString("source_app", FileUtils.getType(path));
        FirebaseAnalytics.getInstance(context).logEvent("prox_office_reader", bundle);
    }

    public static void sendEventRequestPermission(Context context){
        Bundle bundle = new Bundle();
        if (PermissionUtils.permission(context)){
            bundle.putString("event_type", "success");
        }else {
            bundle.putString("event_type", "error");
        }
        FirebaseAnalytics.getInstance(context).logEvent("prox_permission", bundle);
    }

    public static void sendEventSubmitRatePermission(Context context, String comment, int rate){
        Bundle bundle = new Bundle();
        bundle.putString("event_type", "rated");
        bundle.putString("comment", comment);
        bundle.putString("star", rate + " star");
        FirebaseAnalytics.getInstance(context).logEvent("prox_rating_layout", bundle);
    }

    public static void sendEventLaterRatePermission(Context context){
        Bundle bundle = new Bundle();
        bundle.putString("event_type", "cancel");
        FirebaseAnalytics.getInstance(context).logEvent("prox_rating_layout", bundle);
    }

    public static void sendEventChangeRatePermission(Context context, int rate){
        Bundle bundle = new Bundle();
        bundle.putString("event_type", "rated");
        bundle.putString("star", rate + " star");
        FirebaseAnalytics.getInstance(context).logEvent("prox_rating_layout", bundle);
    }
}
