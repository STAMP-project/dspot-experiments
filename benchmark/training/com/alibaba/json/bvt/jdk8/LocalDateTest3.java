package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTest3 extends TestCase {
    public void test_for_issue() throws Exception {
        LocalDateTest3.VO vo = JSON.parseObject("{\"date\":\"2016-05-06\"}", LocalDateTest3.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public static class VO {
        public LocalDate date;
    }
}

