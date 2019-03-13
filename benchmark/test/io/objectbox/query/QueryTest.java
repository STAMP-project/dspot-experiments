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


import DebugFlags.LOG_QUERY_PARAMETERS;
import Order_.date;
import StringOrder.CASE_SENSITIVE;
import TestEntity_.simpleBoolean;
import TestEntity_.simpleInt;
import TestEntity_.simpleString;
import io.objectbox.AbstractObjectBoxTest;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.TestEntity;
import io.objectbox.TestEntity_;
import io.objectbox.TxCallback;
import io.objectbox.exception.DbExceptionListener;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.relation.MyObjectBox;
import io.objectbox.relation.Order;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;

import static QueryBuilder.CASE_SENSITIVE;
import static QueryBuilder.DESCENDING;
import static QueryBuilder.NULLS_LAST;


public class QueryTest extends AbstractQueryTest {
    @Test
    public void testBuild() {
        Query query = box.query().build();
        Assert.assertNotNull(query);
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildTwice() {
        QueryBuilder<TestEntity> queryBuilder = box.query();
        for (int i = 0; i < 2; i++) {
            // calling any builder method after build should fail
            // note: not calling all variants for different types
            queryBuilder.isNull(simpleString);
            queryBuilder.and();
            queryBuilder.notNull(simpleString);
            queryBuilder.or();
            queryBuilder.equal(simpleBoolean, true);
            queryBuilder.notEqual(simpleBoolean, true);
            queryBuilder.less(simpleInt, 42);
            queryBuilder.greater(simpleInt, 42);
            queryBuilder.between(simpleInt, 42, 43);
            queryBuilder.in(simpleInt, new int[]{ 42 });
            queryBuilder.notIn(simpleInt, new int[]{ 42 });
            queryBuilder.contains(simpleString, "42");
            queryBuilder.startsWith(simpleString, "42");
            queryBuilder.order(simpleInt);
            queryBuilder.build().find();
        }
    }

    @Test
    public void testNullNotNull() {
        List<TestEntity> scalars = putTestEntitiesScalars();
        List<TestEntity> strings = putTestEntitiesStrings();
        Assert.assertEquals(strings.size(), box.query().notNull(TestEntity_.simpleString).build().count());
        Assert.assertEquals(scalars.size(), box.query().isNull(TestEntity_.simpleString).build().count());
    }

    @Test
    public void testScalarEqual() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleInt, 2007).build();
        Assert.assertEquals(1, query.count());
        Assert.assertEquals(8, query.findFirst().getId());
        Assert.assertEquals(8, query.findUnique().getId());
        List<TestEntity> all = query.find();
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(8, all.get(0).getId());
    }

    @Test
    public void testBooleanEqual() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleBoolean, true).build();
        Assert.assertEquals(5, query.count());
        Assert.assertEquals(1, query.findFirst().getId());
        query.setParameter(TestEntity_.simpleBoolean, false);
        Assert.assertEquals(5, query.count());
        Assert.assertEquals(2, query.findFirst().getId());
    }

    @Test
    public void testNoConditions() {
        List<TestEntity> entities = putTestEntitiesScalars();
        Query<TestEntity> query = box.query().build();
        List<TestEntity> all = query.find();
        Assert.assertEquals(entities.size(), all.size());
        Assert.assertEquals(entities.size(), query.count());
    }

    @Test
    public void testScalarNotEqual() {
        List<TestEntity> entities = putTestEntitiesScalars();
        Query<TestEntity> query = box.query().notEqual(TestEntity_.simpleInt, 2007).notEqual(TestEntity_.simpleInt, 2002).build();
        Assert.assertEquals(((entities.size()) - 2), query.count());
    }

    @Test
    public void testScalarLessAndGreater() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2003).less(TestEntity_.simpleShort, 2107).build();
        Assert.assertEquals(3, query.count());
    }

    @Test
    public void testScalarBetween() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().between(TestEntity_.simpleInt, 2003, 2006).build();
        Assert.assertEquals(4, query.count());
    }

    @Test
    public void testIntIn() {
        putTestEntitiesScalars();
        int[] valuesInt = new int[]{ 1, 1, 2, 3, 2003, 2007, 2002, -1 };
        Query<TestEntity> query = box.query().in(TestEntity_.simpleInt, valuesInt).parameterAlias("int").build();
        Assert.assertEquals(3, query.count());
        int[] valuesInt2 = new int[]{ 2003 };
        query.setParameters(TestEntity_.simpleInt, valuesInt2);
        Assert.assertEquals(1, query.count());
        int[] valuesInt3 = new int[]{ 2003, 2007 };
        query.setParameters("int", valuesInt3);
        Assert.assertEquals(2, query.count());
    }

    @Test
    public void testLongIn() {
        putTestEntitiesScalars();
        long[] valuesLong = new long[]{ 1, 1, 2, 3, 3003, 3007, 3002, -1 };
        Query<TestEntity> query = box.query().in(TestEntity_.simpleLong, valuesLong).parameterAlias("long").build();
        Assert.assertEquals(3, query.count());
        long[] valuesLong2 = new long[]{ 3003 };
        query.setParameters(TestEntity_.simpleLong, valuesLong2);
        Assert.assertEquals(1, query.count());
        long[] valuesLong3 = new long[]{ 3003, 3007 };
        query.setParameters("long", valuesLong3);
        Assert.assertEquals(2, query.count());
    }

    @Test
    public void testIntNotIn() {
        putTestEntitiesScalars();
        int[] valuesInt = new int[]{ 1, 1, 2, 3, 2003, 2007, 2002, -1 };
        Query<TestEntity> query = box.query().notIn(TestEntity_.simpleInt, valuesInt).parameterAlias("int").build();
        Assert.assertEquals(7, query.count());
        int[] valuesInt2 = new int[]{ 2003 };
        query.setParameters(TestEntity_.simpleInt, valuesInt2);
        Assert.assertEquals(9, query.count());
        int[] valuesInt3 = new int[]{ 2003, 2007 };
        query.setParameters("int", valuesInt3);
        Assert.assertEquals(8, query.count());
    }

    @Test
    public void testLongNotIn() {
        putTestEntitiesScalars();
        long[] valuesLong = new long[]{ 1, 1, 2, 3, 3003, 3007, 3002, -1 };
        Query<TestEntity> query = box.query().notIn(TestEntity_.simpleLong, valuesLong).parameterAlias("long").build();
        Assert.assertEquals(7, query.count());
        long[] valuesLong2 = new long[]{ 3003 };
        query.setParameters(TestEntity_.simpleLong, valuesLong2);
        Assert.assertEquals(9, query.count());
        long[] valuesLong3 = new long[]{ 3003, 3007 };
        query.setParameters("long", valuesLong3);
        Assert.assertEquals(8, query.count());
    }

    @Test
    public void testOffsetLimit() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2002).less(TestEntity_.simpleShort, 2108).build();
        Assert.assertEquals(5, query.count());
        Assert.assertEquals(4, query.find(1, 0).size());
        Assert.assertEquals(1, query.find(4, 0).size());
        Assert.assertEquals(2, query.find(0, 2).size());
        List<TestEntity> list = query.find(1, 2);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(2004, list.get(0).getSimpleInt());
        Assert.assertEquals(2005, list.get(1).getSimpleInt());
    }

    @Test
    public void testString() {
        List<TestEntity> entities = putTestEntitiesStrings();
        int count = entities.size();
        Assert.assertEquals(1, box.query().equal(TestEntity_.simpleString, "banana").build().findUnique().getId());
        Assert.assertEquals((count - 1), box.query().notEqual(TestEntity_.simpleString, "banana").build().count());
        Assert.assertEquals(4, box.query().startsWith(TestEntity_.simpleString, "ba").endsWith(TestEntity_.simpleString, "shake").build().findUnique().getId());
        Assert.assertEquals(2, box.query().contains(TestEntity_.simpleString, "nana").build().count());
    }

    @Test
    public void testStringLess() {
        putTestEntitiesStrings();
        putTestEntity("BaNaNa Split", 100);
        Query<TestEntity> query = box.query().less(TestEntity_.simpleString, "banana juice").order(TestEntity_.simpleString).build();
        List<TestEntity> entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals("apple", entities.get(0).getSimpleString());
        Assert.assertEquals("banana", entities.get(1).getSimpleString());
        query.setParameter(TestEntity_.simpleString, "BANANA MZ");
        entities = query.find();
        Assert.assertEquals(3, entities.size());
        Assert.assertEquals("apple", entities.get(0).getSimpleString());
        Assert.assertEquals("banana", entities.get(1).getSimpleString());
        Assert.assertEquals("banana milk shake", entities.get(2).getSimpleString());
        // Case sensitive
        query = box.query().less(TestEntity_.simpleString, "BANANA", CASE_SENSITIVE).order(TestEntity_.simpleString).build();
        Assert.assertEquals(0, query.count());
        query.setParameter(TestEntity_.simpleString, "banana a");
        entities = query.find();
        Assert.assertEquals(3, entities.size());
        Assert.assertEquals("apple", entities.get(0).getSimpleString());
        Assert.assertEquals("banana", entities.get(1).getSimpleString());
        Assert.assertEquals("BaNaNa Split", entities.get(2).getSimpleString());
    }

    @Test
    public void testStringGreater() {
        putTestEntitiesStrings();
        putTestEntity("FOO", 100);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleString, "banana juice").order(TestEntity_.simpleString).build();
        List<TestEntity> entities = query.find();
        Assert.assertEquals(4, entities.size());
        Assert.assertEquals("banana milk shake", entities.get(0).getSimpleString());
        Assert.assertEquals("bar", entities.get(1).getSimpleString());
        Assert.assertEquals("FOO", entities.get(2).getSimpleString());
        Assert.assertEquals("foo bar", entities.get(3).getSimpleString());
        query.setParameter(TestEntity_.simpleString, "FO");
        entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals("FOO", entities.get(0).getSimpleString());
        Assert.assertEquals("foo bar", entities.get(1).getSimpleString());
        // Case sensitive
        query = box.query().greater(TestEntity_.simpleString, "banana", CASE_SENSITIVE).order(TestEntity_.simpleString).build();
        entities = query.find();
        Assert.assertEquals(3, entities.size());
        Assert.assertEquals("banana milk shake", entities.get(0).getSimpleString());
        Assert.assertEquals("bar", entities.get(1).getSimpleString());
        Assert.assertEquals("foo bar", entities.get(2).getSimpleString());
    }

    @Test
    public void testStringIn() {
        putTestEntitiesStrings();
        putTestEntity("BAR", 100);
        String[] values = new String[]{ "bar", "foo bar" };
        Query<TestEntity> query = box.query().in(TestEntity_.simpleString, values).order(TestEntity_.simpleString, OrderFlags.CASE_SENSITIVE).build();
        List<TestEntity> entities = query.find();
        Assert.assertEquals(3, entities.size());
        Assert.assertEquals("BAR", entities.get(0).getSimpleString());
        Assert.assertEquals("bar", entities.get(1).getSimpleString());
        Assert.assertEquals("foo bar", entities.get(2).getSimpleString());
        String[] values2 = new String[]{ "bar" };
        query.setParameters(TestEntity_.simpleString, values2);
        entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals("BAR", entities.get(0).getSimpleString());
        Assert.assertEquals("bar", entities.get(1).getSimpleString());
        // Case sensitive
        query = box.query().in(TestEntity_.simpleString, values, CASE_SENSITIVE).order(TestEntity_.simpleString).build();
        entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals("bar", entities.get(0).getSimpleString());
        Assert.assertEquals("foo bar", entities.get(1).getSimpleString());
    }

    @Test
    public void testByteArrayEqualsAndSetParameter() {
        putTestEntitiesScalars();
        byte[] value = new byte[]{ 1, 2, ((byte) (2000)) };
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleByteArray, value).parameterAlias("bytes").build();
        Assert.assertEquals(1, query.count());
        TestEntity first = query.findFirst();
        Assert.assertNotNull(first);
        Assert.assertTrue(Arrays.equals(value, first.getSimpleByteArray()));
        byte[] value2 = new byte[]{ 1, 2, ((byte) (2001)) };
        query.setParameter(TestEntity_.simpleByteArray, value2);
        Assert.assertEquals(1, query.count());
        TestEntity first2 = query.findFirst();
        Assert.assertNotNull(first2);
        Assert.assertTrue(Arrays.equals(value2, first2.getSimpleByteArray()));
        byte[] value3 = new byte[]{ 1, 2, ((byte) (2002)) };
        query.setParameter("bytes", value3);
        Assert.assertEquals(1, query.count());
        TestEntity first3 = query.findFirst();
        Assert.assertNotNull(first3);
        Assert.assertTrue(Arrays.equals(value3, first3.getSimpleByteArray()));
    }

    @Test
    public void testByteArrayLess() {
        putTestEntitiesScalars();
        byte[] value = new byte[]{ 1, 2, ((byte) (2005)) };
        Query<TestEntity> query = box.query().less(TestEntity_.simpleByteArray, value).build();
        List<TestEntity> results = query.find();
        Assert.assertEquals(5, results.size());
        // Java does not have compareTo for arrays, so just make sure its not equal to the value
        for (TestEntity result : results) {
            Assert.assertFalse(Arrays.equals(value, result.getSimpleByteArray()));
        }
    }

    @Test
    public void testByteArrayGreater() {
        putTestEntitiesScalars();
        byte[] value = new byte[]{ 1, 2, ((byte) (2005)) };
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleByteArray, value).build();
        List<TestEntity> results = query.find();
        Assert.assertEquals(4, results.size());
        // Java does not have compareTo for arrays, so just make sure its not equal to the value
        for (TestEntity result : results) {
            Assert.assertFalse(Arrays.equals(value, result.getSimpleByteArray()));
        }
    }

    @Test
    public void testScalarFloatLessAndGreater() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleFloat, 400.29F).less(TestEntity_.simpleFloat, 400.51F).build();
        Assert.assertEquals(3, query.count());
    }

    // Android JNI seems to have a limit of 512 local jobject references. Internally, we must delete those temporary
    // references when processing lists. This is the test for that.
    @Test
    public void testBigResultList() {
        List<TestEntity> entities = new ArrayList<>();
        String sameValueForAll = "schrodinger";
        for (int i = 0; i < 10000; i++) {
            TestEntity entity = createTestEntity(sameValueForAll, i);
            entities.add(entity);
        }
        box.put(entities);
        int count = entities.size();
        List<TestEntity> entitiesQueried = box.query().equal(TestEntity_.simpleString, sameValueForAll).build().find();
        Assert.assertEquals(count, entitiesQueried.size());
    }

    @Test
    public void testEqualStringOrder() {
        putTestEntitiesStrings();
        putTestEntity("BAR", 100);
        Assert.assertEquals(2, box.query().equal(TestEntity_.simpleString, "bar").build().count());
        Assert.assertEquals(1, box.query().equal(TestEntity_.simpleString, "bar", CASE_SENSITIVE).build().count());
    }

    @Test
    public void testOrder() {
        putTestEntitiesStrings();
        putTestEntity("BAR", 100);
        List<TestEntity> result = box.query().order(TestEntity_.simpleString).build().find();
        Assert.assertEquals(6, result.size());
        Assert.assertEquals("apple", result.get(0).getSimpleString());
        Assert.assertEquals("banana", result.get(1).getSimpleString());
        Assert.assertEquals("banana milk shake", result.get(2).getSimpleString());
        Assert.assertEquals("bar", result.get(3).getSimpleString());
        Assert.assertEquals("BAR", result.get(4).getSimpleString());
        Assert.assertEquals("foo bar", result.get(5).getSimpleString());
    }

    @Test
    public void testOrderDescCaseNullLast() {
        putTestEntity(null, 1000);
        putTestEntity("BAR", 100);
        putTestEntitiesStrings();
        int flags = ((CASE_SENSITIVE) | (NULLS_LAST)) | (DESCENDING);
        List<TestEntity> result = box.query().order(TestEntity_.simpleString, flags).build().find();
        Assert.assertEquals(7, result.size());
        Assert.assertEquals("foo bar", result.get(0).getSimpleString());
        Assert.assertEquals("bar", result.get(1).getSimpleString());
        Assert.assertEquals("banana milk shake", result.get(2).getSimpleString());
        Assert.assertEquals("banana", result.get(3).getSimpleString());
        Assert.assertEquals("apple", result.get(4).getSimpleString());
        Assert.assertEquals("BAR", result.get(5).getSimpleString());
        Assert.assertNull(result.get(6).getSimpleString());
    }

    @Test
    public void testRemove() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2003).build();
        Assert.assertEquals(6, query.remove());
        Assert.assertEquals(4, box.count());
    }

    @Test
    public void testFindIds() {
        putTestEntitiesScalars();
        Assert.assertEquals(10, box.query().build().findIds().length);
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleInt, 2006).build();
        long[] keys = query.findIds();
        Assert.assertEquals(3, keys.length);
        Assert.assertEquals(8, keys[0]);
        Assert.assertEquals(9, keys[1]);
        Assert.assertEquals(10, keys[2]);
    }

    @Test
    public void testFindIdsWithOrder() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().orderDesc(simpleInt).build();
        long[] ids = query.findIds();
        Assert.assertEquals(10, ids.length);
        Assert.assertEquals(10, ids[0]);
        Assert.assertEquals(1, ids[9]);
        ids = query.findIds(3, 2);
        Assert.assertEquals(2, ids.length);
        Assert.assertEquals(7, ids[0]);
        Assert.assertEquals(6, ids[1]);
    }

    @Test
    public void testOr() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleInt, 2007).or().equal(TestEntity_.simpleLong, 3002).build();
        List<TestEntity> entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals(3002, entities.get(0).getSimpleLong());
        Assert.assertEquals(2007, entities.get(1).getSimpleInt());
    }

    @Test(expected = IllegalStateException.class)
    public void testOr_bad1() {
        box.query().or();
    }

    @Test(expected = IllegalStateException.class)
    public void testOr_bad2() {
        box.query().equal(TestEntity_.simpleInt, 1).or().build();
    }

    @Test
    public void testAnd() {
        putTestEntitiesScalars();
        // OR precedence (wrong): {}, AND precedence (expected): 2008
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleInt, 2006).and().equal(TestEntity_.simpleInt, 2007).or().equal(TestEntity_.simpleInt, 2008).build();
        List<TestEntity> entities = query.find();
        Assert.assertEquals(1, entities.size());
        Assert.assertEquals(2008, entities.get(0).getSimpleInt());
    }

    @Test(expected = IllegalStateException.class)
    public void testAnd_bad1() {
        box.query().and();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnd_bad2() {
        box.query().equal(TestEntity_.simpleInt, 1).and().build();
    }

    @Test(expected = IllegalStateException.class)
    public void testOrAfterAnd() {
        box.query().equal(TestEntity_.simpleInt, 1).and().or().equal(TestEntity_.simpleInt, 2).build();
    }

    @Test(expected = IllegalStateException.class)
    public void testOrderAfterAnd() {
        box.query().equal(TestEntity_.simpleInt, 1).and().order(TestEntity_.simpleInt).equal(TestEntity_.simpleInt, 2).build();
    }

    @Test
    public void testSetParameterInt() {
        String versionNative = BoxStore.getVersionNative();
        String minVersion = "1.5.1-2018-06-21";
        String versionStart = versionNative.substring(0, minVersion.length());
        Assert.assertTrue(versionStart, ((versionStart.compareTo(minVersion)) >= 0));
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleInt, 2007).parameterAlias("foo").build();
        Assert.assertEquals(8, query.findUnique().getId());
        query.setParameter(TestEntity_.simpleInt, 2004);
        Assert.assertEquals(5, query.findUnique().getId());
        query.setParameter("foo", 2002);
        Assert.assertEquals(3, query.findUnique().getId());
    }

    @Test
    public void testSetParameter2Ints() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().between(TestEntity_.simpleInt, 2005, 2008).parameterAlias("foo").build();
        Assert.assertEquals(4, query.count());
        query.setParameters(TestEntity_.simpleInt, 2002, 2003);
        List<TestEntity> entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals(3, entities.get(0).getId());
        Assert.assertEquals(4, entities.get(1).getId());
        query.setParameters("foo", 2007, 2007);
        Assert.assertEquals(8, query.findUnique().getId());
    }

    @Test
    public void testSetParameterFloat() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().greater(TestEntity_.simpleFloat, 400.65).parameterAlias("foo").build();
        Assert.assertEquals(3, query.count());
        query.setParameter(TestEntity_.simpleFloat, 400.75);
        Assert.assertEquals(2, query.count());
        query.setParameter("foo", 400.85);
        Assert.assertEquals(1, query.count());
    }

    @Test
    public void testSetParameter2Floats() {
        putTestEntitiesScalars();
        Query<TestEntity> query = box.query().between(TestEntity_.simpleFloat, 400.15, 400.75).parameterAlias("foo").build();
        Assert.assertEquals(6, query.count());
        query.setParameters(TestEntity_.simpleFloat, 400.65, 400.85);
        List<TestEntity> entities = query.find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals(8, entities.get(0).getId());
        Assert.assertEquals(9, entities.get(1).getId());
        query.setParameters("foo", 400.45, 400.55);
        Assert.assertEquals(6, query.findUnique().getId());
    }

    @Test
    public void testSetParameterString() {
        putTestEntitiesStrings();
        Query<TestEntity> query = box.query().equal(TestEntity_.simpleString, "banana").parameterAlias("foo").build();
        Assert.assertEquals(1, query.findUnique().getId());
        query.setParameter(TestEntity_.simpleString, "bar");
        Assert.assertEquals(3, query.findUnique().getId());
        Assert.assertNull(query.setParameter(TestEntity_.simpleString, "not here!").findUnique());
        query.setParameter("foo", "apple");
        Assert.assertEquals(2, query.findUnique().getId());
    }

    @Test
    public void testForEach() {
        List<TestEntity> testEntities = putTestEntitiesStrings();
        final StringBuilder stringBuilder = new StringBuilder();
        box.query().startsWith(TestEntity_.simpleString, "banana").build().forEach(new QueryConsumer<TestEntity>() {
            @Override
            public void accept(TestEntity data) {
                stringBuilder.append(data.getSimpleString()).append('#');
            }
        });
        Assert.assertEquals("banana#banana milk shake#", stringBuilder.toString());
        // Verify that box does not hang on to the read-only TX by doing a put
        box.put(new TestEntity());
        Assert.assertEquals(((testEntities.size()) + 1), box.count());
    }

    @Test
    public void testForEachBreak() {
        putTestEntitiesStrings();
        final StringBuilder stringBuilder = new StringBuilder();
        box.query().startsWith(TestEntity_.simpleString, "banana").build().forEach(new QueryConsumer<TestEntity>() {
            @Override
            public void accept(TestEntity data) {
                stringBuilder.append(data.getSimpleString());
                throw new BreakForEach();
            }
        });
        Assert.assertEquals("banana", stringBuilder.toString());
    }

    @Test
    public void testForEachWithFilter() {
        putTestEntitiesStrings();
        final StringBuilder stringBuilder = new StringBuilder();
        box.query().filter(createTestFilter()).build().forEach(new QueryConsumer<TestEntity>() {
            @Override
            public void accept(TestEntity data) {
                stringBuilder.append(data.getSimpleString()).append('#');
            }
        });
        Assert.assertEquals("apple#banana milk shake#", stringBuilder.toString());
    }

    @Test
    public void testFindWithFilter() {
        putTestEntitiesStrings();
        List<TestEntity> entities = box.query().filter(createTestFilter()).build().find();
        Assert.assertEquals(2, entities.size());
        Assert.assertEquals("apple", entities.get(0).getSimpleString());
        Assert.assertEquals("banana milk shake", entities.get(1).getSimpleString());
    }

    @Test
    public void testFindWithComparator() {
        putTestEntitiesStrings();
        List<TestEntity> entities = box.query().sort(new Comparator<TestEntity>() {
            @Override
            public int compare(TestEntity o1, TestEntity o2) {
                return o1.getSimpleString().substring(1).compareTo(o2.getSimpleString().substring(1));
            }
        }).build().find();
        Assert.assertEquals(5, entities.size());
        Assert.assertEquals("banana", entities.get(0).getSimpleString());
        Assert.assertEquals("banana milk shake", entities.get(1).getSimpleString());
        Assert.assertEquals("bar", entities.get(2).getSimpleString());
        Assert.assertEquals("foo bar", entities.get(3).getSimpleString());
        Assert.assertEquals("apple", entities.get(4).getSimpleString());
    }

    // TODO can we improve? More than just "still works"?
    @Test
    public void testQueryAttempts() {
        store.close();
        BoxStoreBuilder builder = new BoxStoreBuilder(createTestModel(false)).directory(boxStoreDir).queryAttempts(5).failedReadTxAttemptCallback(new TxCallback() {
            @Override
            public void txFinished(@Nullable
            Object result, @Nullable
            Throwable error) {
                error.printStackTrace();
            }
        });
        builder.entity(new TestEntity_());
        store = builder.build();
        putTestEntitiesScalars();
        Query<TestEntity> query = store.boxFor(TestEntity.class).query().equal(TestEntity_.simpleInt, 2007).build();
        Assert.assertEquals(2007, query.findFirst().getSimpleInt());
    }

    @Test
    public void testDateParam() {
        store.close();
        Assert.assertTrue(store.deleteAllFiles());
        store = MyObjectBox.builder().baseDirectory(boxStoreDir).debugFlags(LOG_QUERY_PARAMETERS).build();
        Date now = new Date();
        Order order = new Order();
        order.setDate(now);
        Box<Order> box = store.boxFor(Order.class);
        box.put(order);
        Query<Order> query = box.query().equal(date, 0).build();
        Assert.assertEquals(0, query.count());
        query.setParameter(date, now);
    }

    @Test
    public void testFailedUnique_exceptionListener() {
        final Exception[] exs = new Exception[]{ null };
        DbExceptionListener exceptionListener = new DbExceptionListener() {
            @Override
            public void onDbException(Exception e) {
                exs[0] = e;
            }
        };
        putTestEntitiesStrings();
        Query<TestEntity> query = box.query().build();
        store.setDbExceptionListener(exceptionListener);
        try {
            query.findUnique();
            Assert.fail("Should have thrown");
        } catch (NonUniqueResultException e) {
            Assert.assertSame(e, exs[0]);
        }
    }
}

