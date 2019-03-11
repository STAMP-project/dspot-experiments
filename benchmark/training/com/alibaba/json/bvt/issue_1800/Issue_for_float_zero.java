package com.alibaba.json.bvt.issue_1800;


import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue_for_float_zero extends TestCase {
    public void test_0() throws Exception {
        Issue_for_float_zero.M1 m = new Issue_for_float_zero.M1(1.0F);
        TestCase.assertEquals("{\"val\":1.0}", JSON.toJSONString(m, WriteNullNumberAsZero));
    }

    public void test_1() throws Exception {
        Issue_for_float_zero.M2 m = new Issue_for_float_zero.M2(1.0);
        TestCase.assertEquals("{\"val\":1.0}", JSON.toJSONString(m, WriteNullNumberAsZero));
    }

    public static class M1 {
        public float val;

        public M1(float val) {
            this.val = val;
        }
    }

    public static class M2 {
        public double val;

        public M2(double val) {
            this.val = val;
        }
    }
}

