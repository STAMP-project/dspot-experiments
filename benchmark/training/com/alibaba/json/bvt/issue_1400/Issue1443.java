package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1443 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"date\":\"3017-08-28T00:00:00+08:00\"}";
        Issue1443.Model model = JSON.parseObject(json, Issue1443.Model.class);
    }

    public static class Model {
        public Date date;
    }
}

