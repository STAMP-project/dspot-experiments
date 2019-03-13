package com.alibaba.json.demo;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateDemo extends TestCase {
    public void test_0() throws Exception {
        Date date = new Date();
        String text = JSON.toJSONString(date, DateDemo.mapping);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals(JSON.toJSONString(format.format(date)), text);
    }

    private static SerializeConfig mapping = new SerializeConfig();

    static {
        DateDemo.mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
    }
}

