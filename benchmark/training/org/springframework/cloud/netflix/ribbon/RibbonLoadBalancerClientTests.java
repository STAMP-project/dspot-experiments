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


import CommonClientConfigKey.IsSecure;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerStats;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient.RibbonServer;


/**
 *
 *
 * @author Spencer Gibb
 * @author Tim Ysewyn
 */
public class RibbonLoadBalancerClientTests {
    @Mock
    private SpringClientFactory clientFactory;

    @Mock
    private BaseLoadBalancer loadBalancer;

    @Mock
    private LoadBalancerStats loadBalancerStats;

    @Mock
    private ServerStats serverStats;

    private Server server = null;

    @Test
    public void reconstructURI() throws Exception {
        testReconstructURI("http");
    }

    @Test
    public void reconstructSecureURI() throws Exception {
        testReconstructURI("https");
    }

    @Test
    public void testReconstructSecureUriWithSpecialCharsPath() {
        testReconstructUriWithPath("https", "/foo=|");
    }

    @Test
    public void testReconstructUnsecureUriWithSpecialCharsPath() {
        testReconstructUriWithPath("http", "/foo=|");
    }

    @Test
    public void testReconstructHonorsRibbonServerScheme() {
        RibbonServer server = new RibbonServer("testService", new Server("ws", "myhost", 9080), false, Collections.singletonMap("mykey", "myvalue"));
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.when(config.get(IsSecure)).thenReturn(false);
        Mockito.when(clientFactory.getClientConfig(server.getServiceId())).thenReturn(config);
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        ServiceInstance serviceInstance = client.choose(server.getServiceId());
        URI uri = client.reconstructURI(serviceInstance, URI.create("http://testService"));
        assertThat(uri).hasScheme("ws").hasHost("myhost").hasPort(9080);
    }

    @Test
    public void testReconstructUriWithSecureClientConfig() throws Exception {
        RibbonServer server = getRibbonServer();
        IClientConfig config = Mockito.mock(IClientConfig.class);
        Mockito.when(config.get(IsSecure)).thenReturn(true);
        Mockito.when(clientFactory.getClientConfig(server.getServiceId())).thenReturn(config);
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        ServiceInstance serviceInstance = client.choose(server.getServiceId());
        URI uri = client.reconstructURI(serviceInstance, new URL(("http://" + (server.getServiceId()))).toURI());
        assertThat(uri.getHost()).isEqualTo(server.getHost());
        assertThat(uri.getPort()).isEqualTo(server.getPort());
        assertThat(uri.getScheme()).isEqualTo("https");
    }

    @Test
    public void testReconstructSecureUriWithoutScheme() throws Exception {
        testReconstructSchemelessUriWithoutClientConfig(getSecureRibbonServer(), "https");
    }

    @Test
    public void testReconstructUnsecureSchemelessUri() throws Exception {
        testReconstructSchemelessUriWithoutClientConfig(getRibbonServer(), "http");
    }

    @Test
    public void testChoose() {
        RibbonServer server = getRibbonServer();
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        ServiceInstance serviceInstance = client.choose(server.getServiceId());
        assertServiceInstance(server, serviceInstance);
        Mockito.verify(this.loadBalancer).chooseServer(ArgumentMatchers.eq("default"));
    }

    @Test
    public void testChooseWithHint() {
        Object hint = new Object();
        RibbonServer server = getRibbonServer();
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        ServiceInstance serviceInstance = client.choose(server.getServiceId(), hint);
        assertServiceInstance(server, serviceInstance);
        Mockito.verify(this.loadBalancer).chooseServer(ArgumentMatchers.same(hint));
    }

    @Test
    public void testChooseMissing() {
        BDDMockito.given(this.clientFactory.getLoadBalancer(this.loadBalancer.getName())).willReturn(null);
        BDDMockito.given(this.loadBalancer.getName()).willReturn("missingservice");
        RibbonLoadBalancerClient client = new RibbonLoadBalancerClient(this.clientFactory);
        ServiceInstance instance = client.choose("missingservice");
        assertThat(instance).as("instance wasn't null").isNull();
    }

    @Test
    public void testExecute() throws IOException {
        final RibbonServer server = getRibbonServer();
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        final String returnVal = "myval";
        Object actualReturn = client.execute(server.getServiceId(), ((LoadBalancerRequest<Object>) (( instance) -> {
            assertServiceInstance(server, instance);
            return returnVal;
        })));
        verifyServerStats();
        Mockito.verify(this.loadBalancer).chooseServer(ArgumentMatchers.eq("default"));
        assertThat(actualReturn).as("retVal was wrong").isEqualTo(returnVal);
    }

    @Test
    public void testExecuteWithHint() throws IOException {
        Object hint = new Object();
        final RibbonServer server = getRibbonServer();
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(server);
        final String returnVal = "myval";
        Object actualReturn = client.execute(server.getServiceId(), ((LoadBalancerRequest<Object>) (( instance) -> {
            assertServiceInstance(server, instance);
            return returnVal;
        })), hint);
        verifyServerStats();
        Mockito.verify(this.loadBalancer).chooseServer(ArgumentMatchers.same(hint));
        assertThat(actualReturn).as("retVal was wrong").isEqualTo(returnVal);
    }

    @Test
    public void testExecuteException() {
        final RibbonServer ribbonServer = getRibbonServer();
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(ribbonServer);
        try {
            client.execute(ribbonServer.getServiceId(), ( instance) -> {
                assertServiceInstance(ribbonServer, instance);
                throw new RuntimeException();
            });
            fail("Should have thrown exception");
        } catch (Exception ex) {
            assertThat(ex).isNotNull();
        }
        verifyServerStats();
    }

    @Test
    public void testExecuteIOException() {
        final RibbonServer ribbonServer = getRibbonServer();
        RibbonLoadBalancerClient client = getRibbonLoadBalancerClient(ribbonServer);
        try {
            client.execute(ribbonServer.getServiceId(), ( instance) -> {
                assertServiceInstance(ribbonServer, instance);
                throw new IOException();
            });
            fail("Should have thrown exception");
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(IOException.class);
        }
        verifyServerStats();
    }

    protected static class MyServer extends Server {
        public MyServer(String host, int port) {
            super(host, port);
        }
    }
}

