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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IndexStatsQueryingInCollectionsAndArraysTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    private static final int PARTITIONS = 137;

    private static final int EMPLOYEE_ARRAY_SIZE = 10;

    private String mapName;

    private HazelcastInstance instance;

    private IMap<Integer, IndexStatsQueryingInCollectionsAndArraysTest.Department> map;

    @Test
    public void testHitAndQueryCounting_WhenAllIndexesHit() {
        map.addIndex("employees[0].id", true);
        map.addIndex("employees[any].id", true);
        Assert.assertEquals(0, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        for (int i = 0; i < 10; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee(j);
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        for (int i = 0; i < 100; i++) {
            map.entrySet(Predicates.alwaysTrue());
            map.entrySet(Predicates.equal("employees[0].id", "0"));
            map.entrySet(Predicates.equal("employees[any].id", "5"));
            map.entrySet(Predicates.lessEqual("employees[0].id", "1"));
            map.entrySet(Predicates.lessEqual("employees[any].id", "6"));
        }
        Assert.assertEquals(500, stats().getQueryCount());
        Assert.assertEquals(400, stats().getIndexedQueryCount());
        Assert.assertEquals(200, valueStats("employees[0].id").getHitCount());
        Assert.assertEquals(200, valueStats("employees[0].id").getQueryCount());
        Assert.assertEquals(200, valueStats("employees[any].id").getHitCount());
        Assert.assertEquals(200, valueStats("employees[any].id").getQueryCount());
    }

    @Test
    public void testHitAndQueryCounting_WhenSingleNumberIndexHit() {
        map.addIndex("employees[0].id", true);
        map.addIndex("employees[any].id", true);
        Assert.assertEquals(0, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        for (int i = 0; i < 10; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee(j);
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        for (int i = 0; i < 100; i++) {
            map.entrySet(Predicates.alwaysTrue());
            map.entrySet(Predicates.equal("employees[0].id", "0"));
        }
        Assert.assertEquals(200, stats().getQueryCount());
        Assert.assertEquals(100, stats().getIndexedQueryCount());
        Assert.assertEquals(100, valueStats("employees[0].id").getHitCount());
        Assert.assertEquals(100, valueStats("employees[0].id").getQueryCount());
        Assert.assertEquals(0, valueStats("employees[any].id").getHitCount());
        Assert.assertEquals(0, valueStats("employees[any].id").getQueryCount());
    }

    @Test
    public void testHitAndQueryCounting_WhenSingleAnyIndexHit() {
        map.addIndex("employees[0].id", true);
        map.addIndex("employees[any].id", true);
        Assert.assertEquals(0, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        for (int i = 0; i < 10; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee(j);
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        for (int i = 0; i < 100; i++) {
            map.entrySet(Predicates.alwaysTrue());
            map.entrySet(Predicates.equal("employees[any].id", "5"));
        }
        Assert.assertEquals(200, stats().getQueryCount());
        Assert.assertEquals(100, stats().getIndexedQueryCount());
        Assert.assertEquals(0, valueStats("employees[0].id").getHitCount());
        Assert.assertEquals(0, valueStats("employees[0].id").getQueryCount());
        Assert.assertEquals(100, valueStats("employees[any].id").getHitCount());
        Assert.assertEquals(100, valueStats("employees[any].id").getQueryCount());
    }

    @Test
    public void testHitCounting_WhenIndexHitMultipleTimes() {
        map.addIndex("employees[0].id", true);
        map.addIndex("employees[any].id", true);
        Assert.assertEquals(0, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        for (int i = 0; i < 10; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee(j);
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        for (int i = 0; i < 100; i++) {
            map.entrySet(Predicates.alwaysTrue());
            map.entrySet(Predicates.or(Predicates.equal("employees[any].id", "0"), Predicates.equal("employees[any].id", "5")));
            map.entrySet(Predicates.or(Predicates.equal("employees[0].id", "0"), Predicates.equal("employees[any].id", "6")));
        }
        Assert.assertEquals(300, stats().getQueryCount());
        Assert.assertEquals(200, stats().getIndexedQueryCount());
        Assert.assertEquals(100, valueStats("employees[0].id").getHitCount());
        Assert.assertEquals(100, valueStats("employees[0].id").getQueryCount());
        Assert.assertEquals(300, valueStats("employees[any].id").getHitCount());
        Assert.assertEquals(200, valueStats("employees[any].id").getQueryCount());
    }

    @Test
    public void testAverageQuerySelectivityCalculation() {
        double expectedEqual_0 = 0.5;
        double expectedEqual_Any = 0.99;
        double expectedGreaterThan_0 = 0.6;
        double expectedGreaterThan_Any = 0.75;
        int iterations = 100;
        map.addIndex("employees[0].id", true);
        map.addIndex("employees[any].id", true);
        for (int i = 0; i < 50; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee(j);
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        for (int i = 50; i < 100; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee((i + j));
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        Assert.assertEquals(0.0, valueStats("employees[0].id").getAverageHitSelectivity(), 0.0);
        Assert.assertEquals(0.0, valueStats("employees[any].id").getAverageHitSelectivity(), 0.0);
        for (int i = 0; i < iterations; i++) {
            // select entries 0-49
            map.entrySet(Predicates.equal("employees[0].id", "0"));
            // select entry 50
            map.entrySet(Predicates.equal("employees[any].id", "50"));
            Assert.assertEquals(expectedEqual_0, valueStats("employees[0].id").getAverageHitSelectivity(), 0.015);
            Assert.assertEquals(expectedEqual_Any, valueStats("employees[any].id").getAverageHitSelectivity(), 0.015);
        }
        for (int i = 0; i < iterations; i++) {
            // select entries 60-99
            map.entrySet(Predicates.greaterThan("employees[0].id", "59"));
            // select entries 75-99
            map.entrySet(Predicates.greaterThan("employees[any].id", "83"));
            Assert.assertEquals((((expectedEqual_0 * iterations) + (expectedGreaterThan_0 * i)) / (iterations + i)), valueStats("employees[0].id").getAverageHitSelectivity(), 0.015);
            Assert.assertEquals((((expectedEqual_Any * iterations) + (expectedGreaterThan_Any * i)) / (iterations + i)), valueStats("employees[any].id").getAverageHitSelectivity(), 0.015);
        }
    }

    @Test
    public void testOperationsCounting() {
        map.addIndex("employees[0].id", true);
        map.addIndex("employees[any].id", true);
        for (int i = 0; i < 100; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee(j);
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        checkOperations(100, 0, 0);
        for (int i = 50; i < 100; ++i) {
            map.remove(i);
        }
        checkOperations(100, 0, 50);
        for (int i = 0; i < 50; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee((j * j));
            }
            map.put(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        checkOperations(100, 50, 50);
        for (int i = 50; i < 100; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee((j + 1));
            }
            map.set(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        checkOperations(150, 50, 50);
        for (int i = 0; i < 50; i++) {
            IndexStatsQueryingInCollectionsAndArraysTest.Employee[] persons = new IndexStatsQueryingInCollectionsAndArraysTest.Employee[IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE];
            for (int j = 0; j < (IndexStatsQueryingInCollectionsAndArraysTest.EMPLOYEE_ARRAY_SIZE); j++) {
                persons[j] = new IndexStatsQueryingInCollectionsAndArraysTest.Employee((j + 1));
            }
            map.set(i, new IndexStatsQueryingInCollectionsAndArraysTest.Department(persons));
        }
        checkOperations(150, 100, 50);
    }

    private static class Employee implements Serializable {
        private final int id;

        private Employee(int age) {
            this.id = age;
        }

        public int getAge() {
            return id;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = (11 * hash) + (this.id);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            final IndexStatsQueryingInCollectionsAndArraysTest.Employee other = ((IndexStatsQueryingInCollectionsAndArraysTest.Employee) (obj));
            if ((this.id) != (other.id)) {
                return false;
            }
            return true;
        }
    }

    private static class Department implements Serializable {
        private final IndexStatsQueryingInCollectionsAndArraysTest.Employee[] employees;

        private Department(IndexStatsQueryingInCollectionsAndArraysTest.Employee[] employees) {
            this.employees = employees;
        }

        public IndexStatsQueryingInCollectionsAndArraysTest.Employee[] getEmployees() {
            return employees;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = (89 * hash) + (Arrays.deepHashCode(this.employees));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            final IndexStatsQueryingInCollectionsAndArraysTest.Department other = ((IndexStatsQueryingInCollectionsAndArraysTest.Department) (obj));
            if (!(Arrays.deepEquals(this.employees, other.employees))) {
                return false;
            }
            return true;
        }
    }
}

