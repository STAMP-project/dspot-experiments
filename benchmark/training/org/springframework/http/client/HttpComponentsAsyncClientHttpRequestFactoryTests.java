/**
 * Copyright 2002-2015 the original author or authors.
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
import HttpMethod.GET;
import HttpMethod.PATCH;
import java.net.URI;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Stephane Nicoll
 */
@SuppressWarnings("deprecation")
public class HttpComponentsAsyncClientHttpRequestFactoryTests extends AbstractAsyncHttpRequestFactoryTestCase {
    @Override
    @Test
    public void httpMethods() throws Exception {
        super.httpMethods();
        assertHttpMethod("patch", PATCH);
    }

    @Test
    public void customHttpAsyncClientUsesItsDefault() throws Exception {
        HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory();
        URI uri = new URI(((baseUrl) + "/status/ok"));
        HttpComponentsAsyncClientHttpRequest request = ((HttpComponentsAsyncClientHttpRequest) (factory.createAsyncRequest(uri, GET)));
        Assert.assertNull("No custom config should be set with a custom HttpAsyncClient", request.getHttpContext().getAttribute(REQUEST_CONFIG));
    }

    @Test
    public void defaultSettingsOfHttpAsyncClientLostOnExecutorCustomization() throws Exception {
        CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(1234).build()).build();
        HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory(client);
        URI uri = new URI(((baseUrl) + "/status/ok"));
        HttpComponentsAsyncClientHttpRequest request = ((HttpComponentsAsyncClientHttpRequest) (factory.createAsyncRequest(uri, GET)));
        Assert.assertNull("No custom config should be set with a custom HttpClient", request.getHttpContext().getAttribute(REQUEST_CONFIG));
        factory.setConnectionRequestTimeout(4567);
        HttpComponentsAsyncClientHttpRequest request2 = ((HttpComponentsAsyncClientHttpRequest) (factory.createAsyncRequest(uri, GET)));
        Object requestConfigAttribute = request2.getHttpContext().getAttribute(REQUEST_CONFIG);
        Assert.assertNotNull(requestConfigAttribute);
        RequestConfig requestConfig = ((RequestConfig) (requestConfigAttribute));
        Assert.assertEquals(4567, requestConfig.getConnectionRequestTimeout());
        // No way to access the request config of the HTTP client so no way to "merge" our customizations
        Assert.assertEquals((-1), requestConfig.getConnectTimeout());
    }
}

