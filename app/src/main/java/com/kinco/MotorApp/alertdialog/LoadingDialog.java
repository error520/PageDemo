package com.kinco.MotorApp.alertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kinco.MotorApp.R;
import com.kinco.MotorApp.edittext.TableAdapter;

import java.lang.reflect.Field;

public class LoadingDialog {
    private View view;
    private LayoutInflater inflater;
    private TextView textView;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private TableAdapter.ViewHolder holder;
    private Field field;
    Button btnPos;
    Button btnNeg;
    public OnClickCancelListener onClickCancelListener;
    Context context;
    public LoadingDialog(final Context context,String title, String text, boolean cancelable){
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.loading_layout,null);
        builder.setView(view);

        //据实际情况是否要“取消”按钮
        if(cancelable){
            builder.setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    onClickCancelListener.onNegativeClick();
                }
            });
        }
        dialog = builder.show();

    }
    public interface OnClickCancelListener{
        void onNegativeClick();
    }

    public void setOnClickCancelListener(OnClickCancelListener onClickCancelListener){
        this.onClickCancelListener=onClickCancelListener;
    }

    public void show(){
        builder.show();
    }

    public void gone(){
        dialog.dismiss();
    }

}
