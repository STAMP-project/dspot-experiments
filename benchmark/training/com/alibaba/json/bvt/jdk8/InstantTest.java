package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.Instant;
import junit.framework.TestCase;
import org.junit.Assert;


public class InstantTest extends TestCase {
    public void test_for_issue() throws Exception {
        InstantTest.VO vo = new InstantTest.VO();
        vo.setDate(Instant.now());
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        InstantTest.VO vo1 = JSON.parseObject(text, InstantTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private Instant date;

        public Instant getDate() {
            return date;
        }

        public void setDate(Instant date) {
            this.date = date;
        }
    }
}

