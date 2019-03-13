package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by kimmking on 06/06/2017.
 */
public class Issue1246 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1246.B b = new Issue1246.B();
        b.setX("xx");
        String test = JSON.toJSONString(b);
        System.out.println(test);
        TestCase.assertEquals("{}", test);
        Issue1246.C c = new Issue1246.C();
        c.ab = b;
        String testC = JSON.toJSONString(c);
        System.out.println(testC);
        TestCase.assertEquals("{\"ab\":{}}", testC);
        Issue1246.D d = new Issue1246.D();
        d.setAb(b);
        String testD = JSON.toJSONString(d);
        System.out.println(testD);
        TestCase.assertEquals("{\"ab\":{}}", testD);
    }

    public static class C {
        public Issue1246.A ab;
    }

    public static class D {
        private Issue1246.A ab;

        public Issue1246.A getAb() {
            return ab;
        }

        public void setAb(Issue1246.A ab) {
            this.ab = ab;
        }
    }

    public static class A {
        private String x;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }
    }

    public static class B extends Issue1246.A {
        private String x;

        @Override
        @JSONField(serialize = false)
        public String getX() {
            return x;
        }

        @Override
        public void setX(String x) {
            this.x = x;
        }
    }
}

