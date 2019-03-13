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
package org.springframework.security.config.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.http.HttpStatus;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.BeanNameCollectingPostProcessor;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.XmlWebApplicationContext;


/**
 *
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public class MiscHttpConfigTests {
    private static final String CONFIG_LOCATION_PREFIX = "classpath:org/springframework/security/config/http/MiscHttpConfigTests";

    @Autowired
    MockMvc mvc;

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Test
    public void configureWhenUsingMinimalConfigurationThenParses() {
        this.spring.configLocations(MiscHttpConfigTests.xml("MinimalConfiguration")).autowire();
    }

    @Test
    public void configureWhenUsingAutoConfigThenSetsUpCorrectFilterList() {
        this.spring.configLocations(MiscHttpConfigTests.xml("AutoConfig")).autowire();
        assertThatFiltersMatchExpectedAutoConfigList();
    }

    @Test
    public void configureWhenUsingSecurityNoneThenNoFiltersAreSetUp() {
        this.spring.configLocations(MiscHttpConfigTests.xml("NoSecurityForPattern")).autowire();
        assertThat(getFilters("/unprotected")).isEmpty();
    }

    @Test
    public void requestWhenUsingDebugFilterAndPatternIsNotConfigureForSecurityThenRespondsOk() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("NoSecurityForPattern")).autowire();
        this.mvc.perform(get("/unprotected")).andExpect(status().isNotFound());
        this.mvc.perform(get("/nomatch")).andExpect(status().isNotFound());
    }

    @Test
    public void requestWhenHttpPatternUsesRegexMatchingThenMatchesAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("RegexSecurityPattern")).autowire();
        this.mvc.perform(get("/protected")).andExpect(status().isUnauthorized());
        this.mvc.perform(get("/unprotected")).andExpect(status().isNotFound());
    }

    @Test
    public void requestWhenHttpPatternUsesCiRegexMatchingThenMatchesAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("CiRegexSecurityPattern")).autowire();
        this.mvc.perform(get("/ProTectEd")).andExpect(status().isUnauthorized());
        this.mvc.perform(get("/UnProTectEd")).andExpect(status().isNotFound());
    }

    @Test
    public void requestWhenHttpPatternUsesCustomRequestMatcherThenMatchesAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("CustomRequestMatcher")).autowire();
        this.mvc.perform(get("/protected")).andExpect(status().isUnauthorized());
        this.mvc.perform(get("/unprotected")).andExpect(status().isNotFound());
    }

    /**
     * SEC-1152
     */
    @Test
    public void requestWhenUsingMinimalConfigurationThenHonorsAnonymousEndpoints() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AnonymousEndpoints")).autowire();
        this.mvc.perform(get("/protected")).andExpect(status().isUnauthorized());
        this.mvc.perform(get("/unprotected")).andExpect(status().isNotFound());
        assertThat(getFilter(AnonymousAuthenticationFilter.class)).isNotNull();
    }

    @Test
    public void requestWhenAnonymousIsDisabledThenRejectsAnonymousEndpoints() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AnonymousDisabled")).autowire();
        this.mvc.perform(get("/protected")).andExpect(status().isUnauthorized());
        this.mvc.perform(get("/unprotected")).andExpect(status().isUnauthorized());
        assertThat(getFilter(AnonymousAuthenticationFilter.class)).isNull();
    }

    @Test
    public void requestWhenAnonymousUsesCustomAttributesThenRespondsWithThoseAttributes() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AnonymousCustomAttributes")).autowire();
        this.mvc.perform(get("/protected").with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(get("/protected")).andExpect(status().isOk()).andExpect(content().string("josh"));
        this.mvc.perform(get("/customKey")).andExpect(status().isOk()).andExpect(content().string(String.valueOf("myCustomKey".hashCode())));
    }

    @Test
    public void requestWhenAnonymousUsesMultipleGrantedAuthoritiesThenRespondsWithThoseAttributes() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AnonymousMultipleAuthorities")).autowire();
        this.mvc.perform(get("/protected").with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(get("/protected")).andExpect(status().isOk()).andExpect(content().string("josh"));
        this.mvc.perform(get("/customKey")).andExpect(status().isOk()).andExpect(content().string(String.valueOf("myCustomKey".hashCode())));
    }

    @Test
    public void requestWhenInterceptUrlMatchesMethodThenSecuresAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("InterceptUrlMethod")).autowire();
        this.mvc.perform(get("/protected").with(httpBasic("user", "password"))).andExpect(status().isOk());
        this.mvc.perform(post("/protected").with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(post("/protected").with(httpBasic("poster", "password"))).andExpect(status().isOk());
        this.mvc.perform(delete("/protected").with(httpBasic("poster", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(delete("/protected").with(httpBasic("admin", "password"))).andExpect(status().isOk());
    }

    @Test
    public void requestWhenInterceptUrlMatchesMethodAndRequiresHttpsThenSecuresAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("InterceptUrlMethodRequiresHttps")).autowire();
        this.mvc.perform(post("/protected").with(csrf())).andExpect(status().isOk());
        this.mvc.perform(get("/protected").secure(true).with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(get("/protected").secure(true).with(httpBasic("admin", "password"))).andExpect(status().isOk());
    }

    @Test
    public void requestWhenInterceptUrlMatchesAnyPatternAndRequiresHttpsThenSecuresAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("InterceptUrlMethodRequiresHttpsAny")).autowire();
        this.mvc.perform(post("/protected").with(csrf())).andExpect(status().isOk());
        this.mvc.perform(get("/protected").secure(true).with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(get("/protected").secure(true).with(httpBasic("admin", "password"))).andExpect(status().isOk());
    }

    @Test
    public void configureWhenOncePerRequestIsFalseThenFilterSecurityInterceptorExercisedForForwards() {
        this.spring.configLocations(MiscHttpConfigTests.xml("OncePerRequest")).autowire();
        FilterSecurityInterceptor filterSecurityInterceptor = getFilter(FilterSecurityInterceptor.class);
        assertThat(filterSecurityInterceptor.isObserveOncePerRequest()).isFalse();
    }

    @Test
    public void requestWhenCustomHttpBasicEntryPointRefThenInvokesOnCommence() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("CustomHttpBasicEntryPointRef")).autowire();
        AuthenticationEntryPoint entryPoint = this.spring.getContext().getBean(AuthenticationEntryPoint.class);
        this.mvc.perform(get("/protected")).andExpect(status().isOk());
        Mockito.verify(entryPoint).commence(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    public void configureWhenInterceptUrlWithRequiresChannelThenAddedChannelFilterToChain() {
        this.spring.configLocations(MiscHttpConfigTests.xml("InterceptUrlMethodRequiresHttpsAny")).autowire();
        assertThat(getFilter(ChannelProcessingFilter.class)).isNotNull();
    }

    @Test
    public void getWhenPortsMappedThenRedirectedAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("PortsMappedInterceptUrlMethodRequiresAny")).autowire();
        this.mvc.perform(get("http://localhost:9080/protected")).andExpect(redirectedUrl("https://localhost:9443/protected"));
    }

    @Test
    public void configureWhenCustomFiltersThenAddedToChainInCorrectOrder() {
        System.setProperty("customFilterRef", "userFilter");
        this.spring.configLocations(MiscHttpConfigTests.xml("CustomFilters")).autowire();
        List<Filter> filters = getFilters("/");
        Class<?> userFilterClass = this.spring.getContext().getBean("userFilter").getClass();
        assertThat(filters).extracting(((Extractor<Filter, Class<?>>) (( filter) -> filter.getClass()))).containsSubsequence(userFilterClass, userFilterClass, SecurityContextPersistenceFilter.class, LogoutFilter.class, userFilterClass);
    }

    @Test
    public void configureWhenTwoFiltersWithSameOrderThenException() {
        assertThatCode(() -> this.spring.configLocations(xml("CollidingFilters")).autowire()).isInstanceOf(BeanDefinitionParsingException.class);
    }

    @Test
    public void configureWhenUsingX509ThenAddsX509FilterCorrectly() {
        this.spring.configLocations(MiscHttpConfigTests.xml("X509")).autowire();
        assertThat(getFilters("/")).extracting(((Extractor<Filter, Class<?>>) (( filter) -> filter.getClass()))).containsSubsequence(CsrfFilter.class, X509AuthenticationFilter.class, ExceptionTranslationFilter.class);
    }

    @Test
    public void getWhenUsingX509AndPropertyPlaceholderThenSubjectPrincipalRegexIsConfigured() throws Exception {
        System.setProperty("subject_principal_regex", "OU=(.*?)(?:,|$)");
        this.spring.configLocations(MiscHttpConfigTests.xml("X509")).autowire();
        this.mvc.perform(get("/protected").with(x509("classpath:org/springframework/security/config/http/MiscHttpConfigTests-certificate.pem"))).andExpect(status().isOk());
    }

    @Test
    public void configureWhenUsingInvalidLogoutSuccessUrlThenThrowsException() {
        assertThatCode(() -> this.spring.configLocations(xml("InvalidLogoutSuccessUrl")).autowire()).isInstanceOf(BeanCreationException.class);
    }

    @Test
    public void logoutWhenSpecifyingCookiesToDeleteThenSetCookieAdded() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("DeleteCookies")).autowire();
        MvcResult result = this.mvc.perform(post("/logout").with(csrf())).andReturn();
        List<String> values = result.getResponse().getHeaders("Set-Cookie");
        assertThat(values.size()).isEqualTo(2);
        assertThat(values).extracting(( value) -> value.split("=")[0]).contains("JSESSIONID", "mycookie");
    }

    @Test
    public void logoutWhenSpecifyingSuccessHandlerRefThenResponseHandledAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("LogoutSuccessHandlerRef")).autowire();
        this.mvc.perform(post("/logout").with(csrf())).andExpect(redirectedUrl("/logoutSuccessEndpoint"));
    }

    @Test
    public void getWhenUnauthenticatedThenUsesConfiguredRequestCache() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("RequestCache")).autowire();
        RequestCache requestCache = this.spring.getContext().getBean(RequestCache.class);
        this.mvc.perform(get("/"));
        Mockito.verify(requestCache).saveRequest(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void getWhenUnauthenticatedThenUsesConfiguredAuthenticationEntryPoint() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("EntryPoint")).autowire();
        AuthenticationEntryPoint entryPoint = this.spring.getContext().getBean(AuthenticationEntryPoint.class);
        this.mvc.perform(get("/"));
        Mockito.verify(entryPoint).commence(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }

    /**
     * See SEC-750. If the http security post processor causes beans to be instantiated too eagerly, they way miss
     * additional processing. In this method we have a UserDetailsService which is referenced from the namespace
     * and also has a post processor registered which will modify it.
     */
    @Test
    public void configureWhenUsingCustomUserDetailsServiceThenBeanPostProcessorsAreStillApplied() {
        this.spring.configLocations(MiscHttpConfigTests.xml("Sec750")).autowire();
        BeanNameCollectingPostProcessor postProcessor = this.spring.getContext().getBean(BeanNameCollectingPostProcessor.class);
        assertThat(postProcessor.getBeforeInitPostProcessedBeans()).contains("authenticationProvider", "userService");
        assertThat(postProcessor.getAfterInitPostProcessedBeans()).contains("authenticationProvider", "userService");
    }

    /* SEC-934 */
    @Test
    public void getWhenUsingTwoIdenticalInterceptUrlsThenTheSecondTakesPrecedence() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("Sec934")).autowire();
        this.mvc.perform(get("/protected").with(httpBasic("user", "password"))).andExpect(status().isOk());
        this.mvc.perform(get("/protected").with(httpBasic("admin", "password"))).andExpect(status().isForbidden());
    }

    @Test
    public void getWhenAuthenticatingThenConsultsCustomSecurityContextRepository() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("SecurityContextRepository")).autowire();
        SecurityContextRepository repository = this.spring.getContext().getBean(SecurityContextRepository.class);
        SecurityContext context = new org.springframework.security.core.context.SecurityContextImpl(new TestingAuthenticationToken("user", "password"));
        Mockito.when(repository.loadContext(ArgumentMatchers.any(HttpRequestResponseHolder.class))).thenReturn(context);
        MvcResult result = this.mvc.perform(get("/protected").with(httpBasic("user", "password"))).andExpect(status().isOk()).andReturn();
        assertThat(result.getRequest().getSession(false)).isNotNull();
        Mockito.verify(repository, Mockito.atLeastOnce()).saveContext(ArgumentMatchers.any(SecurityContext.class), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void getWhenUsingInterceptUrlExpressionsThenAuthorizesAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("InterceptUrlExpressions")).autowire();
        this.mvc.perform(get("/protected").with(httpBasic("admin", "password"))).andExpect(status().isOk());
        this.mvc.perform(get("/protected").with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        this.mvc.perform(get("/unprotected").with(httpBasic("user", "password"))).andExpect(status().isOk());
    }

    @Test
    public void getWhenUsingCustomExpressionHandlerThenAuthorizesAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("ExpressionHandler")).autowire();
        PermissionEvaluator permissionEvaluator = this.spring.getContext().getBean(PermissionEvaluator.class);
        Mockito.when(permissionEvaluator.hasPermission(ArgumentMatchers.any(Authentication.class), ArgumentMatchers.any(Object.class), ArgumentMatchers.any(Object.class))).thenReturn(false);
        this.mvc.perform(get("/").with(httpBasic("user", "password"))).andExpect(status().isForbidden());
        Mockito.verify(permissionEvaluator).hasPermission(ArgumentMatchers.any(Authentication.class), ArgumentMatchers.any(Object.class), ArgumentMatchers.any(Object.class));
    }

    @Test
    public void configureWhenProtectingLoginPageThenWarningLogged() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        redirectLogsTo(baos, DefaultFilterChainValidator.class);
        this.spring.configLocations(MiscHttpConfigTests.xml("ProtectedLoginPage")).autowire();
        assertThat(baos.toString()).contains("[WARN]");
    }

    @Test
    public void configureWhenUsingDisableUrlRewritingThenRedirectIsNotEncodedByResponse() throws IOException, ServletException {
        this.spring.configLocations(MiscHttpConfigTests.xml("DisableUrlRewriting")).autowire();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChainProxy proxy = this.spring.getContext().getBean(FilterChainProxy.class);
        proxy.doFilter(request, new MiscHttpConfigTests.EncodeUrlDenyingHttpServletResponseWrapper(response), ( req, resp) -> {
        });
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost/login");
    }

    @Test
    public void configureWhenUserDetailsServiceInParentContextThenLocatesSuccessfully() {
        assertThatCode(() -> this.spring.configLocations(this.xml("MissingUserDetailsService")).autowire()).isInstanceOf(BeansException.class);
        try (XmlWebApplicationContext parent = new XmlWebApplicationContext()) {
            parent.setConfigLocations(this.xml("AutoConfig"));
            parent.refresh();
            try (XmlWebApplicationContext child = new XmlWebApplicationContext()) {
                child.setParent(parent);
                child.setConfigLocation(this.xml("MissingUserDetailsService"));
                child.refresh();
            }
        }
    }

    @Test
    public void loginWhenConfiguredWithNoInternalAuthenticationProvidersThenSuccessfullyAuthenticates() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("NoInternalAuthenticationProviders")).autowire();
        this.mvc.perform(post("/login").param("username", "user").param("password", "password")).andExpect(redirectedUrl("/"));
    }

    @Test
    public void loginWhenUsingDefaultsThenErasesCredentialsAfterAuthentication() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("HttpBasic")).autowire();
        this.mvc.perform(get("/password").with(httpBasic("user", "password"))).andExpect(content().string(""));
    }

    @Test
    public void loginWhenAuthenticationManagerConfiguredToEraseCredentialsThenErasesCredentialsAfterAuthentication() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AuthenticationManagerEraseCredentials")).autowire();
        this.mvc.perform(get("/password").with(httpBasic("user", "password"))).andExpect(content().string(""));
    }

    /**
     * SEC-2020
     */
    @Test
    public void loginWhenAuthenticationManagerRefConfiguredToKeepCredentialsThenKeepsCredentialsAfterAuthentication() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AuthenticationManagerRefKeepCredentials")).autowire();
        this.mvc.perform(get("/password").with(httpBasic("user", "password"))).andExpect(content().string("password"));
    }

    @Test
    public void loginWhenAuthenticationManagerRefIsNotAProviderManagerThenKeepsCredentialsAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("AuthenticationManagerRefNotProviderManager")).autowire();
        this.mvc.perform(get("/password").with(httpBasic("user", "password"))).andExpect(content().string("password"));
    }

    @Test
    public void loginWhenJeeFilterThenExtractsRoles() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("JeeFilter")).autowire();
        Principal user = Mockito.mock(Principal.class);
        Mockito.when(user.getName()).thenReturn("joe");
        this.mvc.perform(get("/roles").principal(user).with(( request) -> {
            request.addUserRole("admin");
            request.addUserRole("user");
            request.addUserRole("unmapped");
            return request;
        })).andExpect(content().string("ROLE_admin,ROLE_user"));
    }

    @Test
    public void loginWhenUsingCustomAuthenticationDetailsSourceRefThenAuthenticationSourcesDetailsAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("CustomAuthenticationDetailsSourceRef")).autowire();
        Object details = Mockito.mock(Object.class);
        AuthenticationDetailsSource source = this.spring.getContext().getBean(AuthenticationDetailsSource.class);
        Mockito.when(source.buildDetails(ArgumentMatchers.any(Object.class))).thenReturn(details);
        this.mvc.perform(get("/details").with(httpBasic("user", "password"))).andExpect(content().string(details.getClass().getName()));
        this.mvc.perform(get("/details").with(x509("classpath:org/springframework/security/config/http/MiscHttpConfigTests-certificate.pem"))).andExpect(content().string(details.getClass().getName()));
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(post("/login").param("username", "user").param("password", "password").with(csrf())).andReturn().getRequest().getSession(false)));
        this.mvc.perform(get("/details").session(session)).andExpect(content().string(details.getClass().getName()));
        assertThat(getField(getFilter(OpenIDAuthenticationFilter.class), "authenticationDetailsSource")).isEqualTo(source);
    }

    @Test
    public void loginWhenUsingJaasApiProvisionThenJaasSubjectContainsUsername() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("Jaas")).autowire();
        AuthorityGranter granter = this.spring.getContext().getBean(AuthorityGranter.class);
        Mockito.when(granter.grant(ArgumentMatchers.any(Principal.class))).thenReturn(new HashSet(Arrays.asList("USER")));
        this.mvc.perform(get("/username").with(httpBasic("user", "password"))).andExpect(content().string("user"));
    }

    @Test
    public void getWhenUsingCustomHttpFirewallThenFirewallIsInvoked() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("HttpFirewall")).autowire();
        FirewalledRequest request = new FirewalledRequest(new MockHttpServletRequest()) {
            @Override
            public void reset() {
            }
        };
        HttpServletResponse response = new MockHttpServletResponse();
        HttpFirewall firewall = this.spring.getContext().getBean(HttpFirewall.class);
        Mockito.when(firewall.getFirewalledRequest(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(request);
        Mockito.when(firewall.getFirewalledResponse(ArgumentMatchers.any(HttpServletResponse.class))).thenReturn(response);
        this.mvc.perform(get("/unprotected"));
        Mockito.verify(firewall).getFirewalledRequest(ArgumentMatchers.any(HttpServletRequest.class));
        Mockito.verify(firewall).getFirewalledResponse(ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void getWhenUsingCustomAccessDecisionManagerThenAuthorizesAccordingly() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("CustomAccessDecisionManager")).autowire();
        this.mvc.perform(get("/unprotected").with(httpBasic("user", "password"))).andExpect(status().isForbidden());
    }

    /**
     * SEC-1893
     */
    @Test
    public void authenticateWhenUsingPortMapperThenRedirectsAppropriately() throws Exception {
        this.spring.configLocations(MiscHttpConfigTests.xml("PortsMappedRequiresHttps")).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("https://localhost:9080/protected")).andExpect(redirectedUrl("https://localhost:9443/login")).andReturn().getRequest().getSession(false)));
        session = ((MockHttpSession) (this.mvc.perform(post("/login").param("username", "user").param("password", "password").session(session).with(csrf())).andExpect(redirectedUrl("https://localhost:9443/protected")).andReturn().getRequest().getSession(false)));
        this.mvc.perform(get("http://localhost:9080/protected").session(session)).andExpect(redirectedUrl("https://localhost:9443/protected"));
    }

    @RestController
    static class BasicController {
        @RequestMapping("/unprotected")
        public String unprotected() {
            return "ok";
        }

        @RequestMapping("/protected")
        public String protectedMethod(@AuthenticationPrincipal
        String name) {
            return name;
        }
    }

    @RestController
    static class CustomKeyController {
        @GetMapping("/customKey")
        public String customKey() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if ((authentication != null) && (authentication instanceof AnonymousAuthenticationToken)) {
                return String.valueOf(getKeyHash());
            }
            return null;
        }
    }

    @RestController
    static class AuthenticationController {
        @GetMapping("/password")
        public String password(@AuthenticationPrincipal
        Authentication authentication) {
            return ((String) (authentication.getCredentials()));
        }

        @GetMapping("/roles")
        public String roles(@AuthenticationPrincipal
        Authentication authentication) {
            return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        }

        @GetMapping("/details")
        public String details(@AuthenticationPrincipal
        Authentication authentication) {
            return authentication.getDetails().getClass().getName();
        }
    }

    @RestController
    static class JaasController {
        @GetMapping("/username")
        public String username() {
            Subject subject = Subject.getSubject(AccessController.getContext());
            return subject.getPrincipals().iterator().next().getName();
        }
    }

    public static class JaasLoginModule implements LoginModule {
        private Subject subject;

        @Override
        public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
            this.subject = subject;
        }

        @Override
        public boolean login() throws LoginException {
            return this.subject.getPrincipals().add(() -> "user");
        }

        @Override
        public boolean commit() throws LoginException {
            return true;
        }

        @Override
        public boolean abort() throws LoginException {
            return true;
        }

        @Override
        public boolean logout() throws LoginException {
            return true;
        }
    }

    static class MockAccessDecisionManager implements AccessDecisionManager {
        @Override
        public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
            throw new AccessDeniedException("teapot");
        }

        @Override
        public boolean supports(ConfigAttribute attribute) {
            return true;
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }
    }

    static class MockAuthenticationManager implements AuthenticationManager {
        public Authentication authenticate(Authentication authentication) {
            return new TestingAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        }
    }

    static class EncodeUrlDenyingHttpServletResponseWrapper extends HttpServletResponseWrapper {
        public EncodeUrlDenyingHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public String encodeURL(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }

        @Override
        public String encodeRedirectURL(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }

        @Override
        public String encodeUrl(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }

        @Override
        public String encodeRedirectUrl(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }
    }
}

