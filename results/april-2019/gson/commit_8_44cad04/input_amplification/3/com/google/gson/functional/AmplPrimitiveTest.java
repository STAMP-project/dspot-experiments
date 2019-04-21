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

    public void testNumberDeserialization_literalMutationString6_literalMutationString100_literalMutationString789_failAssert0() throws Exception {
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
            json = "X,0.0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString6_literalMutationString100_literalMutationString789 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString6_literalMutationString105_literalMutationString1518_failAssert0() throws Exception {
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
            json = "g,0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString6_literalMutationString105_literalMutationString1518 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_literalMutationString129_literalMutationString772_failAssert0() throws Exception {
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
            json = "3P.0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_literalMutationString129_literalMutationString772 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_literalMutationString129_literalMutationString773_failAssert0() throws Exception {
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
            json = "+.0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_literalMutationString129_literalMutationString773 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_literalMutationString128_literalMutationString2087_failAssert0() throws Exception {
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
            json = "\'_-upper-camel-leading-underscore\':4,\'lower_words\':5,\'u-p-p-e-r_-w-o-r-d-s\':6,";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_literalMutationString128_literalMutationString2087 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberDeserialization_literalMutationString7_literalMutationString132_literalMutationString1494_failAssert0() throws Exception {
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
            json = "ki.0";
            actual = gson.fromJson(json, Number.class);
            actual.longValue();
            junit.framework.TestCase.fail("testNumberDeserialization_literalMutationString7_literalMutationString132_literalMutationString1494 should have thrown JsonSyntaxException");
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

