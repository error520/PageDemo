package com.kinco.kmlink.alertdialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.kinco.kmlink.R;

import razerdp.basepopup.BasePopup;
import razerdp.basepopup.BasePopupWindow;

public class DialogInitialize extends BasePopupWindow {

    public DialogInitialize(Context context){
        super(context);
        setPopupGravity(Gravity.CENTER);
        setPopupFadeEnable(true);
        setBackPressEnable(false);
        setOutSideDismiss(false);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_initial);
    }
}
