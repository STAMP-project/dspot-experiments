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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AggregatorsSpecTest extends HazelcastTestSupport {
    public static final int PERSONS_COUNT = 999;

    @Parameterized.Parameter(0)
    public InMemoryFormat inMemoryFormat;

    @Parameterized.Parameter(1)
    public boolean parallelAccumulation;

    @Parameterized.Parameter(2)
    public String postfix;

    @Test
    public void testAggregators() {
        IMap<Integer, AggregatorsSpecTest.Person> map = getMapWithNodeCount(3, parallelAccumulation);
        AggregatorsSpecTest.populateMapWithPersons(map, postfix, AggregatorsSpecTest.PERSONS_COUNT);
        AggregatorsSpecTest.assertMinAggregators(map, postfix);
        AggregatorsSpecTest.assertMaxAggregators(map, postfix);
        AggregatorsSpecTest.assertSumAggregators(map, postfix);
        AggregatorsSpecTest.assertAverageAggregators(map, postfix);
        AggregatorsSpecTest.assertCountAggregators(map, postfix);
        AggregatorsSpecTest.assertDistinctAggregators(map, postfix);
    }

    @Test
    public void testAggregators_nullCornerCases() {
        IMap<Integer, AggregatorsSpecTest.Person> map = getMapWithNodeCount(3, parallelAccumulation);
        map.put(0, (postfix.contains("[any]") ? AggregatorsSpecTest.PersonAny.nulls() : new AggregatorsSpecTest.Person()));
        if (postfix.contains("[any]")) {
            assertMinAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertMaxAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertSumAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertAverageAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertCountAggregatorsAnyCornerCase(map, postfix, 0);
            AggregatorsSpecTest.assertDistinctAggregatorsAnyCornerCase(map, postfix, Collections.emptySet());
        } else {
            assertMinAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertMaxAggregatorsAnyCornerCase(map, postfix);
            // sum and avg do not accept null values, thus skipped
            AggregatorsSpecTest.assertCountAggregatorsAnyCornerCase(map, postfix, 1);
            HashSet expected = new HashSet();
            expected.add(null);
            AggregatorsSpecTest.assertDistinctAggregatorsAnyCornerCase(map, postfix, expected);
        }
    }

    @Test
    public void testAggregators_emptyCornerCases() {
        IMap<Integer, AggregatorsSpecTest.Person> map = getMapWithNodeCount(3, parallelAccumulation);
        if (postfix.contains("[any]")) {
            map.put(0, AggregatorsSpecTest.PersonAny.empty());
            assertMinAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertMaxAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertSumAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertAverageAggregatorsAnyCornerCase(map, postfix);
            AggregatorsSpecTest.assertCountAggregatorsAnyCornerCase(map, postfix, 0);
            AggregatorsSpecTest.assertDistinctAggregatorsAnyCornerCase(map, postfix, Collections.emptySet());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class Person implements Serializable {
        public Integer intValue;

        public Double doubleValue;

        public Long longValue;

        public BigDecimal bigDecimalValue;

        public BigInteger bigIntegerValue;

        public String comparableValue;

        public Person() {
        }

        public Person(int numberValue) {
            this.intValue = numberValue;
            this.doubleValue = ((double) (numberValue));
            this.longValue = ((long) (numberValue));
            this.bigDecimalValue = BigDecimal.valueOf(numberValue);
            this.bigIntegerValue = BigInteger.valueOf(numberValue);
            this.comparableValue = String.valueOf(numberValue);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class PersonAny extends AggregatorsSpecTest.Person implements Serializable {
        public int[] intValue;

        public double[] doubleValue;

        public long[] longValue;

        public List<BigDecimal> bigDecimalValue;

        public List<BigInteger> bigIntegerValue;

        public List<String> comparableValue;

        public PersonAny() {
        }

        public PersonAny(int numberValue) {
            this.intValue = new int[]{ numberValue };
            this.doubleValue = new double[]{ numberValue };
            this.longValue = new long[]{ numberValue };
            this.bigDecimalValue = Collections.singletonList(BigDecimal.valueOf(numberValue));
            this.bigIntegerValue = Collections.singletonList(BigInteger.valueOf(numberValue));
            this.comparableValue = Collections.singletonList(String.valueOf(numberValue));
        }

        public static AggregatorsSpecTest.PersonAny empty() {
            AggregatorsSpecTest.PersonAny person = new AggregatorsSpecTest.PersonAny();
            person.intValue = new int[]{  };
            person.doubleValue = new double[]{  };
            person.longValue = new long[]{  };
            person.bigDecimalValue = new ArrayList<BigDecimal>();
            person.bigIntegerValue = new ArrayList<BigInteger>();
            person.comparableValue = new ArrayList<String>();
            return person;
        }

        public static AggregatorsSpecTest.PersonAny nulls() {
            return new AggregatorsSpecTest.PersonAny();
        }
    }
}

