/**
 * Copyright (C) 2014 Square, Inc.
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
package okhttp3;


import OkHttpClient.Builder;
import Protocol.H2_PRIOR_KNOWLEDGE;
import Protocol.HTTP_1_0;
import Protocol.HTTP_1_1;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.ProxySelector;
import java.net.ResponseCache;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Test;


public final class OkHttpClientTest {
    private static final ProxySelector DEFAULT_PROXY_SELECTOR = ProxySelector.getDefault();

    private static final CookieHandler DEFAULT_COOKIE_HANDLER = CookieManager.getDefault();

    private static final ResponseCache DEFAULT_RESPONSE_CACHE = ResponseCache.getDefault();

    private final MockWebServer server = new MockWebServer();

    @Test
    public void durationDefaults() {
        OkHttpClient client = TestUtil.defaultClient();
        Assert.assertEquals(0, client.callTimeoutMillis());
        Assert.assertEquals(10000, client.connectTimeoutMillis());
        Assert.assertEquals(10000, client.readTimeoutMillis());
        Assert.assertEquals(10000, client.writeTimeoutMillis());
        Assert.assertEquals(0, client.pingIntervalMillis());
    }

    @Test
    public void timeoutValidRange() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.callTimeout(1, TimeUnit.NANOSECONDS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.connectTimeout(1, TimeUnit.NANOSECONDS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.writeTimeout(1, TimeUnit.NANOSECONDS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.readTimeout(1, TimeUnit.NANOSECONDS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.callTimeout(365, TimeUnit.DAYS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.connectTimeout(365, TimeUnit.DAYS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.writeTimeout(365, TimeUnit.DAYS);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            builder.readTimeout(365, TimeUnit.DAYS);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void clonedInterceptorsListsAreIndependent() throws Exception {
        Interceptor interceptor = ( chain) -> chain.proceed(chain.request());
        OkHttpClient original = TestUtil.defaultClient();
        original.newBuilder().addInterceptor(interceptor).addNetworkInterceptor(interceptor).build();
        Assert.assertEquals(0, original.interceptors().size());
        Assert.assertEquals(0, original.networkInterceptors().size());
    }

    /**
     * When copying the client, stateful things like the connection pool are shared across all
     * clients.
     */
    @Test
    public void cloneSharesStatefulInstances() throws Exception {
        OkHttpClient client = TestUtil.defaultClient();
        // Values should be non-null.
        OkHttpClient a = client.newBuilder().build();
        Assert.assertNotNull(a.dispatcher());
        Assert.assertNotNull(a.connectionPool());
        Assert.assertNotNull(a.sslSocketFactory());
        // Multiple clients share the instances.
        OkHttpClient b = client.newBuilder().build();
        Assert.assertSame(a.dispatcher(), b.dispatcher());
        Assert.assertSame(a.connectionPool(), b.connectionPool());
        Assert.assertSame(a.sslSocketFactory(), b.sslSocketFactory());
    }

    @Test
    public void setProtocolsRejectsHttp10() throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.protocols(Arrays.asList(HTTP_1_0, HTTP_1_1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void certificatePinnerEquality() {
        OkHttpClient clientA = TestUtil.defaultClient();
        OkHttpClient clientB = TestUtil.defaultClient();
        Assert.assertEquals(clientA.certificatePinner(), clientB.certificatePinner());
    }

    @Test
    public void nullInterceptor() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.addInterceptor(null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("interceptor == null", expected.getMessage());
        }
    }

    @Test
    public void nullNetworkInterceptor() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.addNetworkInterceptor(null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("interceptor == null", expected.getMessage());
        }
    }

    @Test
    public void nullInterceptorInList() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(null);
        try {
            builder.build();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Null interceptor: [null]", expected.getMessage());
        }
    }

    @Test
    public void nullNetworkInterceptorInList() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.networkInterceptors().add(null);
        try {
            builder.build();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Null network interceptor: [null]", expected.getMessage());
        }
    }

    @Test
    public void testH2PriorKnowledgeOkHttpClientConstructionFallback() {
        try {
            new OkHttpClient.Builder().protocols(Arrays.asList(H2_PRIOR_KNOWLEDGE, HTTP_1_1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals(("protocols containing h2_prior_knowledge cannot use other protocols: " + "[h2_prior_knowledge, http/1.1]"), expected.getMessage());
        }
    }

    @Test
    public void testH2PriorKnowledgeOkHttpClientConstructionDuplicates() {
        try {
            new OkHttpClient.Builder().protocols(Arrays.asList(H2_PRIOR_KNOWLEDGE, H2_PRIOR_KNOWLEDGE));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals(("protocols containing h2_prior_knowledge cannot use other protocols: " + "[h2_prior_knowledge, h2_prior_knowledge]"), expected.getMessage());
        }
    }

    @Test
    public void testH2PriorKnowledgeOkHttpClientConstructionSuccess() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().protocols(Arrays.asList(H2_PRIOR_KNOWLEDGE)).build();
        Assert.assertEquals(1, okHttpClient.protocols().size());
        Assert.assertEquals(H2_PRIOR_KNOWLEDGE, okHttpClient.protocols().get(0));
    }

    @Test
    public void nullDefaultProxySelector() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        ProxySelector.setDefault(null);
        OkHttpClient client = TestUtil.defaultClient().newBuilder().build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("abc", response.body().string());
    }

    @Test
    public void sslSocketFactorySetAsSocketFactory() throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.socketFactory(SSLSocketFactory.getDefault());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

