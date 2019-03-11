/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.config.annotation.web.configuration;


import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link OAuth2ClientConfiguration}.
 *
 * @author Joe Grandja
 */
public class OAuth2ClientConfigurationTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestWhenAuthorizedClientFoundThenMethodArgumentResolved() throws Exception {
        String clientRegistrationId = "client1";
        String principalName = "user1";
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(principalName, "password");
        OAuth2AuthorizedClientRepository authorizedClientRepository = Mockito.mock(OAuth2AuthorizedClientRepository.class);
        OAuth2AuthorizedClient authorizedClient = Mockito.mock(OAuth2AuthorizedClient.class);
        Mockito.when(authorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.eq(clientRegistrationId), ArgumentMatchers.eq(authentication), ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(authorizedClient);
        OAuth2AccessToken accessToken = Mockito.mock(OAuth2AccessToken.class);
        Mockito.when(authorizedClient.getAccessToken()).thenReturn(accessToken);
        OAuth2ClientConfigurationTests.OAuth2AuthorizedClientArgumentResolverConfig.AUTHORIZED_CLIENT_REPOSITORY = authorizedClientRepository;
        this.spring.register(OAuth2ClientConfigurationTests.OAuth2AuthorizedClientArgumentResolverConfig.class).autowire();
        this.mockMvc.perform(get("/authorized-client").with(authentication(authentication))).andExpect(status().isOk()).andExpect(content().string("resolved"));
    }

    @EnableWebMvc
    @EnableWebSecurity
    static class OAuth2AuthorizedClientArgumentResolverConfig extends WebSecurityConfigurerAdapter {
        static OAuth2AuthorizedClientRepository AUTHORIZED_CLIENT_REPOSITORY;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }

        @RestController
        public class Controller {
            @GetMapping("/authorized-client")
            public String authorizedClient(@RegisteredOAuth2AuthorizedClient("client1")
            OAuth2AuthorizedClient authorizedClient) {
                return authorizedClient != null ? "resolved" : "not-resolved";
            }
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return Mockito.mock(ClientRegistrationRepository.class);
        }

        @Bean
        public OAuth2AuthorizedClientRepository authorizedClientRepository() {
            return OAuth2ClientConfigurationTests.OAuth2AuthorizedClientArgumentResolverConfig.AUTHORIZED_CLIENT_REPOSITORY;
        }
    }

    // gh-5321
    @Test
    public void loadContextWhenOAuth2AuthorizedClientRepositoryRegisteredTwiceThenThrowNoUniqueBeanDefinitionException() {
        assertThatThrownBy(() -> this.spring.register(.class).autowire()).hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class).hasMessageContaining((("Expected single matching bean of type '" + (OAuth2AuthorizedClientRepository.class.getName())) + "' but found 2: authorizedClientRepository1,authorizedClientRepository2"));
    }

    @EnableWebMvc
    @EnableWebSecurity
    static class OAuth2AuthorizedClientRepositoryRegisteredTwiceConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeRequests().anyRequest().authenticated().and().oauth2Login();
            // @formatter:on
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return Mockito.mock(ClientRegistrationRepository.class);
        }

        @Bean
        public OAuth2AuthorizedClientRepository authorizedClientRepository1() {
            return Mockito.mock(OAuth2AuthorizedClientRepository.class);
        }

        @Bean
        public OAuth2AuthorizedClientRepository authorizedClientRepository2() {
            return Mockito.mock(OAuth2AuthorizedClientRepository.class);
        }
    }

    @Test
    public void loadContextWhenClientRegistrationRepositoryNotRegisteredThenThrowNoSuchBeanDefinitionException() {
        assertThatThrownBy(() -> this.spring.register(.class).autowire()).hasRootCauseInstanceOf(NoSuchBeanDefinitionException.class).hasMessageContaining((("No qualifying bean of type '" + (ClientRegistrationRepository.class.getName())) + "' available"));
    }

    @EnableWebMvc
    @EnableWebSecurity
    static class ClientRegistrationRepositoryNotRegisteredConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeRequests().anyRequest().authenticated().and().oauth2Login();
            // @formatter:on
        }
    }

    @Test
    public void loadContextWhenClientRegistrationRepositoryRegisteredTwiceThenThrowNoUniqueBeanDefinitionException() {
        assertThatThrownBy(() -> this.spring.register(.class).autowire()).hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class).hasMessageContaining("expected single matching bean but found 2: clientRegistrationRepository1,clientRegistrationRepository2");
    }

    @EnableWebMvc
    @EnableWebSecurity
    static class ClientRegistrationRepositoryRegisteredTwiceConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeRequests().anyRequest().authenticated().and().oauth2Login();
            // @formatter:on
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository1() {
            return Mockito.mock(ClientRegistrationRepository.class);
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository2() {
            return Mockito.mock(ClientRegistrationRepository.class);
        }

        @Bean
        public OAuth2AuthorizedClientRepository authorizedClientRepository() {
            return Mockito.mock(OAuth2AuthorizedClientRepository.class);
        }
    }
}

