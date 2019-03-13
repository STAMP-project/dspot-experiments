package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;


public class Issue153 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "[{\"url_short\":\"http://t.cn/8soWK4z\",\"url_long\":\"http://wenshao.com\",\"type\":0}]";
        JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<HashMap<String, Object>[]>() {});
    }
}

