package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyAtomicLongTest_field extends TestCase {
    public void test_readOnly() throws Exception {
        ReadOnlyAtomicLongTest_field.Model model = new ReadOnlyAtomicLongTest_field.Model();
        model.value.set(1001);
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":1001}", text);
        ReadOnlyAtomicLongTest_field.Model model2 = JSON.parseObject(text, ReadOnlyAtomicLongTest_field.Model.class);
        Assert.assertEquals(model.value.get(), model2.value.get());
    }

    public static class Model {
        public final AtomicLong value = new AtomicLong();
    }
}

