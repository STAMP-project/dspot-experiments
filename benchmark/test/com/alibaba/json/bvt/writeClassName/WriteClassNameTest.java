package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest.A a = new WriteClassNameTest.A();
        a.setB(new WriteClassNameTest.B());
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest$A\",\"b\":{}}", text);
        WriteClassNameTest.A a1 = ((WriteClassNameTest.A) (JSON.parse(text)));
        Assert.assertNotNull(a1.getB());
    }

    private static class A {
        private WriteClassNameTest.B b;

        public WriteClassNameTest.B getB() {
            return b;
        }

        public void setB(WriteClassNameTest.B b) {
            this.b = b;
        }
    }

    private static final class B {}
}

