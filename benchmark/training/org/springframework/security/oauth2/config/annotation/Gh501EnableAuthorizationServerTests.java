/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.security.oauth2.config.annotation;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * gh-501
 *
 * @author Joe Grandja
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class Gh501EnableAuthorizationServerTests {
    private static final String CLIENT_ID = "client-1234";

    private static final String CLIENT_SECRET = "secret-1234";

    private static BaseClientDetails client;

    @Autowired
    WebApplicationContext context;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    MockMvc mockMvc;

    static {
        Gh501EnableAuthorizationServerTests.client = new BaseClientDetails(Gh501EnableAuthorizationServerTests.CLIENT_ID, null, "read,write", "client_credentials", null);
        Gh501EnableAuthorizationServerTests.client.setClientSecret(Gh501EnableAuthorizationServerTests.CLIENT_SECRET);
    }

    @Test
    public void clientAuthenticationFailsThenCustomAuthenticationEntryPointCalled() throws Exception {
        mockMvc.perform(post("/oauth/token").param("grant_type", "client_credentials").header("Authorization", httpBasicCredentials(Gh501EnableAuthorizationServerTests.CLIENT_ID, "invalid-secret"))).andExpect(status().isUnauthorized());
        Mockito.verify(Gh501EnableAuthorizationServerTests.AuthorizationServerConfig.authenticationEntryPoint).commence(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Configuration
    @EnableAuthorizationServer
    @EnableWebMvc
    static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
        private static AuthenticationEntryPoint authenticationEntryPoint = Mockito.spy(new OAuth2AuthenticationEntryPoint());

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.authenticationEntryPoint(Gh501EnableAuthorizationServerTests.AuthorizationServerConfig.authenticationEntryPoint);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService());
        }

        @Bean
        public ClientDetailsService clientDetailsService() {
            return new ClientDetailsService() {
                @Override
                public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
                    return Gh501EnableAuthorizationServerTests.client;
                }
            };
        }
    }

    @Configuration
    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }
}

