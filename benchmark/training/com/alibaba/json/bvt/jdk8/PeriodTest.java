package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.Period;
import junit.framework.TestCase;
import org.junit.Assert;


public class PeriodTest extends TestCase {
    public void test_for_issue() throws Exception {
        PeriodTest.VO vo = new PeriodTest.VO();
        vo.setDate(Period.of(3, 2, 11));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        PeriodTest.VO vo1 = JSON.parseObject(text, PeriodTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private Period date;

        public Period getDate() {
            return date;
        }

        public void setDate(Period date) {
            this.date = date;
        }
    }
}

