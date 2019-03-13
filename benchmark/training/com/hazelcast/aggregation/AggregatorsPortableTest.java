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
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AggregatorsPortableTest extends HazelcastTestSupport {
    @Test
    public void testConstructors() {
        HazelcastTestSupport.assertUtilityConstructor(Aggregators.class);
    }

    @Test
    public void aggregate_emptyNullSkipped_nullInValues_nullFirst() {
        // GIVEN
        IMap<Integer, AggregatorsPortableTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsPortableTest.Car cornerCaseCar = getCornerCaseCar(null, "1");
        map.put(0, cornerCaseCar);
        // WHEN
        List<String> accumulated = map.aggregate(new AggregatorsPortableTest.TestAggregator("wheels[any]"));
        // THEN
        MatcherAssert.assertThat(accumulated, Matchers.containsInAnyOrder(null, "1"));
        MatcherAssert.assertThat(accumulated, Matchers.hasSize(2));
    }

    @Test
    public void aggregate_emptyNullSkipped_nullInValues() {
        // GIVEN
        IMap<Integer, AggregatorsPortableTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsPortableTest.Car cornerCaseCar = getCornerCaseCar("1", null, "2", null);
        map.put(0, cornerCaseCar);
        // WHEN
        List<String> accumulated = map.aggregate(new AggregatorsPortableTest.TestAggregator("wheels[any]"));
        // THEN
        MatcherAssert.assertThat(accumulated, Matchers.containsInAnyOrder("1", null, "2", null));
        MatcherAssert.assertThat(accumulated, Matchers.hasSize(4));
    }

    @Test
    public void aggregate_emptyNullSkipped_noNullInValues() {
        // GIVEN
        IMap<Integer, AggregatorsPortableTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsPortableTest.Car cornerCaseCar = getCornerCaseCar("2", "1");
        map.put(0, cornerCaseCar);
        // WHEN
        List<String> accumulated = map.aggregate(new AggregatorsPortableTest.TestAggregator("wheels[any]"));
        // THEN
        MatcherAssert.assertThat(accumulated, Matchers.containsInAnyOrder("1", "2"));
        MatcherAssert.assertThat(accumulated, Matchers.hasSize(2));
    }

    @Test
    public void aggregate_nullFirstArray() {
        // GIVEN
        IMap<Integer, AggregatorsPortableTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsPortableTest.Car cornerCaseCar = new AggregatorsPortableTest.Car();
        map.put(0, cornerCaseCar);
        // WHEN
        List<String> accumulated = map.aggregate(new AggregatorsPortableTest.TestAggregator("wheels[any]"));
        // THEN
        MatcherAssert.assertThat(accumulated, Matchers.hasSize(0));
    }

    @Test
    public void aggregate_emptyFirstArray() {
        // GIVEN
        IMap<Integer, AggregatorsPortableTest.Car> map = getMapWithNodeCount(1, false, OBJECT);
        AggregatorsPortableTest.Car cornerCaseCar = new AggregatorsPortableTest.Car();
        cornerCaseCar.wheels = new String[]{  };
        map.put(0, cornerCaseCar);
        // WHEN
        List<String> accumulated = map.aggregate(new AggregatorsPortableTest.TestAggregator("wheels[any]"));
        // THEN
        MatcherAssert.assertThat(accumulated, Matchers.hasSize(0));
    }

    private static class TestAggregator extends AbstractAggregator<Map.Entry<Integer, AggregatorsPortableTest.Car>, String, List<String>> {
        private List<String> accumulated = new ArrayList<String>();

        public TestAggregator(String attribute) {
            super(attribute);
        }

        @Override
        protected void accumulateExtracted(Map.Entry<Integer, AggregatorsPortableTest.Car> entry, String value) {
            accumulated.add(value);
        }

        @Override
        public void combine(Aggregator aggregator) {
            accumulated.addAll(((AggregatorsPortableTest.TestAggregator) (aggregator)).accumulated);
        }

        @Override
        public List<String> aggregate() {
            return accumulated;
        }
    }

    static class Car implements Portable {
        String[] wheels;

        @Override
        public int getFactoryId() {
            return AggregatorsPortableTest.CarFactory.ID;
        }

        @Override
        public int getClassId() {
            return 1;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeUTFArray("wheels", wheels);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            wheels = reader.readUTFArray("wheels");
        }
    }

    static class CarFactory implements PortableFactory {
        static final int ID = 1;

        @Override
        public Portable create(int classId) {
            if (classId == 1) {
                return new AggregatorsPortableTest.Car();
            } else {
                return null;
            }
        }
    }
}

