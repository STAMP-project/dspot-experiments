/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.unit;


import AvroConverter.JsonNodeAndSize;
import Schema.Parser;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.converters.AvroConverter;
import io.confluent.kafkarest.converters.ConversionException;
import io.confluent.kafkarest.entities.EntityUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;


public class AvroConverterTest {
    private static final Parser parser = new Schema.Parser();

    private static final Schema recordSchema = new Schema.Parser().parse(("{\"namespace\": \"namespace\",\n" + (((((((((((((((((((((" \"type\": \"record\",\n" + " \"name\": \"test\",\n") + " \"fields\": [\n") + "     {\"name\": \"null\", \"type\": \"null\"},\n") + "     {\"name\": \"boolean\", \"type\": \"boolean\"},\n") + "     {\"name\": \"int\", \"type\": \"int\"},\n") + "     {\"name\": \"long\", \"type\": \"long\"},\n") + "     {\"name\": \"float\", \"type\": \"float\"},\n") + "     {\"name\": \"double\", \"type\": \"double\"},\n") + "     {\"name\": \"bytes\", \"type\": \"bytes\"},\n") + "     {\"name\": \"string\", \"type\": \"string\", \"aliases\": [\"string_alias\"]},\n") + "     {\"name\": \"null_default\", \"type\": \"null\", \"default\": null},\n") + "     {\"name\": \"boolean_default\", \"type\": \"boolean\", \"default\": false},\n") + "     {\"name\": \"int_default\", \"type\": \"int\", \"default\": 24},\n") + "     {\"name\": \"long_default\", \"type\": \"long\", \"default\": 4000000000},\n") + "     {\"name\": \"float_default\", \"type\": \"float\", \"default\": 12.3},\n") + "     {\"name\": \"double_default\", \"type\": \"double\", \"default\": 23.2},\n") + "     {\"name\": \"bytes_default\", \"type\": \"bytes\", \"default\": \"bytes\"},\n") + "     {\"name\": \"string_default\", \"type\": \"string\", \"default\": ") + "\"default string\"}\n") + "]\n") + "}")));

    private static final Schema arraySchema = new Schema.Parser().parse(("{\"namespace\": \"namespace\",\n" + (((" \"type\": \"array\",\n" + " \"name\": \"test\",\n") + " \"items\": \"string\"\n") + "}")));

    private static final Schema mapSchema = new Schema.Parser().parse(("{\"namespace\": \"namespace\",\n" + (((" \"type\": \"map\",\n" + " \"name\": \"test\",\n") + " \"values\": \"string\"\n") + "}")));

    private static final Schema unionSchema = new Schema.Parser().parse(("{\"type\": \"record\",\n" + (((" \"name\": \"test\",\n" + " \"fields\": [\n") + "     {\"name\": \"union\", \"type\": [\"string\", \"int\"]}\n") + "]}")));

    private static final Schema enumSchema = new Schema.Parser().parse(("{ \"type\": \"enum\",\n" + (("  \"name\": \"Suit\",\n" + "  \"symbols\" : [\"SPADES\", \"HEARTS\", \"DIAMONDS\", \"CLUBS\"]\n") + "}")));

    @Test
    public void testPrimitiveTypesToAvro() {
        Object result = AvroConverter.toAvro(null, AvroConverterTest.createPrimitiveSchema("null"));
        Assert.assertTrue((result == null));
        result = AvroConverter.toAvro(TestUtils.jsonTree("true"), AvroConverterTest.createPrimitiveSchema("boolean"));
        Assert.assertEquals(true, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("boolean"));
        Assert.assertEquals(false, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("12"), AvroConverterTest.createPrimitiveSchema("int"));
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(12, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("12"), AvroConverterTest.createPrimitiveSchema("long"));
        Assert.assertTrue((result instanceof Long));
        Assert.assertEquals(12L, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("5000000000"), AvroConverterTest.createPrimitiveSchema("long"));
        Assert.assertTrue((result instanceof Long));
        Assert.assertEquals(5000000000L, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("23.2"), AvroConverterTest.createPrimitiveSchema("float"));
        Assert.assertTrue((result instanceof Float));
        Assert.assertEquals(23.2F, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("23"), AvroConverterTest.createPrimitiveSchema("float"));
        Assert.assertTrue((result instanceof Float));
        Assert.assertEquals(23.0F, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("23.2"), AvroConverterTest.createPrimitiveSchema("double"));
        Assert.assertTrue((result instanceof Double));
        Assert.assertEquals(23.2, result);
        result = AvroConverter.toAvro(TestUtils.jsonTree("23"), AvroConverterTest.createPrimitiveSchema("double"));
        Assert.assertTrue((result instanceof Double));
        Assert.assertEquals(23.0, result);
        // We can test bytes simply using simple ASCII string since the translation is direct in that
        // case
        result = AvroConverter.toAvro(new TextNode("hello"), AvroConverterTest.createPrimitiveSchema("bytes"));
        Assert.assertTrue((result instanceof ByteBuffer));
        Assert.assertEquals(EntityUtils.encodeBase64Binary("hello".getBytes()), EntityUtils.encodeBase64Binary(((ByteBuffer) (result)).array()));
        result = AvroConverter.toAvro(TestUtils.jsonTree("\"a string\""), AvroConverterTest.createPrimitiveSchema("string"));
        Assert.assertTrue((result instanceof Utf8));
        Assert.assertEquals(new Utf8("a string"), result);
    }

    @Test
    public void testPrimitiveTypeToAvroSchemaMismatches() {
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("12"), AvroConverterTest.createPrimitiveSchema("null"));
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("12"), AvroConverterTest.createPrimitiveSchema("boolean"));
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("int"));
        // Note that we don't test real numbers => int because JsonDecoder permits this and removes
        // the decimal part
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("5000000000"), AvroConverterTest.createPrimitiveSchema("int"));
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("long"));
        // Note that we don't test real numbers => long because JsonDecoder permits this and removes
        // the decimal part
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("float"));
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("double"));
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("bytes"));
        AvroConverterTest.expectConversionException(TestUtils.jsonTree("false"), AvroConverterTest.createPrimitiveSchema("string"));
    }

    @Test
    public void testRecordToAvro() {
        String json = "{\n" + (((((((((((((((("    \"null\": null,\n" + "    \"boolean\": true,\n") + "    \"int\": 12,\n") + "    \"long\": 5000000000,\n") + "    \"float\": 23.4,\n") + "    \"double\": 800.25,\n") + "    \"bytes\": \"hello\",\n") + "    \"string\": \"string\",\n") + "    \"null_default\": null,\n") + "    \"boolean_default\": false,\n") + "    \"int_default\": 24,\n") + "    \"long_default\": 4000000000,\n") + "    \"float_default\": 12.3,\n") + "    \"double_default\": 23.2,\n") + "    \"bytes_default\": \"bytes\",\n") + "    \"string_default\": \"default\"\n") + "}");
        Object result = AvroConverter.toAvro(TestUtils.jsonTree(json), AvroConverterTest.recordSchema);
        Assert.assertTrue((result instanceof GenericRecord));
        GenericRecord resultRecord = ((GenericRecord) (result));
        Assert.assertEquals(null, resultRecord.get("null"));
        Assert.assertEquals(true, resultRecord.get("boolean"));
        Assert.assertEquals(12, resultRecord.get("int"));
        Assert.assertEquals(5000000000L, resultRecord.get("long"));
        Assert.assertEquals(23.4F, resultRecord.get("float"));
        Assert.assertEquals(800.25, resultRecord.get("double"));
        Assert.assertEquals(EntityUtils.encodeBase64Binary("hello".getBytes()), EntityUtils.encodeBase64Binary(((ByteBuffer) (resultRecord.get("bytes"))).array()));
        Assert.assertEquals("string", resultRecord.get("string").toString());
        // Nothing to check with default values, just want to make sure an exception wasn't thrown
        // when they values weren't specified for their fields.
    }

    @Test
    public void testArrayToAvro() {
        String json = "[\"one\", \"two\", \"three\"]";
        Object result = AvroConverter.toAvro(TestUtils.jsonTree(json), AvroConverterTest.arraySchema);
        Assert.assertTrue((result instanceof GenericArray));
        Assert.assertArrayEquals(new Utf8[]{ new Utf8("one"), new Utf8("two"), new Utf8("three") }, toArray());
    }

    @Test
    public void testMapToAvro() {
        String json = "{\"first\": \"one\", \"second\": \"two\"}";
        Object result = AvroConverter.toAvro(TestUtils.jsonTree(json), AvroConverterTest.mapSchema);
        Assert.assertTrue((result instanceof Map));
        Assert.assertEquals(2, ((Map<String, Object>) (result)).size());
    }

    @Test
    public void testUnionToAvro() {
        Object result = AvroConverter.toAvro(TestUtils.jsonTree("{\"union\":{\"string\":\"test string\"}}"), AvroConverterTest.unionSchema);
        Object foo = get("union");
        Assert.assertTrue(((((GenericRecord) (result)).get("union")) instanceof Utf8));
        result = AvroConverter.toAvro(TestUtils.jsonTree("{\"union\":{\"int\":12}}"), AvroConverterTest.unionSchema);
        Assert.assertTrue(((((GenericRecord) (result)).get("union")) instanceof Integer));
        try {
            AvroConverter.toAvro(TestUtils.jsonTree("12.4"), AvroConverterTest.unionSchema);
            Assert.fail("Trying to convert floating point number to union(string,int) schema should fail");
        } catch (ConversionException e) {
            // expected
        }
    }

    @Test
    public void testEnumToAvro() {
        Object result = AvroConverter.toAvro(TestUtils.jsonTree("\"SPADES\""), AvroConverterTest.enumSchema);
        Assert.assertTrue((result instanceof GenericEnumSymbol));
        // There's no failure case here because the only failure mode is passing in non-string data.
        // Even if they put in an invalid symbol name, the exception won't be thrown until
        // serialization.
    }

    @Test
    public void testPrimitiveTypesToJson() {
        AvroConverter.JsonNodeAndSize result = AvroConverter.toJson(((int) (0)));
        Assert.assertTrue(result.json.isNumber());
        Assert.assertTrue(((result.size) > 0));
        result = AvroConverter.toJson(((long) (0)));
        Assert.assertTrue(result.json.isNumber());
        result = AvroConverter.toJson(0.1F);
        Assert.assertTrue(result.json.isNumber());
        result = AvroConverter.toJson(0.1);
        Assert.assertTrue(result.json.isNumber());
        result = AvroConverter.toJson(true);
        Assert.assertTrue(result.json.isBoolean());
        // "Primitive" here refers to Avro primitive types, which are returned as standalone objects,
        // which can't have attached schemas. This includes, for example, Strings and byte[] even
        // though they are not Java primitives
        result = AvroConverter.toJson("abcdefg");
        Assert.assertTrue(result.json.isTextual());
        Assert.assertEquals("abcdefg", result.json.textValue());
        result = AvroConverter.toJson(ByteBuffer.wrap("hello".getBytes()));
        Assert.assertTrue(result.json.isTextual());
        // Was generated from a string, so the Avro encoding should be equivalent to the string
        Assert.assertEquals("hello", result.json.textValue());
    }

    @Test
    public void testUnsupportedJavaPrimitivesToJson() {
        AvroConverterTest.expectConversionException(((byte) (0)));
        AvroConverterTest.expectConversionException(((char) (0)));
        AvroConverterTest.expectConversionException(((short) (0)));
    }

    @Test
    public void testRecordToJson() {
        GenericRecord data = set("null", null).set("boolean", true).set("int", 12).set("long", 5000000000L).set("float", 23.4F).set("double", 800.25).set("bytes", ByteBuffer.wrap("bytes".getBytes())).set("string", "string").build();
        AvroConverter.JsonNodeAndSize result = AvroConverter.toJson(data);
        Assert.assertTrue(((result.size) > 0));
        Assert.assertTrue(result.json.isObject());
        Assert.assertTrue(result.json.get("null").isNull());
        Assert.assertTrue(result.json.get("boolean").isBoolean());
        Assert.assertEquals(true, result.json.get("boolean").booleanValue());
        Assert.assertTrue(result.json.get("int").isIntegralNumber());
        Assert.assertEquals(12, result.json.get("int").intValue());
        Assert.assertTrue(result.json.get("long").isIntegralNumber());
        Assert.assertEquals(5000000000L, result.json.get("long").longValue());
        Assert.assertTrue(result.json.get("float").isFloatingPointNumber());
        Assert.assertEquals(23.4F, result.json.get("float").floatValue(), 0.1);
        Assert.assertTrue(result.json.get("double").isFloatingPointNumber());
        Assert.assertEquals(800.25, result.json.get("double").doubleValue(), 0.01);
        Assert.assertTrue(result.json.get("bytes").isTextual());
        // The bytes value was created from an ASCII string, so Avro's encoding should just give that
        // string back to us in the JSON-serialized version
        Assert.assertEquals("bytes", result.json.get("bytes").textValue());
        Assert.assertTrue(result.json.get("string").isTextual());
        Assert.assertEquals("string", result.json.get("string").textValue());
    }

    @Test
    public void testArrayToJson() {
        GenericData.Array<String> data = new GenericData.Array(AvroConverterTest.arraySchema, Arrays.asList("one", "two", "three"));
        AvroConverter.JsonNodeAndSize result = AvroConverter.toJson(data);
        Assert.assertTrue(((result.size) > 0));
        Assert.assertTrue(result.json.isArray());
        Assert.assertEquals(3, result.json.size());
        Assert.assertEquals(JsonNodeFactory.instance.textNode("one"), result.json.get(0));
        Assert.assertEquals(JsonNodeFactory.instance.textNode("two"), result.json.get(1));
        Assert.assertEquals(JsonNodeFactory.instance.textNode("three"), result.json.get(2));
    }

    @Test
    public void testMapToJson() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("first", "one");
        data.put("second", "two");
        AvroConverter.JsonNodeAndSize result = AvroConverter.toJson(data);
        Assert.assertTrue(((result.size) > 0));
        Assert.assertTrue(result.json.isObject());
        Assert.assertEquals(2, result.json.size());
        Assert.assertNotNull(result.json.get("first"));
        Assert.assertEquals("one", result.json.get("first").asText());
        Assert.assertNotNull(result.json.get("second"));
        Assert.assertEquals("two", result.json.get("second").asText());
    }

    @Test
    public void testEnumToJson() {
        AvroConverter.JsonNodeAndSize result = AvroConverter.toJson(new GenericData.EnumSymbol(AvroConverterTest.enumSchema, "SPADES"));
        Assert.assertTrue(((result.size) > 0));
        Assert.assertTrue(result.json.isTextual());
        Assert.assertEquals("SPADES", result.json.textValue());
    }
}

