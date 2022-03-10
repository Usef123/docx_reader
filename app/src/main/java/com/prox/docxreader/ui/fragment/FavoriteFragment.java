package com.prox.docxreader.ui.fragment;

import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_NAME;
import static com.prox.docxreader.ui.activity.ReaderActivity.FILE_PATH;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prox.docxreader.OnClickFavoriteListener;
import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickDeleteListener;
import com.prox.docxreader.OnClickShareListener;
import com.prox.docxreader.R;
import com.prox.docxreader.adapter.DocumentFavoriteAdapter;
import com.prox.docxreader.adapter.DocumentHomeAdapter;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.ReaderActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private View view;
    private DocumentFavoriteAdapter documentFavoriteAdapter;
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

        documentFavoriteAdapter = new DocumentFavoriteAdapter(new OnClickItemDocumentListener() {
            @Override
            public void onClickItemDocument(Document document) {
                clickItemDocument(document);
            }
        }, new OnClickShareListener() {
            @Override
            public void onClickShare(Document document) {
                clickShare(document);
            }
        }, new OnClickFavoriteListener(){
            @Override
            public void onClickFavorite(Document document) {
                clickFavorite(document);
            }
        });

        recyclerView.setAdapter(documentFavoriteAdapter);

        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerHorizontal);

        showDocumentsFavorite();
    }

    private void clickItemDocument(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            showDocumentsFavorite();
            return;
        }
        //Update Time Access
        document.setTimeAccess(new Date().getTime());
        DocumentDatabase.getInstance(getContext()).documentDAO().updateDocument(document);

        Intent intent = new Intent(getActivity(), ReaderActivity.class);
        intent.putExtra(FILE_PATH, document.getPath());
        intent.putExtra(FILE_NAME, document.getTitle());
        startActivity(intent);
    }

    private void clickShare(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            showDocumentsFavorite();
            return;
        }
        shareDocument(document);
    }

    private void clickFavorite(Document document) {
        if (!(new File(document.getPath())).exists()){
            Toast.makeText(getContext(), getResources().getString(R.string.notification_file_error), Toast.LENGTH_SHORT).show();
            DocumentDatabase.getInstance(getContext()).documentDAO().deleteDocument(document);
            showDocumentsFavorite();
            return;
        }
        removeFavorite(document);
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
        documentFavoriteAdapter.setDocuments(documents);
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

        sortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeSort = SORT_NAME;
                showDocumentsFavorite();
                dialogSort.hide();
            }
        });
        sortTimeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeSort = SORT_TIME_CREATE;
                showDocumentsFavorite();
                dialogSort.hide();
            }
        });
        sortTimeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeSort = SORT_TIME_ACCESS;
                showDocumentsFavorite();
                dialogSort.hide();
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