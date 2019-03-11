/**
 * Copyright 2011-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.cluster;


import ClientOptions.DisconnectedBehavior.REJECT_COMMANDS;
import ReadFrom.NEAREST;
import ReadFrom.REPLICA;
import RedisClusterNode.NodeFlag.HANDSHAKE;
import RedisClusterNode.NodeFlag.MYSELF;
import io.lettuce.category.SlowTests;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisClusterClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.TestSupport;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.cluster.models.partitions.ClusterPartitionParser;
import io.lettuce.core.cluster.models.partitions.Partitions;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.test.Futures;
import io.lettuce.test.Wait;
import io.lettuce.test.resource.DefaultRedisClient;
import io.lettuce.test.settings.TestSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test for mutable cluster setup scenarios.
 *
 * @author Mark Paluch
 * @since 3.0
 */
@SuppressWarnings({ "unchecked" })
@SlowTests
public class RedisClusterSetupTest extends TestSupport {
    private static final String host = TestSettings.hostAddr();

    private static final ClusterTopologyRefreshOptions PERIODIC_REFRESH_ENABLED = ClusterTopologyRefreshOptions.builder().enablePeriodicRefresh(1, TimeUnit.SECONDS).dynamicRefreshSources(false).build();

    private static RedisClusterClient clusterClient;

    private static RedisClient client = DefaultRedisClient.get();

    private RedisCommands<String, String> redis1;

    private RedisCommands<String, String> redis2;

    @Rule
    public ClusterRule clusterRule = new ClusterRule(RedisClusterSetupTest.clusterClient, ClusterTestSettings.port5, ClusterTestSettings.port6);

    @Test
    public void clusterMeet() {
        clusterRule.clusterReset();
        Partitions partitionsBeforeMeet = ClusterPartitionParser.parse(redis1.clusterNodes());
        assertThat(partitionsBeforeMeet.getPartitions()).hasSize(1);
        String result = redis1.clusterMeet(RedisClusterSetupTest.host, ClusterTestSettings.port6);
        assertThat(result).isEqualTo("OK");
        Wait.untilEquals(2, () -> ClusterPartitionParser.parse(redis1.clusterNodes()).size()).waitOrTimeout();
        Partitions partitionsAfterMeet = ClusterPartitionParser.parse(redis1.clusterNodes());
        assertThat(partitionsAfterMeet.getPartitions()).hasSize(2);
    }

    @Test
    public void clusterForget() {
        clusterRule.clusterReset();
        String result = redis1.clusterMeet(RedisClusterSetupTest.host, ClusterTestSettings.port6);
        assertThat(result).isEqualTo("OK");
        Wait.untilTrue(() -> redis1.clusterNodes().contains(redis2.clusterMyId())).waitOrTimeout();
        Wait.untilTrue(() -> redis2.clusterNodes().contains(redis1.clusterMyId())).waitOrTimeout();
        Wait.untilTrue(() -> {
            Partitions partitions = ClusterPartitionParser.parse(redis1.clusterNodes());
            if ((partitions.size()) != 2) {
                return false;
            }
            for (RedisClusterNode redisClusterNode : partitions) {
                if (redisClusterNode.is(HANDSHAKE)) {
                    return false;
                }
            }
            return true;
        }).waitOrTimeout();
        redis1.clusterForget(redis2.clusterMyId());
        Wait.untilEquals(1, () -> ClusterPartitionParser.parse(redis1.clusterNodes()).size()).waitOrTimeout();
        Partitions partitionsAfterForget = ClusterPartitionParser.parse(redis1.clusterNodes());
        assertThat(partitionsAfterForget.getPartitions()).hasSize(1);
    }

    @Test
    public void clusterDelSlots() {
        ClusterSetup.setup2Masters(clusterRule);
        redis1.clusterDelSlots(1, 2, 5, 6);
        Wait.untilEquals(11996, () -> ClusterTestUtil.getOwnPartition(redis1).getSlots().size()).waitOrTimeout();
    }

    @Test
    public void clusterSetSlots() {
        ClusterSetup.setup2Masters(clusterRule);
        redis1.clusterSetSlotNode(6, ClusterTestUtil.getNodeId(redis2));
        waitForSlots(redis1, 11999);
        waitForSlots(redis2, 4384);
        Partitions partitions = ClusterPartitionParser.parse(redis1.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (redisClusterNode.getFlags().contains(MYSELF)) {
                assertThat(redisClusterNode.getSlots()).contains(1, 2, 3, 4, 5).doesNotContain(6);
            }
        }
    }

    @Test
    public void clusterSlotMigrationImport() {
        ClusterSetup.setup2Masters(clusterRule);
        String nodeId2 = ClusterTestUtil.getNodeId(redis2);
        assertThat(redis1.clusterSetSlotMigrating(6, nodeId2)).isEqualTo("OK");
        assertThat(redis1.clusterSetSlotImporting(15000, nodeId2)).isEqualTo("OK");
        assertThat(redis1.clusterSetSlotStable(6)).isEqualTo("OK");
    }

    @Test
    public void clusterTopologyRefresh() {
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(RedisClusterSetupTest.PERIODIC_REFRESH_ENABLED).build());
        RedisClusterSetupTest.clusterClient.reloadPartitions();
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        assertThat(RedisClusterSetupTest.clusterClient.getPartitions()).hasSize(1);
        ClusterSetup.setup2Masters(clusterRule);
        assertThat(RedisClusterSetupTest.clusterClient.getPartitions()).hasSize(2);
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void changeTopologyWhileOperations() throws Exception {
        ClusterSetup.setup2Masters(clusterRule);
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder().enableAllAdaptiveRefreshTriggers().build();
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(clusterTopologyRefreshOptions).build());
        StatefulRedisClusterConnection<String, String> connection = RedisClusterSetupTest.clusterClient.connect();
        RedisAdvancedClusterCommands<String, String> sync = connection.sync();
        RedisAdvancedClusterAsyncCommands<String, String> async = connection.async();
        Partitions partitions = connection.getPartitions();
        assertThat(partitions.getPartitionBySlot(0).getSlots().size()).isEqualTo(12000);
        assertThat(partitions.getPartitionBySlot(16380).getSlots().size()).isEqualTo(4384);
        assertRoutedExecution(async);
        sync.del("A");
        sync.del("t");
        sync.del("p");
        shiftAllSlotsToNode1();
        assertRoutedExecution(async);
        Wait.untilTrue(() -> {
            if ((RedisClusterSetupTest.clusterClient.getPartitions().size()) == 2) {
                for (RedisClusterNode redisClusterNode : RedisClusterSetupTest.clusterClient.getPartitions()) {
                    if ((redisClusterNode.getSlots().size()) > 16380) {
                        return true;
                    }
                }
            }
            return false;
        }).waitOrTimeout();
        assertThat(partitions.getPartitionBySlot(0).getSlots().size()).isEqualTo(16384);
        assertThat(sync.get("A")).isEqualTo("value");
        assertThat(sync.get("t")).isEqualTo("value");
        assertThat(sync.get("p")).isEqualTo("value");
        async.getStatefulConnection().close();
    }

    @Test
    public void slotMigrationShouldUseAsking() {
        ClusterSetup.setup2Masters(clusterRule);
        StatefulRedisClusterConnection<String, String> connection = RedisClusterSetupTest.clusterClient.connect();
        RedisAdvancedClusterCommands<String, String> sync = connection.sync();
        RedisAdvancedClusterAsyncCommands<String, String> async = connection.async();
        Partitions partitions = connection.getPartitions();
        assertThat(partitions.getPartitionBySlot(0).getSlots().size()).isEqualTo(12000);
        assertThat(partitions.getPartitionBySlot(16380).getSlots().size()).isEqualTo(4384);
        redis1.clusterSetSlotMigrating(3300, redis2.clusterMyId());
        redis2.clusterSetSlotImporting(3300, redis1.clusterMyId());
        assertThat(sync.get("b")).isNull();
        async.getStatefulConnection().close();
    }

    @Test
    public void disconnectedConnectionRejectTest() throws Exception {
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(RedisClusterSetupTest.PERIODIC_REFRESH_ENABLED).disconnectedBehavior(REJECT_COMMANDS).build());
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().disconnectedBehavior(REJECT_COMMANDS).build());
        ClusterSetup.setup2Masters(clusterRule);
        assertRoutedExecution(clusterConnection);
        RedisClusterNode partition1 = ClusterTestUtil.getOwnPartition(redis1);
        RedisClusterAsyncCommands<String, String> node1Connection = clusterConnection.getConnection(partition1.getUri().getHost(), partition1.getUri().getPort());
        shiftAllSlotsToNode1();
        suspendConnection(node1Connection);
        RedisFuture<String> set = clusterConnection.set("t", "value");// 15891

        set.await(5, TimeUnit.SECONDS);
        assertThatThrownBy(() -> Futures.await(set)).hasRootCauseInstanceOf(RedisException.class);
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void atLeastOnceForgetNodeFailover() throws Exception {
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(RedisClusterSetupTest.PERIODIC_REFRESH_ENABLED).build());
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.create());
        ClusterSetup.setup2Masters(clusterRule);
        assertRoutedExecution(clusterConnection);
        RedisClusterNode partition1 = ClusterTestUtil.getOwnPartition(redis1);
        RedisClusterNode partition2 = ClusterTestUtil.getOwnPartition(redis2);
        RedisClusterAsyncCommands<String, String> node1Connection = clusterConnection.getConnection(partition1.getUri().getHost(), partition1.getUri().getPort());
        RedisClusterAsyncCommands<String, String> node2Connection = clusterConnection.getConnection(partition2.getUri().getHost(), partition2.getUri().getPort());
        shiftAllSlotsToNode1();
        suspendConnection(node2Connection);
        List<RedisFuture<String>> futures = new ArrayList<>();
        futures.add(clusterConnection.set("t", "value"));// 15891

        futures.add(clusterConnection.set("p", "value"));// 16023

        clusterConnection.set("A", "value").get(1, TimeUnit.SECONDS);// 6373

        for (RedisFuture<String> future : futures) {
            assertThat(future.isDone()).isFalse();
            assertThat(future.isCancelled()).isFalse();
        }
        redis1.clusterForget(partition2.getNodeId());
        redis2.clusterForget(partition1.getNodeId());
        Partitions partitions = RedisClusterSetupTest.clusterClient.getPartitions();
        partitions.remove(partition2);
        partitions.getPartition(0).setSlots(Arrays.stream(ClusterTestSettings.createSlots(0, 16384)).boxed().collect(Collectors.toList()));
        partitions.updateCache();
        RedisClusterSetupTest.clusterClient.updatePartitionsInConnections();
        Wait.untilTrue(() -> Futures.areAllCompleted(futures)).waitOrTimeout();
        assertRoutedExecution(clusterConnection);
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void expireStaleNodeIdConnections() throws Exception {
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(RedisClusterSetupTest.PERIODIC_REFRESH_ENABLED).build());
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        ClusterSetup.setup2Masters(clusterRule);
        PooledClusterConnectionProvider<String, String> clusterConnectionProvider = getPooledClusterConnectionProvider(clusterConnection);
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(0);
        assertRoutedExecution(clusterConnection);
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(2);
        Partitions partitions = ClusterPartitionParser.parse(redis1.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (!(redisClusterNode.getFlags().contains(MYSELF))) {
                redis1.clusterForget(redisClusterNode.getNodeId());
            }
        }
        partitions = ClusterPartitionParser.parse(redis2.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (!(redisClusterNode.getFlags().contains(MYSELF))) {
                redis2.clusterForget(redisClusterNode.getNodeId());
            }
        }
        Wait.untilEquals(1, () -> RedisClusterSetupTest.clusterClient.getPartitions().size()).waitOrTimeout();
        Wait.untilEquals(1, () -> clusterConnectionProvider.getConnectionCount()).waitOrTimeout();
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void doNotExpireStaleNodeIdConnections() throws Exception {
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(ClusterTopologyRefreshOptions.builder().closeStaleConnections(false).build()).build());
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        ClusterSetup.setup2Masters(clusterRule);
        PooledClusterConnectionProvider<String, String> clusterConnectionProvider = getPooledClusterConnectionProvider(clusterConnection);
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(0);
        assertRoutedExecution(clusterConnection);
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(2);
        Partitions partitions = ClusterPartitionParser.parse(redis1.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (!(redisClusterNode.getFlags().contains(MYSELF))) {
                redis1.clusterForget(redisClusterNode.getNodeId());
            }
        }
        partitions = ClusterPartitionParser.parse(redis2.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (!(redisClusterNode.getFlags().contains(MYSELF))) {
                redis2.clusterForget(redisClusterNode.getNodeId());
            }
        }
        RedisClusterSetupTest.clusterClient.reloadPartitions();
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(2);
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void expireStaleHostAndPortConnections() throws Exception {
        RedisClusterSetupTest.clusterClient.setOptions(ClusterClientOptions.builder().build());
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        ClusterSetup.setup2Masters(clusterRule);
        final PooledClusterConnectionProvider<String, String> clusterConnectionProvider = getPooledClusterConnectionProvider(clusterConnection);
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(0);
        assertRoutedExecution(clusterConnection);
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(2);
        for (RedisClusterNode redisClusterNode : RedisClusterSetupTest.clusterClient.getPartitions()) {
            clusterConnection.getConnection(redisClusterNode.getUri().getHost(), redisClusterNode.getUri().getPort());
            clusterConnection.getConnection(redisClusterNode.getNodeId());
        }
        assertThat(clusterConnectionProvider.getConnectionCount()).isEqualTo(4);
        Partitions partitions = ClusterPartitionParser.parse(redis1.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (!(redisClusterNode.getFlags().contains(MYSELF))) {
                redis1.clusterForget(redisClusterNode.getNodeId());
            }
        }
        partitions = ClusterPartitionParser.parse(redis2.clusterNodes());
        for (RedisClusterNode redisClusterNode : partitions.getPartitions()) {
            if (!(redisClusterNode.getFlags().contains(MYSELF))) {
                redis2.clusterForget(redisClusterNode.getNodeId());
            }
        }
        RedisClusterSetupTest.clusterClient.reloadPartitions();
        Wait.untilEquals(1, () -> RedisClusterSetupTest.clusterClient.getPartitions().size()).waitOrTimeout();
        Wait.untilEquals(2L, () -> clusterConnectionProvider.getConnectionCount()).waitOrTimeout();
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void readFromReplicaTest() {
        ClusterSetup.setup2Masters(clusterRule);
        RedisAdvancedClusterAsyncCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().async();
        clusterConnection.getStatefulConnection().setReadFrom(REPLICA);
        Futures.await(clusterConnection.set(TestSupport.key, TestSupport.value));
        try {
            clusterConnection.get(TestSupport.key);
        } catch (RedisException e) {
            assertThat(e).hasMessageContaining("Cannot determine a partition to read for slot");
        }
        clusterConnection.getStatefulConnection().close();
    }

    @Test
    public void readFromNearestTest() {
        ClusterSetup.setup2Masters(clusterRule);
        RedisAdvancedClusterCommands<String, String> clusterConnection = RedisClusterSetupTest.clusterClient.connect().sync();
        clusterConnection.getStatefulConnection().setReadFrom(NEAREST);
        clusterConnection.set(TestSupport.key, TestSupport.value);
        assertThat(clusterConnection.get(TestSupport.key)).isEqualTo(TestSupport.value);
        clusterConnection.getStatefulConnection().close();
    }
}

