package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_13 extends TestCase {
    public void test_e() throws Exception {
        Assert.assertTrue((123000.0 == (JSON.parseObject("{\"vo\":{\"type\":123e3}}", JSONLexerTest_13.A.class).getVo().getType())));
    }

    public void test_E() throws Exception {
        Assert.assertTrue((123000.0 == (JSON.parseObject("{\"vo\":{\"type\":123E3}}", JSONLexerTest_13.A.class).getVo().getType())));
    }

    public void test_e_plus() throws Exception {
        Assert.assertTrue((123000.0 == (JSON.parseObject("{\"vo\":{\"type\":123e+3}}", JSONLexerTest_13.A.class).getVo().getType())));
    }

    public void test_E_plus() throws Exception {
        Assert.assertTrue((123000.0 == (JSON.parseObject("{\"vo\":{\"type\":123E+3}}", JSONLexerTest_13.A.class).getVo().getType())));
    }

    public void test_e_minus() throws Exception {
        Assert.assertTrue((0.123 == (JSON.parseObject("{\"vo\":{\"type\":123e-3}}", JSONLexerTest_13.A.class).getVo().getType())));
    }

    public void test_E_minus() throws Exception {
        Assert.assertTrue((0.123 == (JSON.parseObject("{\"vo\":{\"type\":123E-3}}", JSONLexerTest_13.A.class).getVo().getType())));
    }

    public void test_error_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"vo\":{\"type\":123]", JSONLexerTest_13.A.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class A {
        private JSONLexerTest_13.VO vo;

        public JSONLexerTest_13.VO getVo() {
            return vo;
        }

        public void setVo(JSONLexerTest_13.VO vo) {
            this.vo = vo;
        }
    }

    public static class VO {
        public VO() {
        }

        private double type;

        public double getType() {
            return type;
        }

        public void setType(double type) {
            this.type = type;
        }
    }
}

