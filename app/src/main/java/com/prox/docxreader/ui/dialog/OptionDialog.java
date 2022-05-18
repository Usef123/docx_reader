package com.prox.docxreader.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.prox.docxreader.R;
import com.prox.docxreader.databinding.DialogOptionBinding;
import com.prox.docxreader.utils.FirebaseUtils;

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
        layoutParams.windowAnimations = R.style.OptionDialogAnimation;
        getWindow().setAttributes(layoutParams);

        binding.getRoot().setBackgroundResource(0);

        binding.itemExcelOption.excelOption.setOnClickListener(view -> {
            FirebaseUtils.sendEventMenu(context, FirebaseUtils.MENU_XLSX);
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_xlsFragment);
            cancel();
        });

        binding.itemPdfOption.pdfOption.setOnClickListener(view -> {
            FirebaseUtils.sendEventMenu(context, FirebaseUtils.MENU_PDF);
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pdfFragment);
            cancel();
        });

        binding.itemPptOption.pptOption.setOnClickListener(view -> {
            FirebaseUtils.sendEventMenu(context, FirebaseUtils.MENU_PPTX);
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.action_global_pptFragment);
            cancel();
        });
    }
}
