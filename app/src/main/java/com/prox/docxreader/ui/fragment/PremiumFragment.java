package com.prox.docxreader.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.FragmentPremiumBinding;
import com.proxglobal.purchase.ProxPurchase;

public class PremiumFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPremiumBinding binding = FragmentPremiumBinding.inflate(inflater, container, false);

        binding.btnClose.setOnClickListener(v -> requireActivity().onBackPressed());
        String bestPrice = getResources().getString(R.string.best_price) + ProxPurchase.getInstance().getPrice(BuildConfig.purchase);
        binding.txtBestPrice.setText(bestPrice);
        binding.btnContinue.setOnClickListener(v -> ProxPurchase.getInstance().purchase(requireActivity(), BuildConfig.purchase));
        return binding.getRoot();
    }
}