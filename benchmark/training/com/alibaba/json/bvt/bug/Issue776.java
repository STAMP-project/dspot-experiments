package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/30.
 */
public class Issue776 extends TestCase {
    public void test_for_issue() throws Exception {
        String str1 = "{\"v\":[\" \",\"abc\",\"x\",\"abc\"]}";
        Exception error = null;
        try {
            JSON.parseObject(str1, new com.alibaba.fastjson.TypeReference<Map<String, char[]>>() {});
        } catch (Exception ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }
}

