package com.alibaba.json.bvt.issue_1000;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/06/2017.
 */
public class Issue1083 extends TestCase {
    public void test_for_issue() throws Exception {
        Map map = new HashMap();
        map.put("userId", 456);
        String json = JSON.toJSONString(map, WriteNonStringValueAsString);
        TestCase.assertEquals("{\"userId\":\"456\"}", json);
    }
}

