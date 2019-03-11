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


import CommonClientConfigKey.ConnectTimeout;
import CommonClientConfigKey.MaxHttpConnectionsPerHost;
import CommonClientConfigKey.ReadTimeout;
import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.client.http.RestClient;
import java.net.URI;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration.OverrideRestClient;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.ribbon.okhttp.OkHttpLoadBalancingClient;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Spencer Gibb
 */
public class RibbonClientConfigurationTests {
    private RibbonClientConfigurationTests.CountingConfig config;

    @Mock
    private ServerIntrospector inspector;

    @Test
    public void restClientInitCalledOnce() {
        new RibbonClientConfigurationTests.TestRestClient(this.config);
        assertThat(this.config.count).isEqualTo(1);
    }

    @Test
    public void restClientWithSecureServer() throws Exception {
        RibbonClientConfigurationTests.CountingConfig config = new RibbonClientConfigurationTests.CountingConfig();
        config.setProperty(ConnectTimeout, "1");
        config.setProperty(ReadTimeout, "1");
        config.setProperty(MaxHttpConnectionsPerHost, "1");
        setClientName("bar");
        Server server = new Server("example.com", 443);
        URI uri = new RibbonClientConfigurationTests.TestRestClient(config).reconstructURIWithServer(server, new URI("/foo"));
        assertThat(uri.getScheme()).isEqualTo("https");
        assertThat(uri.getHost()).isEqualTo("example.com");
    }

    @Test
    public void testSecureUriFromClientConfig() throws Exception {
        Server server = new Server("foo", 7777);
        Mockito.when(this.inspector.isSecure(server)).thenReturn(true);
        for (AbstractLoadBalancerAwareClient client : clients()) {
            URI uri = client.reconstructURIWithServer(server, new URI("http://foo/"));
            assertThat(uri).as(getReason(client)).isEqualTo(new URI("https://foo:7777/"));
        }
    }

    @Test
    public void testInSecureUriFromClientConfig() throws Exception {
        Server server = new Server("foo", 7777);
        Mockito.when(this.inspector.isSecure(server)).thenReturn(false);
        for (AbstractLoadBalancerAwareClient client : clients()) {
            URI uri = client.reconstructURIWithServer(server, new URI("http://foo/"));
            assertThat(uri).as(getReason(client)).isEqualTo(new URI("http://foo:7777/"));
        }
    }

    @Test
    public void testNotDoubleEncodedWhenSecure() throws Exception {
        Server server = new Server("foo", 7777);
        Mockito.when(this.inspector.isSecure(server)).thenReturn(true);
        for (AbstractLoadBalancerAwareClient client : clients()) {
            URI uri = client.reconstructURIWithServer(server, new URI("http://foo/%20bar"));
            assertThat(uri).as(getReason(client)).isEqualTo(new URI("https://foo:7777/%20bar"));
        }
    }

    @Test
    public void testPlusInQueryStringGetsRewrittenWhenServerIsSecure() throws Exception {
        Server server = new Server("foo", 7777);
        Mockito.when(this.inspector.isSecure(server)).thenReturn(true);
        for (AbstractLoadBalancerAwareClient client : clients()) {
            URI uri = client.reconstructURIWithServer(server, new URI("http://foo/%20bar?hello=1+2"));
            assertThat(uri).isEqualTo(new URI("https://foo:7777/%20bar?hello=1%202"));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDefaultsToApacheHttpClient() {
        testClient(RibbonLoadBalancingHttpClient.class, null, RestClient.class, OkHttpLoadBalancingClient.class);
        testClient(RibbonLoadBalancingHttpClient.class, new String[]{ "ribbon.httpclient.enabled" }, RestClient.class, OkHttpLoadBalancingClient.class);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testEnableRestClient() {
        testClient(RestClient.class, new String[]{ "ribbon.restclient.enabled" }, RibbonLoadBalancingHttpClient.class, OkHttpLoadBalancingClient.class);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testEnableOkHttpClient() {
        testClient(OkHttpLoadBalancingClient.class, new String[]{ "ribbon.okhttp.enabled" }, RibbonLoadBalancingHttpClient.class, RestClient.class);
    }

    @Configuration
    @EnableAutoConfiguration
    protected static class TestLBConfig {}

    static class CountingConfig extends DefaultClientConfigImpl {
        int count = 0;
    }

    static final class TestRestClient extends OverrideRestClient {
        private TestRestClient(IClientConfig ncc) {
            super(ncc, new DefaultServerIntrospector());
        }

        @Override
        public void initWithNiwsConfig(IClientConfig clientConfig) {
            (((RibbonClientConfigurationTests.CountingConfig) (clientConfig)).count)++;
            super.initWithNiwsConfig(clientConfig);
        }
    }
}

