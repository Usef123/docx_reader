package com.prox.docxreader.ui.dialog;

import static com.prox.docxreader.repository.DocumentRepository.SORT_NAME;
import static com.prox.docxreader.repository.DocumentRepository.SORT_TIME_ACCESS;
import static com.prox.docxreader.repository.DocumentRepository.SORT_TIME_CREATE;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.interfaces.OnSelectSortListener;
import com.prox.docxreader.ui.fragment.FavoriteFragment;
import com.prox.docxreader.ui.fragment.HomeFragment;

public class SortDialog extends CustomDialog{

    public SortDialog(@NonNull Context context,
                      DialogSortBinding binding,
                      boolean isFavorite,
                      int typeSort,
                      OnSelectSortListener onSelectSortListener) {
        super(context, binding.getRoot());

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
            if (!isFavorite){
                HomeFragment.typeSort = SORT_NAME;
            }else {
                FavoriteFragment.typeSort = SORT_NAME;
            }
            onSelectSortListener.onSelectSort();
            cancel();
        });
        binding.sortTimeCreate.setOnClickListener(v -> {
            if (!isFavorite){
                HomeFragment.typeSort = SORT_TIME_CREATE;
            }else {
                FavoriteFragment.typeSort = SORT_TIME_CREATE;
            }
            onSelectSortListener.onSelectSort();
            cancel();
        });
        binding.sortTimeAccess.setOnClickListener(v -> {
            if (!isFavorite){
                HomeFragment.typeSort = SORT_TIME_ACCESS;
            }else {
                FavoriteFragment.typeSort = SORT_TIME_ACCESS;
            }
            onSelectSortListener.onSelectSort();
            cancel();
        });
    }
}
