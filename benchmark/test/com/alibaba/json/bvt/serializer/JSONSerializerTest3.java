package com.alibaba.json.bvt.serializer;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONSerializerTest3 extends TestCase {
    public void test_0() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.setDateFormat("yyyy");
        Assert.assertEquals("yyyy", ((SimpleDateFormat) (serializer.getDateFormat())).toPattern());
        Assert.assertEquals("yyyy", serializer.getDateFormatPattern());
        serializer.setDateFormat("yyyy-MM");
        Assert.assertEquals("yyyy-MM", ((SimpleDateFormat) (serializer.getDateFormat())).toPattern());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(defaultTimeZone);
        serializer.setDateFormat(format);
        Assert.assertEquals("yyyy-MM-dd", serializer.getDateFormatPattern());
        serializer.close();
    }
}

