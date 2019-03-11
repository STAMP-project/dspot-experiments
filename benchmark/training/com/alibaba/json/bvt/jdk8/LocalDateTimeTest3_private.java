package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.time.LocalDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTimeTest3_private extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"date\":\"20111203\"}";
        LocalDateTimeTest3_private.VO vo = JSON.parseObject(text, LocalDateTimeTest3_private.VO.class);
        Assert.assertEquals(2011, vo.date.getYear());
        Assert.assertEquals(12, vo.date.getMonthValue());
        Assert.assertEquals(3, vo.date.getDayOfMonth());
        Assert.assertEquals(0, vo.date.getHour());
        Assert.assertEquals(0, vo.date.getMinute());
        Assert.assertEquals(0, vo.date.getSecond());
        Assert.assertEquals(text, JSON.toJSONString(vo));
    }

    private static class VO {
        @JSONField(format = "yyyyMMdd")
        public LocalDateTime date;
    }
}

