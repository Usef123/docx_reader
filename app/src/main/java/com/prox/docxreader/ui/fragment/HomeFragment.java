package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.ui.activity.ReaderActivity.ACTION_FRAGMENT;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentHomeAdapter;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;

import java.io.File;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private DocumentHomeAdapter documentHomeAdapter;
    private List<Document> documents;
    private EditText edtSearch;

    private static final int SORT_NAME = 1;
    private static final int SORT_TIME_CREATE = 2;
    private static final int SORT_TIME_ACCESS = 3;
    private int typeSort; //Kiểu sắp xếp

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        edtSearch = view.findViewById(R.id.edt_search);

        typeSort = SORT_NAME; //Sắp xếp theo tên

        setupRecyclerView();

        addBtnSort();

        addSearchDocument();

        return view;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_home);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        documentHomeAdapter = new DocumentHomeAdapter(
                this::clickItemDocument,
                this::clickDelete,
                this::clickRename,
                this::clickShare,
                this::clickFavorite);

        recyclerView.setAdapter(documentHomeAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerHorizontal);

        showDocuments();
    }

    private void clickItemDocument(Document document) {
        if (!(new File(document.getPath())).exists()) {
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            documents.remove(document);
            documentHomeAdapter.setDocuments(documents);
            return;
        }
        //Update Time Access
        document.setTimeAccess(new Date().getTime());
        DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);

        Intent intent = new Intent(getActivity(), ReaderActivity.class);
        intent.putExtra(FILE_PATH, document.getPath());
        intent.setAction(ACTION_FRAGMENT);
        startActivity(intent);
    }

    private void clickDelete(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            documents.remove(document);
            documentHomeAdapter.setDocuments(documents);
            return;
        }
        openDialogDelete(document);
    }

    private void clickRename(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            documents.remove(document);
            documentHomeAdapter.setDocuments(documents);
            return;
        }
        openDialogRename(document);
    }

    private void clickShare(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            documents.remove(document);
            documentHomeAdapter.setDocuments(documents);
            return;
        }
        shareDocument(document);
    }

    private void clickFavorite(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            documents.remove(document);
            documentHomeAdapter.setDocuments(documents);
            return;
        }
        setFavorite(document);
    }

    private void showDocuments() {
        String search = edtSearch.getText().toString().trim();
        switch (typeSort){
            case SORT_NAME:
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByName(search);
                break;
            case SORT_TIME_CREATE:
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByTimeCreate(search);
                break;
            case SORT_TIME_ACCESS:
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByTimeAccess(search);
                break;
        }
        documentHomeAdapter.setDocuments(documents);
    }

    private void openDialogDelete(Document document) {
        Dialog dialogDelete = createCustomDialog(R.layout.dialog_delete);
        dialogDelete.show();

        Button btnYes, btnNo;
        btnYes = dialogDelete.findViewById(R.id.btn_ok);
        btnNo = dialogDelete.findViewById(R.id.btn_cancel);

        btnYes.setOnClickListener(view -> {
            File file = new File(document.getPath());
            if (!file.exists()){
                Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
                dialogDelete.hide();
                documents.remove(document);
                documentHomeAdapter.setDocuments(documents);
                return;
            }
            if (file.delete()){
                DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
                Toast.makeText(getContext(), getResources().getString(R.string.notification_delete_success), Toast.LENGTH_SHORT).show();
                dialogDelete.hide();
                documents.remove(document);
                documentHomeAdapter.setDocuments(documents);
            }else{
                Toast.makeText(getContext(), getResources().getString(R.string.notification_delete_error), Toast.LENGTH_SHORT).show();
                dialogDelete.hide();
            }
        });

        btnNo.setOnClickListener(view -> dialogDelete.hide());
    }

    private void openDialogRename(Document document) {
        Dialog dialogRename = createCustomDialog(R.layout.dialog_rename);
        dialogRename.show();

        Button btnOK, btnCancel;
        btnOK = dialogRename.findViewById(R.id.btn_ok);
        btnCancel = dialogRename.findViewById(R.id.btn_cancel);

        EditText editText = dialogRename.findViewById(R.id.edt_rename);

        String titleFull = document.getTitle();         //Tên file có đuôi (.docx hoặc .doc)
        int dot = titleFull.lastIndexOf('.');       //Vị trí dấu . cuối cùng
        String title = titleFull.substring(0, dot);     //Tên file không có đuôi
        String type = titleFull.substring(dot);         //Đuôi file (.docx hoặc .doc)
        editText.setText(title);

        btnOK.setOnClickListener(view -> {
            String rename = editText.getText().toString().trim();
            //Tên để trống
            if (rename.isEmpty()) {
                Toast.makeText(getContext(), getResources().getString(R.string.notification_rename_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            //Tên có thay đổi
            if (!rename.equals(title)){
                //File không tồn tại --> Báo lỗi, xoá file trong DB và RecyclerView
                File fileOld = new File(document.getPath());
                if (!fileOld.exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
                    dialogRename.hide();
                    documents.remove(document);
                    documentHomeAdapter.setDocuments(documents);
                    return;
                }

                //Update tên và path document
                document.setTitle(rename.concat(type));
                String newPath = document.getPath().substring(0, document.getPath().length()-titleFull.length()).concat(document.getTitle());
                document.setPath(newPath);

                //Tên đã tồn tại
                File fileNew = new File(document.getPath());
                if (fileNew.exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_duplicate), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Đổi tên thành công
                if(fileOld.renameTo(new File(newPath))){
                    DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_rename_success), Toast.LENGTH_SHORT).show();
                    dialogRename.hide();
                    showDocuments();
                }else{ //Đổi tên thất bại
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_rename_error), Toast.LENGTH_SHORT).show();
                    dialogRename.hide();
                }
            }else{ //Tên không thay đổi
                Toast.makeText(getContext(), getResources().getString(R.string.notification_not_rename), Toast.LENGTH_SHORT).show();
                dialogRename.hide();
            }
        });

        btnCancel.setOnClickListener(view -> dialogRename.hide());
    }

    private void shareDocument(Document document) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        String titleFull = document.getTitle();         //Tên file có đuôi (.docx hoặc .doc)
        int dot = titleFull.lastIndexOf('.');       //Vị trí dấu . cuối cùng
        String type = titleFull.substring(dot+1);         //Đuôi file (docx hoặc doc)

        intentShareFile.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+document.getPath()));

        startActivity(Intent.createChooser(intentShareFile, titleFull));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setFavorite(Document document) {
        document.setFavorite(!document.isFavorite());
        DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);
        int index = documents.indexOf(document);
        if (index != -1){
            documents.get(index).setFavorite(document.isFavorite());
            documentHomeAdapter.notifyDataSetChanged();
        }
        if (document.isFavorite()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_add_favorite), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), getResources().getString(R.string.notification_remove_favorite), Toast.LENGTH_SHORT).show();
        }
    }

    private void addSearchDocument() {
        edtSearch.addTextChangedListener(new TextWatcher() {
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

    }

    private void addBtnSort() {
        ImageButton btnSort = view.findViewById(R.id.btn_sort);
        btnSort.setOnClickListener(view -> openDialogSort());
    }

    private void openDialogSort() {
        Dialog dialogSort = createCustomDialog(R.layout.dialog_sort);
        dialogSort.show();

        LinearLayout sortName, sortTimeCreate, sortTimeAccess;
        sortName = dialogSort.findViewById(R.id.sort_name);
        sortTimeCreate = dialogSort.findViewById(R.id.sort_time_create);
        sortTimeAccess = dialogSort.findViewById(R.id.sort_time_access);

        ImageView nameChecked, timeCreateChecked, timeAccessChecked;
        nameChecked = dialogSort.findViewById(R.id.name_checked);
        timeCreateChecked = dialogSort.findViewById(R.id.time_create_checked);
        timeAccessChecked = dialogSort.findViewById(R.id.time_access_checked);
        switch (typeSort){
            case SORT_NAME:
                nameChecked.setVisibility(View.VISIBLE);
                timeCreateChecked.setVisibility(View.INVISIBLE);
                timeAccessChecked.setVisibility(View.INVISIBLE);
                break;
            case SORT_TIME_CREATE:
                nameChecked.setVisibility(View.INVISIBLE);
                timeCreateChecked.setVisibility(View.VISIBLE);
                timeAccessChecked.setVisibility(View.INVISIBLE);
                break;
            case SORT_TIME_ACCESS:
                nameChecked.setVisibility(View.INVISIBLE);
                timeCreateChecked.setVisibility(View.INVISIBLE);
                timeAccessChecked.setVisibility(View.VISIBLE);
                break;
        }

        sortName.setOnClickListener(v -> {
            typeSort = SORT_NAME;
            showDocuments();
            dialogSort.hide();
        });
        sortTimeCreate.setOnClickListener(v -> {
            typeSort = SORT_TIME_CREATE;
            showDocuments();
            dialogSort.hide();
        });
        sortTimeAccess.setOnClickListener(v -> {
            typeSort = SORT_TIME_ACCESS;
            showDocuments();
            dialogSort.hide();
        });
    }

    private Dialog createCustomDialog(int layout) {
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