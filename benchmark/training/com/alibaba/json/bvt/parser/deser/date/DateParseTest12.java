package com.alibaba.json.bvt.parser.deser.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateParseTest12 extends TestCase {
    public void test() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"20129401\"", DateParseTest12.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

