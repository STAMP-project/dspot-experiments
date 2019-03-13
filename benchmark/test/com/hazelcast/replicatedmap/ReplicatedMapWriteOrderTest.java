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
package com.hazelcast.replicatedmap;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapWriteOrderTest extends ReplicatedMapAbstractTest {
    private int nodeCount;

    private int operations;

    private int keyCount;

    public ReplicatedMapWriteOrderTest(int nodeCount, int operations, int keyCount) {
        this.nodeCount = nodeCount;
        this.operations = operations;
        this.keyCount = keyCount;
    }

    @Test
    public void testDataIntegrity() {
        System.out.println(("nodeCount = " + (nodeCount)));
        System.out.println(("operations = " + (operations)));
        System.out.println(("keyCount = " + (keyCount)));
        Config config = new Config();
        config.getReplicatedMapConfig("test").setReplicationDelayMillis(0);
        TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory(nodeCount);
        final HazelcastInstance[] instances = factory.newInstances(config);
        String replicatedMapName = "test";
        final List<ReplicatedMap<String, Object>> maps = createMapOnEachInstance(instances, replicatedMapName);
        ArrayList<Integer> keys = generateRandomIntegerList(keyCount);
        Thread[] threads = createThreads(nodeCount, maps, keys, operations);
        for (Thread thread : threads) {
            thread.start();
        }
        HazelcastTestSupport.assertJoinable(threads);
        for (int i = 0; i < (keyCount); i++) {
            final String key = "foo-" + (keys.get(i));
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() throws Exception {
                    System.out.println("---------------------");
                    System.out.println(("key = " + key));
                    printValues();
                    assertValuesAreEqual();
                }

                private void printValues() throws Exception {
                    for (int j = 0; j < (maps.size()); j++) {
                        ReplicatedMap map = maps.get(j);
                        System.out.println(((((("value[" + j) + "] = ") + (map.get(key))) + ", store version: ") + (getStore(map, key).getVersion())));
                    }
                }

                private void assertValuesAreEqual() {
                    for (int i = 0; i < ((maps.size()) - 1); i++) {
                        ReplicatedMap map1 = maps.get(i);
                        ReplicatedMap map2 = maps.get((i + 1));
                        Object v1 = map1.get(key);
                        Object v2 = map2.get(key);
                        Assert.assertNotNull(v1);
                        Assert.assertNotNull(v2);
                        Assert.assertEquals(v1, v2);
                    }
                }
            }, 120);
        }
    }
}

