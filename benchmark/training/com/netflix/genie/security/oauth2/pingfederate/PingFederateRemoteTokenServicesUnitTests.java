/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.security.oauth2.pingfederate;


import HttpMethod.POST;
import HttpStatus.NOT_FOUND;
import PingFederateRemoteTokenServices.CLIENT_ID_KEY;
import PingFederateRemoteTokenServices.ERROR_KEY;
import PingFederateRemoteTokenServices.SCOPE_KEY;
import PingFederateRemoteTokenServices.TOKEN_VALIDATION_ERROR_COUNTER_NAME;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netflix.genie.test.categories.UnitTest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Unit tests for PingFederateRemoteTokenServices.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class PingFederateRemoteTokenServicesUnitTests {
    private static final String CLIENT_ID = UUID.randomUUID().toString();

    private static final String CLIENT_SECRET = UUID.randomUUID().toString();

    private static final String CHECK_TOKEN_ENDPOINT_URL = UUID.randomUUID().toString();

    /**
     * Create a mock server.
     */
    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public WireMockRule wireMockRule = new WireMockRule(Options.DYNAMIC_PORT);

    private ResourceServerProperties resourceServerProperties;

    private MeterRegistry registry;

    private Timer authenticationTimer;

    /**
     * Make sure we can't construct without a client id.
     */
    @Test(expected = IllegalStateException.class)
    public void cantConstructWithoutClientId() {
        final ResourceServerProperties properties = new ResourceServerProperties(null, null);
        final AccessTokenConverter converter = new DefaultAccessTokenConverter();
        new PingFederateRemoteTokenServices(properties, converter, this.registry);
    }

    /**
     * Make sure we can't construct without a client secret.
     */
    @Test(expected = IllegalStateException.class)
    public void cantConstructWithoutClientSecret() {
        final ResourceServerProperties properties = new ResourceServerProperties("AnID", null);
        final AccessTokenConverter converter = new DefaultAccessTokenConverter();
        new PingFederateRemoteTokenServices(properties, converter, this.registry);
    }

    /**
     * Make sure we can't construct without a check token url.
     */
    @Test(expected = IllegalStateException.class)
    public void cantConstructWithoutCheckTokenURL() {
        this.resourceServerProperties.setTokenInfoUri(null);
        final AccessTokenConverter converter = new DefaultAccessTokenConverter();
        new PingFederateRemoteTokenServices(this.resourceServerProperties, converter, this.registry);
    }

    /**
     * Make sure we can validate a token.
     */
    @Test
    public void canLoadAuthentication() {
        final AccessTokenConverter converter = Mockito.mock(AccessTokenConverter.class);
        final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        final PingFederateRemoteTokenServices services = new PingFederateRemoteTokenServices(this.resourceServerProperties, converter, this.registry);
        services.setRestTemplate(restTemplate);
        final String accessToken = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final String scope1 = UUID.randomUUID().toString();
        final String scope2 = UUID.randomUUID().toString();
        final Map<String, Object> map = Maps.newHashMap();
        map.put(CLIENT_ID_KEY, clientId);
        map.put(SCOPE_KEY, ((scope1 + " ") + scope2));
        @SuppressWarnings("unchecked")
        final ResponseEntity<Map> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(Mockito.eq(PingFederateRemoteTokenServicesUnitTests.CHECK_TOKEN_ENDPOINT_URL), Mockito.eq(POST), Mockito.any(HttpEntity.class), Mockito.eq(Map.class))).thenReturn(response);
        Mockito.when(response.getBody()).thenReturn(map);
        final SimpleGrantedAuthority scope1Authority = new SimpleGrantedAuthority(scope1);
        final SimpleGrantedAuthority scope2Authority = new SimpleGrantedAuthority(scope2);
        final Set<GrantedAuthority> authorities = Sets.newHashSet(scope1Authority, scope2Authority);
        final Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(clientId, "NA", authorities);
        final OAuth2Authentication oauth2Authentication = new OAuth2Authentication(Mockito.mock(OAuth2Request.class), authentication);
        Mockito.when(converter.extractAuthentication(Mockito.eq(map))).thenReturn(oauth2Authentication);
        final OAuth2Authentication result = services.loadAuthentication(accessToken);
        Assert.assertThat(result, Matchers.is(oauth2Authentication));
        Assert.assertThat(result.getPrincipal(), Matchers.is(clientId));
        Assert.assertThat(result.getAuthorities().size(), Matchers.is(2));
        Assert.assertTrue(result.getAuthorities().contains(scope1Authority));
        Assert.assertTrue(result.getAuthorities().contains(scope2Authority));
        Mockito.verify(this.authenticationTimer, Mockito.times(1)).record(Mockito.anyLong(), Mockito.eq(TimeUnit.NANOSECONDS));
    }

    /**
     * Make sure invalid response from server causes authentication to fail.
     */
    @Test(expected = InvalidTokenException.class)
    public void cantLoadAuthenticationOnError() {
        final AccessTokenConverter converter = Mockito.mock(AccessTokenConverter.class);
        final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        final PingFederateRemoteTokenServices services = new PingFederateRemoteTokenServices(this.resourceServerProperties, converter, this.registry);
        services.setRestTemplate(restTemplate);
        final String accessToken = UUID.randomUUID().toString();
        final Map<String, Object> map = Maps.newHashMap();
        map.put(ERROR_KEY, UUID.randomUUID().toString());
        @SuppressWarnings("unchecked")
        final ResponseEntity<Map> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(Mockito.eq(PingFederateRemoteTokenServicesUnitTests.CHECK_TOKEN_ENDPOINT_URL), Mockito.eq(POST), Mockito.any(HttpEntity.class), Mockito.eq(Map.class))).thenReturn(response);
        Mockito.when(response.getBody()).thenReturn(map);
        // Should throw exception based on error key being present
        services.loadAuthentication(accessToken);
    }

    /**
     * Make sure rest call failures are handled.
     */
    @Test
    public void cantLoadAuthenticationOnRestError() {
        final String path = "/" + (UUID.randomUUID().toString());
        final String uri = ("http://localhost:" + (this.wireMockRule.port())) + path;
        final int status = NOT_FOUND.value();
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(path)).willReturn(WireMock.aResponse().withStatus(status)));
        final AccessTokenConverter converter = Mockito.mock(AccessTokenConverter.class);
        this.resourceServerProperties = new ResourceServerProperties(PingFederateRemoteTokenServicesUnitTests.CLIENT_ID, PingFederateRemoteTokenServicesUnitTests.CLIENT_SECRET);
        // Some resource no one should ever have
        this.resourceServerProperties.setTokenInfoUri(uri);
        final PingFederateRemoteTokenServices services = new PingFederateRemoteTokenServices(this.resourceServerProperties, converter, this.registry);
        final String accessToken = UUID.randomUUID().toString();
        final Counter restErrorCounter = Mockito.mock(Counter.class);
        Mockito.when(this.registry.counter(Mockito.eq(TOKEN_VALIDATION_ERROR_COUNTER_NAME), Mockito.anySet())).thenReturn(restErrorCounter);
        // Should throw exception based on error key being present
        try {
            services.loadAuthentication(accessToken);
            Assert.fail();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            Mockito.verify(restErrorCounter, Mockito.times(1)).increment();
        }
    }

    /**
     * This method isn't implemented for Ping Federate currently. Make sure this fails in case we ever implement it
     * and need to update the tests.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cantReadAccessToken() {
        final AccessTokenConverter converter = new DefaultAccessTokenConverter();
        final PingFederateRemoteTokenServices services = new PingFederateRemoteTokenServices(this.resourceServerProperties, converter, this.registry);
        services.readAccessToken(UUID.randomUUID().toString());
    }
}

