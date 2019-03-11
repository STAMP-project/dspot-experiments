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
package org.springframework.security.config.annotation.web;


import WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Tests for {@link WebSecurityConfigurerAdapter}.
 *
 * @author Rob Winch
 * @author Joe Grandja
 */
@PrepareForTest({ WebAsyncManager.class })
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.w3c.dom.*", "org.xml.sax.*", "org.apache.xerces.*", "javax.xml.parsers.*", "javax.xml.transform.*" })
public class WebSecurityConfigurerAdapterTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loadConfigWhenRequestSecureThenDefaultSecurityHeadersReturned() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.HeadersArePopulatedByDefaultConfig.class).autowire();
        this.mockMvc.perform(get("/").secure(true)).andExpect(header().string("X-Content-Type-Options", "nosniff")).andExpect(header().string("X-Frame-Options", "DENY")).andExpect(header().string("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains")).andExpect(header().string("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")).andExpect(header().string("Pragma", "no-cache")).andExpect(header().string("Expires", "0")).andExpect(header().string("X-XSS-Protection", "1; mode=block"));
    }

    @EnableWebSecurity
    static class HeadersArePopulatedByDefaultConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }
    }

    @Test
    public void loadConfigWhenDefaultConfigThenWebAsyncManagerIntegrationFilterAdded() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.WebAsyncPopulatedByDefaultConfig.class).autowire();
        WebAsyncManager webAsyncManager = Mockito.mock(WebAsyncManager.class);
        this.mockMvc.perform(get("/").requestAttr(WEB_ASYNC_MANAGER_ATTRIBUTE, webAsyncManager));
        ArgumentCaptor<CallableProcessingInterceptor> callableProcessingInterceptorArgCaptor = ArgumentCaptor.forClass(CallableProcessingInterceptor.class);
        Mockito.verify(webAsyncManager, Mockito.atLeastOnce()).registerCallableInterceptor(ArgumentMatchers.any(), callableProcessingInterceptorArgCaptor.capture());
        CallableProcessingInterceptor callableProcessingInterceptor = callableProcessingInterceptorArgCaptor.getAllValues().stream().filter(( e) -> .class.isAssignableFrom(e.getClass())).findFirst().orElse(null);
        assertThat(callableProcessingInterceptor).isNotNull();
    }

    @EnableWebSecurity
    static class WebAsyncPopulatedByDefaultConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }
    }

    @Test
    public void loadConfigWhenRequestAuthenticateThenAuthenticationEventPublished() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.InMemoryAuthWithWebSecurityConfigurerAdapter.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(status().is3xxRedirection());
        assertThat(WebSecurityConfigurerAdapterTests.InMemoryAuthWithWebSecurityConfigurerAdapter.EVENTS).isNotEmpty();
        assertThat(WebSecurityConfigurerAdapterTests.InMemoryAuthWithWebSecurityConfigurerAdapter.EVENTS).hasSize(1);
    }

    @EnableWebSecurity
    static class InMemoryAuthWithWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter implements ApplicationListener<AuthenticationSuccessEvent> {
        static List<AuthenticationSuccessEvent> EVENTS = new ArrayList<>();

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }

        @Override
        public void onApplicationEvent(AuthenticationSuccessEvent event) {
            WebSecurityConfigurerAdapterTests.InMemoryAuthWithWebSecurityConfigurerAdapter.EVENTS.add(event);
        }
    }

    @Test
    public void loadConfigWhenInMemoryConfigureProtectedThenPasswordUpgraded() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.InMemoryConfigureProtectedConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(status().is3xxRedirection());
        UserDetailsService uds = this.spring.getContext().getBean(UserDetailsService.class);
        assertThat(uds.loadUserByUsername("user").getPassword()).startsWith("{bcrypt}");
    }

    @EnableWebSecurity
    static class InMemoryConfigureProtectedConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }

        @Override
        @Bean
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }
    }

    @Test
    public void loadConfigWhenInMemoryConfigureGlobalThenPasswordUpgraded() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.InMemoryConfigureGlobalConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(status().is3xxRedirection());
        UserDetailsService uds = this.spring.getContext().getBean(UserDetailsService.class);
        assertThat(uds.loadUserByUsername("user").getPassword()).startsWith("{bcrypt}");
    }

    @EnableWebSecurity
    static class InMemoryConfigureGlobalConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }

        @Override
        @Bean
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }
    }

    @Test
    public void loadConfigWhenCustomContentNegotiationStrategyBeanThenOverridesDefault() throws Exception {
        WebSecurityConfigurerAdapterTests.OverrideContentNegotiationStrategySharedObjectConfig.CONTENT_NEGOTIATION_STRATEGY_BEAN = Mockito.mock(ContentNegotiationStrategy.class);
        this.spring.register(WebSecurityConfigurerAdapterTests.OverrideContentNegotiationStrategySharedObjectConfig.class).autowire();
        WebSecurityConfigurerAdapterTests.OverrideContentNegotiationStrategySharedObjectConfig securityConfig = this.spring.getContext().getBean(WebSecurityConfigurerAdapterTests.OverrideContentNegotiationStrategySharedObjectConfig.class);
        assertThat(securityConfig.contentNegotiationStrategySharedObject).isNotNull();
        assertThat(securityConfig.contentNegotiationStrategySharedObject).isSameAs(WebSecurityConfigurerAdapterTests.OverrideContentNegotiationStrategySharedObjectConfig.CONTENT_NEGOTIATION_STRATEGY_BEAN);
    }

    @EnableWebSecurity
    static class OverrideContentNegotiationStrategySharedObjectConfig extends WebSecurityConfigurerAdapter {
        static ContentNegotiationStrategy CONTENT_NEGOTIATION_STRATEGY_BEAN;

        private ContentNegotiationStrategy contentNegotiationStrategySharedObject;

        @Bean
        public ContentNegotiationStrategy contentNegotiationStrategy() {
            return WebSecurityConfigurerAdapterTests.OverrideContentNegotiationStrategySharedObjectConfig.CONTENT_NEGOTIATION_STRATEGY_BEAN;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            this.contentNegotiationStrategySharedObject = http.getSharedObject(ContentNegotiationStrategy.class);
            super.configure(http);
        }
    }

    @Test
    public void loadConfigWhenDefaultContentNegotiationStrategyThenHeaderContentNegotiationStrategy() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.ContentNegotiationStrategyDefaultSharedObjectConfig.class).autowire();
        WebSecurityConfigurerAdapterTests.ContentNegotiationStrategyDefaultSharedObjectConfig securityConfig = this.spring.getContext().getBean(WebSecurityConfigurerAdapterTests.ContentNegotiationStrategyDefaultSharedObjectConfig.class);
        assertThat(securityConfig.contentNegotiationStrategySharedObject).isNotNull();
        assertThat(securityConfig.contentNegotiationStrategySharedObject).isInstanceOf(HeaderContentNegotiationStrategy.class);
    }

    @EnableWebSecurity
    static class ContentNegotiationStrategyDefaultSharedObjectConfig extends WebSecurityConfigurerAdapter {
        private ContentNegotiationStrategy contentNegotiationStrategySharedObject;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            this.contentNegotiationStrategySharedObject = http.getSharedObject(ContentNegotiationStrategy.class);
            super.configure(http);
        }
    }

    @Test
    public void loadConfigWhenUserDetailsServiceHasCircularReferenceThenStillLoads() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.RequiresUserDetailsServiceConfig.class, WebSecurityConfigurerAdapterTests.UserDetailsServiceConfig.class).autowire();
        WebSecurityConfigurerAdapterTests.MyFilter myFilter = this.spring.getContext().getBean(WebSecurityConfigurerAdapterTests.MyFilter.class);
        Throwable thrown = catchThrowable(() -> myFilter.userDetailsService.loadUserByUsername("user"));
        assertThat(thrown).isNull();
        thrown = catchThrowable(() -> myFilter.userDetailsService.loadUserByUsername("admin"));
        assertThat(thrown).isInstanceOf(UsernameNotFoundException.class);
    }

    @Configuration
    static class RequiresUserDetailsServiceConfig {
        @Bean
        public WebSecurityConfigurerAdapterTests.MyFilter myFilter(UserDetailsService userDetailsService) {
            return new WebSecurityConfigurerAdapterTests.MyFilter(userDetailsService);
        }
    }

    @EnableWebSecurity
    static class UserDetailsServiceConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private WebSecurityConfigurerAdapterTests.MyFilter myFilter;

        @Bean
        @Override
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }

        @Override
        public void configure(HttpSecurity http) {
            http.addFilterBefore(this.myFilter, UsernamePasswordAuthenticationFilter.class);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }

    static class MyFilter extends OncePerRequestFilter {
        private UserDetailsService userDetailsService;

        MyFilter(UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(request, response);
        }
    }

    // SEC-2274: WebSecurityConfigurer adds ApplicationContext as a shared object
    @Test
    public void loadConfigWhenSharedObjectsCreatedThenApplicationContextAdded() throws Exception {
        this.spring.register(WebSecurityConfigurerAdapterTests.ApplicationContextSharedObjectConfig.class).autowire();
        WebSecurityConfigurerAdapterTests.ApplicationContextSharedObjectConfig securityConfig = this.spring.getContext().getBean(WebSecurityConfigurerAdapterTests.ApplicationContextSharedObjectConfig.class);
        assertThat(securityConfig.applicationContextSharedObject).isNotNull();
        assertThat(securityConfig.applicationContextSharedObject).isSameAs(this.spring.getContext());
    }

    @EnableWebSecurity
    static class ApplicationContextSharedObjectConfig extends WebSecurityConfigurerAdapter {
        private ApplicationContext applicationContextSharedObject;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            this.applicationContextSharedObject = http.getSharedObject(ApplicationContext.class);
            super.configure(http);
        }
    }

    @Test
    public void loadConfigWhenCustomAuthenticationTrustResolverBeanThenOverridesDefault() throws Exception {
        WebSecurityConfigurerAdapterTests.CustomTrustResolverConfig.AUTHENTICATION_TRUST_RESOLVER_BEAN = Mockito.mock(AuthenticationTrustResolver.class);
        this.spring.register(WebSecurityConfigurerAdapterTests.CustomTrustResolverConfig.class).autowire();
        WebSecurityConfigurerAdapterTests.CustomTrustResolverConfig securityConfig = this.spring.getContext().getBean(WebSecurityConfigurerAdapterTests.CustomTrustResolverConfig.class);
        assertThat(securityConfig.authenticationTrustResolverSharedObject).isNotNull();
        assertThat(securityConfig.authenticationTrustResolverSharedObject).isSameAs(WebSecurityConfigurerAdapterTests.CustomTrustResolverConfig.AUTHENTICATION_TRUST_RESOLVER_BEAN);
    }

    @EnableWebSecurity
    static class CustomTrustResolverConfig extends WebSecurityConfigurerAdapter {
        static AuthenticationTrustResolver AUTHENTICATION_TRUST_RESOLVER_BEAN;

        private AuthenticationTrustResolver authenticationTrustResolverSharedObject;

        @Bean
        public AuthenticationTrustResolver authenticationTrustResolver() {
            return WebSecurityConfigurerAdapterTests.CustomTrustResolverConfig.AUTHENTICATION_TRUST_RESOLVER_BEAN;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            this.authenticationTrustResolverSharedObject = http.getSharedObject(AuthenticationTrustResolver.class);
            super.configure(http);
        }
    }

    @Test
    public void compareOrderWebSecurityConfigurerAdapterWhenLowestOrderToDefaultOrderThenGreaterThanZero() throws Exception {
        AnnotationAwareOrderComparator comparator = new AnnotationAwareOrderComparator();
        assertThat(comparator.compare(new WebSecurityConfigurerAdapterTests.LowestPriorityWebSecurityConfig(), new WebSecurityConfigurerAdapterTests.DefaultOrderWebSecurityConfig())).isGreaterThan(0);
    }

    static class DefaultOrderWebSecurityConfig extends WebSecurityConfigurerAdapter {}

    @Order
    static class LowestPriorityWebSecurityConfig extends WebSecurityConfigurerAdapter {}
}

