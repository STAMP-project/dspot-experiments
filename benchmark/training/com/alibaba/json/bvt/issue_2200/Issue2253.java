package com.alibaba.json.bvt.issue_2200;


import Feature.OrderedField;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Issue2253 extends TestCase {
    public void test_for_issue() throws Exception {
        List<Map<String, Object>> result = new ArrayList();
        result.add(new LinkedHashMap());
        result.get(0).put("3", 3);
        result.get(0).put("2", 2);
        result.get(0).put("7", 7);
        TestCase.assertEquals("[{\"3\":3,\"2\":2,\"7\":7}]", JSON.toJSONString(result, WriteMapNullValue));
        result = JSON.parseObject(JSON.toJSONString(result, WriteMapNullValue), new com.alibaba.fastjson.TypeReference<List<Map<String, Object>>>() {}, OrderedField);
        TestCase.assertEquals("[{\"3\":3,\"2\":2,\"7\":7}]", JSON.toJSONString(result, WriteMapNullValue));
    }
}

