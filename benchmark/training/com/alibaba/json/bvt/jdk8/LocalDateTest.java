package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTest extends TestCase {
    public void test_for_issue() throws Exception {
        LocalDateTest.VO vo = new LocalDateTest.VO();
        vo.setDate(LocalDate.now());
        String text = JSON.toJSONString(vo);
        LocalDateTest.VO vo1 = JSON.parseObject(text, LocalDateTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    /**
     * ????: ???????? ??
     *
     * @author wuqiong  2017/11/21 16:48
     */
    public void test_for_long() throws Exception {
        String text = "{\"date\":1511248447740}";
        LocalDateTest.VO vo = JSON.parseObject(text, LocalDateTest.VO.class);
        Assert.assertEquals(2017, vo.date.getYear());
        Assert.assertEquals(11, vo.date.getMonthValue());
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

