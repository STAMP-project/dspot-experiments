package com.alibaba.json.bvt.serializer.filters;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 *
 *
 * @author wenshao
 */
public class PropertyPathTest extends TestCase {
    public void test_path() throws Exception {
        PropertyPathTest.A a = new PropertyPathTest.A();
        a.setId(123);
        PropertyPathTest.B b = new PropertyPathTest.B();
        b.setId(234);
        PropertyPathTest.C c = new PropertyPathTest.C();
        c.setId(345);
        PropertyPathTest.D d = new PropertyPathTest.D();
        d.setId(456);
        a.setB(b);
        b.setC(c);
        b.setD(d);
        Assert.assertEquals("{\"b\":{\"c\":{\"id\":345},\"d\":{\"id\":456},\"id\":234},\"id\":123}", JSON.toJSONString(a));
        Assert.assertEquals("{\"b\":{\"c\":{\"id\":345},\"id\":234},\"id\":123}", JSON.toJSONString(a, new PropertyPathTest.MyPropertyPreFilter()));
        Assert.assertEquals("{'b':{'c':{'id':345},'id':234},'id':123}", JSON.toJSONString(a, new PropertyPathTest.MyPropertyPreFilter(), UseSingleQuotes));
    }

    public static class MyPropertyPreFilter implements PropertyPreFilter {
        public boolean apply(JSONSerializer serializer, Object source, String name) {
            String path = ((serializer.getContext().toString()) + ".") + name;
            if (path.startsWith("$.b.d")) {
                return false;
            }
            return true;
        }
    }

    public static class A {
        private int id;

        private PropertyPathTest.B b;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public PropertyPathTest.B getB() {
            return b;
        }

        public void setB(PropertyPathTest.B b) {
            this.b = b;
        }
    }

    public static class B {
        private int id;

        private PropertyPathTest.C c;

        private PropertyPathTest.D d;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public PropertyPathTest.C getC() {
            return c;
        }

        public void setC(PropertyPathTest.C c) {
            this.c = c;
        }

        public PropertyPathTest.D getD() {
            return d;
        }

        public void setD(PropertyPathTest.D d) {
            this.d = d;
        }
    }

    public static class C {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class D {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

