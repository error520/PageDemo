package com.kinco.MotorApp.alertdialog;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import java.lang.reflect.Field;


/**
 * 密码输入框
 */
public class PasswordDialog{
    private String password="";
    final private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Field field;
    Button btnPos;
    Button btnNeg;
    public OnClickBottomListener onClickBottomListener;
    Context context;
    public PasswordDialog(final Context context){
        this.context=context;
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Please enter the password");
        final EditText edit = new EditText(context);
        edit.setHeight(150);
        edit.setWidth(30);
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edit.setHint("4 digits password");
        builder.setView(edit);
        builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                password=edit.getText().toString();
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
        btnPos.setText("Connect");
        btnNeg.setText("Cancel");
        btnPos.setTextColor(Color.RED);
        btnNeg.setTextColor(Color.RED);
        //设置不可消失
        try {
            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog,false);
        }catch (Exception e){
            ErrorDialog errorDialog = new ErrorDialog(context,e.toString());
            errorDialog.show();
        }

    }
    public interface OnClickBottomListener{
        void onPositiveClick();
        void onNegativeClick();
    }

    /**
     * 提供给外界设置监听接口的方法
     * @param onClickBottomListener
     * @return
     */
    public PasswordDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    /**
     * 展示dialog
     */
    public void show(){
        builder.show();
    }

    /**
     * 取消dialog
     */
    public void gone(){
        try {
            field.set(dialog,true);
            dialog.dismiss();
        } catch (Exception e) {
            ErrorDialog errorDialog = new ErrorDialog(context,e.toString());
            errorDialog.show();
        }
    };

    public String getPassword(){return password;}

//    private void setRegion(final EditText et) {
//        et.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if (start > 1){
//                    if (MIN_MARK != -1 && MAX_MARK != -1){
//                        int num = Integer.parseInt(s.toString());
//                        if (num > MAX_MARK) {
//                            s = String.valueOf(MAX_MARK);
//                            et.setText(s);
//                        }else if(num < MIN_MARK)
//                            s = String.valueOf(MIN_MARK);
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s != null && !s.equals("")) {
//                    if (MIN_MARK != -1 && MAX_MARK != -1) {
//                        int markVal = 0;
//                        try {
//                            markVal = Integer.parseInt(s.toString());
//                        }catch (NumberFormatException e){
//                            markVal = 0;
//                        }
//                        if (markVal > MAX_MARK) {
//                            Toast.makeText(getBaseContext(), "输入数字不能超过110", Toast.LENGTH_SHORT).show();
//                            et.setText(String.valueOf(MAX_MARK));
//                        }
//                        return;
//                    }
//                }
//            }
//        });
//    }

}
