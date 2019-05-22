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
            json = "f^=";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString6_failAssert0() throws Exception {
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
            json = "!.0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString6 should have thrown JsonSyntaxException");
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

