package com.kinco.kmlink.ui.menupage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.util;

import razerdp.basepopup.BasePopupWindow;

public class DialogContact extends DialogLanguage {
    public DialogContact(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        View layout = createPopupById(R.layout.dialog_contact);
        Button btnDial = layout.findViewById(R.id.btnDial);
        Button btnCopy = layout.findViewById(R.id.btnCopy);
        btnDial.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"0755-2658 5555"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        });
        final ClipboardManager clipboardManager = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        btnCopy.setOnClickListener(v -> {
            ClipData clipData = ClipData.newPlainText("simple text copy","sales@kinco.cn");
            clipboardManager.setPrimaryClip(clipData);
            util.centerToast(getContext(), getContext().getString(R.string.copy_completed),0);
        });
        return layout;
    }
}
