package com.prox.docxreader.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.prox.docxreader.R;
import com.prox.docxreader.databinding.FragmentPdfBinding;

public class PDFFragment extends Fragment {
    private FragmentPdfBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPdfBinding.inflate(inflater, container, false);

        binding.include.txtTitleFragment.setText(R.string.pdf_manager);
        binding.include.getRoot().setBackgroundResource(R.color.pdf_color);
        binding.include.btnOption.setImageResource(R.drawable.ic_back);

        binding.include.btnOption.setOnClickListener(view -> {
            NavController navController= Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.popBackStack();
        });
        return binding.getRoot();
    }
}