package com.alibaba.json.bvt.jsonp;


import com.alibaba.fastjson.JSONPObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 21/02/2017.
 */
public class JSONPParseTest4 extends TestCase {
    public void test_f() throws Exception {
        JSONPObject p = new JSONPObject();
        p.setFunction("f");
        TestCase.assertEquals("f()", p.toJSONString());
    }
}

