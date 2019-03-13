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
package com.hazelcast.aggregation;


import InMemoryFormat.OBJECT;
import com.hazelcast.aggregation.impl.AbstractAggregator;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AggregatorsTest extends HazelcastTestSupport {
    @Test
    public void testConstructors() {
        HazelcastTestSupport.assertUtilityConstructor(Aggregators.class);
    }

    @Test
    public void aggregate_emptyNullSkipped_nullInValues() {
        // GIVEN
        IMap<Integer, AggregatorsTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsTest.Car cornerCaseCar = getCornerCaseCar(null, 1L);
        map.put(0, cornerCaseCar);
        // WHEN
        List<Long> accumulatedArray = map.aggregate(new AggregatorsTest.TestAggregator("wheelsA[any].tiresA[any]"));
        List<Long> accumulatedCollection = map.aggregate(new AggregatorsTest.TestAggregator("wheelsC[any].tiresC[any]"));
        // THEN
        MatcherAssert.assertThat(accumulatedCollection, Matchers.containsInAnyOrder(accumulatedArray.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(accumulatedCollection.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(1L, null));
        MatcherAssert.assertThat(accumulatedArray, Matchers.hasSize(2));
    }

    @Test
    public void aggregate_emptyNullSkipped_noNullInValues() {
        // GIVEN
        IMap<Integer, AggregatorsTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsTest.Car cornerCaseCar = getCornerCaseCar(2L, 1L);
        map.put(0, cornerCaseCar);
        // WHEN
        List<Long> accumulatedArray = map.aggregate(new AggregatorsTest.TestAggregator("wheelsA[any].tiresA[any]"));
        List<Long> accumulatedCollection = map.aggregate(new AggregatorsTest.TestAggregator("wheelsC[any].tiresC[any]"));
        // THEN
        MatcherAssert.assertThat(accumulatedCollection, Matchers.containsInAnyOrder(accumulatedArray.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(accumulatedCollection.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(1L, 2L));
        MatcherAssert.assertThat(accumulatedArray, Matchers.hasSize(2));
    }

    @Test
    public void aggregate_emptyNullSkipped_moreThanOneNullInValues() {
        // GIVEN
        IMap<Integer, AggregatorsTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsTest.Car cornerCaseCar = getCornerCaseCar(2L, null, 1L, null);
        map.put(0, cornerCaseCar);
        // WHEN
        List<Long> accumulatedArray = map.aggregate(new AggregatorsTest.TestAggregator("wheelsA[any].tiresA[any]"));
        List<Long> accumulatedCollection = map.aggregate(new AggregatorsTest.TestAggregator("wheelsC[any].tiresC[any]"));
        // THEN
        MatcherAssert.assertThat(accumulatedCollection, Matchers.containsInAnyOrder(accumulatedArray.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(accumulatedCollection.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(1L, 2L, null, null));
        MatcherAssert.assertThat(accumulatedArray, Matchers.hasSize(4));
    }

    @Test
    public void aggregate_nullFirstArray() {
        // GIVEN
        IMap<Integer, AggregatorsTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsTest.Car cornerCaseCar = new AggregatorsTest.Car();
        map.put(0, cornerCaseCar);
        // WHEN
        List<Long> accumulatedArray = map.aggregate(new AggregatorsTest.TestAggregator("wheelsA[any].tiresA[any]"));
        List<Long> accumulatedCollection = map.aggregate(new AggregatorsTest.TestAggregator("wheelsC[any].tiresC[any]"));
        // THEN
        MatcherAssert.assertThat(accumulatedCollection, Matchers.containsInAnyOrder(accumulatedArray.toArray()));
        MatcherAssert.assertThat(accumulatedArray, Matchers.containsInAnyOrder(accumulatedCollection.toArray()));
        MatcherAssert.assertThat(accumulatedCollection, Matchers.hasSize(0));
    }

    private static class TestAggregator extends AbstractAggregator<Map.Entry<Integer, AggregatorsTest.Car>, Long, List<Long>> {
        private List<Long> accumulated = new ArrayList<Long>();

        public TestAggregator(String attribute) {
            super(attribute);
        }

        @Override
        protected void accumulateExtracted(Map.Entry<Integer, AggregatorsTest.Car> entry, Long value) {
            accumulated.add(value);
        }

        @Override
        public void combine(Aggregator aggregator) {
            accumulated.addAll(((AggregatorsTest.TestAggregator) (aggregator)).accumulated);
        }

        @Override
        public List<Long> aggregate() {
            return accumulated;
        }
    }

    static class Car implements Serializable {
        Collection<AggregatorsTest.Wheel> wheelsC;

        AggregatorsTest.Wheel[] wheelsA;
    }

    static class Wheel implements Serializable {
        Collection<Long> tiresC;

        Long[] tiresA;
    }
}

