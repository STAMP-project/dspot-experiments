package com.alibaba.json.bvt.basicType;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import junit.framework.TestCase;


public class FloatTest3_random extends TestCase {
    public void test_ran() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < ((1000 * 1000) * 1); ++i) {
            float val = rand.nextFloat();
            String str = JSON.toJSONString(new FloatTest3_random.Model(val));
            FloatTest3_random.Model m = JSON.parseObject(str, FloatTest3_random.Model.class);
            TestCase.assertEquals(val, m.value);
        }
    }

    public void test_ran_2() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < ((1000 * 1000) * 1); ++i) {
            float val = rand.nextFloat();
            String str = JSON.toJSONString(new FloatTest3_random.Model(val), BeanToArray);
            FloatTest3_random.Model m = JSON.parseObject(str, FloatTest3_random.Model.class, SupportArrayToBean);
            TestCase.assertEquals(val, m.value);
        }
    }

    public void test_ran_3() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < ((1000 * 1000) * 1); ++i) {
            float val = rand.nextFloat();
            String str = JSON.toJSONString(Collections.singletonMap("val", val));
            float val2 = JSON.parseObject(str).getFloatValue("val");
            TestCase.assertEquals(val, val2);
        }
    }

    public void test_ran_4() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < ((1000 * 1000) * 1); ++i) {
            float val = rand.nextFloat();
            HashMap map = new HashMap();
            map.put("val", val);
            String str = JSON.toJSONString(map);
            float val2 = JSON.parseObject(str).getFloatValue("val");
            TestCase.assertEquals(val, val2);
        }
    }

    public static class Model {
        public float value;

        public Model() {
        }

        public Model(float value) {
            this.value = value;
        }
    }
}

