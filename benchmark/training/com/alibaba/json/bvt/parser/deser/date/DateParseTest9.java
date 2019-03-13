package com.alibaba.json.bvt.parser.deser.date;


import CalendarCodec.instance;
import JSONToken.LITERAL_INT;
import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateParseTest9 extends TestCase {
    public void test_date() throws Exception {
        String text = "\"/Date(1242357713797+0800)/\"";
        Date date = JSON.parseObject(text, Date.class);
        Assert.assertEquals(date.getTime(), 1242357713797L);
        Assert.assertEquals(LITERAL_INT, instance.getFastMatchToken());
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"/Date(1242357713797A0800)/\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"/Date(1242357713797#0800)/\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}

