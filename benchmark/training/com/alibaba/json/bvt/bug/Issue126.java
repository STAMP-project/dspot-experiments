package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Issue126 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONObject j = new JSONObject();
        j.put("content", "\u7238\u7238\u53bb\u54ea\u513f-\u7b2c\u5341\u671f-\u840c\u5a03\u6bd4\u8d5b\u5c0f\u732a\u5feb\u8dd1 \u7238\u7238\u4e0a\u6f14\"\u767e\u53d8\u5927\u5496\u79c0\"-\u3010\u6e56\u5357\u536b\u89c6\u5b98\u65b9\u72481080P\u301120131213: http://youtu.be/ajvaXKAduJ4  via @youtube");
        System.out.println(j.toJSONString());
    }
}

