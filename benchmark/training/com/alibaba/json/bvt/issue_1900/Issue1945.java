package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1945 extends TestCase {
    public void test_0() throws Exception {
        Issue1945.B b = new Issue1945.B();
        b.clazz = new Class[]{ String.class };
        b.aInstance = new HashMap();
        b.aInstance.put("test", "test");
        String s = JSON.toJSONString(b, SerializerFeature.WriteClassName);
        System.out.println(s);
        Issue1945.B a1 = JSON.parseObject(s, Issue1945.B.class);
    }

    static class B implements Serializable {
        public Class[] clazz;

        public Map aInstance;
    }
}

