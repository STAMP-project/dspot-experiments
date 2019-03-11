package com.alibaba.json.bvt.bug;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue64 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue64.VO vo = new Issue64.VO();
        vo.foo = "xxxxxx";
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[\"xxxxxx\"]", text);
        Issue64.VO vo2 = JSON.parseObject(text, Issue64.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo2.foo, vo.foo);
    }

    public static class VO {
        public String foo = "bar";
    }
}

