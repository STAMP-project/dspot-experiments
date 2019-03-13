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
package com.hazelcast.projection;


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
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapProjectionTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test(expected = NullPointerException.class)
    public void null_projection() {
        getMapWithNodeCount(1).project(null);
    }

    @Test(expected = NullPointerException.class)
    public void null_predicate() {
        IMap<String, Double> map = getMapWithNodeCount(1);
        map.project(new MapProjectionTest.PrimitiveValueIncrementingProjection(), null);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings({ "RedundantCast", "unchecked" })
    public void null_projection_and_predicate() {
        getMapWithNodeCount(1).project(((Projection) (null)), ((Predicate) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("RedundantCast")
    public void pagingPredicate_fails() {
        getMapWithNodeCount(1).project(new MapProjectionTest.NullReturningProjection(), new PagingPredicate());
    }

    @Test
    public void projection_1Node_primitiveValue() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        Collection<Double> result = map.project(new MapProjectionTest.PrimitiveValueIncrementingProjection());
        Assert.assertThat(result, containsInAnyOrder(2.0, 5.0, 8.0));
    }

    @Test
    public void projection_3Nodes_primitiveValue() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        Collection<Double> result = map.project(new MapProjectionTest.PrimitiveValueIncrementingProjection());
        Assert.assertThat(result, containsInAnyOrder(2.0, 5.0, 8.0));
    }

    @Test
    public void projection_3Nodes_primitiveValue_exceptionThrowingProjection() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        expected.expect(RuntimeException.class);
        expected.expectMessage("transform() exception");
        map.project(new MapProjectionTest.ExceptionThrowingProjection());
    }

    @Test
    public void projection_3Nodes_nullReturningProjection() {
        IMap<String, Double> map = getMapWithNodeCount(3);
        populateMap(map);
        Collection<Double> result = map.project(new MapProjectionTest.NullReturningProjection());
        Assert.assertThat(result, containsInAnyOrder(((Double) (null)), null, null));
    }

    @Test
    public void projection_1Node_objectValue() {
        IMap<String, MapProjectionTest.Person> map = getMapWithNodeCount(1);
        populateMapWithPersons(map);
        Collection<Double> result = map.project(new MapProjectionTest.ObjectValueIncrementingProjection());
        Assert.assertThat(result, containsInAnyOrder(2.0, 5.0, 8.0));
    }

    @Test
    public void projection_3Nodes_objectValue() {
        IMap<String, MapProjectionTest.Person> map = getMapWithNodeCount(3);
        populateMapWithPersons(map);
        Collection<Double> result = map.project(new MapProjectionTest.ObjectValueIncrementingProjection());
        Assert.assertThat(result, containsInAnyOrder(2.0, 5.0, 8.0));
    }

    @Test
    public void projection_1Node_objectValue_withPredicate() {
        IMap<String, MapProjectionTest.Person> map = getMapWithNodeCount(1);
        populateMapWithPersons(map);
        Collection<Double> result = map.project(new MapProjectionTest.ObjectValueIncrementingProjection(), Predicates.greaterThan("age", 1.0));
        Assert.assertThat(result, containsInAnyOrder(5.0, 8.0));
    }

    @Test
    public void projection_3Nodes_objectValue_withPredicate() {
        IMap<String, MapProjectionTest.Person> map = getMapWithNodeCount(3);
        populateMapWithPersons(map);
        Collection<Double> result = map.project(new MapProjectionTest.ObjectValueIncrementingProjection(), Predicates.greaterThan("age", 1.0));
        Assert.assertThat(result, containsInAnyOrder(5.0, 8.0));
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

    public static class ExceptionThrowingProjection extends Projection {
        @Override
        public Object transform(Object input) {
            throw new RuntimeException("transform() exception");
        }
    }

    public static class NullReturningProjection extends Projection {
        @Override
        public Object transform(Object input) {
            return null;
        }
    }

    public static class PrimitiveValueIncrementingProjection extends Projection<Map.Entry<String, Double>, Double> {
        @Override
        public Double transform(Map.Entry<String, Double> input) {
            return (input.getValue()) + 1.0;
        }
    }

    public static class ObjectValueIncrementingProjection extends Projection<Map.Entry<String, MapProjectionTest.Person>, Double> {
        @Override
        public Double transform(Map.Entry<String, MapProjectionTest.Person> input) {
            return (input.getValue().age) + 1.0;
        }
    }
}

