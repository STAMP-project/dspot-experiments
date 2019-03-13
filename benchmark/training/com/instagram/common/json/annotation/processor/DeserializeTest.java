/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.instagram.common.json.annotation.processor;


import AlternateFieldUUT.ALTERNATE_FIELD_NAME_1;
import AlternateFieldUUT.ALTERNATE_FIELD_NAME_2;
import AlternateFieldUUT.FIELD_NAME;
import EnumUUT.EnumType;
import ExactMappingUUT.BOOLEAN_FIELD_NAME;
import ExactMappingUUT.BOOLEAN_OBJ_FIELD_NAME;
import ExactMappingUUT.DOUBLE_FIELD_NAME;
import ExactMappingUUT.DOUBLE_OBJ_FIELD_NAME;
import ExactMappingUUT.LONG_FIELD_NAME;
import ExactMappingUUT.LONG_OBJ_FIELD_NAME;
import FormatterUUT.FIELD_ASSIGNMENT_FIELD_NAME;
import FormatterUUT.VALUE_FORMATTER_FIELD_NAME;
import MapUUT.MapObject;
import MapUUT.STRING_STRING_MAP_FIELD_NAME;
import SimpleParseUUT.FLOAT_FIELD_NAME;
import SimpleParseUUT.FLOAT_OBJ_FIELD_NAME;
import SimpleParseUUT.INTEGER_FIELD_NAME;
import SimpleParseUUT.INTEGER_LIST_FIELD_NAME;
import SimpleParseUUT.INT_FIELD_NAME;
import SimpleParseUUT.STRING_FIELD_NAME;
import SimpleParseUUT.SubenumUUT;
import SimpleParseUUT.SubenumUUT.A;
import SimpleParseUUT.SubenumUUT.B;
import StrictListParseUUT.SUBOBJECT_LIST_FIELD_NAME;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.instagram.common.json.JsonHelper;
import com.instagram.common.json.annotation.processor.dependent.TypeFormatterImportsContainerUUT;
import com.instagram.common.json.annotation.processor.dependent.TypeFormatterImportsContainerUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.parent.TypeFormatterImportsCompanionUUT;
import com.instagram.common.json.annotation.processor.parent.TypeFormatterImportsUUT;
import com.instagram.common.json.annotation.processor.support.ExtensibleJSONWriter;
import com.instagram.common.json.annotation.processor.uut.CustomParseContainerUUT;
import com.instagram.common.json.annotation.processor.uut.CustomParseContainerUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.EnumUUT;
import com.instagram.common.json.annotation.processor.uut.EnumUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.FormatterUUT;
import com.instagram.common.json.annotation.processor.uut.FormatterUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.ImportsUUT;
import com.instagram.common.json.annotation.processor.uut.ImportsUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.MapUUT;
import com.instagram.common.json.annotation.processor.uut.MapUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.PostprocessingUUT;
import com.instagram.common.json.annotation.processor.uut.PostprocessingUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.SimpleParseUUT;
import com.instagram.common.json.annotation.processor.uut.SimpleParseUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.StrictListParseUUT;
import com.instagram.common.json.annotation.processor.uut.StrictListParseUUT__JsonHelper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONWriter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic deserialization tests.
 */
public class DeserializeTest {
    @Test
    public void simpleDeserializeTest() throws IOException, JSONException {
        final int intValue = 25;
        final int integerValue = 37;
        final float floatValue = 1.0F;
        final float floatObjValue = 2.0F;
        final String stringValue = "hello world\r\n\'\"";
        final List<Integer> integerList = Lists.newArrayList(1, 2, 3, 4);
        final ArrayList<Integer> integerArrayList = Lists.newArrayList(1, 2, 3, 4);
        final Queue<Integer> integerQueue = Queues.newArrayDeque(Arrays.asList(1, 2, 3, 4));
        final Set<Integer> integerSet = Sets.newHashSet(1, 2, 3, 4);
        final int subIntValue = 30;
        final SimpleParseUUT.SubenumUUT subEnum = SubenumUUT.A;
        final List<SimpleParseUUT.SubenumUUT> subEnumList = Lists.newArrayList(A, B);
        StringWriter stringWriter = new StringWriter();
        ExtensibleJSONWriter writer = new ExtensibleJSONWriter(stringWriter);
        writer.object().key(INT_FIELD_NAME).value(intValue).key(INTEGER_FIELD_NAME).value(integerValue).key(FLOAT_FIELD_NAME).value(floatValue).key(FLOAT_OBJ_FIELD_NAME).value(floatObjValue).key(STRING_FIELD_NAME).value(stringValue).key(INTEGER_LIST_FIELD_NAME).array().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Integer integer : integerList) {
                    writer.value(integer);
                }
            }
        }).endArray().key(SimpleParseUUT.INTEGER_ARRAY_LIST_FIELD_NAME).array().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Integer integer : integerList) {
                    writer.value(integer);
                }
            }
        }).endArray().key(SimpleParseUUT.INTEGER_QUEUE_FIELD_NAME).array().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Integer integer : integerQueue) {
                    writer.value(integer);
                }
            }
        }).endArray().key(SimpleParseUUT.INTEGER_SET_FIELD_NAME).array().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Integer integer : integerSet) {
                    writer.value(integer);
                }
            }
        }).endArray().key(SimpleParseUUT.SUBOBJECT_FIELD_NAME).object().key(SimpleParseUUT.SubobjectParseUUT.INT_FIELD_NAME).value(subIntValue).endObject().key(SimpleParseUUT.SUBENUM_FIELD_NAME).value(subEnum.toString()).key(SimpleParseUUT.SUBENUM_LIST_FIELD_NAME).array().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (SimpleParseUUT.SubenumUUT enumValue : subEnumList) {
                    writer.value(enumValue.toString());
                }
            }
        }).endArray().endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        SimpleParseUUT uut = SimpleParseUUT__JsonHelper.parseFromJson(jp);
        Assert.assertTrue(((new SimpleParseUUT__JsonHelper()) instanceof JsonHelper));
        Assert.assertSame(intValue, uut.intField);
        Assert.assertSame(integerValue, uut.integerField.intValue());
        Assert.assertEquals(Float.valueOf(floatValue), Float.valueOf(uut.floatField));
        Assert.assertEquals(Float.valueOf(floatObjValue), uut.FloatField);
        Assert.assertEquals(stringValue, uut.stringField);
        Assert.assertEquals(integerList, uut.integerListField);
        Assert.assertEquals(integerArrayList, uut.integerArrayListField);
        // NOTE: this is because ArrayDeque hilariously does not implement .equals()/.hashcode().
        Assert.assertEquals(Lists.newArrayList(integerQueue), Lists.newArrayList(uut.integerQueueField));
        Assert.assertEquals(integerSet, uut.integerSetField);
        Assert.assertSame(subIntValue, uut.subobjectField.intField);
        Assert.assertSame(subEnum, uut.subenumField);
        Assert.assertEquals(subEnumList, uut.subenumFieldList);
    }

    @Test
    public void valueExtractTest() throws IOException, JSONException {
        final int encodedValue = 25;
        final int deserializedValue = 40;
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        writer.object().key(VALUE_FORMATTER_FIELD_NAME).value(encodedValue).endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        FormatterUUT uut = FormatterUUT__JsonHelper.parseFromJson(jp);
        Assert.assertSame(deserializedValue, uut.getValueFormatter());
    }

    @Test
    public void importsTest() throws IOException, JSONException {
        final String encodedValue = "test";
        final String deserializedValue = ":test";
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        writer.object().key("string_field").value(encodedValue).endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        ImportsUUT uut = ImportsUUT__JsonHelper.parseFromJson(jp);
        Assert.assertEquals(deserializedValue, uut.mStringField);
    }

    @Test
    public void TypeFormatterImportsTest() throws IOException, JSONException {
        final String encodedValue = "test";
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        writer.object().key("callee_ref").object().key("string_field").value(encodedValue).endObject().endObject();
        String inputString = stringWriter.toString();
        TypeFormatterImportsContainerUUT container = TypeFormatterImportsContainerUUT__JsonHelper.parseFromJson(inputString);
        TypeFormatterImportsUUT uut = container.mTypeFormatterImports;
        Assert.assertTrue((uut instanceof TypeFormatterImportsCompanionUUT));
        Assert.assertEquals(encodedValue, uut.mString);
    }

    @Test
    public void fieldAssignmentTest() throws IOException, JSONException {
        final int encodedValue = 25;
        final int deserializedValue = -encodedValue;
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        writer.object().key(FIELD_ASSIGNMENT_FIELD_NAME).value(encodedValue).endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        FormatterUUT uut = FormatterUUT__JsonHelper.parseFromJson(jp);
        Assert.assertSame(deserializedValue, uut.getFieldAssignmentFormatter());
    }

    @Test
    public void enumTest() throws IOException, JSONException {
        final EnumUUT.EnumType value = EnumType.VALUE2;
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        writer.object().key(EnumUUT.ENUM_FIELD_NAME).value(value.toString()).endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        EnumUUT uut = EnumUUT__JsonHelper.parseFromJson(jp);
        Assert.assertSame(value, uut.enumField);
    }

    @Test
    public void exactMappingTest() throws IOException, JSONException {
        // boolean exact fail.  should throw exception.
        try {
            DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
                @Override
                public void extend(ExtensibleJSONWriter writer) throws JSONException {
                    writer.key(BOOLEAN_FIELD_NAME).value(15);
                }
            });
            Assert.fail("primitive type exact mismatches should throw exception");
        } catch (JsonParseException ex) {
            // this is expected.
        }
        // Boolean exact fail.  should be null.
        Assert.assertNull(DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                writer.key(BOOLEAN_OBJ_FIELD_NAME).value(15);
            }
        }).BooleanField);
        // int exact fail.  should throw exception.
        try {
            DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
                @Override
                public void extend(ExtensibleJSONWriter writer) throws JSONException {
                    writer.key(ExactMappingUUT.INT_FIELD_NAME).value(false);
                }
            });
            Assert.fail("primitive type exact mismatches should throw exception");
        } catch (JsonParseException ex) {
            // this is expected.
        }
        // Integer exact fail.  should be null.
        Assert.assertNull(DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                writer.key(ExactMappingUUT.INTEGER_FIELD_NAME).value(false);
            }
        }).IntegerField);
        // long exact fail.  should throw exception.
        try {
            DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
                @Override
                public void extend(ExtensibleJSONWriter writer) throws JSONException {
                    writer.key(LONG_FIELD_NAME).value("abc");
                }
            });
            Assert.fail("primitive type exact mismatches should throw exception");
        } catch (JsonParseException ex) {
            // this is expected.
        }
        // Long exact fail.  should be null.
        Assert.assertNull(DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                writer.key(LONG_OBJ_FIELD_NAME).value("abc");
            }
        }).LongField);
        // float exact fail.  should throw exception.
        try {
            DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
                @Override
                public void extend(ExtensibleJSONWriter writer) throws JSONException {
                    writer.key(ExactMappingUUT.FLOAT_FIELD_NAME).value("abc");
                }
            });
            Assert.fail("primitive type exact mismatches should throw exception");
        } catch (JsonParseException ex) {
            // this is expected.
        }
        // Float exact fail.  should be null.
        Assert.assertNull(DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                writer.key(ExactMappingUUT.FLOAT_OBJ_FIELD_NAME).value("abc");
            }
        }).FloatField);
        // double exact fail.  should throw exception.
        try {
            DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
                @Override
                public void extend(ExtensibleJSONWriter writer) throws JSONException {
                    writer.key(DOUBLE_FIELD_NAME).value("abc");
                }
            });
            Assert.fail("primitive type exact mismatches should throw exception");
        } catch (JsonParseException ex) {
            // this is expected.
        }
        // Double exact fail.  should be null.
        Assert.assertNull(DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                writer.key(DOUBLE_OBJ_FIELD_NAME).value("abc");
            }
        }).DoubleField);
        // string exact fail.  should be null.
        Assert.assertNull(DeserializeTest.parseObjectFromContents(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                writer.key(ExactMappingUUT.STRING_FIELD_NAME).value(15);
            }
        }).StringField);
    }

    @Test
    public void postprocessTest() throws IOException, JSONException {
        final int value = 25;
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        writer.object().key(PostprocessingUUT.FIELD_NAME).value(value).endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        PostprocessingUUT uut = PostprocessingUUT__JsonHelper.parseFromJson(jp);
        Assert.assertSame((value + 1), uut.getValue());
    }

    @Test
    public void malformedArrayEntry() throws IOException, JSONException {
        final List<Integer> integerList = Lists.newArrayList(1, null, 3, 4);
        final int subIntValue = 30;
        StringWriter stringWriter = new StringWriter();
        ExtensibleJSONWriter writer = new ExtensibleJSONWriter(stringWriter);
        writer.object().key(StrictListParseUUT.INTEGER_LIST_FIELD_NAME).array().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Integer integer : integerList) {
                    writer.value(integer);
                }
            }
        }).endArray().key(SUBOBJECT_LIST_FIELD_NAME).object().key(StrictListParseUUT.SubobjectParseUUT.INT_FIELD_NAME).value(subIntValue).endObject().endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        StrictListParseUUT uut = StrictListParseUUT__JsonHelper.parseFromJson(jp);
        Assert.assertEquals(3, uut.integerListField.size());
        Assert.assertEquals(1, uut.integerListField.get(0).intValue());
        Assert.assertEquals(3, uut.integerListField.get(1).intValue());
        Assert.assertEquals(4, uut.integerListField.get(2).intValue());
    }

    @Test
    public void testAlternateFieldNames() throws Exception {
        testAlternateFieldNameHelper(FIELD_NAME, "value1");
        testAlternateFieldNameHelper(ALTERNATE_FIELD_NAME_1, "value2");
        testAlternateFieldNameHelper(ALTERNATE_FIELD_NAME_2, "value3");
    }

    @Test
    public void nullString() throws IOException, JSONException {
        StringWriter stringWriter = new StringWriter();
        ExtensibleJSONWriter writer = new ExtensibleJSONWriter(stringWriter);
        writer.object().key(STRING_FIELD_NAME).value(null).endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        SimpleParseUUT uut = SimpleParseUUT__JsonHelper.parseFromJson(jp);
        Assert.assertNull(uut.stringField);
    }

    @Test
    public void testCustomParse() throws Exception {
        String value = "hey there";
        StringWriter stringWriter = new StringWriter();
        ExtensibleJSONWriter writer = new ExtensibleJSONWriter(stringWriter);
        writer.object().key(CustomParseContainerUUT.INNER_FIELD_NAME).object().key(CustomParseContainerUUT.CustomParseUUT.STRING_FIELD_NAME).value(value).endObject().endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        CustomParseContainerUUT uut = CustomParseContainerUUT__JsonHelper.parseFromJson(jp);
        Assert.assertEquals(value, uut.getCustomParseUUT().getStringField());
    }

    @Test
    public void testMap() throws Exception {
        final String stringValue = "hello world\r\n\'\"";
        final int integerValue = 37;
        final Map<String, Integer> stringIntegerMap = Maps.newHashMap();
        stringIntegerMap.put(stringValue, integerValue);
        final Map<String, String> stringStringMap = Maps.newHashMap();
        stringStringMap.put(stringValue, "value");
        final Map<String, Long> stringLongMap = Maps.newHashMap();
        stringLongMap.put("key_min_value", Long.MIN_VALUE);
        stringLongMap.put("key_max_value", Long.MAX_VALUE);
        final Map<String, Double> stringDoubleMap = Maps.newHashMap();
        stringDoubleMap.put("key_min_value", Double.MIN_VALUE);
        stringDoubleMap.put("key_max_value", Double.MAX_VALUE);
        final Map<String, Float> stringFloatMap = Maps.newHashMap();
        stringFloatMap.put("key_min_value", Float.MIN_VALUE);
        stringFloatMap.put("key_max_value", Float.MAX_VALUE);
        final Map<String, Boolean> stringBooleanMap = Maps.newHashMap();
        stringBooleanMap.put("true", Boolean.TRUE);
        stringBooleanMap.put("false", Boolean.FALSE);
        final Map<String, MapUUT.MapObject> stringObjectMap = Maps.newHashMap();
        MapUUT.MapObject mapObject = new MapUUT.MapObject();
        mapObject.subclassInt = integerValue;
        final HashMap<String, String> stringStringHashMap = Maps.newHashMap();
        stringStringHashMap.put(stringValue, "value");
        StringWriter stringWriter = new StringWriter();
        ExtensibleJSONWriter writer = new ExtensibleJSONWriter(stringWriter);
        writer.object().key(MapUUT.STRING_INTEGER_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
                    writer.key(entry.getKey());
                    writer.value(entry.getValue());
                }
            }
        }).endObject().key(STRING_STRING_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                    writer.key(entry.getKey());
                    writer.value(entry.getValue());
                }
            }
        }).endObject().key(MapUUT.STRING_LONG_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, Long> entry : stringLongMap.entrySet()) {
                    writer.key(entry.getKey());
                    writer.value(entry.getValue());
                }
            }
        }).endObject().key(MapUUT.STRING_DOUBLE_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, Double> entry : stringDoubleMap.entrySet()) {
                    writer.key(entry.getKey());
                    writer.value(entry.getValue());
                }
            }
        }).endObject().key(MapUUT.STRING_FLOAT_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, Float> entry : stringFloatMap.entrySet()) {
                    writer.key(entry.getKey());
                    writer.value(entry.getValue());
                }
            }
        }).endObject().key(MapUUT.STRING_BOOLEAN_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, Boolean> entry : stringBooleanMap.entrySet()) {
                    writer.key(entry.getKey());
                    writer.value(entry.getValue());
                }
            }
        }).endObject().key(MapUUT.STRING_OBJECT_MAP_FIELD_NAME).object().extend(new ExtensibleJSONWriter.Extender() {
            @Override
            public void extend(ExtensibleJSONWriter writer) throws JSONException {
                for (Map.Entry<String, MapUUT.MapObject> entry : stringObjectMap.entrySet()) {
                    writer.key(entry.getKey()).object().key(MapUUT.MapObject.INT_KEY).value(entry.getValue().subclassInt).endObject();
                }
            }
        }).endObject().endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        MapUUT uut = MapUUT__JsonHelper.parseFromJson(jp);
        Assert.assertEquals(stringIntegerMap, uut.stringIntegerMapField);
        Assert.assertEquals(stringStringMap, uut.stringStringMapField);
        Assert.assertEquals(stringLongMap, uut.stringLongMapField);
        Assert.assertEquals(stringDoubleMap, uut.stringDoubleMapField);
        Assert.assertEquals(stringFloatMap, uut.stringFloatMapField);
        Assert.assertEquals(stringBooleanMap, uut.stringBooleanMapField);
        Assert.assertEquals(stringObjectMap, uut.stringObjectMapField);
    }

    @Test
    public void testMapNullValue() throws Exception {
        StringWriter stringWriter = new StringWriter();
        ExtensibleJSONWriter writer = new ExtensibleJSONWriter(stringWriter);
        final String keyWithNullValue = "key_with_null_value";
        writer.object().key(STRING_STRING_MAP_FIELD_NAME).object().key(keyWithNullValue).value(null).endObject().endObject();
        String inputString = stringWriter.toString();
        JsonParser jp = new JsonFactory().createParser(inputString);
        jp.nextToken();
        MapUUT uut = MapUUT__JsonHelper.parseFromJson(jp);
        Assert.assertTrue(uut.stringStringMapField.containsKey(keyWithNullValue));
        Assert.assertNull(uut.stringStringMapField.get(keyWithNullValue));
    }
}

