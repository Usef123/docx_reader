package com.prox.docxreader.ui.fragment;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickMoreListener;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentAdapter;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.MainActivity;
import com.wxiwei.office.constant.MainConstant;
import com.wxiwei.office.officereader.AppActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private DocumentAdapter documentAdapter;
    private List<Document> documents;
    private EditText edtSearch;

    private static final int SORT_NAME = 1;
    private static final int SORT_TIME_CREATE = 2;
    private static final int SORT_TIME_ACCESS = 3;
    private int typeSort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        edtSearch = view.findViewById(R.id.edt_search);
        typeSort = SORT_NAME;

        setupRecyclerView();

        addBtnSort();

        addSearchDocument();

        return view;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_home);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        documents = new ArrayList<>();

        documentAdapter = new DocumentAdapter(documents, new OnClickItemDocumentListener() {
            @Override
            public void onClickItemDocument(Document document) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    showDocuments();
                    return;
                }
                //Update Time Access
                document.setTimeAccess(new Date().getTime());
                DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);

//                Intent intent = new Intent(getActivity(), AppActivity.class);
//                intent.putExtra(MainConstant.INTENT_FILED_FILE_PATH, document.getPath());
//                startActivity(intent);
            }
        }, new OnClickMoreListener() {
            @Override
            public void onClickMore(Document document) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    showDocuments();
                    return;
                }
                openDialogMore(document);
            }
        });

        recyclerView.setAdapter(documentAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerHorizontal);

        showDocuments();
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
        documentAdapter.setDocuments(documents);
    }

    private void openDialogMore(Document document) {
        Dialog dialogMore = createCustomDialog(R.layout.dialog_more_home);
        dialogMore.show();

        Button btnDelete, btnRename, btnShare, btnFavorite;
        btnDelete = dialogMore.findViewById(R.id.btn_delete);
        btnRename = dialogMore.findViewById(R.id.btn_rename);
        btnShare = dialogMore.findViewById(R.id.btn_share);
        btnFavorite = dialogMore.findViewById(R.id.btn_favorite);

        TextView txtTitle = dialogMore.findViewById(R.id.txt_title);
        txtTitle.setText(document.getTitle());

        if (document.isFavorite()){
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite,0,0,0);
        }else{
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_fill,0,0,0);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    dialogMore.hide();
                    showDocuments();
                    return;
                }
                openDialogDelete(document);
                dialogMore.hide();
            }
        });

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    dialogMore.hide();
                    showDocuments();
                    return;
                }
                openDialogRename(document);
                dialogMore.hide();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    dialogMore.hide();
                    showDocuments();
                    return;
                }
                shareDocument(document);
                dialogMore.hide();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    dialogMore.hide();
                    showDocuments();
                    return;
                }
                setFavorite(document);
                dialogMore.hide();
            }
        });
    }

    private void openDialogDelete(Document document) {
        Dialog dialogDelete = createCustomDialog(R.layout.dialog_delete);
        dialogDelete.show();

        Button btnYes, btnNo;
        btnYes = dialogDelete.findViewById(R.id.btn_ok);
        btnNo = dialogDelete.findViewById(R.id.btn_cancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(document.getPath());
                if (!file.exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    dialogDelete.hide();
                    showDocuments();
                    return;
                }
                if (file.delete()){
                    DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_delete_success), Toast.LENGTH_SHORT).show();
                    dialogDelete.hide();
                    showDocuments();
                }else{
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_delete_error), Toast.LENGTH_SHORT).show();
                    dialogDelete.hide();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete.hide();
            }
        });
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

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rename = editText.getText().toString().trim();
                //Tên để trống
                if (rename.isEmpty()) {
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_rename_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Tên có thay đổi
                if (!rename.equals(title)){
                    //File không tồn tại --> Báo lỗi, load lại file
                    File fileOld = new File(document.getPath());
                    if (!fileOld.exists()){
                        Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                        dialogRename.hide();
                        showDocuments();
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
                        showDocuments();
                    }
                }else{ //Tên không thay đổi
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_not_rename), Toast.LENGTH_SHORT).show();
                    dialogRename.hide();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRename.hide();
            }
        });
    }

    private void shareDocument(Document document) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(document.getPath());

        String titleFull = document.getTitle();         //Tên file có đuôi (.docx hoặc .doc)
        int dot = titleFull.lastIndexOf('.');       //Vị trí dấu . cuối cùng
        String type = titleFull.substring(dot+1);         //Đuôi file (docx hoặc doc)

        if(fileWithinMyDir.exists()) {
            intentShareFile.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type));
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+document.getPath()));

            startActivity(Intent.createChooser(intentShareFile, titleFull));
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.notification_share_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setFavorite(Document document) {
        document.setFavorite(!document.isFavorite());
        DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);
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
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogSort();
            }
        });
    }

    private void openDialogSort() {
        Dialog dialogSort = createCustomDialog(R.layout.dialog_sort);
        dialogSort.show();

        RadioButton sortName, sortTimeCreate, sortTimeAccess;
        sortName = dialogSort.findViewById(R.id.sort_name);
        sortTimeCreate = dialogSort.findViewById(R.id.sort_time_create);
        sortTimeAccess = dialogSort.findViewById(R.id.sort_time_access);
        switch (typeSort){
            case SORT_NAME:
                sortName.setChecked(true);
                break;
            case SORT_TIME_CREATE:
                sortTimeCreate.setChecked(true);
                break;
            case SORT_TIME_ACCESS:
                sortTimeAccess.setChecked(true);
                break;
        }

        RadioGroup radioGroup = dialogSort.findViewById(R.id.rad_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.sort_name:
                        typeSort = SORT_NAME;
                        showDocuments();
                        dialogSort.hide();
                        break;
                    case R.id.sort_time_create:
                        typeSort = SORT_TIME_CREATE;
                        showDocuments();
                        dialogSort.hide();
                        break;
                    case R.id.sort_time_access:
                        typeSort = SORT_TIME_ACCESS;
                        showDocuments();
                        dialogSort.hide();
                        break;
                }
            }
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