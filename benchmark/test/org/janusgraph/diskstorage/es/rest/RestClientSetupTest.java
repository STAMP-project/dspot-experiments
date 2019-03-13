/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.es.rest;


import ElasticSearchIndex.BULK_REFRESH;
import ElasticSearchIndex.ES_SCROLL_KEEP_ALIVE;
import ElasticSearchIndex.HOST_PORT_DEFAULT;
import HttpAuthTypes.CUSTOM;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.janusgraph.diskstorage.es.rest.util.RestClientAuthenticator;
import org.janusgraph.diskstorage.es.rest.util.SSLConfigurationCallback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class RestClientSetupTest {
    private static final String INDEX_NAME = "junit";

    private static final String SCHEME_HTTP = "http";

    private static final String SCHEME_HTTPS = "https";

    private static final String ES_HOST_01 = "es-host-01";

    private static final String ES_HOST_02 = "es-host-02";

    private static final int ES_PORT = 8080;

    private static final int ES_SCROLL_KA = (ES_SCROLL_KEEP_ALIVE.getDefaultValue()) * 2;

    private static final String ES_BULK_REFRESH = String.valueOf((!(Boolean.valueOf(BULK_REFRESH.getDefaultValue()))));

    private static final AtomicInteger instanceCount = new AtomicInteger();

    @Captor
    ArgumentCaptor<HttpHost[]> hostListCaptor;

    @Captor
    ArgumentCaptor<Integer> scrollKACaptor;

    @Spy
    private RestClientSetup restClientSetup = new RestClientSetup();

    @Mock
    private RestClient restClientMock;

    @Mock
    private RestElasticSearchClient restElasticSearchClientMock;

    @Mock
    private SSLContext sslContextMock;

    @Mock
    private RestClientBuilder restClientBuilderMock;

    @Test
    public void testConnectBasicHttpConfigurationSingleHost() throws Exception {
        final List<HttpHost[]> hostsConfigured = baseHostsConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".hostname"), RestClientSetupTest.ES_HOST_01).build());
        Assertions.assertNotNull(hostsConfigured);
        Assertions.assertEquals(1, hostsConfigured.size());
        final HttpHost host0 = hostsConfigured.get(0)[0];
        Assertions.assertEquals(RestClientSetupTest.ES_HOST_01, host0.getHostName());
        Assertions.assertEquals(RestClientSetupTest.SCHEME_HTTP, host0.getSchemeName());
        Assertions.assertEquals(HOST_PORT_DEFAULT, host0.getPort());
        Mockito.verify(restClientSetup).getElasticSearchClient(ArgumentMatchers.same(restClientMock), scrollKACaptor.capture());
        Assertions.assertEquals(ES_SCROLL_KEEP_ALIVE.getDefaultValue().intValue(), scrollKACaptor.getValue().intValue());
        Mockito.verify(restElasticSearchClientMock, Mockito.never()).setBulkRefresh(ArgumentMatchers.anyString());
    }

    @Test
    public void testConnectBasicHttpConfigurationMultiHost() throws Exception {
        final List<HttpHost[]> hostsConfigured = baseHostsConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".hostname"), (((RestClientSetupTest.ES_HOST_01) + ",") + (RestClientSetupTest.ES_HOST_02))).build());
        Assertions.assertNotNull(hostsConfigured);
        Assertions.assertEquals(1, hostsConfigured.size());
        final HttpHost host0 = hostsConfigured.get(0)[0];
        Assertions.assertEquals(RestClientSetupTest.ES_HOST_01, host0.getHostName());
        Assertions.assertEquals(RestClientSetupTest.SCHEME_HTTP, host0.getSchemeName());
        Assertions.assertEquals(HOST_PORT_DEFAULT, host0.getPort());
        final HttpHost host1 = hostsConfigured.get(0)[1];
        Assertions.assertEquals(RestClientSetupTest.ES_HOST_02, host1.getHostName());
        Assertions.assertEquals(RestClientSetupTest.SCHEME_HTTP, host1.getSchemeName());
        Assertions.assertEquals(HOST_PORT_DEFAULT, host1.getPort());
        Mockito.verify(restClientSetup).getElasticSearchClient(ArgumentMatchers.same(restClientMock), scrollKACaptor.capture());
        Assertions.assertEquals(ES_SCROLL_KEEP_ALIVE.getDefaultValue().intValue(), scrollKACaptor.getValue().intValue());
        Mockito.verify(restElasticSearchClientMock, Mockito.never()).setBulkRefresh(ArgumentMatchers.anyString());
    }

    @Test
    public void testConnectBasicHttpConfigurationAllOptions() throws Exception {
        final List<HttpHost[]> hostsConfigured = baseHostsConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".hostname"), RestClientSetupTest.ES_HOST_01).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".port"), String.valueOf(RestClientSetupTest.ES_PORT)).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.scroll-keep-alive"), String.valueOf(RestClientSetupTest.ES_SCROLL_KA)).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.bulk-refresh"), String.valueOf(RestClientSetupTest.ES_BULK_REFRESH)).build());
        Assertions.assertNotNull(hostsConfigured);
        Assertions.assertEquals(1, hostsConfigured.size());
        HttpHost host0 = hostsConfigured.get(0)[0];
        Assertions.assertEquals(RestClientSetupTest.ES_HOST_01, host0.getHostName());
        Assertions.assertEquals(RestClientSetupTest.SCHEME_HTTP, host0.getSchemeName());
        Assertions.assertEquals(RestClientSetupTest.ES_PORT, host0.getPort());
        Mockito.verify(restClientSetup).getElasticSearchClient(ArgumentMatchers.same(restClientMock), scrollKACaptor.capture());
        Assertions.assertEquals(RestClientSetupTest.ES_SCROLL_KA, scrollKACaptor.getValue().intValue());
        Mockito.verify(restElasticSearchClientMock).setBulkRefresh(ArgumentMatchers.eq(RestClientSetupTest.ES_BULK_REFRESH));
    }

    @Test
    public void testConnectBasicHttpsConfigurationSingleHost() throws Exception {
        final List<HttpHost[]> hostsConfigured = baseHostsConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".hostname"), RestClientSetupTest.ES_HOST_01).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.enabled"), "true").build());
        Assertions.assertNotNull(hostsConfigured);
        Assertions.assertEquals(1, hostsConfigured.size());
        HttpHost host0 = hostsConfigured.get(0)[0];
        Assertions.assertEquals(RestClientSetupTest.ES_HOST_01, host0.getHostName());
        Assertions.assertEquals(RestClientSetupTest.SCHEME_HTTPS, host0.getSchemeName());
        Assertions.assertEquals(HOST_PORT_DEFAULT, host0.getPort());
        Mockito.verify(restClientSetup).getElasticSearchClient(ArgumentMatchers.same(restClientMock), scrollKACaptor.capture());
        Assertions.assertEquals(ES_SCROLL_KEEP_ALIVE.getDefaultValue().intValue(), scrollKACaptor.getValue().intValue());
        Mockito.verify(restElasticSearchClientMock, Mockito.never()).setBulkRefresh(ArgumentMatchers.anyString());
    }

    @Test
    public void testHttpBasicAuthConfiguration() throws Exception {
        // testing that the appropriate values are passed to the client builder via credentials provider
        final String testRealm = "testRealm";
        final String testUser = "testUser";
        final String testPassword = "testPassword";
        final CredentialsProvider cp = basicAuthTestBase(ImmutableMap.<String, String>builder().build(), testRealm, testUser, testPassword);
        final Credentials credentials = cp.getCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, testRealm));
        Assertions.assertNotNull(credentials);
        Assertions.assertEquals(testUser, credentials.getUserPrincipal().getName());
        Assertions.assertEquals(testPassword, credentials.getPassword());
    }

    @Test
    public void testCustomAuthenticator() throws Exception {
        final String uniqueInstanceKey = String.valueOf(RestClientSetupTest.instanceCount.getAndIncrement());
        final String[] customAuthArgs = new String[]{ uniqueInstanceKey, "arg1", "arg2" };
        final String serializedArgList = StringUtils.join(customAuthArgs, ',');
        final HttpClientConfigCallback hccc = authTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.interface"), "REST_CLIENT").put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.http.auth.type"), CUSTOM.toString()).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.http.auth.custom.authenticator-class"), RestClientSetupTest.TestCustomAuthenticator.class.getName()).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.http.auth.custom.authenticator-args"), serializedArgList).build());
        Mockito.verify(restClientSetup).getCustomAuthenticator(ArgumentMatchers.eq(RestClientSetupTest.TestCustomAuthenticator.class.getName()), ArgumentMatchers.eq(customAuthArgs));
        RestClientSetupTest.TestCustomAuthenticator customAuth = RestClientSetupTest.TestCustomAuthenticator.instanceMap.get(uniqueInstanceKey);
        Assertions.assertNotNull(customAuth);
        // authenticator has been instantiated, verifying it has been called
        Assertions.assertEquals(1, customAuth.numInitCalls);
        // verifying that the custom callback is in the chain
        final HttpAsyncClientBuilder hacb = Mockito.mock(HttpAsyncClientBuilder.class);
        hccc.customizeHttpClient(hacb);
        Assertions.assertEquals(1, customAuth.customizeHttpClientHistory.size());
        Assertions.assertSame(hacb, customAuth.customizeHttpClientHistory.get(0));
        Assertions.assertArrayEquals(customAuthArgs, customAuth.args);
    }

    @Test
    public void testSSLTrustStoreSettingsOnly() throws Exception {
        final String trustStoreFile = "/a/b/c/truststore.jks";
        final String trustStorePassword = "averysecretpassword";
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.truststore.location"), trustStoreFile).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.truststore.password"), trustStorePassword).build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).withTrustStore(ArgumentMatchers.eq(trustStoreFile), ArgumentMatchers.eq(trustStorePassword));
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLKeyStoreSettingsOnly() throws Exception {
        final String keyStoreFile = "/a/b/c/keystore.jks";
        final String keyStorePassword = "key_store_password";
        final String keyPassword = "key_password";
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.location"), keyStoreFile).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.storepassword"), keyStorePassword).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.keypassword"), keyPassword).build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).withKeyStore(ArgumentMatchers.eq(keyStoreFile), ArgumentMatchers.eq(keyStorePassword), ArgumentMatchers.eq(keyPassword));
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLKeyStoreSettingsOnlyEmptyKeyPass() throws Exception {
        final String keyStoreFile = "/a/b/c/keystore.jks";
        final String keyStorePassword = "key_store_password";
        final String keyPassword = "";
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.location"), keyStoreFile).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.storepassword"), keyStorePassword).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.keypassword"), keyPassword).build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).withKeyStore(ArgumentMatchers.eq(keyStoreFile), ArgumentMatchers.eq(keyStorePassword), ArgumentMatchers.eq(keyPassword));
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLKeyStoreSettingsOnlyNoKeyPass() throws Exception {
        final String keyStoreFile = "/a/b/c/keystore.jks";
        final String keyStorePassword = "key_store_password";
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.location"), keyStoreFile).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.storepassword"), keyStorePassword).build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).withKeyStore(ArgumentMatchers.eq(keyStoreFile), ArgumentMatchers.eq(keyStorePassword), ArgumentMatchers.eq(keyStorePassword));
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLKeyAndTrustStoreSettingsOnly() throws Exception {
        final String trustStoreFile = "/a/b/c/truststore.jks";
        final String trustStorePassword = "averysecretpassword";
        final String keyStoreFile = "/a/b/c/keystore.jks";
        final String keyStorePassword = "key_store_password";
        final String keyPassword = "key_password";
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.truststore.location"), trustStoreFile).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.truststore.password"), trustStorePassword).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.location"), keyStoreFile).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.storepassword"), keyStorePassword).put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.keystore.keypassword"), keyPassword).build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).withTrustStore(ArgumentMatchers.eq(trustStoreFile), ArgumentMatchers.eq(trustStorePassword));
        Mockito.verify(sslConfBuilderMock).withKeyStore(ArgumentMatchers.eq(keyStoreFile), ArgumentMatchers.eq(keyStorePassword), ArgumentMatchers.eq(keyPassword));
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLDisableHostNameVerifier() throws Exception {
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.disable-hostname-verification"), "true").build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).disableHostNameVerification();
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLDisableHostNameVerifierExplicitOff() throws Exception {
        final SSLConfigurationCallback.Builder sslConfBuilderMock = Mockito.mock(Builder.class);
        Mockito.doReturn(sslConfBuilderMock).when(restClientSetup).getSSLConfigurationCallbackBuilder();
        baseConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.enabled"), "true").put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.disable-hostname-verification"), "false").build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock, Mockito.never()).disableHostNameVerification();
    }

    @Test
    public void testSSLDisableHostNameVerifierDefaultOff() throws Exception {
        final SSLConfigurationCallback.Builder sslConfBuilderMock = Mockito.mock(Builder.class);
        Mockito.doReturn(sslConfBuilderMock).when(restClientSetup).getSSLConfigurationCallbackBuilder();
        baseConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.enabled"), "true").build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock, Mockito.never()).disableHostNameVerification();
    }

    @Test
    public void testSSLAllowSelfSignedCerts() throws Exception {
        final SSLConfigurationCallback.Builder sslConfBuilderMock = sslSettingsTestBase(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.allow-self-signed-certificates"), "true").build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock).allowSelfSignedCertificates();
        Mockito.verify(sslConfBuilderMock).build();
        Mockito.verifyNoMoreInteractions(sslConfBuilderMock);
    }

    @Test
    public void testSSLAllowSelfSignedCertsExplicitOff() throws Exception {
        final SSLConfigurationCallback.Builder sslConfBuilderMock = Mockito.mock(Builder.class);
        Mockito.doReturn(sslConfBuilderMock).when(restClientSetup).getSSLConfigurationCallbackBuilder();
        baseConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.enabled"), "true").put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.allow-self-signed-certificates"), "false").build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock, Mockito.never()).allowSelfSignedCertificates();
    }

    @Test
    public void testSSLAllowSelfSignedCertsDefaultOff() throws Exception {
        final SSLConfigurationCallback.Builder sslConfBuilderMock = Mockito.mock(Builder.class);
        Mockito.doReturn(sslConfBuilderMock).when(restClientSetup).getSSLConfigurationCallbackBuilder();
        baseConfigTest(ImmutableMap.<String, String>builder().put((("index." + (RestClientSetupTest.INDEX_NAME)) + ".elasticsearch.ssl.enabled"), "true").build());
        Mockito.verify(restClientSetup).getSSLConfigurationCallbackBuilder();
        Mockito.verify(sslConfBuilderMock, Mockito.never()).allowSelfSignedCertificates();
    }

    public static class TestCustomAuthenticator implements RestClientAuthenticator {
        private static final Map<String, RestClientSetupTest.TestCustomAuthenticator> instanceMap = new HashMap<>();

        private final String[] args;

        private final List<Builder> customizeRequestConfigHistory = new LinkedList<>();

        private final List<HttpAsyncClientBuilder> customizeHttpClientHistory = new LinkedList<>();

        private int numInitCalls = 0;

        public TestCustomAuthenticator(String[] args) {
            this.args = args;
            Preconditions.checkArgument(((RestClientSetupTest.TestCustomAuthenticator.instanceMap.put(args[0], this)) == null), "Non-unique key used");
        }

        @Override
        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
            customizeHttpClientHistory.add(httpClientBuilder);
            return httpClientBuilder;
        }

        @Override
        public Builder customizeRequestConfig(Builder requestConfigBuilder) {
            customizeRequestConfigHistory.add(requestConfigBuilder);
            return requestConfigBuilder;
        }

        @Override
        public void init() throws IOException {
            (numInitCalls)++;
        }
    }
}

