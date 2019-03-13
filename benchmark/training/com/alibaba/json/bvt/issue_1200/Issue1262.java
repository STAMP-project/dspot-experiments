package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/06/2017.
 */
public class Issue1262 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1262.Model model = JSON.parseObject("{\"chatterMap\":{}}", Issue1262.Model.class);
    }

    public static class Model {
        public Map<String, Issue1262.Chatter> chatterMap = new ConcurrentHashMap<String, Issue1262.Chatter>();
    }

    public static class Chatter {}
}

