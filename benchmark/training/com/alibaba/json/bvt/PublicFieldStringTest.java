package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class PublicFieldStringTest extends TestCase {
    public static class VO {
        public String id;
    }

    public void test_codec() throws Exception {
        PublicFieldStringTest.VO vo = new PublicFieldStringTest.VO();
        vo.id = "x12345";
        String str = JSON.toJSONString(vo);
        PublicFieldStringTest.VO vo1 = JSON.parseObject(str, PublicFieldStringTest.VO.class);
        Assert.assertEquals(vo1.id, vo.id);
    }
}

