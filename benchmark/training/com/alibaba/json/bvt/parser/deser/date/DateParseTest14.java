package com.alibaba.json.bvt.parser.deser.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateParseTest14 extends TestCase {
    public void test_0_lt() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"19790714130723#56\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_0_gt() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"19790714130723A56\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_1_lt() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"197907141307231#6\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_1_gt() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"197907141307231A6\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_2_lt() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"1979071413072315#\"}", DateParseTest14.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_2_gt() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"date\":\"1979071413072315A\"}", DateParseTest14.VO.class);
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

