package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class MyMapFieldTest extends TestCase {
    public void test_null() throws Exception {
        MyMapFieldTest.Entity value = JSON.parseObject("{value:null}", MyMapFieldTest.Entity.class);
        Assert.assertNull(value.getValue());
    }

    public void test_empty() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{value:{}}", MyMapFieldTest.Entity.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Entity {
        private MyMapFieldTest.MyMap<Object, Object> value;

        public MyMapFieldTest.MyMap<Object, Object> getValue() {
            return value;
        }

        public void setValue(MyMapFieldTest.MyMap<Object, Object> value) {
            this.value = value;
        }
    }

    public class MyMap<K, V> extends HashMap<K, V> {}
}

