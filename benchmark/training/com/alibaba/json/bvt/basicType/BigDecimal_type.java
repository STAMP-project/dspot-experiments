package com.alibaba.json.bvt.basicType;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.math.BigDecimal;
import junit.framework.TestCase;


public class BigDecimal_type extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertEquals("{\"value\":\"9007199254741992\"}", JSON.toJSONString(new BigDecimal_type.Model(9007199254741992L)));
        TestCase.assertEquals("{\"value\":\"-9007199254741992\"}", JSON.toJSONString(new BigDecimal_type.Model((-9007199254741992L))));
        TestCase.assertEquals("{\"value\":9007199254740990}", JSON.toJSONString(new BigDecimal_type.Model(9007199254740990L)));
        TestCase.assertEquals("{\"value\":-9007199254740990}", JSON.toJSONString(new BigDecimal_type.Model((-9007199254740990L))));
        TestCase.assertEquals("{\"value\":100}", JSON.toJSONString(new BigDecimal_type.Model(100)));
        TestCase.assertEquals("{\"value\":-100}", JSON.toJSONString(new BigDecimal_type.Model((-100))));
    }

    @JSONType(serialzeFeatures = SerializerFeature.BrowserCompatible)
    public static class Model {
        public BigDecimal value;

        public Model() {
        }

        public Model(long value) {
            this.value = BigDecimal.valueOf(value);
        }
    }
}

