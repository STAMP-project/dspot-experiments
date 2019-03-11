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
package org.springframework.security.config.annotation.web.builders;


import SessionCreationPolicy.ALWAYS;
import SessionCreationPolicy.NEVER;
import SessionCreationPolicy.STATELESS;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.ExpressionBasedFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Tests to verify that all the functionality of <http> attributes are present in Java Config.
 *
 * @author Rob Winch
 * @author Joe Grandja
 */
public class NamespaceHttpTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    // http@access-decision-manager-ref
    @Test
    public void configureWhenAccessDecisionManagerSetThenVerifyUse() throws Exception {
        NamespaceHttpTests.AccessDecisionManagerRefConfig.ACCESS_DECISION_MANAGER = Mockito.mock(AccessDecisionManager.class);
        Mockito.when(NamespaceHttpTests.AccessDecisionManagerRefConfig.ACCESS_DECISION_MANAGER.supports(FilterInvocation.class)).thenReturn(true);
        Mockito.when(NamespaceHttpTests.AccessDecisionManagerRefConfig.ACCESS_DECISION_MANAGER.supports(ArgumentMatchers.any(ConfigAttribute.class))).thenReturn(true);
        this.spring.register(NamespaceHttpTests.AccessDecisionManagerRefConfig.class).autowire();
        this.mockMvc.perform(get("/"));
        Mockito.verify(NamespaceHttpTests.AccessDecisionManagerRefConfig.ACCESS_DECISION_MANAGER, Mockito.times(1)).decide(ArgumentMatchers.any(Authentication.class), ArgumentMatchers.any(), ArgumentMatchers.anyCollection());
    }

    @EnableWebSecurity
    static class AccessDecisionManagerRefConfig extends WebSecurityConfigurerAdapter {
        static AccessDecisionManager ACCESS_DECISION_MANAGER;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll().accessDecisionManager(NamespaceHttpTests.AccessDecisionManagerRefConfig.ACCESS_DECISION_MANAGER);
        }
    }

    // http@access-denied-page
    @Test
    public void configureWhenAccessDeniedPageSetAndRequestForbiddenThenForwardedToAccessDeniedPage() throws Exception {
        this.spring.register(NamespaceHttpTests.AccessDeniedPageConfig.class).autowire();
        this.mockMvc.perform(get("/admin").with(user(PasswordEncodedUser.user()))).andExpect(status().isForbidden()).andExpect(forwardedUrl("/AccessDeniedPage"));
    }

    @EnableWebSecurity
    static class AccessDeniedPageConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/admin").hasRole("ADMIN").anyRequest().authenticated().and().exceptionHandling().accessDeniedPage("/AccessDeniedPage");
        }
    }

    // http@authentication-manager-ref
    @Test
    public void configureWhenAuthenticationManagerProvidedThenVerifyUse() throws Exception {
        NamespaceHttpTests.AuthenticationManagerRefConfig.AUTHENTICATION_MANAGER = Mockito.mock(AuthenticationManager.class);
        this.spring.register(NamespaceHttpTests.AuthenticationManagerRefConfig.class).autowire();
        this.mockMvc.perform(formLogin());
        Mockito.verify(NamespaceHttpTests.AuthenticationManagerRefConfig.AUTHENTICATION_MANAGER, Mockito.times(1)).authenticate(ArgumentMatchers.any(Authentication.class));
    }

    @EnableWebSecurity
    static class AuthenticationManagerRefConfig extends WebSecurityConfigurerAdapter {
        static AuthenticationManager AUTHENTICATION_MANAGER;

        @Override
        protected AuthenticationManager authenticationManager() throws Exception {
            return NamespaceHttpTests.AuthenticationManagerRefConfig.AUTHENTICATION_MANAGER;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            formLogin();
        }
    }

    // http@create-session=always
    @Test
    public void configureWhenSessionCreationPolicyAlwaysThenSessionCreatedOnRequest() throws Exception {
        this.spring.register(NamespaceHttpTests.CreateSessionAlwaysConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(get("/")).andReturn();
        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNotNull();
        assertThat(session.isNew()).isTrue();
    }

    @EnableWebSecurity
    static class CreateSessionAlwaysConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll().and().sessionManagement().sessionCreationPolicy(ALWAYS);
        }
    }

    // http@create-session=stateless
    @Test
    public void configureWhenSessionCreationPolicyStatelessThenSessionNotCreatedOnRequest() throws Exception {
        this.spring.register(NamespaceHttpTests.CreateSessionStatelessConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(get("/")).andReturn();
        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull();
    }

    @EnableWebSecurity
    static class CreateSessionStatelessConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll().and().sessionManagement().sessionCreationPolicy(STATELESS);
        }
    }

    // http@create-session=ifRequired
    @Test
    public void configureWhenSessionCreationPolicyIfRequiredThenSessionCreatedWhenRequiredOnRequest() throws Exception {
        this.spring.register(NamespaceHttpTests.IfRequiredConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(get("/unsecure")).andReturn();
        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull();
        mvcResult = this.mockMvc.perform(formLogin()).andReturn();
        session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNotNull();
        assertThat(session.isNew()).isTrue();
    }

    @EnableWebSecurity
    static class IfRequiredConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            formLogin();
        }
    }

    // http@create-session=never
    @Test
    public void configureWhenSessionCreationPolicyNeverThenSessionNotCreatedOnRequest() throws Exception {
        this.spring.register(NamespaceHttpTests.CreateSessionNeverConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(get("/")).andReturn();
        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull();
    }

    @EnableWebSecurity
    static class CreateSessionNeverConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().anonymous().and().sessionManagement().sessionCreationPolicy(NEVER);
        }
    }

    // http@entry-point-ref
    @Test
    public void configureWhenAuthenticationEntryPointSetAndRequestUnauthorizedThenRedirectedToAuthenticationEntryPoint() throws Exception {
        this.spring.register(NamespaceHttpTests.EntryPointRefConfig.class).autowire();
        this.mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/entry-point"));
    }

    @EnableWebSecurity
    static class EntryPointRefConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            formLogin();
        }
    }

    // http@jaas-api-provision
    @Test
    public void configureWhenJaasApiIntegrationFilterAddedThenJaasSubjectObtained() throws Exception {
        LoginContext loginContext = Mockito.mock(LoginContext.class);
        Mockito.when(loginContext.getSubject()).thenReturn(new Subject());
        JaasAuthenticationToken authenticationToken = Mockito.mock(JaasAuthenticationToken.class);
        Mockito.when(authenticationToken.isAuthenticated()).thenReturn(true);
        Mockito.when(authenticationToken.getLoginContext()).thenReturn(loginContext);
        this.spring.register(NamespaceHttpTests.JaasApiProvisionConfig.class).autowire();
        this.mockMvc.perform(get("/").with(authentication(authenticationToken)));
        Mockito.verify(loginContext, Mockito.times(1)).getSubject();
    }

    @EnableWebSecurity
    static class JaasApiProvisionConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.addFilter(new JaasApiIntegrationFilter());
        }
    }

    // http@realm
    @Test
    public void configureWhenHttpBasicAndRequestUnauthorizedThenReturnWWWAuthenticateWithRealm() throws Exception {
        this.spring.register(NamespaceHttpTests.RealmConfig.class).autowire();
        this.mockMvc.perform(get("/")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "Basic realm=\"RealmConfig\""));
    }

    @EnableWebSecurity
    static class RealmConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().and().httpBasic().realmName("RealmConfig");
        }
    }

    // http@request-matcher-ref ant
    @Test
    public void configureWhenAntPatternMatchingThenAntPathRequestMatcherUsed() throws Exception {
        this.spring.register(NamespaceHttpTests.RequestMatcherAntConfig.class).autowire();
        FilterChainProxy filterChainProxy = this.spring.getContext().getBean(FilterChainProxy.class);
        assertThat(filterChainProxy.getFilterChains().get(0)).isInstanceOf(DefaultSecurityFilterChain.class);
        DefaultSecurityFilterChain securityFilterChain = ((DefaultSecurityFilterChain) (filterChainProxy.getFilterChains().get(0)));
        assertThat(securityFilterChain.getRequestMatcher()).isInstanceOf(AntPathRequestMatcher.class);
    }

    @EnableWebSecurity
    static class RequestMatcherAntConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**");
        }
    }

    // http@request-matcher-ref regex
    @Test
    public void configureWhenRegexPatternMatchingThenRegexRequestMatcherUsed() throws Exception {
        this.spring.register(NamespaceHttpTests.RequestMatcherRegexConfig.class).autowire();
        FilterChainProxy filterChainProxy = this.spring.getContext().getBean(FilterChainProxy.class);
        assertThat(filterChainProxy.getFilterChains().get(0)).isInstanceOf(DefaultSecurityFilterChain.class);
        DefaultSecurityFilterChain securityFilterChain = ((DefaultSecurityFilterChain) (filterChainProxy.getFilterChains().get(0)));
        assertThat(securityFilterChain.getRequestMatcher()).isInstanceOf(RegexRequestMatcher.class);
    }

    @EnableWebSecurity
    static class RequestMatcherRegexConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.regexMatcher("/regex/.*");
        }
    }

    // http@request-matcher-ref
    @Test
    public void configureWhenRequestMatcherProvidedThenRequestMatcherUsed() throws Exception {
        this.spring.register(NamespaceHttpTests.RequestMatcherRefConfig.class).autowire();
        FilterChainProxy filterChainProxy = this.spring.getContext().getBean(FilterChainProxy.class);
        assertThat(filterChainProxy.getFilterChains().get(0)).isInstanceOf(DefaultSecurityFilterChain.class);
        DefaultSecurityFilterChain securityFilterChain = ((DefaultSecurityFilterChain) (filterChainProxy.getFilterChains().get(0)));
        assertThat(securityFilterChain.getRequestMatcher()).isInstanceOf(NamespaceHttpTests.RequestMatcherRefConfig.MyRequestMatcher.class);
    }

    @EnableWebSecurity
    static class RequestMatcherRefConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatcher(new NamespaceHttpTests.RequestMatcherRefConfig.MyRequestMatcher());
        }

        static class MyRequestMatcher implements RequestMatcher {
            public boolean matches(HttpServletRequest request) {
                return true;
            }
        }
    }

    // http@security=none
    @Test
    public void configureWhenIgnoredAntPatternsThenAntPathRequestMatcherUsedWithNoFilters() throws Exception {
        this.spring.register(NamespaceHttpTests.SecurityNoneConfig.class).autowire();
        FilterChainProxy filterChainProxy = this.spring.getContext().getBean(FilterChainProxy.class);
        assertThat(filterChainProxy.getFilterChains().get(0)).isInstanceOf(DefaultSecurityFilterChain.class);
        DefaultSecurityFilterChain securityFilterChain = ((DefaultSecurityFilterChain) (filterChainProxy.getFilterChains().get(0)));
        assertThat(securityFilterChain.getRequestMatcher()).isInstanceOf(AntPathRequestMatcher.class);
        assertThat(getPattern()).isEqualTo("/resources/**");
        assertThat(securityFilterChain.getFilters()).isEmpty();
        assertThat(filterChainProxy.getFilterChains().get(1)).isInstanceOf(DefaultSecurityFilterChain.class);
        securityFilterChain = ((DefaultSecurityFilterChain) (filterChainProxy.getFilterChains().get(1)));
        assertThat(securityFilterChain.getRequestMatcher()).isInstanceOf(AntPathRequestMatcher.class);
        assertThat(getPattern()).isEqualTo("/public/**");
        assertThat(securityFilterChain.getFilters()).isEmpty();
    }

    @EnableWebSecurity
    static class SecurityNoneConfig extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/resources/**", "/public/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }
    }

    // http@security-context-repository-ref
    @Test
    public void configureWhenNullSecurityContextRepositoryThenSecurityContextNotSavedInSession() throws Exception {
        this.spring.register(NamespaceHttpTests.SecurityContextRepoConfig.class).autowire();
        MvcResult mvcResult = this.mockMvc.perform(formLogin()).andReturn();
        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull();
    }

    @EnableWebSecurity
    static class SecurityContextRepoConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            formLogin();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }

    // http@servlet-api-provision=false
    @Test
    public void configureWhenServletApiDisabledThenRequestNotServletApiWrapper() throws Exception {
        this.spring.register(NamespaceHttpTests.ServletApiProvisionConfig.class, NamespaceHttpTests.MainController.class).autowire();
        this.mockMvc.perform(get("/"));
        assertThat(NamespaceHttpTests.MainController.HTTP_SERVLET_REQUEST_TYPE).isNotInstanceOf(SecurityContextHolderAwareRequestWrapper.class);
    }

    @EnableWebSecurity
    static class ServletApiProvisionConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll().and().servletApi().disable();
        }
    }

    // http@servlet-api-provision defaults to true
    @Test
    public void configureWhenServletApiDefaultThenRequestIsServletApiWrapper() throws Exception {
        this.spring.register(NamespaceHttpTests.ServletApiProvisionDefaultsConfig.class, NamespaceHttpTests.MainController.class).autowire();
        this.mockMvc.perform(get("/"));
        assertThat(SecurityContextHolderAwareRequestWrapper.class).isAssignableFrom(NamespaceHttpTests.MainController.HTTP_SERVLET_REQUEST_TYPE);
    }

    @EnableWebSecurity
    static class ServletApiProvisionDefaultsConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll();
        }
    }

    @Controller
    static class MainController {
        static Class<? extends HttpServletRequest> HTTP_SERVLET_REQUEST_TYPE;

        @GetMapping("/")
        public String index(HttpServletRequest request) {
            NamespaceHttpTests.MainController.HTTP_SERVLET_REQUEST_TYPE = request.getClass();
            return "index";
        }
    }

    // http@use-expressions=true
    @Test
    public void configureWhenUseExpressionsEnabledThenExpressionBasedSecurityMetadataSource() throws Exception {
        this.spring.register(NamespaceHttpTests.UseExpressionsConfig.class).autowire();
        NamespaceHttpTests.UseExpressionsConfig config = this.spring.getContext().getBean(NamespaceHttpTests.UseExpressionsConfig.class);
        assertThat(ExpressionBasedFilterInvocationSecurityMetadataSource.class).isAssignableFrom(config.filterInvocationSecurityMetadataSourceType);
    }

    @EnableWebSecurity
    static class UseExpressionsConfig extends WebSecurityConfigurerAdapter {
        private Class<? extends FilterInvocationSecurityMetadataSource> filterInvocationSecurityMetadataSourceType;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/users**", "/sessions/**").hasRole("USER").antMatchers("/signup").permitAll().anyRequest().hasRole("USER");
        }

        @Override
        public void init(final WebSecurity web) throws Exception {
            super.init(web);
            final HttpSecurity http = getHttp();
            web.postBuildAction(() -> {
                FilterSecurityInterceptor securityInterceptor = http.getSharedObject(.class);
                org.springframework.security.config.annotation.web.builders.UseExpressionsConfig.this.filterInvocationSecurityMetadataSourceType = securityInterceptor.getSecurityMetadataSource().getClass();
            });
        }
    }

    // http@use-expressions=false
    @Test
    public void configureWhenUseExpressionsDisabledThenDefaultSecurityMetadataSource() throws Exception {
        this.spring.register(NamespaceHttpTests.DisableUseExpressionsConfig.class).autowire();
        NamespaceHttpTests.DisableUseExpressionsConfig config = this.spring.getContext().getBean(NamespaceHttpTests.DisableUseExpressionsConfig.class);
        assertThat(DefaultFilterInvocationSecurityMetadataSource.class).isAssignableFrom(config.filterInvocationSecurityMetadataSourceType);
    }

    @EnableWebSecurity
    static class DisableUseExpressionsConfig extends WebSecurityConfigurerAdapter {
        private Class<? extends FilterInvocationSecurityMetadataSource> filterInvocationSecurityMetadataSourceType;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.apply(new org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer(getApplicationContext())).getRegistry().antMatchers("/users**", "/sessions/**").hasRole("USER").antMatchers("/signup").hasRole("ANONYMOUS").anyRequest().hasRole("USER");
        }

        @Override
        public void init(final WebSecurity web) throws Exception {
            super.init(web);
            final HttpSecurity http = getHttp();
            web.postBuildAction(() -> {
                FilterSecurityInterceptor securityInterceptor = http.getSharedObject(.class);
                org.springframework.security.config.annotation.web.builders.DisableUseExpressionsConfig.this.filterInvocationSecurityMetadataSourceType = securityInterceptor.getSecurityMetadataSource().getClass();
            });
        }
    }
}

