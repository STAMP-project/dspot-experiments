package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.DisableCircularReferenceDetect;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1583 extends TestCase {
    public void test_issue() throws Exception {
        Map<String, List<String>> totalMap = new HashMap<String, List<String>>();
        for (int i = 0; i < 10; i++) {
            List<String> list = new ArrayList<String>();
            for (int j = 0; j < 2; j++) {
                list.add(("list" + j));
            }
            totalMap.put(("map" + i), list);
        }
        List<Map.Entry<String, List<String>>> mapList = new ArrayList<Map.Entry<String, List<String>>>(totalMap.entrySet());
        String jsonString = JSON.toJSONString(mapList, DisableCircularReferenceDetect);
        System.out.println(jsonString);
        List<Map.Entry<String, List<String>>> parse = JSON.parseObject(jsonString, new com.alibaba.fastjson.TypeReference<List<Map.Entry<String, List<String>>>>() {});
        System.out.println(parse);
    }
}

