package com.alibaba.json.bvt.serializer;


import SerializerFeature.NotWriteRootClassName;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import java.util.Enumeration;
import java.util.Vector;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumerationSeriliazerTest extends TestCase {
    public void test_nullAsEmtpyList() throws Exception {
        EnumerationSeriliazerTest.VO e = new EnumerationSeriliazerTest.VO();
        Assert.assertEquals("{\"elements\":[]}", JSON.toJSONString(e, WriteMapNullValue, WriteNullListAsEmpty));
    }

    public void test_null() throws Exception {
        EnumerationSeriliazerTest.VO e = new EnumerationSeriliazerTest.VO();
        Assert.assertEquals("{\"elements\":null}", JSON.toJSONString(e, WriteMapNullValue));
    }

    public void test_1() throws Exception {
        EnumerationSeriliazerTest.VO e = new EnumerationSeriliazerTest.VO(new EnumerationSeriliazerTest.Entity(), new EnumerationSeriliazerTest.Entity());
        Assert.assertEquals("{\"elements\":[{},{}]}", JSON.toJSONString(e, WriteMapNullValue));
    }

    public void test_2() throws Exception {
        EnumerationSeriliazerTest.VO e = new EnumerationSeriliazerTest.VO(new EnumerationSeriliazerTest.Entity(), new EnumerationSeriliazerTest.Entity2());
        Assert.assertEquals("{\"elements\":[{},{\"@type\":\"com.alibaba.json.bvt.serializer.EnumerationSeriliazerTest$Entity2\"}]}", JSON.toJSONString(e, WriteClassName, NotWriteRootClassName));
    }

    public void test_3() throws Exception {
        EnumerationSeriliazerTest.VO2 e = new EnumerationSeriliazerTest.VO2(new EnumerationSeriliazerTest.Entity(), new EnumerationSeriliazerTest.Entity());
        Assert.assertEquals("{\"elements\":[{},{}]}", JSON.toJSONString(e, WriteClassName, NotWriteRootClassName));
    }

    public void test_4() throws Exception {
        EnumerationSeriliazerTest.VO3 e = new EnumerationSeriliazerTest.VO3(new EnumerationSeriliazerTest.Entity(), new EnumerationSeriliazerTest.Entity2());
        Assert.assertEquals("{\"elements\":[{\"@type\":\"com.alibaba.json.bvt.serializer.EnumerationSeriliazerTest$Entity\"},{\"@type\":\"com.alibaba.json.bvt.serializer.EnumerationSeriliazerTest$Entity2\"}]}", JSON.toJSONString(e, WriteClassName, NotWriteRootClassName));
    }

    private static class VO {
        private Enumeration<EnumerationSeriliazerTest.Entity> elements;

        public VO(EnumerationSeriliazerTest.Entity... array) {
            if ((array.length) > 0) {
                Vector<EnumerationSeriliazerTest.Entity> vector = new Vector<EnumerationSeriliazerTest.Entity>();
                for (EnumerationSeriliazerTest.Entity item : array) {
                    vector.add(item);
                }
                this.elements = vector.elements();
            }
        }

        public Enumeration<EnumerationSeriliazerTest.Entity> getElements() {
            return elements;
        }

        public void setElements(Enumeration<EnumerationSeriliazerTest.Entity> elements) {
            this.elements = elements;
        }
    }

    private static class VO2 extends EnumerationSeriliazerTest.IVO2<EnumerationSeriliazerTest.Entity> {
        public VO2(EnumerationSeriliazerTest.Entity... array) {
            if ((array.length) > 0) {
                Vector<EnumerationSeriliazerTest.Entity> vector = new Vector<EnumerationSeriliazerTest.Entity>();
                for (EnumerationSeriliazerTest.Entity item : array) {
                    vector.add(item);
                }
                this.elements = vector.elements();
            }
        }
    }

    private static class VO3 {
        private Enumeration elements;

        public VO3(EnumerationSeriliazerTest.Entity... array) {
            if ((array.length) > 0) {
                Vector<EnumerationSeriliazerTest.Entity> vector = new Vector<EnumerationSeriliazerTest.Entity>();
                for (EnumerationSeriliazerTest.Entity item : array) {
                    vector.add(item);
                }
                this.elements = vector.elements();
            }
        }

        public Enumeration getElements() {
            return elements;
        }

        public void setElements(Enumeration elements) {
            this.elements = elements;
        }
    }

    private abstract static class IVO2<T> {
        protected Enumeration<EnumerationSeriliazerTest.Entity> elements;

        public Enumeration<EnumerationSeriliazerTest.Entity> getElements() {
            return elements;
        }

        public void setElements(Enumeration<EnumerationSeriliazerTest.Entity> elements) {
            this.elements = elements;
        }
    }

    public static class Entity {}

    public static class Entity2 extends EnumerationSeriliazerTest.Entity {}
}

