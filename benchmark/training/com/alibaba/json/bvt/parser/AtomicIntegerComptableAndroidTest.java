package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/03/2017.
 */
public class AtomicIntegerComptableAndroidTest extends TestCase {
    public void test_for_compatible_zero() throws Exception {
        String text = "{\"andIncrement\":-1,\"andDecrement\":0}";
        TestCase.assertEquals(0, JSON.parseObject(text, AtomicInteger.class).intValue());
    }

    public void test_for_compatible_six() throws Exception {
        String text = "{\"andIncrement\":5,\"andDecrement\":6}";
        TestCase.assertEquals(6, JSON.parseObject(text, AtomicInteger.class).intValue());
    }

    public void test_for_compatible_five() throws Exception {
        String text = "{\"andDecrement\":6,\"andIncrement\":5}";
        TestCase.assertEquals(5, JSON.parseObject(text, AtomicInteger.class).intValue());
    }
}

