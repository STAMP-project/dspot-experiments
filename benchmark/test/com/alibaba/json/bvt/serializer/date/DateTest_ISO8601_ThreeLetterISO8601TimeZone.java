package com.alibaba.json.bvt.serializer.date;


import com.alibaba.fastjson.JSON;
import java.util.Calendar;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest_ISO8601_ThreeLetterISO8601TimeZone extends TestCase {
    public void test_date_9() throws Exception {
        String text = "{\"gmtCreate\":\"2014-08-21T09:51:36.235+09:00\"}";
        Calendar date = JSON.parseObject(text, DateTest_ISO8601_ThreeLetterISO8601TimeZone.VO.class).getGmtCreate();
        Assert.assertNotNull(date);
        Assert.assertEquals(9, ((date.getTimeZone().getRawOffset()) / (3600 * 1000)));
    }

    public static class VO {
        private Calendar gmtCreate;

        public Calendar getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Calendar gmtCreate) {
            this.gmtCreate = gmtCreate;
        }
    }
}

