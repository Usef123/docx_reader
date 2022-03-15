package com.prox.docxreader.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prox.docxreader.databinding.FragmentPolicyBinding;

public class PolicyFragment extends Fragment {
    private static final String URI_POLICY = "https://docs.yoctoproject.org/overview-manual/intro.html#";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPolicyBinding binding = FragmentPolicyBinding.inflate(inflater, container, false);
        binding.webPolicy.loadUrl(URI_POLICY);
        return binding.getRoot();
    }
}