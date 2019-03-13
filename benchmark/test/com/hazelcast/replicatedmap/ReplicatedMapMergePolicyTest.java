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


import LifecycleEvent.LifecycleState;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.replicatedmap.merge.HigherHitsMapMergePolicy;
import com.hazelcast.replicatedmap.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.replicatedmap.merge.PassThroughMergePolicy;
import com.hazelcast.replicatedmap.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.SplitBrainTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(NightlyTest.class)
public class ReplicatedMapMergePolicyTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public ReplicatedMapMergePolicyTest.ReplicatedMapMergePolicyTestCase testCase;

    private TestHazelcastInstanceFactory factory;

    @Test
    public void testMapMergePolicy() {
        final String mapName = HazelcastTestSupport.randomMapName();
        Config config = newConfig(testCase.getMergePolicyClassName(), mapName);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        ReplicatedMapMergePolicyTest.TestLifeCycleListener lifeCycleListener = new ReplicatedMapMergePolicyTest.TestLifeCycleListener(1);
        h2.getLifecycleService().addLifecycleListener(lifeCycleListener);
        // wait for cluster to be formed before breaking the connection
        HazelcastTestSupport.waitAllForSafeState(h1, h2);
        SplitBrainTestSupport.blockCommunicationBetween(h1, h2);
        HazelcastTestSupport.closeConnectionBetween(h1, h2);
        HazelcastTestSupport.assertClusterSizeEventually(1, h1);
        HazelcastTestSupport.assertClusterSizeEventually(1, h2);
        ReplicatedMap<Object, Object> map1 = h1.getReplicatedMap(mapName);
        ReplicatedMap<Object, Object> map2 = h2.getReplicatedMap(mapName);
        final Map<Object, Object> expectedValues = testCase.populateMaps(map1, map2, h1);
        SplitBrainTestSupport.unblockCommunicationBetween(h1, h2);
        HazelcastTestSupport.assertOpenEventually(lifeCycleListener.latch);
        HazelcastTestSupport.assertClusterSizeEventually(2, h1, h2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                ReplicatedMap<Object, Object> mapTest = h1.getReplicatedMap(mapName);
                for (Map.Entry<Object, Object> entry : expectedValues.entrySet()) {
                    Assert.assertEquals(entry.getValue(), mapTest.get(entry.getKey()));
                }
            }
        });
    }

    private class TestLifeCycleListener implements LifecycleListener {
        CountDownLatch latch;

        TestLifeCycleListener(int countdown) {
            latch = new CountDownLatch(countdown);
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.MERGED)) {
                latch.countDown();
            }
        }
    }

    private interface ReplicatedMapMergePolicyTestCase {
        // Populate given maps with K-V pairs. Optional HZ instance is required by specific merge policies in order to
        // generate keys owned by the given instance.
        // return K-V pairs expected to be found in the merged map
        Map<Object, Object> populateMaps(ReplicatedMap<Object, Object> map1, ReplicatedMap<Object, Object> map2, HazelcastInstance instance);

        // return merge policy's class name
        String getMergePolicyClassName();
    }

    private static class LatestUpdateMergePolicyTestCase implements ReplicatedMapMergePolicyTest.ReplicatedMapMergePolicyTestCase {
        @Override
        public Map<Object, Object> populateMaps(ReplicatedMap<Object, Object> map1, ReplicatedMap<Object, Object> map2, HazelcastInstance instance) {
            map1.put("key1", "value");
            // prevent updating at the same time
            HazelcastTestSupport.sleepAtLeastSeconds(1);
            map2.put("key1", "LatestUpdatedValue");
            map2.put("key2", "value2");
            // prevent updating at the same time
            HazelcastTestSupport.sleepAtLeastSeconds(1);
            map1.put("key2", "LatestUpdatedValue2");
            Map<Object, Object> expectedValues = new HashMap<Object, Object>();
            expectedValues.put("key1", "LatestUpdatedValue");
            expectedValues.put("key2", "LatestUpdatedValue2");
            return expectedValues;
        }

        @Override
        public String getMergePolicyClassName() {
            return LatestUpdateMapMergePolicy.class.getName();
        }

        @Override
        public String toString() {
            return "LatestUpdateMapMergePolicy";
        }
    }

    private static class HighestHitsMergePolicyTestCase implements ReplicatedMapMergePolicyTest.ReplicatedMapMergePolicyTestCase {
        @Override
        public Map<Object, Object> populateMaps(ReplicatedMap<Object, Object> map1, ReplicatedMap<Object, Object> map2, HazelcastInstance instance) {
            map1.put("key1", "higherHitsValue");
            map1.put("key2", "value2");
            // increase hits number
            map1.get("key1");
            map1.get("key1");
            map2.put("key1", "value1");
            map2.put("key2", "higherHitsValue2");
            // increase hits number
            map2.get("key2");
            map2.get("key2");
            Map<Object, Object> expectedValues = new HashMap<Object, Object>();
            expectedValues.put("key1", "higherHitsValue");
            expectedValues.put("key2", "higherHitsValue2");
            return expectedValues;
        }

        @Override
        public String getMergePolicyClassName() {
            return HigherHitsMapMergePolicy.class.getName();
        }

        @Override
        public String toString() {
            return "HigherHitsMapMergePolicy";
        }
    }

    private static class PutIfAbsentMapMergePolicyTestCase implements ReplicatedMapMergePolicyTest.ReplicatedMapMergePolicyTestCase {
        @Override
        public Map<Object, Object> populateMaps(ReplicatedMap<Object, Object> map1, ReplicatedMap<Object, Object> map2, HazelcastInstance instance) {
            map1.put("key1", "PutIfAbsentValue1");
            map2.put("key1", "value");
            map2.put("key2", "PutIfAbsentValue2");
            Map<Object, Object> expectedValues = new HashMap<Object, Object>();
            expectedValues.put("key1", "PutIfAbsentValue1");
            expectedValues.put("key2", "PutIfAbsentValue2");
            return expectedValues;
        }

        @Override
        public String getMergePolicyClassName() {
            return PutIfAbsentMapMergePolicy.class.getName();
        }

        @Override
        public String toString() {
            return "PutIfAbsentMapMergePolicy";
        }
    }

    private static class PassThroughMapMergePolicyTestCase implements ReplicatedMapMergePolicyTest.ReplicatedMapMergePolicyTestCase {
        @Override
        public Map<Object, Object> populateMaps(ReplicatedMap<Object, Object> map1, ReplicatedMap<Object, Object> map2, HazelcastInstance instance) {
            Assert.assertNotNull(instance);
            String key = HazelcastTestSupport.generateKeyOwnedBy(instance);
            map1.put(key, "value");
            map2.put(key, "passThroughValue");
            Map<Object, Object> expectedValues = new HashMap<Object, Object>();
            expectedValues.put(key, "passThroughValue");
            return expectedValues;
        }

        @Override
        public String getMergePolicyClassName() {
            return PassThroughMergePolicy.class.getName();
        }

        @Override
        public String toString() {
            return "PassThroughMergePolicy";
        }
    }

    private static class CustomMergePolicyTestCase implements ReplicatedMapMergePolicyTest.ReplicatedMapMergePolicyTestCase {
        @Override
        public Map<Object, Object> populateMaps(ReplicatedMap<Object, Object> map1, ReplicatedMap<Object, Object> map2, HazelcastInstance instance) {
            Assert.assertNotNull(instance);
            String key = HazelcastTestSupport.generateKeyOwnedBy(instance);
            Integer value = 1;
            map1.put(key, "value");
            map2.put(key, value);
            Map<Object, Object> expectedValues = new HashMap<Object, Object>();
            expectedValues.put(key, value);
            return expectedValues;
        }

        @Override
        public String getMergePolicyClassName() {
            return CustomReplicatedMapMergePolicy.class.getName();
        }

        @Override
        public String toString() {
            return "CustomMergePolicy";
        }
    }
}

