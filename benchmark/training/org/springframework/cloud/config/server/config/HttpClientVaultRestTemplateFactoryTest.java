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
package org.springframework.cloud.config.server.config;


import java.net.UnknownHostException;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.cloud.config.server.environment.HttpClientVaultRestTemplateFactory;
import org.springframework.cloud.config.server.environment.VaultEnvironmentProperties;
import org.springframework.cloud.config.server.proxy.ProxyHostProperties;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Dylan Roberts
 */
public class HttpClientVaultRestTemplateFactoryTest {
    private static final ProxyHostProperties AUTHENTICATED_HTTP_PROXY = new ProxyHostProperties();

    private static final ProxyHostProperties AUTHENTICATED_HTTPS_PROXY = new ProxyHostProperties();

    private static final ProxyHostProperties HTTP_PROXY = new ProxyHostProperties();

    private static final ProxyHostProperties HTTPS_PROXY = new ProxyHostProperties();

    static {
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTP_PROXY.setHost("http://authenticated.http.proxy");
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTP_PROXY.setPort(8080);
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTP_PROXY.setUsername("username");
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTP_PROXY.setPassword("password");
    }

    static {
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTPS_PROXY.setHost("http://authenticated.https.proxy");
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTPS_PROXY.setPort(8081);
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTPS_PROXY.setUsername("username2");
        HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTPS_PROXY.setPassword("password2");
    }

    static {
        HttpClientVaultRestTemplateFactoryTest.HTTP_PROXY.setHost("http://http.proxy");
        HttpClientVaultRestTemplateFactoryTest.HTTP_PROXY.setPort(8080);
    }

    static {
        HttpClientVaultRestTemplateFactoryTest.HTTPS_PROXY.setHost("http://https.proxy");
        HttpClientVaultRestTemplateFactoryTest.HTTPS_PROXY.setPort(8081);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HttpClientVaultRestTemplateFactory factory;

    @Test
    public void authenticatedHttpsProxy() throws Exception {
        VaultEnvironmentProperties properties = getVaultEnvironmentProperties(null, HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTPS_PROXY);
        RestTemplate restTemplate = this.factory.build(properties);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTPS_PROXY.getHost()))));
        restTemplate.getForObject("https://somehost", String.class);
    }

    @Test
    public void httpsProxy() throws Exception {
        VaultEnvironmentProperties properties = getVaultEnvironmentProperties(null, HttpClientVaultRestTemplateFactoryTest.HTTPS_PROXY);
        RestTemplate restTemplate = this.factory.build(properties);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(HttpClientVaultRestTemplateFactoryTest.HTTPS_PROXY.getHost()))));
        restTemplate.getForObject("https://somehost", String.class);
    }

    @Test
    public void httpsProxy_notCalled() throws Exception {
        VaultEnvironmentProperties properties = getVaultEnvironmentProperties(null, HttpClientVaultRestTemplateFactoryTest.HTTPS_PROXY);
        RestTemplate restTemplate = this.factory.build(properties);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString("somehost"))));
        restTemplate.getForObject("http://somehost", String.class);
    }

    @Test
    public void authenticatedHttpProxy() throws Exception {
        VaultEnvironmentProperties properties = getVaultEnvironmentProperties(HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTP_PROXY, null);
        RestTemplate restTemplate = this.factory.build(properties);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(HttpClientVaultRestTemplateFactoryTest.AUTHENTICATED_HTTP_PROXY.getHost()))));
        restTemplate.getForObject("http://somehost", String.class);
    }

    @Test
    public void httpProxy() throws Exception {
        VaultEnvironmentProperties properties = getVaultEnvironmentProperties(HttpClientVaultRestTemplateFactoryTest.HTTP_PROXY, null);
        RestTemplate restTemplate = this.factory.build(properties);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString(HttpClientVaultRestTemplateFactoryTest.HTTP_PROXY.getHost()))));
        restTemplate.getForObject("http://somehost", String.class);
    }

    @Test
    public void httpProxy_notCalled() throws Exception {
        VaultEnvironmentProperties properties = getVaultEnvironmentProperties(HttpClientVaultRestTemplateFactoryTest.HTTP_PROXY, null);
        RestTemplate restTemplate = this.factory.build(properties);
        this.expectedException.expectCause(Matchers.allOf(Matchers.instanceOf(UnknownHostException.class), Matchers.hasProperty("message", Matchers.containsString("somehost"))));
        restTemplate.getForObject("https://somehost", String.class);
    }
}

