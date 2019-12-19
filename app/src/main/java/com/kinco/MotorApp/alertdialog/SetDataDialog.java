package com.kinco.MotorApp.alertdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinco.MotorApp.R;

import java.lang.reflect.Field;

public class SetDataDialog {
    private String SetData = "";
    private String title;
    private String Unit;
    final private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Field field;
    Button btnPos;
    Button btnNeg;
    public SetDataDialog.OnClickBottomListener onClickBottomListener;
    Context context;


    public SetDataDialog(final Context context,String title,String Unit,String Hint) {
        this.context = context;
        this.title=title;
        this.Unit=Unit;
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
//        builder.setMessage("\n"+"Range:" +Hint+"\n"+"Default:" +"\n"+"Current:" );
        final TextView blank= new TextView(context);
        blank.setText("");
        final TextView blank1= new TextView(context);
        blank1.setText("");

        final TextView range = new TextView(context);
        range.setText("        range:   ");
        range.setTextSize(17);
        final TextView Range = new TextView(context);
        Range.setText(Hint);
        Range.setTextSize(17);
        Range.setTextColor(Color.BLACK);
        Range.setBackgroundResource(R.drawable.lin);

        LinearLayout layout1=new LinearLayout(context);
        layout1.setOrientation(LinearLayout.HORIZONTAL);
        layout1.addView(range);
        layout1.addView(Range);

        final TextView default1= new TextView(context);
        default1.setText("        default:");
        default1.setTextSize(17);
        final TextView Default = new TextView(context);
        Default.setText("Default");
        Default.setTextSize(17);
        Default.setTextColor(Color.BLACK);
        Default.setBackgroundResource(R.drawable.lin);

        LinearLayout layout2=new LinearLayout(context);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.addView(default1);
        layout2.addView(Default);

        final TextView current = new TextView(context);
        current.setText("        current:");
        current.setTextSize(17);
        final TextView Current = new TextView(context);
        Current.setText("Current");
        Current.setTextSize(17);
        Current.setTextColor(Color.BLACK);
        Current.setBackgroundResource(R.drawable.lin);

        LinearLayout layout3=new LinearLayout(context);
        layout3.setOrientation(LinearLayout.HORIZONTAL);
        layout3.addView(current);
        layout3.addView(Current);

        final TextView input = new TextView(context);
        input.setText("        input:");
        input.setTextSize(17);
        final EditText edit = new EditText(context);
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(400000)});
        edit.setHint("SetData");
        final TextView unit = new TextView(context);
        unit.setText(Unit);
        unit.setTextSize(17);
        unit.setTextColor(Color.BLACK);
        LinearLayout layout4=new LinearLayout(context);
        layout4.setOrientation(LinearLayout.HORIZONTAL);
        layout4.addView(input);
        layout4.addView(edit);
        layout4.addView(unit);

        LinearLayout layout0=new LinearLayout(context);
        layout0.setOrientation(LinearLayout.VERTICAL);
        layout0.addView(layout1);
        layout0.addView(blank);
        layout0.addView(layout2);
        layout0.addView(blank1);
        layout0.addView(layout3);
        layout0.addView(layout4);

        builder.setView(layout0);

        builder.setPositiveButton("set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SetData = edit.getText().toString();
                onClickBottomListener.onPositiveClick();

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onClickBottomListener.onNegativeClick();
            }
        });

        dialog = builder.show();
        btnPos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnNeg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnPos.setText("Set");
        btnNeg.setText("Cancel");
        btnPos.setTextColor(Color.RED);
        btnNeg.setTextColor(Color.RED);
        //设置不可消失
        try {
            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            ErrorDialog errorDialog = new ErrorDialog(context, e.toString());
            errorDialog.show();
        }

    }

    public interface OnClickBottomListener {
        void onPositiveClick();

        void onNegativeClick();
    }

    /**
     * 提供给外界设置监听接口的方法
     *
     * @param onClickBottomListener
     * @return
     */
    public SetDataDialog setOnClickBottomListener(SetDataDialog.OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    /**
     * 展示dialog
     */
    public void show() {
        builder.show();
    }

    /**
     * 取消dialog
     */
    public void gone() {
        try {
            field.set(dialog, true);
            dialog.dismiss();
        } catch (Exception e) {
            ErrorDialog errorDialog = new ErrorDialog(context, e.toString());
            errorDialog.show();
        }
    }

    ;

    public String getSetData() {
        return SetData;
    }

}