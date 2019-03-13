package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


public class Issue1764 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1764.Model model = new Issue1764.Model();
        model.value = 9007199254741992L;
        String str = JSON.toJSONString(model);
        TestCase.assertEquals("{\"value\":\"9007199254741992\"}", str);
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.BrowserCompatible)
        public long value;
    }
}

