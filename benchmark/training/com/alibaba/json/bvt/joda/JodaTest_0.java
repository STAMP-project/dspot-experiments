package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.joda.time.LocalDate;
import org.junit.Assert;


public class JodaTest_0 extends TestCase {
    public void test_for_issue() throws Exception {
        JodaTest_0.VO vo = new JodaTest_0.VO();
        vo.setDate(LocalDate.now());
        String text = JSON.toJSONString(vo);
        JodaTest_0.VO vo1 = JSON.parseObject(text, JodaTest_0.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public void test_for_issue_1() throws Exception {
        JodaTest_0.VO vo = JSON.parseObject("{\"date\":\"2016-05-06T20:24:28.484\"}", JodaTest_0.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthOfYear());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_issue_2() throws Exception {
        JodaTest_0.VO vo = JSON.parseObject("{\"date\":\"20160506\"}", JodaTest_0.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthOfYear());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    /**
     * ????: ???????? ??
     *
     * @author wuqiong  2017/11/21 16:48
     */
    public void test_for_long() throws Exception {
        String text = "{\"date\":1511248447740}";
        JodaTest_0.VO vo = JSON.parseObject(text, JodaTest_0.VO.class);
        Assert.assertEquals(2017, vo.date.getYear());
        Assert.assertEquals(11, vo.date.getMonthOfYear());
        Assert.assertEquals(21, vo.date.getDayOfMonth());
    }

    public static class VO {
        private LocalDate date;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }
}

