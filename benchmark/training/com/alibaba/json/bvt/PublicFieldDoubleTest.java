package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class PublicFieldDoubleTest extends TestCase {
    public static class VO {
        public double id;
    }

    public void test_codec() throws Exception {
        PublicFieldDoubleTest.VO vo = new PublicFieldDoubleTest.VO();
        vo.id = 12.34;
        String str = JSON.toJSONString(vo);
        PublicFieldDoubleTest.VO vo1 = JSON.parseObject(str, PublicFieldDoubleTest.VO.class);
        Assert.assertTrue(((vo1.id) == (vo.id)));
    }

    public void test_nan() throws Exception {
        JSON.parseObject("{\"id\":NaN}", PublicFieldDoubleTest.VO.class);
    }
}

