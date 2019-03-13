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
package com.hazelcast.map;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.projection.Projection;
import com.hazelcast.projection.Projections;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapQueryPartitionIteratorTest extends HazelcastTestSupport {
    private HazelcastInstance instance;

    private MapProxyImpl<String, String> proxy;

    @Test(expected = NoSuchElementException.class)
    public void test_next_Throws_Exception_On_EmptyPartition() throws Exception {
        proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.TestProjection(), TruePredicate.<String, String>truePredicate()).next();
    }

    @Test(expected = NullPointerException.class)
    public void test_null_projection_throws_exception() throws Exception {
        proxy.iterator(10, 1, null, TruePredicate.<String, String>truePredicate());
    }

    @Test(expected = NullPointerException.class)
    public void test_null_predicate_throws_exception() throws Exception {
        proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.TestProjection(), null);
    }

    @Test
    public void test_HasNext_Returns_False_On_EmptyPartition() throws Exception {
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.TestProjection(), TruePredicate.<String, String>truePredicate());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void test_Next_Returns_Value_On_NonEmptyPartition() throws Exception {
        String value = HazelcastTestSupport.randomString();
        fillMap(proxy, 1, 1, value);
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.GetValueProjection<String>(), TruePredicate.<String, String>truePredicate());
        final String next = iterator.next();
        Assert.assertEquals(value, next);
    }

    @Test
    public void test_Next_Returns_Value_On_NonEmptyPartition_and_HasNext_Returns_False_when_Item_Consumed() throws Exception {
        String value = HazelcastTestSupport.randomString();
        fillMap(proxy, 1, 1, value);
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.GetValueProjection<String>(), TruePredicate.<String, String>truePredicate());
        final String next = iterator.next();
        Assert.assertEquals(value, next);
        boolean hasNext = iterator.hasNext();
        Assert.assertFalse(hasNext);
    }

    @Test
    public void test_HasNext_Returns_True_On_NonEmptyPartition() throws Exception {
        fillMap(proxy, 1, 1, HazelcastTestSupport.randomString());
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.TestProjection(), TruePredicate.<String, String>truePredicate());
        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void test_with_projection_and_true_predicate() throws Exception {
        fillMap(proxy, 1, 100, HazelcastTestSupport.randomString());
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.TestProjection(), TruePredicate.<String, String>truePredicate());
        final ArrayList<String> projected = collectAll(iterator);
        final Collection<String> actualValues = proxy.values();
        Assert.assertEquals(actualValues.size(), projected.size());
        for (String value : actualValues) {
            Assert.assertTrue(projected.contains(("dummy" + value)));
        }
    }

    @Test
    public void test_with_projection_and_predicate() throws Exception {
        final MapProxyImpl<String, Integer> intMap = ((MapProxyImpl<String, Integer>) (instance.<String, Integer>getMap(HazelcastTestSupport.randomMapName())));
        fillMap(intMap, 1, 100);
        final Iterator<Map.Entry<String, Integer>> iterator = intMap.iterator(10, 1, Projections.<Map.Entry<String, Integer>>identity(), new MapQueryPartitionIteratorTest.EvenPredicate());
        final ArrayList<Map.Entry<String, Integer>> projected = collectAll(iterator);
        for (Map.Entry<String, Integer> i : projected) {
            Assert.assertTrue((((i.getValue()) % 2) == 0));
        }
        final Collection<Integer> actualValues = intMap.values();
        Assert.assertEquals(((actualValues.size()) / 2), projected.size());
        for (Map.Entry<String, Integer> e : intMap.entrySet()) {
            if (((e.getValue()) % 2) == 0) {
                Assert.assertTrue(projected.contains(e));
            }
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_remove_Throws_Exception() throws Exception {
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.TestProjection(), TruePredicate.<String, String>truePredicate());
        iterator.remove();
    }

    @Test
    public void test_Next_Returns_Values_When_FetchSizeExceeds_On_NonEmptyPartition() throws Exception {
        String value = HazelcastTestSupport.randomString();
        fillMap(proxy, 1, 100, value);
        final Iterator<String> iterator = proxy.iterator(10, 1, new MapQueryPartitionIteratorTest.GetValueProjection<String>(), TruePredicate.<String, String>truePredicate());
        for (int i = 0; i < 100; i++) {
            String val = iterator.next();
            Assert.assertEquals(value, val);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_NoExceptions_When_IndexesAreAccessed_During_PredicateOptimization() {
        int count = (instance.getPartitionService().getPartitions().size()) * 10;
        MapProxyImpl<Integer, Integer> map = ((MapProxyImpl<Integer, Integer>) (instance.<Integer, Integer>getMap(HazelcastTestSupport.randomMapName())));
        for (int i = 0; i < count; ++i) {
            map.put(i, i);
        }
        // this predicate is a subject for optimizing it into a between predicate
        Predicate<Integer, Integer> predicate = Predicates.and(Predicates.greaterEqual("this", 0), Predicates.lessEqual("this", (count - 1)));
        Collection result = collectAll(map.iterator(10, 1, Projections.<Map.Entry<Integer, Integer>>identity(), predicate));
        Assert.assertTrue((!(result.isEmpty())));
    }

    private static class EvenPredicate implements Predicate<String, Integer> {
        @Override
        public boolean apply(Map.Entry<String, Integer> mapEntry) {
            return ((mapEntry.getValue()) % 2) == 0;
        }
    }

    private static class TestProjection extends Projection<Map.Entry<String, String>, String> {
        @Override
        public String transform(Map.Entry<String, String> input) {
            return "dummy" + (input.getValue());
        }
    }

    private static class GetValueProjection<T> extends Projection<Map.Entry<String, T>, T> {
        @Override
        public T transform(Map.Entry<String, T> input) {
            return input.getValue();
        }
    }
}

