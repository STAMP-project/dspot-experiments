package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        LocalTimeTest.VO vo = new LocalTimeTest.VO();
        vo.setDate(LocalTime.now());
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        LocalTimeTest.VO vo1 = JSON.parseObject(text, LocalTimeTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    /**
     * ????: ???????? ??
     *
     * @author wuqiong  2017/11/21 16:48
     */
    public void test_for_long() throws Exception {
        String text = "{\"date\":1511248447740}";
        LocalTimeTest.VO vo = JSON.parseObject(text, LocalTimeTest.VO.class);
        Assert.assertEquals(15, vo.date.getHour());
        Assert.assertEquals(14, vo.date.getMinute());
        Assert.assertEquals(7, vo.date.getSecond());
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

