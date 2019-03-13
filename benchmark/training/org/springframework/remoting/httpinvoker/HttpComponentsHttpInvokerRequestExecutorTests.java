/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.remoting.httpinvoker;


import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class HttpComponentsHttpInvokerRequestExecutorTests {
    @Test
    public void customizeConnectionTimeout() throws IOException {
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor();
        executor.setConnectTimeout(5000);
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        Assert.assertEquals(5000, getConfig().getConnectTimeout());
    }

    @Test
    public void customizeConnectionRequestTimeout() throws IOException {
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor();
        executor.setConnectionRequestTimeout(7000);
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        Assert.assertEquals(7000, getConfig().getConnectionRequestTimeout());
    }

    @Test
    public void customizeReadTimeout() throws IOException {
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor();
        executor.setReadTimeout(10000);
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        Assert.assertEquals(10000, getConfig().getSocketTimeout());
    }

    @Test
    public void defaultSettingsOfHttpClientMergedOnExecutorCustomization() throws IOException {
        RequestConfig defaultConfig = RequestConfig.custom().setConnectTimeout(1234).build();
        CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class, Mockito.withSettings().extraInterfaces(Configurable.class));
        Configurable configurable = ((Configurable) (client));
        Mockito.when(configurable.getConfig()).thenReturn(defaultConfig);
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor(client);
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        Assert.assertSame("Default client configuration is expected", defaultConfig, getConfig());
        executor.setConnectionRequestTimeout(4567);
        HttpPost httpPost2 = executor.createHttpPost(config);
        Assert.assertNotNull(getConfig());
        Assert.assertEquals(4567, getConfig().getConnectionRequestTimeout());
        // Default connection timeout merged
        Assert.assertEquals(1234, getConfig().getConnectTimeout());
    }

    @Test
    public void localSettingsOverrideClientDefaultSettings() throws Exception {
        RequestConfig defaultConfig = RequestConfig.custom().setConnectTimeout(1234).setConnectionRequestTimeout(6789).build();
        CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class, Mockito.withSettings().extraInterfaces(Configurable.class));
        Configurable configurable = ((Configurable) (client));
        Mockito.when(configurable.getConfig()).thenReturn(defaultConfig);
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor(client);
        executor.setConnectTimeout(5000);
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        RequestConfig requestConfig = httpPost.getConfig();
        Assert.assertEquals(5000, requestConfig.getConnectTimeout());
        Assert.assertEquals(6789, requestConfig.getConnectionRequestTimeout());
        Assert.assertEquals((-1), requestConfig.getSocketTimeout());
    }

    @Test
    public void mergeBasedOnCurrentHttpClient() throws Exception {
        RequestConfig defaultConfig = RequestConfig.custom().setSocketTimeout(1234).build();
        final CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class, Mockito.withSettings().extraInterfaces(Configurable.class));
        Configurable configurable = ((Configurable) (client));
        Mockito.when(configurable.getConfig()).thenReturn(defaultConfig);
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor() {
            @Override
            public HttpClient getHttpClient() {
                return client;
            }
        };
        executor.setReadTimeout(5000);
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        RequestConfig requestConfig = httpPost.getConfig();
        Assert.assertEquals((-1), requestConfig.getConnectTimeout());
        Assert.assertEquals((-1), requestConfig.getConnectionRequestTimeout());
        Assert.assertEquals(5000, requestConfig.getSocketTimeout());
        // Update the Http client so that it returns an updated  config
        RequestConfig updatedDefaultConfig = RequestConfig.custom().setConnectTimeout(1234).build();
        Mockito.when(configurable.getConfig()).thenReturn(updatedDefaultConfig);
        executor.setReadTimeout(7000);
        HttpPost httpPost2 = executor.createHttpPost(config);
        RequestConfig requestConfig2 = httpPost2.getConfig();
        Assert.assertEquals(1234, requestConfig2.getConnectTimeout());
        Assert.assertEquals((-1), requestConfig2.getConnectionRequestTimeout());
        Assert.assertEquals(7000, requestConfig2.getSocketTimeout());
    }

    @Test
    public void ignoreFactorySettings() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsHttpInvokerRequestExecutor executor = new HttpComponentsHttpInvokerRequestExecutor(httpClient) {
            @Override
            protected RequestConfig createRequestConfig(HttpInvokerClientConfiguration config) {
                return null;
            }
        };
        HttpInvokerClientConfiguration config = mockHttpInvokerClientConfiguration("http://fake-service");
        HttpPost httpPost = executor.createHttpPost(config);
        Assert.assertNull("custom request config should not be set", getConfig());
    }
}

