/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.eureka;


import Health.Builder;
import InstanceStatus.DOWN;
import InstanceStatus.UNKNOWN;
import InstanceStatus.UP;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.cloud.client.discovery.health.DiscoveryClientHealthIndicator;
import org.springframework.cloud.client.discovery.health.DiscoveryCompositeHealthIndicator;
import org.springframework.cloud.client.discovery.health.DiscoveryHealthIndicator;
import org.springframework.context.annotation.Bean;


/**
 * Tests the {@link EurekaHealthCheckHandler} with different health indicator registered.
 *
 * @author Jakub Narloch
 */
public class EurekaHealthCheckHandlerTests {
    private EurekaHealthCheckHandler healthCheckHandler;

    @Test
    public void testNoHealthCheckRegistered() throws Exception {
        InstanceStatus status = healthCheckHandler.getStatus(UNKNOWN);
        assertThat(status).isEqualTo(UNKNOWN);
    }

    @Test
    public void testAllUp() throws Exception {
        initialize(EurekaHealthCheckHandlerTests.UpHealthConfiguration.class);
        InstanceStatus status = healthCheckHandler.getStatus(UNKNOWN);
        assertThat(status).isEqualTo(UP);
    }

    @Test
    public void testDown() throws Exception {
        initialize(EurekaHealthCheckHandlerTests.UpHealthConfiguration.class, EurekaHealthCheckHandlerTests.DownHealthConfiguration.class);
        InstanceStatus status = healthCheckHandler.getStatus(UNKNOWN);
        assertThat(status).isEqualTo(DOWN);
    }

    @Test
    public void testUnknown() throws Exception {
        initialize(EurekaHealthCheckHandlerTests.FatalHealthConfiguration.class);
        InstanceStatus status = healthCheckHandler.getStatus(UNKNOWN);
        assertThat(status).isEqualTo(UNKNOWN);
    }

    @Test
    public void testEurekaIgnored() throws Exception {
        initialize(EurekaHealthCheckHandlerTests.EurekaDownHealthConfiguration.class);
        InstanceStatus status = healthCheckHandler.getStatus(UP);
        assertThat(status).isEqualTo(UP);
    }

    public static class UpHealthConfiguration {
        @Bean
        public HealthIndicator healthIndicator() {
            return new AbstractHealthIndicator() {
                @Override
                protected void doHealthCheck(Health.Builder builder) throws Exception {
                    builder.up();
                }
            };
        }
    }

    public static class DownHealthConfiguration {
        @Bean
        public HealthIndicator healthIndicator() {
            return new AbstractHealthIndicator() {
                @Override
                protected void doHealthCheck(Health.Builder builder) throws Exception {
                    builder.down();
                }
            };
        }
    }

    public static class FatalHealthConfiguration {
        @Bean
        public HealthIndicator healthIndicator() {
            return new AbstractHealthIndicator() {
                @Override
                protected void doHealthCheck(Health.Builder builder) throws Exception {
                    builder.status("fatal");
                }
            };
        }
    }

    public static class EurekaDownHealthConfiguration {
        @Bean
        public DiscoveryHealthIndicator discoveryHealthIndicator() {
            return new DiscoveryClientHealthIndicator(null, null) {
                @Override
                public Health health() {
                    return Health.up().build();
                }
            };
        }

        @Bean
        public DiscoveryHealthIndicator eurekaHealthIndicator() {
            return new EurekaHealthIndicator(null, null, null) {
                @Override
                public Health health() {
                    return Health.down().build();
                }
            };
        }

        @Bean
        public DiscoveryCompositeHealthIndicator discoveryCompositeHealthIndicator(List<DiscoveryHealthIndicator> indicators) {
            return new DiscoveryCompositeHealthIndicator(new OrderedHealthAggregator(), indicators);
        }
    }
}

