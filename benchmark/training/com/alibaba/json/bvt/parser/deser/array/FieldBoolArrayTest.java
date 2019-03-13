package com.alibaba.json.bvt.parser.deser.array;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/01/2017.
 */
public class FieldBoolArrayTest extends TestCase {
    public void test_intArray() throws Exception {
        FieldBoolArrayTest.Model model = JSON.parseObject("{\"value\":[1,null,true,false,0]}", FieldBoolArrayTest.Model.class);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(5, model.value.length);
        TestCase.assertEquals(true, model.value[0]);
        TestCase.assertEquals(false, model.value[1]);
        TestCase.assertEquals(true, model.value[2]);
        TestCase.assertEquals(false, model.value[3]);
        TestCase.assertEquals(false, model.value[4]);
    }

    public static class Model {
        public boolean[] value;
    }
}

