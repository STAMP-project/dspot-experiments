package com.alibaba.json.bvt.basicType;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.Random;
import junit.framework.TestCase;


public class FloatTest3_array_random extends TestCase {
    public void test_ran() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < ((1000 * 1000) * 1); ++i) {
            float val = rand.nextFloat();
            String str = JSON.toJSONString(new FloatTest3_array_random.Model(new float[]{ val }));
            FloatTest3_array_random.Model m = JSON.parseObject(str, FloatTest3_array_random.Model.class);
            TestCase.assertEquals(val, m.value[0]);
        }
    }

    public void test_ran_2() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < ((1000 * 1000) * 10); ++i) {
            float val = rand.nextFloat();
            String str = JSON.toJSONString(new FloatTest3_array_random.Model(new float[]{ val }), BeanToArray);
            FloatTest3_array_random.Model m = JSON.parseObject(str, FloatTest3_array_random.Model.class, SupportArrayToBean);
            TestCase.assertEquals(val, m.value[0]);
        }
    }

    public static class Model {
        public float[] value;

        public Model() {
        }

        public Model(float[] value) {
            this.value = value;
        }
    }
}

