/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.core;


import ClusterMembershipEvent.Type.MEMBER_ADDED;
import ClusterMembershipEvent.Type.MEMBER_REMOVED;
import ClusterMembershipEvent.Type.REACHABILITY_CHANGED;
import io.atomix.cluster.ClusterMembershipEvent;
import io.atomix.cluster.ClusterMembershipEventListener;
import io.atomix.cluster.Member;
import io.atomix.core.barrier.DistributedCyclicBarrierType;
import io.atomix.core.counter.AtomicCounter;
import io.atomix.core.counter.AtomicCounterType;
import io.atomix.core.counter.DistributedCounterType;
import io.atomix.core.election.LeaderElectionType;
import io.atomix.core.election.LeaderElectorType;
import io.atomix.core.idgenerator.AtomicIdGeneratorType;
import io.atomix.core.list.DistributedListType;
import io.atomix.core.lock.AtomicLockType;
import io.atomix.core.lock.DistributedLockType;
import io.atomix.core.log.DistributedLog;
import io.atomix.core.log.DistributedLogPartition;
import io.atomix.core.map.AtomicCounterMapType;
import io.atomix.core.map.AtomicMapType;
import io.atomix.core.map.AtomicNavigableMapType;
import io.atomix.core.map.AtomicSortedMapType;
import io.atomix.core.map.DistributedMap;
import io.atomix.core.map.DistributedMapType;
import io.atomix.core.map.DistributedNavigableMapType;
import io.atomix.core.map.DistributedSortedMapType;
import io.atomix.core.multimap.AtomicMultimapType;
import io.atomix.core.multimap.DistributedMultimapType;
import io.atomix.core.multiset.DistributedMultisetType;
import io.atomix.core.profile.ConsensusProfile;
import io.atomix.core.profile.Profile;
import io.atomix.core.queue.DistributedQueueType;
import io.atomix.core.semaphore.AtomicSemaphoreType;
import io.atomix.core.semaphore.DistributedSemaphoreType;
import io.atomix.core.set.DistributedNavigableSetType;
import io.atomix.core.set.DistributedSetType;
import io.atomix.core.set.DistributedSortedSetType;
import io.atomix.core.tree.AtomicDocumentTreeType;
import io.atomix.core.value.AtomicValueType;
import io.atomix.core.value.DistributedValueType;
import io.atomix.core.workqueue.WorkQueueType;
import io.atomix.protocols.log.DistributedLogProtocol;
import io.atomix.protocols.log.partition.LogPartitionGroup;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.partition.RaftPartitionGroup;
import io.atomix.utils.concurrent.Futures;
import io.atomix.utils.config.ConfigurationException;
import io.atomix.utils.net.Address;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Atomix test.
 */
public class AtomixTest extends AbstractAtomixTest {
    private List<Atomix> instances;

    /**
     * Tests scaling up a cluster.
     */
    @Test
    public void testScaleUpPersistent() throws Exception {
        Atomix atomix1 = startAtomix(1, Arrays.asList(1), ConsensusProfile.builder().withMembers("1").withDataPath(new File(AbstractAtomixTest.DATA_DIR, "scale-up")).build()).get(30, TimeUnit.SECONDS);
        Atomix atomix2 = startAtomix(2, Arrays.asList(1, 2), Profile.client()).get(30, TimeUnit.SECONDS);
        Atomix atomix3 = startAtomix(3, Arrays.asList(1, 2, 3), Profile.client()).get(30, TimeUnit.SECONDS);
    }

    /**
     * Tests scaling up a cluster.
     */
    @Test
    public void testBootstrapDataGrid() throws Exception {
        List<CompletableFuture<Atomix>> futures = new ArrayList<>(3);
        futures.add(startAtomix(1, Arrays.asList(2), Profile.dataGrid()));
        futures.add(startAtomix(2, Arrays.asList(1), Profile.dataGrid()));
        futures.add(startAtomix(3, Arrays.asList(1), Profile.dataGrid()));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get(30, TimeUnit.SECONDS);
    }

    /**
     * Tests scaling up a cluster.
     */
    @Test
    public void testScaleUpEphemeral() throws Exception {
        Atomix atomix1 = startAtomix(1, Arrays.asList(2), Profile.dataGrid()).get(30, TimeUnit.SECONDS);
        Atomix atomix2 = startAtomix(2, Arrays.asList(1), Profile.dataGrid()).get(30, TimeUnit.SECONDS);
        Atomix atomix3 = startAtomix(3, Arrays.asList(1), Profile.dataGrid()).get(30, TimeUnit.SECONDS);
    }

    @Test
    public void testDiscoverData() throws Exception {
        Address multicastAddress = Address.from("230.0.0.1", AbstractAtomixTest.findAvailablePort(1234));
        Atomix atomix1 = startAtomix(1, Arrays.asList(), ( builder) -> builder.withProfiles(Profile.dataGrid()).withMulticastEnabled().withMulticastAddress(multicastAddress).build()).get(30, TimeUnit.SECONDS);
        Atomix atomix2 = startAtomix(2, Arrays.asList(), ( builder) -> builder.withProfiles(Profile.dataGrid()).withMulticastEnabled().withMulticastAddress(multicastAddress).build()).get(30, TimeUnit.SECONDS);
        Atomix atomix3 = startAtomix(3, Arrays.asList(), ( builder) -> builder.withProfiles(Profile.dataGrid()).withMulticastEnabled().withMulticastAddress(multicastAddress).build()).get(30, TimeUnit.SECONDS);
        Thread.sleep(5000);
        Assert.assertEquals(3, atomix1.getMembershipService().getMembers().size());
        Assert.assertEquals(3, atomix2.getMembershipService().getMembers().size());
        Assert.assertEquals(3, atomix3.getMembershipService().getMembers().size());
    }

    @Test
    public void testLogPrimitive() throws Exception {
        CompletableFuture<Atomix> future1 = startAtomix(1, Arrays.asList(1, 2), ( builder) -> builder.withManagementGroup(RaftPartitionGroup.builder("system").withNumPartitions(1).withMembers(String.valueOf(1), String.valueOf(2)).withDataDirectory(new File(new File(AbstractAtomixTest.DATA_DIR, "log"), "1")).build()).withPartitionGroups(LogPartitionGroup.builder("log").withNumPartitions(3).build()).build());
        CompletableFuture<Atomix> future2 = startAtomix(2, Arrays.asList(1, 2), ( builder) -> builder.withManagementGroup(RaftPartitionGroup.builder("system").withNumPartitions(1).withMembers(String.valueOf(1), String.valueOf(2)).withDataDirectory(new File(new File(AbstractAtomixTest.DATA_DIR, "log"), "2")).build()).withPartitionGroups(LogPartitionGroup.builder("log").withNumPartitions(3).build()).build());
        Atomix atomix1 = future1.get();
        Atomix atomix2 = future2.get();
        DistributedLog<String> log1 = atomix1.<String>logBuilder().withProtocol(DistributedLogProtocol.builder().build()).build();
        DistributedLog<String> log2 = atomix2.<String>logBuilder().withProtocol(DistributedLogProtocol.builder().build()).build();
        Assert.assertEquals(3, log1.getPartitions().size());
        Assert.assertEquals(1, log1.getPartitions().get(0).id());
        Assert.assertEquals(2, log1.getPartitions().get(1).id());
        Assert.assertEquals(3, log1.getPartitions().get(2).id());
        Assert.assertEquals(1, log2.getPartition(1).id());
        Assert.assertEquals(2, log2.getPartition(2).id());
        Assert.assertEquals(3, log2.getPartition(3).id());
        DistributedLogPartition<String> partition1 = log1.getPartition("Hello world!");
        DistributedLogPartition<String> partition2 = log2.getPartition("Hello world!");
        Assert.assertEquals(partition1.id(), partition2.id());
        CountDownLatch latch = new CountDownLatch(2);
        partition2.consume(( record) -> {
            assertEquals("Hello world!", record.value());
            latch.countDown();
        });
        log2.consume(( record) -> {
            assertEquals("Hello world!", record.value());
            latch.countDown();
        });
        partition1.produce("Hello world!");
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void testLogBasedPrimitives() throws Exception {
        CompletableFuture<Atomix> future1 = startAtomix(1, Arrays.asList(1, 2), ( builder) -> builder.withManagementGroup(RaftPartitionGroup.builder("system").withNumPartitions(1).withMembers(String.valueOf(1), String.valueOf(2)).withDataDirectory(new File(new File(AbstractAtomixTest.DATA_DIR, "log"), "1")).build()).withPartitionGroups(LogPartitionGroup.builder("log").withNumPartitions(3).build()).build());
        CompletableFuture<Atomix> future2 = startAtomix(2, Arrays.asList(1, 2), ( builder) -> builder.withManagementGroup(RaftPartitionGroup.builder("system").withNumPartitions(1).withMembers(String.valueOf(1), String.valueOf(2)).withDataDirectory(new File(new File(AbstractAtomixTest.DATA_DIR, "log"), "2")).build()).withPartitionGroups(LogPartitionGroup.builder("log").withNumPartitions(3).build()).build());
        Atomix atomix1 = future1.get();
        Atomix atomix2 = future2.get();
        DistributedMap<String, String> map1 = atomix1.<String, String>mapBuilder("test-map").withProtocol(DistributedLogProtocol.builder().build()).build();
        DistributedMap<String, String> map2 = atomix2.<String, String>mapBuilder("test-map").withProtocol(DistributedLogProtocol.builder().build()).build();
        CountDownLatch latch = new CountDownLatch(1);
        map2.addListener(( event) -> {
            map2.async().get("foo").thenAccept(( value) -> {
                assertEquals("bar", value);
                latch.countDown();
            });
        });
        map1.put("foo", "bar");
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
        AtomicCounter counter1 = atomix1.atomicCounterBuilder("test-counter").withProtocol(DistributedLogProtocol.builder().build()).build();
        AtomicCounter counter2 = atomix2.atomicCounterBuilder("test-counter").withProtocol(DistributedLogProtocol.builder().build()).build();
        Assert.assertEquals(1, counter1.incrementAndGet());
        Assert.assertEquals(1, counter1.get());
        Thread.sleep(1000);
        Assert.assertEquals(1, counter2.get());
        Assert.assertEquals(2, counter2.incrementAndGet());
    }

    @Test
    public void testStopStartConsensus() throws Exception {
        Atomix atomix1 = startAtomix(1, Arrays.asList(1), ConsensusProfile.builder().withMembers("1").withDataPath(new File(AbstractAtomixTest.DATA_DIR, "start-stop-consensus")).build()).get(30, TimeUnit.SECONDS);
        atomix1.stop().get(30, TimeUnit.SECONDS);
        try {
            atomix1.start().get(30, TimeUnit.SECONDS);
            Assert.fail("Expected ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
            Assert.assertEquals("Atomix instance shutdown", ex.getCause().getMessage());
        }
    }

    /**
     * Tests scaling down a cluster.
     */
    @Test
    public void testScaleDownPersistent() throws Exception {
        List<CompletableFuture<Atomix>> futures = new ArrayList<>();
        futures.add(startAtomix(1, Arrays.asList(1, 2, 3), Profile.dataGrid()));
        futures.add(startAtomix(2, Arrays.asList(1, 2, 3), Profile.dataGrid()));
        futures.add(startAtomix(3, Arrays.asList(1, 2, 3), Profile.dataGrid()));
        Futures.allOf(futures).get(30, TimeUnit.SECONDS);
        AtomixTest.TestClusterMembershipEventListener eventListener1 = new AtomixTest.TestClusterMembershipEventListener();
        instances.get(0).getMembershipService().addListener(eventListener1);
        AtomixTest.TestClusterMembershipEventListener eventListener2 = new AtomixTest.TestClusterMembershipEventListener();
        instances.get(1).getMembershipService().addListener(eventListener2);
        AtomixTest.TestClusterMembershipEventListener eventListener3 = new AtomixTest.TestClusterMembershipEventListener();
        instances.get(2).getMembershipService().addListener(eventListener3);
        instances.get(0).stop().get(30, TimeUnit.SECONDS);
        Assert.assertEquals(REACHABILITY_CHANGED, eventListener2.event().type());
        Assert.assertEquals(MEMBER_REMOVED, eventListener2.event().type());
        Assert.assertEquals(2, instances.get(1).getMembershipService().getMembers().size());
        Assert.assertEquals(REACHABILITY_CHANGED, eventListener3.event().type());
        Assert.assertEquals(MEMBER_REMOVED, eventListener3.event().type());
        Assert.assertEquals(2, instances.get(2).getMembershipService().getMembers().size());
        instances.get(1).stop().get(30, TimeUnit.SECONDS);
        Assert.assertEquals(REACHABILITY_CHANGED, eventListener3.event().type());
        Assert.assertEquals(MEMBER_REMOVED, eventListener3.event().type());
        Assert.assertEquals(1, instances.get(2).getMembershipService().getMembers().size());
        instances.get(2).stop().get(30, TimeUnit.SECONDS);
    }

    /**
     * Tests a client joining and leaving the cluster.
     */
    @Test
    public void testClientJoinLeaveDataGrid() throws Exception {
        testClientJoinLeave(Profile.dataGrid(), Profile.dataGrid(), Profile.dataGrid());
    }

    /**
     * Tests a client joining and leaving the cluster.
     */
    @Test
    public void testClientJoinLeaveConsensus() throws Exception {
        testClientJoinLeave(ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "join-leave"), "1")).build(), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "join-leave"), "2")).build(), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "join-leave"), "3")).build());
    }

    /**
     * Tests a client properties.
     */
    @Test
    public void testClientProperties() throws Exception {
        List<CompletableFuture<Atomix>> futures = new ArrayList<>();
        futures.add(startAtomix(1, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "client-properties"), "1")).build()));
        futures.add(startAtomix(2, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "client-properties"), "2")).build()));
        futures.add(startAtomix(3, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "client-properties"), "3")).build()));
        Futures.allOf(futures).get(30, TimeUnit.SECONDS);
        AtomixTest.TestClusterMembershipEventListener dataListener = new AtomixTest.TestClusterMembershipEventListener();
        instances.get(0).getMembershipService().addListener(dataListener);
        Properties properties = new Properties();
        properties.setProperty("a-key", "a-value");
        Atomix client1 = startAtomix(4, Arrays.asList(1, 2, 3), properties, Profile.client()).get(30, TimeUnit.SECONDS);
        Assert.assertEquals(1, client1.getPartitionService().getPartitionGroups().size());
        // client1 added to data node
        ClusterMembershipEvent event1 = dataListener.event();
        Assert.assertEquals(MEMBER_ADDED, event1.type());
        Member member = event1.subject();
        Assert.assertNotNull(member.properties());
        Assert.assertEquals(1, member.properties().size());
        Assert.assertEquals("a-value", member.properties().get("a-key"));
    }

    @Test
    public void testPrimitiveGetters() throws Exception {
        List<CompletableFuture<Atomix>> futures = new ArrayList<>();
        futures.add(startAtomix(1, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-getters"), "1")).build()));
        futures.add(startAtomix(2, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-getters"), "2")).build()));
        futures.add(startAtomix(3, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-getters"), "3")).build()));
        Futures.allOf(futures).get(30, TimeUnit.SECONDS);
        Atomix atomix = startAtomix(4, Arrays.asList(1, 2, 3), Profile.client()).get(30, TimeUnit.SECONDS);
        Assert.assertEquals("a", atomix.getAtomicCounter("a").name());
        Assert.assertEquals(AtomicCounterType.instance(), atomix.getAtomicCounter("a").type());
        Assert.assertSame(atomix.getAtomicCounter("a"), atomix.getAtomicCounter("a"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicCounterType.instance()).size());
        Assert.assertEquals("b", atomix.getAtomicMap("b").name());
        Assert.assertEquals(AtomicMapType.instance(), atomix.getAtomicMap("b").type());
        Assert.assertSame(atomix.getAtomicMap("b"), atomix.getAtomicMap("b"));
        Assert.assertEquals(2, atomix.getPrimitives(AtomicMapType.instance()).size());
        Assert.assertEquals("c", atomix.getAtomicCounterMap("c").name());
        Assert.assertEquals(AtomicCounterMapType.instance(), atomix.getAtomicCounterMap("c").type());
        Assert.assertSame(atomix.getAtomicCounterMap("c"), atomix.getAtomicCounterMap("c"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicCounterMapType.instance()).size());
        Assert.assertEquals("d", atomix.getAtomicDocumentTree("d").name());
        Assert.assertEquals(AtomicDocumentTreeType.instance(), atomix.getAtomicDocumentTree("d").type());
        Assert.assertSame(atomix.getAtomicDocumentTree("d"), atomix.getAtomicDocumentTree("d"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicDocumentTreeType.instance()).size());
        Assert.assertEquals("e", atomix.getAtomicIdGenerator("e").name());
        Assert.assertEquals(AtomicIdGeneratorType.instance(), atomix.getAtomicIdGenerator("e").type());
        Assert.assertSame(atomix.getAtomicIdGenerator("e"), atomix.getAtomicIdGenerator("e"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicIdGeneratorType.instance()).size());
        Assert.assertEquals("f", atomix.getAtomicLock("f").name());
        Assert.assertEquals(AtomicLockType.instance(), atomix.getAtomicLock("f").type());
        Assert.assertSame(atomix.getAtomicLock("f"), atomix.getAtomicLock("f"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicLockType.instance()).size());
        Assert.assertEquals("g", atomix.getAtomicMultimap("g").name());
        Assert.assertEquals(AtomicMultimapType.instance(), atomix.getAtomicMultimap("g").type());
        Assert.assertSame(atomix.getAtomicMultimap("g"), atomix.getAtomicMultimap("g"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicMultimapType.instance()).size());
        Assert.assertEquals("h", atomix.getAtomicNavigableMap("h").name());
        Assert.assertEquals(AtomicNavigableMapType.instance(), atomix.getAtomicNavigableMap("h").type());
        Assert.assertSame(atomix.getAtomicNavigableMap("h"), atomix.getAtomicNavigableMap("h"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicNavigableMapType.instance()).size());
        Assert.assertEquals("i", atomix.getAtomicSemaphore("i").name());
        Assert.assertEquals(AtomicSemaphoreType.instance(), atomix.getAtomicSemaphore("i").type());
        Assert.assertSame(atomix.getAtomicSemaphore("i"), atomix.getAtomicSemaphore("i"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicSemaphoreType.instance()).size());
        Assert.assertEquals("j", atomix.getAtomicSortedMap("j").name());
        Assert.assertEquals(AtomicSortedMapType.instance(), atomix.getAtomicSortedMap("j").type());
        Assert.assertSame(atomix.getAtomicSortedMap("j"), atomix.getAtomicSortedMap("j"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicSortedMapType.instance()).size());
        Assert.assertEquals("k", atomix.getAtomicValue("k").name());
        Assert.assertEquals(AtomicValueType.instance(), atomix.getAtomicValue("k").type());
        Assert.assertSame(atomix.getAtomicValue("k"), atomix.getAtomicValue("k"));
        Assert.assertEquals(1, atomix.getPrimitives(AtomicValueType.instance()).size());
        Assert.assertEquals("l", atomix.getCounter("l").name());
        Assert.assertEquals(DistributedCounterType.instance(), atomix.getCounter("l").type());
        Assert.assertSame(atomix.getCounter("l"), atomix.getCounter("l"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedCounterType.instance()).size());
        Assert.assertEquals("m", atomix.getCyclicBarrier("m").name());
        Assert.assertEquals(DistributedCyclicBarrierType.instance(), atomix.getCyclicBarrier("m").type());
        Assert.assertSame(atomix.getCyclicBarrier("m"), atomix.getCyclicBarrier("m"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedCyclicBarrierType.instance()).size());
        Assert.assertEquals("n", atomix.getLeaderElection("n").name());
        Assert.assertEquals(LeaderElectionType.instance(), atomix.getLeaderElection("n").type());
        Assert.assertSame(atomix.getLeaderElection("n"), atomix.getLeaderElection("n"));
        Assert.assertEquals(1, atomix.getPrimitives(LeaderElectionType.instance()).size());
        Assert.assertEquals("o", atomix.getLeaderElector("o").name());
        Assert.assertEquals(LeaderElectorType.instance(), atomix.getLeaderElector("o").type());
        Assert.assertSame(atomix.getLeaderElector("o"), atomix.getLeaderElector("o"));
        Assert.assertEquals(1, atomix.getPrimitives(LeaderElectorType.instance()).size());
        Assert.assertEquals("p", atomix.getList("p").name());
        Assert.assertEquals(DistributedListType.instance(), atomix.getList("p").type());
        Assert.assertSame(atomix.getList("p"), atomix.getList("p"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedListType.instance()).size());
        Assert.assertEquals("q", atomix.getLock("q").name());
        Assert.assertEquals(DistributedLockType.instance(), atomix.getLock("q").type());
        Assert.assertSame(atomix.getLock("q"), atomix.getLock("q"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedLockType.instance()).size());
        Assert.assertEquals("r", atomix.getMap("r").name());
        Assert.assertEquals(DistributedMapType.instance(), atomix.getMap("r").type());
        Assert.assertSame(atomix.getMap("r"), atomix.getMap("r"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedMapType.instance()).size());
        Assert.assertEquals("s", atomix.getMultimap("s").name());
        Assert.assertEquals(DistributedMultimapType.instance(), atomix.getMultimap("s").type());
        Assert.assertSame(atomix.getMultimap("s"), atomix.getMultimap("s"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedMultimapType.instance()).size());
        Assert.assertEquals("t", atomix.getMultiset("t").name());
        Assert.assertEquals(DistributedMultisetType.instance(), atomix.getMultiset("t").type());
        Assert.assertSame(atomix.getMultiset("t"), atomix.getMultiset("t"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedMultisetType.instance()).size());
        Assert.assertEquals("u", atomix.getNavigableMap("u").name());
        Assert.assertEquals(DistributedNavigableMapType.instance(), atomix.getNavigableMap("u").type());
        Assert.assertSame(atomix.getNavigableMap("u"), atomix.getNavigableMap("u"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedNavigableMapType.instance()).size());
        Assert.assertEquals("v", atomix.getNavigableSet("v").name());
        Assert.assertEquals(DistributedNavigableSetType.instance(), atomix.getNavigableSet("v").type());
        Assert.assertSame(atomix.getNavigableSet("v"), atomix.getNavigableSet("v"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedNavigableSetType.instance()).size());
        Assert.assertEquals("w", atomix.getQueue("w").name());
        Assert.assertEquals(DistributedQueueType.instance(), atomix.getQueue("w").type());
        Assert.assertSame(atomix.getQueue("w"), atomix.getQueue("w"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedQueueType.instance()).size());
        Assert.assertEquals("x", atomix.getSemaphore("x").name());
        Assert.assertEquals(DistributedSemaphoreType.instance(), atomix.getSemaphore("x").type());
        Assert.assertSame(atomix.getSemaphore("x"), atomix.getSemaphore("x"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedSemaphoreType.instance()).size());
        Assert.assertEquals("y", atomix.getSet("y").name());
        Assert.assertEquals(DistributedSetType.instance(), atomix.getSet("y").type());
        Assert.assertSame(atomix.getSet("y"), atomix.getSet("y"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedSetType.instance()).size());
        Assert.assertEquals("z", atomix.getSortedMap("z").name());
        Assert.assertEquals(DistributedSortedMapType.instance(), atomix.getSortedMap("z").type());
        Assert.assertSame(atomix.getSortedMap("z"), atomix.getSortedMap("z"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedSortedMapType.instance()).size());
        Assert.assertEquals("aa", atomix.getSortedSet("aa").name());
        Assert.assertEquals(DistributedSortedSetType.instance(), atomix.getSortedSet("aa").type());
        Assert.assertSame(atomix.getSortedSet("aa"), atomix.getSortedSet("aa"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedSortedSetType.instance()).size());
        Assert.assertEquals("bb", atomix.getValue("bb").name());
        Assert.assertEquals(DistributedValueType.instance(), atomix.getValue("bb").type());
        Assert.assertSame(atomix.getValue("bb"), atomix.getValue("bb"));
        Assert.assertEquals(1, atomix.getPrimitives(DistributedValueType.instance()).size());
        Assert.assertEquals("cc", atomix.getWorkQueue("cc").name());
        Assert.assertEquals(WorkQueueType.instance(), atomix.getWorkQueue("cc").type());
        Assert.assertSame(atomix.getWorkQueue("cc"), atomix.getWorkQueue("cc"));
        Assert.assertEquals(1, atomix.getPrimitives(WorkQueueType.instance()).size());
        Assert.assertEquals(30, atomix.getPrimitives().size());
    }

    @Test
    public void testPrimitiveConfigurations() throws Exception {
        IntStream.range(1, 4).forEach(( i) -> instances.add(Atomix.builder(getClass().getResource("/primitives.conf").getFile()).withMemberId(String.valueOf(i)).withHost("localhost").withPort((5000 + i)).withProfiles(ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-getters"), String.valueOf(i))).build()).build()));
        Futures.allOf(instances.stream().map(Atomix::start)).get(30, TimeUnit.SECONDS);
        Atomix atomix = Atomix.builder(getClass().getResource("/primitives.conf").getFile()).withHost("localhost").withPort(5000).build();
        instances.add(atomix);
        // try {
        // atomix.getAtomicCounter("foo");
        // fail();
        // } catch (IllegalStateException e) {
        // }
        atomix.start().get(30, TimeUnit.SECONDS);
        try {
            atomix.mapBuilder("foo").withProtocol(MultiRaftProtocol.builder("wrong").build()).get();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof ConfigurationException));
        }
        try {
            atomix.mapBuilder("bar").withProtocol(MultiRaftProtocol.builder("wrong").build()).build();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof ConfigurationException));
        }
        Assert.assertEquals(AtomicCounterType.instance(), atomix.getPrimitive("atomic-counter", AtomicCounterType.instance()).type());
        Assert.assertEquals("atomic-counter", atomix.getAtomicCounter("atomic-counter").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicMapType.instance(), atomix.getPrimitive("atomic-map", AtomicMapType.instance()).type());
        Assert.assertEquals("atomic-map", atomix.getAtomicMap("atomic-map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicCounterMapType.instance(), atomix.getPrimitive("atomic-counter-map", AtomicCounterMapType.instance()).type());
        Assert.assertEquals("atomic-counter-map", atomix.getAtomicCounterMap("atomic-counter-map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicDocumentTreeType.instance(), atomix.getPrimitive("atomic-document-tree", AtomicDocumentTreeType.instance()).type());
        Assert.assertEquals("atomic-document-tree", atomix.getAtomicDocumentTree("atomic-document-tree").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicIdGeneratorType.instance(), atomix.getPrimitive("atomic-id-generator", AtomicIdGeneratorType.instance()).type());
        Assert.assertEquals("atomic-id-generator", atomix.getAtomicIdGenerator("atomic-id-generator").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicLockType.instance(), atomix.getPrimitive("atomic-lock", AtomicLockType.instance()).type());
        Assert.assertEquals("atomic-lock", atomix.getAtomicLock("atomic-lock").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicMultimapType.instance(), atomix.getPrimitive("atomic-multimap", AtomicMultimapType.instance()).type());
        Assert.assertEquals("atomic-multimap", atomix.getAtomicMultimap("atomic-multimap").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicNavigableMapType.instance(), atomix.getPrimitive("atomic-navigable-map", AtomicNavigableMapType.instance()).type());
        Assert.assertEquals("atomic-navigable-map", atomix.getAtomicNavigableMap("atomic-navigable-map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicSemaphoreType.instance(), atomix.getPrimitive("atomic-semaphore", AtomicSemaphoreType.instance()).type());
        Assert.assertEquals("atomic-semaphore", atomix.getAtomicSemaphore("atomic-semaphore").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicSortedMapType.instance(), atomix.getPrimitive("atomic-sorted-map", AtomicSortedMapType.instance()).type());
        Assert.assertEquals("atomic-sorted-map", atomix.getAtomicSortedMap("atomic-sorted-map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(AtomicValueType.instance(), atomix.getPrimitive("atomic-value", AtomicValueType.instance()).type());
        Assert.assertEquals("atomic-value", atomix.getAtomicValue("atomic-value").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedCounterType.instance(), atomix.getPrimitive("counter", DistributedCounterType.instance()).type());
        Assert.assertEquals("counter", atomix.getCounter("counter").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedCyclicBarrierType.instance(), atomix.getPrimitive("cyclic-barrier", DistributedCyclicBarrierType.instance()).type());
        Assert.assertEquals("cyclic-barrier", atomix.getCyclicBarrier("cyclic-barrier").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(LeaderElectionType.instance(), atomix.getPrimitive("leader-election", LeaderElectionType.instance()).type());
        Assert.assertEquals("leader-election", atomix.getLeaderElection("leader-election").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(LeaderElectorType.instance(), atomix.getPrimitive("leader-elector", LeaderElectorType.instance()).type());
        Assert.assertEquals("leader-elector", atomix.getLeaderElector("leader-elector").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedListType.instance(), atomix.getPrimitive("list", DistributedListType.instance()).type());
        Assert.assertEquals("list", atomix.getList("list").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedLockType.instance(), atomix.getPrimitive("lock", DistributedLockType.instance()).type());
        Assert.assertEquals("lock", atomix.getLock("lock").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedMapType.instance(), atomix.getPrimitive("map", DistributedMapType.instance()).type());
        Assert.assertEquals("map", atomix.getMap("map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedMultimapType.instance(), atomix.getPrimitive("multimap", DistributedMultimapType.instance()).type());
        Assert.assertEquals("multimap", atomix.getMultimap("multimap").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedMultisetType.instance(), atomix.getPrimitive("multiset", DistributedMultisetType.instance()).type());
        Assert.assertEquals("multiset", atomix.getMultiset("multiset").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedNavigableMapType.instance(), atomix.getPrimitive("navigable-map", DistributedNavigableMapType.instance()).type());
        Assert.assertEquals("navigable-map", atomix.getNavigableMap("navigable-map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedNavigableSetType.instance(), atomix.getPrimitive("navigable-set", DistributedNavigableSetType.instance()).type());
        Assert.assertEquals("navigable-set", atomix.getNavigableSet("navigable-set").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedQueueType.instance(), atomix.getPrimitive("queue", DistributedQueueType.instance()).type());
        Assert.assertEquals("queue", atomix.getQueue("queue").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedSemaphoreType.instance(), atomix.getPrimitive("semaphore", DistributedSemaphoreType.instance()).type());
        Assert.assertEquals("semaphore", atomix.getSemaphore("semaphore").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedSetType.instance(), atomix.getPrimitive("set", DistributedSetType.instance()).type());
        Assert.assertEquals("set", atomix.getSet("set").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedSortedMapType.instance(), atomix.getPrimitive("sorted-map", DistributedSortedMapType.instance()).type());
        Assert.assertEquals("sorted-map", atomix.getSortedMap("sorted-map").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedSortedSetType.instance(), atomix.getPrimitive("sorted-set", DistributedSortedSetType.instance()).type());
        Assert.assertEquals("sorted-set", atomix.getSortedSet("sorted-set").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(DistributedValueType.instance(), atomix.getPrimitive("value", DistributedValueType.instance()).type());
        Assert.assertEquals("value", atomix.getValue("value").name());
        Assert.assertEquals("two", group());
        Assert.assertEquals(WorkQueueType.instance(), atomix.getPrimitive("work-queue", WorkQueueType.instance()).type());
        Assert.assertEquals("work-queue", atomix.getWorkQueue("work-queue").name());
        Assert.assertEquals("two", group());
    }

    @Test
    public void testPrimitiveBuilders() throws Exception {
        List<CompletableFuture<Atomix>> futures = new ArrayList<>();
        futures.add(startAtomix(1, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-builders"), "1")).build()));
        futures.add(startAtomix(2, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-builders"), "2")).build()));
        futures.add(startAtomix(3, Arrays.asList(1, 2, 3), ConsensusProfile.builder().withMembers("1", "2", "3").withDataPath(new File(new File(AbstractAtomixTest.DATA_DIR, "primitive-builders"), "3")).build()));
        Futures.allOf(futures).get(30, TimeUnit.SECONDS);
        Atomix atomix = startAtomix(4, Arrays.asList(1, 2, 3), Profile.client()).get(30, TimeUnit.SECONDS);
        Assert.assertEquals("a", atomix.atomicCounterBuilder("a").build().name());
        Assert.assertEquals(AtomicCounterType.instance(), atomix.atomicCounterBuilder("a").build().type());
        Assert.assertEquals("b", atomix.atomicMapBuilder("b").build().name());
        Assert.assertEquals(AtomicMapType.instance(), atomix.atomicMapBuilder("b").build().type());
        Assert.assertEquals("c", atomix.atomicCounterMapBuilder("c").build().name());
        Assert.assertEquals(AtomicCounterMapType.instance(), atomix.atomicCounterMapBuilder("c").build().type());
        Assert.assertEquals("d", atomix.atomicDocumentTreeBuilder("d").build().name());
        Assert.assertEquals(AtomicDocumentTreeType.instance(), atomix.atomicDocumentTreeBuilder("d").build().type());
        Assert.assertEquals("e", atomix.atomicIdGeneratorBuilder("e").build().name());
        Assert.assertEquals(AtomicIdGeneratorType.instance(), atomix.atomicIdGeneratorBuilder("e").build().type());
        Assert.assertEquals("f", atomix.atomicLockBuilder("f").build().name());
        Assert.assertEquals(AtomicLockType.instance(), atomix.atomicLockBuilder("f").build().type());
        Assert.assertEquals("g", atomix.atomicMultimapBuilder("g").build().name());
        Assert.assertEquals(AtomicMultimapType.instance(), atomix.atomicMultimapBuilder("g").build().type());
        Assert.assertEquals("h", atomix.atomicNavigableMapBuilder("h").build().name());
        Assert.assertEquals(AtomicNavigableMapType.instance(), atomix.atomicNavigableMapBuilder("h").build().type());
        Assert.assertEquals("i", atomix.atomicSemaphoreBuilder("i").build().name());
        Assert.assertEquals(AtomicSemaphoreType.instance(), atomix.atomicSemaphoreBuilder("i").build().type());
        Assert.assertEquals("j", atomix.atomicSortedMapBuilder("j").build().name());
        Assert.assertEquals(AtomicSortedMapType.instance(), atomix.atomicSortedMapBuilder("j").build().type());
        Assert.assertEquals("k", atomix.atomicValueBuilder("k").build().name());
        Assert.assertEquals(AtomicValueType.instance(), atomix.atomicValueBuilder("k").build().type());
        Assert.assertEquals("l", atomix.counterBuilder("l").build().name());
        Assert.assertEquals(DistributedCounterType.instance(), atomix.counterBuilder("l").build().type());
        Assert.assertEquals("m", atomix.cyclicBarrierBuilder("m").build().name());
        Assert.assertEquals(DistributedCyclicBarrierType.instance(), atomix.cyclicBarrierBuilder("m").build().type());
        Assert.assertEquals("n", atomix.leaderElectionBuilder("n").build().name());
        Assert.assertEquals(LeaderElectionType.instance(), atomix.leaderElectionBuilder("n").build().type());
        Assert.assertEquals("o", atomix.leaderElectorBuilder("o").build().name());
        Assert.assertEquals(LeaderElectorType.instance(), atomix.leaderElectorBuilder("o").build().type());
        Assert.assertEquals("p", atomix.listBuilder("p").build().name());
        Assert.assertEquals(DistributedListType.instance(), atomix.listBuilder("p").build().type());
        Assert.assertEquals("q", atomix.lockBuilder("q").build().name());
        Assert.assertEquals(DistributedLockType.instance(), atomix.lockBuilder("q").build().type());
        Assert.assertEquals("r", atomix.mapBuilder("r").build().name());
        Assert.assertEquals(DistributedMapType.instance(), atomix.mapBuilder("r").build().type());
        Assert.assertEquals("s", atomix.multimapBuilder("s").build().name());
        Assert.assertEquals(DistributedMultimapType.instance(), atomix.multimapBuilder("s").build().type());
        Assert.assertEquals("t", atomix.multisetBuilder("t").build().name());
        Assert.assertEquals(DistributedMultisetType.instance(), atomix.multisetBuilder("t").build().type());
        Assert.assertEquals("u", atomix.navigableMapBuilder("u").build().name());
        Assert.assertEquals(DistributedNavigableMapType.instance(), atomix.navigableMapBuilder("u").build().type());
        Assert.assertEquals("v", atomix.navigableSetBuilder("v").build().name());
        Assert.assertEquals(DistributedNavigableSetType.instance(), atomix.navigableSetBuilder("v").build().type());
        Assert.assertEquals("w", atomix.queueBuilder("w").build().name());
        Assert.assertEquals(DistributedQueueType.instance(), atomix.queueBuilder("w").build().type());
        Assert.assertEquals("x", atomix.semaphoreBuilder("x").build().name());
        Assert.assertEquals(DistributedSemaphoreType.instance(), atomix.semaphoreBuilder("x").build().type());
        Assert.assertEquals("y", atomix.setBuilder("y").build().name());
        Assert.assertEquals(DistributedSetType.instance(), atomix.setBuilder("y").build().type());
        Assert.assertEquals("z", atomix.sortedMapBuilder("z").build().name());
        Assert.assertEquals(DistributedSortedMapType.instance(), atomix.sortedMapBuilder("z").build().type());
        Assert.assertEquals("aa", atomix.sortedSetBuilder("aa").build().name());
        Assert.assertEquals(DistributedSortedSetType.instance(), atomix.sortedSetBuilder("aa").build().type());
        Assert.assertEquals("bb", atomix.valueBuilder("bb").build().name());
        Assert.assertEquals(DistributedValueType.instance(), atomix.valueBuilder("bb").build().type());
        Assert.assertEquals("cc", atomix.workQueueBuilder("cc").build().name());
        Assert.assertEquals(WorkQueueType.instance(), atomix.workQueueBuilder("cc").build().type());
    }

    private static class TestClusterMembershipEventListener implements ClusterMembershipEventListener {
        private final BlockingQueue<ClusterMembershipEvent> queue = new LinkedBlockingQueue<>();

        @Override
        public void event(ClusterMembershipEvent event) {
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public boolean eventReceived() {
            return !(queue.isEmpty());
        }

        public ClusterMembershipEvent event() throws InterruptedException, TimeoutException {
            ClusterMembershipEvent event = queue.poll(15, TimeUnit.SECONDS);
            if (event == null) {
                throw new TimeoutException();
            }
            return event;
        }
    }
}

