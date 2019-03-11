package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/01/2017.
 */
public class Issue983 extends TestCase {
    public void test_for_issue() throws Exception {
        Map.Entry entry = JSON.parseObject("{\"name\":\"foo\"}", Map.Entry.class);
        TestCase.assertEquals("name", entry.getKey());
        TestCase.assertEquals("foo", entry.getValue());
    }
}

