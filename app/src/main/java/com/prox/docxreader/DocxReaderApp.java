package com.prox.docxreader;

import android.app.Application;

import com.proxglobal.purchase.ProxPurchase;

import java.util.Collections;
import java.util.List;

public class DocxReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        List<String> listINAPId = Collections.emptyList();
        List<String> listSubsId = Collections.singletonList(BuildConfig.id_subs);
        ProxPurchase.getInstance().initBilling(this, listINAPId, listSubsId);

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        String token = task.getResult();
//                        Log.d("ntduc", token);
//                    }
//                });
    }
}
