package com.prox.docxreader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.prox.docxreader.OnClickItemDocumentListener;
import com.prox.docxreader.OnClickMoreListener;
import com.prox.docxreader.R;
import com.prox.docxreader.modul.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    private List<Document> documents;
    private OnClickItemDocumentListener onClickItemDocumentListener;
    private OnClickMoreListener onClickMoreListener;

    public DocumentAdapter(List<Document> documents,
                           OnClickItemDocumentListener onClickItemDocumentListener,
                           OnClickMoreListener onClickMoreListener){
        this.documents = documents;
        this.onClickItemDocumentListener = onClickItemDocumentListener;
        this.onClickMoreListener = onClickMoreListener;
    }

    public void setDocuments(List<Document> documents){
        this.documents = documents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docx, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.txtTitleDocx.setText(document.getTitle());
        holder.txtTimeDocx.setText(getDate(document.getTimeCreate()));

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickMoreListener.onClickMore(document);
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
        private ImageView btnMore;
        private ConstraintLayout itemDocx;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleDocx = itemView.findViewById(R.id.txt_title);
            txtTimeDocx = itemView.findViewById(R.id.txt_time);
            btnMore = itemView.findViewById(R.id.btn_more);
            itemDocx = itemView.findViewById(R.id.itemDocx);
        }
    }

    private String getDate(long val){
        return new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(new Date(val*1000));
    }
}
