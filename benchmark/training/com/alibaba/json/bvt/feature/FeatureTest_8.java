package com.alibaba.json.bvt.feature;


import Feature.InitStringFieldAsEmpty;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FeatureTest_8 extends TestCase {
    public void test_get_obj() throws Exception {
        FeatureTest_8.VO value = JSON.parseObject("{}", FeatureTest_8.VO.class, InitStringFieldAsEmpty);
        Assert.assertEquals("", value.id);
    }

    private static class VO {
        public String id;
    }
}

