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


import com.hazelcast.aggregation.impl.DoubleAverageAggregator;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapAggregateTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test(expected = NullPointerException.class)
    public void null_aggregator() {
        getMapWithNodeCount(1).aggregate(null);
    }

    @Test(expected = NullPointerException.class)
    public void null_predicate() {
        getMapWithNodeCount(1).aggregate(new DoubleAverageAggregator(), null);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("RedundantCast")
    public void null_aggregator_and_predicate() {
        getMapWithNodeCount(1).aggregate(((Aggregator) (null)), ((Predicate) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("RedundantCast")
    public void pagingPredicate_fails() {
        getMapWithNodeCount(1).aggregate(new DoubleAverageAggregator(), new PagingPredicate());
    }

    @Test
    public void doubleAvg_1Node_primitiveValue() {
        IMap<String, Double> map = getMapWithNodeCount(1);
        populateMap(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, Double>>());
        Assert.assertEquals(Double.valueOf(4.0), avg);
    }

    @Test
    public void doubleAvg_3Nodes_primitiveValue() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, Double>>());
        Assert.assertEquals(Double.valueOf(4.0), avg);
    }

    @Test
    public void doubleAvg_3Nodes_exceptionOnAccumulate() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        expected.expect(RuntimeException.class);
        expected.expectMessage("accumulate() exception");
        map.aggregate(new MapAggregateTest.ExceptionThrowingAggregator<java.util.Map.Entry<String, Double>>(true, false, false));
    }

    @Test
    public void doubleAvg_3Nodes_exceptionOnCombine() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        expected.expect(RuntimeException.class);
        expected.expectMessage("combine() exception");
        map.aggregate(new MapAggregateTest.ExceptionThrowingAggregator<java.util.Map.Entry<String, Double>>(false, true, false));
    }

    @Test
    public void doubleAvg_3Nodes_exceptionOnAggregate() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        expected.expect(RuntimeException.class);
        expected.expectMessage("aggregate() exception");
        map.aggregate(new MapAggregateTest.ExceptionThrowingAggregator<java.util.Map.Entry<String, Double>>(false, false, true));
    }

    @Test
    public void doubleAvg_1Node_objectValue() {
        IMap<String, MapAggregateTest.Person> map = getMapWithNodeCount(1);
        populateMapWithPersons(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, MapAggregateTest.Person>>("age"));
        Assert.assertEquals(Double.valueOf(4.0), avg);
    }

    @Test
    public void doubleAvg_3Nodes_objectValue() {
        IMap<String, MapAggregateTest.Person> map = getMapWithNodeCount(3);
        populateMapWithPersons(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, MapAggregateTest.Person>>("age"));
        Assert.assertEquals(Double.valueOf(4.0), avg);
    }

    @Test
    public void doubleAvg_1Node_objectValue_withPredicate() {
        IMap<String, MapAggregateTest.Person> map = getMapWithNodeCount(1);
        populateMapWithPersons(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, MapAggregateTest.Person>>("age"), Predicates.greaterThan("age", 2.0));
        Assert.assertEquals(Double.valueOf(5.5), avg);
    }

    @Test
    public void doubleAvg_1Node_objectValue_withEmptyResultPredicate() {
        IMap<String, MapAggregateTest.Person> map = getMapWithNodeCount(1);
        populateMapWithPersons(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, MapAggregateTest.Person>>("age"), Predicates.greaterThan("age", 30.0));
        Assert.assertNull(avg);
    }

    @Test
    public void doubleAvg_3Nodes_objectValue_withPredicate() {
        IMap<String, MapAggregateTest.Person> map = getMapWithNodeCount(3);
        populateMapWithPersons(map);
        Double avg = map.aggregate(new DoubleAverageAggregator<java.util.Map.Entry<String, MapAggregateTest.Person>>("age"), Predicates.greaterThan("age", 2.0));
        Assert.assertEquals(Double.valueOf(5.5), avg);
    }

    private static class ExceptionThrowingAggregator<I> extends Aggregator<I, Double> {
        private boolean throwOnAccumulate;

        private boolean throwOnCombine;

        private boolean throwOnAggregate;

        ExceptionThrowingAggregator(boolean throwOnAccumulate, boolean throwOnCombine, boolean throwOnAggregate) {
            this.throwOnAccumulate = throwOnAccumulate;
            this.throwOnCombine = throwOnCombine;
            this.throwOnAggregate = throwOnAggregate;
        }

        @Override
        public void accumulate(I entry) {
            if (throwOnAccumulate) {
                throw new RuntimeException("accumulate() exception");
            }
        }

        @Override
        public void combine(Aggregator aggregator) {
            if (throwOnCombine) {
                throw new RuntimeException("combine() exception");
            }
        }

        @Override
        public Double aggregate() {
            if (throwOnAggregate) {
                throw new RuntimeException("aggregate() exception");
            }
            return 0.0;
        }
    }

    public static class Person implements DataSerializable {
        public double age;

        public Person() {
        }

        public Person(double age) {
            this.age = age;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeDouble(age);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            age = in.readDouble();
        }
    }
}

