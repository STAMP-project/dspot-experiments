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
package org.springframework.boot.autoconfigure.cassandra;


import com.datastax.driver.core.PoolingOptions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link CassandraAutoConfiguration}
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 */
public class CassandraAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(CassandraAutoConfiguration.class));

    @Test
    public void createClusterWithDefault() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getClusterName()).startsWith("cluster");
        });
    }

    @Test
    public void createClusterWithOverrides() {
        this.contextRunner.withPropertyValues("spring.data.cassandra.cluster-name=testcluster").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getClusterName()).isEqualTo("testcluster");
        });
    }

    @Test
    public void createCustomizeCluster() {
        this.contextRunner.withUserConfiguration(CassandraAutoConfigurationTests.MockCustomizerConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void customizerOverridesAutoConfig() {
        this.contextRunner.withUserConfiguration(CassandraAutoConfigurationTests.SimpleCustomizerConfig.class).withPropertyValues("spring.data.cassandra.cluster-name=testcluster").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getClusterName()).isEqualTo("overridden-name");
        });
    }

    @Test
    public void defaultPoolOptions() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            PoolingOptions poolingOptions = context.getBean(.class).getConfiguration().getPoolingOptions();
            assertThat(poolingOptions.getIdleTimeoutSeconds()).isEqualTo(PoolingOptions.DEFAULT_IDLE_TIMEOUT_SECONDS);
            assertThat(poolingOptions.getPoolTimeoutMillis()).isEqualTo(PoolingOptions.DEFAULT_POOL_TIMEOUT_MILLIS);
            assertThat(poolingOptions.getHeartbeatIntervalSeconds()).isEqualTo(PoolingOptions.DEFAULT_HEARTBEAT_INTERVAL_SECONDS);
            assertThat(poolingOptions.getMaxQueueSize()).isEqualTo(PoolingOptions.DEFAULT_MAX_QUEUE_SIZE);
        });
    }

    @Test
    public void customizePoolOptions() {
        this.contextRunner.withPropertyValues("spring.data.cassandra.pool.idle-timeout=42", "spring.data.cassandra.pool.pool-timeout=52", "spring.data.cassandra.pool.heartbeat-interval=62", "spring.data.cassandra.pool.max-queue-size=72").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            PoolingOptions poolingOptions = context.getBean(.class).getConfiguration().getPoolingOptions();
            assertThat(poolingOptions.getIdleTimeoutSeconds()).isEqualTo(42);
            assertThat(poolingOptions.getPoolTimeoutMillis()).isEqualTo(52);
            assertThat(poolingOptions.getHeartbeatIntervalSeconds()).isEqualTo(62);
            assertThat(poolingOptions.getMaxQueueSize()).isEqualTo(72);
        });
    }

    @Configuration
    static class MockCustomizerConfig {
        @Bean
        public ClusterBuilderCustomizer customizer() {
            return Mockito.mock(ClusterBuilderCustomizer.class);
        }
    }

    @Configuration
    static class SimpleCustomizerConfig {
        @Bean
        public ClusterBuilderCustomizer customizer() {
            return ( clusterBuilder) -> clusterBuilder.withClusterName("overridden-name");
        }
    }
}

