package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import junit.framework.TestCase;


public class Bug_for_JSONObject extends TestCase {
    public void test_0() throws Exception {
        JSONSerializer ser = new JSONSerializer();
        ser.config(WriteClassName, true);
        ser.write(new JSONObject());
    }
}

