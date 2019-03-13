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


import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterMembershipListenerTest extends HazelcastTestSupport {
    @Test(expected = NullPointerException.class)
    public void testAddMembershipListener_whenNullListener() {
        HazelcastInstance hz = createHazelcastInstance();
        Cluster cluster = hz.getCluster();
        cluster.addMembershipListener(null);
    }

    @Test
    public void testAddMembershipListener_whenListenerRegisteredTwice() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        Cluster cluster = hz1.getCluster();
        final MembershipListener membershipListener = Mockito.mock(MembershipListener.class);
        String id1 = cluster.addMembershipListener(membershipListener);
        String id2 = cluster.addMembershipListener(membershipListener);
        // first we check if the registration id's are different
        Assert.assertNotEquals(id1, id2);
        // an now we make sure that if a member joins the cluster, the same interface gets invoked twice.
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                // now we verify that the memberAdded method is called twice.
                Mockito.verify(membershipListener, Mockito.times(2)).memberAdded(ArgumentMatchers.any(MembershipEvent.class));
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveMembershipListener_whenNullListener() {
        HazelcastInstance hz = createHazelcastInstance();
        Cluster cluster = hz.getCluster();
        cluster.removeMembershipListener(null);
    }

    @Test
    public void testRemoveMembershipListener_whenNonExistingRegistrationId() {
        HazelcastInstance hz = createHazelcastInstance();
        Cluster cluster = hz.getCluster();
        boolean result = cluster.removeMembershipListener("notexist");
        Assert.assertFalse(result);
    }

    @Test
    public void testRemoveMembershipListener() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        Cluster cluster = hz1.getCluster();
        MembershipListener membershipListener = Mockito.mock(MembershipListener.class);
        String id = cluster.addMembershipListener(membershipListener);
        boolean removed = cluster.removeMembershipListener(id);
        Assert.assertTrue(removed);
        // now we add a member
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        // and verify that the listener isn't called.
        Mockito.verify(membershipListener, Mockito.never()).memberAdded(ArgumentMatchers.any(MembershipEvent.class));
    }

    @Test
    public void testMembershipListener() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        ClusterMembershipListenerTest.MembershipListenerImpl listener = new ClusterMembershipListenerTest.MembershipListenerImpl();
        hz1.getCluster().addMembershipListener(listener);
        // start a second instance
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        assertEventuallySizeAtLeast(listener.events, 1);
        assertMembershipAddedEvent(listener.events.get(0), hz2.getCluster().getLocalMember(), hz1.getCluster().getLocalMember(), hz2.getCluster().getLocalMember());
        // terminate the second instance
        Member member2 = hz2.getCluster().getLocalMember();
        hz2.shutdown();
        assertEventuallySizeAtLeast(listener.events, 2);
        assertMembershipRemovedEvent(listener.events.get(1), member2, hz1.getCluster().getLocalMember());
    }

    @Test
    public void testMembershipListenerSequentialInvocation() throws Exception {
        final int nodeCount = 10;
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        final CountDownLatch eventLatch = new CountDownLatch((nodeCount - 1));
        final CountDownLatch nodeLatch = new CountDownLatch((nodeCount - 1));
        Config config = new Config().addListenerConfig(new ListenerConfig().setImplementation(ClusterMembershipListenerTest.newAddMemberListener(eventLatch)));
        // first node has listener
        factory.newHazelcastInstance(config);
        for (int i = 1; i < nodeCount; i++) {
            HazelcastTestSupport.spawn(new Runnable() {
                public void run() {
                    factory.newHazelcastInstance(new Config());
                    nodeLatch.countDown();
                }
            });
        }
        HazelcastTestSupport.assertOpenEventually(nodeLatch);
        HazelcastTestSupport.assertOpenEventually(eventLatch);
    }

    @Test
    public void testInitialMembershipListener() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        ClusterMembershipListenerTest.InitialMembershipListenerImpl listener = new ClusterMembershipListenerTest.InitialMembershipListenerImpl();
        hz1.getCluster().addMembershipListener(listener);
        assertEventuallySizeAtLeast(listener.events, 1);
        assertInitialMembershipEvent(listener.events.get(0), hz1.getCluster().getLocalMember());
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        assertEventuallySizeAtLeast(listener.events, 2);
        assertMembershipAddedEvent(listener.events.get(1), hz2.getCluster().getLocalMember(), hz1.getCluster().getLocalMember(), hz2.getCluster().getLocalMember());
        Member member2 = hz2.getCluster().getLocalMember();
        hz2.shutdown();
        assertEventuallySizeAtLeast(listener.events, 3);
        assertMembershipRemovedEvent(listener.events.get(2), member2, hz1.getCluster().getLocalMember());
    }

    @Test
    public void testInitialMembershipListenerRegistrationWithMultipleInitialMembers() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        ClusterMembershipListenerTest.InitialMembershipListenerImpl listener = new ClusterMembershipListenerTest.InitialMembershipListenerImpl();
        hz1.getCluster().addMembershipListener(listener);
        assertEventuallySizeAtLeast(listener.events, 1);
        assertInitialMembershipEvent(listener.events.get(0), hz1.getCluster().getLocalMember(), hz2.getCluster().getLocalMember());
    }

    private static class MembershipListenerImpl implements MembershipListener {
        private List<EventObject> events = Collections.synchronizedList(new LinkedList<EventObject>());

        public void memberAdded(MembershipEvent e) {
            events.add(e);
        }

        public void memberRemoved(MembershipEvent e) {
            events.add(e);
        }

        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        }
    }

    private static class InitialMembershipListenerImpl implements InitialMembershipListener {
        private List<EventObject> events = Collections.synchronizedList(new LinkedList<EventObject>());

        public void init(InitialMembershipEvent e) {
            events.add(e);
        }

        public void memberAdded(MembershipEvent e) {
            events.add(e);
        }

        public void memberRemoved(MembershipEvent e) {
            events.add(e);
        }

        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        }

        public void assertEventCount(int expected) {
            Assert.assertEquals(expected, events.size());
        }
    }
}

