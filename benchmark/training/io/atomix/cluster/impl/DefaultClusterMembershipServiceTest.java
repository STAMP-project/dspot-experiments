/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.cluster.impl;


import ClusterMembershipEvent.Type.METADATA_CHANGED;
import io.atomix.cluster.BootstrapService;
import io.atomix.cluster.ClusterMembershipEvent;
import io.atomix.cluster.ClusterMembershipEventListener;
import io.atomix.cluster.ManagedClusterMembershipService;
import io.atomix.cluster.Member;
import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.TestBootstrapService;
import io.atomix.cluster.messaging.impl.TestBroadcastServiceFactory;
import io.atomix.cluster.messaging.impl.TestMessagingServiceFactory;
import io.atomix.cluster.messaging.impl.TestUnicastServiceFactory;
import io.atomix.cluster.protocol.HeartbeatMembershipProtocolConfig;
import io.atomix.utils.Version;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Default cluster service test.
 */
public class DefaultClusterMembershipServiceTest {
    @Test
    public void testClusterService() throws Exception {
        TestMessagingServiceFactory messagingServiceFactory = new TestMessagingServiceFactory();
        TestUnicastServiceFactory unicastServiceFactory = new TestUnicastServiceFactory();
        TestBroadcastServiceFactory broadcastServiceFactory = new TestBroadcastServiceFactory();
        Collection<Node> bootstrapLocations = buildBootstrapNodes(3);
        Member localMember1 = buildMember(1);
        BootstrapService bootstrapService1 = new TestBootstrapService(messagingServiceFactory.newMessagingService(localMember1.address()).start().join(), unicastServiceFactory.newUnicastService(localMember1.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService clusterService1 = new DefaultClusterMembershipService(localMember1, Version.from("1.0.0"), new DefaultNodeDiscoveryService(bootstrapService1, localMember1, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), bootstrapService1, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig().setFailureTimeout(Duration.ofSeconds(2))));
        Member localMember2 = buildMember(2);
        BootstrapService bootstrapService2 = new TestBootstrapService(messagingServiceFactory.newMessagingService(localMember2.address()).start().join(), unicastServiceFactory.newUnicastService(localMember2.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService clusterService2 = new DefaultClusterMembershipService(localMember2, Version.from("1.0.0"), new DefaultNodeDiscoveryService(bootstrapService2, localMember2, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), bootstrapService2, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig().setFailureTimeout(Duration.ofSeconds(2))));
        Member localMember3 = buildMember(3);
        BootstrapService bootstrapService3 = new TestBootstrapService(messagingServiceFactory.newMessagingService(localMember3.address()).start().join(), unicastServiceFactory.newUnicastService(localMember3.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService clusterService3 = new DefaultClusterMembershipService(localMember3, Version.from("1.0.1"), new DefaultNodeDiscoveryService(bootstrapService3, localMember3, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), bootstrapService3, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig().setFailureTimeout(Duration.ofSeconds(2))));
        Assert.assertNull(clusterService1.getMember(MemberId.from("1")));
        Assert.assertNull(clusterService1.getMember(MemberId.from("2")));
        Assert.assertNull(clusterService1.getMember(MemberId.from("3")));
        CompletableFuture.allOf(new CompletableFuture[]{ clusterService1.start(), clusterService2.start(), clusterService3.start() }).join();
        Thread.sleep(5000);
        Assert.assertEquals(3, clusterService1.getMembers().size());
        Assert.assertEquals(3, clusterService2.getMembers().size());
        Assert.assertEquals(3, clusterService3.getMembers().size());
        Assert.assertTrue(clusterService1.getLocalMember().isActive());
        Assert.assertTrue(clusterService1.getMember(MemberId.from("1")).isActive());
        Assert.assertTrue(clusterService1.getMember(MemberId.from("2")).isActive());
        Assert.assertTrue(clusterService1.getMember(MemberId.from("3")).isActive());
        Assert.assertEquals("1.0.0", clusterService1.getMember("1").version().toString());
        Assert.assertEquals("1.0.0", clusterService1.getMember("2").version().toString());
        Assert.assertEquals("1.0.1", clusterService1.getMember("3").version().toString());
        Member anonymousMember = buildMember(4);
        BootstrapService ephemeralBootstrapService = new TestBootstrapService(messagingServiceFactory.newMessagingService(anonymousMember.address()).start().join(), unicastServiceFactory.newUnicastService(anonymousMember.address()).start().join(), broadcastServiceFactory.newBroadcastService().start().join());
        ManagedClusterMembershipService ephemeralClusterService = new DefaultClusterMembershipService(anonymousMember, Version.from("1.1.0"), new DefaultNodeDiscoveryService(ephemeralBootstrapService, anonymousMember, new io.atomix.cluster.discovery.BootstrapDiscoveryProvider(bootstrapLocations)), ephemeralBootstrapService, new io.atomix.cluster.protocol.HeartbeatMembershipProtocol(new HeartbeatMembershipProtocolConfig().setFailureTimeout(Duration.ofSeconds(2))));
        Assert.assertFalse(ephemeralClusterService.getLocalMember().isActive());
        Assert.assertNull(ephemeralClusterService.getMember(MemberId.from("1")));
        Assert.assertNull(ephemeralClusterService.getMember(MemberId.from("2")));
        Assert.assertNull(ephemeralClusterService.getMember(MemberId.from("3")));
        Assert.assertNull(ephemeralClusterService.getMember(MemberId.from("4")));
        Assert.assertNull(ephemeralClusterService.getMember(MemberId.from("5")));
        ephemeralClusterService.start().join();
        Thread.sleep(1000);
        Assert.assertEquals(4, clusterService1.getMembers().size());
        Assert.assertEquals(4, clusterService2.getMembers().size());
        Assert.assertEquals(4, clusterService3.getMembers().size());
        Assert.assertEquals(4, ephemeralClusterService.getMembers().size());
        Assert.assertEquals("1.0.0", clusterService1.getMember("1").version().toString());
        Assert.assertEquals("1.0.0", clusterService1.getMember("2").version().toString());
        Assert.assertEquals("1.0.1", clusterService1.getMember("3").version().toString());
        Assert.assertEquals("1.1.0", clusterService1.getMember("4").version().toString());
        clusterService1.stop().join();
        Thread.sleep(5000);
        Assert.assertEquals(3, clusterService2.getMembers().size());
        Assert.assertNull(clusterService2.getMember(MemberId.from("1")));
        Assert.assertTrue(clusterService2.getMember(MemberId.from("2")).isActive());
        Assert.assertTrue(clusterService2.getMember(MemberId.from("3")).isActive());
        Assert.assertTrue(clusterService2.getMember(MemberId.from("4")).isActive());
        ephemeralClusterService.stop().join();
        Thread.sleep(5000);
        Assert.assertEquals(2, clusterService2.getMembers().size());
        Assert.assertNull(clusterService2.getMember(MemberId.from("1")));
        Assert.assertTrue(clusterService2.getMember(MemberId.from("2")).isActive());
        Assert.assertTrue(clusterService2.getMember(MemberId.from("3")).isActive());
        Assert.assertNull(clusterService2.getMember(MemberId.from("4")));
        Thread.sleep(2500);
        Assert.assertEquals(2, clusterService2.getMembers().size());
        Assert.assertNull(clusterService2.getMember(MemberId.from("1")));
        Assert.assertTrue(clusterService2.getMember(MemberId.from("2")).isActive());
        Assert.assertTrue(clusterService2.getMember(MemberId.from("3")).isActive());
        Assert.assertNull(clusterService2.getMember(MemberId.from("4")));
        DefaultClusterMembershipServiceTest.TestClusterMembershipEventListener eventListener = new DefaultClusterMembershipServiceTest.TestClusterMembershipEventListener();
        clusterService2.addListener(eventListener);
        ClusterMembershipEvent event;
        clusterService3.getLocalMember().properties().put("foo", "bar");
        event = eventListener.nextEvent();
        Assert.assertEquals(METADATA_CHANGED, event.type());
        Assert.assertEquals("bar", event.subject().properties().get("foo"));
        clusterService3.getLocalMember().properties().put("foo", "baz");
        event = eventListener.nextEvent();
        Assert.assertEquals(METADATA_CHANGED, event.type());
        Assert.assertEquals("baz", event.subject().properties().get("foo"));
        CompletableFuture.allOf(new CompletableFuture[]{ clusterService1.stop(), clusterService2.stop(), clusterService3.stop() }).join();
    }

    private class TestClusterMembershipEventListener implements ClusterMembershipEventListener {
        private BlockingQueue<ClusterMembershipEvent> queue = new ArrayBlockingQueue<ClusterMembershipEvent>(10);

        @Override
        public void event(ClusterMembershipEvent event) {
            queue.add(event);
        }

        ClusterMembershipEvent nextEvent() {
            try {
                return queue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}

