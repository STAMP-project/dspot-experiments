package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.time.ZonedDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class ZonedDateTimeTest2 extends TestCase {
    public void test_for_issue() throws Exception {
        ZonedDateTimeTest2.VO vo = new ZonedDateTimeTest2.VO();
        vo.date = ZonedDateTime.now();
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        ZonedDateTimeTest2.VO vo1 = JSON.parseObject(text, ZonedDateTimeTest2.VO.class);
        Assert.assertEquals(vo.date.getSecond(), vo1.date.getSecond());
    }

    public static class VO {
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        public ZonedDateTime date;
    }
}

