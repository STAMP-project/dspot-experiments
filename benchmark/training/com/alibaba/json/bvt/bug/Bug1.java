package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Bug1 extends TestCase {
    public void testToEntry2() {
        JSONTest.InnerEntry inner1 = null;// ??

        String source1 = JSONObject.toJSONString(inner1);
        System.out.println(source1);
        JSONTest.OuterEntry inner2 = JSONObject.parseObject(source1, JSONTest.OuterEntry.class);// ??

    }
}

