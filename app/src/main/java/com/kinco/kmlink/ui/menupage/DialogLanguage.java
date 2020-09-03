package com.kinco.kmlink.ui.menupage;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RadioGroup;

import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.ui.main.MainActivity;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.PrefUtil;

import razerdp.basepopup.BasePopupWindow;

public class DialogLanguage extends BasePopupWindow {
    public DialogLanguage(Context context) {
        super(context);
        setPopupGravity(Gravity.CENTER);
        setPopupFadeEnable(true);
    }

    @Override
    public View onCreateContentView() {
        View layout = createPopupById(R.layout.dialog_language);
        RadioGroup radioGroup = layout.findViewById(R.id.LanguageChoice);
        String language = PrefUtil.getLanguage(getContext());
        if(language.substring(0,2).equals("zh")){
            radioGroup.check(R.id.radio_zh);
        }else{
            radioGroup.check(R.id.radio_en);
        }
        Button btnOk = layout.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v->{
            if(radioGroup.getCheckedRadioButtonId()==R.id.radio_en && !language.equals("en")){
                setLanguage(getContext(),"en");
            }else if(radioGroup.getCheckedRadioButtonId()==R.id.radio_zh && !language.equals("zh")){
                setLanguage(getContext(),"zh");
            }
            dismiss();
        });
        Button btnCancel = layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v->{
            dismiss();
        });
        return layout;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0.3f,
                Animation.RELATIVE_TO_SELF,0);
        animation.setDuration(150);
//        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.pop_up);
        return animation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0.3f);
        animation.setDuration(100);
        return animation;
    }

    private void setLanguage(Context context,String language) {
        // 切换
        LanguageUtil.changeAppLanguage(context, language);
        // 存入sp
        PrefUtil.setLanguage(context, language);
        // 重启app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
