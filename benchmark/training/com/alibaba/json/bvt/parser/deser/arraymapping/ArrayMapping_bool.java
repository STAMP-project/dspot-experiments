package com.alibaba.json.bvt.parser.deser.arraymapping;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMapping_bool extends TestCase {
    public void test_for_true() throws Exception {
        ArrayMapping_bool.Model model = JSON.parseObject("[true,\"wenshao\"]", ArrayMapping_bool.Model.class, SupportArrayToBean);
        Assert.assertEquals(true, model.id);
        Assert.assertEquals("wenshao", model.name);
    }

    public void test_for_false() throws Exception {
        ArrayMapping_bool.Model model = JSON.parseObject("[false,\"wenshao\"]", ArrayMapping_bool.Model.class, SupportArrayToBean);
        Assert.assertEquals(false, model.id);
        Assert.assertEquals("wenshao", model.name);
    }

    public static class Model {
        public boolean id;

        public String name;
    }
}

