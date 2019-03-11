package com.alibaba.json.bvt.parser.deser.arraymapping;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMapping_double extends TestCase {
    public void test_float() throws Exception {
        ArrayMapping_double.Model model = JSON.parseObject("[123.45,\"wenshao\"]", ArrayMapping_double.Model.class, SupportArrayToBean);
        Assert.assertTrue((123.45 == (model.id)));
        Assert.assertEquals("wenshao", model.name);
    }

    public static class Model {
        public double id;

        public String name;
    }
}

