package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Bug_for_huangchun extends TestCase {
    public void test_serialize_url() throws Exception {
        JSONObject json = new JSONObject();
        json.put("info", "<a href=\"http://www.baidu.com\"> \u95ee\u9898\u94fe\u63a5 </a> ");
        String text = JSON.toJSONString(json);
        System.out.println(text);
    }
}

