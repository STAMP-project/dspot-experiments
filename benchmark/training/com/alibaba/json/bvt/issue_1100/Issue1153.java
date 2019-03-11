package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 08/05/2017.
 */
public class Issue1153 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\n" + (("name: \'zhangshan\', //\u8fd9\u662f\u4e00\u4e2a\u59d3\u540d\n" + "test : \'//helo\'\n") + "}");
        JSONObject jsonObject = JSON.parseObject(json);
        System.out.println(jsonObject);
    }
}

