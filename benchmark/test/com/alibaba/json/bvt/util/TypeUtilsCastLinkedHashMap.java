package com.alibaba.json.bvt.util;


import Feature.OrderedField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.LinkedHashMap;
import junit.framework.TestCase;


public class TypeUtilsCastLinkedHashMap extends TestCase {
    public void test_for_cast() throws Exception {
        JSONObject obj = JSON.parseObject("{\"id\":1001,\"name\":\"xxx\"}", OrderedField);
        obj.toJavaObject(LinkedHashMap.class);
    }
}

