/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.redis;


import Status.DOWN;
import Status.UP;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;


/**
 * Tests for {@link RedisHealthIndicator}.
 *
 * @author Christian Dupuis
 * @author Richard Santana
 * @author Stephane Nicoll
 */
public class RedisHealthIndicatorTests {
    @Test
    public void redisIsUp() {
        Properties info = new Properties();
        info.put("redis_version", "2.8.9");
        RedisConnection redisConnection = Mockito.mock(RedisConnection.class);
        BDDMockito.given(redisConnection.info()).willReturn(info);
        RedisHealthIndicator healthIndicator = createHealthIndicator(redisConnection);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("version")).isEqualTo("2.8.9");
    }

    @Test
    public void redisIsDown() {
        RedisConnection redisConnection = Mockito.mock(RedisConnection.class);
        BDDMockito.given(redisConnection.info()).willThrow(new RedisConnectionFailureException("Connection failed"));
        RedisHealthIndicator healthIndicator = createHealthIndicator(redisConnection);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(((String) (health.getDetails().get("error")))).contains("Connection failed");
    }

    @Test
    public void redisClusterIsUp() {
        Properties clusterProperties = new Properties();
        clusterProperties.setProperty("cluster_size", "4");
        clusterProperties.setProperty("cluster_slots_ok", "4");
        clusterProperties.setProperty("cluster_slots_fail", "0");
        List<RedisClusterNode> redisMasterNodes = Arrays.asList(new RedisClusterNode("127.0.0.1", 7001), new RedisClusterNode("127.0.0.2", 7001));
        RedisClusterConnection redisConnection = Mockito.mock(RedisClusterConnection.class);
        BDDMockito.given(redisConnection.clusterGetNodes()).willReturn(redisMasterNodes);
        BDDMockito.given(redisConnection.clusterGetClusterInfo()).willReturn(new ClusterInfo(clusterProperties));
        RedisConnectionFactory redisConnectionFactory = Mockito.mock(RedisConnectionFactory.class);
        BDDMockito.given(redisConnectionFactory.getConnection()).willReturn(redisConnection);
        RedisHealthIndicator healthIndicator = new RedisHealthIndicator(redisConnectionFactory);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("cluster_size")).isEqualTo(4L);
        assertThat(health.getDetails().get("slots_up")).isEqualTo(4L);
        assertThat(health.getDetails().get("slots_fail")).isEqualTo(0L);
        Mockito.verify(redisConnectionFactory, Mockito.atLeastOnce()).getConnection();
    }
}

