/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.objectbox.query;


import StringOrder.CASE_SENSITIVE;
import io.objectbox.TestEntity;
import io.objectbox.TestEntityCursor;
import io.objectbox.TestEntity_;
import io.objectbox.exception.DbException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class PropertyQueryTest extends AbstractQueryTest {
    @Test
    public void testFindStrings() {
        putTestEntity(null, 1000);
        putTestEntity("BAR", 100);
        putTestEntitiesStrings();
        putTestEntity("banana", 101);
        Query<TestEntity> query = box.query().startsWith(TestEntity_.simpleString, "b").build();
        String[] result = query.property(TestEntity_.simpleString).findStrings();
        Assert.assertEquals(5, result.length);
        Assert.assertEquals("BAR", result[0]);
        Assert.assertEquals("banana", result[1]);
        Assert.assertEquals("bar", result[2]);
        Assert.assertEquals("banana milk shake", result[3]);
        Assert.assertEquals("banana", result[4]);
        result = query.property(TestEntity_.simpleString).distinct().findStrings();
        Assert.assertEquals(3, result.length);
        List<String> list = Arrays.asList(result);
        Assert.assertTrue(list.contains("BAR"));
        Assert.assertTrue(list.contains("banana"));
        Assert.assertTrue(list.contains("banana milk shake"));
        result = query.property(TestEntity_.simpleString).distinct(CASE_SENSITIVE).findStrings();
        Assert.assertEquals(4, result.length);
        list = Arrays.asList(result);
        Assert.assertTrue(list.contains("BAR"));
        Assert.assertTrue(list.contains("banana"));
        Assert.assertTrue(list.contains("bar"));
        Assert.assertTrue(list.contains("banana milk shake"));
    }

    @Test
    public void testFindStrings_nullValue() {
        putTestEntity(null, 3);
        putTestEntitiesStrings();
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleInt, 3).build();
        String[] strings = query.property(TestEntity_.simpleString).findStrings();
        Assert.assertEquals(1, strings.length);
        Assert.assertEquals("bar", strings[0]);
        strings = query.property(TestEntity_.simpleString).nullValue("****").findStrings();
        Assert.assertEquals(2, strings.length);
        Assert.assertEquals("****", strings[0]);
        Assert.assertEquals("bar", strings[1]);
        putTestEntity(null, 3);
        Assert.assertEquals(3, query.property(TestEntity_.simpleString).nullValue("****").findStrings().length);
        Assert.assertEquals(2, query.property(TestEntity_.simpleString).nullValue("****").distinct().findStrings().length);
    }

    @Test
    public void testFindInts_nullValue() {
        putTestEntity(null, 1);
        TestEntityCursor.INT_NULL_HACK = true;
        try {
            putTestEntities(3);
        } finally {
            TestEntityCursor.INT_NULL_HACK = false;
        }
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleLong, 1001).build();
        int[] results = query.property(TestEntity_.simpleInt).findInts();
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(1, results[0]);
        results = query.property(TestEntity_.simpleInt).nullValue((-1977)).findInts();
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals((-1977), results[1]);
    }

    // TODO add null tests for other types
    @Test(expected = IllegalArgumentException.class)
    public void testFindStrings_wrongPropertyType() {
        putTestEntitiesStrings();
        box.query().build().property(TestEntity_.simpleInt).findStrings();
    }

    @Test
    public void testFindString() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        PropertyQuery propertyQuery = query.property(TestEntity_.simpleString);
        Assert.assertNull(propertyQuery.findString());
        Assert.assertNull(propertyQuery.reset().unique().findString());
        putTestEntities(5);
        Assert.assertEquals("foo3", propertyQuery.reset().findString());
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        propertyQuery = query.property(TestEntity_.simpleString);
        Assert.assertEquals("foo5", propertyQuery.reset().unique().findString());
        putTestEntity(null, 6);
        putTestEntity(null, 7);
        query.setParameter(TestEntity_.simpleLong, 1005);
        Assert.assertEquals("nope", propertyQuery.reset().distinct().nullValue("nope").unique().findString());
    }

    @Test(expected = DbException.class)
    public void testFindString_uniqueFails() {
        putTestEntity("foo", 1);
        putTestEntity("foo", 2);
        box.query().build().property(TestEntity_.simpleString).unique().findString();
    }

    @Test
    public void testFindLongs() {
        putTestEntities(5);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        long[] result = query.property(TestEntity_.simpleLong).findLongs();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(1003, result[0]);
        Assert.assertEquals(1004, result[1]);
        Assert.assertEquals(1005, result[2]);
        putTestEntity(null, 5);
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(2, query.property(TestEntity_.simpleLong).findLongs().length);
        Assert.assertEquals(1, query.property(TestEntity_.simpleLong).distinct().findLongs().length);
    }

    @Test
    public void testFindLong() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleLong).findLong());
        Assert.assertNull(query.property(TestEntity_.simpleLong).findLong());
        putTestEntities(5);
        Assert.assertEquals(1003, ((long) (query.property(TestEntity_.simpleLong).findLong())));
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(1005, ((long) (query.property(TestEntity_.simpleLong).distinct().findLong())));
    }

    @Test(expected = DbException.class)
    public void testFindLong_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleLong).unique().findLong();
    }

    @Test
    public void testFindInt() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleInt).findInt());
        Assert.assertNull(query.property(TestEntity_.simpleInt).unique().findInt());
        putTestEntities(5);
        Assert.assertEquals(3, ((int) (query.property(TestEntity_.simpleInt).findInt())));
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(5, ((int) (query.property(TestEntity_.simpleInt).distinct().unique().findInt())));
        TestEntityCursor.INT_NULL_HACK = true;
        try {
            putTestEntity(null, 6);
        } finally {
            TestEntityCursor.INT_NULL_HACK = false;
        }
        query.setParameter(TestEntity_.simpleLong, 1005);
        Assert.assertEquals((-99), ((int) (query.property(TestEntity_.simpleInt).nullValue((-99)).unique().findInt())));
    }

    @Test(expected = DbException.class)
    public void testFindInt_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleInt).unique().findInt();
    }

    @Test
    public void testFindShort() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleShort).findShort());
        Assert.assertNull(query.property(TestEntity_.simpleShort).unique().findShort());
        putTestEntities(5);
        Assert.assertEquals(103, ((short) (query.property(TestEntity_.simpleShort).findShort())));
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(105, ((short) (query.property(TestEntity_.simpleShort).distinct().unique().findShort())));
    }

    @Test(expected = DbException.class)
    public void testFindShort_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleShort).unique().findShort();
    }

    // TODO add test for findChar
    @Test
    public void testFindByte() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleByte).findByte());
        Assert.assertNull(query.property(TestEntity_.simpleByte).unique().findByte());
        putTestEntities(5);
        Assert.assertEquals(((byte) (13)), ((byte) (query.property(TestEntity_.simpleByte).findByte())));
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(((byte) (15)), ((byte) (query.property(TestEntity_.simpleByte).distinct().unique().findByte())));
    }

    @Test(expected = DbException.class)
    public void testFindByte_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleByte).unique().findByte();
    }

    @Test
    public void testFindBoolean() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleBoolean).findBoolean());
        Assert.assertNull(query.property(TestEntity_.simpleBoolean).unique().findBoolean());
        putTestEntities(5);
        Assert.assertFalse(query.property(TestEntity_.simpleBoolean).findBoolean());
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertFalse(query.property(TestEntity_.simpleBoolean).distinct().unique().findBoolean());
    }

    @Test(expected = DbException.class)
    public void testFindBoolean_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleBoolean).unique().findBoolean();
    }

    @Test
    public void testFindFloat() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleFloat).findFloat());
        Assert.assertNull(query.property(TestEntity_.simpleFloat).unique().findFloat());
        putTestEntities(5);
        Assert.assertEquals(200.3F, query.property(TestEntity_.simpleFloat).findFloat(), 0.001F);
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(200.5F, query.property(TestEntity_.simpleFloat).distinct().unique().findFloat(), 0.001F);
    }

    @Test(expected = DbException.class)
    public void testFindFloat_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleFloat).unique().findFloat();
    }

    @Test
    public void testFindDouble() {
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleLong, 1002).build();
        Assert.assertNull(query.property(TestEntity_.simpleDouble).findDouble());
        Assert.assertNull(query.property(TestEntity_.simpleDouble).unique().findDouble());
        putTestEntities(5);
        Assert.assertEquals(2000.03, query.property(TestEntity_.simpleDouble).findDouble(), 0.001);
        query = box.query().greater(TestEntity_.simpleLong, 1004).build();
        Assert.assertEquals(2000.05, query.property(TestEntity_.simpleDouble).distinct().unique().findDouble(), 0.001);
    }

    @Test(expected = DbException.class)
    public void testFindDouble_uniqueFails() {
        putTestEntity(null, 1);
        putTestEntity(null, 1);
        box.query().build().property(TestEntity_.simpleDouble).unique().findDouble();
    }

    @Test
    public void testFindInts() {
        putTestEntities(5);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2).build();
        int[] result = query.property(TestEntity_.simpleInt).findInts();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(3, result[0]);
        Assert.assertEquals(4, result[1]);
        Assert.assertEquals(5, result[2]);
        putTestEntity(null, 5);
        query = box.query().greater(TestEntity_.simpleInt, 4).build();
        Assert.assertEquals(2, query.property(TestEntity_.simpleInt).findInts().length);
        Assert.assertEquals(1, query.property(TestEntity_.simpleInt).distinct().findInts().length);
    }

    @Test
    public void testFindShorts() {
        putTestEntities(5);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2).build();
        short[] result = query.property(TestEntity_.simpleShort).findShorts();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(103, result[0]);
        Assert.assertEquals(104, result[1]);
        Assert.assertEquals(105, result[2]);
        putTestEntity(null, 5);
        query = box.query().greater(TestEntity_.simpleInt, 4).build();
        Assert.assertEquals(2, query.property(TestEntity_.simpleShort).findShorts().length);
        Assert.assertEquals(1, query.property(TestEntity_.simpleShort).distinct().findShorts().length);
    }

    // TODO @Test for findChars (no char property in entity)
    @Test
    public void testFindFloats() {
        putTestEntities(5);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2).build();
        float[] result = query.property(TestEntity_.simpleFloat).findFloats();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(200.3F, result[0], 1.0E-4F);
        Assert.assertEquals(200.4F, result[1], 1.0E-4F);
        Assert.assertEquals(200.5F, result[2], 1.0E-4F);
        putTestEntity(null, 5);
        query = box.query().greater(TestEntity_.simpleInt, 4).build();
        Assert.assertEquals(2, query.property(TestEntity_.simpleFloat).findFloats().length);
        Assert.assertEquals(1, query.property(TestEntity_.simpleFloat).distinct().findFloats().length);
    }

    @Test
    public void testFindDoubles() {
        putTestEntities(5);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2).build();
        double[] result = query.property(TestEntity_.simpleDouble).findDoubles();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(2000.03, result[0], 1.0E-4);
        Assert.assertEquals(2000.04, result[1], 1.0E-4);
        Assert.assertEquals(2000.05, result[2], 1.0E-4);
        putTestEntity(null, 5);
        query = box.query().greater(TestEntity_.simpleInt, 4).build();
        Assert.assertEquals(2, query.property(TestEntity_.simpleDouble).findDoubles().length);
        Assert.assertEquals(1, query.property(TestEntity_.simpleDouble).distinct().findDoubles().length);
    }

    @Test
    public void testFindBytes() {
        putTestEntities(5);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleByte, 12).build();
        byte[] result = query.property(TestEntity_.simpleByte).findBytes();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(13, result[0]);
        Assert.assertEquals(14, result[1]);
        Assert.assertEquals(15, result[2]);
        putTestEntity(null, 5);
        query = box.query().greater(TestEntity_.simpleByte, 14).build();
        Assert.assertEquals(2, query.property(TestEntity_.simpleByte).findBytes().length);
        Assert.assertEquals(1, query.property(TestEntity_.simpleByte).distinct().findBytes().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindLongs_wrongPropertyType() {
        putTestEntitiesStrings();
        box.query().build().property(TestEntity_.simpleInt).findLongs();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindInts_wrongPropertyType() {
        putTestEntitiesStrings();
        box.query().build().property(TestEntity_.simpleLong).findInts();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindShorts_wrongPropertyType() {
        putTestEntitiesStrings();
        box.query().build().property(TestEntity_.simpleInt).findShorts();
    }

    @Test
    public void testCount() {
        putTestEntity(null, 1000);
        putTestEntity("BAR", 100);
        putTestEntitiesStrings();
        putTestEntity("banana", 101);
        Query<TestEntity> query = box.query().build();
        PropertyQuery stringQuery = query.property(TestEntity_.simpleString);
        Assert.assertEquals(8, query.count());
        Assert.assertEquals(7, stringQuery.count());
        Assert.assertEquals(6, stringQuery.distinct().count());
    }

    @Test
    public void testAggregates() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().less(TestEntity_.simpleInt, 2002).build();
        PropertyQuery intQuery = query.property(TestEntity_.simpleInt);
        PropertyQuery floatQuery = query.property(TestEntity_.simpleFloat);
        Assert.assertEquals(2000.5, intQuery.avg(), 1.0E-4);
        Assert.assertEquals(2000, intQuery.min(), 1.0E-4);
        Assert.assertEquals(400, floatQuery.minDouble(), 0.001);
        Assert.assertEquals(2001, intQuery.max(), 1.0E-4);
        Assert.assertEquals(400.1, floatQuery.maxDouble(), 0.001);
        Assert.assertEquals(4001, intQuery.sum(), 1.0E-4);
        Assert.assertEquals(800.1, floatQuery.sumDouble(), 0.001);
    }

    @Test
    public void testSumDoubleOfFloats() {
        TestEntity entity = new TestEntity();
        entity.setSimpleFloat(0);
        TestEntity entity2 = new TestEntity();
        entity2.setSimpleFloat((-2.05F));
        box.put(entity, entity2);
        double sum = box.query().build().property(TestEntity_.simpleFloat).sumDouble();
        Assert.assertEquals((-2.05), sum, 1.0E-4);
    }
}

