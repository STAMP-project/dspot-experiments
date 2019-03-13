/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.web.authentication;


import HttpServletResponse.SC_UNAUTHORIZED;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServicesTests;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests {@link AbstractAuthenticationProcessingFilter}.
 *
 * @author Ben Alex
 * @author Luke Taylor
 * @author Rob Winch
 */
@SuppressWarnings("deprecation")
public class AbstractAuthenticationProcessingFilterTests {
    SavedRequestAwareAuthenticationSuccessHandler successHandler;

    SimpleUrlAuthenticationFailureHandler failureHandler;

    @Test
    public void testDefaultProcessesFilterUrlMatchesWithPathParameter() {
        MockHttpServletRequest request = createMockAuthenticationRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter();
        setFilterProcessesUrl("/login");
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        request.setServletPath("/login;jsessionid=I8MIONOSTHOR");
        // the firewall ensures that path parameters are ignored
        HttpServletRequest firewallRequest = firewall.getFirewalledRequest(request);
        assertThat(filter.requiresAuthentication(firewallRequest, response)).isTrue();
    }

    @Test
    public void testFilterProcessesUrlVariationsRespected() throws Exception {
        // Setup our HTTP request
        MockHttpServletRequest request = createMockAuthenticationRequest();
        request.setServletPath("/j_OTHER_LOCATION");
        request.setRequestURI("/mycontext/j_OTHER_LOCATION");
        // Setup our filter configuration
        MockFilterConfig config = new MockFilterConfig(null, null);
        // Setup our expectation that the filter chain will not be invoked, as we redirect
        // to defaultTargetUrl
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Setup our test object, to grant access
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(true);
        setFilterProcessesUrl("/j_OTHER_LOCATION");
        filter.setAuthenticationSuccessHandler(successHandler);
        // Test
        filter.doFilter(request, response, chain);
        assertThat(response.getRedirectedUrl()).isEqualTo("/mycontext/logged_in.jsp");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).isEqualTo("test");
    }

    @Test
    public void testGettersSetters() throws Exception {
        AbstractAuthenticationProcessingFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter();
        filter.setAuthenticationManager(Mockito.mock(AuthenticationManager.class));
        filter.setFilterProcessesUrl("/p");
        filter.afterPropertiesSet();
        assertThat(filter.getRememberMeServices()).isNotNull();
        filter.setRememberMeServices(new TokenBasedRememberMeServices("key", new AbstractRememberMeServicesTests.MockUserDetailsService()));
        assertThat(filter.getRememberMeServices().getClass()).isEqualTo(TokenBasedRememberMeServices.class);
        assertThat(((filter.getAuthenticationManager()) != null)).isTrue();
    }

    @Test
    public void testIgnoresAnyServletPathOtherThanFilterProcessesUrl() throws Exception {
        // Setup our HTTP request
        MockHttpServletRequest request = createMockAuthenticationRequest();
        request.setServletPath("/some.file.html");
        request.setRequestURI("/mycontext/some.file.html");
        // Setup our filter configuration
        MockFilterConfig config = new MockFilterConfig(null, null);
        // Setup our expectation that the filter chain will be invoked, as our request is
        // for a page the filter isn't monitoring
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Setup our test object, to deny access
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(false);
        // Test
        filter.doFilter(request, response, chain);
    }

    @Test
    public void testNormalOperationWithDefaultFilterProcessesUrl() throws Exception {
        // Setup our HTTP request
        MockHttpServletRequest request = createMockAuthenticationRequest();
        HttpSession sessionPreAuth = request.getSession();
        // Setup our filter configuration
        MockFilterConfig config = new MockFilterConfig(null, null);
        // Setup our expectation that the filter chain will not be invoked, as we redirect
        // to defaultTargetUrl
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Setup our test object, to grant access
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(true);
        setFilterProcessesUrl("/j_mock_post");
        setSessionAuthenticationStrategy(Mockito.mock(SessionAuthenticationStrategy.class));
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        setAuthenticationManager(Mockito.mock(AuthenticationManager.class));
        afterPropertiesSet();
        // Test
        filter.doFilter(request, response, chain);
        assertThat(response.getRedirectedUrl()).isEqualTo("/mycontext/logged_in.jsp");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).isEqualTo("test");
        // Should still have the same session
        assertThat(request.getSession()).isEqualTo(sessionPreAuth);
    }

    @Test
    public void testStartupDetectsInvalidAuthenticationManager() throws Exception {
        AbstractAuthenticationProcessingFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter();
        filter.setAuthenticationFailureHandler(failureHandler);
        successHandler.setDefaultTargetUrl("/");
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setFilterProcessesUrl("/login");
        try {
            filter.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage()).isEqualTo("authenticationManager must be specified");
        }
    }

    @Test
    public void testStartupDetectsInvalidFilterProcessesUrl() throws Exception {
        AbstractAuthenticationProcessingFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter();
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.setAuthenticationManager(Mockito.mock(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(successHandler);
        try {
            filter.setFilterProcessesUrl(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage()).isEqualTo("Pattern cannot be null or empty");
        }
    }

    @Test
    public void testSuccessLoginThenFailureLoginResultsInSessionLosingToken() throws Exception {
        // Setup our HTTP request
        MockHttpServletRequest request = createMockAuthenticationRequest();
        // Setup our filter configuration
        MockFilterConfig config = new MockFilterConfig(null, null);
        // Setup our expectation that the filter chain will not be invoked, as we redirect
        // to defaultTargetUrl
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Setup our test object, to grant access
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(true);
        setFilterProcessesUrl("/j_mock_post");
        filter.setAuthenticationSuccessHandler(successHandler);
        // Test
        filter.doFilter(request, response, chain);
        assertThat(response.getRedirectedUrl()).isEqualTo("/mycontext/logged_in.jsp");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).isEqualTo("test");
        // Now try again but this time have filter deny access
        // Setup our HTTP request
        // Setup our expectation that the filter chain will not be invoked, as we redirect
        // to authenticationFailureUrl
        chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(false);
        response = new MockHttpServletResponse();
        // Setup our test object, to deny access
        filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(false);
        setFilterProcessesUrl("/j_mock_post");
        filter.setAuthenticationFailureHandler(failureHandler);
        // Test
        filter.doFilter(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void testSuccessfulAuthenticationInvokesSuccessHandlerAndSetsContext() throws Exception {
        // Setup our HTTP request
        MockHttpServletRequest request = createMockAuthenticationRequest();
        // Setup our filter configuration
        MockFilterConfig config = new MockFilterConfig(null, null);
        // Setup our expectation that the filter chain will be invoked, as we want to go
        // to the location requested in the session
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Setup our test object, to grant access
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(true);
        setFilterProcessesUrl("/j_mock_post");
        AuthenticationSuccessHandler successHandler = Mockito.mock(AuthenticationSuccessHandler.class);
        filter.setAuthenticationSuccessHandler(successHandler);
        // Test
        filter.doFilter(request, response, chain);
        Mockito.verify(successHandler).onAuthenticationSuccess(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    public void testFailedAuthenticationInvokesFailureHandler() throws Exception {
        // Setup our HTTP request
        MockHttpServletRequest request = createMockAuthenticationRequest();
        // Setup our filter configuration
        MockFilterConfig config = new MockFilterConfig(null, null);
        // Setup our expectation that the filter chain will not be invoked, as we redirect
        // to authenticationFailureUrl
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Setup our test object, to deny access
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(false);
        AuthenticationFailureHandler failureHandler = Mockito.mock(AuthenticationFailureHandler.class);
        filter.setAuthenticationFailureHandler(failureHandler);
        // Test
        filter.doFilter(request, response, chain);
        Mockito.verify(failureHandler).onAuthenticationFailure(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * SEC-571
     */
    @Test
    public void testNoSessionIsCreatedIfAllowSessionCreationIsFalse() throws Exception {
        MockHttpServletRequest request = createMockAuthenticationRequest();
        MockFilterConfig config = new MockFilterConfig(null, null);
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Reject authentication, so exception would normally be stored in session
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(false);
        failureHandler.setAllowSessionCreation(false);
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.doFilter(request, response, chain);
        assertThat(request.getSession(false)).isNull();
    }

    /**
     * SEC-462
     */
    @Test
    public void testLoginErrorWithNoFailureUrlSendsUnauthorizedStatus() throws Exception {
        MockHttpServletRequest request = createMockAuthenticationRequest();
        MockFilterConfig config = new MockFilterConfig(null, null);
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(false);
        successHandler.setDefaultTargetUrl("http://monkeymachine.co.uk/");
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    /**
     * SEC-1919
     */
    @Test
    public void loginErrorWithInternAuthenticationServiceExceptionLogsError() throws Exception {
        MockHttpServletRequest request = createMockAuthenticationRequest();
        AbstractAuthenticationProcessingFilterTests.MockFilterChain chain = new AbstractAuthenticationProcessingFilterTests.MockFilterChain(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Log logger = Mockito.mock(Log.class);
        AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter(false);
        ReflectionTestUtils.setField(filter, "logger", logger);
        filter.exceptionToThrow = new InternalAuthenticationServiceException("Mock requested to do so");
        successHandler.setDefaultTargetUrl("http://monkeymachine.co.uk/");
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.doFilter(request, response, chain);
        Mockito.verify(logger).error(ArgumentMatchers.anyString(), ArgumentMatchers.eq(filter.exceptionToThrow));
        assertThat(response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    /**
     * https://github.com/spring-projects/spring-security/pull/3905
     */
    @Test(expected = IllegalArgumentException.class)
    public void setRememberMeServicesShouldntAllowNulls() {
        AbstractAuthenticationProcessingFilter filter = new AbstractAuthenticationProcessingFilterTests.MockAuthenticationFilter();
        filter.setRememberMeServices(null);
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
        private AuthenticationException exceptionToThrow;

        private boolean grantAccess;

        public MockAuthenticationFilter(boolean grantAccess) {
            this();
            setRememberMeServices(new NullRememberMeServices());
            this.grantAccess = grantAccess;
            this.exceptionToThrow = new BadCredentialsException("Mock requested to do so");
        }

        private MockAuthenticationFilter() {
            super("/j_mock_post");
        }

        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
            if (grantAccess) {
                return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test", "test", AuthorityUtils.createAuthorityList("TEST"));
            } else {
                throw exceptionToThrow;
            }
        }
    }

    private class MockFilterChain implements FilterChain {
        private boolean expectToProceed;

        public MockFilterChain(boolean expectToProceed) {
            this.expectToProceed = expectToProceed;
        }

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (expectToProceed) {
            } else {
                fail("Did not expect filter chain to proceed");
            }
        }
    }
}

