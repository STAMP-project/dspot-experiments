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
package com.hazelcast.mapreduce.aggregation;


import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DistinctAggregationTest extends AbstractAggregationTest {
    @Test
    public void testDistinctAggregationWithPredicates() {
        String mapName = HazelcastTestSupport.randomMapName();
        IMap<Integer, DistinctAggregationTest.Car> map = AbstractAggregationTest.hazelcastInstance.getMap(mapName);
        DistinctAggregationTest.Car vw1999 = DistinctAggregationTest.Car.newCar(1999, "VW");
        DistinctAggregationTest.Car bmw2000 = DistinctAggregationTest.Car.newCar(2000, "BMW");
        DistinctAggregationTest.Car vw2000 = DistinctAggregationTest.Car.newCar(2000, "VW");
        map.put(0, vw1999);
        map.put(1, bmw2000);
        map.put(2, vw2000);
        Supplier<Integer, DistinctAggregationTest.Car, DistinctAggregationTest.Car> supplier = Supplier.fromPredicate(Predicates.equal("buildYear", 2000));
        Aggregation<Integer, DistinctAggregationTest.Car, Set<DistinctAggregationTest.Car>> aggregation = Aggregations.distinctValues();
        Set<DistinctAggregationTest.Car> cars = map.aggregate(supplier, aggregation);
        MatcherAssert.assertThat(cars, Matchers.containsInAnyOrder(bmw2000, vw2000));
    }

    private static class Car implements Serializable {
        private int buildYear;

        private String brand;

        private static DistinctAggregationTest.Car newCar(int buildYear, String brand) {
            DistinctAggregationTest.Car car = new DistinctAggregationTest.Car();
            car.buildYear = buildYear;
            car.brand = brand;
            return car;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof DistinctAggregationTest.Car)) {
                return false;
            }
            DistinctAggregationTest.Car car = ((DistinctAggregationTest.Car) (o));
            if ((buildYear) != (car.buildYear)) {
                return false;
            }
            return brand.equals(car.brand);
        }

        @Override
        public int hashCode() {
            int result = buildYear;
            result = (31 * result) + (brand.hashCode());
            return result;
        }
    }
}

