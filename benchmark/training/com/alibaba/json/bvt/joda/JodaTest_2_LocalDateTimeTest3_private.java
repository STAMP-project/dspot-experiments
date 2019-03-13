package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.joda.time.LocalDateTime;


public class JodaTest_2_LocalDateTimeTest3_private extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"date\":\"20111203\"}";
        JodaTest_2_LocalDateTimeTest3_private.VO vo = JSON.parseObject(text, JodaTest_2_LocalDateTimeTest3_private.VO.class);
        TestCase.assertEquals(2011, vo.date.getYear());
        TestCase.assertEquals(12, vo.date.getMonthOfYear());
        TestCase.assertEquals(3, vo.date.getDayOfMonth());
        TestCase.assertEquals(0, vo.date.getHourOfDay());
        TestCase.assertEquals(0, vo.date.getMinuteOfHour());
        TestCase.assertEquals(0, vo.date.getSecondOfMinute());
        TestCase.assertEquals(text, JSON.toJSONString(vo));
    }

    private static class VO {
        @JSONField(format = "yyyyMMdd")
        public LocalDateTime date;
    }
}

