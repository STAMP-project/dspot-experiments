package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTimeTest4 extends TestCase {
    public void test_for_issue() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2016, 5, 6, 9, 3, 16);
        LocalDateTimeTest4.VO vo = new LocalDateTimeTest4.VO();
        vo.setDate(dateTime);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"date\":\"2016-05-06T09:03:16\"}", text);
        LocalDateTimeTest4.VO vo1 = JSON.parseObject(text, LocalDateTimeTest4.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private LocalDateTime date;

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }
}

