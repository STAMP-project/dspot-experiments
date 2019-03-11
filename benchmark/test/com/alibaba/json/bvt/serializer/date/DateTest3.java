package com.alibaba.json.bvt.serializer.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest3 extends TestCase {
    public void test_date() throws Exception {
        String text = "{\"gmtCreate\":\"2014-08-21T09:51:36.25+07:00\"}";
        Date date = JSON.parseObject(text, DateTest3.VO.class).getGmtCreate();
        Assert.assertNotNull(date);
    }

    public static class VO {
        private Date gmtCreate;

        public Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }
    }
}

