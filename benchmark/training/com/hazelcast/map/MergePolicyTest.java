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


import LifecycleEvent.LifecycleState;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.map.merge.HigherHitsMapMergePolicy;
import com.hazelcast.map.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.hazelcast.map.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class MergePolicyTest extends HazelcastTestSupport {
    @Test
    public void testLatestUpdateMapMergePolicy() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = newConfig(LatestUpdateMapMergePolicy.class.getName(), mapName);
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(h1, h2);
        MergePolicyTest.TestMemberShipListener memberShipListener = new MergePolicyTest.TestMemberShipListener(1);
        h2.getCluster().addMembershipListener(memberShipListener);
        CountDownLatch mergeBlockingLatch = new CountDownLatch(1);
        MergePolicyTest.TestLifeCycleListener lifeCycleListener = new MergePolicyTest.TestLifeCycleListener(1, mergeBlockingLatch);
        h2.getLifecycleService().addLifecycleListener(lifeCycleListener);
        HazelcastTestSupport.closeConnectionBetween(h1, h2);
        HazelcastTestSupport.assertOpenEventually(memberShipListener.memberRemovedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(1, h1);
        HazelcastTestSupport.assertClusterSizeEventually(1, h2);
        IMap<Object, Object> map1 = h1.getMap(mapName);
        IMap<Object, Object> map2 = h2.getMap(mapName);
        map1.put("key1", "value");
        // prevent updating at the same time
        HazelcastTestSupport.sleepAtLeastMillis(1000);
        map2.put("key1", "LatestUpdatedValue");
        map2.put("key2", "value2");
        // prevent updating at the same time
        HazelcastTestSupport.sleepAtLeastMillis(1000);
        map1.put("key2", "LatestUpdatedValue2");
        // allow merge process to continue
        mergeBlockingLatch.countDown();
        HazelcastTestSupport.assertOpenEventually(lifeCycleListener.mergeFinishedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(2, h1, h2);
        IMap<Object, Object> mapTest = h1.getMap(mapName);
        Assert.assertEquals("LatestUpdatedValue", mapTest.get("key1"));
        Assert.assertEquals("LatestUpdatedValue2", mapTest.get("key2"));
    }

    @Test
    public void testHigherHitsMapMergePolicy() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = newConfig(HigherHitsMapMergePolicy.class.getName(), mapName);
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(h1, h2);
        MergePolicyTest.TestMemberShipListener memberShipListener = new MergePolicyTest.TestMemberShipListener(1);
        h2.getCluster().addMembershipListener(memberShipListener);
        CountDownLatch mergeBlockingLatch = new CountDownLatch(1);
        MergePolicyTest.TestLifeCycleListener lifeCycleListener = new MergePolicyTest.TestLifeCycleListener(1, mergeBlockingLatch);
        h2.getLifecycleService().addLifecycleListener(lifeCycleListener);
        HazelcastTestSupport.closeConnectionBetween(h1, h2);
        HazelcastTestSupport.assertOpenEventually(memberShipListener.memberRemovedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(1, h1);
        HazelcastTestSupport.assertClusterSizeEventually(1, h2);
        IMap<Object, Object> map1 = h1.getMap(mapName);
        map1.put("key1", "higherHitsValue");
        map1.put("key2", "value2");
        // increase hits number
        map1.get("key1");
        map1.get("key1");
        IMap<Object, Object> map2 = h2.getMap(mapName);
        map2.put("key1", "value1");
        map2.put("key2", "higherHitsValue2");
        // increase hits number
        map2.get("key2");
        map2.get("key2");
        // allow merge process to continue
        mergeBlockingLatch.countDown();
        HazelcastTestSupport.assertOpenEventually(lifeCycleListener.mergeFinishedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(2, h1, h2);
        IMap<Object, Object> mapTest = h2.getMap(mapName);
        Assert.assertEquals("higherHitsValue", mapTest.get("key1"));
        Assert.assertEquals("higherHitsValue2", mapTest.get("key2"));
    }

    @Test
    public void testPutIfAbsentMapMergePolicy() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = newConfig(PutIfAbsentMapMergePolicy.class.getName(), mapName);
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(h1, h2);
        MergePolicyTest.TestMemberShipListener memberShipListener = new MergePolicyTest.TestMemberShipListener(1);
        h2.getCluster().addMembershipListener(memberShipListener);
        CountDownLatch mergeBlockingLatch = new CountDownLatch(1);
        MergePolicyTest.TestLifeCycleListener lifeCycleListener = new MergePolicyTest.TestLifeCycleListener(1, mergeBlockingLatch);
        h2.getLifecycleService().addLifecycleListener(lifeCycleListener);
        HazelcastTestSupport.closeConnectionBetween(h1, h2);
        HazelcastTestSupport.assertOpenEventually(memberShipListener.memberRemovedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(1, h1);
        HazelcastTestSupport.assertClusterSizeEventually(1, h2);
        IMap<Object, Object> map1 = h1.getMap(mapName);
        map1.put("key1", "PutIfAbsentValue1");
        IMap<Object, Object> map2 = h2.getMap(mapName);
        map2.put("key1", "value");
        map2.put("key2", "PutIfAbsentValue2");
        // allow merge process to continue
        mergeBlockingLatch.countDown();
        HazelcastTestSupport.assertOpenEventually(lifeCycleListener.mergeFinishedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(2, h1, h2);
        IMap<Object, Object> mapTest = h2.getMap(mapName);
        Assert.assertEquals("PutIfAbsentValue1", mapTest.get("key1"));
        Assert.assertEquals("PutIfAbsentValue2", mapTest.get("key2"));
    }

    @Test
    public void testPassThroughMapMergePolicy() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = newConfig(PassThroughMergePolicy.class.getName(), mapName);
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(h1, h2);
        MergePolicyTest.TestMemberShipListener memberShipListener = new MergePolicyTest.TestMemberShipListener(1);
        h2.getCluster().addMembershipListener(memberShipListener);
        CountDownLatch mergeBlockingLatch = new CountDownLatch(1);
        MergePolicyTest.TestLifeCycleListener lifeCycleListener = new MergePolicyTest.TestLifeCycleListener(1, mergeBlockingLatch);
        h2.getLifecycleService().addLifecycleListener(lifeCycleListener);
        HazelcastTestSupport.closeConnectionBetween(h1, h2);
        HazelcastTestSupport.assertOpenEventually(memberShipListener.memberRemovedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(1, h1);
        HazelcastTestSupport.assertClusterSizeEventually(1, h2);
        IMap<Object, Object> map1 = h1.getMap(mapName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(h1);
        map1.put(key, "value");
        IMap<Object, Object> map2 = h2.getMap(mapName);
        map2.put(key, "passThroughValue");
        // allow merge process to continue
        mergeBlockingLatch.countDown();
        HazelcastTestSupport.assertOpenEventually(lifeCycleListener.mergeFinishedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(2, h1, h2);
        IMap<Object, Object> mapTest = h2.getMap(mapName);
        Assert.assertEquals("passThroughValue", mapTest.get(key));
    }

    @Test
    public void testCustomMergePolicy() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = newConfig(TestCustomMapMergePolicy.class.getName(), mapName);
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(h1, h2);
        MergePolicyTest.TestMemberShipListener memberShipListener = new MergePolicyTest.TestMemberShipListener(1);
        h2.getCluster().addMembershipListener(memberShipListener);
        CountDownLatch mergeBlockingLatch = new CountDownLatch(1);
        MergePolicyTest.TestLifeCycleListener lifeCycleListener = new MergePolicyTest.TestLifeCycleListener(1, mergeBlockingLatch);
        h2.getLifecycleService().addLifecycleListener(lifeCycleListener);
        HazelcastTestSupport.closeConnectionBetween(h1, h2);
        HazelcastTestSupport.assertOpenEventually(memberShipListener.memberRemovedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(1, h1);
        HazelcastTestSupport.assertClusterSizeEventually(1, h2);
        IMap<Object, Object> map1 = h1.getMap(mapName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(h1);
        map1.put(key, "value");
        IMap<Object, Object> map2 = h2.getMap(mapName);
        map2.put(key, 1);
        // allow merge process to continue
        mergeBlockingLatch.countDown();
        HazelcastTestSupport.assertOpenEventually(lifeCycleListener.mergeFinishedLatch);
        HazelcastTestSupport.assertClusterSizeEventually(2, h1, h2);
        IMap<Object, Object> mapTest = h2.getMap(mapName);
        Assert.assertNotNull(mapTest.get(key));
        Assert.assertTrue(((mapTest.get(key)) instanceof Integer));
    }

    private class TestLifeCycleListener implements LifecycleListener {
        final CountDownLatch mergeFinishedLatch;

        final CountDownLatch mergeBlockingLatch;

        TestLifeCycleListener(int countdown, CountDownLatch mergeBlockingLatch) {
            this.mergeFinishedLatch = new CountDownLatch(countdown);
            this.mergeBlockingLatch = mergeBlockingLatch;
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.MERGING)) {
                try {
                    mergeBlockingLatch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw ExceptionUtil.rethrow(e);
                }
            } else
                if ((event.getState()) == (LifecycleState.MERGED)) {
                    mergeFinishedLatch.countDown();
                }

        }
    }

    private class TestMemberShipListener implements MembershipListener {
        final CountDownLatch memberRemovedLatch;

        TestMemberShipListener(int countdown) {
            memberRemovedLatch = new CountDownLatch(countdown);
        }

        @Override
        public void memberAdded(MembershipEvent membershipEvent) {
        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
            memberRemovedLatch.countDown();
        }

        @Override
        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        }
    }
}

