/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.config.annotation.web.configurers.oauth2.client;


import CommonOAuth2Provider.GITHUB;
import CommonOAuth2Provider.GOOGLE;
import HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import IdTokenClaimNames.AUD;
import IdTokenClaimNames.AZP;
import IdTokenClaimNames.ISS;
import IdTokenClaimNames.SUB;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;


/**
 * Tests for {@link OAuth2LoginConfigurer}.
 *
 * @author Kazuki Shimizu
 * @author Joe Grandja
 * @since 5.0.1
 */
public class OAuth2LoginConfigurerTests {
    private static final ClientRegistration GOOGLE_CLIENT_REGISTRATION = GOOGLE.getBuilder("google").clientId("clientId").clientSecret("clientSecret").build();

    private static final ClientRegistration GITHUB_CLIENT_REGISTRATION = GITHUB.getBuilder("github").clientId("clientId").clientSecret("clientSecret").build();

    private ConfigurableApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Autowired
    SecurityContextRepository securityContextRepository;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockFilterChain filterChain;

    @Test
    public void oauth2Login() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfig.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities()).first().isInstanceOf(OAuth2UserAuthority.class).hasToString("ROLE_USER");
    }

    // gh-6009
    @Test
    public void oauth2LoginWhenSuccessThenAuthenticationSuccessEventPublished() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfig.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        assertThat(OAuth2LoginConfigurerTests.OAuth2LoginConfig.EVENTS).isNotEmpty();
        assertThat(OAuth2LoginConfigurerTests.OAuth2LoginConfig.EVENTS).hasSize(1);
        assertThat(OAuth2LoginConfigurerTests.OAuth2LoginConfig.EVENTS.get(0)).isInstanceOf(AuthenticationSuccessEvent.class);
    }

    @Test
    public void oauth2LoginWhenAuthenticatedThenIgnored() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfig.class);
        // authenticate
        TestingAuthenticationToken expectedAuthentication = new TestingAuthenticationToken("a", "b", "ROLE_TEST");
        this.request.getSession().setAttribute(SPRING_SECURITY_CONTEXT_KEY, new org.springframework.security.core.context.SecurityContextImpl(expectedAuthentication));
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", "state");
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication).isEqualTo(expectedAuthentication);
    }

    @Test
    public void oauth2LoginCustomWithConfigurer() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomWithConfigurer.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(2);
        assertThat(authentication.getAuthorities()).first().hasToString("ROLE_USER");
        assertThat(authentication.getAuthorities()).last().hasToString("ROLE_OAUTH2_USER");
    }

    @Test
    public void oauth2LoginCustomWithBeanRegistration() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomWithBeanRegistration.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest();
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(2);
        assertThat(authentication.getAuthorities()).first().hasToString("ROLE_USER");
        assertThat(authentication.getAuthorities()).last().hasToString("ROLE_OAUTH2_USER");
    }

    // gh-5488
    @Test
    public void oauth2LoginConfigLoginProcessingUrl() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigLoginProcessingUrl.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest();
        this.request.setServletPath("/login/oauth2/google");
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities()).first().isInstanceOf(OAuth2UserAuthority.class).hasToString("ROLE_USER");
    }

    // gh-5521
    @Test
    public void oauth2LoginWithCustomAuthorizationRequestParameters() throws Exception {
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomAuthorizationRequestResolver.class);
        OAuth2AuthorizationRequestResolver resolver = this.context.getBean(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomAuthorizationRequestResolver.class).resolver;
        OAuth2AuthorizationRequest result = OAuth2AuthorizationRequest.authorizationCode().authorizationUri("https://accounts.google.com/authorize").clientId("client-id").state("adsfa").authorizationRequestUri("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=clientId&scope=openid+profile+email&state=state&redirect_uri=http%3A%2F%2Flocalhost%2Flogin%2Foauth2%2Fcode%2Fgoogle&custom-param1=custom-value1").build();
        Mockito.when(resolver.resolve(ArgumentMatchers.any())).thenReturn(result);
        String requestUri = "/oauth2/authorization/google";
        this.request = new MockHttpServletRequest("GET", requestUri);
        this.request.setServletPath(requestUri);
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.response.getRedirectedUrl()).isEqualTo("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=clientId&scope=openid+profile+email&state=state&redirect_uri=http%3A%2F%2Flocalhost%2Flogin%2Foauth2%2Fcode%2Fgoogle&custom-param1=custom-value1");
    }

    // gh-5347
    @Test
    public void oauth2LoginWithOneClientConfiguredThenRedirectForAuthorization() throws Exception {
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfig.class);
        String requestUri = "/";
        this.request = new MockHttpServletRequest("GET", requestUri);
        this.request.setServletPath(requestUri);
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.response.getRedirectedUrl()).matches("http://localhost/oauth2/authorization/google");
    }

    // gh-5347
    @Test
    public void oauth2LoginWithOneClientConfiguredAndRequestFaviconNotAuthenticatedThenRedirectDefaultLoginPage() throws Exception {
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfig.class);
        String requestUri = "/favicon.ico";
        this.request = new MockHttpServletRequest("GET", requestUri);
        this.request.setServletPath(requestUri);
        this.request.addHeader(HttpHeaders.ACCEPT, new MediaType("image", "*").toString());
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.response.getRedirectedUrl()).matches("http://localhost/login");
    }

    // gh-5347
    @Test
    public void oauth2LoginWithMultipleClientsConfiguredThenRedirectDefaultLoginPage() throws Exception {
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigMultipleClients.class);
        String requestUri = "/";
        this.request = new MockHttpServletRequest("GET", requestUri);
        this.request.setServletPath(requestUri);
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.response.getRedirectedUrl()).matches("http://localhost/login");
    }

    @Test
    public void oauth2LoginWithCustomLoginPageThenRedirectCustomLoginPage() throws Exception {
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomLoginPage.class);
        String requestUri = "/";
        this.request = new MockHttpServletRequest("GET", requestUri);
        this.request.setServletPath(requestUri);
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.response.getRedirectedUrl()).matches("http://localhost/custom-login");
    }

    @Test
    public void oidcLogin() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfig.class, OAuth2LoginConfigurerTests.JwtDecoderFactoryConfig.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest("openid");
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities()).first().isInstanceOf(OidcUserAuthority.class).hasToString("ROLE_USER");
    }

    @Test
    public void oidcLoginCustomWithConfigurer() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomWithConfigurer.class, OAuth2LoginConfigurerTests.JwtDecoderFactoryConfig.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest("openid");
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(2);
        assertThat(authentication.getAuthorities()).first().hasToString("ROLE_USER");
        assertThat(authentication.getAuthorities()).last().hasToString("ROLE_OIDC_USER");
    }

    @Test
    public void oidcLoginCustomWithBeanRegistration() throws Exception {
        // setup application context
        loadConfig(OAuth2LoginConfigurerTests.OAuth2LoginConfigCustomWithBeanRegistration.class, OAuth2LoginConfigurerTests.JwtDecoderFactoryConfig.class);
        // setup authorization request
        OAuth2AuthorizationRequest authorizationRequest = createOAuth2AuthorizationRequest("openid");
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, this.request, this.response);
        // setup authentication parameters
        this.request.setParameter("code", "code123");
        this.request.setParameter("state", authorizationRequest.getState());
        // perform test
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.filterChain);
        // assertions
        Authentication authentication = this.securityContextRepository.loadContext(new org.springframework.security.web.context.HttpRequestResponseHolder(this.request, this.response)).getAuthentication();
        assertThat(authentication.getAuthorities()).hasSize(2);
        assertThat(authentication.getAuthorities()).first().hasToString("ROLE_USER");
        assertThat(authentication.getAuthorities()).last().hasToString("ROLE_OIDC_USER");
    }

    @Test
    public void oidcLoginCustomWithNoUniqueJwtDecoderFactory() {
        assertThatThrownBy(() -> loadConfig(.class, .class)).hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class).hasMessageContaining(("No qualifying bean of type " + ("'org.springframework.security.oauth2.jwt.JwtDecoderFactory<org.springframework.security.oauth2.client.registration.ClientRegistration>' " + "available: expected single matching bean but found 2: jwtDecoderFactory1,jwtDecoderFactory2")));
    }

    @EnableWebSecurity
    static class OAuth2LoginConfig extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter implements ApplicationListener<AuthenticationSuccessEvent> {
        static List<AuthenticationSuccessEvent> EVENTS = new ArrayList<>();

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().clientRegistrationRepository(new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION));
            super.configure(http);
        }

        @Override
        public void onApplicationEvent(AuthenticationSuccessEvent event) {
            OAuth2LoginConfigurerTests.OAuth2LoginConfig.EVENTS.add(event);
        }
    }

    @EnableWebSecurity
    static class OAuth2LoginConfigCustomWithConfigurer extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().clientRegistrationRepository(new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION)).userInfoEndpoint().userAuthoritiesMapper(OAuth2LoginConfigurerTests.createGrantedAuthoritiesMapper());
            super.configure(http);
        }
    }

    @EnableWebSecurity
    static class OAuth2LoginConfigCustomWithBeanRegistration extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login();
            super.configure(http);
        }

        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION);
        }

        @Bean
        GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
            return OAuth2LoginConfigurerTests.createGrantedAuthoritiesMapper();
        }
    }

    @EnableWebSecurity
    static class OAuth2LoginConfigLoginProcessingUrl extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().clientRegistrationRepository(new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION)).loginProcessingUrl("/login/oauth2/*");
            super.configure(http);
        }
    }

    @EnableWebSecurity
    static class OAuth2LoginConfigCustomAuthorizationRequestResolver extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter {
        private ClientRegistrationRepository clientRegistrationRepository = new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION);

        OAuth2AuthorizationRequestResolver resolver = Mockito.mock(OAuth2AuthorizationRequestResolver.class);

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().clientRegistrationRepository(this.clientRegistrationRepository).authorizationEndpoint().authorizationRequestResolver(this.resolver);
            super.configure(http);
        }
    }

    @EnableWebSecurity
    static class OAuth2LoginConfigMultipleClients extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().clientRegistrationRepository(new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION, OAuth2LoginConfigurerTests.GITHUB_CLIENT_REGISTRATION));
            super.configure(http);
        }
    }

    @EnableWebSecurity
    static class OAuth2LoginConfigCustomLoginPage extends OAuth2LoginConfigurerTests.CommonWebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().clientRegistrationRepository(new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(OAuth2LoginConfigurerTests.GOOGLE_CLIENT_REGISTRATION)).loginPage("/custom-login");
            super.configure(http);
        }
    }

    private abstract static class CommonWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().and().securityContext().securityContextRepository(securityContextRepository()).and().oauth2Login().tokenEndpoint().accessTokenResponseClient(OAuth2LoginConfigurerTests.createOauth2AccessTokenResponseClient()).and().userInfoEndpoint().userService(OAuth2LoginConfigurerTests.createOauth2UserService()).oidcUserService(OAuth2LoginConfigurerTests.createOidcUserService());
        }

        @Bean
        SecurityContextRepository securityContextRepository() {
            return new HttpSessionSecurityContextRepository();
        }

        @Bean
        HttpSessionOAuth2AuthorizationRequestRepository oauth2AuthorizationRequestRepository() {
            return new HttpSessionOAuth2AuthorizationRequestRepository();
        }
    }

    @Configuration
    static class JwtDecoderFactoryConfig {
        @Bean
        JwtDecoderFactory<ClientRegistration> jwtDecoderFactory() {
            return ( clientRegistration) -> getJwtDecoder();
        }

        private static JwtDecoder getJwtDecoder() {
            Map<String, Object> claims = new HashMap<>();
            claims.put(SUB, "sub123");
            claims.put(ISS, "http://localhost/iss");
            claims.put(AUD, Arrays.asList("clientId", "a", "u", "d"));
            claims.put(AZP, "clientId");
            Jwt jwt = new Jwt("token123", Instant.now(), Instant.now().plusSeconds(3600), Collections.singletonMap("header1", "value1"), claims);
            JwtDecoder jwtDecoder = Mockito.mock(JwtDecoder.class);
            Mockito.when(jwtDecoder.decode(ArgumentMatchers.any())).thenReturn(jwt);
            return jwtDecoder;
        }
    }

    @Configuration
    static class NoUniqueJwtDecoderFactoryConfig {
        @Bean
        JwtDecoderFactory<ClientRegistration> jwtDecoderFactory1() {
            return ( clientRegistration) -> org.springframework.security.config.annotation.web.configurers.oauth2.client.JwtDecoderFactoryConfig.getJwtDecoder();
        }

        @Bean
        JwtDecoderFactory<ClientRegistration> jwtDecoderFactory2() {
            return ( clientRegistration) -> org.springframework.security.config.annotation.web.configurers.oauth2.client.JwtDecoderFactoryConfig.getJwtDecoder();
        }
    }
}

