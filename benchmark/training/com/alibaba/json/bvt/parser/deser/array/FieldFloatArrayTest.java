package com.alibaba.json.bvt.parser.deser.array;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/01/2017.
 */
public class FieldFloatArrayTest extends TestCase {
    public void test_intArray() throws Exception {
        FieldFloatArrayTest.Model model = JSON.parseObject("{\"value\":[1,2.1,-0.3]}", FieldFloatArrayTest.Model.class);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(3, model.value.length);
        TestCase.assertEquals(1.0F, model.value[0]);
        TestCase.assertEquals(2.1F, model.value[1]);
        TestCase.assertEquals((-0.3F), model.value[2]);
    }

    public static class Model {
        public float[] value;
    }
}

