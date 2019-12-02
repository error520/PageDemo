package com.kinco.MotorApp;


import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kinco.MotorApp.BluetoothService.BLEService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author: Nicholas
 * @descrption: 这是一个工具类，定义了一些频繁调用的方法
 */
public class util {
    //CRC校验
    public static byte[] CRC16_Check(byte Pushdata[], int length){
        int Reg_CRC=0xffff;
        int temp;
        int i,j;
        byte byteResult[]=new byte[2];

        for( i = 0; i<length; i ++){
            temp = Pushdata[i];
            if(temp < 0) temp += 256;
            temp &= 0xff;
            Reg_CRC^= temp;

            for (j = 0; j<8; j++){
                if ((Reg_CRC & 0x0001) == 0x0001)
                    Reg_CRC=(Reg_CRC>>1)^0xA001;
                else
                    Reg_CRC >>=1;
            }
        }
        byteResult[0] = (byte)(Reg_CRC&0xff);  //低位
        byteResult[1] = (byte)((Reg_CRC&0xff00)>>8);  //高位
        return byteResult;
    }
    //数字转byte数组
    public static byte[] intToByte2(int i) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (i & 0xFF);
        targets[0] = (byte) (i >> 8 & 0xFF);
        return targets;
    }
    //字节数组转字节样式的字符串
    public static String toHexString(byte[] byteArray, boolean addGap) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i])+(addGap?" ":""));
        }
        return hexString.toString().toUpperCase();
    }

    //只要有效数据
    public static String toHexString(byte[] byteArray,int off) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            if ((byteArray[i+off] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i+off])+" ");
        }
        return hexString.toString().toUpperCase();
    }

    /**
     * byte数组转换为无符号short整数
     *
     * @param bytes
     *
     * @return short整数
     */
    public static int byte2ToUnsignedShort(byte[] bytes) {
        return byte2ToUnsignedShort(bytes, 0);
    }

    /**
     * byte数组转换为无符号short整数
     * @param bytes
     * @param off
     * @return short整数
     */
    public static int byte2ToUnsignedShort(byte[] bytes, int off) {
        int high = bytes[off];
        int low = bytes[off + 1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    /**
     * 两个byte合成转换为无符号short整数
     * @param bt1
     * @param bt2
     * @return short整数
     */
    public static int byte2ToUnsignedShort(byte bt1, byte bt2){
        int high = bt1;
        int low = bt2;
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }


    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }



    //广播过滤器
    public static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.ACTION_GET_DEVICE_NAME);
        intentFilter.addAction(BLEService.ACTION_SEARCH_COMPLETED);
        intentFilter.addAction(BLEService.ACTION_DATA_LENGTH_FALSE);
        intentFilter.addAction(BLEService.ACTION_ERROR_CODE);
        return intentFilter;
    };

    //设置listview匹配子项数目
    public static void setListViewHeightBasedOnChildren(ListView listView){
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    //long是1, short是0
    public static void centerToast(Context context, String message, int i){
        Toast toast = Toast.makeText(context,message,i);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static void saveLog(Context context,String filename,String str){
        FileOutputStream out = null;
        BufferedWriter writer= null;
        try{
            File dir = new File(Environment.getExternalStorageDirectory()+"/KincoLog");
            if(!dir.exists())
                dir.mkdir();
            File fs = new File(Environment.getExternalStorageDirectory()+"/KincoLog/"+filename);
            //out = context.openFileOutput("ErrorLog.txt",Context.MODE_PRIVATE);  //软件内部的目录创建
            out = new FileOutputStream(fs);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                if(writer!=null)
                    writer.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.d("BLEService","写入成功!");
        }
    }


}
