package com.prox.docxreader.ui.dialog;

import static com.prox.docxreader.DocxReaderApp.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.DialogOptionBinding;
import com.prox.docxreader.utils.FirebaseUtils;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

public class OptionDialog extends Dialog {

    public OptionDialog(@NonNull Context context,
                        Activity activity,
                        DialogOptionBinding binding) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        setCancelable(true);

        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP|Gravity.START;
        layoutParams.y = 128;
        layoutParams.x = 16;
        getWindow().setAttributes(layoutParams);

        binding.getRoot().setBackgroundResource(0);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int choose_menu = preferences.getInt("choose_menu", 1);
        Log.d("choose_menu", String.valueOf(choose_menu));

        binding.itemExcelOption.excelOption.setOnClickListener(view -> {
            FirebaseUtils.sendEventMenu(context, FirebaseUtils.MENU_XLSX);

            if (choose_menu % 2 == 0) {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                DocxReaderApp.instance.showInterstitial(activity, "menu", new AdsCallback() {
                    @Override
                    public void onClosed() {
                        super.onClosed();
                        Log.d(TAG, "HomeFragment Ads onClosed");
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_xlsFragment);
                        cancel();
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        Log.d(TAG, "HomeFragment Ads onError");
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_xlsFragment);
                        cancel();
                    }
                });
            }else {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_xlsFragment);
                cancel();
            }
        });

        binding.itemPdfOption.pdfOption.setOnClickListener(view -> {
            FirebaseUtils.sendEventMenu(context, FirebaseUtils.MENU_PDF);

            if (choose_menu % 2 == 0) {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                DocxReaderApp.instance.showInterstitial(activity, "menu", new AdsCallback() {
                    @Override
                    public void onClosed() {
                        super.onClosed();
                        Log.d(TAG, "HomeFragment Ads onClosed");
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pdfFragment);
                        cancel();
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        Log.d(TAG, "HomeFragment Ads onError");
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pdfFragment);
                        cancel();
                    }
                });
            }else {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pdfFragment);
                cancel();
            }
        });

        binding.itemPptOption.pptOption.setOnClickListener(view -> {
            FirebaseUtils.sendEventMenu(context, FirebaseUtils.MENU_PPTX);

            if (choose_menu % 2 == 0) {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                DocxReaderApp.instance.showInterstitial(activity, "menu", new AdsCallback() {
                    @Override
                    public void onClosed() {
                        super.onClosed();
                        Log.d(TAG, "HomeFragment Ads onClosed");
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pptFragment);
                        cancel();
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        Log.d(TAG, "HomeFragment Ads onError");
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pptFragment);
                        cancel();
                    }
                });
            }else {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pptFragment);
                cancel();
            }
        });
    }
}
