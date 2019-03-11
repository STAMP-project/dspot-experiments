package com.alibaba.json.bvt.parser.deser.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateParseTest10 extends TestCase {
    public void test_date() throws Exception {
        String text = "{\"value\":\"1979-07-14\"}";
        DateParseTest10.VO vo = JSON.parseObject(text, DateParseTest10.VO.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        Assert.assertEquals(vo.getValue(), dateFormat.parse("1979-07-14").getTime());
    }

    public static class VO {
        private long value;

        public long getValue() {
            return value;
        }

        public DateParseTest10.VO setValue(long value) {
            this.value = value;
            return this;
        }
    }
}

