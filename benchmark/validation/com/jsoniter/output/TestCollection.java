package com.jsoniter.output;


import EncodingMode.DYNAMIC_MODE;
import EncodingMode.REFLECTION_MODE;
import com.jsoniter.spi.Config;
import java.util.HashSet;
import junit.framework.TestCase;


public class TestCollection extends TestCase {
    public void test_indention() {
        HashSet<Integer> set = new HashSet<Integer>();
        set.add(1);
        Config cfg = new Config.Builder().encodingMode(REFLECTION_MODE).indentionStep(2).build();
        TestCase.assertEquals(("[\n" + ("  1\n" + "]")), JsonStream.serialize(cfg, set));
        cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals(("[\n" + ("  1\n" + "]")), JsonStream.serialize(cfg, set));
    }

    public void test_indention_with_empty_array() {
        Config cfg = new Config.Builder().encodingMode(REFLECTION_MODE).indentionStep(2).build();
        TestCase.assertEquals("[]", JsonStream.serialize(cfg, new HashSet<Integer>()));
        cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals("[]", JsonStream.serialize(cfg, new HashSet<Integer>()));
    }
}

