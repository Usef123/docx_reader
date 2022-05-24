package com.prox.docxreader.ui.activity;

import static com.prox.docxreader.DocxReaderApp.TAG;
import static com.prox.docxreader.ui.fragment.SettingFragment.URI_POLICY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.prox.docxreader.BuildConfig;
import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.ActivityIapBinding;
import com.proxglobal.purchase.ProxPurchase;
import com.proxglobal.purchase.function.PurchaseListioner;

public class IAPActivity extends AppCompatActivity {
    public static final String PURCHASE = "PURCHASE";
    private String typeSub = BuildConfig.id_subs_year;

    private ActivityIapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        binding.btnClose.setOnClickListener(view -> finish());
        binding.txtPrivatePolicy.setOnClickListener(view ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_POLICY))));

        binding.btnPriceMonth.getRoot().setOnClickListener(view -> {
            binding.btnPriceMonth.select.setVisibility(View.VISIBLE);
            binding.btnPriceYear.select.setVisibility(View.INVISIBLE);
            typeSub = BuildConfig.id_subs_month;
        });

        binding.btnPriceYear.getRoot().setOnClickListener(view -> {
            binding.btnPriceMonth.select.setVisibility(View.INVISIBLE);
            binding.btnPriceYear.select.setVisibility(View.VISIBLE);
            typeSub = BuildConfig.id_subs_year;
        });

        binding.btnContinue.setOnClickListener(view -> {
            DocxReaderApp.purchase.subscribe(this, typeSub);
        });

        DocxReaderApp.purchase.setPurchaseListioner(new PurchaseListioner() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                Log.d(TAG, "onProductPurchased: id("+productId+"), transactionDetails("+transactionDetails+")");
                Intent intent = new Intent(IAPActivity.this, MainActivity.class);
                intent.setAction(PURCHASE);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Log.d(TAG, "displayErrorMessage: "+errorMsg);
            }

            @Override
            public void onUserCancelBilling() {
                Log.d(TAG, "onUserCancelBilling");
            }
        });
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    private void init(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding.txtPrivatePolicy.setPaintFlags(binding.txtPrivatePolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.btnPriceMonth.select.setVisibility(View.INVISIBLE);

        String currencyMonth = DocxReaderApp.purchase.getCurrency(BuildConfig.id_subs_month, ProxPurchase.TYPE_IAP.SUBSCRIPTION);
        String currencyYear = DocxReaderApp.purchase.getCurrency(BuildConfig.id_subs_month, ProxPurchase.TYPE_IAP.SUBSCRIPTION);

        long priceNewMonth = (long) (ProxPurchase.getInstance().getPriceWithoutCurrency(BuildConfig.id_subs_month, ProxPurchase.TYPE_IAP.SUBSCRIPTION) / 1000000);
        long priceOldMonth = priceNewMonth*3;

        long priceNewYear = (long) (ProxPurchase.getInstance().getPriceWithoutCurrency(BuildConfig.id_subs_year, ProxPurchase.TYPE_IAP.SUBSCRIPTION) / 1000000);
        long priceOldYear = priceNewYear*3;

        binding.btnPriceMonth.txtPrice1.setText(priceNewMonth + " " + currencyMonth);
        binding.btnPriceMonth.txtPrice2.setText(R.string.per_month);
        binding.btnPriceMonth.txtPrice3.setText(priceOldMonth + " " +  currencyMonth + getResources().getString(R.string.month));

        binding.btnPriceYear.txtPrice1.setText(priceNewYear + " " +  currencyYear);
        binding.btnPriceYear.txtPrice2.setText(R.string.per_year);
        binding.btnPriceYear.txtPrice3.setText(priceOldYear + " " +  currencyYear + getResources().getString(R.string.year));
    }
}