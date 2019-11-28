package com.kinco.MotorApp.alertdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog extends AlertDialog.Builder{
    public ErrorDialog(Context context) {
        super(context);
        super.setTitle("Error occured!");
        super.setMessage("Please check log files and contact with the developer");
        super.setCancelable(false);
        super.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

}
