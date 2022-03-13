package com.prox.docxreader.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.prox.docxreader.R;

public class PolicyFragment extends Fragment {
    private static final String URI_POLICY = "https://docs.yoctoproject.org/overview-manual/intro.html#";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_policy, container, false);
        WebView webView = view.findViewById(R.id.web_policy);
        webView.loadUrl(URI_POLICY);
        return view;
    }
}