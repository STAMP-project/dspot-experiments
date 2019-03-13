package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeZoneTest extends TestCase {
    public void test_timezone() throws Exception {
        TimeZone tz1 = TimeZone.getDefault();
        String text = JSON.toJSONString(tz1);
        Assert.assertEquals(JSON.toJSONString(tz1.getID()), text);
        TimeZone tz2 = JSON.parseObject(text, TimeZone.class);
        Assert.assertEquals(tz1.getID(), tz2.getID());
    }
}

