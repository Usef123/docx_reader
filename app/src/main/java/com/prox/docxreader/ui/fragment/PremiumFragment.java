package com.prox.docxreader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.FragmentPremiumBinding;
import com.prox.docxreader.ui.activity.MainActivity;
import com.prox.docxreader.utils.LanguageUtils;
import com.proxglobal.purchase.ProxPurchase;
import com.proxglobal.purchase.function.PurchaseListioner;

public class PremiumFragment extends Fragment {
    public static final String PURCHASE = "PURCHASE";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPremiumBinding binding = FragmentPremiumBinding.inflate(inflater, container, false);

        LanguageUtils.loadLanguage(requireContext());

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        requireActivity().getWindow().setStatusBarColor(this.getResources().getColor(R.color.doc_color));

        binding.btnClose.setOnClickListener(v -> requireActivity().onBackPressed());
        String bestPrice = getResources().getString(R.string.best_price) + " " + ProxPurchase.getInstance().getPriceSub(BuildConfig.id_subs);
        binding.txtBestPrice.setText(bestPrice);
        binding.btnContinue.setOnClickListener(v -> {
            ProxPurchase.getInstance().subscribe(requireActivity(), BuildConfig.id_subs);
            ProxPurchase.getInstance().setPurchaseListioner(new PurchaseListioner() {
                @Override
                public void onProductPurchased(String productId, String transactionDetails) {
                    Log.d("PurchaseListioner", "onProductPurchased: id("+productId+"), transactionDetails("+transactionDetails+")");
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.setAction(PURCHASE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void displayErrorMessage(String errorMsg) {
                    Log.d("PurchaseListioner", "displayErrorMessage: "+errorMsg);
                }

                @Override
                public void onUserCancelBilling() {
                    Log.d("PurchaseListioner", "onUserCancelBilling");
                }
            });
        });
        return binding.getRoot();
    }
}