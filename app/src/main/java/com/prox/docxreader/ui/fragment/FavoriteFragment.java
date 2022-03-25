package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.viewmodel.DocumentViewModel.SORT_NAME;
import static com.prox.docxreader.viewmodel.DocumentViewModel.SORT_TIME_ACCESS;
import static com.prox.docxreader.viewmodel.DocumentViewModel.SORT_TIME_CREATE;
import static com.prox.docxreader.ui.activity.ReaderActivity.ACTION_FRAGMENT;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentFavoriteAdapter;
import com.prox.docxreader.viewmodel.DocumentViewModel;
import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.databinding.FragmentFavoriteBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;

import java.io.File;
import java.util.Date;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding favoriteBinding;

    private DocumentViewModel viewModel;

    private DocumentFavoriteAdapter documentFavoriteAdapter;

    private int typeSort = SORT_NAME; //Kiểu sắp xếp

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        favoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false);

        favoriteBinding.include.txtTitleFragment.setText(getResources().getString(R.string.title_favorite));

        setupRecyclerView();

        addSearchDocument();

        favoriteBinding.include.btnSort.setOnClickListener(view -> openDialogSort());

        viewModel = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);

        showDocumentsFavorite();

        return favoriteBinding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        favoriteBinding.include.edtSearch.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        documentFavoriteAdapter = null;
        viewModel = null;
        favoriteBinding = null;
    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        favoriteBinding.recyclerViewFavorite.setLayoutManager(manager);

        documentFavoriteAdapter = new DocumentFavoriteAdapter(
                this::clickItemDocument,
                this::clickShare,
                this::clickFavorite);

        favoriteBinding.recyclerViewFavorite.setAdapter(documentFavoriteAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        dividerHorizontal.setDrawable(getResources().getDrawable(R.drawable.line_custom));
        favoriteBinding.recyclerViewFavorite.addItemDecoration(dividerHorizontal);
    }

    private void clickItemDocument(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            viewModel.delete(document);
            return;
        }
        //Update Time Access
        document.setTimeAccess(new Date().getTime());
        viewModel.update(document);

        Intent intent = new Intent(getActivity(), ReaderActivity.class);
        intent.putExtra(FILE_PATH, document.getPath());
        intent.setAction(ACTION_FRAGMENT);
        startActivity(intent);
    }

    private void clickShare(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            viewModel.delete(document);
            return;
        }
        shareDocument(document);
    }

    private void clickFavorite(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            viewModel.delete(document);
            return;
        }
        removeFavorite(document);
    }

    private void showDocumentsFavorite() {
        String search = favoriteBinding.include.edtSearch.getText().toString().trim();
        if (search.isEmpty()){
            favoriteBinding.include.btnClear.setVisibility(View.GONE);
        }else{
            favoriteBinding.include.btnClear.setVisibility(View.VISIBLE);
        }
        viewModel.getDocuments(true, typeSort, search).observe(getViewLifecycleOwner(), documents -> {
            Log.d("viewmodel", "onChange");
            documentFavoriteAdapter.setDocuments(documents);
            if (documents.size()==0){
                favoriteBinding.notiList.setVisibility(View.VISIBLE);
            }else{
                favoriteBinding.notiList.setVisibility(View.GONE);
            }
        });
    }

    private void shareDocument(Document document) {
        File file = new File(document.getPath());
        Uri uri = FileProvider.getUriForFile(requireContext(), "com.prox.docxreader.fileprovider", file);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        String titleFull = document.getTitle();         //Tên file có đuôi (.docx hoặc .doc)
        int dot = titleFull.lastIndexOf('.');       //Vị trí dấu . cuối cùng
        String type = titleFull.substring(dot+1);         //Đuôi file (docx hoặc doc)

        intentShareFile.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intentShareFile, titleFull);

        List<ResolveInfo> resInfoList = requireContext().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            requireContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(chooser);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeFavorite(Document document) {
        document.setFavorite(false);
        viewModel.update(document);
        Toast.makeText(getContext(), R.string.notification_remove_favorite, Toast.LENGTH_SHORT).show();
    }

    private void addSearchDocument() {
        favoriteBinding.include.edtSearch.addTextChangedListener(new TextWatcher() {
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

        favoriteBinding.include.btnClear.setOnClickListener(v -> favoriteBinding.include.edtSearch.setText(""));
    }

    private void openDialogSort() {
        DialogSortBinding dialogSortBinding = DialogSortBinding.inflate(getLayoutInflater());
        Dialog dialogSort = createCustomDialog(dialogSortBinding.getRoot());
        dialogSort.show();

        switch (typeSort){
            case SORT_NAME:
                dialogSortBinding.nameChecked.setVisibility(View.VISIBLE);
                dialogSortBinding.timeCreateChecked.setVisibility(View.INVISIBLE);
                dialogSortBinding.timeAccessChecked.setVisibility(View.INVISIBLE);
                break;
            case SORT_TIME_CREATE:
                dialogSortBinding.nameChecked.setVisibility(View.INVISIBLE);
                dialogSortBinding.timeCreateChecked.setVisibility(View.VISIBLE);
                dialogSortBinding.timeAccessChecked.setVisibility(View.INVISIBLE);
                break;
            case SORT_TIME_ACCESS:
                dialogSortBinding.nameChecked.setVisibility(View.INVISIBLE);
                dialogSortBinding.timeCreateChecked.setVisibility(View.INVISIBLE);
                dialogSortBinding.timeAccessChecked.setVisibility(View.VISIBLE);
                break;
        }

        dialogSortBinding.sortName.setOnClickListener(v -> {
            typeSort = SORT_NAME;
            showDocumentsFavorite();
            dialogSort.cancel();
        });
        dialogSortBinding.sortTimeCreate.setOnClickListener(v -> {
            typeSort = SORT_TIME_CREATE;
            showDocumentsFavorite();
            dialogSort.cancel();
        });
        dialogSortBinding.sortTimeAccess.setOnClickListener(v -> {
            typeSort = SORT_TIME_ACCESS;
            showDocumentsFavorite();
            dialogSort.cancel();
        });
    }

    private Dialog createCustomDialog(View layout) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        return dialog;
    }
}