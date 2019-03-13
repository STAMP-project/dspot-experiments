package com.alibaba.json.bvt.parser;


import AtomicCodec.instance;
import JSONToken.LBRACKET;
import JSONToken.LITERAL_INT;
import JSONToken.LITERAL_STRING;
import junit.framework.TestCase;
import org.junit.Assert;


public class FastMatchCheckTest extends TestCase {
    public void test_match() throws Exception {
        Assert.assertEquals(LBRACKET, instance.getFastMatchToken());
        Assert.assertEquals(LITERAL_STRING, MiscCodec.instance.getFastMatchToken());
        Assert.assertEquals(LITERAL_INT, NumberDeserializer.instance.getFastMatchToken());
        Assert.assertEquals(LITERAL_INT, SqlDateDeserializer.instance.getFastMatchToken());
        Assert.assertEquals(LBRACKET, ObjectArrayCodec.instance.getFastMatchToken());
        Assert.assertEquals(LITERAL_STRING, CharacterCodec.instance.getFastMatchToken());
    }
}

