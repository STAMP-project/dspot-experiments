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
package org.springframework.http.client;


import HttpClientContext.REQUEST_CONFIG;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.PATCH;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpMethod.TRACE;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
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
public class HttpComponentsClientHttpRequestFactoryTests extends AbstractHttpRequestFactoryTestCase {
    @Override
    @Test
    public void httpMethods() throws Exception {
        super.httpMethods();
        assertHttpMethod("patch", PATCH);
    }

    @Test
    public void assertCustomConfig() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory hrf = new HttpComponentsClientHttpRequestFactory(httpClient);
        hrf.setConnectTimeout(1234);
        hrf.setConnectionRequestTimeout(4321);
        hrf.setReadTimeout(4567);
        URI uri = new URI(((baseUrl) + "/status/ok"));
        HttpComponentsClientHttpRequest request = ((HttpComponentsClientHttpRequest) (hrf.createRequest(uri, GET)));
        Object config = request.getHttpContext().getAttribute(REQUEST_CONFIG);
        Assert.assertNotNull("Request config should be set", config);
        Assert.assertTrue(("Wrong request config type" + (config.getClass().getName())), RequestConfig.class.isInstance(config));
        RequestConfig requestConfig = ((RequestConfig) (config));
        Assert.assertEquals("Wrong custom connection timeout", 1234, requestConfig.getConnectTimeout());
        Assert.assertEquals("Wrong custom connection request timeout", 4321, requestConfig.getConnectionRequestTimeout());
        Assert.assertEquals("Wrong custom socket timeout", 4567, requestConfig.getSocketTimeout());
    }

    @Test
    public void defaultSettingsOfHttpClientMergedOnExecutorCustomization() throws Exception {
        RequestConfig defaultConfig = RequestConfig.custom().setConnectTimeout(1234).build();
        CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class, Mockito.withSettings().extraInterfaces(Configurable.class));
        Configurable configurable = ((Configurable) (client));
        Mockito.when(configurable.getConfig()).thenReturn(defaultConfig);
        HttpComponentsClientHttpRequestFactory hrf = new HttpComponentsClientHttpRequestFactory(client);
        Assert.assertSame("Default client configuration is expected", defaultConfig, retrieveRequestConfig(hrf));
        hrf.setConnectionRequestTimeout(4567);
        RequestConfig requestConfig = retrieveRequestConfig(hrf);
        Assert.assertNotNull(requestConfig);
        Assert.assertEquals(4567, requestConfig.getConnectionRequestTimeout());
        // Default connection timeout merged
        Assert.assertEquals(1234, requestConfig.getConnectTimeout());
    }

    @Test
    public void localSettingsOverrideClientDefaultSettings() throws Exception {
        RequestConfig defaultConfig = RequestConfig.custom().setConnectTimeout(1234).setConnectionRequestTimeout(6789).build();
        CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class, Mockito.withSettings().extraInterfaces(Configurable.class));
        Configurable configurable = ((Configurable) (client));
        Mockito.when(configurable.getConfig()).thenReturn(defaultConfig);
        HttpComponentsClientHttpRequestFactory hrf = new HttpComponentsClientHttpRequestFactory(client);
        hrf.setConnectTimeout(5000);
        RequestConfig requestConfig = retrieveRequestConfig(hrf);
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
        HttpComponentsClientHttpRequestFactory hrf = new HttpComponentsClientHttpRequestFactory() {
            @Override
            public HttpClient getHttpClient() {
                return client;
            }
        };
        hrf.setReadTimeout(5000);
        RequestConfig requestConfig = retrieveRequestConfig(hrf);
        Assert.assertEquals((-1), requestConfig.getConnectTimeout());
        Assert.assertEquals((-1), requestConfig.getConnectionRequestTimeout());
        Assert.assertEquals(5000, requestConfig.getSocketTimeout());
        // Update the Http client so that it returns an updated  config
        RequestConfig updatedDefaultConfig = RequestConfig.custom().setConnectTimeout(1234).build();
        Mockito.when(configurable.getConfig()).thenReturn(updatedDefaultConfig);
        hrf.setReadTimeout(7000);
        RequestConfig requestConfig2 = retrieveRequestConfig(hrf);
        Assert.assertEquals(1234, requestConfig2.getConnectTimeout());
        Assert.assertEquals((-1), requestConfig2.getConnectionRequestTimeout());
        Assert.assertEquals(7000, requestConfig2.getSocketTimeout());
    }

    @Test
    public void createHttpUriRequest() throws Exception {
        URI uri = new URI("http://example.com");
        testRequestBodyAllowed(uri, GET, false);
        testRequestBodyAllowed(uri, HEAD, false);
        testRequestBodyAllowed(uri, OPTIONS, false);
        testRequestBodyAllowed(uri, TRACE, false);
        testRequestBodyAllowed(uri, PUT, true);
        testRequestBodyAllowed(uri, POST, true);
        testRequestBodyAllowed(uri, PATCH, true);
        testRequestBodyAllowed(uri, DELETE, true);
    }
}

