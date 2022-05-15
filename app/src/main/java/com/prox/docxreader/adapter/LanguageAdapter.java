package com.prox.docxreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prox.docxreader.utils.LanguageUtils;
import com.prox.docxreader.interfaces.OnClickLanguageListener;
import com.prox.docxreader.databinding.ItemLanguageBinding;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {
    private Context context;
    private final String[] languages;
    private final String[] typeLanguages;
    private ImageView imgChecked;
    private final OnClickLanguageListener onClickLanguageListener;

    public LanguageAdapter(Context context, String[] languages, String[] typeLanguages, OnClickLanguageListener onClickLanguageListener){
        this.context = context;
        this.languages = languages;
        this.typeLanguages = typeLanguages;
        this.onClickLanguageListener = onClickLanguageListener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLanguageBinding binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LanguageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        holder.binding.txtLanguage.setText(languages[position]);
        if (LanguageUtils.getLanguage(context).contains(typeLanguages[position])){
            holder.binding.imgChecked.setVisibility(View.VISIBLE);
            imgChecked = holder.binding.imgChecked;
        }else{
            holder.binding.imgChecked.setVisibility(View.INVISIBLE);
        }

        holder.binding.itemLanguage.setOnClickListener(v -> {
            imgChecked.setVisibility(View.INVISIBLE);
            holder.binding.imgChecked.setVisibility(View.VISIBLE);
            imgChecked = holder.binding.imgChecked;

            onClickLanguageListener.onClickLanguage(typeLanguages[position]);
        });
    }

    @Override
    public int getItemCount() {
        return languages.length;
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder{
        private final ItemLanguageBinding binding;

        public LanguageViewHolder(ItemLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
