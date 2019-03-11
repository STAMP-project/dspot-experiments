package com.alibaba.json.bvt.issue_1200;


import Feature.DisableCircularReferenceDetect;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/05/2017.
 */
public class Issue1231 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1231.Model model = new Issue1231.Model();
        model.self = model;
        model.id = 123;
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"id\":123,\"self\":{\"$ref\":\"@\"}}", text);
        {
            Issue1231.Model model2 = JSON.parseObject(text, Issue1231.Model.class, DisableCircularReferenceDetect);
            TestCase.assertNotNull(model2);
            TestCase.assertNotSame(model2, model2.self);
        }
        {
            JSONObject jsonObject = JSON.parseObject(text, DisableCircularReferenceDetect);
            TestCase.assertNotNull(jsonObject);
            JSONObject self = jsonObject.getJSONObject("self");
            TestCase.assertNotNull(self);
            TestCase.assertNotNull(self.get("$ref"));
            TestCase.assertEquals("@", self.get("$ref"));
        }
    }

    public static class Model {
        public int id;

        public Issue1231.Model self;
    }
}

