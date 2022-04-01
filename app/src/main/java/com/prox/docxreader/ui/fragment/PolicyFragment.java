package com.prox.docxreader.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.prox.docxreader.databinding.FragmentPolicyBinding;

public class PolicyFragment extends Fragment {
    private static final String URI_POLICY = "https://hellowordapp.github.io/policy/privacy.html";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPolicyBinding binding = FragmentPolicyBinding.inflate(inflater, container, false);
        binding.webPolicy.loadUrl(URI_POLICY);
        return binding.getRoot();
    }
}