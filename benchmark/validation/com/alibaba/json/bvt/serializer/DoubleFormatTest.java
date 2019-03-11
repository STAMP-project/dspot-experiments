package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 09/01/2017.
 */
public class DoubleFormatTest extends TestCase {
    public void test_format() throws Exception {
        DoubleFormatTest.Model model = new DoubleFormatTest.Model();
        model.value = 123.45678;
        String str = JSON.toJSONString(model);
        TestCase.assertEquals("{\"value\":123.46}", str);
    }

    public static class Model {
        @JSONField(format = "0.00")
        public double value;
    }
}

