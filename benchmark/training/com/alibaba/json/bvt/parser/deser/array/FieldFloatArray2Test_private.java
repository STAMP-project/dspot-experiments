package com.alibaba.json.bvt.parser.deser.array;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/01/2017.
 */
public class FieldFloatArray2Test_private extends TestCase {
    public void test_intArray() throws Exception {
        FieldFloatArray2Test_private.Model model = JSON.parseObject("{\"value\":[[1,2.1,-0.3]]}", FieldFloatArray2Test_private.Model.class);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(1, model.value.length);
        TestCase.assertEquals(3, model.value[0].length);
        TestCase.assertEquals(1.0F, model.value[0][0]);
        TestCase.assertEquals(2.1F, model.value[0][1]);
        TestCase.assertEquals((-0.3F), model.value[0][2]);
    }

    private static class Model {
        public float[][] value;
    }
}

