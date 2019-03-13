package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/06/2017.
 */
public class Issue1205 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONArray array = new JSONArray();
        array.add(new JSONObject());
        List<Issue1205.Model> list = array.toJavaObject(new com.alibaba.fastjson.TypeReference<List<Issue1205.Model>>() {});
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(Issue1205.Model.class, list.get(0).getClass());
    }

    public static class Model {}
}

