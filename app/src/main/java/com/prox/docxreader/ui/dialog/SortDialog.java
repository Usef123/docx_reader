package com.prox.docxreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.ui.fragment.FavoriteFragment;
import com.prox.docxreader.ui.fragment.HomeFragment;
import com.prox.docxreader.ui.fragment.PDFFragment;
import com.prox.docxreader.ui.fragment.PPTFragment;
import com.prox.docxreader.ui.fragment.XLSFragment;
import com.prox.docxreader.viewmodel.DocumentViewModel;

public class SortDialog extends Dialog {
    public static final int FRAGMENT_HOME = 1;
    public static final int FRAGMENT_FAVORITE = 2;
    public static final int FRAGMENT_XLSX = 3;
    public static final int FRAGMENT_PDF = 4;
    public static final int FRAGMENT_PPTX = 5;

    public static final int SORT_NAME = 1;
    public static final int SORT_TIME_CREATE = 2;
    public static final int SORT_TIME_ACCESS = 3;

    public SortDialog(@NonNull Context context,
                      DialogSortBinding binding,
                      DocumentViewModel model,
                      int typeFragment,
                      int typeSort) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        setCancelable(true);

        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP|Gravity.END;
        layoutParams.y = 128;
        layoutParams.x = 16;
        getWindow().setAttributes(layoutParams);

        switch (typeSort){
            case SORT_NAME:
                binding.nameChecked.setVisibility(View.VISIBLE);
                binding.timeCreateChecked.setVisibility(View.INVISIBLE);
                binding.timeAccessChecked.setVisibility(View.INVISIBLE);
                break;
            case SORT_TIME_CREATE:
                binding.nameChecked.setVisibility(View.INVISIBLE);
                binding.timeCreateChecked.setVisibility(View.VISIBLE);
                binding.timeAccessChecked.setVisibility(View.INVISIBLE);
                break;
            case SORT_TIME_ACCESS:
                binding.nameChecked.setVisibility(View.INVISIBLE);
                binding.timeCreateChecked.setVisibility(View.INVISIBLE);
                binding.timeAccessChecked.setVisibility(View.VISIBLE);
                break;
        }

        binding.sortName.setOnClickListener(v -> {
            switch (typeFragment) {
                case FRAGMENT_HOME:
                    HomeFragment.typeSort = SORT_NAME;
                    break;
                case FRAGMENT_FAVORITE:
                    FavoriteFragment.typeSort = SORT_NAME;
                    break;
                case FRAGMENT_XLSX:
                    XLSFragment.typeSort = SORT_NAME;
                    break;
                case FRAGMENT_PDF:
                    PDFFragment.typeSort = SORT_NAME;
                    break;
                case FRAGMENT_PPTX:
                    PPTFragment.typeSort = SORT_NAME;
                    break;
            }
            model.setValue();
            cancel();
        });

        binding.sortTimeCreate.setOnClickListener(v -> {
            switch (typeFragment) {
                case FRAGMENT_HOME:
                    HomeFragment.typeSort = SORT_TIME_CREATE;
                    break;
                case FRAGMENT_FAVORITE:
                    FavoriteFragment.typeSort = SORT_TIME_CREATE;
                    break;
                case FRAGMENT_XLSX:
                    XLSFragment.typeSort = SORT_TIME_CREATE;
                    break;
                case FRAGMENT_PDF:
                    PDFFragment.typeSort = SORT_TIME_CREATE;
                    break;
                case FRAGMENT_PPTX:
                    PPTFragment.typeSort = SORT_TIME_CREATE;
                    break;
            }
            model.setValue();
            cancel();
        });

        binding.sortTimeAccess.setOnClickListener(v -> {
            switch (typeFragment) {
                case FRAGMENT_HOME:
                    HomeFragment.typeSort = SORT_TIME_ACCESS;
                    break;
                case FRAGMENT_FAVORITE:
                    FavoriteFragment.typeSort = SORT_TIME_ACCESS;
                    break;
                case FRAGMENT_XLSX:
                    XLSFragment.typeSort = SORT_TIME_ACCESS;
                    break;
                case FRAGMENT_PDF:
                    PDFFragment.typeSort = SORT_TIME_ACCESS;
                    break;
                case FRAGMENT_PPTX:
                    PPTFragment.typeSort = SORT_TIME_ACCESS;
                    break;
            }
            model.setValue();
            cancel();
        });
    }
}
