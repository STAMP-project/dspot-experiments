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
package org.springframework.cloud.netflix.ribbon.apache;


import CommonClientConfigKey.ConnectTimeout;
import CommonClientConfigKey.GZipPayload;
import CommonClientConfigKey.IsSecure;
import CommonClientConfigKey.MaxConnectionsPerHost;
import CommonClientConfigKey.MaxTotalConnections;
import CommonClientConfigKey.ReadTimeout;
import com.netflix.client.ClientException;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerStats;
import com.netflix.servo.monitor.Monitors;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author S?bastien Nussbaumer
 * @author Ryan Baxter
 * @author Gang Li
 */
public class RibbonLoadBalancingHttpClientTests {
    private ILoadBalancer loadBalancer;

    @Test
    public void testRequestConfigUseDefaultsNoOverride() throws Exception {
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.UseDefaults.class, null);
        assertThat(result.isRedirectsEnabled()).isFalse();
    }

    @Test
    public void testRequestConfigDoNotFollowRedirectsNoOverride() throws Exception {
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.DoNotFollowRedirects.class, null);
        assertThat(result.isRedirectsEnabled()).isFalse();
    }

    @Test
    public void testRequestConfigFollowRedirectsNoOverride() throws Exception {
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.FollowRedirects.class, null);
        assertThat(result.isRedirectsEnabled()).isTrue();
    }

    @Test
    public void testTimeouts() throws Exception {
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.Timeouts.class, null);
        assertThat(result.getConnectTimeout()).isEqualTo(60000);
        assertThat(result.getSocketTimeout()).isEqualTo(50000);
    }

    @Test
    public void testDefaultTimeouts() throws Exception {
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.UseDefaults.class, null);
        assertThat(result.getConnectTimeout()).isEqualTo(1000);
        assertThat(result.getSocketTimeout()).isEqualTo(1000);
    }

    @Test
    public void testCompressionDefault() throws Exception {
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.UseDefaults.class, null);
        assertThat(result.isContentCompressionEnabled()).isTrue();
    }

    @Test
    public void testCompressionDisabled() throws Exception {
        IClientConfig configOverride = DefaultClientConfigImpl.getClientConfigWithDefaultValues();
        configOverride.set(GZipPayload, false);
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.UseDefaults.class, configOverride);
        assertThat(result.isContentCompressionEnabled()).isFalse();
    }

    @Test
    public void testConnections() throws Exception {
        SpringClientFactory factory = new SpringClientFactory();
        factory.setApplicationContext(new AnnotationConfigApplicationContext(RibbonAutoConfiguration.class, RibbonLoadBalancingHttpClientTests.Connections.class));
        RetryableRibbonLoadBalancingHttpClient client = factory.getClient("service", RetryableRibbonLoadBalancingHttpClient.class);
        HttpClient delegate = client.getDelegate();
        PoolingHttpClientConnectionManager connManager = ((PoolingHttpClientConnectionManager) (ReflectionTestUtils.getField(delegate, "connManager")));
        assertThat(connManager.getMaxTotal()).isEqualTo(101);
        assertThat(connManager.getDefaultMaxPerRoute()).isEqualTo(201);
    }

    @Test
    public void testRequestConfigDoNotFollowRedirectsOverrideWithFollowRedirects() throws Exception {
        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(CommonClientConfigKey.FollowRedirects, true);
        override.set(IsSecure, false);
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.DoNotFollowRedirects.class, override);
        assertThat(result.isRedirectsEnabled()).isTrue();
    }

    @Test
    public void testRequestConfigFollowRedirectsOverrideWithDoNotFollowRedirects() throws Exception {
        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(CommonClientConfigKey.FollowRedirects, false);
        override.set(IsSecure, false);
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.FollowRedirects.class, override);
        assertThat(result.isRedirectsEnabled()).isFalse();
    }

    @Test
    public void testUpdatedTimeouts() throws Exception {
        SpringClientFactory factory = new SpringClientFactory();
        RequestConfig result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.Timeouts.class, null, factory);
        assertThat(result.getConnectTimeout()).isEqualTo(60000);
        assertThat(result.getSocketTimeout()).isEqualTo(50000);
        IClientConfig config = factory.getClientConfig("service");
        config.set(ConnectTimeout, 60);
        config.set(ReadTimeout, 50);
        result = getBuiltRequestConfig(RibbonLoadBalancingHttpClientTests.Timeouts.class, null, factory);
        assertThat(result.getConnectTimeout()).isEqualTo(60);
        assertThat(result.getSocketTimeout()).isEqualTo(50);
    }

    @Test
    public void testNeverRetry() throws Exception {
        ServerIntrospector introspector = Mockito.mock(ServerIntrospector.class);
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        Mockito.doThrow(new IOException("boom")).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
        clientConfig.setClientName("foo");
        RibbonLoadBalancingHttpClient client = new RibbonLoadBalancingHttpClient(delegate, clientConfig, introspector);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.when(request.toRequest(ArgumentMatchers.any(RequestConfig.class))).thenReturn(Mockito.mock(HttpUriRequest.class));
        try {
            client.execute(request, null);
            fail("Expected IOException");
        } catch (IOException e) {
        } finally {
            Mockito.verify(delegate, Mockito.times(1)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        }
    }

    @Test
    public void testRetryFail() throws Exception {
        int retriesNextServer = 0;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = false;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.GET;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        StatusLine fourOFourStatusLine = Mockito.mock(StatusLine.class);
        CloseableHttpResponse fourOFourResponse = Mockito.mock(CloseableHttpResponse.class);
        Locale locale = new Locale("en");
        Mockito.doReturn(locale).when(fourOFourResponse).getLocale();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpEntity entity = Mockito.mock(HttpEntity.class);
                Mockito.doReturn(new ByteArrayInputStream("test".getBytes())).when(entity).getContent();
                return entity;
            }
        }).when(fourOFourResponse).getEntity();
        Mockito.doReturn(404).when(fourOFourStatusLine).getStatusCode();
        Mockito.doReturn(fourOFourStatusLine).when(fourOFourResponse).getStatusLine();
        Mockito.doReturn(locale).when(fourOFourResponse).getLocale();
        Mockito.doReturn(fourOFourResponse).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "404", myBackOffPolicy);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uri).when(uriRequest).getURI();
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
        Mockito.verify(delegate, Mockito.times(2)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        byte[] buf = new byte[100];
        InputStream inputStream = returnedResponse.getInputStream();
        int read = inputStream.read(buf);
        assertThat(new String(buf, 0, read)).isEqualTo("test");
    }

    @Test
    public void testRetrySameServerOnly() throws Exception {
        int retriesNextServer = 0;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = false;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.GET;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        AbstractLoadBalancer lb = Mockito.mock(AbstractLoadBalancer.class);
        LoadBalancerStats lbStats = Mockito.mock(LoadBalancerStats.class);
        Mockito.doReturn(lbStats).when(lb).getLoadBalancerStats();
        ServerStats serverStats = Mockito.mock(ServerStats.class);
        Mockito.doReturn(serverStats).when(lbStats).getSingleServerStat(ArgumentMatchers.any(Server.class));
        RibbonLoadBalancerContext ribbonLoadBalancerContext = Mockito.mock(RibbonLoadBalancerContext.class);
        Mockito.doReturn(lb).when(ribbonLoadBalancerContext).getLoadBalancer();
        Mockito.doReturn(Monitors.newTimer("_LoadBalancerExecutionTimer", TimeUnit.MILLISECONDS)).when(ribbonLoadBalancerContext).getExecuteTracer();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", null);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uri).when(uriRequest).getURI();
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
        Mockito.verify(delegate, Mockito.times(2)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(1)).chooseServer(ArgumentMatchers.eq(serviceName));
        Mockito.verify(ribbonLoadBalancerContext, Mockito.times(1)).noteRequestCompletion(serverStats, response, null, 0, null);
    }

    @Test
    public void testRetryNextServer() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = false;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.GET;
        URI uri = new URI((((("http://" + host) + ":") + port) + "/a%2Bb"));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", myBackOffPolicy);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uri).when(uriRequest).getURI();
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        client.execute(request, null);
        Mockito.verify(delegate, Mockito.times(3)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(2)).chooseServer(ArgumentMatchers.eq(serviceName));
        assertThat(myBackOffPolicy.getCount()).isEqualTo(2);
        Mockito.verify(request, Mockito.times(3)).withNewUri(ArgumentMatchers.argThat(new ArgumentMatcher<URI>() {
            @Override
            public boolean matches(URI argument) {
                if (argument.equals(uri)) {
                    return true;
                }
                return false;
            }
        }));
    }

    @Test
    public void testRetryOnPost() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = true;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", myBackOffPolicy);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
        Mockito.verify(response, Mockito.times(0)).close();
        Mockito.verify(delegate, Mockito.times(3)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(2)).chooseServer(ArgumentMatchers.eq(serviceName));
        assertThat(myBackOffPolicy.getCount()).isEqualTo(2);
    }

    @Test
    public void testDoubleEncoding() throws Exception {
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.GET;
        final URI uri = new URI((((("https://" + host) + ":") + port) + "/a%2Bb"));
        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
        clientConfig.setClientName(serviceName);
        ServerIntrospector introspector = Mockito.mock(ServerIntrospector.class);
        RibbonCommandContext context = new RibbonCommandContext(serviceName, method.toString(), uri.toString(), false, new org.springframework.util.LinkedMultiValueMap<String, String>(), new org.springframework.util.LinkedMultiValueMap<String, String>(), new ByteArrayInputStream("bar".getBytes()), new ArrayList<org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer>());
        RibbonApacheHttpRequest request = new RibbonApacheHttpRequest(context);
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        RibbonLoadBalancingHttpClient client = new RibbonLoadBalancingHttpClient(delegate, clientConfig, introspector);
        client.execute(request, null);
        Mockito.verify(response, Mockito.times(0)).close();
        Mockito.verify(delegate, Mockito.times(1)).execute(ArgumentMatchers.argThat(new ArgumentMatcher<HttpUriRequest>() {
            @Override
            public boolean matches(HttpUriRequest argument) {
                if (argument instanceof HttpUriRequest) {
                    HttpUriRequest arg = ((HttpUriRequest) (argument));
                    return arg.getURI().equals(uri);
                }
                return false;
            }
        }));
    }

    @Test
    public void testDoubleEncodingWithRetry() throws Exception {
        int retriesNextServer = 0;
        int retriesSameServer = 0;
        boolean retryable = true;
        boolean retryOnAllOps = true;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.GET;
        final URI uri = new URI((((("https://" + host) + ":") + port) + "/a%20b"));
        RibbonCommandContext context = new RibbonCommandContext(serviceName, method.toString(), uri.toString(), true, new org.springframework.util.LinkedMultiValueMap<String, String>(), new org.springframework.util.LinkedMultiValueMap<String, String>(), new ByteArrayInputStream(new String("bar").getBytes()), new ArrayList<org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer>());
        RibbonApacheHttpRequest request = new RibbonApacheHttpRequest(context);
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", null, true);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        client.execute(request, null);
        Mockito.verify(response, Mockito.times(0)).close();
        Mockito.verify(delegate, Mockito.times(1)).execute(ArgumentMatchers.argThat(new ArgumentMatcher<HttpUriRequest>() {
            @Override
            public boolean matches(HttpUriRequest argument) {
                if (argument instanceof HttpUriRequest) {
                    HttpUriRequest arg = ((HttpUriRequest) (argument));
                    return arg.getURI().equals(uri);
                }
                return false;
            }
        }));
    }

    @Test
    public void testNoRetryOnPost() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = false;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", null);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uri).when(uriRequest).getURI();
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        try {
            client.execute(request, null);
            fail("Expected IOException");
        } catch (IOException e) {
        } finally {
            Mockito.verify(response, Mockito.times(0)).close();
            Mockito.verify(delegate, Mockito.times(1)).execute(ArgumentMatchers.any(HttpUriRequest.class));
            Mockito.verify(lb, Mockito.times(1)).chooseServer(ArgumentMatchers.eq(serviceName));
        }
    }

    @Test
    public void testRetryOnStatusCode() throws Exception {
        int retriesNextServer = 0;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = false;
        String serviceName = "foo";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.GET;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Locale locale = new Locale("en");
        Mockito.doReturn(locale).when(response).getLocale();
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        final CloseableHttpResponse fourOFourResponse = Mockito.mock(CloseableHttpResponse.class);
        Mockito.doReturn(locale).when(fourOFourResponse).getLocale();
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContentLength(5);
        entity.setContent(new ByteArrayInputStream("error".getBytes()));
        Mockito.doReturn(entity).when(fourOFourResponse).getEntity();
        StatusLine fourOFourStatusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(404).when(fourOFourStatusLine).getStatusCode();
        Mockito.doReturn(fourOFourStatusLine).when(fourOFourResponse).getStatusLine();
        Mockito.doReturn(fourOFourResponse).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "404", myBackOffPolicy);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uri).when(uriRequest).getURI();
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        client.execute(request, null);
        Mockito.verify(fourOFourResponse, Mockito.times(1)).close();
        Mockito.verify(delegate, Mockito.times(2)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(1)).chooseServer(ArgumentMatchers.eq(serviceName));
        assertThat(myBackOffPolicy.getCount()).isEqualTo(1);
    }

    @Test
    public void retryListenerTest() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = true;
        String serviceName = "listener";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RibbonLoadBalancingHttpClientTests.MyRetryListener myRetryListener = new RibbonLoadBalancingHttpClientTests.MyRetryListener();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", myBackOffPolicy, false, new RetryListener[]{ myRetryListener });
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
        Mockito.verify(response, Mockito.times(0)).close();
        Mockito.verify(delegate, Mockito.times(3)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(2)).chooseServer(ArgumentMatchers.eq(serviceName));
        assertThat(myBackOffPolicy.getCount()).isEqualTo(2);
        assertThat(myRetryListener.getOnError()).isEqualTo(2);
    }

    @Test
    public void retryDefaultListenerTest() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = true;
        String serviceName = "listener";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RibbonLoadBalancingHttpClientTests.MyRetryListener myRetryListener = new RibbonLoadBalancingHttpClientTests.MyRetryListener();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", myBackOffPolicy, false, new RetryListener[]{  });
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
        Mockito.verify(response, Mockito.times(0)).close();
        Mockito.verify(delegate, Mockito.times(3)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(2)).chooseServer(ArgumentMatchers.eq(serviceName));
        assertThat(myBackOffPolicy.getCount()).isEqualTo(2);
        assertThat(myRetryListener.getOnError()).isEqualTo(0);
    }

    @Test(expected = TerminatedRetryException.class)
    public void retryListenerTestNoRetry() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = true;
        String serviceName = "listener";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RibbonLoadBalancingHttpClientTests.MyRetryListenerNotRetry myRetryListenerNotRetry = new RibbonLoadBalancingHttpClientTests.MyRetryListenerNotRetry();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", myBackOffPolicy, false, new RetryListener[]{ myRetryListenerNotRetry });
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
    }

    @Test
    public void retryWithOriginalConstructorTest() throws Exception {
        int retriesNextServer = 1;
        int retriesSameServer = 1;
        boolean retryable = true;
        boolean retryOnAllOps = true;
        String serviceName = "listener";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        final CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        Mockito.doReturn(200).when(statusLine).getStatusCode();
        Mockito.doReturn(statusLine).when(response).getStatusLine();
        Mockito.doThrow(new IOException("boom")).doThrow(new IOException("boom again")).doReturn(response).when(delegate).execute(ArgumentMatchers.any(HttpUriRequest.class));
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RibbonLoadBalancerContext ribbonLoadBalancerContext = new RibbonLoadBalancerContext(lb);
        RibbonLoadBalancingHttpClientTests.MyBackOffPolicy myBackOffPolicy = new RibbonLoadBalancingHttpClientTests.MyBackOffPolicy();
        RetryableRibbonLoadBalancingHttpClient client = setupClientForRetry(retriesNextServer, retriesSameServer, retryable, retryOnAllOps, serviceName, host, port, delegate, lb, "", myBackOffPolicy, false);
        client.setRibbonLoadBalancerContext(ribbonLoadBalancerContext);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        RibbonApacheHttpResponse returnedResponse = client.execute(request, null);
        Mockito.verify(response, Mockito.times(0)).close();
        Mockito.verify(delegate, Mockito.times(3)).execute(ArgumentMatchers.any(HttpUriRequest.class));
        Mockito.verify(lb, Mockito.times(2)).chooseServer(ArgumentMatchers.eq(serviceName));
        assertThat(myBackOffPolicy.getCount()).isEqualTo(2);
    }

    @Test
    public void noServersFoundTest() throws Exception {
        String serviceName = "noservers";
        String host = serviceName;
        int port = 80;
        HttpMethod method = HttpMethod.POST;
        URI uri = new URI(((("http://" + host) + ":") + port));
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RetryableRibbonLoadBalancingHttpClient client = setupClientForServerValidation(serviceName, host, port, delegate, lb);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(null).when(lb).chooseServer(ArgumentMatchers.eq(serviceName));
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        try {
            client.execute(request, null);
            fail("Expected IOException for no servers available");
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
        CloseableHttpClient delegate = Mockito.mock(CloseableHttpClient.class);
        ILoadBalancer lb = Mockito.mock(ILoadBalancer.class);
        RetryableRibbonLoadBalancingHttpClient client = setupClientForServerValidation(serviceName, host, port, delegate, lb);
        RibbonApacheHttpRequest request = Mockito.mock(RibbonApacheHttpRequest.class);
        Mockito.doReturn(new Server(null, 8000)).when(lb).chooseServer(ArgumentMatchers.eq(serviceName));
        Mockito.doReturn(method).when(request).getMethod();
        Mockito.doReturn(uri).when(request).getURI();
        Mockito.doReturn(request).when(request).withNewUri(ArgumentMatchers.any(URI.class));
        HttpUriRequest uriRequest = Mockito.mock(HttpUriRequest.class);
        Mockito.doReturn(uriRequest).when(request).toRequest(ArgumentMatchers.any(RequestConfig.class));
        try {
            client.execute(request, null);
            fail("Expected IOException for no servers available");
        } catch (ClientException ex) {
            assertThat(ex.getMessage()).contains("Invalid Server for: ");
        }
    }

    class MyBackOffPolicy implements BackOffPolicy {
        private int count = 0;

        @Override
        public BackOffContext start(RetryContext retryContext) {
            return null;
        }

        @Override
        public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
            (count)++;
        }

        int getCount() {
            return count;
        }
    }

    class MyRetryListener implements RetryListener {
        private int onError = 0;

        int getOnError() {
            return onError;
        }

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            (onError)++;
        }
    }

    @Configuration
    protected static class DoNotFollowRedirects {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(CommonClientConfigKey.FollowRedirects, false);
            return config;
        }
    }

    @Configuration
    protected static class Connections {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(MaxTotalConnections, 101);
            config.set(MaxConnectionsPerHost, 201);
            return config;
        }
    }

    @Configuration
    protected static class Timeouts {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(ConnectTimeout, 60000);
            config.set(ReadTimeout, 50000);
            return config;
        }
    }

    @Configuration
    protected static class UseDefaults {}

    @Configuration
    protected static class FollowRedirects {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(CommonClientConfigKey.FollowRedirects, true);
            return config;
        }
    }

    class MyRetryListenerNotRetry implements RetryListener {
        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            return false;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        }
    }
}

