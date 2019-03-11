package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/12/2016.
 */
public class Issue922 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "[1,2,3]";
        JSONArray array = JSON.parseArray(text);
        List<Long> list = array.toJavaList(Long.class);
        TestCase.assertEquals(1L, list.get(0).longValue());
        TestCase.assertEquals(2L, list.get(1).longValue());
        TestCase.assertEquals(3L, list.get(2).longValue());
    }
}

