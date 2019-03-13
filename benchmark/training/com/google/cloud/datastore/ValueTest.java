/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import Value.BaseBuilder;
import ValueType.BLOB;
import ValueType.BOOLEAN;
import ValueType.DOUBLE;
import ValueType.ENTITY;
import ValueType.KEY;
import ValueType.LAT_LNG;
import ValueType.LIST;
import ValueType.LONG;
import ValueType.NULL;
import ValueType.RAW_VALUE;
import ValueType.STRING;
import ValueType.TIMESTAMP;
import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ValueTest {
    private static final Key KEY = Key.newBuilder("ds", "kind", 1).build();

    private static final Blob BLOB = Blob.copyFrom(new byte[]{  });

    private static final Timestamp TIMESTAMP = Timestamp.now();

    private static final Entity ENTITY = Entity.newBuilder(ValueTest.KEY).set("FOO", "BAR").build();

    private static final NullValue NULL_VALUE = NullValue.of();

    private static final StringValue STRING_VALUE = StringValue.of("hello");

    private static final RawValue RAW_VALUE = RawValue.of(ValueTest.STRING_VALUE.toPb());

    private static final LatLngValue LAT_LNG_VALUE = LatLngValue.of(new LatLng(37.422035, (-122.084124)));

    private static final ImmutableMap<ValueType, Object[]> TYPES = ImmutableMap.<ValueType, Object[]>builder().put(NULL, new Object[]{ NullValue.class, ValueTest.NULL_VALUE.get() }).put(ValueType.KEY, new Object[]{ KeyValue.class, ValueTest.KEY }).put(ValueType.BLOB, new Object[]{ BlobValue.class, ValueTest.BLOB }).put(BOOLEAN, new Object[]{ BooleanValue.class, Boolean.TRUE }).put(ValueType.TIMESTAMP, new Object[]{ TimestampValue.class, ValueTest.TIMESTAMP }).put(DOUBLE, new Object[]{ DoubleValue.class, 1.25 }).put(ValueType.ENTITY, new Object[]{ EntityValue.class, ValueTest.ENTITY }).put(LIST, new Object[]{ ListValue.class, ImmutableList.of(ValueTest.NULL_VALUE, ValueTest.STRING_VALUE, ValueTest.RAW_VALUE) }).put(LONG, new Object[]{ LongValue.class, 123L }).put(ValueType.RAW_VALUE, new Object[]{ RawValue.class, ValueTest.RAW_VALUE.get() }).put(LAT_LNG, new Object[]{ LatLngValue.class, ValueTest.LAT_LNG_VALUE.get() }).put(STRING, new Object[]{ StringValue.class, ValueTest.STRING_VALUE.get() }).build();

    private ImmutableMap<ValueType, Value<?>> typeToValue;

    @SuppressWarnings("rawtypes")
    private class TestBuilder extends BaseBuilder<Set, Value<Set>, ValueTest.TestBuilder> {
        TestBuilder() {
            super(LIST);
        }

        @SuppressWarnings({ "unchecked" })
        @Override
        public Value<Set> build() {
            return new Value(this) {
                @Override
                public ValueTest.TestBuilder toBuilder() {
                    return mergeFrom(this);
                }
            };
        }
    }

    @Test
    public void testType() throws Exception {
        for (Map.Entry<ValueType, Value<?>> entry : typeToValue.entrySet()) {
            Assert.assertEquals(entry.getKey(), entry.getValue().getType());
        }
    }

    @Test
    public void testExcludeFromIndexes() throws Exception {
        for (Map.Entry<ValueType, Value<?>> entry : typeToValue.entrySet()) {
            Assert.assertFalse(entry.getValue().excludeFromIndexes());
        }
        ValueTest.TestBuilder builder = new ValueTest.TestBuilder();
        Assert.assertFalse(builder.build().excludeFromIndexes());
        Assert.assertTrue(setExcludeFromIndexes(true).build().excludeFromIndexes());
        Assert.assertFalse(setExcludeFromIndexes(false).build().excludeFromIndexes());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testMeaning() throws Exception {
        ValueTest.TestBuilder builder = new ValueTest.TestBuilder();
        Assert.assertEquals(10, setMeaning(10).build().getMeaning());
    }

    @Test
    public void testGet() throws Exception {
        for (Map.Entry<ValueType, Value<?>> entry : typeToValue.entrySet()) {
            ValueType valueType = entry.getKey();
            Value<?> value = entry.getValue();
            Assert.assertEquals(ValueTest.TYPES.get(valueType)[1], value.get());
        }
        ValueTest.TestBuilder builder = new ValueTest.TestBuilder();
        Set<String> value = Collections.singleton("bla");
        Assert.assertEquals(value, set(value).build().get());
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testToBuilder() throws Exception {
        Set<String> content = Collections.singleton("bla");
        @SuppressWarnings("rawtypes")
        ValueBuilder builder = new ValueTest.TestBuilder();
        setExcludeFromIndexes(true);
        Value<?> value = builder.build();
        builder = value.toBuilder();
        Assert.assertEquals(1, value.getMeaning());
        Assert.assertTrue(value.excludeFromIndexes());
        Assert.assertEquals(LIST, value.getType());
        Assert.assertEquals(content, value.get());
        Assert.assertEquals(value, builder.build());
    }
}

