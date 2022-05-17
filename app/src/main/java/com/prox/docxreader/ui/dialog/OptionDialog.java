package com.prox.docxreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.prox.docxreader.databinding.DialogOptionBinding;

public class OptionDialog extends Dialog {

    public OptionDialog(@NonNull Context context,
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
    }
}
