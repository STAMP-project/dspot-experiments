package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class DoubleFieldDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        DoubleFieldDeserializerTest.Entity a = JSON.parseObject("{\"value\":123.45}", DoubleFieldDeserializerTest.Entity.class);
        Assert.assertTrue((123.45 == (a.getValue())));
    }

    public static class Entity {
        public Double value;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }
}

