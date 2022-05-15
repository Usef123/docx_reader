package com.prox.docxreader.ui.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.prox.docxreader.databinding.DialogRenameBinding;
import com.prox.docxreader.modul.Document;
import com.prox.docxreader.utils.FileUtils;
import com.prox.docxreader.viewmodel.DocumentViewModel;

public class RenameDialog extends CustomDialog{
    public RenameDialog(@NonNull Context context, DialogRenameBinding binding, DocumentViewModel model, Document document) {
        super(context, binding.getRoot());

        binding.edtRename.setText(FileUtils.getName(document.getPath()));

        binding.btnOk.setOnClickListener(view -> {
            String rename = binding.edtRename.getText().toString().trim();
            String newPath = FileUtils.renameFile(context, document.getPath(), rename);
            if (newPath != null){
                document.setPath(newPath);
                document.setTitle(FileUtils.getName(newPath)+"."+FileUtils.getType(newPath));
                model.update(document);
            }
            cancel();
        });

        binding.btnCancel.setOnClickListener(view -> cancel());
    }
}
