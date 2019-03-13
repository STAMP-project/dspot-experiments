package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.ZonedDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class ZonedDateTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        ZonedDateTimeTest.VO vo = new ZonedDateTimeTest.VO();
        vo.setDate(ZonedDateTime.now());
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        ZonedDateTimeTest.VO vo1 = JSON.parseObject(text, ZonedDateTimeTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private ZonedDateTime date;

        public ZonedDateTime getDate() {
            return date;
        }

        public void setDate(ZonedDateTime date) {
            this.date = date;
        }
    }
}

