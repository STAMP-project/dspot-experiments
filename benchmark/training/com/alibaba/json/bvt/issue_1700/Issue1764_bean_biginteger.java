package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.math.BigInteger;
import junit.framework.TestCase;


public class Issue1764_bean_biginteger extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertEquals("{\"value\":\"9007199254741992\"}", JSON.toJSONString(new Issue1764_bean_biginteger.Model(9007199254741992L), SerializerFeature.BrowserCompatible));
        TestCase.assertEquals("{\"value\":\"-9007199254741992\"}", JSON.toJSONString(new Issue1764_bean_biginteger.Model((-9007199254741992L)), SerializerFeature.BrowserCompatible));
        TestCase.assertEquals("{\"value\":9007199254740990}", JSON.toJSONString(new Issue1764_bean_biginteger.Model(9007199254740990L), SerializerFeature.BrowserCompatible));
        TestCase.assertEquals("{\"value\":-9007199254740990}", JSON.toJSONString(new Issue1764_bean_biginteger.Model((-9007199254740990L)), SerializerFeature.BrowserCompatible));
        TestCase.assertEquals("{\"value\":100}", JSON.toJSONString(new Issue1764_bean_biginteger.Model(100), SerializerFeature.BrowserCompatible));
        TestCase.assertEquals("{\"value\":-100}", JSON.toJSONString(new Issue1764_bean_biginteger.Model((-100)), SerializerFeature.BrowserCompatible));
    }

    public static class Model {
        public BigInteger value;

        public Model() {
        }

        public Model(long value) {
            this.value = BigInteger.valueOf(value);
        }
    }
}

