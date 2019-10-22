package com.example.pagedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

//import com.example.tabledemo.R;

public class ListTableActivity extends Activity {
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
    String[] Name={"Output frequency","Output voltage","Output current","Motor power","Motor actual frequency",
    "Inverter running status","Input terminals status","Output terminals status","AI1 input voltage","Temperature of heatsink 1",
    "Fault record 1","Bus voltage of the latest failure","Actual current of the latest failure","Operation frequency of the latest failure",
    "Custom-made version number","Software date"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(219, 238, 244));

        //！！！数据每次点击后都应该刷新数据
        final List<Parameter> list = new ArrayList<Parameter>();
        for(int i=0;i<15;i++){
            list.add(new Parameter(  Name[i],"-300.00~300.00Hz"+i));
        }
        ListView tableListView = (ListView) findViewById(R.id.list1);

        TableAdapter adapter = new TableAdapter(this, list);

        tableListView.setAdapter(adapter);

//        // 点击事件
//        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//                Parameter parameter=list.get(position);
//                Toast.makeText(ListTableActivity.this,parameter.getName(),Toast.LENGTH_SHORT).show();
//
//                AlertDialog.Builder builder=new AlertDialog.Builder(ListTableActivity.this);
//                builder.setTitle(parameter.getName());
//                //切记单选，复选对话框不能设置内容
//                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(ListTableActivity.this,"你点击了确认按钮",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(ListTableActivity.this,"你点击了取消按钮",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                //！！！创建单选的数据，改善后应该为说明书中的各个选项
//                final String[] str=new String[]{"0","1","2","3","4"};
//                //设置单选对话框的监听
//                //！！！初始选中位置为第一个 完善后应为原来没有改变前的选项
//                builder.setSingleChoiceItems(str, 0, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(ListTableActivity.this,"你选中了"+str[which],Toast.LENGTH_SHORT).show();//！！！被选中的值应该被返回致数据库
//                    }
//                });
//                AlertDialog alertDialog=builder.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//
//            }
//        });



    }

}
