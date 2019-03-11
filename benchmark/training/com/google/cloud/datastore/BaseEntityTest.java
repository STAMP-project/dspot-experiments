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


import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class BaseEntityTest {
    private static final Blob BLOB = Blob.copyFrom(new byte[]{ 1, 2 });

    private static final Timestamp TIMESTAMP = Timestamp.now();

    private static final LatLng LAT_LNG = new LatLng(37.422035, (-122.084124));

    private static final Key KEY = Key.newBuilder("ds1", "k1", "n1").build();

    private static final Entity ENTITY = set("name", "foo").build();

    private static final IncompleteKey INCOMPLETE_KEY = IncompleteKey.newBuilder("ds1", "k1").build();

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY = Entity.newBuilder(BaseEntityTest.INCOMPLETE_KEY).build();

    private BaseEntityTest.Builder builder;

    private class Builder extends BaseEntity.Builder<Key, BaseEntityTest.Builder> {
        @Override
        public BaseEntity<Key> build() {
            return new BaseEntity<Key>(this) {};
        }
    }

    @Test
    public void testContains() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertTrue(entity.contains("list1"));
        Assert.assertFalse(entity.contains("bla"));
        entity = clear().build();
        Assert.assertFalse(entity.contains("list1"));
    }

    @Test
    public void testGetValue() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BlobValue.of(BaseEntityTest.BLOB), entity.getValue("blob"));
    }

    @Test(expected = DatastoreException.class)
    public void testGetValueNotFound() throws Exception {
        BaseEntity<Key> entity = clear().build();
        entity.getValue("blob");
    }

    @Test
    public void testIsNull() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertTrue(entity.isNull("null"));
        Assert.assertFalse(entity.isNull("blob"));
        entity = setNull("blob").build();
        Assert.assertTrue(entity.isNull("blob"));
    }

    @Test(expected = DatastoreException.class)
    public void testIsNullNotFound() throws Exception {
        BaseEntity<Key> entity = clear().build();
        entity.isNull("null");
    }

    @Test
    public void testGetString() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals("hello world", entity.getString("string"));
        Assert.assertEquals("bla", entity.getString("stringValue"));
        entity = set("string", "foo").build();
        Assert.assertEquals("foo", entity.getString("string"));
    }

    @Test
    public void testGetLong() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(125, entity.getLong("long"));
        entity = builder.set("long", LongValue.of(10)).build();
        Assert.assertEquals(10, entity.getLong("long"));
    }

    @Test
    public void testGetDouble() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(1.25, entity.getDouble("double"), 0);
        entity = builder.set("double", DoubleValue.of(10)).build();
        Assert.assertEquals(10, entity.getDouble("double"), 0);
    }

    @Test
    public void testGetBoolean() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertTrue(entity.getBoolean("boolean"));
        entity = builder.set("boolean", BooleanValue.of(false)).build();
        Assert.assertFalse(entity.getBoolean("boolean"));
    }

    @Test
    public void testGetTimestamp() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BaseEntityTest.TIMESTAMP, entity.getTimestamp("timestamp"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (-1));
        Timestamp timestamp = Timestamp.of(cal.getTime());
        entity = builder.set("timestamp", TimestampValue.of(timestamp)).build();
        Assert.assertEquals(timestamp, entity.getTimestamp("timestamp"));
    }

    @Test
    public void testGetLatLng() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BaseEntityTest.LAT_LNG, entity.getLatLng("latLng"));
    }

    @Test
    public void testGetKey() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BaseEntityTest.KEY, entity.getKey("key"));
        Key key = Key.newBuilder(BaseEntityTest.KEY).setName("BLA").build();
        entity = builder.set("key", key).build();
        Assert.assertEquals(key, entity.getKey("key"));
    }

    @Test
    public void testGetEntity() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BaseEntityTest.ENTITY, entity.getEntity("entity"));
        Assert.assertEquals(BaseEntityTest.PARTIAL_ENTITY, entity.getEntity("partialEntity"));
        entity = builder.set("entity", EntityValue.of(BaseEntityTest.PARTIAL_ENTITY)).build();
        Assert.assertEquals(BaseEntityTest.PARTIAL_ENTITY, entity.getEntity("entity"));
    }

    @Test
    public void testGetList() throws Exception {
        BaseEntity<Key> entity = builder.build();
        List<? extends Value<?>> list = entity.getList("list1");
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(NullValue.of(), list.get(0));
        Assert.assertEquals("foo", get());
        Assert.assertEquals(BaseEntityTest.LAT_LNG, get());
        list = entity.getList("list2");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(Long.valueOf(10), get());
        Assert.assertEquals(Double.valueOf(2), get());
        list = entity.getList("list3");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(Boolean.TRUE, get());
        entity = builder.set("list1", ListValue.of(list)).build();
        Assert.assertEquals(list, entity.getList("list1"));
        List<Value<?>> stringList = entity.getList("stringList");
        Assert.assertEquals(ImmutableList.of(StringValue.of("s1"), StringValue.of("s2"), StringValue.of("s3")), stringList);
        List<Value<Double>> doubleList = entity.getList("doubleList");
        Assert.assertEquals(ImmutableList.of(DoubleValue.of(12.3), DoubleValue.of(4.56), DoubleValue.of(0.789)), doubleList);
        List<EntityValue> entityList = entity.getList("entityList");
        Assert.assertEquals(ImmutableList.of(EntityValue.of(BaseEntityTest.ENTITY), EntityValue.of(BaseEntityTest.PARTIAL_ENTITY)), entityList);
    }

    @Test
    public void testGetBlob() throws Exception {
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BaseEntityTest.BLOB, entity.getBlob("blob"));
        Blob blob = Blob.copyFrom(new byte[]{  });
        entity = builder.set("blob", BlobValue.of(blob)).build();
        Assert.assertEquals(blob, entity.getBlob("blob"));
    }

    @Test
    public void testNames() throws Exception {
        Set<String> names = ImmutableSet.<String>builder().add("string", "stringValue", "boolean", "double", "long", "list1", "list2", "list3").add("entity", "partialEntity", "null", "timestamp", "blob", "key", "blobList").add("booleanList", "timestampList", "doubleList", "keyList", "entityList", "stringList").add("longList", "latLng", "latLngList").build();
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(names, entity.getNames());
    }

    @Test
    public void testKey() throws Exception {
        builder.setKey(BaseEntityTest.KEY);
        BaseEntity<Key> entity = builder.build();
        Assert.assertEquals(BaseEntityTest.KEY, entity.getKey());
    }
}

