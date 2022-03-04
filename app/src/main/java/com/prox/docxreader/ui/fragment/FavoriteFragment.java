package com.prox.docxreader.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;

public class FavoriteFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private List<Document> documents;
    private ImageView btnSort;
    private EditText edtSearch;
    private TextView txtTitle;

    private static final int SORT_NAME = 1;
    private static final int SORT_TIME_CREATE = 2;
    private static final int SORT_TIME_ACCESS = 3;
    private int typeSort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        txtTitle = view.findViewById(R.id.txt_title_fragment);
        txtTitle.setText(getResources().getString(R.string.title_favorite));

        setupRecyclerView();

        addBtnSort();

        addSearchDocument();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView = view.findViewById(R.id.recycler_view_favorite);
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

        showDocumentsFavorite();
    }

    private void showDocumentsFavorite() {
        documents = DocumentDatabase.getInstance(getContext()).documentDAO().getDocumentsFavorite();
        documentAdapter.setDocuments(documents);
    }

    private void openDialogMore(Document document) {
        Dialog dialogMore = createCustomDialog(R.layout.dialog_more_favorite);
        dialogMore.show();

        LinearLayout itemShare, itemRemoveFavorite;
        itemShare = dialogMore.findViewById(R.id.item_share);
        itemRemoveFavorite = dialogMore.findViewById(R.id.item_favorite);

        itemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDocument(document);
                dialogMore.hide();
            }
        });

        itemRemoveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFavorite(document);
                dialogMore.hide();
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

    private void removeFavorite(Document document) {
        document.setFavorite(false);
        DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);
        Toast.makeText(getContext(), getResources().getString(R.string.notification_remove_favorite), Toast.LENGTH_SHORT).show();
        showDocumentsFavorite();
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
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByName(search);
                }else if (typeSort == SORT_TIME_CREATE){
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByTimeCreate(search);
                }else if (typeSort == SORT_TIME_ACCESS){
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByTimeAccess(search);
                }else{
                    documentsSearch = DocumentDatabase.getInstance(getContext()).documentDAO().searchDocumentFavorite(search);
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
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByName(search);
                typeSort = SORT_NAME;
                dialogSort.hide();
                documentAdapter.setDocuments(documents);
            }
        });

        sortTimeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByTimeCreate(search);
                typeSort = SORT_TIME_CREATE;
                dialogSort.hide();
                documentAdapter.setDocuments(documents);
            }
        });

        sortTimeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documents = DocumentDatabase.getInstance(getContext()).documentDAO().sortDocumentFavoriteByTimeAccess(search);
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