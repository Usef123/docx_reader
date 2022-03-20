package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.DocumentViewModel.SORT_NAME;
import static com.prox.docxreader.DocumentViewModel.SORT_TIME_ACCESS;
import static com.prox.docxreader.DocumentViewModel.SORT_TIME_CREATE;
import static com.prox.docxreader.ui.activity.ReaderActivity.ACTION_FRAGMENT;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.prox.docxreader.adapter.DocumentHomeAdapter;
import com.prox.docxreader.DocumentViewModel;
import com.prox.docxreader.databinding.DialogDeleteBinding;
import com.prox.docxreader.databinding.DialogRenameBinding;
import com.prox.docxreader.databinding.DialogSortBinding;
import com.prox.docxreader.databinding.FragmentHomeBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding homeBinding;

    private DocumentViewModel viewModel;

    private DocumentHomeAdapter documentHomeAdapter;

    private int typeSort = SORT_NAME; //Kiểu sắp xếp

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerView();

        addSearchDocument();

        homeBinding.include.btnSort.setOnClickListener(view -> openDialogSort());

        viewModel = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);

        showDocuments();

        return homeBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        documentHomeAdapter = null;
        viewModel = null;
        homeBinding = null;
    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        homeBinding.recyclerViewHome.setLayoutManager(manager);

        documentHomeAdapter = new DocumentHomeAdapter(
                this::clickItemDocument,
                this::clickDelete,
                this::clickRename,
                this::clickShare,
                this::clickFavorite);

        homeBinding.recyclerViewHome.setAdapter(documentHomeAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        homeBinding.recyclerViewHome.addItemDecoration(dividerHorizontal);
    }

    private void clickItemDocument(Document document) {
        if (!(new File(document.getPath())).exists()) {
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

    private void clickDelete(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            viewModel.delete(document);
            return;
        }
        openDialogDelete(document);
    }

    private void clickRename(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
            viewModel.delete(document);
            return;
        }
        openDialogRename(document);
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
        setFavorite(document);
    }

    private void showDocuments() {
        String search = homeBinding.include.edtSearch.getText().toString().trim();
        if (search.isEmpty()){
            homeBinding.include.btnClear.setVisibility(View.GONE);
        }else{
            homeBinding.include.btnClear.setVisibility(View.VISIBLE);
        }

        viewModel.getDocuments(false, typeSort, search).observe(getViewLifecycleOwner(), documents -> {
            Log.d("viewmodel", "onChange");
            documentHomeAdapter.setDocuments(documents);
            if (documents.size()==0){
                homeBinding.notiList.setVisibility(View.VISIBLE);
            }else{
                homeBinding.notiList.setVisibility(View.GONE);
            }
        });
    }

    private void openDialogDelete(Document document) {
        DialogDeleteBinding dialogDeleteBinding = DialogDeleteBinding.inflate(getLayoutInflater());
        Dialog dialogDelete = createCustomDialog(dialogDeleteBinding.getRoot());
        dialogDelete.show();

        dialogDeleteBinding.btnOk.setOnClickListener(view -> {
            File file = new File(document.getPath());
            if (!file.exists()){
                Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
                viewModel.delete(document);
                dialogDelete.hide();
                return;
            }
            if (file.delete()){
                Toast.makeText(getContext(), R.string.notification_delete_success, Toast.LENGTH_SHORT).show();
                broadcastScanFile(file.getPath());
                viewModel.delete(document);
            }else{
                Toast.makeText(getContext(), R.string.notification_delete_error, Toast.LENGTH_SHORT).show();
            }
            dialogDelete.hide();
        });

        dialogDeleteBinding.btnCancel.setOnClickListener(view -> dialogDelete.hide());
    }

    private void openDialogRename(Document document) {
        DialogRenameBinding dialogRenameBinding = DialogRenameBinding.inflate(getLayoutInflater());
        Dialog dialogRename = createCustomDialog(dialogRenameBinding.getRoot());
        dialogRename.show();

        String titleFull = document.getTitle();         //Tên file có đuôi (.docx hoặc .doc)
        int dot = titleFull.lastIndexOf('.');       //Vị trí dấu . cuối cùng
        String title = titleFull.substring(0, dot);     //Tên file không có đuôi
        String type = titleFull.substring(dot);         //Đuôi file (.docx hoặc .doc)
        dialogRenameBinding.edtRename.setText(title);

        dialogRenameBinding.btnOk.setOnClickListener(view -> {
            String rename = dialogRenameBinding.edtRename.getText().toString().trim();
            //Tên để trống
            if (rename.isEmpty()) {
                Toast.makeText(getContext(), R.string.notification_rename_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            //Tên có thay đổi
            if (!rename.equals(title)){
                //File không tồn tại --> Báo lỗi, xoá file trong DB và RecyclerView
                File fileOld = new File(document.getPath());
                if (!fileOld.exists()){
                    Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
                    viewModel.delete(document);
                    dialogRename.hide();
                    return;
                }

                //Update tên và path document
                document.setTitle(rename.concat(type));
                String newPath = document.getPath().substring(0, document.getPath().length()-titleFull.length()).concat(document.getTitle());
                document.setPath(newPath);

                //Tên đã tồn tại
                File fileNew = new File(document.getPath());
                if (fileNew.exists()){
                    Toast.makeText(getContext(), R.string.notification_file_duplicate, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Đổi tên thành công
                if(fileOld.renameTo(new File(newPath))){
                    broadcastScanFile(fileNew.getPath());
                    broadcastScanFile(fileOld.getPath());
                    viewModel.update(document);
                    Toast.makeText(getContext(), R.string.notification_rename_success, Toast.LENGTH_SHORT).show();
                }else{ //Đổi tên thất bại
                    Toast.makeText(getContext(), R.string.notification_rename_error, Toast.LENGTH_SHORT).show();
                }
            }else{ //Tên không thay đổi
                Toast.makeText(getContext(), R.string.notification_not_rename, Toast.LENGTH_SHORT).show();
            }
            dialogRename.hide();
        });

        dialogRenameBinding.btnCancel.setOnClickListener(view -> dialogRename.hide());
    }

    private void shareDocument(Document document) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        String titleFull = document.getTitle();         //Tên file có đuôi (.docx hoặc .doc)
        int dot = titleFull.lastIndexOf('.');       //Vị trí dấu . cuối cùng
        String type = titleFull.substring(dot+1);         //Đuôi file (docx hoặc doc)

        intentShareFile.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://"+document.getPath()));

        startActivity(Intent.createChooser(intentShareFile, titleFull));
    }

    private void setFavorite(Document document) {
        document.setFavorite(!document.isFavorite());
        viewModel.update(document);
        if (document.isFavorite()){
            Toast.makeText(getContext(), R.string.notification_add_favorite, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), R.string.notification_remove_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    private void addSearchDocument() {
        homeBinding.include.edtSearch.addTextChangedListener(new TextWatcher() {
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

        homeBinding.include.btnClear.setOnClickListener(v -> homeBinding.include.edtSearch.setText(""));
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
            showDocuments();
            dialogSort.hide();
        });
        dialogSortBinding.sortTimeCreate.setOnClickListener(v -> {
            typeSort = SORT_TIME_CREATE;
            showDocuments();
            dialogSort.hide();
        });
        dialogSortBinding.sortTimeAccess.setOnClickListener(v -> {
            typeSort = SORT_TIME_ACCESS;
            showDocuments();
            dialogSort.hide();
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

    private void broadcastScanFile(String path) {
        Intent intentNotify = new Intent();
        int dot = path.lastIndexOf('.');    //Vị trí dấu . cuối cùng
        String type = path.substring(dot+1);   //Đuôi file (docx hoặc doc)
        intentNotify.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type));
        intentNotify.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentNotify.setData(Uri.fromFile(new File(path)));
        requireActivity().sendBroadcast(intentNotify);
    }
}