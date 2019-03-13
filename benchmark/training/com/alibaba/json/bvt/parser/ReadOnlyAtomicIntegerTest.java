package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyAtomicIntegerTest extends TestCase {
    public void test_readOnly() throws Exception {
        ReadOnlyAtomicIntegerTest.Model model = new ReadOnlyAtomicIntegerTest.Model();
        model.value.set(1001);
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":1001}", text);
        ReadOnlyAtomicIntegerTest.Model model2 = JSON.parseObject(text, ReadOnlyAtomicIntegerTest.Model.class);
        Assert.assertEquals(model.value.get(), model2.value.get());
    }

    public static class Model {
        private final AtomicInteger value = new AtomicInteger();

        public AtomicInteger getValue() {
            return value;
        }
    }
}

