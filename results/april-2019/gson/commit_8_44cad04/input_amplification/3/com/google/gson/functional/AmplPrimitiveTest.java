package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import junit.framework.TestCase;


public class AmplPrimitiveTest extends TestCase {
    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gson = new Gson();
    }

    public void testNumberDeserialization_add15_literalMutationString102_failAssert0_literalMutationString1482_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add15__6 = expected.intValue();
                int o_testNumberDeserialization_add15__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__14 = expected.longValue();
                long o_testNumberDeserialization_add15__15 = expected.longValue();
                long o_testNumberDeserialization_add15__16 = actual.longValue();
                json = "X yMN`s;U{R IF";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString102 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString102_failAssert0_literalMutationString1482 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add14_literalMutationString94_failAssert0_add3750_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add14__6 = expected.intValue();
                int o_testNumberDeserialization_add14__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                Number o_testNumberDeserialization_add14__12 = gson.fromJson(json, Number.class);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add14__15 = expected.longValue();
                long o_testNumberDeserialization_add14__16 = actual.longValue();
                json = "1r.0";
                gson.fromJson(json, Number.class);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add14__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add14_literalMutationString94 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add14_literalMutationString94_failAssert0_add3750 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString103_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add15__6 = expected.intValue();
            int o_testNumberDeserialization_add15__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add15__14 = expected.longValue();
            long o_testNumberDeserialization_add15__15 = expected.longValue();
            long o_testNumberDeserialization_add15__16 = actual.longValue();
            json = "1z.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add15__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString103_failAssert0_add3700_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                expected.intValue();
                int o_testNumberDeserialization_add15__6 = expected.intValue();
                int o_testNumberDeserialization_add15__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__14 = expected.longValue();
                long o_testNumberDeserialization_add15__15 = expected.longValue();
                long o_testNumberDeserialization_add15__16 = actual.longValue();
                json = "1z.0";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103_failAssert0_add3700 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add18_add333_literalMutationString987_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add18__6 = expected.intValue();
            int o_testNumberDeserialization_add18__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__14 = expected.longValue();
            long o_testNumberDeserialization_add18__15 = actual.longValue();
            json = "M.0";
            Number o_testNumberDeserialization_add18_add333__25 = gson.fromJson(json, Number.class);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__19 = actual.longValue();
            long o_testNumberDeserialization_add18__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add18_add333_literalMutationString987 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_failAssert0_literalMutationString236_failAssert0() throws Exception {
        try {
            {
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
                json = "k+.0";
                actual = gson.fromJson(json, Number.class);
                actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_failAssert0_literalMutationString236 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add18_literalMutationString150_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add18__6 = expected.intValue();
            int o_testNumberDeserialization_add18__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__14 = expected.longValue();
            long o_testNumberDeserialization_add18__15 = actual.longValue();
            json = "U.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__19 = actual.longValue();
            long o_testNumberDeserialization_add18__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add18_literalMutationString150 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add10_literalMutationString131_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number o_testNumberDeserialization_add10__4 = gson.fromJson(json, Number.class);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add10__7 = expected.intValue();
            int o_testNumberDeserialization_add10__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add10__15 = expected.longValue();
            long o_testNumberDeserialization_add10__16 = actual.longValue();
            json = "T)-";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add10__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add10_literalMutationString131 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add16_literalMutationString112_failAssert0_add2798_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add16__6 = expected.intValue();
                int o_testNumberDeserialization_add16__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add16__14 = actual.longValue();
                long o_testNumberDeserialization_add16__15 = expected.longValue();
                long o_testNumberDeserialization_add16__16 = actual.longValue();
                json = "A.0";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add16__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add16_literalMutationString112 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add16_literalMutationString112_failAssert0_add2798 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString6_failAssert0_add405_failAssert0_literalMutationString1691_failAssert0() throws Exception {
        try {
            {
                {
                    String json = "1";
                    Number expected = new Integer(json);
                    Number actual = gson.fromJson(json, Number.class);
                    expected.intValue();
                    actual.intValue();
                    json = String.valueOf(Long.MAX_VALUE);
                    expected = new Long(json);
                    actual = gson.fromJson(json, Number.class);
                    expected.longValue();
                    expected.longValue();
                    actual.longValue();
                    json = "^qB(";
                    actual = gson.fromJson(json, Number.class);
                    actual.longValue();
                    junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString6 should have thrown JsonSyntaxException");
                }
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString6_failAssert0_add405 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString6_failAssert0_add405_failAssert0_literalMutationString1691 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString103_failAssert0_add3699_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                gson.fromJson(json, Number.class);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add15__6 = expected.intValue();
                int o_testNumberDeserialization_add15__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__14 = expected.longValue();
                long o_testNumberDeserialization_add15__15 = expected.longValue();
                long o_testNumberDeserialization_add15__16 = actual.longValue();
                json = "1z.0";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103_failAssert0_add3699 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add12_add293_literalMutationString1003_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add12_add293__6 = actual.intValue();
            int o_testNumberDeserialization_add12__6 = actual.intValue();
            int o_testNumberDeserialization_add12__7 = expected.intValue();
            int o_testNumberDeserialization_add12__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__15 = expected.longValue();
            long o_testNumberDeserialization_add12__16 = actual.longValue();
            json = "l<x";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add12_add293_literalMutationString1003 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add14_literalMutationString94_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add14__6 = expected.intValue();
            int o_testNumberDeserialization_add14__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            Number o_testNumberDeserialization_add14__12 = gson.fromJson(json, Number.class);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__15 = expected.longValue();
            long o_testNumberDeserialization_add14__16 = actual.longValue();
            json = "1r.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add14__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add14_literalMutationString94 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString105_failAssert0_add3406_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add15__6 = expected.intValue();
                int o_testNumberDeserialization_add15__7 = actual.intValue();
                String.valueOf(Long.MAX_VALUE);
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__14 = expected.longValue();
                long o_testNumberDeserialization_add15__15 = expected.longValue();
                long o_testNumberDeserialization_add15__16 = actual.longValue();
                json = "Yc ";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString105 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString105_failAssert0_add3406 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add16_add291_literalMutationString942_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add16__6 = expected.intValue();
            int o_testNumberDeserialization_add16__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add16__14 = actual.longValue();
            long o_testNumberDeserialization_add16__15 = expected.longValue();
            long o_testNumberDeserialization_add16__16 = actual.longValue();
            json = "1N.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add16_add291__30 = actual.longValue();
            long o_testNumberDeserialization_add16__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add16_add291_literalMutationString942 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add16_literalMutationString112_failAssert0_literalMutationString1358_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add16__6 = expected.intValue();
                int o_testNumberDeserialization_add16__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add16__14 = actual.longValue();
                long o_testNumberDeserialization_add16__15 = expected.longValue();
                long o_testNumberDeserialization_add16__16 = actual.longValue();
                json = "T.0";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add16__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add16_literalMutationString112 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add16_literalMutationString112_failAssert0_literalMutationString1358 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add12_add294_literalMutationString1257_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add12__6 = actual.intValue();
            int o_testNumberDeserialization_add12_add294__9 = expected.intValue();
            int o_testNumberDeserialization_add12__7 = expected.intValue();
            int o_testNumberDeserialization_add12__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__15 = expected.longValue();
            long o_testNumberDeserialization_add12__16 = actual.longValue();
            json = "0C-";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add12_add294_literalMutationString1257 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString8_failAssert0_add388_failAssert0() throws Exception {
        try {
            {
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
                actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8_failAssert0_add388 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_failAssert0_add431_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                expected.intValue();
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
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_failAssert0_add431 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add17_literalMutationString157_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add17__6 = expected.intValue();
            int o_testNumberDeserialization_add17__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add17__14 = expected.longValue();
            long o_testNumberDeserialization_add17__15 = actual.longValue();
            json = "l V";
            Number o_testNumberDeserialization_add17__17 = gson.fromJson(json, Number.class);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add17__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add17_literalMutationString157 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString8_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add16_literalMutationString112_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add16__6 = expected.intValue();
            int o_testNumberDeserialization_add16__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add16__14 = actual.longValue();
            long o_testNumberDeserialization_add16__15 = expected.longValue();
            long o_testNumberDeserialization_add16__16 = actual.longValue();
            json = "A.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add16__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add16_literalMutationString112 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_failAssert0_add433_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                expected.intValue();
                actual.intValue();
                String.valueOf(Long.MAX_VALUE);
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                expected.longValue();
                actual.longValue();
                json = "k.0";
                actual = gson.fromJson(json, Number.class);
                actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_failAssert0_add433 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add12_literalMutationString123_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add12__6 = actual.intValue();
            int o_testNumberDeserialization_add12__7 = expected.intValue();
            int o_testNumberDeserialization_add12__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__15 = expected.longValue();
            long o_testNumberDeserialization_add12__16 = actual.longValue();
            json = "h.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add12_literalMutationString123 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add12_literalMutationString122_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add12__6 = actual.intValue();
            int o_testNumberDeserialization_add12__7 = expected.intValue();
            int o_testNumberDeserialization_add12__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__15 = expected.longValue();
            long o_testNumberDeserialization_add12__16 = actual.longValue();
            json = "Bcv";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add12__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add12_literalMutationString122 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString103_failAssert0_literalMutationString2088_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add15__6 = expected.intValue();
                int o_testNumberDeserialization_add15__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__14 = expected.longValue();
                long o_testNumberDeserialization_add15__15 = expected.longValue();
                long o_testNumberDeserialization_add15__16 = actual.longValue();
                json = "1z0";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103_failAssert0_literalMutationString2088 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add14_literalMutationString93_failAssert0_literalMutationString1449_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add14__6 = expected.intValue();
                int o_testNumberDeserialization_add14__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                Number o_testNumberDeserialization_add14__12 = gson.fromJson(json, Number.class);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add14__15 = expected.longValue();
                long o_testNumberDeserialization_add14__16 = actual.longValue();
                json = "\'_-upper-camel-leading-underscore\':4,\'lower_words\':5,\'u-p-p-e-r_-w-o-r-d-s\':6,";
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add14__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add14_literalMutationString93 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add14_literalMutationString93_failAssert0_literalMutationString1449 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_failAssert0_add430_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                gson.fromJson(json, Number.class);
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
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_failAssert0_add430 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add18_literalMutationString148_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add18__6 = expected.intValue();
            int o_testNumberDeserialization_add18__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__14 = expected.longValue();
            long o_testNumberDeserialization_add18__15 = actual.longValue();
            json = "vnj";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__19 = actual.longValue();
            long o_testNumberDeserialization_add18__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add18_literalMutationString148 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_add280_literalMutationString1182_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add15__6 = expected.intValue();
            int o_testNumberDeserialization_add15__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add15__14 = expected.longValue();
            long o_testNumberDeserialization_add15__15 = expected.longValue();
            long o_testNumberDeserialization_add15__16 = actual.longValue();
            json = "p:A";
            Number o_testNumberDeserialization_add15_add280__28 = gson.fromJson(json, Number.class);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add15__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add15_add280_literalMutationString1182 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add13_literalMutationString86_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add13__6 = expected.intValue();
            int o_testNumberDeserialization_add13__7 = actual.intValue();
            String o_testNumberDeserialization_add13__8 = String.valueOf(Long.MAX_VALUE);
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add13__15 = expected.longValue();
            long o_testNumberDeserialization_add13__16 = actual.longValue();
            json = "V.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add13__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add13_literalMutationString86 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString8_failAssert0_literalMutationString192_failAssert0() throws Exception {
        try {
            {
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
                json = "ds";
                actual = gson.fromJson(json, Number.class);
                actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8_failAssert0_literalMutationString192 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString3_failAssert0_literalMutationString214_failAssert0_literalMutationString2079_failAssert0() throws Exception {
        try {
            {
                {
                    String json = "6";
                    Number expected = new Integer(json);
                    Number actual = gson.fromJson(json, Number.class);
                    expected.intValue();
                    actual.intValue();
                    json = String.valueOf(Long.MAX_VALUE);
                    expected = new Long(json);
                    actual = gson.fromJson(json, Number.class);
                    expected.longValue();
                    actual.longValue();
                    json = "1+.0";
                    actual = gson.fromJson(json, Number.class);
                    actual.longValue();
                    junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString3 should have thrown NumberFormatException");
                }
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString3_failAssert0_literalMutationString214 should have thrown AssertionFailedError");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString3_failAssert0_literalMutationString214_failAssert0_literalMutationString2079 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString103_failAssert0_add3708_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                int o_testNumberDeserialization_add15__6 = expected.intValue();
                int o_testNumberDeserialization_add15__7 = actual.intValue();
                json = String.valueOf(Long.MAX_VALUE);
                expected = new Long(json);
                actual = gson.fromJson(json, Number.class);
                long o_testNumberDeserialization_add15__14 = expected.longValue();
                long o_testNumberDeserialization_add15__15 = expected.longValue();
                long o_testNumberDeserialization_add15__16 = actual.longValue();
                json = "1z.0";
                actual = gson.fromJson(json, Number.class);
                actual.longValue();
                long o_testNumberDeserialization_add15__20 = actual.longValue();
                junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString103_failAssert0_add3708 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString8_failAssert0_add380_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                gson.fromJson(json, Number.class);
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
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8_failAssert0_add380 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add18_add334_literalMutationString1146_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add18__6 = expected.intValue();
            int o_testNumberDeserialization_add18__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18__14 = expected.longValue();
            long o_testNumberDeserialization_add18__15 = actual.longValue();
            json = "1P.0";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add18_add334__27 = actual.longValue();
            long o_testNumberDeserialization_add18__19 = actual.longValue();
            long o_testNumberDeserialization_add18__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add18_add334_literalMutationString1146 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString8_failAssert0_add381_failAssert0() throws Exception {
        try {
            {
                String json = "1";
                Number expected = new Integer(json);
                Number actual = gson.fromJson(json, Number.class);
                expected.intValue();
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
                junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString8_failAssert0_add381 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add11_add325_literalMutationString1085_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add11__6 = expected.intValue();
            int o_testNumberDeserialization_add11__7 = expected.intValue();
            int o_testNumberDeserialization_add11__8 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add11__15 = expected.longValue();
            long o_testNumberDeserialization_add11__16 = actual.longValue();
            json = "!D*";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add11_add325__30 = actual.longValue();
            long o_testNumberDeserialization_add11__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add11_add325_literalMutationString1085 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_add15_literalMutationString105_failAssert0() throws Exception {
        try {
            String json = "1";
            Number expected = new Integer(json);
            Number actual = gson.fromJson(json, Number.class);
            int o_testNumberDeserialization_add15__6 = expected.intValue();
            int o_testNumberDeserialization_add15__7 = actual.intValue();
            json = String.valueOf(Long.MAX_VALUE);
            expected = new Long(json);
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add15__14 = expected.longValue();
            long o_testNumberDeserialization_add15__15 = expected.longValue();
            long o_testNumberDeserialization_add15__16 = actual.longValue();
            json = "Yc ";
            actual = gson.fromJson(json, Number.class);
            long o_testNumberDeserialization_add15__20 = actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_add15_literalMutationString105 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    private String extractElementFromArray(String json) {
        return json.substring(((json.indexOf('[')) + 1), json.indexOf(']'));
    }

    private static class ClassWithIntegerField {
        Integer i;
    }
}

