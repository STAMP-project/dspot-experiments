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
package org.springframework.boot.actuate.couchbase;


import Status.DOWN;
import Status.UP;
import com.couchbase.client.core.message.internal.DiagnosticsReport;
import com.couchbase.client.core.message.internal.EndpointHealth;
import com.couchbase.client.core.service.ServiceType;
import com.couchbase.client.core.state.LifecycleState;
import com.couchbase.client.java.Cluster;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link CouchbaseReactiveHealthIndicator}.
 */
public class CouchbaseReactiveHealthIndicatorTests {
    @Test
    @SuppressWarnings("unchecked")
    public void couchbaseClusterIsUp() {
        Cluster cluster = Mockito.mock(Cluster.class);
        CouchbaseReactiveHealthIndicator healthIndicator = new CouchbaseReactiveHealthIndicator(cluster);
        List<EndpointHealth> endpoints = Arrays.asList(new EndpointHealth(ServiceType.BINARY, LifecycleState.CONNECTED, new InetSocketAddress(0), new InetSocketAddress(0), 1234, "endpoint-1"));
        DiagnosticsReport diagnostics = new DiagnosticsReport(endpoints, "test-sdk", "test-id", null);
        BDDMockito.given(cluster.diagnostics()).willReturn(diagnostics);
        Health health = healthIndicator.health().block(Duration.ofSeconds(30));
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).containsEntry("sdk", "test-sdk");
        assertThat(health.getDetails()).containsKey("endpoints");
        assertThat(((List<Map<String, Object>>) (health.getDetails().get("endpoints")))).hasSize(1);
        Mockito.verify(cluster).diagnostics();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void couchbaseClusterIsDown() {
        Cluster cluster = Mockito.mock(Cluster.class);
        CouchbaseReactiveHealthIndicator healthIndicator = new CouchbaseReactiveHealthIndicator(cluster);
        List<EndpointHealth> endpoints = Arrays.asList(new EndpointHealth(ServiceType.BINARY, LifecycleState.CONNECTED, new InetSocketAddress(0), new InetSocketAddress(0), 1234, "endpoint-1"), new EndpointHealth(ServiceType.BINARY, LifecycleState.CONNECTING, new InetSocketAddress(0), new InetSocketAddress(0), 1234, "endpoint-2"));
        DiagnosticsReport diagnostics = new DiagnosticsReport(endpoints, "test-sdk", "test-id", null);
        BDDMockito.given(cluster.diagnostics()).willReturn(diagnostics);
        Health health = healthIndicator.health().block(Duration.ofSeconds(30));
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).containsEntry("sdk", "test-sdk");
        assertThat(health.getDetails()).containsKey("endpoints");
        assertThat(((List<Map<String, Object>>) (health.getDetails().get("endpoints")))).hasSize(2);
        Mockito.verify(cluster).diagnostics();
    }
}

