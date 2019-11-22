package com.kinco.MotorApp.alertdialog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.kinco.MotorApp.BluetoothService.BLEService;


//TODO 密码输入框
public class PasswordDialog extends AlertDialog.Builder {
    private String password="";
    private final BLEService mBleService;
    public PasswordDialog(final Context context, BLEService bleService){
        super(context);
        super.setCancelable(false);
        super.setTitle("Please enter the password");
        final EditText edit = new EditText(getContext());
        this.mBleService = bleService;
        edit.setHeight(150);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edit.setHint("0~65536");
        super.setView(edit);
        super.setPositiveButton("connect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String password = edit.getText().toString();
                if (password.length()==4) {
                    mBleService.readData("0000","0001");
                } else {
                    Toast.makeText(context, "please enter complete password!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        super.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mBleService.close();
            }
        });



    }

    public String getPassword(){return password;}

//    public void showLoginDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("请输入终端登录密码");
//
//        // String digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
//        //edit.setKeyListener(DigitsKeyListener.getInstance(digits));
//
//
//
//
//
//        Button btnPos = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
//        Button btnNeg = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
//        btnPos.setTextColor(Color.BLUE);
//        btnNeg.setTextColor(Color.BLUE);
//    }
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
