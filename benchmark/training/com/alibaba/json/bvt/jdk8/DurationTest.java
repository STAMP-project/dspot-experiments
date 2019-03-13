package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.Duration;
import junit.framework.TestCase;
import org.junit.Assert;


public class DurationTest extends TestCase {
    public void test_for_issue() throws Exception {
        DurationTest.VO vo = new DurationTest.VO();
        vo.setDate(Duration.ofHours(3));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        DurationTest.VO vo1 = JSON.parseObject(text, DurationTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private Duration date;

        public Duration getDate() {
            return date;
        }

        public void setDate(Duration date) {
            this.date = date;
        }
    }
}

