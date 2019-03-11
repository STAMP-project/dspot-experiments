package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.OffsetDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class OffsetDateTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        OffsetDateTimeTest.VO vo = new OffsetDateTimeTest.VO();
        vo.setDate(OffsetDateTime.now());
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        OffsetDateTimeTest.VO vo1 = JSON.parseObject(text, OffsetDateTimeTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private OffsetDateTime date;

        public OffsetDateTime getDate() {
            return date;
        }

        public void setDate(OffsetDateTime date) {
            this.date = date;
        }
    }
}

