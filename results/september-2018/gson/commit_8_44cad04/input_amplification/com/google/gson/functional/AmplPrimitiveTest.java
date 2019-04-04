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

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1862() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1862__11 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1862__11)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
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

    @Test(timeout = 10000)
    public void testNumberDeserialization_add14_literalMutationString120_failAssert11() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add14__6 = expected.intValue();
            int o_testNumberDeserialization_add14__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            Number o_testNumberDeserialization_add14__12 = this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__15 = expected.longValue();
            long o_testNumberDeserialization_add14__16 = actual.longValue();
            json = "IvC";
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add14_literalMutationString120 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__16)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString121__17)));
            json = "km.0";
            Assert.assertEquals("km.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString4_failAssert3_add233_literalMutationString1473_failAssert63() throws Exception {
        try {
            try {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = this.gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add233__8 = expected.intValue();
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add233__9 = actual.intValue();
                String o_testNumberDeserialization_literalMutationString4_failAssert3_add233__10 = String.valueOf(Long.MAX_VALUE);
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = this.gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add233__17 = expected.longValue();
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add233__18 = actual.longValue();
                json = "d";
                actual = this.gson.fromJson(json, Number.class);
                actual.longValue();
                org.junit.Assert.fail("testNumberDeserialization_literalMutationString4 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString4_failAssert3_add233_literalMutationString1473 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1902() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
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
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_literalMutationString1809() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "r.0";
            Assert.assertEquals("r.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__16)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString118__17)));
            json = "k0";
            Assert.assertEquals("k0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1339() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1890() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1890__20 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1890__20)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3691() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3691__17 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3691__17);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1330() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1330__26 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1330__26)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3696() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3696__21 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3696__21)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3696__21)).hashCode())));
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1895() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1895__23 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1895__23)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1334() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add392() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add392__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add392__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add392__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add392__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add392__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add392__16)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add392__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add392__17)));
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1851() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1851__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1851__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1851__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
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

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString4_failAssert3_add251_literalMutationString1164_failAssert95() throws Exception {
        try {
            try {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = this.gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add251__8 = expected.intValue();
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add251__9 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = this.gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add251__16 = expected.longValue();
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add251__17 = actual.longValue();
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add251__18 = actual.longValue();
                json = "m";
                actual = this.gson.fromJson(json, Number.class);
                actual.longValue();
                org.junit.Assert.fail("testNumberDeserialization_literalMutationString4 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString4_failAssert3_add251_literalMutationString1164 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_literalMutationString1814() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k0";
            Assert.assertEquals("k0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_add14_literalMutationString112_failAssert12() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add14__6 = expected.intValue();
            int o_testNumberDeserialization_add14__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            Number o_testNumberDeserialization_add14__12 = this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__15 = expected.longValue();
            long o_testNumberDeserialization_add14__16 = actual.longValue();
            json = "1U.0";
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add14_literalMutationString112 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add148_add2202() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__10 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2202__17 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2202__17);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1857() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1857__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1857__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
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

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add148_add2207() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2207__21 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2207__21)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2207__21)).hashCode())));
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__16)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_literalMutationString126__17)));
            json = "|.0";
            Assert.assertEquals("|.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_literalMutationString3650() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "gdw";
            Assert.assertEquals("gdw", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1886() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1886__23 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1886__23)));
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

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18)));
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1283() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1283__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1283__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1283__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_literalMutationString1825() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "k0";
            Assert.assertEquals("k0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add371_add2239() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2239__17 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2239__17);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1319() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1319__20 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1319__20)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add371_add2243() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2243__21 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2243__21)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2243__21)).hashCode())));
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3679() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3679__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3679__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3712() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1891() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1891__26 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1891__26)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1911() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1877() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1877__18 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1877__18)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1877__18)).hashCode())));
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3674() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3674__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3674__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3674__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18)));
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1312() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1312__18 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1312__18)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1312__18)).hashCode())));
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add371() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18)));
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1875() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1875__18 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1875__18)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1875__18)).hashCode())));
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

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1907() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add143_add2611() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2611__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2611__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2611__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1295() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1295__11 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1295__11)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add192() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add192__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add192__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add192__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add192__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add192__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add192__16)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add192__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add192__17)));
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1903() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1903__26 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1903__26)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_literalMutationString1818() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "b>_";
            Assert.assertEquals("b>_", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_literalMutationString1241() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "7hs";
            Assert.assertEquals("7hs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3687() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3687__14 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3687__14)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add371_add2251() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2251__23 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2251__23)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_add17_literalMutationString354_failAssert24() throws Exception {
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
            json = "a.0";
            Number o_testNumberDeserialization_add17__17 = this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add17__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add17_literalMutationString354 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1289() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1289__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1289__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1881() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1881__20 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add181_add1881__20)));
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

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_literalMutationString1249() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "><6";
            Assert.assertEquals("><6", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18)));
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3684() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3684__11 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3684__11)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1324() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1324__23 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1324__23)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__16)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString353__17)));
            json = "ds";
            Assert.assertEquals("ds", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1858() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1858__14 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1858__14);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_literalMutationString3622() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhhs";
            Assert.assertEquals("dhhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1852() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1852__11 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1852__11)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__16)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString348__17)));
            json = "ahs";
            Assert.assertEquals("ahs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_literalMutationString1256() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dJhs";
            Assert.assertEquals("dJhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_add10_literalMutationString153_failAssert28() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_add10__4 = this.gson.fromJson(json, Number.class);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add10__7 = expected.intValue();
            int o_testNumberDeserialization_add10__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add10__15 = expected.longValue();
            long o_testNumberDeserialization_add10__16 = actual.longValue();
            json = "1_.0";
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add10__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add10_literalMutationString153 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__16)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString346__17)));
            json = "1Rv";
            Assert.assertEquals("1Rv", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_add1897() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add143_add2633() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2633__17 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2633__17);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add143_add2636() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2636__21 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("9223372036854775807", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2636__21)).toString());
            Assert.assertEquals(-1773151198, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2636__21)).hashCode())));
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1847() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1847__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1847__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3708() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add371_add2266() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_literalMutationString1837() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "kQ.0";
            Assert.assertEquals("kQ.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3700() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3700__23 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3700__23)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add181_literalMutationString1832() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add181__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add181__18 = actual.longValue();
            json = "W.0";
            Assert.assertEquals("W.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_literalMutationString1262() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "ds";
            Assert.assertEquals("ds", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_add3703() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3703__26 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_add367_add3703__26)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__8)));
            int o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__9)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__16 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__16)));
            long o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__17 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString8_failAssert7_literalMutationString339__17)));
            json = "dUhs";
            Assert.assertEquals("dUhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_add1842() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1842__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1842__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add177_add1842__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add383_add1303() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add383__9 = actual.intValue();
            String o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1303__14 = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", o_testNumberDeserialization_literalMutationString8_failAssert7_add383_add1303__14);
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__17 = actual.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add383__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_literalMutationString1822() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "k4.0";
            Assert.assertEquals("k4.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_add17_literalMutationString398_failAssert22() throws Exception {
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
            json = "1R.0";
            Number o_testNumberDeserialization_add17__17 = this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add17__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add17_literalMutationString398 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add367_literalMutationString3644() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add367__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add367__18 = actual.longValue();
            json = "+hs";
            Assert.assertEquals("+hs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add177_literalMutationString1828() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add177__9 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__16 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add177__18 = actual.longValue();
            json = "(YL";
            Assert.assertEquals("(YL", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add143_add2640() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2640__23 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143_add2640__23)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_add14_literalMutationString129_failAssert13() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add14__6 = expected.intValue();
            int o_testNumberDeserialization_add14__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            Number o_testNumberDeserialization_add14__12 = this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__15 = expected.longValue();
            long o_testNumberDeserialization_add14__16 = actual.longValue();
            json = "l.0";
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__20 = actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_add14_literalMutationString129 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            expected.intValue();
            actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            expected.longValue();
            actual.longValue();
            json = "dhs";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            expected.intValue();
            actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            expected.longValue();
            actual.longValue();
            json = "k.0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString4_failAssert3_add249_literalMutationString2696_failAssert87() throws Exception {
        try {
            try {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = this.gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add249__8 = expected.intValue();
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add249__9 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = this.gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add249__16 = expected.longValue();
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add249__17 = expected.longValue();
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add249__18 = actual.longValue();
                json = "x";
                actual = this.gson.fromJson(json, Number.class);
                actual.longValue();
                org.junit.Assert.fail("testNumberDeserialization_literalMutationString4 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString4_failAssert3_add249_literalMutationString2696 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString4_failAssert3_add212_literalMutationString2282_failAssert56() throws Exception {
        try {
            try {
                String json = "1";
                Number expected = new Integer(json);
                Number o_testNumberDeserialization_literalMutationString4_failAssert3_add212__6 = this.gson.fromJson(json, Number.class);
                Number actual = this.gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add212__9 = expected.intValue();
                int o_testNumberDeserialization_literalMutationString4_failAssert3_add212__10 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = this.gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add212__17 = expected.longValue();
                long o_testNumberDeserialization_literalMutationString4_failAssert3_add212__18 = actual.longValue();
                json = "a";
                actual = this.gson.fromJson(json, Number.class);
                actual.longValue();
                org.junit.Assert.fail("testNumberDeserialization_literalMutationString4 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString4_failAssert3_add212_literalMutationString2282 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add148_add2183() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2183__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2183__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148_add2183__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add148_add2225() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add143_add2650() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18 = actual.longValue();
            json = "k.0";
            Assert.assertEquals("k.0", json);
            this.gson.fromJson(json, Number.class);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add148() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__9 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148__9)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add148__10 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148__10)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__17 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148__17)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add148__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add148__18)));
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString8_failAssert7_add371_add2210() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2210__6 = this.gson.fromJson(json, Number.class);
            Assert.assertEquals("1", ((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2210__6)).toString());
            Assert.assertEquals(49, ((int) (((LazilyParsedNumber) (o_testNumberDeserialization_literalMutationString8_failAssert7_add371_add2210__6)).hashCode())));
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__8 = expected.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__9 = actual.intValue();
            int o_testNumberDeserialization_literalMutationString8_failAssert7_add371__10 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__17 = expected.longValue();
            long o_testNumberDeserialization_literalMutationString8_failAssert7_add371__18 = actual.longValue();
            json = "dhs";
            Assert.assertEquals("dhs", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testNumberDeserialization_literalMutationString7_failAssert6_add143() throws Exception {
        try {
            String json = "1";
            Assert.assertEquals("1", json);
            Number expected = new Integer(json);
            Number actual = this.gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143__8)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9 = expected.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143__9)));
            int o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10 = actual.intValue();
            Assert.assertEquals(1, ((int) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143__10)));
            json = String.valueOf(Long.MAX_VALUE);
            Assert.assertEquals("9223372036854775807", json);
            expected = new Long(json);
            actual = this.gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17 = expected.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143__17)));
            long o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18 = actual.longValue();
            Assert.assertEquals(9223372036854775807L, ((long) (o_testNumberDeserialization_literalMutationString7_failAssert6_add143__18)));
            json = "k.0";
            Assert.assertEquals("k.0", json);
            actual = this.gson.fromJson(json, Number.class);
            actual.longValue();
            org.junit.Assert.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
        }
    }

    private String extractElementFromArray(String json) {
        return json.substring(((json.indexOf('[')) + 1), json.indexOf(']'));
    }

    private static class ClassWithIntegerField {
        Integer i;
    }
}

