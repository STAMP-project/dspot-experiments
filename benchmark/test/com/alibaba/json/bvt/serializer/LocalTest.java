package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalTest extends TestCase {
    public void test_timezone() throws Exception {
        String text = JSON.toJSONString(Locale.CHINA);
        Assert.assertEquals(JSON.toJSONString(Locale.CHINA.toString()), text);
        Locale locale = JSON.parseObject(text, Locale.class);
        Assert.assertEquals(Locale.CHINA, locale);
    }
}

