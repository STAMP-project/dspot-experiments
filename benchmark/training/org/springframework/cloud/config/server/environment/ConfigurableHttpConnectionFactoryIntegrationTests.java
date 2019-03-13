/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.config.server.environment;


import WebApplicationType.NONE;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.cloud.config.server.proxy.ProxyHostProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static java.net.Proxy.Type.HTTP;


/**
 *
 *
 * @author Dylan Roberts
 */
public class ConfigurableHttpConnectionFactoryIntegrationTests {
    private static final ProxyHostProperties AUTHENTICATED_HTTP_PROXY = new ProxyHostProperties();

    private static final ProxyHostProperties AUTHENTICATED_HTTPS_PROXY = new ProxyHostProperties();

    private static final ProxyHostProperties HTTP_PROXY = new ProxyHostProperties();

    private static final ProxyHostProperties HTTPS_PROXY = new ProxyHostProperties();

    static {
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTP_PROXY.setHost("http://authenticated.http.proxy");
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTP_PROXY.setPort(8080);
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTP_PROXY.setUsername("username");
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTP_PROXY.setPassword("password");
    }

    static {
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTPS_PROXY.setHost("http://authenticated.https.proxy");
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTPS_PROXY.setPort(8081);
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTPS_PROXY.setUsername("username2");
        ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTPS_PROXY.setPassword("password2");
    }

    static {
        ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.setHost("http://http.proxy");
        ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.setPort(8080);
    }

    static {
        ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY.setHost("http://https.proxy");
        ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY.setPort(8081);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void authenticatedHttpsProxy() throws Exception {
        String repoUrl = "https://myrepo/repo.git";
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties(repoUrl, null, ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTPS_PROXY)).run();
        HttpClient httpClient = getHttpClientForUrl(repoUrl);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTPS_PROXY.getHost()))));
        makeRequest(httpClient, "https://somehost");
    }

    @Test
    public void httpsProxy() throws Exception {
        String repoUrl = "https://myrepo/repo.git";
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties(repoUrl, null, ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY)).run();
        HttpClient httpClient = getHttpClientForUrl(repoUrl);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY.getHost()))));
        makeRequest(httpClient, "https://somehost");
    }

    @Test
    public void httpsProxy_placeholderUrl() throws Exception {
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties("https://myrepo/{placeholder1}/{placeholder2}-repo.git", null, ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY)).run();
        HttpClient httpClient = getHttpClientForUrl("https://myrepo/someplaceholdervalue/anotherplaceholdervalue-repo.git");
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY.getHost()))));
        makeRequest(httpClient, "https://somehost");
    }

    @Test
    public void httpsProxy_notCalled() throws Exception {
        String repoUrl = "https://myrepo/repo.git";
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties(repoUrl, null, ConfigurableHttpConnectionFactoryIntegrationTests.HTTPS_PROXY)).run();
        HttpClient httpClient = getHttpClientForUrl(repoUrl);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString("somehost"))));
        makeRequest(httpClient, "http://somehost");
    }

    @Test
    public void authenticatedHttpProxy() throws Exception {
        String repoUrl = "https://myrepo/repo.git";
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties(repoUrl, ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTP_PROXY, null)).run();
        HttpClient httpClient = getHttpClientForUrl(repoUrl);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.AUTHENTICATED_HTTP_PROXY.getHost()))));
        makeRequest(httpClient, "http://somehost");
    }

    @Test
    public void httpProxy() throws Exception {
        String repoUrl = "https://myrepo/repo.git";
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties(repoUrl, ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY, null)).run();
        HttpClient httpClient = getHttpClientForUrl(repoUrl);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.getHost()))));
        makeRequest(httpClient, "http://somehost");
    }

    @Test
    public void httpProxy_placeholderUrl() throws Exception {
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties("https://myrepo/{placeholder}-repo.git", ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY, null)).run();
        HttpClient httpClient = getHttpClientForUrl("https://myrepo/someplaceholdervalue-repo.git");
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.getHost()))));
        makeRequest(httpClient, "http://somehost");
    }

    @Test
    public void httpProxy_notCalled() throws Exception {
        String repoUrl = "https://myrepo/repo.git";
        new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(gitProperties(repoUrl, ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY, null)).run();
        HttpClient httpClient = getHttpClientForUrl(repoUrl);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString("somehost"))));
        makeRequest(httpClient, "https://somehost");
    }

    @Test
    public void httpProxy_fromSystemProperty() throws Exception {
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        try {
            ProxySelector.setDefault(new ProxySelector() {
                @Override
                public List<Proxy> select(URI uri) {
                    InetSocketAddress address = new InetSocketAddress(ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.getHost(), ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.getPort());
                    Proxy proxy = new Proxy(HTTP, address);
                    return Collections.singletonList(proxy);
                }

                @Override
                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                }
            });
            String repoUrl = "https://myrepo/repo.git";
            new SpringApplicationBuilder(ConfigurableHttpConnectionFactoryIntegrationTests.TestConfiguration.class).web(NONE).properties(new String[]{ "spring.cloud.config.server.git.uri=" + repoUrl }).run();
            HttpClient httpClient = getHttpClientForUrl(repoUrl);
            this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(ConfigurableHttpConnectionFactoryIntegrationTests.HTTP_PROXY.getHost()))));
            makeRequest(httpClient, "http://somehost");
        } finally {
            ProxySelector.setDefault(defaultProxySelector);
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigServerProperties.class)
    @Import({ PropertyPlaceholderAutoConfiguration.class, EnvironmentRepositoryConfiguration.class })
    protected static class TestConfiguration {}
}

