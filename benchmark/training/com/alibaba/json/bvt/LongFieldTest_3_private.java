package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongFieldTest_3_private extends TestCase {
    public void test_min() throws Exception {
        Random random = new Random();
        LongFieldTest_3_private.Model[] array = new LongFieldTest_3_private.Model[8192];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = new LongFieldTest_3_private.Model();
            array[i].value = random.nextLong();
        }
        String text = JSON.toJSONString(array);
        LongFieldTest_3_private.Model[] array2 = JSON.parseObject(text, LongFieldTest_3_private.Model[].class);
        Assert.assertEquals(array.length, array2.length);
        for (int i = 0; i < (array.length); ++i) {
            Assert.assertEquals(array[i].value, array2[i].value);
        }
    }

    private static class Model {
        public long value;
    }
}

