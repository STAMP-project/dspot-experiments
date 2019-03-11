package com.alibaba.json.bvt.parser.deser.array;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/01/2017.
 */
public class FieldIntArrayTest_private extends TestCase {
    public void test_intArray() throws Exception {
        FieldIntArrayTest_private.Model model = JSON.parseObject("{\"value\":[1,2,3]}", FieldIntArrayTest_private.Model.class);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(3, model.value.length);
        TestCase.assertEquals(1, model.value[0]);
        TestCase.assertEquals(2, model.value[1]);
        TestCase.assertEquals(3, model.value[2]);
    }

    private static class Model {
        public int[] value;
    }
}

