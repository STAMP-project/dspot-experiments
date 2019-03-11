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
package org.springframework.cloud.netflix.ribbon.okhttp;


import com.netflix.client.ClientException;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import java.net.URI;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Ryan Baxter
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({ "ribbon.okhttp.enabled: true", "ribbon.httpclient.enabled: false" })
@ContextConfiguration(classes = { RibbonAutoConfiguration.class, HttpClientConfiguration.class, RibbonClientConfiguration.class, LoadBalancerAutoConfiguration.class })
public class SpringRetryEnabledOkHttpClientTests implements ApplicationContextAware {
    private ApplicationContext context;

    private ILoadBalancer loadBalancer;

    @Test
    public void testLoadBalancedRetryFactoryBean() throws Exception {
        Map<String, LoadBalancedRetryFactory> factories = context.getBeansOfType(LoadBalancedRetryFactory.class);
        assertThat(factories.values()).hasSize(1);
        assertThat(factories.values().toArray()[0]).isInstanceOf(RibbonLoadBalancedRetryFactory.class);
        Map<String, OkHttpLoadBalancingClient> clients = context.getBeansOfType(OkHttpLoadBalancingClient.class);
        assertThat(clients.values()).hasSize(1);
        assertThat(clients.values().toArray()[0]).isInstanceOf(RetryableOkHttpLoadBalancingClient.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = ((RibbonLoadBalancerContext) (ReflectionTestUtils.getField(clients.values().toArray()[0], RetryableOkHttpLoadBalancingClient.class, "ribbonLoadBalancerContext")));
        assertThat(ribbonLoadBalancerContext).as("RetryableOkHttpLoadBalancingClient.ribbonLoadBalancerContext should not be null").isNotNull();
    }

    @Test
    public void noServersFoundTest() throws Exception {
        String serviceName = "noservers";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        OkHttpClient delegate = Mockito.mock(OkHttpClient.class);
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RetryableOkHttpLoadBalancingClient client = setupClientForServerValidation(serviceName, host, port, delegate, lb);
        OkHttpRibbonRequest request = Mockito.mock(OkHttpRibbonRequest.class);
        Mockito.doReturn(null).when(lb).chooseServer(ArgumentMatchers.eq(serviceName));
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        Request okRequest = new Request.Builder().url("ws:testerror.sc").build();
        Mockito.doReturn(okRequest).when(request).toRequest();
        try {
            client.execute(request, null);
            fail("Expected ClientException for no servers available");
        } catch (ClientException ex) {
            assertThat(ex.getMessage()).contains("Load balancer does not have available server for client");
        }
    }

    @Test
    public void invalidServerTest() throws Exception {
        String serviceName = "noservers";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        OkHttpClient delegate = Mockito.mock(OkHttpClient.class);
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RetryableOkHttpLoadBalancingClient client = setupClientForServerValidation(serviceName, host, port, delegate, lb);
        OkHttpRibbonRequest request = Mockito.mock(OkHttpRibbonRequest.class);
        Mockito.doReturn(new Server(null, 8000)).when(lb).chooseServer(ArgumentMatchers.eq(serviceName));
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        Request okRequest = new Request.Builder().url("ws:testerror.sc").build();
        Mockito.doReturn(okRequest).when(request).toRequest();
        try {
            client.execute(request, null);
            fail("Expected ClientException for no Invalid Host");
        } catch (ClientException ex) {
            assertThat(ex.getMessage()).contains("Invalid Server for: ");
        }
    }
}

