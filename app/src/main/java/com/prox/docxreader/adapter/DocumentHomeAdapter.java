package com.prox.docxreader.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.prox.docxreader.OnClickFavoriteListener;
import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickDeleteListener;
import com.prox.docxreader.OnClickRenameListener;
import com.prox.docxreader.OnClickShareListener;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.ItemDocxHomeBinding;
import com.prox.docxreader.modul.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DocumentHomeAdapter extends RecyclerView.Adapter<DocumentHomeAdapter.DocumentViewHolder> {
    private List<Document> documents;
    private final OnClickItemDocumentListener onClickItemDocumentListener;
    private final OnClickDeleteListener onClickDeleteListener;
    private final OnClickRenameListener onClickRenameListener;
    private final OnClickShareListener onClickShareListener;
    private final OnClickFavoriteListener onClickFavoriteListener;

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

    @SuppressLint("NotifyDataSetChanged")
    public void setDocuments(List<Document> documents){
        this.documents = documents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocxHomeBinding binding = ItemDocxHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.binding.itemDocx.txtTitle.setText(document.getTitle());
        holder.binding.itemDocx.txtTime.setText(getDate(document.getTimeCreate()));
        if (document.isFavorite()){
            holder.binding.btnFavorite.setImageResource(R.drawable.ic_favorite);
        }else{
            holder.binding.btnFavorite.setImageResource(R.drawable.ic_favorite_fill);
        }

        viewBinderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(document.getId()));
        holder.binding.btnDelete.setOnClickListener(view -> {
            onClickDeleteListener.onClickDelete(document);
            viewBinderHelper.closeLayout(String.valueOf(document.getId()));
        });
        holder.binding.btnRename.setOnClickListener(view -> {
            onClickRenameListener.onClickRename(document);
            viewBinderHelper.closeLayout(String.valueOf(document.getId()));
        });
        holder.binding.btnShare.setOnClickListener(view -> {
            onClickShareListener.onClickShare(document);
            viewBinderHelper.closeLayout(String.valueOf(document.getId()));
        });
        holder.binding.btnFavorite.setOnClickListener(view -> {
            onClickFavoriteListener.onClickFavorite(document);
            viewBinderHelper.closeLayout(String.valueOf(document.getId()));
        });
        holder.binding.itemDocx.itemDocx.setOnClickListener(v -> {
            onClickItemDocumentListener.onClickItemDocument(document);
            if (holder.binding.swipeRevealLayout.isOpened()){
                viewBinderHelper.closeLayout(String.valueOf(document.getId()));
            }
        });

        holder.binding.itemDocx.btnMore.setOnClickListener(v ->{
            if (holder.binding.swipeRevealLayout.isOpened()){
                viewBinderHelper.closeLayout(String.valueOf(document.getId()));
            }else {
                viewBinderHelper.openLayout(String.valueOf(document.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (documents == null){
            return 0;
        }
        return documents.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder{
        private final ItemDocxHomeBinding binding;

        public DocumentViewHolder(@NonNull ItemDocxHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDate(long val){
        return new SimpleDateFormat("HH:mm, dd/MM/yyyy").format(new Date(val*1000));
    }
}
