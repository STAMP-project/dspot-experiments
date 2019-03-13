/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.querycache;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.mapreduce.helpers.Employee;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheMethodsWithPredicateTest extends AbstractQueryCacheTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Employee> TRUE_PREDICATE = TruePredicate.INSTANCE;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void testKeySet_onIndexedField() {
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        int count = 111;
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        populateMap(map, count, (2 * count));
        // just choose arbitrary numbers for querying in order to prove whether #keySet with predicate is correctly working
        int equalsOrBiggerThan = 27;
        int expectedSize = (2 * count) - equalsOrBiggerThan;
        assertKeySetSizeEventually(expectedSize, new SqlPredicate(("id >= " + equalsOrBiggerThan)), cache);
    }

    @Test
    public void testKeySet_onIndexedField_whenIncludeValueFalse() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("__key", true);
        populateMap(map, count, (2 * count));
        // just choose arbitrary numbers for querying in order to prove whether #keySet with predicate is correctly working
        int equalsOrBiggerThan = 27;
        int expectedSize = (2 * count) - equalsOrBiggerThan;
        assertKeySetSizeEventually(expectedSize, new SqlPredicate(("__key >= " + equalsOrBiggerThan)), cache);
    }

    @Test
    public void testKeySet_onIndexedField_afterRemovalOfSomeIndexes() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        populateMap(map, 17, count);
        // just choose arbitrary numbers to prove whether #keySet with predicate is working
        int smallerThan = 17;
        int expectedSize = 17;
        assertKeySetSizeEventually(expectedSize, new SqlPredicate(("id < " + smallerThan)), cache);
    }

    @Test
    public void testKeySetIsNotBackedByQueryCache() {
        int count = 111;
        IMap<Employee, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        for (int i = 0; i < count; i++) {
            map.put(new Employee(i), new Employee(i));
        }
        Predicate predicate = Predicates.lessThan("id", count);
        QueryCache<Employee, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        for (Map.Entry<Employee, Employee> entry : cache.entrySet(predicate)) {
            entry.getValue().setAge(((Employee.MAX_AGE) + 1));
        }
        for (Map.Entry<Employee, Employee> entry : cache.entrySet(predicate)) {
            Assert.assertNotEquals(((Employee.MAX_AGE) + 1), entry.getValue().getAge());
        }
    }

    @Test
    public void testKeySetIsNotBackedByQueryCache_nonIndexedAttribute() {
        int count = 111;
        IMap<Employee, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        for (int i = 0; i < count; i++) {
            map.put(new Employee(i), new Employee(i));
        }
        Predicate predicate = Predicates.lessThan("salary", Employee.MAX_SALARY);
        QueryCache<Employee, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        for (Map.Entry<Employee, Employee> entry : cache.entrySet(predicate)) {
            entry.getValue().setAge(((Employee.MAX_AGE) + 1));
        }
        for (Map.Entry<Employee, Employee> entry : cache.entrySet(predicate)) {
            Assert.assertNotEquals(((Employee.MAX_AGE) + 1), entry.getValue().getAge());
        }
    }

    @Test
    public void testEntrySet() {
        int count = 1;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        populateMap(map, count, (2 * count));
        // just choose arbitrary numbers for querying in order to prove whether #keySet with predicate is correctly working
        int equalsOrBiggerThan = 0;
        int expectedSize = (2 * count) - equalsOrBiggerThan;
        assertEntrySetSizeEventually(expectedSize, new SqlPredicate(("id >= " + equalsOrBiggerThan)), cache);
    }

    @Test
    public void testEntrySetIsNotBackedByQueryCache() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        Predicate predicate = Predicates.lessThan("id", count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        for (Map.Entry<Integer, Employee> entry : cache.entrySet(predicate)) {
            entry.getValue().setAge(((Employee.MAX_AGE) + 1));
        }
        for (Map.Entry<Integer, Employee> entry : cache.entrySet(predicate)) {
            Assert.assertNotEquals(((Employee.MAX_AGE) + 1), entry.getValue().getAge());
        }
    }

    @Test
    public void testEntrySetIsNotBackedByQueryCache_nonIndexedAttribute() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        Predicate predicate = Predicates.lessThan("salary", Employee.MAX_SALARY);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        for (Map.Entry<Integer, Employee> entry : cache.entrySet(predicate)) {
            entry.getValue().setAge(((Employee.MAX_AGE) + 1));
        }
        for (Map.Entry<Integer, Employee> entry : cache.entrySet(predicate)) {
            Assert.assertNotEquals(((Employee.MAX_AGE) + 1), entry.getValue().getAge());
        }
    }

    @Test
    public void testEntrySet_whenIncludeValueFalse() throws Exception {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName, QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE, false);
        cache.addIndex("id", true);
        removeEntriesFromMap(map, 17, count);
        // just choose arbitrary numbers to prove whether #entrySet with predicate is working
        int smallerThan = 17;
        int expectedSize = 0;
        assertEntrySetSizeEventually(expectedSize, new SqlPredicate(("id < " + smallerThan)), cache);
    }

    @Test
    public void testEntrySet_withIndexedKeys_whenIncludeValueFalse() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName, QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE, false);
        // here adding key index. (key --> integer; value --> Employee)
        cache.addIndex("__key", true);
        removeEntriesFromMap(map, 17, count);
        // just choose arbitrary numbers to prove whether #entrySet with predicate is working
        int smallerThan = 17;
        int expectedSize = 17;
        assertEntrySetSizeEventually(expectedSize, new SqlPredicate(("__key < " + smallerThan)), cache);
    }

    @Test
    public void testValues() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        populateMap(map, count, (2 * count));
        // just choose arbitrary numbers for querying in order to prove whether #keySet with predicate is correctly working
        int equalsOrBiggerThan = 27;
        int expectedSize = (2 * count) - equalsOrBiggerThan;
        assertValuesSizeEventually(expectedSize, new SqlPredicate(("id >= " + equalsOrBiggerThan)), cache);
    }

    @Test
    public void testValuesAreNotBackedByQueryCache() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        Predicate predicate = Predicates.lessThan("id", count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        for (Employee employee : cache.values(predicate)) {
            employee.setAge(((Employee.MAX_AGE) + 1));
        }
        for (Employee employee : cache.values(predicate)) {
            Assert.assertNotEquals(((Employee.MAX_AGE) + 1), employee.getAge());
        }
    }

    @Test
    public void testValuesAreNotBackedByQueryCache_nonIndexedAttribute() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        Predicate predicate = Predicates.lessThan("salary", Employee.MAX_SALARY);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        cache.addIndex("id", true);
        for (Employee employee : cache.values(predicate)) {
            employee.setAge(((Employee.MAX_AGE) + 1));
        }
        for (Employee employee : cache.values(predicate)) {
            Assert.assertNotEquals(((Employee.MAX_AGE) + 1), employee.getAge());
        }
    }

    @Test
    public void testValues_withoutIndex() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        removeEntriesFromMap(map, 17, count);
        // just choose arbitrary numbers to prove whether #entrySet with predicate is working
        int smallerThan = 17;
        int expectedSize = 17;
        assertValuesSizeEventually(expectedSize, new SqlPredicate(("__key < " + smallerThan)), cache);
    }

    @Test
    public void testValues_withoutIndex_whenIncludeValueFalse() {
        int count = 111;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE);
        populateMap(map, count);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName, QueryCacheMethodsWithPredicateTest.TRUE_PREDICATE, false);
        removeEntriesFromMap(map, 17, count);
        // just choose arbitrary numbers to prove whether #entrySet with predicate is working
        int smallerThan = 17;
        int expectedSize = 0;
        assertValuesSizeEventually(expectedSize, new SqlPredicate(("__key < " + smallerThan)), cache);
    }
}

