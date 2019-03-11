package com.alibaba.json.bvt.parser.deser.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateDeserializerTest extends TestCase {
    public void test_date() throws Exception {
        long millis = System.currentTimeMillis();
        Assert.assertEquals(new Date(millis), JSON.parseObject((("'" + millis) + "'"), Date.class));
    }
}

