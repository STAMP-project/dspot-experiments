/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import ByteString.EMPTY;
import TestMap.Builder;
import TestMap.EnumValue;
import TestMap.EnumValue.BAR;
import TestMap.EnumValue.FOO;
import TestMap.EnumValue.UNRECOGNIZED;
import TestMap.INT32_TO_MESSAGE_FIELD_FIELD_NUMBER;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import map_test.MapTestProto.BizarroTestMap;
import map_test.MapTestProto.ReservedAsMapField;
import map_test.MapTestProto.ReservedAsMapFieldWithEnumValue;
import map_test.MapTestProto.TestMap;
import map_test.MapTestProto.TestMap.MessageValue;
import map_test.MapTestProto.TestOnChangeEventPropagation;
import org.junit.Assert;


/**
 * Unit tests for map fields.
 */
public class MapTest extends TestCase {
    public void testSetMapValues() {
        TestMap.Builder usingMutableMapBuilder = TestMap.newBuilder();
        setMapValuesUsingMutableMap(usingMutableMapBuilder);
        TestMap usingMutableMap = usingMutableMapBuilder.build();
        assertMapValuesSet(usingMutableMap);
        TestMap.Builder usingAccessorsBuilder = TestMap.newBuilder();
        setMapValuesUsingAccessors(usingAccessorsBuilder);
        TestMap usingAccessors = usingAccessorsBuilder.build();
        assertMapValuesSet(usingAccessors);
        TestCase.assertEquals(usingAccessors, usingMutableMap);
    }

    public void testUpdateMapValues() {
        TestMap.Builder usingMutableMapBuilder = TestMap.newBuilder();
        setMapValuesUsingMutableMap(usingMutableMapBuilder);
        TestMap usingMutableMap = usingMutableMapBuilder.build();
        assertMapValuesSet(usingMutableMap);
        TestMap.Builder usingAccessorsBuilder = TestMap.newBuilder();
        setMapValuesUsingAccessors(usingAccessorsBuilder);
        TestMap usingAccessors = usingAccessorsBuilder.build();
        assertMapValuesSet(usingAccessors);
        TestCase.assertEquals(usingAccessors, usingMutableMap);
        // 
        usingMutableMapBuilder = usingMutableMap.toBuilder();
        updateMapValuesUsingMutableMap(usingMutableMapBuilder);
        usingMutableMap = usingMutableMapBuilder.build();
        assertMapValuesUpdated(usingMutableMap);
        usingAccessorsBuilder = usingAccessors.toBuilder();
        updateMapValuesUsingAccessors(usingAccessorsBuilder);
        usingAccessors = usingAccessorsBuilder.build();
        assertMapValuesUpdated(usingAccessors);
        TestCase.assertEquals(usingAccessors, usingMutableMap);
    }

    public void testGetMapIsImmutable() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapsAreImmutable(builder);
        assertMapsAreImmutable(builder.build());
        setMapValuesUsingAccessors(builder);
        assertMapsAreImmutable(builder);
        assertMapsAreImmutable(builder.build());
    }

    public void testMutableMapLifecycle() {
        TestMap.Builder builder = TestMap.newBuilder();
        Map<Integer, Integer> intMap = builder.getMutableInt32ToInt32Field();
        intMap.put(1, 2);
        TestCase.assertEquals(MapTest.newMap(1, 2), builder.build().getInt32ToInt32Field());
        try {
            intMap.put(2, 3);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapTest.newMap(1, 2), builder.getInt32ToInt32Field());
        builder.getMutableInt32ToInt32Field().put(2, 3);
        TestCase.assertEquals(MapTest.newMap(1, 2, 2, 3), builder.getInt32ToInt32Field());
        // 
        Map<Integer, TestMap.EnumValue> enumMap = builder.getMutableInt32ToEnumField();
        enumMap.put(1, BAR);
        TestCase.assertEquals(MapTest.newMap(1, BAR), builder.build().getInt32ToEnumField());
        try {
            enumMap.put(2, FOO);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapTest.newMap(1, BAR), builder.getInt32ToEnumField());
        builder.getMutableInt32ToEnumField().put(2, FOO);
        TestCase.assertEquals(MapTest.newMap(1, BAR, 2, FOO), builder.getInt32ToEnumField());
        // 
        Map<Integer, String> stringMap = builder.getMutableInt32ToStringField();
        stringMap.put(1, "1");
        TestCase.assertEquals(MapTest.newMap(1, "1"), builder.build().getInt32ToStringField());
        try {
            stringMap.put(2, "2");
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapTest.newMap(1, "1"), builder.getInt32ToStringField());
        builder.putInt32ToStringField(2, "2");
        TestCase.assertEquals(MapTest.newMap(1, "1", 2, "2"), builder.getInt32ToStringField());
        // 
        Map<Integer, TestMap.MessageValue> messageMap = builder.getMutableInt32ToMessageField();
        messageMap.put(1, TestMap.MessageValue.getDefaultInstance());
        TestCase.assertEquals(MapTest.newMap(1, TestMap.MessageValue.getDefaultInstance()), builder.build().getInt32ToMessageField());
        try {
            messageMap.put(2, TestMap.MessageValue.getDefaultInstance());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapTest.newMap(1, TestMap.MessageValue.getDefaultInstance()), builder.getInt32ToMessageField());
        builder.putInt32ToMessageField(2, TestMap.MessageValue.getDefaultInstance());
        TestCase.assertEquals(MapTest.newMap(1, TestMap.MessageValue.getDefaultInstance(), 2, TestMap.MessageValue.getDefaultInstance()), builder.getInt32ToMessageField());
    }

    // 
    public void testMutableMapLifecycle_collections() {
        TestMap.Builder builder = TestMap.newBuilder();
        Map<Integer, Integer> intMap = builder.getMutableInt32ToInt32Field();
        intMap.put(1, 2);
        TestCase.assertEquals(MapTest.newMap(1, 2), builder.build().getInt32ToInt32Field());
        try {
            intMap.remove(2);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            intMap.entrySet().remove(new Object());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            intMap.entrySet().iterator().remove();
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            intMap.keySet().remove(new Object());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            intMap.values().remove(new Object());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            intMap.values().iterator().remove();
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapTest.newMap(1, 2), intMap);
        TestCase.assertEquals(MapTest.newMap(1, 2), builder.getInt32ToInt32Field());
        TestCase.assertEquals(MapTest.newMap(1, 2), builder.build().getInt32ToInt32Field());
    }

    public void testGettersAndSetters() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        TestMap message = builder.build();
        assertMapValuesCleared(message);
        builder = message.toBuilder();
        setMapValuesUsingAccessors(builder);
        message = builder.build();
        assertMapValuesSet(message);
        builder = message.toBuilder();
        updateMapValuesUsingAccessors(builder);
        message = builder.build();
        assertMapValuesUpdated(message);
        builder = message.toBuilder();
        builder.clear();
        assertMapValuesCleared(builder);
        message = builder.build();
        assertMapValuesCleared(message);
    }

    public void testPutAll() throws Exception {
        TestMap.Builder sourceBuilder = TestMap.newBuilder();
        setMapValuesUsingAccessors(sourceBuilder);
        TestMap source = sourceBuilder.build();
        assertMapValuesSet(source);
        TestMap.Builder destination = TestMap.newBuilder();
        copyMapValues(source, destination);
        assertMapValuesSet(destination.build());
    }

    public void testPutAllForUnknownEnumValues() throws Exception {
        TestMap source = // unknown value.
        TestMap.newBuilder().putAllInt32ToEnumFieldValue(MapTest.newMap(0, 0, 1, 1, 2, 1000)).build();
        TestMap destination = TestMap.newBuilder().putAllInt32ToEnumFieldValue(source.getInt32ToEnumFieldValue()).build();
        TestCase.assertEquals(0, destination.getInt32ToEnumFieldValue().get(0).intValue());
        TestCase.assertEquals(1, destination.getInt32ToEnumFieldValue().get(1).intValue());
        TestCase.assertEquals(1000, destination.getInt32ToEnumFieldValue().get(2).intValue());
        TestCase.assertEquals(3, destination.getInt32ToEnumFieldCount());
    }

    public void testPutForUnknownEnumValues() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder().putInt32ToEnumFieldValue(0, 0).putInt32ToEnumFieldValue(1, 1).putInt32ToEnumFieldValue(2, 1000);// unknown value.

        TestMap message = builder.build();
        TestCase.assertEquals(0, message.getInt32ToEnumFieldValueOrThrow(0));
        TestCase.assertEquals(1, message.getInt32ToEnumFieldValueOrThrow(1));
        TestCase.assertEquals(1000, message.getInt32ToEnumFieldValueOrThrow(2));
        TestCase.assertEquals(3, message.getInt32ToEnumFieldCount());
    }

    public void testPutChecksNullKeysAndValues() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        try {
            builder.putInt32ToStringField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected.
        }
        try {
            builder.putInt32ToBytesField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected.
        }
        try {
            builder.putInt32ToEnumField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected.
        }
        try {
            builder.putInt32ToMessageField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected.
        }
        try {
            builder.putStringToInt32Field(null, 1);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected.
        }
    }

    public void testSerializeAndParse() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(message.getSerializedSize(), message.toByteString().size());
        message = TestMap.parser().parseFrom(message.toByteString());
        assertMapValuesSet(message);
        builder = message.toBuilder();
        updateMapValuesUsingAccessors(builder);
        message = builder.build();
        TestCase.assertEquals(message.getSerializedSize(), message.toByteString().size());
        message = TestMap.parser().parseFrom(message.toByteString());
        assertMapValuesUpdated(message);
        builder = message.toBuilder();
        builder.clear();
        message = builder.build();
        TestCase.assertEquals(message.getSerializedSize(), message.toByteString().size());
        message = TestMap.parser().parseFrom(message.toByteString());
        assertMapValuesCleared(message);
    }

    public void testParseError() throws Exception {
        ByteString bytes = TestUtil.toBytes("SOME BYTES");
        String stringKey = "a string key";
        TestMap map = tryParseTestMap(BizarroTestMap.newBuilder().putInt32ToInt32Field(5, bytes).build());
        TestCase.assertEquals(0, map.getInt32ToInt32FieldOrDefault(5, (-1)));
        map = tryParseTestMap(BizarroTestMap.newBuilder().putInt32ToStringField(stringKey, 5).build());
        TestCase.assertEquals("", map.getInt32ToStringFieldOrDefault(0, null));
        map = tryParseTestMap(BizarroTestMap.newBuilder().putInt32ToBytesField(stringKey, 5).build());
        TestCase.assertEquals(map.getInt32ToBytesFieldOrDefault(0, null), EMPTY);
        map = tryParseTestMap(BizarroTestMap.newBuilder().putInt32ToEnumField(stringKey, bytes).build());
        TestCase.assertEquals(FOO, map.getInt32ToEnumFieldOrDefault(0, null));
        try {
            tryParseTestMap(BizarroTestMap.newBuilder().putInt32ToMessageField(stringKey, bytes).build());
            TestCase.fail();
        } catch (InvalidProtocolBufferException expected) {
            TestCase.assertTrue(((expected.getUnfinishedMessage()) instanceof TestMap));
            map = ((TestMap) (expected.getUnfinishedMessage()));
            TestCase.assertTrue(map.getInt32ToMessageField().isEmpty());
        }
        map = tryParseTestMap(BizarroTestMap.newBuilder().putStringToInt32Field(stringKey, bytes).build());
        TestCase.assertEquals(0, map.getStringToInt32FieldOrDefault(stringKey, (-1)));
    }

    public void testMergeFrom() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        TestMap.Builder other = TestMap.newBuilder();
        other.mergeFrom(message);
        assertMapValuesSet(other.build());
    }

    public void testEqualsAndHashCode() throws Exception {
        // Test that generated equals() and hashCode() will disregard the order
        // of map entries when comparing/hashing map fields.
        // We can't control the order of elements in a HashMap. The best we can do
        // here is to add elements in different order.
        TestMap.Builder b1 = TestMap.newBuilder().putInt32ToInt32Field(1, 2).putInt32ToInt32Field(3, 4).putInt32ToInt32Field(5, 6);
        TestMap m1 = b1.build();
        TestMap.Builder b2 = TestMap.newBuilder().putInt32ToInt32Field(5, 6).putInt32ToInt32Field(1, 2).putInt32ToInt32Field(3, 4);
        TestMap m2 = b2.build();
        TestCase.assertEquals(m1, m2);
        TestCase.assertEquals(m1.hashCode(), m2.hashCode());
        // Make sure we did compare map fields.
        b2.putInt32ToInt32Field(1, 0);
        m2 = b2.build();
        TestCase.assertFalse(m1.equals(m2));
        // Don't check m1.hashCode() != m2.hashCode() because it's not guaranteed
        // to be different.
        // Regression test for b/18549190: if a map is a subset of the other map,
        // equals() should return false.
        b2.removeInt32ToInt32Field(1);
        m2 = b2.build();
        TestCase.assertFalse(m1.equals(m2));
        TestCase.assertFalse(m2.equals(m1));
    }

    public void testNestedBuilderOnChangeEventPropagation() {
        TestOnChangeEventPropagation.Builder parent = TestOnChangeEventPropagation.newBuilder();
        parent.getOptionalMessageBuilder().putInt32ToInt32Field(1, 2);
        TestOnChangeEventPropagation message = parent.build();
        TestCase.assertEquals(2, message.getOptionalMessage().getInt32ToInt32Field().get(1).intValue());
        // Make a change using nested builder.
        parent.getOptionalMessageBuilder().putInt32ToInt32Field(1, 3);
        // Should be able to observe the change.
        message = parent.build();
        TestCase.assertEquals(3, message.getOptionalMessage().getInt32ToInt32Field().get(1).intValue());
        // Make another change using mergeFrom()
        TestMap.Builder other = TestMap.newBuilder().putInt32ToInt32Field(1, 4);
        parent.getOptionalMessageBuilder().mergeFrom(other.build());
        // Should be able to observe the change.
        message = parent.build();
        TestCase.assertEquals(4, message.getOptionalMessage().getInt32ToInt32Field().get(1).intValue());
        // Make yet another change by clearing the nested builder.
        parent.getOptionalMessageBuilder().clear();
        // Should be able to observe the change.
        message = parent.build();
        TestCase.assertEquals(0, message.getOptionalMessage().getInt32ToInt32Field().size());
    }

    public void testNestedBuilderOnChangeEventPropagationReflection() {
        FieldDescriptor intMapField = MapTest.f("int32_to_int32_field");
        // Create an outer message builder with nested builder.
        TestOnChangeEventPropagation.Builder parentBuilder = TestOnChangeEventPropagation.newBuilder();
        TestMap.Builder testMapBuilder = parentBuilder.getOptionalMessageBuilder();
        // Create a map entry message.
        TestMap.Builder entryBuilder = TestMap.newBuilder().putInt32ToInt32Field(1, 1);
        // Put the entry into the nested builder.
        testMapBuilder.addRepeatedField(intMapField, entryBuilder.getRepeatedField(intMapField, 0));
        // Should be able to observe the change.
        TestOnChangeEventPropagation message = parentBuilder.build();
        TestCase.assertEquals(1, message.getOptionalMessage().getInt32ToInt32Field().size());
        // Change the entry value.
        entryBuilder.putInt32ToInt32Field(1, 4);
        testMapBuilder = parentBuilder.getOptionalMessageBuilder();
        testMapBuilder.setRepeatedField(intMapField, 0, entryBuilder.getRepeatedField(intMapField, 0));
        // Should be able to observe the change.
        message = parentBuilder.build();
        TestCase.assertEquals(4, message.getOptionalMessage().getInt32ToInt32Field().get(1).intValue());
        // Clear the nested builder.
        testMapBuilder = parentBuilder.getOptionalMessageBuilder();
        testMapBuilder.clearField(intMapField);
        // Should be able to observe the change.
        message = parentBuilder.build();
        TestCase.assertEquals(0, message.getOptionalMessage().getInt32ToInt32Field().size());
    }

    public void testReflectionApi() throws Exception {
        // In reflection API, map fields are just repeated message fields.
        TestMap.Builder builder = TestMap.newBuilder().putInt32ToInt32Field(1, 2).putInt32ToInt32Field(3, 4).putInt32ToMessageField(11, MessageValue.newBuilder().setValue(22).build()).putInt32ToMessageField(33, MessageValue.newBuilder().setValue(44).build());
        TestMap message = builder.build();
        // Test getField(), getRepeatedFieldCount(), getRepeatedField().
        MapTest.assertHasMapValues(message, "int32_to_int32_field", MapTest.mapForValues(1, 2, 3, 4));
        MapTest.assertHasMapValues(message, "int32_to_message_field", MapTest.mapForValues(11, MessageValue.newBuilder().setValue(22).build(), 33, MessageValue.newBuilder().setValue(44).build()));
        // Test clearField()
        builder.clearField(MapTest.f("int32_to_int32_field"));
        builder.clearField(MapTest.f("int32_to_message_field"));
        message = builder.build();
        TestCase.assertEquals(0, message.getInt32ToInt32Field().size());
        TestCase.assertEquals(0, message.getInt32ToMessageField().size());
        // Test setField()
        MapTest.setMapValues(builder, "int32_to_int32_field", MapTest.mapForValues(11, 22, 33, 44));
        MapTest.setMapValues(builder, "int32_to_message_field", MapTest.mapForValues(111, MessageValue.newBuilder().setValue(222).build(), 333, MessageValue.newBuilder().setValue(444).build()));
        message = builder.build();
        TestCase.assertEquals(22, message.getInt32ToInt32Field().get(11).intValue());
        TestCase.assertEquals(44, message.getInt32ToInt32Field().get(33).intValue());
        TestCase.assertEquals(222, message.getInt32ToMessageField().get(111).getValue());
        TestCase.assertEquals(444, message.getInt32ToMessageField().get(333).getValue());
        // Test addRepeatedField
        builder.addRepeatedField(MapTest.f("int32_to_int32_field"), MapTest.newMapEntry(builder, "int32_to_int32_field", 55, 66));
        builder.addRepeatedField(MapTest.f("int32_to_message_field"), MapTest.newMapEntry(builder, "int32_to_message_field", 555, MessageValue.newBuilder().setValue(666).build()));
        message = builder.build();
        TestCase.assertEquals(66, message.getInt32ToInt32Field().get(55).intValue());
        TestCase.assertEquals(666, message.getInt32ToMessageField().get(555).getValue());
        // Test addRepeatedField (overriding existing values)
        builder.addRepeatedField(MapTest.f("int32_to_int32_field"), MapTest.newMapEntry(builder, "int32_to_int32_field", 55, 55));
        builder.addRepeatedField(MapTest.f("int32_to_message_field"), MapTest.newMapEntry(builder, "int32_to_message_field", 555, MessageValue.newBuilder().setValue(555).build()));
        message = builder.build();
        TestCase.assertEquals(55, message.getInt32ToInt32Field().get(55).intValue());
        TestCase.assertEquals(555, message.getInt32ToMessageField().get(555).getValue());
        // Test setRepeatedField
        for (int i = 0; i < (builder.getRepeatedFieldCount(MapTest.f("int32_to_int32_field"))); i++) {
            Message mapEntry = ((Message) (builder.getRepeatedField(MapTest.f("int32_to_int32_field"), i)));
            int oldKey = ((Integer) (MapTest.getFieldValue(mapEntry, "key"))).intValue();
            int oldValue = ((Integer) (MapTest.getFieldValue(mapEntry, "value"))).intValue();
            // Swap key with value for each entry.
            Message.Builder mapEntryBuilder = mapEntry.toBuilder();
            MapTest.setFieldValue(mapEntryBuilder, "key", oldValue);
            MapTest.setFieldValue(mapEntryBuilder, "value", oldKey);
            builder.setRepeatedField(MapTest.f("int32_to_int32_field"), i, mapEntryBuilder.build());
        }
        message = builder.build();
        TestCase.assertEquals(11, message.getInt32ToInt32Field().get(22).intValue());
        TestCase.assertEquals(33, message.getInt32ToInt32Field().get(44).intValue());
        TestCase.assertEquals(55, message.getInt32ToInt32Field().get(55).intValue());
    }

    // See additional coverage in TextFormatTest.java.
    public void testTextFormat() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        String textData = TextFormat.printToString(message);
        builder = TestMap.newBuilder();
        TextFormat.merge(textData, builder);
        message = builder.build();
        assertMapValuesSet(message);
    }

    public void testDynamicMessage() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        Message dynamicDefaultInstance = DynamicMessage.getDefaultInstance(TestMap.getDescriptor());
        Message dynamicMessage = dynamicDefaultInstance.newBuilderForType().mergeFrom(message.toByteString()).build();
        TestCase.assertEquals(message, dynamicMessage);
        TestCase.assertEquals(message.hashCode(), dynamicMessage.hashCode());
    }

    // Check that DynamicMessage handles map field serialization the same way as generated code
    // regarding unset key and value field in a map entry.
    public void testDynamicMessageUnsetKeyAndValue() throws Exception {
        FieldDescriptor field = MapTest.f("int32_to_int32_field");
        Message dynamicDefaultInstance = DynamicMessage.getDefaultInstance(TestMap.getDescriptor());
        Message.Builder builder = dynamicDefaultInstance.newBuilderForType();
        // Add an entry without key and value.
        builder.addRepeatedField(field, builder.newBuilderForField(field).build());
        Message message = builder.build();
        ByteString bytes = message.toByteString();
        // Parse it back to the same generated type.
        Message generatedMessage = TestMap.parseFrom(bytes);
        // Assert the serialized bytes are equivalent.
        TestCase.assertEquals(generatedMessage.toByteString(), bytes);
    }

    public void testReflectionEqualsAndHashCode() throws Exception {
        // Test that generated equals() and hashCode() will disregard the order
        // of map entries when comparing/hashing map fields.
        // We use DynamicMessage to test reflection based equals()/hashCode().
        Message dynamicDefaultInstance = DynamicMessage.getDefaultInstance(TestMap.getDescriptor());
        FieldDescriptor field = MapTest.f("int32_to_int32_field");
        Message.Builder b1 = dynamicDefaultInstance.newBuilderForType();
        b1.addRepeatedField(field, MapTest.newMapEntry(b1, "int32_to_int32_field", 1, 2));
        b1.addRepeatedField(field, MapTest.newMapEntry(b1, "int32_to_int32_field", 3, 4));
        b1.addRepeatedField(field, MapTest.newMapEntry(b1, "int32_to_int32_field", 5, 6));
        Message m1 = b1.build();
        Message.Builder b2 = dynamicDefaultInstance.newBuilderForType();
        b2.addRepeatedField(field, MapTest.newMapEntry(b2, "int32_to_int32_field", 5, 6));
        b2.addRepeatedField(field, MapTest.newMapEntry(b2, "int32_to_int32_field", 1, 2));
        b2.addRepeatedField(field, MapTest.newMapEntry(b2, "int32_to_int32_field", 3, 4));
        Message m2 = b2.build();
        TestCase.assertEquals(m1, m2);
        TestCase.assertEquals(m1.hashCode(), m2.hashCode());
        // Make sure we did compare map fields.
        b2.setRepeatedField(field, 0, MapTest.newMapEntry(b1, "int32_to_int32_field", 0, 0));
        m2 = b2.build();
        TestCase.assertFalse(m1.equals(m2));
        // Don't check m1.hashCode() != m2.hashCode() because it's not guaranteed
        // to be different.
    }

    public void testUnknownEnumValues() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder().putAllInt32ToEnumFieldValue(MapTest.newMap(0, 0, 1, 1, 2, 1000));// unknown value.

        TestMap message = builder.build();
        TestCase.assertEquals(FOO, message.getInt32ToEnumField().get(0));
        TestCase.assertEquals(BAR, message.getInt32ToEnumField().get(1));
        TestCase.assertEquals(UNRECOGNIZED, message.getInt32ToEnumField().get(2));
        TestCase.assertEquals(1000, message.getInt32ToEnumFieldValue().get(2).intValue());
        // Unknown enum values should be preserved after:
        // 1. Serialization and parsing.
        // 2. toBuild().
        // 3. mergeFrom().
        message = TestMap.parseFrom(message.toByteString());
        TestCase.assertEquals(1000, message.getInt32ToEnumFieldValue().get(2).intValue());
        builder = message.toBuilder();
        TestCase.assertEquals(1000, builder.getInt32ToEnumFieldValue().get(2).intValue());
        builder = TestMap.newBuilder().mergeFrom(message);
        TestCase.assertEquals(1000, builder.getInt32ToEnumFieldValue().get(2).intValue());
        // hashCode()/equals() should take unknown enum values into account.
        builder.putAllInt32ToEnumFieldValue(MapTest.newMap(2, 1001));
        TestMap message2 = builder.build();
        TestCase.assertFalse(((message.hashCode()) == (message2.hashCode())));
        TestCase.assertFalse(message.equals(message2));
        // Unknown values will be converted to UNRECOGNIZED so the resulted enum map
        // should be the same.
        TestCase.assertTrue(message.getInt32ToEnumField().equals(message2.getInt32ToEnumField()));
    }

    public void testUnknownEnumValuesInReflectionApi() throws Exception {
        Descriptor descriptor = TestMap.getDescriptor();
        EnumDescriptor enumDescriptor = EnumValue.getDescriptor();
        FieldDescriptor field = descriptor.findFieldByName("int32_to_enum_field");
        Map<Integer, Integer> data = MapTest.newMap(0, 0, 1, 1, 2, 1000);// unknown value

        TestMap.Builder builder = TestMap.newBuilder().putAllInt32ToEnumFieldValue(data);
        // Try to read unknown enum values using reflection API.
        for (int i = 0; i < (builder.getRepeatedFieldCount(field)); i++) {
            Message mapEntry = ((Message) (builder.getRepeatedField(field, i)));
            int key = ((Integer) (MapTest.getFieldValue(mapEntry, "key"))).intValue();
            int value = getNumber();
            TestCase.assertEquals(data.get(key).intValue(), value);
            Message.Builder mapEntryBuilder = mapEntry.toBuilder();
            // Increase the value by 1.
            MapTest.setFieldValue(mapEntryBuilder, "value", enumDescriptor.findValueByNumberCreatingIfUnknown((value + 1)));
            builder.setRepeatedField(field, i, mapEntryBuilder.build());
        }
        // Verify that enum values have been successfully updated.
        TestMap message = builder.build();
        for (Map.Entry<Integer, Integer> entry : message.getInt32ToEnumFieldValue().entrySet()) {
            TestCase.assertEquals(((data.get(entry.getKey())) + 1), entry.getValue().intValue());
        }
    }

    public void testIterationOrder() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(Arrays.asList("1", "2", "3"), new ArrayList<String>(message.getStringToInt32Field().keySet()));
    }

    public void testGetMap() {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(message.getStringToInt32Field(), message.getStringToInt32FieldMap());
        TestCase.assertEquals(message.getInt32ToBytesField(), message.getInt32ToBytesFieldMap());
        TestCase.assertEquals(message.getInt32ToEnumField(), message.getInt32ToEnumFieldMap());
        TestCase.assertEquals(message.getInt32ToEnumFieldValue(), message.getInt32ToEnumFieldValueMap());
        TestCase.assertEquals(message.getInt32ToMessageField(), message.getInt32ToMessageFieldMap());
    }

    public void testContains() {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        assertMapContainsSetValues(builder);
        assertMapContainsSetValues(builder.build());
    }

    public void testCount() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapCounts(0, builder);
        setMapValuesUsingAccessors(builder);
        assertMapCounts(3, builder);
        TestMap message = builder.build();
        assertMapCounts(3, message);
        builder = message.toBuilder().putInt32ToInt32Field(4, 44);
        TestCase.assertEquals(4, builder.getInt32ToInt32FieldCount());
        TestCase.assertEquals(4, builder.build().getInt32ToInt32FieldCount());
        // already present - should be unchanged
        builder.putInt32ToInt32Field(4, 44);
        TestCase.assertEquals(4, builder.getInt32ToInt32FieldCount());
    }

    public void testGetOrDefault() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapCounts(0, builder);
        setMapValuesUsingAccessors(builder);
        doTestGetOrDefault(builder);
        doTestGetOrDefault(builder.build());
    }

    public void testGetOrThrow() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapCounts(0, builder);
        setMapValuesUsingAccessors(builder);
        doTestGetOrDefault(builder);
        doTestGetOrDefault(builder.build());
    }

    public void testPut() {
        TestMap.Builder builder = TestMap.newBuilder();
        builder.putInt32ToInt32Field(1, 11);
        TestCase.assertEquals(11, builder.getInt32ToInt32FieldOrThrow(1));
        builder.putInt32ToStringField(1, "a");
        TestCase.assertEquals("a", builder.getInt32ToStringFieldOrThrow(1));
        try {
            builder.putInt32ToStringField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected
        }
        builder.putInt32ToBytesField(1, TestUtil.toBytes("11"));
        TestCase.assertEquals(TestUtil.toBytes("11"), builder.getInt32ToBytesFieldOrThrow(1));
        try {
            builder.putInt32ToBytesField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected
        }
        builder.putInt32ToEnumField(1, FOO);
        TestCase.assertEquals(FOO, builder.getInt32ToEnumFieldOrThrow(1));
        try {
            builder.putInt32ToEnumField(1, null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected
        }
        builder.putInt32ToEnumFieldValue(1, BAR.getNumber());
        TestCase.assertEquals(BAR.getNumber(), builder.getInt32ToEnumFieldValueOrThrow(1));
        builder.putInt32ToEnumFieldValue(1, (-1));
        TestCase.assertEquals((-1), builder.getInt32ToEnumFieldValueOrThrow(1));
        TestCase.assertEquals(UNRECOGNIZED, builder.getInt32ToEnumFieldOrThrow(1));
        builder.putStringToInt32Field("a", 1);
        TestCase.assertEquals(1, builder.getStringToInt32FieldOrThrow("a"));
        try {
            builder.putStringToInt32Field(null, (-1));
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testRemove() {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestCase.assertEquals(11, builder.getInt32ToInt32FieldOrThrow(1));
        for (int times = 0; times < 2; times++) {
            builder.removeInt32ToInt32Field(1);
            TestCase.assertEquals((-1), builder.getInt32ToInt32FieldOrDefault(1, (-1)));
        }
        TestCase.assertEquals("11", builder.getInt32ToStringFieldOrThrow(1));
        for (int times = 0; times < 2; times++) {
            builder.removeInt32ToStringField(1);
            TestCase.assertNull(builder.getInt32ToStringFieldOrDefault(1, null));
        }
        TestCase.assertEquals(TestUtil.toBytes("11"), builder.getInt32ToBytesFieldOrThrow(1));
        for (int times = 0; times < 2; times++) {
            builder.removeInt32ToBytesField(1);
            TestCase.assertNull(builder.getInt32ToBytesFieldOrDefault(1, null));
        }
        TestCase.assertEquals(FOO, builder.getInt32ToEnumFieldOrThrow(1));
        for (int times = 0; times < 2; times++) {
            builder.removeInt32ToEnumField(1);
            TestCase.assertNull(builder.getInt32ToEnumFieldOrDefault(1, null));
        }
        TestCase.assertEquals(11, builder.getStringToInt32FieldOrThrow("1"));
        for (int times = 0; times < 2; times++) {
            builder.removeStringToInt32Field("1");
            TestCase.assertEquals((-1), builder.getStringToInt32FieldOrDefault("1", (-1)));
        }
        try {
            builder.removeStringToInt32Field(null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testReservedWordsFieldNames() {
        ReservedAsMapField.newBuilder().build();
        ReservedAsMapFieldWithEnumValue.newBuilder().build();
    }

    public void testDeterministicSerialziation() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        // int32->int32
        builder.putInt32ToInt32Field(5, 1);
        builder.putInt32ToInt32Field(1, 1);
        builder.putInt32ToInt32Field(4, 1);
        builder.putInt32ToInt32Field((-2), 1);
        builder.putInt32ToInt32Field(0, 1);
        // uint32->int32
        builder.putUint32ToInt32Field(5, 1);
        builder.putUint32ToInt32Field(1, 1);
        builder.putUint32ToInt32Field(4, 1);
        builder.putUint32ToInt32Field((-2), 1);
        builder.putUint32ToInt32Field(0, 1);
        // int64->int32
        builder.putInt64ToInt32Field(5L, 1);
        builder.putInt64ToInt32Field(1L, 1);
        builder.putInt64ToInt32Field(4L, 1);
        builder.putInt64ToInt32Field((-2L), 1);
        builder.putInt64ToInt32Field(0L, 1);
        // string->int32
        builder.putStringToInt32Field("baz", 1);
        builder.putStringToInt32Field("foo", 1);
        builder.putStringToInt32Field("bar", 1);
        builder.putStringToInt32Field("", 1);
        builder.putStringToInt32Field("hello", 1);
        builder.putStringToInt32Field("world", 1);
        TestMap message = builder.build();
        byte[] serialized = new byte[message.getSerializedSize()];
        CodedOutputStream output = CodedOutputStream.newInstance(serialized);
        output.useDeterministicSerialization();
        message.writeTo(output);
        output.flush();
        CodedInputStream input = CodedInputStream.newInstance(serialized);
        List<Integer> int32Keys = new ArrayList<Integer>();
        List<Integer> uint32Keys = new ArrayList<Integer>();
        List<Long> int64Keys = new ArrayList<Long>();
        List<String> stringKeys = new ArrayList<String>();
        int tag;
        while (true) {
            tag = input.readTag();
            if (tag == 0) {
                break;
            }
            int length = input.readRawVarint32();
            int oldLimit = input.pushLimit(length);
            switch (WireFormat.getTagFieldNumber(tag)) {
                case TestMap.STRING_TO_INT32_FIELD_FIELD_NUMBER :
                    stringKeys.add(readMapStringKey(input));
                    break;
                case TestMap.INT32_TO_INT32_FIELD_FIELD_NUMBER :
                    int32Keys.add(readMapIntegerKey(input));
                    break;
                case TestMap.UINT32_TO_INT32_FIELD_FIELD_NUMBER :
                    uint32Keys.add(readMapIntegerKey(input));
                    break;
                case TestMap.INT64_TO_INT32_FIELD_FIELD_NUMBER :
                    int64Keys.add(readMapLongKey(input));
                    break;
                default :
                    TestCase.fail("Unexpected fields.");
            }
            input.popLimit(oldLimit);
        } 
        TestCase.assertEquals(Arrays.asList((-2), 0, 1, 4, 5), int32Keys);
        TestCase.assertEquals(Arrays.asList((-2), 0, 1, 4, 5), uint32Keys);
        TestCase.assertEquals(Arrays.asList((-2L), 0L, 1L, 4L, 5L), int64Keys);
        TestCase.assertEquals(Arrays.asList("", "bar", "baz", "foo", "hello", "world"), stringKeys);
    }

    public void testInitFromPartialDynamicMessage() {
        FieldDescriptor fieldDescriptor = TestMap.getDescriptor().findFieldByNumber(INT32_TO_MESSAGE_FIELD_FIELD_NUMBER);
        Descriptor mapEntryType = fieldDescriptor.getMessageType();
        FieldDescriptor keyField = mapEntryType.findFieldByNumber(1);
        FieldDescriptor valueField = mapEntryType.findFieldByNumber(2);
        DynamicMessage dynamicMessage = DynamicMessage.newBuilder(TestMap.getDescriptor()).addRepeatedField(fieldDescriptor, DynamicMessage.newBuilder(mapEntryType).setField(keyField, 10).setField(valueField, TestMap.MessageValue.newBuilder().setValue(10).build()).build()).build();
        TestMap message = TestMap.newBuilder().mergeFrom(dynamicMessage).build();
        TestCase.assertEquals(TestMap.MessageValue.newBuilder().setValue(10).build(), message.getInt32ToMessageFieldMap().get(10));
    }

    public void testInitFromFullyDynamicMessage() {
        FieldDescriptor fieldDescriptor = TestMap.getDescriptor().findFieldByNumber(INT32_TO_MESSAGE_FIELD_FIELD_NUMBER);
        Descriptor mapEntryType = fieldDescriptor.getMessageType();
        FieldDescriptor keyField = mapEntryType.findFieldByNumber(1);
        FieldDescriptor valueField = mapEntryType.findFieldByNumber(2);
        DynamicMessage dynamicMessage = DynamicMessage.newBuilder(TestMap.getDescriptor()).addRepeatedField(fieldDescriptor, DynamicMessage.newBuilder(mapEntryType).setField(keyField, 10).setField(valueField, DynamicMessage.newBuilder(TestMap.MessageValue.getDescriptor()).setField(TestMap.MessageValue.getDescriptor().findFieldByName("value"), 10).build()).build()).build();
        TestMap message = TestMap.newBuilder().mergeFrom(dynamicMessage).build();
        TestCase.assertEquals(TestMap.MessageValue.newBuilder().setValue(10).build(), message.getInt32ToMessageFieldMap().get(10));
    }

    public void testMap_withNulls() {
        TestMap.Builder builder = TestMap.newBuilder();
        try {
            builder.putStringToInt32Field(null, 3);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            builder.putAllStringToInt32Field(MapTest.newMap(null, 3, "hi", 4));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            builder.putInt32ToMessageField(3, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            builder.putAllInt32ToMessageField(MapTest.<Integer, MessageValue>newMap(4, null, 5, null));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            builder.putAllInt32ToMessageField(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        Assert.assertArrayEquals(new byte[0], builder.build().toByteArray());
    }
}

