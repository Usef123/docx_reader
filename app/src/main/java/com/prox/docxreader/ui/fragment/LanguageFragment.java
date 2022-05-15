package com.prox.docxreader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prox.docxreader.utils.LanguageUtils;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.LanguageAdapter;
import com.prox.docxreader.databinding.FragmentLanguageBinding;
import com.prox.docxreader.ui.activity.MainActivity;

public class LanguageFragment extends Fragment {
    public static final String CHANGE_LANGUAGE = "CHANGE_LANGUAGE";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLanguageBinding binding = FragmentLanguageBinding.inflate(inflater, container, false);

        LanguageAdapter languageAdapter = new LanguageAdapter(
                requireContext(),
                getResources().getStringArray(R.array.language),
                getResources().getStringArray(R.array.type_language),
                typeLanguage -> {
                    LanguageUtils.setLocale(getContext(), typeLanguage);
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.setAction(CHANGE_LANGUAGE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerViewLanguage.setLayoutManager(manager);
        binding.recyclerViewLanguage.setAdapter(languageAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        binding.recyclerViewLanguage.addItemDecoration(dividerHorizontal);

        return binding.getRoot();
    }
}