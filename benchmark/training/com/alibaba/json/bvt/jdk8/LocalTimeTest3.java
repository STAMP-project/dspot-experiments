package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalTimeTest3 extends TestCase {
    public void test_for_issue() throws Exception {
        LocalTimeTest3.VO vo1 = JSON.parseObject("{\"date\":\"2016-05-05T20:24:28.484\"}", LocalTimeTest3.VO.class);
        Assert.assertEquals(20, vo1.date.getHour());
        Assert.assertEquals(24, vo1.date.getMinute());
        Assert.assertEquals(28, vo1.date.getSecond());
        Assert.assertEquals(484000000, vo1.date.getNano());
    }

    public static class VO {
        public LocalTime date;
    }
}

