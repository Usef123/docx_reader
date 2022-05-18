package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.DocxReaderApp.TAG;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;
import static com.prox.docxreader.ui.activity.SplashActivity.OPEN_OUTSIDE;
import static com.prox.docxreader.ui.dialog.SortDialog.FRAGMENT_PDF;
import static com.prox.docxreader.ui.dialog.SortDialog.SORT_NAME;
import static com.prox.docxreader.ui.dialog.SortDialog.SORT_TIME_ACCESS;
import static com.prox.docxreader.ui.dialog.SortDialog.SORT_TIME_CREATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prox.docxreader.DocxReaderApp;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentPDFAdapter;
import com.prox.docxreader.databinding.DialogDeleteBinding;
import com.prox.docxreader.databinding.DialogRenameBinding;
import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.databinding.FragmentPdfBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;
import com.prox.docxreader.ui.dialog.DeleteDialog;
import com.prox.docxreader.ui.dialog.RenameDialog;
import com.prox.docxreader.ui.dialog.SortDialog;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.proxglobal.proxads.adsv2.callback.AdsCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PDFFragment extends Fragment {
    private FragmentPdfBinding binding;
    private DocumentViewModel model;
    private DocumentPDFAdapter adapter;

    public static int typeSort = SORT_NAME; //Kiểu sắp xếp

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPdfBinding.inflate(inflater, container, false);

        binding.include.txtTitleFragment.setText(R.string.pdf_manager);
        binding.include.getRoot().setBackgroundResource(R.color.pdf_color);
        binding.include.btnOption.setImageResource(R.drawable.ic_back);
        binding.include.btnOption.setBackgroundResource(R.drawable.img_button_pdf_selector);
        binding.include.btnSort.setBackgroundResource(R.drawable.img_button_pdf_selector);

        setupRecyclerView();

        model = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);
        model.getPDF().observe(getViewLifecycleOwner(), documents -> {
            if (documents == null){
                documents = new ArrayList<>();
            }
            if (typeSort == SORT_NAME){
                documents.sort(Comparator.comparing(Document::getTitle));
            }else if (typeSort == SORT_TIME_CREATE){
                documents.sort((document1, document2) ->
                        (int) (document2.getTimeCreate() - document1.getTimeCreate()));
            }else if (typeSort == SORT_TIME_ACCESS){
                documents.sort((document1, document2) ->
                        (int) (document2.getTimeAccess() - document1.getTimeAccess()));
            }

            List<Document> documentsSearch = new ArrayList<>();
            for (Document document : documents){
                if (document.getTitle().contains(binding.include.edtSearch.getText().toString().trim())){
                    documentsSearch.add(document);
                }
            }

            adapter.setDocuments(documentsSearch);
            Log.d(TAG, "PDFFragment document.size()="+documentsSearch.size());
            if (documentsSearch.size()==0){
                binding.notiList.setVisibility(View.VISIBLE);
            }else{
                binding.notiList.setVisibility(View.GONE);
            }
        });

        addSearchDocument();

        binding.include.btnOption.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.popBackStack();
        });

        binding.include.btnSort.setOnClickListener(view -> {
            SortDialog dialog = new SortDialog(
                    requireContext(),
                    DialogSortBinding.inflate(getLayoutInflater()),
                    model,
                    FRAGMENT_PDF,
                    PDFFragment.typeSort);
            dialog.show();
        });

        showDocuments();

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "PDFFragment onStop");
        binding.include.edtSearch.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "PDFFragment onDestroyView");
        adapter = null;
        model = null;
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerViewHome.setLayoutManager(manager);

        adapter = new DocumentPDFAdapter(
                this::clickItemDocument,
                this::clickDelete,
                this::clickRename,
                this::clickShare);

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

        DocxReaderApp.instance.showInterstitial(requireActivity(), "insite", new AdsCallback() {
            @Override
            public void onClosed() {
                super.onClosed();
                Log.d(TAG, "PDFFragment Ads onClosed");
                startReaderActivity(document);
            }

            @Override
            public void onError() {
                super.onError();
                Log.d(TAG, "PDFFragment Ads onError");
                startReaderActivity(document);
            }
        });
    }

    private void startReaderActivity(Document document) {
        Log.d(TAG, "PDFFragment startReaderActivity part: " + document.getPath());
        Intent intent = new Intent(requireActivity(), ReaderActivity.class);
        intent.putExtra(FILE_PATH, document.getPath());
        intent.putExtra(OPEN_OUTSIDE, false);
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

    private void showDocuments() {
        String search = binding.include.edtSearch.getText().toString().trim();
        if (search.isEmpty()){
            binding.include.btnClear.setVisibility(View.GONE);
        }else{
            binding.include.btnClear.setVisibility(View.VISIBLE);
        }

        model.setValue();
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