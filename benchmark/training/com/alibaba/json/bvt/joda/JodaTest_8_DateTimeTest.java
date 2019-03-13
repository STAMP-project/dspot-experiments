package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.Assert;


public class JodaTest_8_DateTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        JodaTest_8_DateTimeTest.VO vo = new JodaTest_8_DateTimeTest.VO();
        vo.date = DateTime.now();
        String text = JSON.toJSONString(vo);
        JodaTest_8_DateTimeTest.VO vo1 = JSON.parseObject(text, JodaTest_8_DateTimeTest.VO.class);
        Assert.assertEquals(vo.date.toDate(), vo1.date.toDate());
    }

    /**
     * ????: ???????? ??
     *
     * @author wuqiong  2017/11/21 16:48
     */
    public void test_for_long() throws Exception {
        String text = "{\"date\":1511248447740}";
        JodaTest_8_DateTimeTest.VO vo = JSON.parseObject(text, JodaTest_8_DateTimeTest.VO.class);
        Assert.assertEquals(("timeZone " + (TimeZone.getDefault())), 15, vo.date.getHourOfDay());
        Assert.assertEquals(14, vo.date.getMinuteOfHour());
        Assert.assertEquals(7, vo.date.getSecondOfMinute());
    }

    public static class VO {
        private DateTime date;

        public DateTime getDate() {
            return date;
        }

        public void setDate(DateTime date) {
            this.date = date;
        }
    }
}

