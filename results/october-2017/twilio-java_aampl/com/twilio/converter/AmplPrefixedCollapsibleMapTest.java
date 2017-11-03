package com.twilio.converter;


/**
 * Test class for {@link PrefixedCollapsibleMap}.
 */
public class AmplPrefixedCollapsibleMapTest {
    @org.junit.Test
    public void testNull() {
        java.util.Map<java.lang.String, java.lang.String> actual = com.twilio.converter.PrefixedCollapsibleMap.serialize(null, "Prefix");
        org.junit.Assert.assertEquals(java.util.Collections.emptyMap(), actual);
    }

    @org.junit.Test
    public void testSingleKey() {
        java.util.Map<java.lang.String, java.lang.Object> input = new java.util.HashMap<>();
        input.put("foo", "bar");
        java.util.Map<java.lang.String, java.lang.String> actual = com.twilio.converter.PrefixedCollapsibleMap.serialize(input, "Prefix");
        java.util.Map<java.lang.String, java.lang.String> expected = new java.util.HashMap<>();
        expected.put("Prefix.foo", "bar");
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void testNestedKeys() {
        java.util.Map<java.lang.String, java.lang.Object> input = new java.util.HashMap<>();
        java.util.Map<java.lang.String, java.lang.Object> nested = new java.util.HashMap<>();
        nested.put("bar", "baz");
        input.put("foo", nested);
        java.util.Map<java.lang.String, java.lang.String> actual = com.twilio.converter.PrefixedCollapsibleMap.serialize(input, "Prefix");
        java.util.Map<java.lang.String, java.lang.String> expected = new java.util.HashMap<>();
        expected.put("Prefix.foo.bar", "baz");
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void testMultipleKeys() {
        java.util.Map<java.lang.String, java.lang.Object> input = new java.util.HashMap<>();
        java.util.Map<java.lang.String, java.lang.Object> nested = new java.util.HashMap<>();
        nested.put("bar", "baz");
        java.util.Map<java.lang.String, java.lang.Object> watson = new java.util.HashMap<>();
        watson.put("language", "en");
        watson.put("alice", "bob");
        input.put("foo", nested);
        input.put("watson", watson);
        java.util.Map<java.lang.String, java.lang.String> actual = com.twilio.converter.PrefixedCollapsibleMap.serialize(input, "Prefix");
        java.util.Map<java.lang.String, java.lang.String> expected = new java.util.HashMap<>();
        expected.put("Prefix.foo.bar", "baz");
        expected.put("Prefix.watson.language", "en");
        expected.put("Prefix.watson.alice", "bob");
        org.junit.Assert.assertEquals(expected, actual);
    }
}

