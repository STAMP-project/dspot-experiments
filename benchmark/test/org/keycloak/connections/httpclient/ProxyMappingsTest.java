/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.connections.httpclient;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpHost;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link ProxyMappings}.
 *
 * @author <a href="mailto:thomas.darimont@gmail.com">Thomas Darimont</a>
 */
public class ProxyMappingsTest {
    private static final List<String> DEFAULT_MAPPINGS = // 
    // 
    // 
    Arrays.asList(".*\\.(google|googleapis)\\.com;http://proxy1:8080", ".*\\.facebook\\.com;http://proxy2:8080");

    private static final List<String> MAPPINGS_WITH_FALLBACK = new ArrayList<>();

    private static final List<String> MAPPINGS_WITH_FALLBACK_AND_PROXY_EXCEPTION = new ArrayList<>();

    static {
        ProxyMappingsTest.MAPPINGS_WITH_FALLBACK.addAll(ProxyMappingsTest.DEFAULT_MAPPINGS);
        ProxyMappingsTest.MAPPINGS_WITH_FALLBACK.add(".*;http://fallback:8080");
    }

    static {
        ProxyMappingsTest.MAPPINGS_WITH_FALLBACK_AND_PROXY_EXCEPTION.addAll(ProxyMappingsTest.DEFAULT_MAPPINGS);
        ProxyMappingsTest.MAPPINGS_WITH_FALLBACK_AND_PROXY_EXCEPTION.add(".*\\.acme\\.corp\\.com;NO_PROXY");
        ProxyMappingsTest.MAPPINGS_WITH_FALLBACK_AND_PROXY_EXCEPTION.add(".*;http://fallback:8080");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    ProxyMappings proxyMappings;

    @Test
    public void proxyMappingFromEmptyListShouldBeEmpty() {
        Assert.assertThat(new ProxyMappings(new ArrayList()).isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldReturnProxy1ForConfiguredProxyMapping() {
        HttpHost proxy = proxyMappings.getProxyFor("account.google.com");
        Assert.assertThat(proxy, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(proxy.getHostName(), CoreMatchers.is("proxy1"));
    }

    @Test
    public void shouldReturnProxy1ForConfiguredProxyMappingAlternative() {
        HttpHost proxy = proxyMappings.getProxyFor("www.googleapis.com");
        Assert.assertThat(proxy, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(proxy.getHostName(), CoreMatchers.is("proxy1"));
    }

    @Test
    public void shouldReturnProxy1ForConfiguredProxyMappingWithSubDomain() {
        HttpHost proxy = proxyMappings.getProxyFor("awesome.account.google.com");
        Assert.assertThat(proxy, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(proxy.getHostName(), CoreMatchers.is("proxy1"));
    }

    @Test
    public void shouldReturnProxy2ForConfiguredProxyMapping() {
        HttpHost proxy = proxyMappings.getProxyFor("login.facebook.com");
        Assert.assertThat(proxy, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(proxy.getHostName(), CoreMatchers.is("proxy2"));
    }

    @Test
    public void shouldReturnNoProxyForUnknownHost() {
        HttpHost proxy = proxyMappings.getProxyFor("login.microsoft.com");
        Assert.assertThat(proxy, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldRejectNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("hostname");
        proxyMappings.getProxyFor(null);
    }

    @Test
    public void shouldReturnFallbackForNotExplicitlyMappedHostname() {
        ProxyMappings proxyMappingsWithFallback = ProxyMappings.valueOf(ProxyMappingsTest.MAPPINGS_WITH_FALLBACK);
        HttpHost proxy = proxyMappingsWithFallback.getProxyFor("login.salesforce.com");
        Assert.assertThat(proxy.getHostName(), CoreMatchers.is("fallback"));
    }

    @Test
    public void shouldReturnCorrectProxyOrFallback() {
        ProxyMappings proxyMappingsWithFallback = ProxyMappings.valueOf(ProxyMappingsTest.MAPPINGS_WITH_FALLBACK);
        HttpHost forGoogle = proxyMappingsWithFallback.getProxyFor("login.google.com");
        Assert.assertThat(forGoogle.getHostName(), CoreMatchers.is("proxy1"));
        HttpHost forFacebook = proxyMappingsWithFallback.getProxyFor("login.facebook.com");
        Assert.assertThat(forFacebook.getHostName(), CoreMatchers.is("proxy2"));
        HttpHost forMicrosoft = proxyMappingsWithFallback.getProxyFor("login.microsoft.com");
        Assert.assertThat(forMicrosoft.getHostName(), CoreMatchers.is("fallback"));
        HttpHost forSalesForce = proxyMappingsWithFallback.getProxyFor("login.salesforce.com");
        Assert.assertThat(forSalesForce.getHostName(), CoreMatchers.is("fallback"));
    }

    @Test
    public void shouldReturnFallbackForNotExplicitlyMappedHostnameAndHonorProxyExceptions() {
        ProxyMappings proxyMappingsWithFallbackAndProxyException = ProxyMappings.valueOf(ProxyMappingsTest.MAPPINGS_WITH_FALLBACK_AND_PROXY_EXCEPTION);
        HttpHost forGoogle = proxyMappingsWithFallbackAndProxyException.getProxyFor("login.google.com");
        Assert.assertThat(forGoogle.getHostName(), CoreMatchers.is("proxy1"));
        HttpHost forFacebook = proxyMappingsWithFallbackAndProxyException.getProxyFor("login.facebook.com");
        Assert.assertThat(forFacebook.getHostName(), CoreMatchers.is("proxy2"));
        HttpHost forAcmeCorp = proxyMappingsWithFallbackAndProxyException.getProxyFor("myapp.acme.corp.com");
        Assert.assertThat(forAcmeCorp, CoreMatchers.is(CoreMatchers.nullValue()));
        HttpHost forMicrosoft = proxyMappingsWithFallbackAndProxyException.getProxyFor("login.microsoft.com");
        Assert.assertThat(forMicrosoft.getHostName(), CoreMatchers.is("fallback"));
        HttpHost forSalesForce = proxyMappingsWithFallbackAndProxyException.getProxyFor("login.salesforce.com");
        Assert.assertThat(forSalesForce.getHostName(), CoreMatchers.is("fallback"));
    }
}

