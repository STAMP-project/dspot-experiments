package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue1500 extends TestCase {
    public void test_for_issue() throws Exception {
        // test aa
        Issue1500.Aa aa = new Issue1500.Aa();
        aa.setName("aa");
        String jsonAa = JSON.toJSONString(aa);
        Issue1500.Aa aa1 = JSON.parseObject(jsonAa, Issue1500.Aa.class);
        Assert.assertEquals("aa", aa1.getName());
        // test C
        Issue1500.C c = new Issue1500.C();
        c.setE(aa);
        String jsonC = JSON.toJSONString(c, WriteClassName);
        Issue1500.C c2 = JSON.parseObject(jsonC, Issue1500.C.class);
        Assert.assertEquals("Aa", c2.getE().getClass().getSimpleName());
        Assert.assertEquals("aa", ((Issue1500.Aa) (c2.getE())).getName());
    }

    public static class Aa extends Exception {
        public Aa() {
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class C {
        private Exception e;

        public Exception getE() {
            return e;
        }

        public void setE(Exception e) {
            this.e = e;
        }
    }
}

