package com.kinco.kmlink.ui.menupage;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kinco.kmlink.R;

public class DialogVersion extends DialogLanguage {
    public DialogVersion(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        View layout = createPopupById(R.layout.dialog_version);
        TextView tvVersion = layout.findViewById(R.id.tv_version_code);
        String version = "";
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info =  manager.getPackageInfo(getContext().getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "未能检测到版本号";
            e.printStackTrace();
        }
        tvVersion.setText(version);
        Button btnOK = layout.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(v->{
            dismiss();
        });
        return layout;
    }
}
