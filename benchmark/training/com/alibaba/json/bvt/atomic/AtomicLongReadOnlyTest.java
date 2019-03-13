package com.alibaba.json.bvt.atomic;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.junit.Assert;


public class AtomicLongReadOnlyTest extends TestCase {
    public void test_codec_null() throws Exception {
        AtomicLongReadOnlyTest.V0 v = new AtomicLongReadOnlyTest.V0(123);
        String text = JSON.toJSONString(v);
        Assert.assertEquals("{\"value\":123}", text);
        AtomicLongReadOnlyTest.V0 v1 = JSON.parseObject(text, AtomicLongReadOnlyTest.V0.class);
        Assert.assertEquals(v1.getValue().intValue(), v.getValue().intValue());
    }

    public static class V0 {
        private final AtomicLong value;

        public V0() {
            this(0);
        }

        public V0(int value) {
            this.value = new AtomicLong(value);
        }

        public AtomicLong getValue() {
            return value;
        }
    }
}

