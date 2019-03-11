package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class TestSpecial_2 extends TestCase {
    public void test_special() throws Exception {
        TestSpecial_2.Model model = new TestSpecial_2.Model();
        TestSpecial_2.Value value = new TestSpecial_2.Value();
        model.values.put("com.ibatis.sqlmap.client.SqlMapExecutor@queryForObject(String,Object)", value);
        model.subInvokes.put("com.ibatis.sqlmap.client.SqlMapExecutor@queryForObject(String,Object)", value);
        String json = JSON.toJSONString(model);
        System.out.println(json);
        TestSpecial_2.Model m2 = JSON.parseObject(json, TestSpecial_2.Model.class);
        TestCase.assertEquals(1, m2.values.size());
        TestCase.assertEquals(1, m2.subInvokes.size());
        TestCase.assertSame(m2.values.values().iterator().next(), m2.subInvokes.values().iterator().next());
    }

    public static class Model {
        public Map<String, TestSpecial_2.Value> values = new HashMap<String, TestSpecial_2.Value>();

        public Map<String, TestSpecial_2.Value> subInvokes = new HashMap<String, TestSpecial_2.Value>();
    }

    public static class Value {}
}

