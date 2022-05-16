package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.DocxReaderApp.TAG;
import static com.prox.docxreader.repository.DocumentRepository.SORT_NAME;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentFavoriteAdapter;
import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.databinding.FragmentFavoriteBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;
import com.prox.docxreader.ui.dialog.SortDialog;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

import java.io.File;
import java.util.Date;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private DocumentViewModel model;
    private DocumentFavoriteAdapter adapter;

    public static int typeSort = SORT_NAME; //Kiểu sắp xếp

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "FavoriteFragment onCreateView");
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        binding.include.txtTitleFragment.setText(getResources().getString(R.string.title_favorite));

        setupRecyclerView();

        model = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);

        addSearchDocument();

        binding.include.btnSort.setOnClickListener(view -> {
            SortDialog dialog = new SortDialog(
                    requireContext(),
                    DialogSortBinding.inflate(getLayoutInflater()),
                    false,
                    FavoriteFragment.typeSort,
                    this::showDocumentsFavorite);
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

        model.getDocuments(true, typeSort, search).observe(getViewLifecycleOwner(), documents -> {
            Log.d(TAG, "FavoriteFragment document.size()="+documents.size());
            adapter.setDocuments(documents);
            if (documents.size()==0){
                binding.notiList.setVisibility(View.VISIBLE);
            }else{
                binding.notiList.setVisibility(View.GONE);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeFavorite(Document document) {
        document.setFavorite(false);
        model.update(document);
        Toast.makeText(getContext(), R.string.notification_remove_favorite, Toast.LENGTH_SHORT).show();
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