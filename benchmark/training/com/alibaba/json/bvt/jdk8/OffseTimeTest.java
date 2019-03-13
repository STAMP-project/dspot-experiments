package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.OffsetTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class OffseTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        OffseTimeTest.VO vo = new OffseTimeTest.VO();
        vo.setDate(OffsetTime.now());
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        OffseTimeTest.VO vo1 = JSON.parseObject(text, OffseTimeTest.VO.class);
        Assert.assertEquals(vo.getDate(), vo1.getDate());
    }

    public static class VO {
        private OffsetTime date;

        public OffsetTime getDate() {
            return date;
        }

        public void setDate(OffsetTime date) {
            this.date = date;
        }
    }
}

