package com.prox.docxreader.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prox.docxreader.R;

public class SettingFragment extends Fragment {
    public static final String EMAIL_FEEDBACK = "duclet2k@outlook.com";
    private static final String URI_APP = "https://play.google.com/store/apps/details?id=com.facebook.katana";
    private static final String URI_PACKAGE = "https://play.google.com/store/apps/dev?id=5700313618786177705";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Button btnLanguage = view.findViewById(R.id.btn_language);
        btnLanguage.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_languageFragment)
        );

        Button btnFeedback = view.findViewById(R.id.btn_feedback);
        btnFeedback.setOnClickListener(v -> openEmail());

        Button btnShare = view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> {
            shareApp();
        });

        Button btnMoreApp = view.findViewById(R.id.btn_more_app);
        btnMoreApp.setOnClickListener(v -> {
            openCHPlay();
        });

        Button btnPrivacyPolicy = view.findViewById(R.id.btn_privacy_policy);
        btnPrivacyPolicy.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_policyFragment)
        );
        return view;
    }

    private void openCHPlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URI_PACKAGE));
        startActivity(intent);
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, URI_APP);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.notification_share_to)));
    }

    public void openEmail() {
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_FEEDBACK});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_email));
        emailIntent.setSelector(selectorIntent);

        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.notification_send_mail)));
    }
}