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
package org.springframework.cloud.netflix.ribbon;


import CommonClientConfigKey.MaxAutoRetries;
import CommonClientConfigKey.MaxAutoRetriesNextServer;
import CommonClientConfigKey.OkToRetryOnAllOperations;
import HttpMethod.GET;
import HttpMethod.POST;
import RibbonLoadBalancedRetryPolicy.RETRYABLE_STATUS_CODES;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.ServerStats;
import java.io.IOException;
import java.net.SocketException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient.RibbonServer;
import org.springframework.http.HttpRequest;


/**
 *
 *
 * @author Ryan Baxter
 */
public class RibbonLoadBalancedRetryFactoryTests {
    @Mock
    private SpringClientFactory clientFactory;

    @Mock
    private BaseLoadBalancer loadBalancer;

    @Mock
    private LoadBalancerStats loadBalancerStats;

    @Mock
    private ServerStats serverStats;

    @Test
    public void testGetRetryPolicyNoRetry() throws Exception {
        int sameServer = 0;
        int nextServer = 0;
        boolean retryOnAllOps = false;
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.doReturn(sameServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(sameServer).when(config).getPropertyAsInteger(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).getPropertyAsInteger(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(retryOnAllOps).when(config).get(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(retryOnAllOps).when(config).getPropertyAsBoolean(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.anyBoolean());
        Mockito.doReturn("").when(config).getPropertyAsString(ArgumentMatchers.eq(RETRYABLE_STATUS_CODES), ArgumentMatchers.eq(""));
        Mockito.doReturn(server.getServiceId()).when(config).getClientName();
        Mockito.doReturn(config).when(clientFactory).getClientConfig(ArgumentMatchers.eq(server.getServiceId()));
        clientFactory.getLoadBalancerContext(server.getServiceId()).setRetryHandler(new com.netflix.client.DefaultLoadBalancerRetryHandler(config));
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        RibbonLoadBalancedRetryFactory factory = new RibbonLoadBalancedRetryFactory(clientFactory);
        LoadBalancedRetryPolicy policy = factory.createRetryPolicy(server.getServiceId(), client);
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.doReturn(GET).when(request).getMethod();
        LoadBalancedRetryContext context = new LoadBalancedRetryContext(null, request);
        assertThat(policy.canRetryNextServer(context)).isTrue();
        assertThat(policy.canRetrySameServer(context)).isFalse();
        assertThat(policy.retryableStatusCode(400)).isFalse();
    }

    @Test
    public void testGetRetryPolicyNotGet() throws Exception {
        int sameServer = 3;
        int nextServer = 3;
        boolean retryOnAllOps = false;
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.doReturn(sameServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(sameServer).when(config).getPropertyAsInteger(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).getPropertyAsInteger(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(retryOnAllOps).when(config).get(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(retryOnAllOps).when(config).getPropertyAsBoolean(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.anyBoolean());
        Mockito.doReturn("").when(config).getPropertyAsString(ArgumentMatchers.eq(RETRYABLE_STATUS_CODES), ArgumentMatchers.eq(""));
        Mockito.doReturn(server.getServiceId()).when(config).getClientName();
        Mockito.doReturn(config).when(clientFactory).getClientConfig(ArgumentMatchers.eq(server.getServiceId()));
        clientFactory.getLoadBalancerContext(server.getServiceId()).setRetryHandler(new com.netflix.client.DefaultLoadBalancerRetryHandler(config));
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        RibbonLoadBalancedRetryFactory factory = new RibbonLoadBalancedRetryFactory(clientFactory);
        LoadBalancedRetryPolicy policy = factory.createRetryPolicy(server.getServiceId(), client);
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.doReturn(POST).when(request).getMethod();
        LoadBalancedRetryContext context = new LoadBalancedRetryContext(null, request);
        assertThat(policy.canRetryNextServer(context)).isFalse();
        assertThat(policy.canRetrySameServer(context)).isFalse();
        assertThat(policy.retryableStatusCode(400)).isFalse();
    }

    @Test
    public void testGetRetryPolicyRetryOnNonGet() throws Exception {
        int sameServer = 3;
        int nextServer = 3;
        boolean retryOnAllOps = true;
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.doReturn(sameServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(sameServer).when(config).getPropertyAsInteger(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).getPropertyAsInteger(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(retryOnAllOps).when(config).get(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(retryOnAllOps).when(config).getPropertyAsBoolean(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.anyBoolean());
        Mockito.doReturn("").when(config).getPropertyAsString(ArgumentMatchers.eq(RETRYABLE_STATUS_CODES), ArgumentMatchers.eq(""));
        Mockito.doReturn(server.getServiceId()).when(config).getClientName();
        Mockito.doReturn(config).when(clientFactory).getClientConfig(ArgumentMatchers.eq(server.getServiceId()));
        clientFactory.getLoadBalancerContext(server.getServiceId()).initWithNiwsConfig(config);
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        RibbonLoadBalancedRetryFactory factory = new RibbonLoadBalancedRetryFactory(clientFactory);
        LoadBalancedRetryPolicy policy = factory.createRetryPolicy(server.getServiceId(), client);
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.doReturn(POST).when(request).getMethod();
        LoadBalancedRetryContext context = new LoadBalancedRetryContext(null, request);
        assertThat(policy.canRetryNextServer(context)).isTrue();
        assertThat(policy.canRetrySameServer(context)).isTrue();
        assertThat(policy.retryableStatusCode(400)).isFalse();
    }

    @Test
    public void testGetRetryPolicyRetryCount() throws Exception {
        int sameServer = 3;
        int nextServer = 3;
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.doReturn(sameServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(false).when(config).get(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.eq(false));
        Mockito.doReturn(config).when(clientFactory).getClientConfig(ArgumentMatchers.eq(server.getServiceId()));
        Mockito.doReturn("").when(config).getPropertyAsString(ArgumentMatchers.eq(RETRYABLE_STATUS_CODES), ArgumentMatchers.eq(""));
        clientFactory.getLoadBalancerContext(server.getServiceId()).setRetryHandler(new com.netflix.client.DefaultLoadBalancerRetryHandler(config));
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        RibbonLoadBalancedRetryFactory factory = new RibbonLoadBalancedRetryFactory(clientFactory);
        LoadBalancedRetryPolicy policy = factory.createRetryPolicy(server.getServiceId(), client);
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.doReturn(GET).when(request).getMethod();
        LoadBalancedRetryContext context = Mockito.spy(new LoadBalancedRetryContext(null, request));
        // Loop through as if we are retrying a request until we exhaust the number of
        // retries
        // outer loop is for next server retries
        // inner loop is for same server retries
        for (int i = 0; i < (nextServer + 1); i++) {
            // iterate once time beyond the same server retry limit to cause us to reset
            // the same sever counter and increment the next server counter
            for (int j = 0; j < (sameServer + 1); j++) {
                if (j < 3) {
                    assertThat(policy.canRetrySameServer(context)).isTrue();
                } else {
                    assertThat(policy.canRetrySameServer(context)).isFalse();
                }
                policy.registerThrowable(context, new IOException());
            }
            if (i < 3) {
                assertThat(policy.canRetryNextServer(context)).isTrue();
            } else {
                assertThat(policy.canRetryNextServer(context)).isFalse();
            }
        }
        assertThat(context.isExhaustedOnly()).isTrue();
        assertThat(policy.retryableStatusCode(400)).isFalse();
        Mockito.verify(context, Mockito.times(4)).setServiceInstance(ArgumentMatchers.any(ServiceInstance.class));
    }

    @Test
    public void testCiruitRelatedExceptionsUpdateServerStats() throws Exception {
        int sameServer = 3;
        int nextServer = 3;
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.doReturn(sameServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(false).when(config).get(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.eq(false));
        Mockito.doReturn(config).when(clientFactory).getClientConfig(ArgumentMatchers.eq(server.getServiceId()));
        Mockito.doReturn("").when(config).getPropertyAsString(ArgumentMatchers.eq(RETRYABLE_STATUS_CODES), ArgumentMatchers.eq(""));
        clientFactory.getLoadBalancerContext(server.getServiceId()).setRetryHandler(new com.netflix.client.DefaultLoadBalancerRetryHandler(config));
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        RibbonLoadBalancedRetryFactory factory = new RibbonLoadBalancedRetryFactory(clientFactory);
        LoadBalancedRetryPolicy policy = factory.createRetryPolicy(server.getServiceId(), client);
        HttpRequest request = Mockito.mock(HttpRequest.class);
        LoadBalancedRetryContext context = Mockito.spy(new LoadBalancedRetryContext(null, request));
        Mockito.doReturn(server).when(context).getServiceInstance();
        policy.registerThrowable(context, new IOException());
        Mockito.verify(serverStats, Mockito.times(0)).incrementSuccessiveConnectionFailureCount();
        // Circuit Related should increment failure count
        policy.registerThrowable(context, new SocketException());
        Mockito.verify(serverStats, Mockito.times(1)).incrementSuccessiveConnectionFailureCount();
    }

    @Test
    public void testRetryableStatusCodes() throws Exception {
        int sameServer = 3;
        int nextServer = 3;
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.doReturn(sameServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetries), ArgumentMatchers.anyInt());
        Mockito.doReturn(nextServer).when(config).get(ArgumentMatchers.eq(MaxAutoRetriesNextServer), ArgumentMatchers.anyInt());
        Mockito.doReturn(false).when(config).get(ArgumentMatchers.eq(OkToRetryOnAllOperations), ArgumentMatchers.eq(false));
        Mockito.doReturn(config).when(clientFactory).getClientConfig(ArgumentMatchers.eq(server.getServiceId()));
        Mockito.doReturn("404, 418,502,foo, ,").when(config).getPropertyAsString(ArgumentMatchers.eq(RETRYABLE_STATUS_CODES), ArgumentMatchers.eq(""));
        clientFactory.getLoadBalancerContext(server.getServiceId()).setRetryHandler(new com.netflix.client.DefaultLoadBalancerRetryHandler(config));
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        RibbonLoadBalancedRetryFactory factory = new RibbonLoadBalancedRetryFactory(clientFactory);
        LoadBalancedRetryPolicy policy = factory.createRetryPolicy(server.getServiceId(), client);
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.doReturn(GET).when(request).getMethod();
        assertThat(policy.retryableStatusCode(400)).isFalse();
        assertThat(policy.retryableStatusCode(404)).isTrue();
        assertThat(policy.retryableStatusCode(418)).isTrue();
        assertThat(policy.retryableStatusCode(502)).isTrue();
    }
}

