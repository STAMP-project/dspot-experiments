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
import TestMap.EnumValue.BAR;
import TestMap.EnumValue.FOO;
import TestMap.MessageValue;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;
import map_lite_test.MapForProto2TestProto.BizarroTestMap;
import map_lite_test.MapForProto2TestProto.TestMap;
import map_lite_test.MapForProto2TestProto.TestUnknownEnumValue;


/**
 * Unit tests for map fields.
 */
public final class MapForProto2LiteTest extends TestCase {
    public void testSetMapValues() {
        TestMap.Builder mapBuilder = TestMap.newBuilder();
        setMapValues(mapBuilder);
        TestMap map = mapBuilder.build();
        assertMapValuesSet(map);
    }

    public void testUpdateMapValues() {
        TestMap.Builder mapBuilder = TestMap.newBuilder();
        setMapValues(mapBuilder);
        TestMap map = mapBuilder.build();
        assertMapValuesSet(map);
        mapBuilder = map.toBuilder();
        updateMapValues(mapBuilder);
        map = mapBuilder.build();
        assertMapValuesUpdated(map);
    }

    public void testSanityCopyOnWrite() throws InvalidProtocolBufferException {
        // Since builders are implemented as a thin wrapper around a message
        // instance, we attempt to verify that we can't cause the builder to modify
        // a produced message.
        TestMap.Builder builder = TestMap.newBuilder();
        TestMap message = builder.build();
        builder.putInt32ToInt32Field(1, 2);
        TestCase.assertTrue(message.getInt32ToInt32Field().isEmpty());
        message = builder.build();
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2), message.getInt32ToInt32Field());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2), builder.getInt32ToInt32Field());
        builder.putInt32ToInt32Field(2, 3);
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2), message.getInt32ToInt32Field());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2, 2, 3), builder.getInt32ToInt32Field());
    }

    public void testGetMapIsImmutable() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapsAreImmutable(builder);
        assertMapsAreImmutable(builder.build());
        setMapValues(builder);
        assertMapsAreImmutable(builder);
        assertMapsAreImmutable(builder.build());
    }

    public void testMutableMapLifecycle() {
        TestMap.Builder builder = TestMap.newBuilder().putInt32ToInt32Field(1, 2);
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2), builder.build().getInt32ToInt32Field());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2), builder.getInt32ToInt32Field());
        builder.putInt32ToInt32Field(2, 3);
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, 2, 2, 3), builder.getInt32ToInt32Field());
        builder.putInt32ToEnumField(1, BAR);
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, BAR), builder.build().getInt32ToEnumField());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, BAR), builder.getInt32ToEnumField());
        builder.putInt32ToEnumField(2, FOO);
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, BAR, 2, FOO), builder.getInt32ToEnumField());
        builder.putInt32ToStringField(1, "1");
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, "1"), builder.build().getInt32ToStringField());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, "1"), builder.getInt32ToStringField());
        builder.putInt32ToStringField(2, "2");
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, "1", 2, "2"), builder.getInt32ToStringField());
        builder.putInt32ToMessageField(1, MessageValue.getDefaultInstance());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, MessageValue.getDefaultInstance()), builder.build().getInt32ToMessageField());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, MessageValue.getDefaultInstance()), builder.getInt32ToMessageField());
        builder.putInt32ToMessageField(2, MessageValue.getDefaultInstance());
        TestCase.assertEquals(MapForProto2LiteTest.newMap(1, MessageValue.getDefaultInstance(), 2, MessageValue.getDefaultInstance()), builder.getInt32ToMessageField());
    }

    public void testGettersAndSetters() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        TestMap message = builder.build();
        assertMapValuesCleared(message);
        builder = message.toBuilder();
        setMapValues(builder);
        message = builder.build();
        assertMapValuesSet(message);
        builder = message.toBuilder();
        updateMapValues(builder);
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
        setMapValues(sourceBuilder);
        TestMap source = sourceBuilder.build();
        assertMapValuesSet(source);
        TestMap.Builder destination = TestMap.newBuilder();
        copyMapValues(source, destination);
        assertMapValuesSet(destination.build());
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
        setMapValues(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(message.getSerializedSize(), message.toByteString().size());
        message = TestMap.parser().parseFrom(message.toByteString());
        assertMapValuesSet(message);
        builder = message.toBuilder();
        updateMapValues(builder);
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
        setMapValues(builder);
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
    }

    public void testUnknownEnumValues() throws Exception {
        TestUnknownEnumValue.Builder builder = TestUnknownEnumValue.newBuilder().putInt32ToInt32Field(1, 1).putInt32ToInt32Field(2, 54321);
        ByteString data = builder.build().toByteString();
        TestMap message = TestMap.parseFrom(data);
        // Entries with unknown enum values will be stored into UnknownFieldSet so
        // there is only one entry in the map.
        TestCase.assertEquals(1, message.getInt32ToEnumField().size());
        TestCase.assertEquals(BAR, message.getInt32ToEnumField().get(1));
        // Serializing and parsing should preserve the unknown entry.
        data = message.toByteString();
        TestUnknownEnumValue messageWithUnknownEnums = TestUnknownEnumValue.parseFrom(data);
        TestCase.assertEquals(2, messageWithUnknownEnums.getInt32ToInt32Field().size());
        TestCase.assertEquals(1, messageWithUnknownEnums.getInt32ToInt32Field().get(1).intValue());
        TestCase.assertEquals(54321, messageWithUnknownEnums.getInt32ToInt32Field().get(2).intValue());
    }

    public void testIterationOrder() throws Exception {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValues(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(Arrays.asList("1", "2", "3"), new ArrayList<String>(message.getStringToInt32Field().keySet()));
    }

    public void testGetMap() {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValues(builder);
        TestMap message = builder.build();
        TestCase.assertEquals(message.getStringToInt32Field(), message.getStringToInt32FieldMap());
        TestCase.assertEquals(message.getInt32ToBytesField(), message.getInt32ToBytesFieldMap());
        TestCase.assertEquals(message.getInt32ToEnumField(), message.getInt32ToEnumFieldMap());
        TestCase.assertEquals(message.getInt32ToMessageField(), message.getInt32ToMessageFieldMap());
    }

    public void testContains() {
        TestMap.Builder builder = TestMap.newBuilder();
        setMapValues(builder);
        assertMapContainsSetValues(builder);
        assertMapContainsSetValues(builder.build());
    }

    public void testCount() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapCounts(0, builder);
        setMapValues(builder);
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
        setMapValues(builder);
        doTestGetOrDefault(builder);
        doTestGetOrDefault(builder.build());
    }

    public void testGetOrThrow() {
        TestMap.Builder builder = TestMap.newBuilder();
        assertMapCounts(0, builder);
        setMapValues(builder);
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
        setMapValues(builder);
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
}

