package com.prox.docxreader.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prox.docxreader.R;
import com.prox.docxreader.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    public static final String EMAIL_FEEDBACK = "duclet2k@outlook.com";
    private static final String URI_PACKAGE = "https://play.google.com/store/apps/developer?id=Andromeda+App";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSettingBinding binding = FragmentSettingBinding.inflate(inflater, container, false);

        binding.btnLanguage.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_settingFragment_to_languageFragment)
        );

        binding.btnFeedback.setOnClickListener(v -> openEmail());

        binding.btnShare.setOnClickListener(v -> shareApp());

        binding.btnMoreApp.setOnClickListener(v -> openCHPlay());

        binding.btnPrivacyPolicy.setOnClickListener(v ->
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_settingFragment_to_policyFragment)
        );
        return binding.getRoot();
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
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+requireActivity().getApplicationContext().getPackageName());
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