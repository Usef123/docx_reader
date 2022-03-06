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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteFragment extends Fragment {
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
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        edtSearch = view.findViewById(R.id.edt_search);
        typeSort = SORT_NAME;

        TextView txtTitle = view.findViewById(R.id.txt_title_fragment);
        txtTitle.setText(getResources().getString(R.string.title_favorite));

        setupRecyclerView();

        addBtnSort();

        addSearchDocument();

        return view;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_favorite);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        documents = new ArrayList<>();

        documentAdapter = new DocumentAdapter(documents, new OnClickItemDocumentListener() {
            @Override
            public void onClickItemDocument(Document document) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    showDocumentsFavorite();
                    return;
                }
                //Update Time Access
                document.setTimeAccess(new Date().getTime());
                DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);

                Toast.makeText(getContext(), document.getPath(), Toast.LENGTH_SHORT).show();
            }
        }, new OnClickMoreListener() {
            @Override
            public void onClickMore(Document document) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    showDocumentsFavorite();
                    return;
                }
                openDialogMore(document);
            }
        });

        recyclerView.setAdapter(documentAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerHorizontal);

        showDocumentsFavorite();
    }

    private void showDocumentsFavorite() {
        String search = edtSearch.getText().toString().trim();
        switch (typeSort){
            case SORT_NAME:
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByName(search);
                break;
            case SORT_TIME_CREATE:
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByTimeCreate(search);
                break;
            case SORT_TIME_ACCESS:
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByTimeAccess(search);
                break;
        }
        documentAdapter.setDocuments(documents);
    }

    private void openDialogMore(Document document) {
        Dialog dialogMore = createCustomDialog(R.layout.dialog_more_favorite);
        dialogMore.show();

        Button btnShare, btnFavorite;
        btnShare = dialogMore.findViewById(R.id.btn_share);
        btnFavorite = dialogMore.findViewById(R.id.btn_favorite);

        TextView txtTitle = dialogMore.findViewById(R.id.txt_title);
        txtTitle.setText(document.getTitle());

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(new File(document.getPath())).exists()){
                    Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
                    dialogMore.hide();
                    showDocumentsFavorite();
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
                    showDocumentsFavorite();
                    return;
                }
                removeFavorite(document);
                dialogMore.hide();
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

    private void removeFavorite(Document document) {
        document.setFavorite(false);
        DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);
        Toast.makeText(getContext(), getResources().getString(R.string.notification_remove_favorite), Toast.LENGTH_SHORT).show();
        showDocumentsFavorite();
    }

    private void addSearchDocument() {
        edtSearch.addTextChangedListener(new TextWatcher() {
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
                        showDocumentsFavorite();
                        dialogSort.hide();
                        break;
                    case R.id.sort_time_create:
                        typeSort = SORT_TIME_CREATE;
                        showDocumentsFavorite();
                        dialogSort.hide();
                        break;
                    case R.id.sort_time_access:
                        typeSort = SORT_TIME_ACCESS;
                        showDocumentsFavorite();
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