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


import ClusterConnectionProvider.Intent.WRITE;
import RedisClusterNode.NodeFlag.MASTER;
import RedisClusterNode.NodeFlag.SLAVE;
import io.lettuce.category.SlowTests;
import io.lettuce.core.RedisClient;
import io.lettuce.core.TestSupport;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.test.Wait;
import io.lettuce.test.settings.TestSettings;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("unchecked")
@SlowTests
public class RedisClusterStressScenariosTest extends TestSupport {
    private static final String host = TestSettings.hostAddr();

    private static RedisClient client;

    private static RedisClusterClient clusterClient;

    private Logger log = LogManager.getLogger(getClass());

    private StatefulRedisConnection<String, String> redis5;

    private StatefulRedisConnection<String, String> redis6;

    private RedisCommands<String, String> redissync5;

    private RedisCommands<String, String> redissync6;

    protected String key = "key";

    protected String value = "value";

    @Rule
    public ClusterRule clusterRule = new ClusterRule(RedisClusterStressScenariosTest.clusterClient, ClusterTestSettings.port5, ClusterTestSettings.port6);

    @Test
    public void testClusterFailover() {
        log.info("Cluster node 5 is master");
        log.info(("Cluster nodes seen from node 5:\n" + (redissync5.clusterNodes())));
        log.info(("Cluster nodes seen from node 6:\n" + (redissync6.clusterNodes())));
        Wait.untilTrue(() -> ClusterTestUtil.getOwnPartition(redissync5).is(MASTER)).waitOrTimeout();
        Wait.untilTrue(() -> ClusterTestUtil.getOwnPartition(redissync6).is(SLAVE)).waitOrTimeout();
        String failover = redissync6.clusterFailover(true);
        assertThat(failover).isEqualTo("OK");
        Wait.untilTrue(() -> ClusterTestUtil.getOwnPartition(redissync6).is(MASTER)).waitOrTimeout();
        Wait.untilTrue(() -> ClusterTestUtil.getOwnPartition(redissync5).is(SLAVE)).waitOrTimeout();
        log.info(("Cluster nodes seen from node 5 after clusterFailover:\n" + (redissync5.clusterNodes())));
        log.info(("Cluster nodes seen from node 6 after clusterFailover:\n" + (redissync6.clusterNodes())));
        RedisClusterNode redis5Node = ClusterTestUtil.getOwnPartition(redissync5);
        RedisClusterNode redis6Node = ClusterTestUtil.getOwnPartition(redissync6);
        assertThat(redis5Node.getFlags()).contains(SLAVE);
        assertThat(redis6Node.getFlags()).contains(MASTER);
    }

    @Test
    public void testClusterConnectionStability() {
        RedisAdvancedClusterAsyncCommandsImpl<String, String> connection = ((RedisAdvancedClusterAsyncCommandsImpl<String, String>) (RedisClusterStressScenariosTest.clusterClient.connect().async()));
        RedisChannelHandler<String, String> statefulConnection = connection.getStatefulConnection();
        connection.set("a", "b");
        ClusterDistributionChannelWriter writer = ((ClusterDistributionChannelWriter) (statefulConnection.getChannelWriter()));
        StatefulRedisConnectionImpl<Object, Object> statefulSlotConnection = ((StatefulRedisConnectionImpl) (writer.getClusterConnectionProvider().getConnection(WRITE, 3300)));
        final RedisAsyncCommands<Object, Object> slotConnection = statefulSlotConnection.async();
        slotConnection.set("a", "b");
        slotConnection.getStatefulConnection().close();
        Wait.untilTrue(() -> !(slotConnection.isOpen())).waitOrTimeout();
        assertThat(statefulSlotConnection.isClosed()).isTrue();
        assertThat(statefulSlotConnection.isOpen()).isFalse();
        assertThat(connection.isOpen()).isTrue();
        assertThat(statefulConnection.isOpen()).isTrue();
        assertThat(statefulConnection.isClosed()).isFalse();
        try {
            connection.set("a", "b");
        } catch (RedisException e) {
            assertThat(e).hasMessageContaining("Connection is closed");
        }
        connection.getStatefulConnection().close();
    }
}

