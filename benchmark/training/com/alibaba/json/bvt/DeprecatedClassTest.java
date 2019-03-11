package com.alibaba.json.bvt;


import com.alibaba.fastjson.serializer.JSONSerializerMap;
import junit.framework.TestCase;


@SuppressWarnings("deprecation")
public class DeprecatedClassTest extends TestCase {
    public void test_1() throws Exception {
        new JSONSerializerMap().put(Object.class, null);
    }
}

