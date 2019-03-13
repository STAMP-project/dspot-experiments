package org.embulk.spi.json;


import JsonParser.Stream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.msgpack.value.Value;


public class TestJsonParser {
    @Test
    public void testString() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("\"foobar\"");
        Assert.assertFalse(msgpackValue.getValueType().isNumberType());
        Assert.assertTrue(msgpackValue.getValueType().isStringType());
        Assert.assertEquals("foobar", msgpackValue.asStringValue().toString());
    }

    @Test(expected = JsonParseException.class)
    public void testStringUnquoted() throws Exception {
        final JsonParser parser = new JsonParser();
        parser.parse("foobar");
    }

    @Test
    public void testOrdinaryInteger() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("12345");
        Assert.assertTrue(msgpackValue.getValueType().isNumberType());
        Assert.assertTrue(msgpackValue.getValueType().isIntegerType());
        Assert.assertFalse(msgpackValue.getValueType().isFloatType());
        Assert.assertFalse(msgpackValue.getValueType().isStringType());
        Assert.assertEquals(12345, msgpackValue.asIntegerValue().asInt());
    }

    @Test
    public void testExponentialInteger1() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("12345e3");
        Assert.assertTrue(msgpackValue.getValueType().isNumberType());
        // TODO: Consider this needs to be an integer?
        // See: https://github.com/embulk/embulk/issues/775
        Assert.assertTrue(msgpackValue.getValueType().isFloatType());
        Assert.assertFalse(msgpackValue.getValueType().isIntegerType());
        Assert.assertFalse(msgpackValue.getValueType().isStringType());
        Assert.assertEquals(1.2345E7, msgpackValue.asFloatValue().toDouble(), 1.0E-9);
        // Not sure this |toString| is to be tested...
        Assert.assertEquals("1.2345E7", msgpackValue.asFloatValue().toString());
    }

    @Test
    public void testExponentialInteger2() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("123e2");
        Assert.assertTrue(msgpackValue.getValueType().isNumberType());
        // TODO: Consider this needs to be an integer?
        // See: https://github.com/embulk/embulk/issues/775
        Assert.assertTrue(msgpackValue.getValueType().isFloatType());
        Assert.assertFalse(msgpackValue.getValueType().isIntegerType());
        Assert.assertFalse(msgpackValue.getValueType().isStringType());
        Assert.assertEquals(12300.0, msgpackValue.asFloatValue().toDouble(), 1.0E-9);
        // Not sure this |toString| is to be tested...
        Assert.assertEquals("12300.0", msgpackValue.asFloatValue().toString());
    }

    @Test
    public void testOrdinaryFloat() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("12345.12");
        Assert.assertTrue(msgpackValue.getValueType().isNumberType());
        Assert.assertTrue(msgpackValue.getValueType().isFloatType());
        Assert.assertFalse(msgpackValue.getValueType().isIntegerType());
        Assert.assertFalse(msgpackValue.getValueType().isStringType());
        Assert.assertEquals(12345.12, msgpackValue.asFloatValue().toDouble(), 1.0E-9);
        // Not sure this |toString| is to be tested...
        Assert.assertEquals("12345.12", msgpackValue.asFloatValue().toString());
    }

    @Test
    public void testExponentialFloat() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("1.234512E4");
        Assert.assertTrue(msgpackValue.getValueType().isNumberType());
        Assert.assertTrue(msgpackValue.getValueType().isFloatType());
        Assert.assertFalse(msgpackValue.getValueType().isIntegerType());
        Assert.assertFalse(msgpackValue.getValueType().isStringType());
        Assert.assertEquals(12345.12, msgpackValue.asFloatValue().toDouble(), 1.0E-9);
        // Not sure this |toString| is to be tested...
        Assert.assertEquals("12345.12", msgpackValue.asFloatValue().toString());
    }

    @Test
    public void testParseJson() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parse("{\"col1\": 1, \"col2\": \"foo\", \"col3\": [1,2,3], \"col4\": {\"a\": 1}}");
        Assert.assertTrue(msgpackValue.isMapValue());
        final Map<Value, Value> map = msgpackValue.asMapValue().map();
        Assert.assertEquals(1, map.get(TestJsonParser.key("col1")).asIntegerValue().asInt());
        Assert.assertEquals("foo", map.get(TestJsonParser.key("col2")).asStringValue().toString());
        // Check array value
        final Value col3Value = map.get(TestJsonParser.key("col3"));
        Assert.assertTrue(col3Value.isArrayValue());
        Assert.assertEquals(Arrays.asList(1, 2, 3), col3Value.asArrayValue().list().stream().map(( v) -> v.asIntegerValue().asInt()).collect(Collectors.toList()));
        // Check map value
        final Value col4Value = map.get(TestJsonParser.key("col4"));
        Assert.assertTrue(col4Value.isMapValue());
        final Value aOfCol4 = col4Value.asMapValue().map().get(TestJsonParser.key("a"));
        Assert.assertEquals(1, aOfCol4.asIntegerValue().asInt());
    }

    @Test
    public void testParseMultipleJsons() throws Exception {
        final JsonParser parser = new JsonParser();
        final String multipleJsons = "{\"col1\": 1}{\"col1\": 2}";
        try (JsonParser.Stream stream = parser.open(TestJsonParser.toInputStream(multipleJsons))) {
            Assert.assertEquals("{\"col1\":1}", stream.next().toJson());
            Assert.assertEquals("{\"col1\":2}", stream.next().toJson());
            Assert.assertNull(stream.next());
        }
    }

    @Test
    public void testParseWithPointer1() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parseWithOffsetInJsonPointer("{\"a\": {\"b\": 1}}", "/a/b");
        Assert.assertEquals(1, msgpackValue.asIntegerValue().asInt());
    }

    @Test
    public void testParseWithPointer2() throws Exception {
        final JsonParser parser = new JsonParser();
        final Value msgpackValue = parser.parseWithOffsetInJsonPointer("{\"a\": [{\"b\": 1}, {\"b\": 2}]}", "/a/1/b");
        Assert.assertEquals(2, msgpackValue.asIntegerValue().asInt());
    }

    @Test
    public void testParseMultipleJsonsWithPointer() throws Exception {
        final JsonParser parser = new JsonParser();
        final String multipleJsons = "{\"a\": {\"b\": 1}}{\"a\": {\"b\": 2}}";
        try (JsonParser.Stream stream = parser.openWithOffsetInJsonPointer(TestJsonParser.toInputStream(multipleJsons), "/a/b")) {
            Assert.assertEquals(1, stream.next().asIntegerValue().asInt());
            Assert.assertEquals(2, stream.next().asIntegerValue().asInt());
            Assert.assertNull(stream.next());
        }
    }
}

