package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.joda.time.LocalTime;
import org.junit.Assert;


public class JodaTest_3_LocalTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        JodaTest_3_LocalTimeTest.VO vo = new JodaTest_3_LocalTimeTest.VO();
        vo.setDate(LocalTime.now());
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        JodaTest_3_LocalTimeTest.VO vo1 = JSON.parseObject(text, JodaTest_3_LocalTimeTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    /**
     * ????: ???????? ??
     *
     * @author wuqiong  2017/11/21 16:48
     */
    public void test_for_long() throws Exception {
        String text = "{\"date\":1511248447740}";
        JodaTest_3_LocalTimeTest.VO vo = JSON.parseObject(text, JodaTest_3_LocalTimeTest.VO.class);
        Assert.assertEquals(15, vo.date.getHourOfDay());
        Assert.assertEquals(14, vo.date.getMinuteOfHour());
        Assert.assertEquals(7, vo.date.getSecondOfMinute());
    }

    public static class VO {
        private LocalTime date;

        public LocalTime getDate() {
            return date;
        }

        public void setDate(LocalTime date) {
            this.date = date;
        }
    }
}

