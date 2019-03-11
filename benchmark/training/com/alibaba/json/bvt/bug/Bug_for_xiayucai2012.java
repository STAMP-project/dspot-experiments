package com.alibaba.json.bvt.bug;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_xiayucai2012 extends TestCase {
    public void test_for_xiayucai2012() throws Exception {
        String text = "{\"date\":\"0000-00-00 00:00:00\"}";
        JSONObject json = JSON.parseObject(text);
        Date date = json.getObject("date", Date.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat(JSON.DEFFAULT_DATE_FORMAT, JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        Assert.assertEquals(dateFormat.parse(json.getString("date")), date);
    }
}

