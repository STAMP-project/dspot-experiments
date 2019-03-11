package com.alibaba.json.bvt.serializer.filters;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassLevelFeatureConfigTest2 extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.config(ClassLevelFeatureConfigTest2.Model.class, BeanToArray, true);
        ClassLevelFeatureConfigTest2.Model model = new ClassLevelFeatureConfigTest2.Model();
        model.id = 1001;
        Assert.assertEquals("[1001]", JSON.toJSONString(model, config));
        config.config(ClassLevelFeatureConfigTest2.Model.class, BeanToArray, false);
        Assert.assertEquals("{\"id\":1001}", JSON.toJSONString(model, config));
    }

    public static class Model {
        public int id;
    }
}

