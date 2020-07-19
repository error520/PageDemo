package com.kinco.kmlink.utils;

import android.content.Context;
import android.util.Log;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class RawDataHelper {
    private Context context;
    private LoadFinishedCallback callback;

    public  RawDataHelper(Context context){
        this.context = context;
    }

    public void getParameterFromRaw(int resourceId,LoadFinishedCallback callback){
        this.callback = callback;
        new Thread(()->{
            try{
                InputStream in= context.getResources().openRawResource(resourceId);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                XMLReader reader = factory.newSAXParser().getXMLReader();
                MySaxHandler handler = new RawDataHelper.MySaxHandler();
                reader.setContentHandler(handler);
                reader.parse(new InputSource(in));
            }catch(Exception e) {
                e.toString();
            }
        }).start();
    }

    public interface LoadFinishedCallback{
        public boolean onLoadFinished(List<ParameterBean> parameterList);
    }


    private class MySaxHandler extends DefaultHandler {
        private String TAG = "ParameterHelper";
        private String nodeName="";
        private StringBuilder id;
        private StringBuilder name;
        private StringBuilder type;
        private StringBuilder item;
        private List<String> itemList;
        private List<ParameterBean> parameterList;

        public MySaxHandler(){

        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            name = new StringBuilder();
            id = new StringBuilder();
            type = new StringBuilder();
            item = new StringBuilder();
            itemList = new LinkedList<>();
            parameterList = new ArrayList<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            nodeName = localName;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            switch (nodeName){
                case "name": name.append(ch,start,length);break;
                case "id":id.append(ch,start,length);break;
                case "type": type.append(ch,start,length);break;
                case "item": item.append(ch,start,length);break;
            }


        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if("item".equals(localName)){
                itemList.add(item.toString().trim());
                item.setLength(0);
            }else if("Parameter".equals(localName)){
                String idString = id.toString().trim();
//                Log.d("ParameterHelper","id: "+idString);
//                Log.d("ParameterHelper","name: "+name.toString().trim());
//                Log.d("ParameterHelper","type: "+type.toString().trim());
//                Log.d("ParameterHelper","options: \n");
//                for(String i:itemList){
//                    Log.d("ParameterHelper",i);
//                }
//                Log.d("ParameterHelper","address: "+idToAddress(idString));
//                Log.d(TAG,"-------------------------------------------\n");
                ParameterBean parameter = new ParameterBean();
                parameter.setName(name.toString().trim());
                parameter.setType(Integer.valueOf(type.toString().trim()));
                parameter.setDescription(itemList);
                parameter.setAddress(idToAddress(idString));
                parameterList.add(parameter);
                name.setLength(0);
                id.setLength(0);
                type.setLength(0);
                itemList.clear();
            }else if("Parameters".equals(localName)){   //文档末, 全部读取完
                if(RawDataHelper.this.callback!=null){
                    RawDataHelper.this.callback.onLoadFinished(parameterList);
                }
            }
        }

        private String idToAddress(String id){
            String dec = id.split("\\.")[1];
            int data  =Integer.parseInt(dec,10);
            String address = Integer.toString(data,16);
            while(address.length()<4){
                address = "0"+address;
            }
            return address;
        }
    }


}
