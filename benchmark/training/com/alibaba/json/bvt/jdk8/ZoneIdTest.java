package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.ZoneId;
import junit.framework.TestCase;
import org.junit.Assert;


public class ZoneIdTest extends TestCase {
    public void test_for_issue() throws Exception {
        ZoneIdTest.VO vo = new ZoneIdTest.VO();
        vo.setDate(ZoneId.of("Europe/Paris"));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        ZoneIdTest.VO vo1 = JSON.parseObject(text, ZoneIdTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private ZoneId date;

        public ZoneId getDate() {
            return date;
        }

        public void setDate(ZoneId date) {
            this.date = date;
        }
    }
}

