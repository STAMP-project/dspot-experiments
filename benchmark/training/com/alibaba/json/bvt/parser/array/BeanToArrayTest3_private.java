package com.alibaba.json.bvt.parser.array;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest3_private extends TestCase {
    public void test_array() throws Exception {
        BeanToArrayTest3_private.Model model = new BeanToArrayTest3_private.Model();
        model.item = new BeanToArrayTest3_private.Item();
        model.item.id = 1001;
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"item\":[1001]}", text);
        BeanToArrayTest3_private.Model model2 = JSON.parseObject(text, BeanToArrayTest3_private.Model.class);
        Assert.assertEquals(model2.item.id, model.item.id);
    }

    private static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
        public BeanToArrayTest3_private.Item item;
    }

    private static class Item {
        public int id;
    }
}

