package com.fasterxml.jackson.core.json;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.SerializedString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests asserts that using closed `JsonParser` doesn't cause ArrayIndexOutOfBoundsException
 * with `nextXxx()` methods but returns `null` as expected.
 */
@RunWith(Parameterized.class)
public class JsonParserClosedCaseTest {
    private static final JsonFactory JSON_F = new JsonFactory();

    private JsonParser parser;

    public JsonParserClosedCaseTest(String parserName, JsonParser parser) {
        this.parser = parser;
    }

    @Test
    public void testNullReturnedOnClosedParserOnNextFieldName() throws Exception {
        Assert.assertNull(parser.nextFieldName());
    }

    @Test
    public void testFalseReturnedOnClosedParserOnNextFieldNameSerializedString() throws Exception {
        Assert.assertFalse(parser.nextFieldName(new SerializedString("")));
    }

    @Test
    public void testNullReturnedOnClosedParserOnNextToken() throws Exception {
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testNullReturnedOnClosedParserOnNextValue() throws Exception {
        Assert.assertNull(parser.nextValue());
    }
}

