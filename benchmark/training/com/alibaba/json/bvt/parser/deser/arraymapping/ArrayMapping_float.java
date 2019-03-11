package com.alibaba.json.bvt.parser.deser.arraymapping;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMapping_float extends TestCase {
    public void test_float() throws Exception {
        ArrayMapping_float.Model model = JSON.parseObject("[123.45,\"wenshao\"]", ArrayMapping_float.Model.class, SupportArrayToBean);
        Assert.assertTrue((123.45F == (model.id)));
        Assert.assertEquals("wenshao", model.name);
    }

    public static class Model {
        public float id;

        public String name;
    }
}

