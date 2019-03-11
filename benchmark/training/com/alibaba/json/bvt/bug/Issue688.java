package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/11/13.
 */
public class Issue688 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue688.User monther = new Issue688.User("HanMeiMei", 29L);
        Issue688.User child = new Issue688.User("liLei", 2L);
        Issue688.User grandma = new Issue688.User("zhangHua", 60L);
        Map<Issue688.User, Issue688.User> userMap = new HashMap<Issue688.User, Issue688.User>();
        userMap.put(child, monther);
        userMap.put(monther, grandma);
        String json = JSON.toJSONString(userMap);
        System.out.println(json);
    }

    public static class User {
        public String name;

        public long id;

        public User() {
        }

        public User(String name, long id) {
            this.name = name;
            this.id = id;
        }
    }
}

