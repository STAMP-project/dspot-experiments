package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 08/05/2017.
 */
public class Issue969 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(new Issue969.Model());
        jsonObject.put("models", jsonArray);
        List list = jsonObject.getObject("models", new com.alibaba.fastjson.TypeReference<List<Issue969.Model>>() {});
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(Issue969.Model.class, list.get(0).getClass());
    }

    public void test_for_issue_1() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(new Issue969.Model());
        jsonObject.put("models", jsonArray);
        List list = jsonObject.getObject("models", getType());
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(Issue969.Model.class, list.get(0).getClass());
    }

    public static class Model {}
}

