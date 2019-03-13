package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTimeTest2 extends TestCase {
    public void test_for_issue() throws Exception {
        LocalDateTimeTest2.VO vo = JSON.parseObject("{\"date\":\"2011-12-03\"}", LocalDateTimeTest2.VO.class);
        Assert.assertEquals(2011, vo.date.getYear());
        Assert.assertEquals(12, vo.date.getMonthValue());
        Assert.assertEquals(3, vo.date.getDayOfMonth());
        Assert.assertEquals(0, vo.date.getHour());
        Assert.assertEquals(0, vo.date.getMinute());
        Assert.assertEquals(0, vo.date.getSecond());
    }

    public static class VO {
        public LocalDateTime date;
    }
}

