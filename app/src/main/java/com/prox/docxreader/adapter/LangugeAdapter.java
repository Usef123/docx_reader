package com.prox.docxreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.prox.docxreader.LocaleHelper;
import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickLanguageListener;
import com.prox.docxreader.OnClickMoreListener;
import com.prox.docxreader.R;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.ui.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LangugeAdapter extends RecyclerView.Adapter<LangugeAdapter.LanguageViewHolder> {
    private String[] languages, typeLanguages;
    private ImageView imgChecked;
    private OnClickLanguageListener onClickLanguageListener;

    public LangugeAdapter(String[] languages, String[] typeLanguages, OnClickLanguageListener onClickLanguageListener){
        this.languages = languages;
        this.typeLanguages = typeLanguages;
        this.onClickLanguageListener = onClickLanguageListener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        holder.txtLanguage.setText(languages[position]);
        if (Locale.getDefault().getLanguage().contains(typeLanguages[position])){
            holder.imgChecked.setVisibility(View.VISIBLE);
            imgChecked = holder.imgChecked;
        }else{
            holder.imgChecked.setVisibility(View.INVISIBLE);
        }

        holder.itemLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgChecked.setVisibility(View.INVISIBLE);
                holder.imgChecked.setVisibility(View.VISIBLE);
                imgChecked = holder.imgChecked;

                onClickLanguageListener.onClickLanguage(typeLanguages[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languages.length;
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder{
        private TextView txtLanguage;
        private ImageView imgChecked;
        private ConstraintLayout itemLanguage;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLanguage = itemView.findViewById(R.id.txt_language);
            imgChecked = itemView.findViewById(R.id.img_checked);
            itemLanguage = itemView.findViewById(R.id.item_language);
        }
    }
}
