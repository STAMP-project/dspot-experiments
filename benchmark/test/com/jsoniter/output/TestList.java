package com.jsoniter.output;


import EncodingMode.DYNAMIC_MODE;
import EncodingMode.REFLECTION_MODE;
import com.jsoniter.spi.Config;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;


public class TestList extends TestCase {
    public void test_indention() {
        Config cfg = new Config.Builder().encodingMode(REFLECTION_MODE).indentionStep(2).build();
        TestCase.assertEquals(("[\n" + (("  1,\n" + "  2\n") + "]")), JsonStream.serialize(cfg, Arrays.asList(1, 2)));
        cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals(("[\n" + (("  1,\n" + "  2\n") + "]")), JsonStream.serialize(cfg, Arrays.asList(1, 2)));
    }

    public void test_indention_with_empty_array() {
        Config cfg = new Config.Builder().encodingMode(REFLECTION_MODE).indentionStep(2).build();
        TestCase.assertEquals("[]", JsonStream.serialize(cfg, new ArrayList<Integer>()));
        cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals("[]", JsonStream.serialize(cfg, new ArrayList<Integer>()));
    }
}

