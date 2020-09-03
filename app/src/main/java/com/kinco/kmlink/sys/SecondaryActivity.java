package com.kinco.kmlink.sys;

import android.os.Bundle;

import com.kinco.kmlink.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondaryActivity extends AppCompatActivity {

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
    }
}
