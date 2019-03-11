package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_389 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_389.Def def = new Bug_for_issue_389.Def();
        def.add(new Bug_for_issue_389.User());
        String defStr = JSON.toJSONString(def);
        Assert.assertEquals("[{}]", defStr);
        Bug_for_issue_389.Def _def = JSON.parseObject(defStr, Bug_for_issue_389.Def.class);
        Assert.assertEquals(Bug_for_issue_389.User.class, _def.get(0).getClass());
    }

    public void test_for_issue_1() throws Exception {
        Bug_for_issue_389.Def def = new Bug_for_issue_389.Def();
        def.add(new Bug_for_issue_389.User());
        String defStr = JSON.toJSONString(def);
        Assert.assertEquals("[{}]", defStr);
        Bug_for_issue_389.Def _def = JSON.parseObject(defStr, new com.alibaba.fastjson.TypeReference<Bug_for_issue_389.Def>() {});
        Assert.assertEquals(Bug_for_issue_389.User.class, _def.get(0).getClass());
    }

    public static class User {
        String name;

        String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Def extends ArrayList<Bug_for_issue_389.User> {}
}

