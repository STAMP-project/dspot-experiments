package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/10/15.
 */
public class EmptyArrayAsNullTest extends TestCase {
    public void test_emtpyAsNull() throws Exception {
        String text = "{\"value\":[]}";
        EmptyArrayAsNullTest.Model model = JSON.parseObject(text, EmptyArrayAsNullTest.Model.class);
        TestCase.assertNull(model.value);
    }

    public static class Model {
        public EmptyArrayAsNullTest.Value value;
    }

    public static class Value {}
}

