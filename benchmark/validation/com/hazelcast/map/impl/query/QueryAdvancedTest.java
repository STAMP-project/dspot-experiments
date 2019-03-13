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
package com.hazelcast.map.impl.query;


import com.hazelcast.config.Config;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryAdvancedTest extends HazelcastTestSupport {
    @Test
    public void testQueryOperationAreNotSentToLiteMembers() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance fullMember = nodeFactory.newHazelcastInstance();
        HazelcastInstance liteMember = nodeFactory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastTestSupport.assertClusterSizeEventually(2, fullMember);
        IMap<Integer, Integer> map = fullMember.getMap(HazelcastTestSupport.randomMapName());
        QueryAdvancedTest.DeserializationCountingPredicate predicate = new QueryAdvancedTest.DeserializationCountingPredicate();
        // initialize all partitions
        for (int i = 0; i < 5000; i++) {
            map.put(i, i);
        }
        map.values(predicate);
        Assert.assertEquals(0, predicate.serializationCount());
    }

    public static class DeserializationCountingPredicate implements DataSerializable , Predicate {
        private static final AtomicInteger counter = new AtomicInteger();

        @Override
        public boolean apply(Map.Entry mapEntry) {
            return false;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            QueryAdvancedTest.DeserializationCountingPredicate.counter.incrementAndGet();
        }

        int serializationCount() {
            return QueryAdvancedTest.DeserializationCountingPredicate.counter.get();
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testQueryWithTTL() {
        Config config = getConfig();
        String mapName = "default";
        config.getMapConfig(mapName).setTimeToLiveSeconds(10);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap(mapName);
        map.addIndex("name", false);
        map.addIndex("age", false);
        map.addIndex("active", true);
        int passiveEmployees = 5;
        int activeEmployees = 5;
        int allEmployees = passiveEmployees + activeEmployees;
        final CountDownLatch latch = new CountDownLatch(allEmployees);
        map.addEntryListener(new EntryAdapter() {
            @Override
            public void entryEvicted(EntryEvent event) {
                latch.countDown();
            }
        }, false);
        for (int i = 0; i < activeEmployees; i++) {
            SampleTestObjects.Employee employee = new SampleTestObjects.Employee(("activeEmployee" + i), 60, true, i);
            map.put(("activeEmployee" + i), employee);
        }
        for (int i = 0; i < passiveEmployees; i++) {
            SampleTestObjects.Employee employee = new SampleTestObjects.Employee(("passiveEmployee" + i), 60, false, i);
            map.put(("passiveEmployee" + i), employee);
        }
        // check the query result before eviction
        Collection values = map.values(new SqlPredicate("active"));
        Assert.assertEquals(String.format("Expected %s results but got %s. Number of evicted entries: %s.", activeEmployees, values.size(), (allEmployees - (latch.getCount()))), activeEmployees, values.size());
        // wait until eviction is completed
        HazelcastTestSupport.assertOpenEventually(latch);
        // check the query result after eviction
        values = map.values(new SqlPredicate("active"));
        Assert.assertEquals(0, values.size());
    }

    @Test
    public void testTwoNodesWithPartialIndexes() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance1.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        for (int i = 0; i < 500; i++) {
            SampleTestObjects.Employee employee = new SampleTestObjects.Employee(i, ("name" + (i % 100)), ("city" + (i % 100)), (i % 60), ((i & 1) == 1), ((double) (i)));
            map.put(String.valueOf(i), employee);
        }
        HazelcastTestSupport.assertClusterSize(2, instance1, instance2);
        map = instance2.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        Collection<SampleTestObjects.Employee> entries = map.values(new SqlPredicate("name='name3' and city='city3' and age > 2"));
        Assert.assertEquals(5, entries.size());
        for (SampleTestObjects.Employee employee : entries) {
            Assert.assertEquals("name3", employee.getName());
            Assert.assertEquals("city3", employee.getCity());
        }
        entries = map.values(new SqlPredicate("name LIKE '%name3' and city like '%city3' and age > 2"));
        Assert.assertEquals(5, entries.size());
        for (SampleTestObjects.Employee employee : entries) {
            Assert.assertEquals("name3", employee.getName());
            Assert.assertEquals("city3", employee.getCity());
            Assert.assertTrue(((employee.getAge()) > 2));
        }
        entries = map.values(new SqlPredicate("name LIKE '%name3%' and city like '%city30%'"));
        Assert.assertEquals(5, entries.size());
        for (SampleTestObjects.Employee employee : entries) {
            Assert.assertTrue(employee.getName().startsWith("name3"));
            Assert.assertTrue(employee.getCity().startsWith("city3"));
        }
    }

    @Test
    public void testTwoNodesWithIndexes() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance1.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("city", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        for (int i = 0; i < 5000; i++) {
            SampleTestObjects.Employee employee = new SampleTestObjects.Employee(i, ("name" + (i % 100)), ("city" + (i % 100)), (i % 60), ((i & 1) == 1), ((double) (i)));
            map.put(String.valueOf(i), employee);
        }
        HazelcastTestSupport.assertClusterSize(2, instance1, instance2);
        map = instance2.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("city", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        Collection<SampleTestObjects.Employee> entries = map.values(new SqlPredicate("name='name3' and city='city3' and age > 2"));
        Assert.assertEquals(50, entries.size());
        for (SampleTestObjects.Employee employee : entries) {
            Assert.assertEquals("name3", employee.getName());
            Assert.assertEquals("city3", employee.getCity());
        }
        entries = map.values(new SqlPredicate("name LIKE '%name3' and city like '%city3' and age > 2"));
        Assert.assertEquals(50, entries.size());
        for (SampleTestObjects.Employee employee : entries) {
            Assert.assertEquals("name3", employee.getName());
            Assert.assertEquals("city3", employee.getCity());
            Assert.assertTrue(((employee.getAge()) > 2));
        }
        entries = map.values(new SqlPredicate("name LIKE '%name3%' and city like '%city30%'"));
        Assert.assertEquals(50, entries.size());
        for (SampleTestObjects.Employee employee : entries) {
            Assert.assertTrue(employee.getName().startsWith("name3"));
            Assert.assertTrue(employee.getCity().startsWith("city3"));
        }
    }

    @Test
    public void testOneMemberWithoutIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        QueryBasicTest.doFunctionalQueryTest(map);
    }

    @Test
    public void testOneMemberWithIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        QueryBasicTest.doFunctionalQueryTest(map);
    }

    @Test
    public void testOneMemberSQLWithoutIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        QueryBasicTest.doFunctionalSQLQueryTest(map);
        Set<Map.Entry<String, SampleTestObjects.Employee>> entries = map.entrySet(new SqlPredicate("active and age>23"));
        Assert.assertEquals(27, entries.size());
    }

    @Test
    public void testOneMemberSQLWithIndex() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        QueryBasicTest.doFunctionalSQLQueryTest(map);
    }

    @Test
    public void testTwoMembers() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        QueryBasicTest.doFunctionalQueryTest(map);
    }

    @Test
    public void testTwoMembersWithIndexes() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        QueryBasicTest.doFunctionalQueryTest(map);
    }

    @Test
    public void testTwoMembersWithIndexesAndShutdown() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance1.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        QueryBasicTest.doFunctionalQueryTest(map);
        Assert.assertEquals(101, map.size());
        instance2.getLifecycleService().shutdown();
        Assert.assertEquals(101, map.size());
        Set<Map.Entry<String, SampleTestObjects.Employee>> entries = map.entrySet(new SqlPredicate("active and age=23"));
        Assert.assertEquals(2, entries.size());
        for (Map.Entry<String, SampleTestObjects.Employee> entry : entries) {
            SampleTestObjects.Employee employee = entry.getValue();
            Assert.assertEquals(employee.getAge(), 23);
            Assert.assertTrue(employee.isActive());
        }
    }

    @Test
    public void testTwoMembersWithIndexesAndShutdown2() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance1.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        QueryBasicTest.doFunctionalQueryTest(map);
        Assert.assertEquals(101, map.size());
        instance1.getLifecycleService().shutdown();
        map = instance2.getMap("employees");
        Assert.assertEquals(101, map.size());
        Set<Map.Entry<String, SampleTestObjects.Employee>> entries = map.entrySet(new SqlPredicate("active and age=23"));
        Assert.assertEquals(2, entries.size());
        for (Map.Entry<String, SampleTestObjects.Employee> entry : entries) {
            SampleTestObjects.Employee employee = entry.getValue();
            Assert.assertEquals(employee.getAge(), 23);
            Assert.assertTrue(employee.isActive());
        }
    }

    @Test
    public void testTwoMembersWithIndexesAndShutdown3() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance1.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        QueryBasicTest.doFunctionalQueryTest(map);
        Assert.assertEquals(101, map.size());
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        Assert.assertEquals(101, map.size());
        instance1.getLifecycleService().shutdown();
        map = instance2.getMap("employees");
        Assert.assertEquals(101, map.size());
        Set<Map.Entry<String, SampleTestObjects.Employee>> entries = map.entrySet(new SqlPredicate("active and age=23"));
        Assert.assertEquals(2, entries.size());
        for (Map.Entry<String, SampleTestObjects.Employee> entry : entries) {
            SampleTestObjects.Employee employee = entry.getValue();
            Assert.assertEquals(employee.getAge(), 23);
            Assert.assertTrue(employee.isActive());
        }
    }

    @Test
    public void testSecondMemberAfterAddingIndexes() {
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("age", true);
        map.addIndex("active", false);
        nodeFactory.newHazelcastInstance(config);
        QueryBasicTest.doFunctionalQueryTest(map);
    }

    @Test
    public void testMapWithIndexAfterShutDown() {
        Config config = getConfig();
        String mapName = "default";
        config.getMapConfig(mapName).addMapIndexConfig(new MapIndexConfig("typeName", false));
        HazelcastInstance[] instances = createHazelcastInstanceFactory(3).newInstances(config);
        final IMap<Integer, SampleTestObjects.ValueType> map = instances[0].getMap(mapName);
        final int sampleSize1 = 100;
        final int sampleSize2 = 30;
        int totalSize = sampleSize1 + sampleSize2;
        for (int i = 0; i < sampleSize1; i++) {
            map.put(i, new SampleTestObjects.ValueType(("type" + i)));
        }
        for (int i = sampleSize1; i < totalSize; i++) {
            map.put(i, new SampleTestObjects.ValueType("typex"));
        }
        Collection typexValues = map.values(new SqlPredicate("typeName = typex"));
        Assert.assertEquals(sampleSize2, typexValues.size());
        instances[1].shutdown();
        Assert.assertEquals(totalSize, map.size());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                final Collection values = map.values(new SqlPredicate("typeName = typex"));
                Assert.assertEquals(sampleSize2, values.size());
            }
        });
        instances[2].shutdown();
        Assert.assertEquals(totalSize, map.size());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                final Collection values = map.values(new SqlPredicate("typeName = typex"));
                Assert.assertEquals(sampleSize2, values.size());
            }
        });
    }

    // issue 1404 "to be fixed by issue 1404"
    @Test
    public void testQueryAfterInitialLoad() {
        final int size = 100;
        String name = "default";
        Config config = getConfig();
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setImplementation(new com.hazelcast.core.MapStoreAdapter<Integer, SampleTestObjects.Employee>() {
            @Override
            public Map<Integer, SampleTestObjects.Employee> loadAll(Collection<Integer> keys) {
                Map<Integer, SampleTestObjects.Employee> map = new HashMap<Integer, SampleTestObjects.Employee>();
                for (Integer key : keys) {
                    SampleTestObjects.Employee emp = new SampleTestObjects.Employee();
                    emp.setActive(true);
                    map.put(key, emp);
                }
                return map;
            }

            @Override
            public Set<Integer> loadAllKeys() {
                Set<Integer> set = new HashSet<Integer>();
                for (int i = 0; i < size; i++) {
                    set.add(i);
                }
                return set;
            }
        });
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        final IMap map = instance.getMap(name);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection values = map.values(new SqlPredicate("active = true"));
                Assert.assertEquals(size, values.size());
            }
        });
    }

    // see: https://github.com/hazelcast/hazelcast/issues/3927
    @Test
    public void testUnknownPortableField_notCausesQueryException_withoutIndex() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getSerializationConfig().addPortableFactory(666, new PortableFactory() {
            public Portable create(int classId) {
                return new SampleTestObjects.PortableEmployee();
            }
        });
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        IMap<Integer, SampleTestObjects.PortableEmployee> map = hazelcastInstance.getMap(mapName);
        for (int i = 0; i < 5; i++) {
            map.put(i, new SampleTestObjects.PortableEmployee(i, ("name_" + i)));
        }
        Collection values = map.values(new SqlPredicate("notExist = name_0 OR a > 1"));
        Assert.assertEquals(3, values.size());
    }

    // see: https://github.com/hazelcast/hazelcast/issues/3927
    @Test
    public void testUnknownPortableField_notCausesQueryException_withIndex() {
        String mapName = "default";
        Config config = getConfig();
        config.getSerializationConfig().addPortableFactory(666, new PortableFactory() {
            public Portable create(int classId) {
                return new SampleTestObjects.PortableEmployee();
            }
        });
        config.getMapConfig(mapName).addMapIndexConfig(new MapIndexConfig("notExist", false)).addMapIndexConfig(new MapIndexConfig("n", false));
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        IMap<Integer, SampleTestObjects.PortableEmployee> map = hazelcastInstance.getMap(mapName);
        for (int i = 0; i < 5; i++) {
            map.put(i, new SampleTestObjects.PortableEmployee(i, ("name_" + i)));
        }
        Collection values = map.values(new SqlPredicate("n = name_2 OR notExist = name_0"));
        Assert.assertEquals(1, values.size());
    }
}

