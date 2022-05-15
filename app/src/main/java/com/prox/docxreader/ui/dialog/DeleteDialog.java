package com.prox.docxreader.ui.dialog;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.prox.docxreader.R;
import com.prox.docxreader.databinding.DialogDeleteBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;

import java.io.File;

public class DeleteDialog extends CustomDialog{
    public DeleteDialog(@NonNull Context context, DialogDeleteBinding binding, DocumentViewModel model, Document document) {
        super(context, binding.getRoot());

        binding.btnOk.setOnClickListener(view -> {
            File file = new File(document.getPath());
            if (!file.exists()){
                Toast.makeText(getContext(), R.string.notification_file_not_found, Toast.LENGTH_SHORT).show();
                model.delete(document);
            }else {
                if (FileUtils.deleteFile(context, document.getPath())){
                    Toast.makeText(context, R.string.notification_delete_success, Toast.LENGTH_SHORT).show();
                    model.delete(document);
                }else{
                    Toast.makeText(context, R.string.notification_delete_error, Toast.LENGTH_SHORT).show();
                }
            }
            cancel();
        });

        binding.btnCancel.setOnClickListener(view -> cancel());
    }
}
