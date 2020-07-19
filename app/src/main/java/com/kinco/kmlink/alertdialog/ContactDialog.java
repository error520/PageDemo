package com.kinco.kmlink.alertdialog;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.util;

public class ContactDialog {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    public ContactDialog(final Context context){
        this.context = context;
        builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        view = inflater.inflate(R.layout.contact_layout,null);
        Button btnDial = view.findViewById(R.id.btnDial);
        Button btnCopy = view.findViewById(R.id.btnCopy);
        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"0755-2658 5555"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("simple text copy","sales@kinco.cn");
                clipboardManager.setPrimaryClip(clipData);
                util.centerToast(context,context.getString(R.string.copy_completed),0);
            }
        });
        builder.setView(view);
        dialog = builder.show();
    }
}
