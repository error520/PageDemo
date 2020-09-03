package com.kinco.kmlink.alertdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.FileUtil;
import com.kinco.kmlink.utils.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件存储弹窗
 */
public class SaveFileDialog {
    final private AlertDialog.Builder builder;
    private AlertDialog dialog;
    Button btnPos;
    Button btnNeg;
    Context context;

    public SaveFileDialog(final Context context,final StringBuilder sb){
        this.context = context;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.file_name));
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.save_file_dialog,null);
        final EditText editText = layout.findViewById(R.id.file_name_edittext);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = "wave"+formatter.format(curDate);
        editText.setText(str);
        editText.setSelectAllOnFocus(true);
        builder.setView(layout);

        builder.setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = editText.getText().toString();
                if(fileName!=null&&!fileName.equals("")) {
                    FileUtil.saveFile(0, fileName + ".dat", sb.toString());
                    util.centerToast(context,context.getString(R.string.save_successfully),0);
                }
                else
                    util.centerToast(context,context.getString(R.string.filename_empty),0);
            }
        });

        builder.setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.show();
        btnPos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnNeg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnPos.setTextColor(context.getColor(R.color.colorAccent));
        btnNeg.setTextColor(Color.parseColor("#DA0D0D"));
    }
}
