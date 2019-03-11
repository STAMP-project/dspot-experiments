package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeConfigTest2 extends TestCase {
    public void test_1() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setTypeKey("%type");
        Assert.assertEquals("%type", config.getTypeKey());
        SerializeConfigTest2.Model model = new SerializeConfigTest2.Model();
        model.value = 1001;
        Assert.assertEquals("{\"%type\":\"com.alibaba.json.bvt.serializer.SerializeConfigTest2$Model\",\"value\":1001}", JSON.toJSONString(model, config, WriteClassName));
    }

    public static class Model {
        public int value;
    }
}

