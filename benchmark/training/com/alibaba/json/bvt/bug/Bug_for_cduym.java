package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_cduym extends TestCase {
    public void test1() {
        List<Bug_for_cduym.A> as = new ArrayList<Bug_for_cduym.A>();
        Bug_for_cduym.A a1 = new Bug_for_cduym.A();
        a1.setA(1000);
        a1.setB(2000L);
        a1.setC("xxx");
        as.add(a1);
        as.add(a1);
        Bug_for_cduym.Demo o = new Bug_for_cduym.Demo();
        o.setAs(as);
        String text = JSON.toJSONString(o, WriteClassName);
        System.out.println(text);
        Bug_for_cduym.Demo target = ((Bug_for_cduym.Demo) (JSON.parseObject(text, Object.class)));
        Assert.assertSame(((List) (target.getAs())).get(0), ((List) (target.getAs())).get(1));
    }

    public static class Demo {
        private Object as;

        public Object getAs() {
            return as;
        }

        public void setAs(Object as) {
            this.as = as;
        }
    }

    private static class A {
        private Integer a;

        private Long b;

        private String c;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public Long getB() {
            return b;
        }

        public void setB(Long b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }
}

