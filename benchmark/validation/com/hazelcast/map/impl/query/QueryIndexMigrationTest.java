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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.util.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ SlowTest.class, ParallelTest.class })
public class QueryIndexMigrationTest extends HazelcastTestSupport {
    private Random random = new Random();

    private TestHazelcastInstanceFactory nodeFactory;

    private ExecutorService executor;

    @Parameterized.Parameter(0)
    public IndexCopyBehavior copyBehavior;

    @Test(timeout = TimeConstants.MINUTE)
    public void testQueryDuringAndAfterMigration() {
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(getTestConfig());
        int count = 500;
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        for (int i = 0; i < count; i++) {
            map.put(String.valueOf(i), new SampleTestObjects.Employee(("joe" + i), (i % 60), ((i & 1) == 1), ((double) (i))));
        }
        nodeFactory.newInstances(getTestConfig(), 3);
        final IMap<String, SampleTestObjects.Employee> employees = instance.getMap("employees");
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<SampleTestObjects.Employee> values = employees.values(new SqlPredicate("active and name LIKE 'joe15%'"));
                for (SampleTestObjects.Employee employee : values) {
                    Assert.assertTrue(employee.isActive());
                }
                Assert.assertEquals(6, values.size());
            }
        }, 3);
    }

    @Test
    public void testQueryDuringAndAfterMigrationWithIndex() {
        Config config = getTestConfig();
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("active", false);
        int size = 500;
        for (int i = 0; i < size; i++) {
            map.put(String.valueOf(i), new SampleTestObjects.Employee(("joe" + i), (i % 60), ((i & 1) == 1), ((double) (i))));
        }
        nodeFactory.newInstances(config, 3);
        final IMap<String, SampleTestObjects.Employee> employees = instance.getMap("employees");
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<SampleTestObjects.Employee> values = employees.values(new SqlPredicate("active and name LIKE 'joe15%'"));
                for (SampleTestObjects.Employee employee : values) {
                    Assert.assertTrue(((employee.isActive()) && (employee.getName().startsWith("joe15"))));
                }
                Assert.assertEquals(6, values.size());
            }
        }, 3);
    }

    @Test
    public void testQueryWithIndexesWhileMigrating() {
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(getTestConfig());
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("employees");
        map.addIndex("age", true);
        map.addIndex("active", false);
        for (int i = 0; i < 500; i++) {
            map.put(("e" + i), new SampleTestObjects.Employee(("name" + i), (i % 50), ((i & 1) == 1), ((double) (i))));
        }
        Assert.assertEquals(500, map.size());
        Set<Map.Entry<String, SampleTestObjects.Employee>> entries = map.entrySet(new SqlPredicate("active=true and age>44"));
        Assert.assertEquals(30, entries.size());
        nodeFactory.newInstances(getTestConfig(), 3);
        long startNow = Clock.currentTimeMillis();
        while (((Clock.currentTimeMillis()) - startNow) < 10000) {
            entries = map.entrySet(new SqlPredicate("active=true and age>44"));
            Assert.assertEquals(30, entries.size());
        } 
    }

    /**
     * test for issue #359
     */
    @Test(timeout = 4 * (TimeConstants.MINUTE))
    public void testIndexCleanupOnMigration() throws Exception {
        int nodeCount = 6;
        final int runCount = 500;
        final Config config = newConfigWithIndex("testMap", "name");
        executor = Executors.newFixedThreadPool(nodeCount);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int i = 0; i < nodeCount; i++) {
            HazelcastTestSupport.sleepMillis(((random.nextInt(((i + 1) * 100))) + 10));
            futures.add(executor.submit(new Runnable() {
                public void run() {
                    HazelcastInstance hz = nodeFactory.newHazelcastInstance(config);
                    IMap<Object, SampleTestObjects.Value> map = hz.getMap("testMap");
                    updateMapAndRunQuery(map, runCount);
                }
            }));
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

    /**
     * see Zendesk ticket #82
     */
    @Test(timeout = TimeConstants.MINUTE)
    public void testQueryWithIndexDuringJoin() throws InterruptedException {
        final String name = "test";
        final String findMe = "find-me";
        int nodeCount = 5;
        final int entryPerNode = 1000;
        final int modulo = 10;
        final CountDownLatch latch = new CountDownLatch(nodeCount);
        final Config config = newConfigWithIndex(name, "name");
        for (int i = 0; i < nodeCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HazelcastInstance hz = nodeFactory.newHazelcastInstance(config);
                    IMap<Object, Object> map = hz.getMap(name);
                    QueryIndexMigrationTest.fillMap(map, findMe, entryPerNode, modulo);
                    latch.countDown();
                }
            }).start();
        }
        Assert.assertTrue(latch.await(1, TimeUnit.MINUTES));
        Collection<HazelcastInstance> instances = nodeFactory.getAllHazelcastInstances();
        Assert.assertEquals(nodeCount, instances.size());
        HazelcastTestSupport.waitAllForSafeState(instances);
        int expected = (entryPerNode / modulo) * nodeCount;
        for (HazelcastInstance hz : instances) {
            IMap<Object, Object> map = hz.getMap(name);
            Predicate predicate = Predicates.equal("name", findMe);
            for (int i = 0; i < 10; i++) {
                int size = map.values(predicate).size();
                Assert.assertEquals(expected, size);
            }
        }
    }
}

