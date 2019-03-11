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


import ListValue.Builder;
import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ListValueTest {
    private static final List<Value<?>> CONTENT = ImmutableList.of(NullValue.of(), StringValue.of("foo"));

    private static final String STRING1 = "string1";

    private static final String STRING2 = "string2";

    private static final long LONG1 = 1L;

    private static final long LONG2 = 2L;

    private static final double DOUBLE1 = 1.0;

    private static final double DOUBLE2 = 2.0;

    private static final boolean BOOLEAN1 = true;

    private static final boolean BOOLEAN2 = false;

    private static final Timestamp TIMESTAMP1 = Timestamp.ofTimeMicroseconds(1);

    private static final Timestamp TIMESTAMP2 = Timestamp.ofTimeMicroseconds(2);

    private static final LatLng LATLNG1 = LatLng.of(ListValueTest.DOUBLE1, ListValueTest.DOUBLE2);

    private static final LatLng LATLNG2 = LatLng.of(ListValueTest.DOUBLE2, ListValueTest.DOUBLE1);

    private static final Key KEY1 = Key.newBuilder("project", "kind", "name1").build();

    private static final Key KEY2 = Key.newBuilder("project", "kind", "name2").build();

    private static final FullEntity<Key> ENTITY1 = FullEntity.newBuilder(ListValueTest.KEY1).build();

    private static final FullEntity<Key> ENTITY2 = FullEntity.newBuilder(ListValueTest.KEY2).build();

    private static final Blob BLOB1 = Blob.copyFrom(new byte[]{ 13, 14, 10, 13 });

    private static final Blob BLOB2 = Blob.copyFrom(new byte[]{ 11, 0, 0, 0 });

    @Test
    public void testToBuilder() throws Exception {
        ListValue value = ListValue.of(ListValueTest.CONTENT);
        Assert.assertEquals(value, value.toBuilder().build());
    }

    @Test
    public void testOf() throws Exception {
        ListValue value = ListValue.of(ListValueTest.CONTENT);
        Assert.assertEquals(ListValueTest.CONTENT, value.get());
        Assert.assertFalse(value.excludeFromIndexes());
        value = ListValue.of(Collections.<Value<?>>emptyList());
        Assert.assertEquals(Collections.<Value<?>>emptyList(), value.get());
        Assert.assertFalse(value.excludeFromIndexes());
        value = ListValue.of(ListValueTest.STRING1);
        Assert.assertEquals(ImmutableList.of(StringValue.of(ListValueTest.STRING1)), value.get());
        value = ListValue.of(ListValueTest.STRING1, ListValueTest.STRING2);
        Assert.assertEquals(ImmutableList.of(StringValue.of(ListValueTest.STRING1), StringValue.of(ListValueTest.STRING2)), value.get());
        value = ListValue.of(ListValueTest.LONG1);
        Assert.assertEquals(ImmutableList.of(LongValue.of(ListValueTest.LONG1)), value.get());
        value = ListValue.of(ListValueTest.LONG1, ListValueTest.LONG2);
        Assert.assertEquals(ImmutableList.of(LongValue.of(ListValueTest.LONG1), LongValue.of(ListValueTest.LONG2)), value.get());
        value = ListValue.of(ListValueTest.DOUBLE1);
        Assert.assertEquals(ImmutableList.of(DoubleValue.of(ListValueTest.DOUBLE1)), value.get());
        value = ListValue.of(ListValueTest.DOUBLE1, ListValueTest.DOUBLE2);
        Assert.assertEquals(ImmutableList.of(DoubleValue.of(ListValueTest.DOUBLE1), DoubleValue.of(ListValueTest.DOUBLE2)), value.get());
        value = ListValue.of(ListValueTest.BOOLEAN1);
        Assert.assertEquals(ImmutableList.of(BooleanValue.of(ListValueTest.BOOLEAN1)), value.get());
        value = ListValue.of(ListValueTest.BOOLEAN1, ListValueTest.BOOLEAN2);
        Assert.assertEquals(ImmutableList.of(BooleanValue.of(ListValueTest.BOOLEAN1), BooleanValue.of(ListValueTest.BOOLEAN2)), value.get());
        value = ListValue.of(ListValueTest.TIMESTAMP1);
        Assert.assertEquals(ImmutableList.of(TimestampValue.of(ListValueTest.TIMESTAMP1)), value.get());
        value = ListValue.of(ListValueTest.TIMESTAMP1, ListValueTest.TIMESTAMP2);
        Assert.assertEquals(ImmutableList.of(TimestampValue.of(ListValueTest.TIMESTAMP1), TimestampValue.of(ListValueTest.TIMESTAMP2)), value.get());
        value = ListValue.of(ListValueTest.LATLNG1);
        Assert.assertEquals(ImmutableList.of(LatLngValue.of(ListValueTest.LATLNG1)), value.get());
        value = ListValue.of(ListValueTest.LATLNG1, ListValueTest.LATLNG2);
        Assert.assertEquals(ImmutableList.of(LatLngValue.of(ListValueTest.LATLNG1), LatLngValue.of(ListValueTest.LATLNG2)), value.get());
        value = ListValue.of(ListValueTest.KEY1);
        Assert.assertEquals(ImmutableList.of(KeyValue.of(ListValueTest.KEY1)), value.get());
        value = ListValue.of(ListValueTest.KEY1, ListValueTest.KEY2);
        Assert.assertEquals(ImmutableList.of(KeyValue.of(ListValueTest.KEY1), KeyValue.of(ListValueTest.KEY2)), value.get());
        value = ListValue.of(ListValueTest.ENTITY1);
        Assert.assertEquals(ImmutableList.of(EntityValue.of(ListValueTest.ENTITY1)), value.get());
        value = ListValue.of(ListValueTest.ENTITY1, ListValueTest.ENTITY2);
        Assert.assertEquals(ImmutableList.of(EntityValue.of(ListValueTest.ENTITY1), EntityValue.of(ListValueTest.ENTITY2)), value.get());
        value = ListValue.of(ListValueTest.BLOB1);
        Assert.assertEquals(ImmutableList.of(BlobValue.of(ListValueTest.BLOB1)), value.get());
        value = ListValue.of(ListValueTest.BLOB1, ListValueTest.BLOB2);
        Assert.assertEquals(ImmutableList.of(BlobValue.of(ListValueTest.BLOB1), BlobValue.of(ListValueTest.BLOB2)), value.get());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testBuilder() throws Exception {
        ListValue.Builder builder = ListValue.newBuilder().set(ListValueTest.CONTENT);
        ListValue value = builder.setMeaning(1).setExcludeFromIndexes(true).build();
        Assert.assertEquals(ListValueTest.CONTENT, value.get());
        Assert.assertEquals(1, value.getMeaning());
        Assert.assertTrue(value.excludeFromIndexes());
        builder = ListValue.newBuilder();
        for (Value<?> v : ListValueTest.CONTENT) {
            builder.addValue(v);
        }
        Assert.assertEquals(ListValueTest.CONTENT, builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        Assert.assertEquals(Collections.<Value<?>>emptyList(), builder.build().get());
        builder = builder.addValue(ListValueTest.STRING1);
        Assert.assertEquals(ImmutableList.of(StringValue.of(ListValueTest.STRING1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.STRING1, ListValueTest.STRING2);
        Assert.assertEquals(ImmutableList.of(StringValue.of(ListValueTest.STRING1), StringValue.of(ListValueTest.STRING2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.LONG1);
        Assert.assertEquals(ImmutableList.of(LongValue.of(ListValueTest.LONG1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.LONG1, ListValueTest.LONG2);
        Assert.assertEquals(ImmutableList.of(LongValue.of(ListValueTest.LONG1), LongValue.of(ListValueTest.LONG2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.DOUBLE1);
        Assert.assertEquals(ImmutableList.of(DoubleValue.of(ListValueTest.DOUBLE1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.DOUBLE1, ListValueTest.DOUBLE2);
        Assert.assertEquals(ImmutableList.of(DoubleValue.of(ListValueTest.DOUBLE1), DoubleValue.of(ListValueTest.DOUBLE2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.BOOLEAN1);
        Assert.assertEquals(ImmutableList.of(BooleanValue.of(ListValueTest.BOOLEAN1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.BOOLEAN1, ListValueTest.BOOLEAN2);
        Assert.assertEquals(ImmutableList.of(BooleanValue.of(ListValueTest.BOOLEAN1), BooleanValue.of(ListValueTest.BOOLEAN2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.TIMESTAMP1);
        Assert.assertEquals(ImmutableList.of(TimestampValue.of(ListValueTest.TIMESTAMP1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.TIMESTAMP1, ListValueTest.TIMESTAMP2);
        Assert.assertEquals(ImmutableList.of(TimestampValue.of(ListValueTest.TIMESTAMP1), TimestampValue.of(ListValueTest.TIMESTAMP2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.LATLNG1);
        Assert.assertEquals(ImmutableList.of(LatLngValue.of(ListValueTest.LATLNG1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.LATLNG1, ListValueTest.LATLNG2);
        Assert.assertEquals(ImmutableList.of(LatLngValue.of(ListValueTest.LATLNG1), LatLngValue.of(ListValueTest.LATLNG2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.KEY1);
        Assert.assertEquals(ImmutableList.of(KeyValue.of(ListValueTest.KEY1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.KEY1, ListValueTest.KEY2);
        Assert.assertEquals(ImmutableList.of(KeyValue.of(ListValueTest.KEY1), KeyValue.of(ListValueTest.KEY2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.ENTITY1);
        Assert.assertEquals(ImmutableList.of(EntityValue.of(ListValueTest.ENTITY1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.ENTITY1, ListValueTest.ENTITY2);
        Assert.assertEquals(ImmutableList.of(EntityValue.of(ListValueTest.ENTITY1), EntityValue.of(ListValueTest.ENTITY2)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.BLOB1);
        Assert.assertEquals(ImmutableList.of(BlobValue.of(ListValueTest.BLOB1)), builder.build().get());
        builder = builder.set(Collections.<Value<?>>emptyList());
        builder = builder.addValue(ListValueTest.BLOB1, ListValueTest.BLOB2);
        Assert.assertEquals(ImmutableList.of(BlobValue.of(ListValueTest.BLOB1), BlobValue.of(ListValueTest.BLOB2)), builder.build().get());
    }
}

