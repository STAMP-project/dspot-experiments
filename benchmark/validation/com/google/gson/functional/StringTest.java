package com.google.gson.functional;


import com.google.gson.Gson;
import junit.framework.TestCase;


/**
 * Functional tests for Json serialization and deserialization of strings.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class StringTest extends TestCase {
    private Gson gson;

    public void testStringValueSerialization() throws Exception {
        String value = "someRandomStringValue";
        TestCase.assertEquals((('"' + value) + '"'), gson.toJson(value));
    }

    public void testStringValueDeserialization() throws Exception {
        String value = "someRandomStringValue";
        String actual = gson.fromJson((("\"" + value) + "\""), String.class);
        TestCase.assertEquals(value, actual);
    }

    public void testSingleQuoteInStringSerialization() throws Exception {
        String valueWithQuotes = "beforeQuote'afterQuote";
        String jsonRepresentation = gson.toJson(valueWithQuotes);
        TestCase.assertEquals(valueWithQuotes, gson.fromJson(jsonRepresentation, String.class));
    }

    public void testEscapedCtrlNInStringSerialization() throws Exception {
        String value = "a\nb";
        String json = gson.toJson(value);
        TestCase.assertEquals("\"a\\nb\"", json);
    }

    public void testEscapedCtrlNInStringDeserialization() throws Exception {
        String json = "\'a\\nb\'";
        String actual = gson.fromJson(json, String.class);
        TestCase.assertEquals("a\nb", actual);
    }

    public void testEscapedCtrlRInStringSerialization() throws Exception {
        String value = "a\rb";
        String json = gson.toJson(value);
        TestCase.assertEquals("\"a\\rb\"", json);
    }

    public void testEscapedCtrlRInStringDeserialization() throws Exception {
        String json = "\'a\\rb\'";
        String actual = gson.fromJson(json, String.class);
        TestCase.assertEquals("a\rb", actual);
    }

    public void testEscapedBackslashInStringSerialization() throws Exception {
        String value = "a\\b";
        String json = gson.toJson(value);
        TestCase.assertEquals("\"a\\\\b\"", json);
    }

    public void testEscapedBackslashInStringDeserialization() throws Exception {
        String actual = gson.fromJson("\'a\\\\b\'", String.class);
        TestCase.assertEquals("a\\b", actual);
    }

    public void testSingleQuoteInStringDeserialization() throws Exception {
        String value = "beforeQuote'afterQuote";
        String actual = gson.fromJson((("\"" + value) + "\""), String.class);
        TestCase.assertEquals(value, actual);
    }

    public void testEscapingQuotesInStringSerialization() throws Exception {
        String valueWithQuotes = "beforeQuote\"afterQuote";
        String jsonRepresentation = gson.toJson(valueWithQuotes);
        String target = gson.fromJson(jsonRepresentation, String.class);
        TestCase.assertEquals(valueWithQuotes, target);
    }

    public void testEscapingQuotesInStringDeserialization() throws Exception {
        String value = "beforeQuote\\\"afterQuote";
        String actual = gson.fromJson((("\"" + value) + "\""), String.class);
        String expected = "beforeQuote\"afterQuote";
        TestCase.assertEquals(expected, actual);
    }

    public void testStringValueAsSingleElementArraySerialization() throws Exception {
        String[] target = new String[]{ "abc" };
        TestCase.assertEquals("[\"abc\"]", gson.toJson(target));
        TestCase.assertEquals("[\"abc\"]", gson.toJson(target, String[].class));
    }

    public void testStringWithEscapedSlashDeserialization() {
        String value = "/";
        String json = "\'\\/\'";
        String actual = gson.fromJson(json, String.class);
        TestCase.assertEquals(value, actual);
    }

    /**
     * Created in response to http://groups.google.com/group/google-gson/browse_thread/thread/2431d4a3d0d6cb23
     */
    public void testAssignmentCharSerialization() {
        String value = "abc=";
        String json = gson.toJson(value);
        TestCase.assertEquals("\"abc\\u003d\"", json);
    }

    /**
     * Created in response to http://groups.google.com/group/google-gson/browse_thread/thread/2431d4a3d0d6cb23
     */
    public void testAssignmentCharDeserialization() {
        String json = "\"abc=\"";
        String value = gson.fromJson(json, String.class);
        TestCase.assertEquals("abc=", value);
        json = "\'abc=\'";
        value = gson.fromJson(json, String.class);
        TestCase.assertEquals("abc=", value);
    }

    public void testJavascriptKeywordsInStringSerialization() {
        String value = "null true false function";
        String json = gson.toJson(value);
        TestCase.assertEquals((("\"" + value) + "\""), json);
    }

    public void testJavascriptKeywordsInStringDeserialization() {
        String json = "'null true false function'";
        String value = gson.fromJson(json, String.class);
        TestCase.assertEquals(json.substring(1, ((json.length()) - 1)), value);
    }
}

