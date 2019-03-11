package com.alibaba.json.bvt.bug;


import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_491 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{id:1,keyword:[{uuid:\"ddd\"},{uuid:\"zzz\"}]}";
        Map<String, String> map = Bug_for_issue_491.getJsonToMap1(json, String.class);
        Assert.assertEquals("1", map.get("id"));
    }

    public void test_for_issue_2() throws Exception {
        String json = "{1:{name:\"ddd\"},2:{name:\"zzz\"}}";
        Map<Integer, Bug_for_issue_491.Model> map = Bug_for_issue_491.getJsonToMap(json, Integer.class, Bug_for_issue_491.Model.class);
        Assert.assertEquals("ddd", map.get(1).name);
        Assert.assertEquals("zzz", map.get(2).name);
    }

    public static class Model {
        public String name;
    }
}

