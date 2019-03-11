package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ProxyTest extends TestCase {
    public void test_0() throws Exception {
        ProxyTest.A a = ProxyTest.create(ProxyTest.A.class);
        a.setId(123);
        Assert.assertEquals("{\"id\":123}", JSON.toJSONString(a));
    }

    public static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

