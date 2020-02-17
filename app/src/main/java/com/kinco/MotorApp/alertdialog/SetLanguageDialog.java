package com.kinco.MotorApp.alertdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.kinco.MotorApp.LanguageUtils.LanguageUtil;
import com.kinco.MotorApp.LanguageUtils.PrefUtils;
import com.kinco.MotorApp.MainActivity;
import com.kinco.MotorApp.R;
import java.lang.reflect.Field;
import java.util.Locale;

public class SetLanguageDialog {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Field field;
    Button btnPos;
    Button btnNeg;
    private Context context;

    public SetLanguageDialog(final Context context){
        builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.Language));
        //builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.language_layout,null);
        builder.setView(layout);
        final RadioGroup radioGroup = layout.findViewById(R.id.LanguageChoice);
        String language = PrefUtils.getLanguage(context);
        Log.d("MySV3",language);
        if(language.substring(0,2).equals("zh")){
            radioGroup.check(R.id.radio_zh);
        }else
            radioGroup.check(R.id.radio_en);

        builder.setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.radio_en){
                    setLanguage(context,"en");
                }else{
                    setLanguage(context,"zh");
                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //setLanguage(context,"zh");
            }
        });
        dialog = builder.show();
        btnPos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnNeg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnPos.setTextColor(Color.parseColor("#DA0D0D"));
        btnNeg.setTextColor(Color.parseColor("#DA0D0D"));
    }

    private void setLanguage(Context context,String language) {
        // 切换
        LanguageUtil.changeAppLanguage(context, language);
        // 存入sp
        PrefUtils.setLanguage(context, language);
        // 重启app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
