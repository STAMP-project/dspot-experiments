package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteBigDecimalAsPlain;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wenshao on 16/8/9.
 */
public class WriteBigDecimalAsPlainTest extends TestCase {
    public void test_for_feature() throws Exception {
        BigDecimal value = new BigDecimal("0.00000001");
        Assert.assertEquals("1E-8", JSON.toJSONString(value));
        Assert.assertEquals("0.00000001", JSON.toJSONString(value, WriteBigDecimalAsPlain));
    }

    public void test_1() throws Exception {
        WriteBigDecimalAsPlainTest.Model m = new WriteBigDecimalAsPlainTest.Model();
        m.value = new BigDecimal("0.00000001");
        Assert.assertEquals("{\"value\":1E-8}", JSON.toJSONString(m));
        Assert.assertEquals("{\"value\":0.00000001}", JSON.toJSONString(m, WriteBigDecimalAsPlain));
    }

    public static class Model {
        private BigDecimal value;

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}

