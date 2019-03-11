package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;


public class Issue2240 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue2240.ResultMap resultMap = new Issue2240.ResultMap();
        resultMap.setA(Collections.<Long, Integer>emptyMap());
        resultMap.setB(Collections.<Long, Integer>emptyMap());
        TestCase.assertEquals("{\"a\":{},\"b\":{}}", JSON.toJSONString(resultMap));
    }

    public static class ResultMap {
        private Map<Long, Integer> a;

        private Map<Long, Integer> b;

        public Map<Long, Integer> getA() {
            return a;
        }

        public void setA(Map<Long, Integer> a) {
            this.a = a;
        }

        public Map<Long, Integer> getB() {
            return b;
        }

        public void setB(Map<Long, Integer> b) {
            this.b = b;
        }
    }
}

