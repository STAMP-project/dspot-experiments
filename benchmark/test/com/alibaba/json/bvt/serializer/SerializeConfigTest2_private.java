package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeConfigTest2_private extends TestCase {
    public void test_1() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setTypeKey("%type");
        Assert.assertEquals("%type", config.getTypeKey());
        SerializeConfigTest2_private.Model model = new SerializeConfigTest2_private.Model();
        model.value = 1001;
        Assert.assertEquals("{\"%type\":\"com.alibaba.json.bvt.serializer.SerializeConfigTest2_private$Model\",\"value\":1001}", JSON.toJSONString(model, config, WriteClassName));
    }

    private static class Model {
        public int value;
    }
}

