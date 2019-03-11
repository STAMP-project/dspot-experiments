package com.alibaba.json.bvt.ref;


import SerializerFeature.DisableCircularReferenceDetect;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest extends TestCase {
    public void test_ref() throws Exception {
        JSONSerializer ser = new JSONSerializer();
        Assert.assertFalse(ser.containsReference(null));
    }

    public void test_array_ref() throws Exception {
        JSON.toJSONString(new RefTest.A[]{ new RefTest.A() }, DisableCircularReferenceDetect);
    }

    public class A {
        private RefTest.A a;

        public RefTest.A getA() {
            return a;
        }

        public void setA(RefTest.A a) {
            this.a = a;
        }
    }
}

