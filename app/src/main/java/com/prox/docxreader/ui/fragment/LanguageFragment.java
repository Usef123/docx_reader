package com.prox.docxreader.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.OnClickLanguageListener;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.LangugeAdapter;

public class LanguageFragment extends Fragment {
    private View view;
    private LangugeAdapter langugeAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_language, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_language);
        langugeAdapter = new LangugeAdapter(
                getResources().getStringArray(R.array.language),
                getResources().getStringArray(R.array.type_language),
                new OnClickLanguageListener() {
                    @Override
                    public void onClickLanguage(String typeLanguage) {
                        LocaleHelper.setLocale(getContext(), typeLanguage);
                        getActivity().recreate();
                    }
                });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(langugeAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerHorizontal);

        return view;
    }
}