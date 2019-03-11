package com.alibaba.json.bvt.serializer;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class SimpleDataFormatSerializerTest extends TestCase {
    private static SerializeConfig mapping = new SerializeConfig();

    static {
        SimpleDataFormatSerializerTest.mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
    }

    public void test_0() throws Exception {
        Date date = new Date();
        String text = JSON.toJSONString(date, SimpleDataFormatSerializerTest.mapping);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format2.setTimeZone(defaultTimeZone);
        Assert.assertEquals(JSON.toJSONString(format.format(date)), text);
        Assert.assertEquals(JSON.toJSONString(format2.format(date)), text);
    }
}

