package com.alibaba.json.bvt.basicType;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.math.BigDecimal;
import junit.framework.TestCase;


public class BigDecimal_field extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertEquals("{\"value\":\"9007199254741992\"}", JSON.toJSONString(new BigDecimal_field.Model(9007199254741992L)));
        TestCase.assertEquals("{\"value\":\"-9007199254741992\"}", JSON.toJSONString(new BigDecimal_field.Model((-9007199254741992L))));
        TestCase.assertEquals("{\"value\":9007199254740990}", JSON.toJSONString(new BigDecimal_field.Model(9007199254740990L)));
        TestCase.assertEquals("{\"value\":-9007199254740990}", JSON.toJSONString(new BigDecimal_field.Model((-9007199254740990L))));
        TestCase.assertEquals("{\"value\":100}", JSON.toJSONString(new BigDecimal_field.Model(100)));
        TestCase.assertEquals("{\"value\":-100}", JSON.toJSONString(new BigDecimal_field.Model((-100))));
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.BrowserCompatible)
        public BigDecimal value;

        public Model() {
        }

        public Model(long value) {
            this.value = BigDecimal.valueOf(value);
        }
    }
}

