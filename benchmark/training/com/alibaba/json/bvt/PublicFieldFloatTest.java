package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class PublicFieldFloatTest extends TestCase {
    public static class VO {
        public float id;
    }

    public void test_codec() throws Exception {
        PublicFieldFloatTest.VO vo = new PublicFieldFloatTest.VO();
        vo.id = 123.4F;
        String str = JSON.toJSONString(vo);
        PublicFieldFloatTest.VO vo1 = JSON.parseObject(str, PublicFieldFloatTest.VO.class);
        Assert.assertTrue(((vo1.id) == (vo.id)));
    }
}

