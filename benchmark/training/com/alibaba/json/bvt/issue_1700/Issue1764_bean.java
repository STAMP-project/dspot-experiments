package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


public class Issue1764_bean extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertEquals("{\"value\":\"9007199254741992\"}", JSON.toJSONString(new Issue1764_bean.Model(9007199254741992L)));
        TestCase.assertEquals("{\"value\":\"9007199254741990\"}", JSON.toJSONString(new Issue1764_bean.Model(9007199254741990L)));
        TestCase.assertEquals("{\"value\":100}", JSON.toJSONString(new Issue1764_bean.Model(100L)));
        TestCase.assertEquals("{\"value\":\"-9007199254741990\"}", JSON.toJSONString(new Issue1764_bean.Model((-9007199254741990L))));
        TestCase.assertEquals("{\"value\":-9007199254740990}", JSON.toJSONString(new Issue1764_bean.Model((-9007199254740990L))));
    }

    @JSONType(serialzeFeatures = SerializerFeature.BrowserCompatible)
    public static class Model {
        public long value;

        public Model() {
        }

        public Model(long value) {
            this.value = value;
        }
    }
}

