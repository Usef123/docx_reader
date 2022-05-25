package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.DocxReaderApp.TAG;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;
import static com.prox.docxreader.ui.activity.SplashActivity.OPEN_OUTSIDE;
import static com.prox.docxreader.ui.dialog.SortDialog.FRAGMENT_FAVORITE;
import static com.prox.docxreader.ui.dialog.SortDialog.SORT_NAME;
import static com.prox.docxreader.ui.dialog.SortDialog.SORT_TIME_ACCESS;
import static com.prox.docxreader.ui.dialog.SortDialog.SORT_TIME_CREATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentFavoriteAdapter;
import com.prox.docxreader.databinding.DialogOptionBinding;
import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.databinding.FragmentFavoriteBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;
import com.prox.docxreader.ui.dialog.OptionDialog;
import com.prox.docxreader.ui.dialog.SortDialog;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.utils.LanguageUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private DocumentViewModel model;
    private DocumentFavoriteAdapter adapter;

    public static int typeSort = SORT_NAME; //Kiểu sắp xếp

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "FavoriteFragment onCreateView");
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);

        LanguageUtils.loadLanguage(requireContext());

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        requireActivity().getWindow().setStatusBarColor(this.getResources().getColor(R.color.doc_color));

        binding.include.txtTitleFragment.setText(getResources().getString(R.string.title_favorite));

        setupRecyclerView();

        model = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);
        model.getDOCXFavorite().observe(getViewLifecycleOwner(), documents -> {
            if (documents == null){
                documents = new ArrayList<>();
            }
            if (typeSort == SORT_NAME){
                documents.sort(Comparator.comparing(Document::getTitle));
            }else if (typeSort == SORT_TIME_CREATE){
                documents.sort((document1, document2) -> Long.compare(document2.getTimeCreate(), document1.getTimeCreate()));
            }else if (typeSort == SORT_TIME_ACCESS){
                documents.sort((document1, document2) -> Long.compare(document2.getTimeAccess(), document1.getTimeAccess()));
            }

            List<Document> documentsSearch = new ArrayList<>();
            for (Document document : documents){
                if (document.getTitle().contains(binding.include.edtSearch.getText().toString().trim())){
                    documentsSearch.add(document);
                }
            }

            adapter.setDocuments(documentsSearch);
            Log.d(TAG, "FavoriteFragment document.size()="+documentsSearch.size());
            if (documentsSearch.size()==0){
                binding.notiList.setVisibility(View.VISIBLE);
            }else{
                binding.notiList.setVisibility(View.GONE);
            }
        });

        addSearchDocument();

        binding.include.btnOption.setOnClickListener(view -> {
            OptionDialog dialog = new OptionDialog(
                    requireContext(),
                    requireActivity(),
                    DialogOptionBinding.inflate(getLayoutInflater()));

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            int choose_menu = preferences.getInt("choose_menu", 1);
            Log.d("choose_menu", String.valueOf(choose_menu));
            if (choose_menu % 2 == 0) {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                DocxReaderApp.instance.showInterstitial(requireActivity(), "menu", new AdsCallback() {
                    @Override
                    public void onClosed() {
                        super.onClosed();
                        Log.d(TAG, "FavoriteFragment Ads onClosed");
                        dialog.show();
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        Log.d(TAG, "FavoriteFragment Ads onError");
                        dialog.show();
                    }
                });
            }else {
                preferences.edit().putInt("choose_menu", choose_menu + 1).apply();
                dialog.show();
            }
        });

        binding.include.btnSort.setOnClickListener(view -> {
            SortDialog dialog = new SortDialog(
                    requireContext(),
                    DialogSortBinding.inflate(getLayoutInflater()),
                    model,
                    FRAGMENT_FAVORITE,
                    FavoriteFragment.typeSort);
            dialog.show();
        });

        showDocumentsFavorite();

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "FavoriteFragment onStop");
        binding.include.edtSearch.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "FavoriteFragment onDestroyView");
        adapter = null;
        model = null;
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerViewFavorite.setLayoutManager(manager);

        adapter = new DocumentFavoriteAdapter(
                this::clickItemDocument,
                this::clickShare,
                this::clickFavorite);

        binding.recyclerViewFavorite.setAdapter(adapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        dividerHorizontal.setDrawable(getResources().getDrawable(R.drawable.line_custom));
        binding.recyclerViewFavorite.addItemDecoration(dividerHorizontal);
    }

    private void clickItemDocument(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            model.delete(document);
            return;
        }
        //Update Time Access
        document.setTimeAccess(new Date().getTime());
        model.update(document);

        DocxReaderApp.instance.showInterstitial(requireActivity(), "insite", new AdsCallback() {
            @Override
            public void onClosed() {
                super.onClosed();
                Log.d(TAG, "FavoriteFragment Ads onClosed");
                startReaderActivity(document);
            }

            @Override
            public void onError() {
                super.onError();
                Log.d(TAG, "FavoriteFragment Ads onError");
                startReaderActivity(document);
            }
        });
    }

    private void startReaderActivity(Document document) {
        Log.d(TAG, "FavoriteFragment startReaderActivity part: "+document.getPath());
        Intent intent = new Intent(requireActivity(), ReaderActivity.class);
        intent.putExtra(FILE_PATH, document.getPath());
        intent.putExtra(OPEN_OUTSIDE, false);
        startActivity(intent);
    }

    private void clickShare(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            model.delete(document);
            return;
        }
        FileUtils.shareFile(requireContext(), document.getPath());
    }

    private void clickFavorite(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            model.delete(document);
            return;
        }
        removeFavorite(document);
    }

    private void showDocumentsFavorite() {
        String search = binding.include.edtSearch.getText().toString().trim();
        if (search.isEmpty()){
            binding.include.btnClear.setVisibility(View.GONE);
        }else{
            binding.include.btnClear.setVisibility(View.VISIBLE);
        }

        model.setValue();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeFavorite(Document document) {
        document.setFavorite(false);
        model.update(document);
        Snackbar.make(binding.getRoot(), R.string.notification_remove_favorite, Snackbar.LENGTH_SHORT).show();
    }

    private void addSearchDocument() {
        binding.include.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showDocumentsFavorite();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.include.btnClear.setOnClickListener(v -> binding.include.edtSearch.setText(""));
    }
}