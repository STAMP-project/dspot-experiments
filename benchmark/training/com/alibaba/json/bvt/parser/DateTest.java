package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.sql.Date;
import java.sql.Timestamp;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest extends TestCase {
    public void test_0() throws Exception {
        Assert.assertNull(JSON.parseObject("", Date.class));
        Assert.assertNull(JSON.parseObject(null, Date.class));
        Assert.assertNull(JSON.parseObject("null", Date.class));
        Assert.assertNull(JSON.parseObject("\"\"", Date.class));
        Assert.assertNull(JSON.parseObject("", java.util.Date.class));
        Assert.assertNull(JSON.parseObject(null, java.util.Date.class));
        Assert.assertNull(JSON.parseObject("null", java.util.Date.class));
        Assert.assertNull(JSON.parseObject("\"\"", java.util.Date.class));
        Assert.assertNull(JSON.parseObject("", Timestamp.class));
        Assert.assertNull(JSON.parseObject(null, Timestamp.class));
        Assert.assertNull(JSON.parseObject("null", Timestamp.class));
        Assert.assertNull(JSON.parseObject("\"\"", Timestamp.class));
        Assert.assertNull(JSON.parseObject("{date:\"\"}", DateTest.Entity.class).getDate());
    }

    public static class Entity {
        private java.util.Date date;

        public java.util.Date getDate() {
            return date;
        }

        public void setDate(java.util.Date date) {
            this.date = date;
        }
    }
}

