package com.kinco.kmlink.ui.secondpage;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.MyFragment;
import com.kinco.kmlink.utils.PrefUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ReadCategoryFragment extends MyFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_read_category,container,false);
        for(int i=0; i<5; i++){
            int id = getResources().getIdentifier("button"+i,"id", getContext().getPackageName());
            Button button = layout.findViewById(id);
            if(i==2){
                if(PrefUtil.getLanguage(getContext()).equals("zh")){
                    button.setTypeface(Typeface.SANS_SERIF);
                }
            }
            int finalI = i;
            button.setOnClickListener(v->{
                startActivityByIndex(finalI);
            });
        }
        return layout;
    }

    private void startActivityByIndex(int index){
        Intent intent = new Intent(getContext(), ReadChildActivity.class);
        intent.putExtra("index",index);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }
}
