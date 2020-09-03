package com.kinco.kmlink;

import android.util.Log;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.utils.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
//        boolean show = Boolean.parseBoolean("true");
//        System.out.println(show);
//        ParameterBean bean = new ParameterBean();
//        bean.setType(3);
//        bean.addDescriptionItem("0.1");
//        bean.addDescriptionItem("%");
//        bean.addDescriptionItem("0~6000");
//        byte[] message = {0x05,0x03,0x00,0x00,0x00,0x77,0x09};
//        util.setParameterByMessage(message, bean);
//        System.out.println(bean.getCurrentValue()+"%");
        byte[] bytes = util.hexStringToBytes("ABCDEF");
        System.out.println("1234abCd".toUpperCase());
    }



}