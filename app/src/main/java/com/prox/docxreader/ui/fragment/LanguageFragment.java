package com.prox.docxreader.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.LangugeAdapter;
import com.prox.docxreader.databinding.FragmentLanguageBinding;

public class LanguageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLanguageBinding binding = FragmentLanguageBinding.inflate(inflater, container, false);

        LangugeAdapter langugeAdapter = new LangugeAdapter(
                getResources().getStringArray(R.array.language),
                getResources().getStringArray(R.array.type_language),
                typeLanguage -> {
                    LocaleHelper.setLocale(getContext(), typeLanguage);
                    requireActivity().recreate();
                });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerViewLanguage.setLayoutManager(manager);
        binding.recyclerViewLanguage.setAdapter(langugeAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        binding.recyclerViewLanguage.addItemDecoration(dividerHorizontal);

        return binding.getRoot();
    }
}