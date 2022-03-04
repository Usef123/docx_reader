package com.prox.docxreader.ui.fragment;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickMoreListener;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentAdapter;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private List<Document> documents;
    private ImageView btnSort;
    private EditText edtSearch;

    private static final int SORT_NAME = 1;
    private static final int SORT_TIME_CREATE = 2;
    private static final int SORT_TIME_ACCESS = 3;
    private int typeSort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        setupRecyclerView();

        addBtnSort();

        addSearchDocument();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView = view.findViewById(R.id.recycler_view_home);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        documents = new ArrayList<>();

        documentAdapter = new DocumentAdapter(documents, new OnClickItemDocumentListener() {
            @Override
            public void onClickItemDocument(Document document) {
                Log.d("path file", document.getPath());
            }
        }, new OnClickMoreListener() {
            @Override
            public void onClickMore(Document document) {
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
        documents = DocumentDatabase.getInstance(getContext()).documentDAO().getDocuments();
        documentAdapter.setDocuments(documents);
    }

    private void openDialogMore(Document document) {
        Dialog dialogMore = createCustomDialog(R.layout.dialog_more);
        dialogMore.show();

        LinearLayout itemDelete, itemRename, itemShare, itemFavorite;
        itemDelete = dialogMore.findViewById(R.id.item_delete);
        itemRename = dialogMore.findViewById(R.id.item_rename);
        itemShare = dialogMore.findViewById(R.id.item_share);
        itemFavorite = dialogMore.findViewById(R.id.item_favorite);
        ImageView imgFavorite = dialogMore.findViewById(R.id.ic_favorite);

        if (document.isFavorite()){
            imgFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }else{
            imgFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_fill));
        }

        itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogDelete(document);
                dialogMore.hide();
            }
        });

        itemRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogRename(document);
                dialogMore.hide();
            }
        });

        itemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDocument(document);
                dialogMore.hide();
            }
        });

        itemFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavorite(document);
                dialogMore.hide();
            }
        });
    }

    private void openDialogDelete(Document document) {
        Dialog dialogDelete = createCustomDialog(R.layout.dialog_delete);
        dialogDelete.show();

        Button btnYes, btnNo;
        btnYes = dialogDelete.findViewById(R.id.btn_yes);
        btnNo = dialogDelete.findViewById(R.id.btn_no);

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
        btnOK = dialogRename.findViewById(R.id.btn_yes);
        btnCancel = dialogRename.findViewById(R.id.btn_no);

        EditText editText = dialogRename.findViewById(R.id.edt_rename);
        String title = document.getTitle(); //Name có .docx
        String name = title.substring(0, title.length()-5); //Name bỏ .docx
        editText.setText(name);

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
                if (!rename.equals(name)){
                    //File không tồn tại --> Báo lỗi, load lại file
                    File fileOld = new File(document.getPath());
                    if (!fileOld.exists()){
                        Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                        dialogRename.hide();
                        showDocuments();
                        return;
                    }

                    //Update tên và path document
                    document.setTitle(rename.concat(".docx"));
                    String newPath = document.getPath().substring(0, document.getPath().length()-title.length()).concat(document.getTitle());
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

        if(fileWithinMyDir.exists()) {
            intentShareFile.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"));
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+document.getPath()));

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
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
        edtSearch = view.findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search = edtSearch.getText().toString().trim();
                List<Document> documentsSearch = null;
                if (typeSort == SORT_NAME){
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByName(search);
                }else if (typeSort == SORT_TIME_CREATE){
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByTimeCreate(search);
                }else if (typeSort == SORT_TIME_ACCESS){
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByTimeAccess(search);
                }else{
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().searchDocument(search);
                }
                if (documentsSearch!=null){
                    documentAdapter.setDocuments(documentsSearch);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void addBtnSort() {
        btnSort = view.findViewById(R.id.btn_filter);
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

        LinearLayout sortName, sortTimeCreate, sortTimeAccess;
        CheckBox chkSortName, chkSortTimeCreate, chkSortTimeAccess;
        sortName = dialogSort.findViewById(R.id.sort_name);
        sortTimeCreate = dialogSort.findViewById(R.id.sort_time_create);
        sortTimeAccess = dialogSort.findViewById(R.id.sort_time_access);
        chkSortName = dialogSort.findViewById(R.id.chk_sort_name);
        chkSortTimeCreate = dialogSort.findViewById(R.id.chk_sort_time_create);
        chkSortTimeAccess = dialogSort.findViewById(R.id.chk_sort_time_access);

        if (typeSort == SORT_NAME){
            chkSortName.setChecked(true);
            chkSortTimeCreate.setChecked(false);
            chkSortTimeAccess.setChecked(false);
        }else if (typeSort == SORT_TIME_CREATE){
            chkSortName.setChecked(false);
            chkSortTimeCreate.setChecked(true);
            chkSortTimeAccess.setChecked(false);
        }else if (typeSort == SORT_TIME_ACCESS){
            chkSortName.setChecked(false);
            chkSortTimeCreate.setChecked(false);
            chkSortTimeAccess.setChecked(true);
        }

        String search = edtSearch.getText().toString().trim();
        sortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByName(search);
                typeSort = SORT_NAME;
                dialogSort.hide();
                documentAdapter.setDocuments(documents);
            }
        });

        sortTimeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByTimeCreate(search);
                typeSort = SORT_TIME_CREATE;
                dialogSort.hide();
                documentAdapter.setDocuments(documents);
            }
        });

        sortTimeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentByTimeAccess(search);
                typeSort = SORT_TIME_ACCESS;
                dialogSort.hide();
                documentAdapter.setDocuments(documents);
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
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        return dialog;
    }
}