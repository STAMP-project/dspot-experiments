package com.alibaba.json.bvt.jsonp;


import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 21/02/2017.
 */
public class JSONPParseTest3 extends TestCase {
    public void test_f() throws Exception {
        String text = "parent.callback ({'id':1, 'name':'ido)nans'},1,2 );   /**/ ";
        JSONPObject jsonpObject = ((JSONPObject) (JSON.parseObject(text, JSONPObject.class)));
        TestCase.assertEquals("parent.callback", jsonpObject.getFunction());
        TestCase.assertEquals(3, jsonpObject.getParameters().size());
        JSONObject param = ((JSONObject) (jsonpObject.getParameters().get(0)));
        TestCase.assertEquals(1, param.get("id"));
        TestCase.assertEquals("ido)nans", param.get("name"));
        String json = JSON.toJSONString(jsonpObject, BrowserSecure);
        TestCase.assertEquals("/**/parent.callback({\"name\":\"ido\\u0029nans\",\"id\":1},1,2)", json);
    }
}

