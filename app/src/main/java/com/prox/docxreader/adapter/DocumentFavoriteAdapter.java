package com.prox.docxreader.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.prox.docxreader.OnClickFavoriteListener;
import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickShareListener;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.ItemDocxFavoriteBinding;
import com.prox.docxreader.modul.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DocumentFavoriteAdapter extends RecyclerView.Adapter<DocumentFavoriteAdapter.DocumentViewHolder> {
    private List<Document> documents;
    private final OnClickItemDocumentListener onClickItemDocumentListener;
    private final OnClickShareListener onClickShareListener;
    private final OnClickFavoriteListener onClickFavoriteListener;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public DocumentFavoriteAdapter(OnClickItemDocumentListener onClickItemDocumentListener,
                                   OnClickShareListener onClickShareListener,
                                   OnClickFavoriteListener onClickFavoriteListener){
        this.onClickItemDocumentListener = onClickItemDocumentListener;
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
        ItemDocxFavoriteBinding binding = ItemDocxFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        holder.binding.btnShare.setOnClickListener(view -> onClickShareListener.onClickShare(document));
        holder.binding.btnFavorite.setOnClickListener(view -> onClickFavoriteListener.onClickFavorite(document));
        holder.binding.itemDocx.itemDocx.setOnClickListener(v -> onClickItemDocumentListener.onClickItemDocument(document));
    }

    @Override
    public int getItemCount() {
        if (documents == null){
            return 0;
        }
        return documents.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder{
        private final ItemDocxFavoriteBinding binding;

        public DocumentViewHolder(ItemDocxFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDate(long val){
        return new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(new Date(val*1000));
    }
}
