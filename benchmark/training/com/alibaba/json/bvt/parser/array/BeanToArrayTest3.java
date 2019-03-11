package com.alibaba.json.bvt.parser.array;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest3 extends TestCase {
    public void test_array() throws Exception {
        BeanToArrayTest3.Model model = new BeanToArrayTest3.Model();
        model.item = new BeanToArrayTest3.Item();
        model.item.id = 1001;
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"item\":[1001]}", text);
        BeanToArrayTest3.Model model2 = JSON.parseObject(text, BeanToArrayTest3.Model.class);
        Assert.assertEquals(model2.item.id, model.item.id);
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
        public BeanToArrayTest3.Item item;
    }

    public static class Item {
        public int id;
    }
}

