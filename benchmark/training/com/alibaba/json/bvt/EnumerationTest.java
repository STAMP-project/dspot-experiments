package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumerationTest extends TestCase {
    public void test_enumeration() throws Exception {
        Assert.assertEquals("[]", JSON.toJSONString(new Vector().elements()));
        Assert.assertEquals("[null]", JSON.toJSONString(new Vector(Collections.singleton(null)).elements()));
        Assert.assertEquals("{\"value\":[]}", JSON.toJSONString(new EnumerationTest.VO(), WriteMapNullValue, WriteNullListAsEmpty));
    }

    private static class VO {
        private Enumeration value;

        public Enumeration getValue() {
            return value;
        }

        public void setValue(Enumeration value) {
            this.value = value;
        }
    }
}

