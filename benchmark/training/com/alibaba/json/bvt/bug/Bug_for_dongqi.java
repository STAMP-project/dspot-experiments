package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_dongqi extends TestCase {
    public void test_bug() throws Exception {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("value", "\uff1b\r\n3\u3001\u009e \u516c");
        System.out.print(JSON.toJSONString(obj));
        Assert.assertEquals("{\"value\":\"\uff1b\\r\\n3\u3001\\u009E \u516c\"}", JSON.toJSONString(obj));
    }
}

