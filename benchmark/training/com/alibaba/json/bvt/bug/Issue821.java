package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/03/2017.
 */
public class Issue821 extends TestCase {
    public void test_for_issue() throws Exception {
        String str1 = "{\"v\":[\" \",\"a\",\"x\",\"a\"]}";
        System.out.println(str1);
        char[] c = JSON.parseObject(str1, new com.alibaba.fastjson.TypeReference<Map<String, char[]>>() {}).get("v");
        TestCase.assertEquals(4, c.length);
        TestCase.assertEquals(c[0], ' ');
        TestCase.assertEquals(c[1], 'a');
        TestCase.assertEquals(c[2], 'x');
        TestCase.assertEquals(c[3], 'a');
    }
}

