package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongFieldTest_4 extends TestCase {
    public void test_min() throws Exception {
        Random random = new Random();
        LongFieldTest_4.Model[] array = new LongFieldTest_4.Model[2048];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = new LongFieldTest_4.Model();
            array[i].value = random.nextLong();
        }
        String text = JSON.toJSONString(array);
        LongFieldTest_4.Model[] array2 = JSON.parseObject(text, LongFieldTest_4.Model[].class);
        Assert.assertEquals(array.length, array2.length);
        for (int i = 0; i < (array.length); ++i) {
            Assert.assertEquals(array[i].value, array2[i].value);
        }
    }

    @JSONType(serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
    public static class Model {
        public long value;
    }
}

