package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;


/**
 * Created by kimmking on 15/06/2017.
 */
public class Issue1271 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"a\":1,\"b\":2}";
        final AtomicInteger count = new AtomicInteger(0);
        ExtraProcessor extraProcessor = new ExtraProcessor() {
            public void processExtra(Object object, String key, Object value) {
                System.out.println(((("setter not found, class " + (object.getClass().getName())) + ", property ") + key));
                count.incrementAndGet();
            }
        };
        Issue1271.A a = JSON.parseObject(json, Issue1271.A.class, extraProcessor);
        TestCase.assertEquals(1, a.a);
        TestCase.assertEquals(1, count.intValue());
        Issue1271.B b = JSON.parseObject(json, Issue1271.B.class, extraProcessor);
        TestCase.assertEquals(1, b.a);
        TestCase.assertEquals(2, count.intValue());
    }

    public static class A {
        public int a;
    }

    public static class B {
        private int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }
}

