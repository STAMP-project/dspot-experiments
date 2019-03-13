/**
 * Copyright 2012-2016 the original author or authors.
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


import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * gh-808
 *
 * @author Joe Grandja
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class Gh808EnableAuthorizationServerTests {
    private static final String CLIENT_ID = "acme";

    private static final String CLIENT_SECRET = "acmesecret";

    private static final String USER_ID = Gh808EnableAuthorizationServerTests.CLIENT_ID;

    private static final String USER_SECRET = (Gh808EnableAuthorizationServerTests.CLIENT_SECRET) + "2";

    private static BaseClientDetails client;

    private static UserDetails user;

    @Autowired
    WebApplicationContext context;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    MockMvc mockMvc;

    static {
        Gh808EnableAuthorizationServerTests.client = new BaseClientDetails(Gh808EnableAuthorizationServerTests.CLIENT_ID, null, "read,write", "password,client_credentials", "ROLE_ADMIN", "http://example.com/oauth2callback");
        Gh808EnableAuthorizationServerTests.client.setClientSecret(Gh808EnableAuthorizationServerTests.CLIENT_SECRET);
        Gh808EnableAuthorizationServerTests.user = new org.springframework.security.core.userdetails.User(Gh808EnableAuthorizationServerTests.USER_ID, Gh808EnableAuthorizationServerTests.USER_SECRET, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void clientAuthenticationFailsUsingUserCredentialsOnClientCredentialsGrantFlow() throws Exception {
        mockMvc.perform(post("/oauth/token").param("grant_type", "client_credentials").header("Authorization", httpBasicCredentials(Gh808EnableAuthorizationServerTests.USER_ID, Gh808EnableAuthorizationServerTests.USER_SECRET))).andExpect(status().isUnauthorized());
    }

    @Test
    public void clientAuthenticationFailsUsingUserCredentialsOnResourceOwnerPasswordGrantFlow() throws Exception {
        mockMvc.perform(post("/oauth/token").param("grant_type", "password").param("client_id", Gh808EnableAuthorizationServerTests.CLIENT_ID).param("username", Gh808EnableAuthorizationServerTests.USER_ID).param("password", Gh808EnableAuthorizationServerTests.USER_SECRET).header("Authorization", httpBasicCredentials(Gh808EnableAuthorizationServerTests.USER_ID, Gh808EnableAuthorizationServerTests.USER_SECRET))).andExpect(status().isUnauthorized());
    }

    @Configuration
    @EnableAuthorizationServer
    @EnableWebMvc
    static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsService userDetailsService;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService());
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(this.authenticationManager).userDetailsService(this.userDetailsService);
        }

        @Bean
        public ClientDetailsService clientDetailsService() {
            return new ClientDetailsService() {
                @Override
                public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
                    return Gh808EnableAuthorizationServerTests.client;
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

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            // Expose the Global AuthenticationManager
            return super.authenticationManagerBean();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                    return Gh808EnableAuthorizationServerTests.user;
                }
            };
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }
}

