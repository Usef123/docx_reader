package com.prox.docxreader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.prox.docxreader.OnClickFavoriteListener;
import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickDeleteListener;
import com.prox.docxreader.OnClickRenameListener;
import com.prox.docxreader.OnClickShareListener;
import com.prox.docxreader.R;
import com.prox.docxreader.database.DocumentDatabase;
import com.prox.docxreader.modul.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DocumentHomeAdapter extends RecyclerView.Adapter<DocumentHomeAdapter.DocumentViewHolder> {
    private List<Document> documents;
    private OnClickItemDocumentListener onClickItemDocumentListener;
    private OnClickDeleteListener onClickDeleteListener;
    private OnClickRenameListener onClickRenameListener;
    private OnClickShareListener onClickShareListener;
    private OnClickFavoriteListener onClickFavoriteListener;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public DocumentHomeAdapter(OnClickItemDocumentListener onClickItemDocumentListener,
                               OnClickDeleteListener onClickDeleteListener,
                               OnClickRenameListener onClickRenameListener,
                               OnClickShareListener onClickShareListener,
                               OnClickFavoriteListener onClickFavoriteListener){
        this.onClickItemDocumentListener = onClickItemDocumentListener;
        this.onClickDeleteListener = onClickDeleteListener;
        this.onClickRenameListener = onClickRenameListener;
        this.onClickShareListener = onClickShareListener;
        this.onClickFavoriteListener = onClickFavoriteListener;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    public void setDocuments(List<Document> documents){
        this.documents = documents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docx_home, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.txtTitleDocx.setText(document.getTitle());
        holder.txtTimeDocx.setText(getDate(document.getTimeCreate()));
        if (document.isFavorite()){
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite);
        }else{
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_fill);
        }

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(document.getId()));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDeleteListener.onClickDelete(document);
            }
        });
        holder.btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRenameListener.onClickRename(document);
            }
        });
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickShareListener.onClickShare(document);
            }
        });
        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFavoriteListener.onClickFavorite(document);
            }
        });
        holder.itemDocx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemDocumentListener.onClickItemDocument(document);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitleDocx, txtTimeDocx;
        private ImageButton btnDelete, btnRename, btnShare, btnFavorite;
        private ConstraintLayout itemDocx;
        private SwipeRevealLayout swipeRevealLayout;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleDocx = itemView.findViewById(R.id.txt_title);
            txtTimeDocx = itemView.findViewById(R.id.txt_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnRename = itemView.findViewById(R.id.btn_rename);
            btnShare = itemView.findViewById(R.id.btn_share);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            itemDocx = itemView.findViewById(R.id.itemDocx);

            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
        }
    }

    private String getDate(long val){
        return new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(new Date(val*1000));
    }
}
