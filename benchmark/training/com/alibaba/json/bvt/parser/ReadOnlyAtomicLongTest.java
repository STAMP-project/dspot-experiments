package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyAtomicLongTest extends TestCase {
    public void test_readOnly() throws Exception {
        ReadOnlyAtomicLongTest.Model model = new ReadOnlyAtomicLongTest.Model();
        model.value.set(1001);
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":1001}", text);
        ReadOnlyAtomicLongTest.Model model2 = JSON.parseObject(text, ReadOnlyAtomicLongTest.Model.class);
        Assert.assertEquals(model.value.get(), model2.value.get());
    }

    public static class Model {
        private final AtomicLong value = new AtomicLong();

        public AtomicLong getValue() {
            return value;
        }
    }
}

