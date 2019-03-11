package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.Hashtable;
import junit.framework.TestCase;
import org.junit.Assert;


public class HashtableFieldTest extends TestCase {
    public void test_null() throws Exception {
        HashtableFieldTest.Entity value = JSON.parseObject("{value:null}", HashtableFieldTest.Entity.class);
        Assert.assertNull(value.getValue());
    }

    public void test_empty() throws Exception {
        HashtableFieldTest.Entity value = JSON.parseObject("{value:{}}", HashtableFieldTest.Entity.class);
        Assert.assertEquals(0, value.getValue().size());
    }

    public void test_null_2() throws Exception {
        HashtableFieldTest.Entity value = JSON.parseObject("{\"value\":null}", HashtableFieldTest.Entity.class);
        Assert.assertNull(value.getValue());
    }

    public void test_empty_a() throws Exception {
        HashtableFieldTest.A value = JSON.parseObject("{value:{\"@type\":\"java.util.Hashtable\"}}", HashtableFieldTest.A.class);
        Assert.assertEquals(0, ((Hashtable) (value.getValue())).size());
    }

    private static class Entity {
        private Hashtable value;

        public Hashtable getValue() {
            return value;
        }

        public void setValue(Hashtable value) {
            this.value = value;
        }
    }

    private static class A {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

