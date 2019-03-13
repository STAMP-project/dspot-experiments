package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 09/01/2017.
 */
public class FloatFormatTest2 extends TestCase {
    public void test_format() throws Exception {
        FloatFormatTest2.Model model = new FloatFormatTest2.Model();
        model.value = 123.45678F;
        String str = JSON.toJSONString(model);
        TestCase.assertEquals("{\"value\":123.46}", str);
    }

    public static class Model {
        @JSONField(format = "0.00")
        public Float value;
    }
}

