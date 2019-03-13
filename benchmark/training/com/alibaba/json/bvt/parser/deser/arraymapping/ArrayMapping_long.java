package com.alibaba.json.bvt.parser.deser.arraymapping;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMapping_long extends TestCase {
    public void test_for_error() throws Exception {
        ArrayMapping_long.Model model = JSON.parseObject("[1001,\"wenshao\"]", ArrayMapping_long.Model.class, SupportArrayToBean);
        Assert.assertEquals(1001, model.id);
        Assert.assertEquals("wenshao", model.name);
    }

    public static class Model {
        public long id;

        public String name;
    }
}

