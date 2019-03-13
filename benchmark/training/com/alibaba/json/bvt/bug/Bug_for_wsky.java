package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_wsky extends TestCase {
    public void test_writeMapNull() throws Exception {
        JSON.parseObject(JSON.toJSONString(new Bug_for_wsky.MethodReturn(), WriteMapNullValue), Bug_for_wsky.MethodReturn.class);
    }

    public static class MethodReturn {
        public Object ReturnValue;

        public Throwable Exception;
    }
}

