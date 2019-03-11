package com.alibaba.json.bvt.writeClassName;


import Feature.SupportAutoType;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest2 extends TestCase {
    public void test_writeClassName() throws Exception {
        WriteClassNameTest2.A a = new WriteClassNameTest2.A();
        a.setB(new WriteClassNameTest2.B());
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest2$A\",\"b\":{\"id\":0}}", text);
        WriteClassNameTest2.A a1 = ((WriteClassNameTest2.A) (JSON.parse(text, SupportAutoType)));
        Assert.assertNotNull(a1.getB());
    }

    public static class A {
        private WriteClassNameTest2.B b;

        public WriteClassNameTest2.B getB() {
            return b;
        }

        public void setB(WriteClassNameTest2.B b) {
            this.b = b;
        }
    }

    public static final class B {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

