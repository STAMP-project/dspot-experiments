package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LazilyParsedNumber;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplPrimitiveTest {
    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString4_failAssert3_literalMutationString204_failAssert37() throws Exception {
        try {
            try {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = this.gson.fromJson(json, Number.class);
                expected.intValue();
                actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = this.gson.fromJson(json, Number.class);
                expected.longValue();
                actual.longValue();
                json = "^";
                actual = this.gson.fromJson(json, Number.class);
                actual.longValue();
                org.junit.Assert.fail("testNumberDeserialization_literalMutationString4 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString4_failAssert3_literalMutationString204 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_add17_literalMutationString391_failAssert23() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add17__6 = expected.intValue();
            int o_testNumberDeserialization_add17__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add17__14 = expected.longValue();
            long o_testNumberDeserialization_add17__15 = actual.longValue();
            json = "h1,";
            Number o_testNumberDeserialization_add17__17 = this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add17__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add17_literalMutationString391 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18)));
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1867() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1867__14 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1867__14);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }
