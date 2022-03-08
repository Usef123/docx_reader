package com.prox.docxreader.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prox.docxreader.R;

public class SettingFragment extends Fragment {
    private View view;
    private Button btnLanguage, btnShare, btnFeedback, btnMoreApp, btnPrivacyPolicy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnLanguage = view.findViewById(R.id.btn_language);
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_languageFragment);
            }
        });
        return view;
    }
}