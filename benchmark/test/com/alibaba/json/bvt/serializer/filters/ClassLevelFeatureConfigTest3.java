package com.alibaba.json.bvt.serializer.filters;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassLevelFeatureConfigTest3 extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.config(ClassLevelFeatureConfigTest3.Model.class, BeanToArray, false);
        ClassLevelFeatureConfigTest3.Model model = new ClassLevelFeatureConfigTest3.Model();
        model.id = 1001;
        Assert.assertEquals("{\"id\":1001}", JSON.toJSONString(model, config));
        config.config(ClassLevelFeatureConfigTest3.Model.class, BeanToArray, true);
        Assert.assertEquals("[1001]", JSON.toJSONString(model, config));
    }

    @JSONType(serialzeFeatures = SerializerFeature.BeanToArray)
    public static class Model {
        public int id;
    }
}

