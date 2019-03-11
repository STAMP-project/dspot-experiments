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


import OAuth2ParameterNames.REGISTRATION_ID;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link OAuth2ClientConfigurer}.
 *
 * @author Joe Grandja
 */
public class OAuth2ClientConfigurerTests {
    private static ClientRegistrationRepository clientRegistrationRepository;

    private static OAuth2AuthorizedClientService authorizedClientService;

    private static OAuth2AuthorizedClientRepository authorizedClientRepository;

    private static OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    private static OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    private static RequestCache requestCache;

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    private ClientRegistration registration1;

    @Test
    public void configureWhenAuthorizationCodeRequestThenRedirectForAuthorization() throws Exception {
        this.spring.register(OAuth2ClientConfigurerTests.OAuth2ClientConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(get("/oauth2/authorization/registration-1")).andExpect(status().is3xxRedirection()).andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl()).matches(("https://provider.com/oauth2/authorize\\?" + (("response_type=code&client_id=client-1&" + "scope=user&state=.{15,}&") + "redirect_uri=http://localhost/client-1")));
    }

    @Test
    public void configureWhenAuthorizationCodeResponseSuccessThenAuthorizedClientSaved() throws Exception {
        this.spring.register(OAuth2ClientConfigurerTests.OAuth2ClientConfig.class).autowire();
        // Setup the Authorization Request in the session
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(REGISTRATION_ID, this.registration1.getRegistrationId());
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode().authorizationUri(this.registration1.getProviderDetails().getAuthorizationUri()).clientId(this.registration1.getClientId()).redirectUri("http://localhost/client-1").state("state").attributes(attributes).build();
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        MockHttpSession session = ((MockHttpSession) (request.getSession()));
        String principalName = "user1";
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(principalName, "password");
        this.mockMvc.perform(get("/client-1").param(OAuth2ParameterNames.CODE, "code").param(OAuth2ParameterNames.STATE, "state").with(authentication(authentication)).session(session)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("http://localhost/client-1"));
        OAuth2AuthorizedClient authorizedClient = OAuth2ClientConfigurerTests.authorizedClientRepository.loadAuthorizedClient(this.registration1.getRegistrationId(), authentication, request);
        assertThat(authorizedClient).isNotNull();
    }

    @Test
    public void configureWhenRequestCacheProvidedAndClientAuthorizationRequiredExceptionThrownThenRequestCacheUsed() throws Exception {
        this.spring.register(OAuth2ClientConfigurerTests.OAuth2ClientConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(get("/resource1").with(user("user1"))).andExpect(status().is3xxRedirection()).andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl()).matches(("https://provider.com/oauth2/authorize\\?" + (("response_type=code&client_id=client-1&" + "scope=user&state=.{15,}&") + "redirect_uri=http://localhost/client-1")));
        Mockito.verify(OAuth2ClientConfigurerTests.requestCache).saveRequest(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    // gh-5521
    @Test
    public void configureWhenCustomAuthorizationRequestResolverSetThenAuthorizationRequestIncludesCustomParameters() throws Exception {
        // Override default resolver
        OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver = OAuth2ClientConfigurerTests.authorizationRequestResolver;
        OAuth2ClientConfigurerTests.authorizationRequestResolver = Mockito.mock(OAuth2AuthorizationRequestResolver.class);
        Mockito.when(OAuth2ClientConfigurerTests.authorizationRequestResolver.resolve(ArgumentMatchers.any())).thenAnswer(( invocation) -> defaultAuthorizationRequestResolver.resolve(invocation.getArgument(0)));
        this.spring.register(OAuth2ClientConfigurerTests.OAuth2ClientConfig.class).autowire();
        this.mockMvc.perform(get("/oauth2/authorization/registration-1")).andExpect(status().is3xxRedirection()).andReturn();
        Mockito.verify(OAuth2ClientConfigurerTests.authorizationRequestResolver).resolve(ArgumentMatchers.any());
    }

    @EnableWebSecurity
    @EnableWebMvc
    static class OAuth2ClientConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().and().requestCache().requestCache(OAuth2ClientConfigurerTests.requestCache).and().oauth2Client().authorizationCodeGrant().authorizationRequestResolver(OAuth2ClientConfigurerTests.authorizationRequestResolver).accessTokenResponseClient(OAuth2ClientConfigurerTests.accessTokenResponseClient);
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return OAuth2ClientConfigurerTests.clientRegistrationRepository;
        }

        @Bean
        public OAuth2AuthorizedClientRepository authorizedClientRepository() {
            return OAuth2ClientConfigurerTests.authorizedClientRepository;
        }

        @RestController
        public class ResourceController {
            @GetMapping("/resource1")
            public String resource1(@RegisteredOAuth2AuthorizedClient("registration-1")
            OAuth2AuthorizedClient authorizedClient) {
                return "resource1";
            }
        }
    }
}

