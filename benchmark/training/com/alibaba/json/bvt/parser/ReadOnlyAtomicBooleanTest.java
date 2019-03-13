package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyAtomicBooleanTest extends TestCase {
    public void test_readOnly() throws Exception {
        ReadOnlyAtomicBooleanTest.Model model = new ReadOnlyAtomicBooleanTest.Model();
        model.value.set(true);
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":true}", text);
        ReadOnlyAtomicBooleanTest.Model model2 = JSON.parseObject(text, ReadOnlyAtomicBooleanTest.Model.class);
        Assert.assertEquals(model.value.get(), model2.value.get());
    }

    public static class Model {
        private final AtomicBoolean value = new AtomicBoolean();

        public AtomicBoolean getValue() {
            return value;
        }
    }
}

