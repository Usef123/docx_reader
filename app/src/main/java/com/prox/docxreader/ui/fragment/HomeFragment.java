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

import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentHomeAdapter;
import com.prox.docxreader.databinding.DialogDeleteBinding;
import com.prox.docxreader.databinding.DialogRenameBinding;
import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.databinding.FragmentHomeBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;
import com.prox.docxreader.ui.dialog.DeleteDialog;
import com.prox.docxreader.ui.dialog.RenameDialog;
import com.prox.docxreader.ui.dialog.SortDialog;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.proxglobal.proxads.adsv2.ads.ProxAds;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

import java.io.File;
import java.util.Date;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DocumentViewModel model;
    private DocumentHomeAdapter adapter;

    public static int typeSort = SORT_NAME; //Kiểu sắp xếp

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "HomeFragment onCreateView");
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerView();

        model = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);

        addSearchDocument();

        binding.include.btnSort.setOnClickListener(view -> {
            SortDialog dialog = new SortDialog(
                    requireContext(),
                    DialogSortBinding.inflate(getLayoutInflater()),
                    false,
                    HomeFragment.typeSort,
                    this::showDocuments);
            dialog.show();
        });

        showDocuments();

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "HomeFragment onStop");
        binding.include.edtSearch.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment onDestroyView");
        adapter = null;
        model = null;
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerViewHome.setLayoutManager(manager);

        adapter = new DocumentHomeAdapter(
                this::clickItemDocument,
                this::clickDelete,
                this::clickRename,
                this::clickShare,
                this::clickFavorite);

        binding.recyclerViewHome.setAdapter(adapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        dividerHorizontal.setDrawable(getResources().getDrawable(R.drawable.line_custom));
        binding.recyclerViewHome.addItemDecoration(dividerHorizontal);
    }

    private void clickItemDocument(Document document) {
        if (!(new File(document.getPath())).exists()) {
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            model.delete(document);
            return;
        }
        //Update Time Access
        document.setTimeAccess(new Date().getTime());
        model.update(document);

        ProxAds.getInstance().showInterstitial(requireActivity(), "insite", new AdsCallback() {
            @Override
            public void onClosed() {
                super.onClosed();
                Log.d(TAG, "HomeFragment Ads onClosed");
                startReaderActivity(document);
            }

            @Override
            public void onError() {
                super.onError();
                Log.d(TAG, "HomeFragment Ads onError");
                startReaderActivity(document);
            }
        });
    }

    private void startReaderActivity(Document document) {
        Log.d(TAG, "HomeFragment startReaderActivity part: "+document.getPath());
        Intent intent = new Intent(requireActivity(), ReaderActivity.class);
        intent.putExtra(FILE_PATH, document.getPath());
        startActivity(intent);
    }

    private void clickDelete(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            model.delete(document);
            return;
        }
        DeleteDialog dialog = new DeleteDialog(
                requireContext(),
                DialogDeleteBinding.inflate(getLayoutInflater()),
                model,
                document);
        dialog.show();
    }

    private void clickRename(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            model.delete(document);
            return;
        }
        RenameDialog dialog = new RenameDialog(
                requireContext(),
                DialogRenameBinding.inflate(getLayoutInflater()),
                model,
                document);
        dialog.show();
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
        setFavorite(document);
    }

    private void showDocuments() {
        String search = binding.include.edtSearch.getText().toString().trim();
        if (search.isEmpty()){
            binding.include.btnClear.setVisibility(View.GONE);
        }else{
            binding.include.btnClear.setVisibility(View.VISIBLE);
        }
        model.getDocuments(false, typeSort, search).observe(getViewLifecycleOwner(), documents -> {adapter.setDocuments(documents);
            Log.d(TAG, "HomeFragment document.size()="+documents.size());
            if (documents.size()==0){
                binding.notiList.setVisibility(View.VISIBLE);
            }else{
                binding.notiList.setVisibility(View.GONE);
            }
        });
    }

    private void setFavorite(Document document) {
        document.setFavorite(!document.isFavorite());
        model.update(document);
        if (document.isFavorite()){
            Toast.makeText(getContext(), R.string.notification_add_favorite, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), R.string.notification_remove_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    private void addSearchDocument() {
        binding.include.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showDocuments();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.include.btnClear.setOnClickListener(v -> binding.include.edtSearch.setText(""));
    }
}