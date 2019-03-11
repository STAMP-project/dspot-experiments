package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumTest extends TestCase {
    public void test_enum() throws Exception {
        Assert.assertNull(JSON.parseObject("''", TimeUnit.class));
    }

    public void test_enum_1() throws Exception {
        Assert.assertEquals(EnumTest.E.A, JSON.parseObject("0", EnumTest.E.class));
    }

    public void test_enum_3() throws Exception {
        Assert.assertEquals(EnumTest.E.A, JSON.parseObject("{value:0}", EnumTest.Entity.class).getValue());
    }

    public void test_enum_2() throws Exception {
        Assert.assertEquals(EnumTest.E.A, JSON.parseObject("'A'", EnumTest.E.class));
    }

    public void test_enum_error() throws Exception {
        TestCase.assertNull(JSON.parseObject("'123'", TimeUnit.class));
    }

    public void test_enum_error_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("12.3", TimeUnit.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static enum E {

        A,
        B,
        C;}

    public static class Entity {
        private EnumTest.E value;

        public Entity() {
        }

        public Entity(EnumTest.E value) {
            super();
            this.value = value;
        }

        public EnumTest.E getValue() {
            return value;
        }

        public void setValue(EnumTest.E value) {
            this.value = value;
        }
    }
}

