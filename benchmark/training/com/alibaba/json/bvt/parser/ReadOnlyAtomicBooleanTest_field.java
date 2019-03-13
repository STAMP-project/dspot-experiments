package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyAtomicBooleanTest_field extends TestCase {
    public void test_readOnly() throws Exception {
        ReadOnlyAtomicBooleanTest_field.Model model = new ReadOnlyAtomicBooleanTest_field.Model();
        model.value.set(true);
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":true}", text);
        ReadOnlyAtomicBooleanTest_field.Model model2 = JSON.parseObject(text, ReadOnlyAtomicBooleanTest_field.Model.class);
        Assert.assertEquals(model.value.get(), model2.value.get());
    }

    public static class Model {
        public final AtomicBoolean value = new AtomicBoolean();
    }
}

