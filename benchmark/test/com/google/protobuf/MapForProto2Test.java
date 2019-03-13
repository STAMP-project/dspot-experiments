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
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import junit.framework.TestCase;
import map_test.MapForProto2TestProto.BizarroTestMap;
import map_test.MapForProto2TestProto.ReservedAsMapField;
import map_test.MapForProto2TestProto.ReservedAsMapFieldWithEnumValue;
import map_test.MapForProto2TestProto.TestMap;
import map_test.MapForProto2TestProto.TestMap.MessageValue;
import map_test.MapForProto2TestProto.TestMap.MessageWithRequiredFields;
import map_test.MapForProto2TestProto.TestRecursiveMap;
import map_test.MapForProto2TestProto.TestUnknownEnumValue;
import map_test.Message1;
import map_test.RedactAllTypes;


/**
 * Unit tests for map fields in proto2 protos.
 */
public class MapForProto2Test extends TestCase {
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
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2), builder.build().getInt32ToInt32Field());
        try {
            intMap.put(2, 3);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2), builder.getInt32ToInt32Field());
        builder.getMutableInt32ToInt32Field().put(2, 3);
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2, 2, 3), builder.getInt32ToInt32Field());
        // 
        Map<Integer, TestMap.EnumValue> enumMap = builder.getMutableInt32ToEnumField();
        enumMap.put(1, BAR);
        TestCase.assertEquals(MapForProto2Test.newMap(1, BAR), builder.build().getInt32ToEnumField());
        try {
            enumMap.put(2, FOO);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapForProto2Test.newMap(1, BAR), builder.getInt32ToEnumField());
        builder.getMutableInt32ToEnumField().put(2, FOO);
        TestCase.assertEquals(MapForProto2Test.newMap(1, BAR, 2, FOO), builder.getInt32ToEnumField());
        // 
        Map<Integer, String> stringMap = builder.getMutableInt32ToStringField();
        stringMap.put(1, "1");
        TestCase.assertEquals(MapForProto2Test.newMap(1, "1"), builder.build().getInt32ToStringField());
        try {
            stringMap.put(2, "2");
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapForProto2Test.newMap(1, "1"), builder.getInt32ToStringField());
        builder.getMutableInt32ToStringField().put(2, "2");
        TestCase.assertEquals(MapForProto2Test.newMap(1, "1", 2, "2"), builder.getInt32ToStringField());
        // 
        Map<Integer, TestMap.MessageValue> messageMap = builder.getMutableInt32ToMessageField();
        messageMap.put(1, TestMap.MessageValue.getDefaultInstance());
        TestCase.assertEquals(MapForProto2Test.newMap(1, TestMap.MessageValue.getDefaultInstance()), builder.build().getInt32ToMessageField());
        try {
            messageMap.put(2, TestMap.MessageValue.getDefaultInstance());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        TestCase.assertEquals(MapForProto2Test.newMap(1, TestMap.MessageValue.getDefaultInstance()), builder.getInt32ToMessageField());
        builder.getMutableInt32ToMessageField().put(2, TestMap.MessageValue.getDefaultInstance());
        TestCase.assertEquals(MapForProto2Test.newMap(1, TestMap.MessageValue.getDefaultInstance(), 2, TestMap.MessageValue.getDefaultInstance()), builder.getInt32ToMessageField());
    }

    // 
    public void testMutableMapLifecycle_collections() {
        TestMap.Builder builder = TestMap.newBuilder();
        Map<Integer, Integer> intMap = builder.getMutableInt32ToInt32Field();
        intMap.put(1, 2);
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2), builder.build().getInt32ToInt32Field());
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
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2), intMap);
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2), builder.getInt32ToInt32Field());
        TestCase.assertEquals(MapForProto2Test.newMap(1, 2), builder.build().getInt32ToInt32Field());
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
        TestCase.assertEquals(3, destination.getInt32ToEnumFieldCount());
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
        TestMap.Builder b1 = TestMap.newBuilder();
        b1.putInt32ToInt32Field(1, 2);
        b1.putInt32ToInt32Field(3, 4);
        b1.putInt32ToInt32Field(5, 6);
        TestMap m1 = b1.build();
        TestMap.Builder b2 = TestMap.newBuilder();
        b2.putInt32ToInt32Field(5, 6);
        b2.putInt32ToInt32Field(1, 2);
        b2.putInt32ToInt32Field(3, 4);
        TestMap m2 = b2.build();
        TestCase.assertEquals(m1, m2);
        TestCase.assertEquals(m1.hashCode(), m2.hashCode());
        // Make sure we did compare map fields.
        b2.putInt32ToInt32Field(1, 0);
        m2 = b2.build();
        TestCase.assertFalse(m1.equals(m2));
        // Don't check m1.hashCode() != m2.hashCode() because it's not guaranteed
        // to be different.
    }

    public void testReflectionApi() throws Exception {
        // In reflection API, map fields are just repeated message fields.
        TestMap.Builder builder = TestMap.newBuilder().putInt32ToInt32Field(1, 2).putInt32ToInt32Field(3, 4).putInt32ToMessageField(11, MessageValue.newBuilder().setValue(22).build()).putInt32ToMessageField(33, MessageValue.newBuilder().setValue(44).build());
        TestMap message = builder.build();
        // Test getField(), getRepeatedFieldCount(), getRepeatedField().
        MapForProto2Test.assertHasMapValues(message, "int32_to_int32_field", MapForProto2Test.mapForValues(1, 2, 3, 4));
        MapForProto2Test.assertHasMapValues(message, "int32_to_message_field", MapForProto2Test.mapForValues(11, MessageValue.newBuilder().setValue(22).build(), 33, MessageValue.newBuilder().setValue(44).build()));
        // Test clearField()
        builder.clearField(MapForProto2Test.f("int32_to_int32_field"));
        builder.clearField(MapForProto2Test.f("int32_to_message_field"));
        message = builder.build();
        TestCase.assertEquals(0, message.getInt32ToInt32Field().size());
        TestCase.assertEquals(0, message.getInt32ToMessageField().size());
        // Test setField()
        MapForProto2Test.setMapValues(builder, "int32_to_int32_field", MapForProto2Test.mapForValues(11, 22, 33, 44));
        MapForProto2Test.setMapValues(builder, "int32_to_message_field", MapForProto2Test.mapForValues(111, MessageValue.newBuilder().setValue(222).build(), 333, MessageValue.newBuilder().setValue(444).build()));
        message = builder.build();
        TestCase.assertEquals(22, message.getInt32ToInt32Field().get(11).intValue());
        TestCase.assertEquals(44, message.getInt32ToInt32Field().get(33).intValue());
        TestCase.assertEquals(222, message.getInt32ToMessageField().get(111).getValue());
        TestCase.assertEquals(444, message.getInt32ToMessageField().get(333).getValue());
        // Test addRepeatedField
        builder.addRepeatedField(MapForProto2Test.f("int32_to_int32_field"), MapForProto2Test.newMapEntry(builder, "int32_to_int32_field", 55, 66));
        builder.addRepeatedField(MapForProto2Test.f("int32_to_message_field"), MapForProto2Test.newMapEntry(builder, "int32_to_message_field", 555, MessageValue.newBuilder().setValue(666).build()));
        message = builder.build();
        TestCase.assertEquals(66, message.getInt32ToInt32Field().get(55).intValue());
        TestCase.assertEquals(666, message.getInt32ToMessageField().get(555).getValue());
        // Test addRepeatedField (overriding existing values)
        builder.addRepeatedField(MapForProto2Test.f("int32_to_int32_field"), MapForProto2Test.newMapEntry(builder, "int32_to_int32_field", 55, 55));
        builder.addRepeatedField(MapForProto2Test.f("int32_to_message_field"), MapForProto2Test.newMapEntry(builder, "int32_to_message_field", 555, MessageValue.newBuilder().setValue(555).build()));
        message = builder.build();
        TestCase.assertEquals(55, message.getInt32ToInt32Field().get(55).intValue());
        TestCase.assertEquals(555, message.getInt32ToMessageField().get(555).getValue());
        // Test setRepeatedField
        for (int i = 0; i < (builder.getRepeatedFieldCount(MapForProto2Test.f("int32_to_int32_field"))); i++) {
            Message mapEntry = ((Message) (builder.getRepeatedField(MapForProto2Test.f("int32_to_int32_field"), i)));
            int oldKey = ((Integer) (MapForProto2Test.getFieldValue(mapEntry, "key"))).intValue();
            int oldValue = ((Integer) (MapForProto2Test.getFieldValue(mapEntry, "value"))).intValue();
            // Swap key with value for each entry.
            Message.Builder mapEntryBuilder = mapEntry.toBuilder();
            MapForProto2Test.setFieldValue(mapEntryBuilder, "key", oldValue);
            MapForProto2Test.setFieldValue(mapEntryBuilder, "value", oldKey);
            builder.setRepeatedField(MapForProto2Test.f("int32_to_int32_field"), i, mapEntryBuilder.build());
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
        FieldDescriptor field = MapForProto2Test.f("int32_to_int32_field");
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
        FieldDescriptor field = MapForProto2Test.f("int32_to_int32_field");
        Message.Builder b1 = dynamicDefaultInstance.newBuilderForType();
        b1.addRepeatedField(field, MapForProto2Test.newMapEntry(b1, "int32_to_int32_field", 1, 2));
        b1.addRepeatedField(field, MapForProto2Test.newMapEntry(b1, "int32_to_int32_field", 3, 4));
        b1.addRepeatedField(field, MapForProto2Test.newMapEntry(b1, "int32_to_int32_field", 5, 6));
        Message m1 = b1.build();
        Message.Builder b2 = dynamicDefaultInstance.newBuilderForType();
        b2.addRepeatedField(field, MapForProto2Test.newMapEntry(b2, "int32_to_int32_field", 5, 6));
        b2.addRepeatedField(field, MapForProto2Test.newMapEntry(b2, "int32_to_int32_field", 1, 2));
        b2.addRepeatedField(field, MapForProto2Test.newMapEntry(b2, "int32_to_int32_field", 3, 4));
        Message m2 = b2.build();
        TestCase.assertEquals(m1, m2);
        TestCase.assertEquals(m1.hashCode(), m2.hashCode());
        // Make sure we did compare map fields.
        b2.setRepeatedField(field, 0, MapForProto2Test.newMapEntry(b1, "int32_to_int32_field", 0, 0));
        m2 = b2.build();
        TestCase.assertFalse(m1.equals(m2));
        // Don't check m1.hashCode() != m2.hashCode() because it's not guaranteed
        // to be different.
    }

    public void testUnknownEnumValues() throws Exception {
        TestUnknownEnumValue.Builder builder = TestUnknownEnumValue.newBuilder().putInt32ToInt32Field(1, 1).putInt32ToInt32Field(2, 54321);
        ByteString data = builder.build().toByteString();
        TestMap message = TestMap.parseFrom(data);
        // Entries with unknown enum values will be stored into UnknownFieldSet so
        // there is only one entry in the map.
        TestCase.assertEquals(1, message.getInt32ToEnumField().size());
        TestCase.assertEquals(BAR, message.getInt32ToEnumField().get(1));
        // UnknownFieldSet should not be empty.
        TestCase.assertFalse(message.getUnknownFields().asMap().isEmpty());
        // Serializing and parsing should preserve the unknown entry.
        data = message.toByteString();
        TestUnknownEnumValue messageWithUnknownEnums = TestUnknownEnumValue.parseFrom(data);
        TestCase.assertEquals(2, messageWithUnknownEnums.getInt32ToInt32Field().size());
        TestCase.assertEquals(1, messageWithUnknownEnums.getInt32ToInt32Field().get(1).intValue());
        TestCase.assertEquals(54321, messageWithUnknownEnums.getInt32ToInt32Field().get(2).intValue());
    }

    public void testRequiredMessage() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        builder.putRequiredMessageMap(0, MessageWithRequiredFields.newBuilder().buildPartial());
        TestMap message = builder.buildPartial();
        TestCase.assertFalse(message.isInitialized());
        builder.putRequiredMessageMap(0, MessageWithRequiredFields.newBuilder().setValue(1).build());
        message = builder.build();
        TestCase.assertTrue(message.isInitialized());
    }

    public void testRecursiveMap() throws Exception {
        TestRecursiveMap.Builder builder = TestRecursiveMap.newBuilder();
        builder.putRecursiveMapField(1, TestRecursiveMap.newBuilder().setValue(2).build());
        builder.putRecursiveMapField(3, TestRecursiveMap.newBuilder().setValue(4).build());
        ByteString data = builder.build().toByteString();
        TestRecursiveMap message = TestRecursiveMap.parseFrom(data);
        TestCase.assertEquals(2, message.getRecursiveMapField().get(1).getValue());
        TestCase.assertEquals(4, message.getRecursiveMapField().get(3).getValue());
    }

    public void testIterationOrder() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(Arrays.asList("1", "2", "3"), new ArrayList<String>(message.getStringToInt32Field().keySet()));
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

    // Regression test for b/20494788
    public void testMapInitializationOrder() throws Exception {
        TestCase.assertEquals("RedactAllTypes", RedactAllTypes.getDefaultInstance().getDescriptorForType().getName());
        map_test.Message1.Builder builder = Message1.newBuilder();
        builder.putMapField("key", true);
        Message1 message = builder.build();
        Message mapEntry = ((Message) (message.getRepeatedField(message.getDescriptorForType().findFieldByName("map_field"), 0)));
        TestCase.assertEquals(2, mapEntry.getAllFields().size());
    }

    public void testReservedWordsFieldNames() {
        ReservedAsMapField.newBuilder().build();
        ReservedAsMapFieldWithEnumValue.newBuilder().build();
    }

    public void testGetMap() {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValuesUsingAccessors(builder);
        assertMapValuesSet(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(message.getStringToInt32Field(), message.getStringToInt32FieldMap());
        TestCase.assertEquals(message.getInt32ToBytesField(), message.getInt32ToBytesFieldMap());
        TestCase.assertEquals(message.getInt32ToEnumField(), message.getInt32ToEnumFieldMap());
        TestCase.assertEquals(message.getInt32ToMessageField(), message.getInt32ToMessageFieldMap());
    }
}

