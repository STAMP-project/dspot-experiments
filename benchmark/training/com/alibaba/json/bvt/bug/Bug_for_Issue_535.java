package com.alibaba.json.bvt.bug;


import Feature.UseBigDecimal;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_Issue_535 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_Issue_535.TestPOJO testPOJO = new Bug_for_Issue_535.TestPOJO();
        testPOJO.setA("a");
        testPOJO.setB(new BigDecimal("1234512312312312312312"));
        String s = JSON.toJSONString(testPOJO);
        System.out.println(s);
        Bug_for_Issue_535.TestPOJO vo2 = JSON.parseObject(s, Bug_for_Issue_535.TestPOJO.class, UseBigDecimal);
        Assert.assertEquals(testPOJO.getB(), vo2.getB());
    }

    public static class TestPOJO {
        private String a;

        private BigDecimal b;

        // getter and setter
        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public BigDecimal getB() {
            return b;
        }

        public void setB(BigDecimal b) {
            this.b = b;
        }
    }
}

