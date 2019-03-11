package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1424 extends TestCase {
    public static class IntegerVal {
        private int v;

        public void setV(int v) {
            this.v = v;
        }

        @Override
        public String toString() {
            return String.valueOf(v);
        }
    }

    public static class FloatVal {
        private float v;

        public void setV(float v) {
            this.v = v;
        }

        @Override
        public String toString() {
            return String.valueOf(v);
        }
    }

    public void test_for_issue_int() {
        Map<String, Long> intOverflowMap = new HashMap<String, Long>();
        long intOverflow = Integer.MAX_VALUE;
        intOverflowMap.put("v", (intOverflow + 1));
        String sIntOverflow = JSON.toJSONString(intOverflowMap);
        Exception error = null;
        try {
            JSON.parseObject(sIntOverflow, Issue1424.IntegerVal.class);
        } catch (Exception e) {
            error = e;
        }
        TestCase.assertNotNull(error);
    }

    public void test_for_issue_float() {
        Map<String, Double> floatOverflowMap = new HashMap<String, Double>();
        double floatOverflow = Float.MAX_VALUE;
        floatOverflowMap.put("v", (floatOverflow + 1));
        String sFloatOverflow = JSON.toJSONString(floatOverflowMap);
        TestCase.assertEquals("{\"v\":3.4028234663852886E38}", sFloatOverflow);
        Issue1424.FloatVal floatVal = JSON.parseObject(sFloatOverflow, Issue1424.FloatVal.class);
        TestCase.assertEquals(3.4028235E38F, floatVal.v);
        TestCase.assertEquals(floatVal.v, Float.parseFloat("3.4028234663852886E38"));
    }

    public void test_for_issue_float_infinity() {
        Map<String, Double> floatOverflowMap = new HashMap<String, Double>();
        double floatOverflow = Float.MAX_VALUE;
        floatOverflowMap.put("v", (floatOverflow + floatOverflow));
        String sFloatOverflow = JSON.toJSONString(floatOverflowMap);
        System.out.println(sFloatOverflow);
        TestCase.assertEquals("{\"v\":6.805646932770577E38}", sFloatOverflow);
        Issue1424.FloatVal floatVal = JSON.parseObject(sFloatOverflow, Issue1424.FloatVal.class);
        TestCase.assertEquals(Float.parseFloat("6.805646932770577E38"), floatVal.v);
    }
}

