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
package com.hazelcast.cluster;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.util.AbstractClockTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class SystemClockChangeTest extends AbstractClockTest {
    @Test
    public void testCluster_whenMasterClockIsBehind() {
        AbstractClockTest.setClockOffset(TimeUnit.MINUTES.toMillis((-60)));
        startIsolatedNode();
        AbstractClockTest.resetClock();
        HazelcastInstance hz = startNode();
        HazelcastTestSupport.assertClusterSize(2, hz);
        AbstractClockTest.assertClusterTime(isolatedNode, hz);
    }

    @Test
    public void testCluster_whenMasterClockIsAhead() {
        AbstractClockTest.setClockOffset(TimeUnit.MINUTES.toMillis(60));
        startIsolatedNode();
        AbstractClockTest.resetClock();
        HazelcastInstance hz = startNode();
        HazelcastTestSupport.assertClusterSize(2, hz);
        AbstractClockTest.assertClusterTime(isolatedNode, hz);
    }

    @Test
    public void testCluster_whenMasterChanges() {
        AbstractClockTest.setClockOffset(TimeUnit.MINUTES.toMillis(60));
        startIsolatedNode();
        AbstractClockTest.resetClock();
        HazelcastInstance hz1 = startNode();
        HazelcastInstance hz2 = startNode();
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2);
        shutdownIsolatedNode();
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz2);
        AbstractClockTest.assertClusterTime(hz1, hz2);
    }

    @Test
    public void testCluster_whenMasterClockJumpsForward() {
        AbstractClockTest.setJumpingClock(TimeUnit.MINUTES.toMillis(30));
        startIsolatedNode();
        AbstractClockTest.resetClock();
        HazelcastInstance hz = startNode();
        AbstractClockTest.assertClusterSizeAlways(2, hz);
        AbstractClockTest.assertClusterTime(hz, isolatedNode);
    }

    @Test
    public void testCluster_whenMasterClockJumpsBackward() {
        AbstractClockTest.setJumpingClock(TimeUnit.MINUTES.toMillis((-30)));
        startIsolatedNode();
        AbstractClockTest.resetClock();
        HazelcastInstance hz = startNode();
        AbstractClockTest.assertClusterSizeAlways(2, hz);
        AbstractClockTest.assertClusterTime(hz, isolatedNode);
    }

    @Test
    public void testCluster_whenSlaveClockJumpsForward() {
        HazelcastInstance hz = startNode();
        AbstractClockTest.setJumpingClock(TimeUnit.MINUTES.toMillis(30));
        startIsolatedNode();
        AbstractClockTest.assertClusterSizeAlways(2, hz);
        AbstractClockTest.assertClusterTime(System.currentTimeMillis(), hz);
        AbstractClockTest.assertClusterTime(System.currentTimeMillis(), isolatedNode);
    }

    @Test
    public void testCluster_whenSlaveClockJumpsBackward() {
        HazelcastInstance hz = startNode();
        AbstractClockTest.setJumpingClock(TimeUnit.MINUTES.toMillis((-30)));
        startIsolatedNode();
        AbstractClockTest.assertClusterSizeAlways(2, hz);
        AbstractClockTest.assertClusterTime(System.currentTimeMillis(), hz);
        AbstractClockTest.assertClusterTime(System.currentTimeMillis(), isolatedNode);
    }
}

