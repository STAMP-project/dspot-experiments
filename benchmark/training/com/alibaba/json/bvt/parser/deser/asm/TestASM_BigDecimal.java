package com.alibaba.json.bvt.parser.deser.asm;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_BigDecimal extends TestCase {
    public void test_decimal() throws Exception {
        TestASM_BigDecimal.V0 v = new TestASM_BigDecimal.V0();
        String text = JSON.toJSONString(v, UseSingleQuotes);
        Assert.assertEquals("{}", text);
    }

    public void test_decimal_1() throws Exception {
        TestASM_BigDecimal.V0 v = new TestASM_BigDecimal.V0();
        v.setDecimal(new BigDecimal("123"));
        String text = JSON.toJSONString(v, UseSingleQuotes);
        Assert.assertEquals("{'decimal':123}", text);
    }

    public void test_decimal_2() throws Exception {
        TestASM_BigDecimal.V1 v = new TestASM_BigDecimal.V1();
        v.setId(123);
        String text = JSON.toJSONString(v, UseSingleQuotes);
        Assert.assertEquals("{'id':123}", text);
    }

    public void test_decimal_3() throws Exception {
        TestASM_BigDecimal.V1 v = new TestASM_BigDecimal.V1();
        v.setId(123);
        String text = JSON.toJSONString(v, UseSingleQuotes, WriteMapNullValue);
        System.out.println(text);
        Assert.assertEquals("{'decimal':null,'id':123}", text);
    }

    public static class V0 {
        private BigDecimal decimal;

        public BigDecimal getDecimal() {
            return decimal;
        }

        public void setDecimal(BigDecimal decimal) {
            this.decimal = decimal;
        }
    }

    public static class V1 {
        private int id;

        private BigDecimal decimal;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public BigDecimal getDecimal() {
            return decimal;
        }

        public void setDecimal(BigDecimal decimal) {
            this.decimal = decimal;
        }
    }
}

