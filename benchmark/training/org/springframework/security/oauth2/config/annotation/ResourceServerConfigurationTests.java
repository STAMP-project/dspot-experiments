/**
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.config.annotation;


import OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE;
import OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 *
 *
 * @author Dave Syer
 */
public class ResourceServerConfigurationTests {
    private static InMemoryTokenStore tokenStore = new InMemoryTokenStore();

    private OAuth2AccessToken token;

    private OAuth2Authentication authentication;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testDefaults() throws Exception {
        ResourceServerConfigurationTests.tokenStore.storeAccessToken(token, authentication);
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.ResourceServerContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.get("/").header("Authorization", "Bearer FOO")).andExpect(MockMvcResultMatchers.status().isNotFound());
        context.close();
    }

    @Test
    public void testWithAuthServer() throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.ResourceServerAndAuthorizationServerContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.header().string("WWW-Authenticate", CoreMatchers.containsString("Bearer")));
        mvc.perform(MockMvcRequestBuilders.post("/oauth/token")).andExpect(MockMvcResultMatchers.header().string("WWW-Authenticate", CoreMatchers.containsString("Basic")));
        mvc.perform(MockMvcRequestBuilders.get("/oauth/authorize")).andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
        mvc.perform(MockMvcRequestBuilders.post("/oauth/token").header("Authorization", ("Basic " + (new String(Base64.encode("client:secret".getBytes())))))).andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Missing grant type")));
        context.close();
    }

    @Test
    public void testWithAuthServerCustomPath() throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.ResourceServerAndAuthorizationServerCustomPathContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.header().string("WWW-Authenticate", CoreMatchers.containsString("Bearer")));
        mvc.perform(MockMvcRequestBuilders.post("/token")).andExpect(MockMvcResultMatchers.header().string("WWW-Authenticate", CoreMatchers.containsString("Basic")));
        mvc.perform(MockMvcRequestBuilders.get("/authorize")).andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
        mvc.perform(MockMvcRequestBuilders.post("/token").header("Authorization", ("Basic " + (new String(Base64.encode("client:secret".getBytes())))))).andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Missing grant type")));
        context.close();
    }

    @Test
    public void testWithAuthServerAndGlobalMethodSecurity() throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.ResourceServerAndAuthorizationServerContextAndGlobalMethodSecurity.class);
        context.refresh();
        context.close();
    }

    @Test
    public void testCustomTokenServices() throws Exception {
        ResourceServerConfigurationTests.tokenStore.storeAccessToken(token, authentication);
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.TokenServicesContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mvc.perform(MockMvcRequestBuilders.get("/").header("Authorization", "Bearer FOO")).andExpect(MockMvcResultMatchers.status().isNotFound());
        context.close();
    }

    @Test
    public void testCustomTokenExtractor() throws Exception {
        ResourceServerConfigurationTests.tokenStore.storeAccessToken(token, authentication);
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.TokenExtractorContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/").header("Authorization", "Bearer BAR")).andExpect(MockMvcResultMatchers.status().isNotFound());
        context.close();
    }

    @Test
    public void testCustomExpressionHandler() throws Exception {
        ResourceServerConfigurationTests.tokenStore.storeAccessToken(token, authentication);
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.ExpressionHandlerContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("#oauth2");
        mvc.perform(MockMvcRequestBuilders.get("/").header("Authorization", "Bearer FOO")).andExpect(MockMvcResultMatchers.status().isUnauthorized());
        context.close();
    }

    @Test
    public void testCustomAuthenticationEntryPoint() throws Exception {
        ResourceServerConfigurationTests.tokenStore.storeAccessToken(token, authentication);
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.AuthenticationEntryPointContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/").header("Authorization", "Bearer FOO")).andExpect(MockMvcResultMatchers.status().isFound());
        context.close();
    }

    @Test
    public void testCustomAuthenticationDetailsSource() throws Exception {
        ResourceServerConfigurationTests.tokenStore.storeAccessToken(token, authentication);
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ResourceServerConfigurationTests.AuthenticationDetailsSourceContext.class);
        context.refresh();
        MockMvc mvc = buildMockMvc(context);
        mvc.perform(MockMvcRequestBuilders.get("/").header("Authorization", "Bearer FOO")).andExpect(MockMvcResultMatchers.status().isNotFound());
        context.close();
        OAuth2AuthenticationDetails authenticationDetails = ((OAuth2AuthenticationDetails) (authentication.getDetails()));
        Assert.assertEquals("Basic", authenticationDetails.getTokenType());
        Assert.assertEquals("BAR", authenticationDetails.getTokenValue());
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    protected static class ResourceServerContext {
        @Bean
        public TokenStore tokenStore() {
            return ResourceServerConfigurationTests.tokenStore;
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableAuthorizationServer
    @EnableWebSecurity
    @EnableWebMvc
    protected static class ResourceServerAndAuthorizationServerContext extends AuthorizationServerConfigurerAdapter {
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory().withClient("client").secret("secret").scopes("scope");
        }

        @Configuration
        protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
            @Bean
            public PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance();
            }
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableAuthorizationServer
    @EnableWebSecurity
    @EnableWebMvc
    protected static class ResourceServerAndAuthorizationServerCustomPathContext extends AuthorizationServerConfigurerAdapter {
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory().withClient("client").secret("secret").scopes("scope");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.pathMapping("/oauth/token", "/token");
            endpoints.pathMapping("/oauth/authorize", "/authorize");
        }

        @Configuration
        protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
            @Bean
            public PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance();
            }
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableAuthorizationServer
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
    protected static class ResourceServerAndAuthorizationServerContextAndGlobalMethodSecurity extends AuthorizationServerConfigurerAdapter {
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory();
        }

        @Autowired
        public void setup(AuthenticationManagerBuilder builder) throws Exception {
            builder.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    protected static class AuthenticationEntryPointContext extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated();
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.authenticationEntryPoint(authenticationEntryPoint());
        }

        private AuthenticationEntryPoint authenticationEntryPoint() {
            return new LoginUrlAuthenticationEntryPoint("/login");
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    protected static class TokenExtractorContext extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenExtractor(new org.springframework.security.oauth2.provider.authentication.TokenExtractor() {
                @Override
                public org.springframework.security.core.Authentication extract(HttpServletRequest request) {
                    return new org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken("FOO", "N/A");
                }
            }).tokenStore(tokenStore());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().access("#oauth2.isClient()");
        }

        @Bean
        public TokenStore tokenStore() {
            return ResourceServerConfigurationTests.tokenStore;
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    protected static class ExpressionHandlerContext extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.expressionHandler(new DefaultWebSecurityExpressionHandler());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().access("#oauth2.isClient()");
        }

        @Bean
        public TokenStore tokenStore() {
            return ResourceServerConfigurationTests.tokenStore;
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    protected static class TokenServicesContext {
        @Bean
        protected ClientDetailsService clientDetailsService() {
            InMemoryClientDetailsService service = new InMemoryClientDetailsService();
            service.setClientDetailsStore(Collections.singletonMap("client", new BaseClientDetails("client", null, null, null, null)));
            return service;
        }

        @Bean
        public DefaultTokenServices tokenServices() {
            DefaultTokenServices tokenServices = new DefaultTokenServices();
            tokenServices.setTokenStore(tokenStore());
            tokenServices.setClientDetailsService(clientDetailsService());
            return tokenServices;
        }

        @Bean
        public TokenStore tokenStore() {
            return ResourceServerConfigurationTests.tokenStore;
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    protected static class AuthenticationDetailsSourceContext extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.authenticationDetailsSource(new org.springframework.security.authentication.AuthenticationDetailsSource<HttpServletRequest, OAuth2AuthenticationDetails>() {
                @Override
                public OAuth2AuthenticationDetails buildDetails(HttpServletRequest request) {
                    request.setAttribute(ACCESS_TOKEN_TYPE, "Basic");
                    request.setAttribute(ACCESS_TOKEN_VALUE, "BAR");
                    return new OAuth2AuthenticationDetails(request);
                }
            });
        }

        @Bean
        public TokenStore tokenStore() {
            return ResourceServerConfigurationTests.tokenStore;
        }
    }
}

