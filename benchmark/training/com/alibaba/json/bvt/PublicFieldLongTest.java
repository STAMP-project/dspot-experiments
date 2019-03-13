package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class PublicFieldLongTest extends TestCase {
    public static class VO {
        public long id;
    }

    public void test_codec() throws Exception {
        PublicFieldLongTest.VO vo = new PublicFieldLongTest.VO();
        vo.id = 1234;
        String str = JSON.toJSONString(vo);
        PublicFieldLongTest.VO vo1 = JSON.parseObject(str, PublicFieldLongTest.VO.class);
        Assert.assertEquals(vo1.id, vo.id);
    }
}

