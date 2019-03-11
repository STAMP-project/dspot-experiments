package com.alibaba.json.bvt.parser.deser.array;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/01/2017.
 */
public class FieldIntArrayTest2 extends TestCase {
    public void test_intArray() throws Exception {
        FieldIntArrayTest2.Model model = JSON.parseObject("{\"value\":[1,null,3]}", FieldIntArrayTest2.Model.class);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(3, model.value.length);
        TestCase.assertEquals(1, model.value[0]);
        TestCase.assertEquals(0, model.value[1]);
        TestCase.assertEquals(3, model.value[2]);
    }

    public static class Model {
        public int[] value;
    }
}

