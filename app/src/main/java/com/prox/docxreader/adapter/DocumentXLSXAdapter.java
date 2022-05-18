package com.prox.docxreader.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.prox.docxreader.R;
import com.prox.docxreader.databinding.ItemOtherBinding;
import com.prox.docxreader.interfaces.OnClickDeleteListener;
import com.prox.docxreader.interfaces.OnClickItemDocumentListener;
import com.prox.docxreader.interfaces.OnClickRenameListener;
import com.prox.docxreader.interfaces.OnClickShareListener;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.utils.NumberUtils;

import java.util.List;

public class DocumentXLSXAdapter extends RecyclerView.Adapter<DocumentXLSXAdapter.DocumentViewHolder> {
    private List<Document> documents;
    private final OnClickItemDocumentListener onClickItemDocumentListener;
    private final OnClickDeleteListener onClickDeleteListener;
    private final OnClickRenameListener onClickRenameListener;
    private final OnClickShareListener onClickShareListener;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public DocumentXLSXAdapter(OnClickItemDocumentListener onClickItemDocumentListener,
                               OnClickDeleteListener onClickDeleteListener,
                               OnClickRenameListener onClickRenameListener,
                               OnClickShareListener onClickShareListener){
        this.onClickItemDocumentListener = onClickItemDocumentListener;
        this.onClickDeleteListener = onClickDeleteListener;
        this.onClickRenameListener = onClickRenameListener;
        this.onClickShareListener = onClickShareListener;
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
        ItemOtherBinding binding = ItemOtherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.binding.itemDocx.img.setImageResource(R.drawable.ic_item_xlsx);
        holder.binding.itemDocx.txtTitle.setText(document.getTitle());
        holder.binding.itemDocx.txtTime.setText(NumberUtils.formatAsDate(document.getTimeCreate()));

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
        private final ItemOtherBinding binding;

        public DocumentViewHolder(@NonNull ItemOtherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
