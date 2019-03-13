package com.alibaba.json.bvt.atomic;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Assert;


public class AtomicBooleanReadOnlyTest extends TestCase {
    public void test_codec_null() throws Exception {
        AtomicBooleanReadOnlyTest.V0 v = new AtomicBooleanReadOnlyTest.V0(true);
        String text = JSON.toJSONString(v);
        Assert.assertEquals("{\"value\":true}", text);
        AtomicBooleanReadOnlyTest.V0 v1 = JSON.parseObject(text, AtomicBooleanReadOnlyTest.V0.class);
        Assert.assertEquals(v1.getValue().get(), v.getValue().get());
    }

    public static class V0 {
        private final AtomicBoolean value;

        public V0() {
            this(false);
        }

        public V0(boolean value) {
            this.value = new AtomicBoolean(value);
        }

        public AtomicBoolean getValue() {
            return value;
        }
    }
}

