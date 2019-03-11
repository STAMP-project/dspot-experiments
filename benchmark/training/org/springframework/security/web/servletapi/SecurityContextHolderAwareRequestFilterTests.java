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
package org.springframework.security.web.servletapi;


import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ClassUtils;


/**
 * Tests {@link SecurityContextHolderAwareRequestFilter}.
 *
 * @author Ben Alex
 * @author Rob Winch
 * @author Edd? Mel?ndez
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassUtils.class)
public class SecurityContextHolderAwareRequestFilterTests {
    @Captor
    private ArgumentCaptor<HttpServletRequest> requestCaptor;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private LogoutHandler logoutHandler;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private List<LogoutHandler> logoutHandlers;

    private SecurityContextHolderAwareRequestFilter filter;

    // ~ Methods
    // ========================================================================================================
    @Test
    public void expectedRequestWrapperClassIsUsed() throws Exception {
        this.filter.setRolePrefix("ROLE_");
        this.filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), this.filterChain);
        // Now re-execute the filter, ensuring our replacement wrapper is still used
        this.filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), this.filterChain);
        Mockito.verify(this.filterChain, Mockito.times(2)).doFilter(ArgumentMatchers.any(SecurityContextHolderAwareRequestWrapper.class), ArgumentMatchers.any(HttpServletResponse.class));
        this.filter.destroy();
    }

    @Test
    public void authenticateFalse() throws Exception {
        assertThat(wrappedRequest().authenticate(this.response)).isFalse();
        Mockito.verify(this.authenticationEntryPoint).commence(ArgumentMatchers.eq(this.requestCaptor.getValue()), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(AuthenticationException.class));
        verifyZeroInteractions(this.authenticationManager, this.logoutHandler);
        Mockito.verify(this.request, Mockito.times(0)).authenticate(ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void authenticateTrue() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("test", "password", "ROLE_USER"));
        assertThat(wrappedRequest().authenticate(this.response)).isTrue();
        verifyZeroInteractions(this.authenticationEntryPoint, this.authenticationManager, this.logoutHandler);
        Mockito.verify(this.request, Mockito.times(0)).authenticate(ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void authenticateNullEntryPointFalse() throws Exception {
        this.filter.setAuthenticationEntryPoint(null);
        this.filter.afterPropertiesSet();
        assertThat(wrappedRequest().authenticate(this.response)).isFalse();
        Mockito.verify(this.request).authenticate(this.response);
        verifyZeroInteractions(this.authenticationEntryPoint, this.authenticationManager, this.logoutHandler);
    }

    @Test
    public void authenticateNullEntryPointTrue() throws Exception {
        when(this.request.authenticate(this.response)).thenReturn(true);
        this.filter.setAuthenticationEntryPoint(null);
        this.filter.afterPropertiesSet();
        assertThat(wrappedRequest().authenticate(this.response)).isTrue();
        Mockito.verify(this.request).authenticate(this.response);
        verifyZeroInteractions(this.authenticationEntryPoint, this.authenticationManager, this.logoutHandler);
    }

    @Test
    public void login() throws Exception {
        TestingAuthenticationToken expectedAuth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        when(this.authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class))).thenReturn(expectedAuth);
        wrappedRequest().login(expectedAuth.getName(), String.valueOf(expectedAuth.getCredentials()));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(expectedAuth);
        verifyZeroInteractions(this.authenticationEntryPoint, this.logoutHandler);
        Mockito.verify(this.request, Mockito.times(0)).login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    // SEC-2296
    @Test
    public void loginWithExistingUser() throws Exception {
        TestingAuthenticationToken expectedAuth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        when(this.authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class))).thenReturn(new TestingAuthenticationToken("newuser", "not be found", "ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(expectedAuth);
        try {
            wrappedRequest().login(expectedAuth.getName(), String.valueOf(expectedAuth.getCredentials()));
            fail("Expected Exception");
        } catch (ServletException success) {
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(expectedAuth);
            verifyZeroInteractions(this.authenticationEntryPoint, this.logoutHandler);
            Mockito.verify(this.request, Mockito.times(0)).login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        }
    }

    @Test
    public void loginFail() throws Exception {
        AuthenticationException authException = new BadCredentialsException("Invalid");
        when(this.authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class))).thenThrow(authException);
        try {
            wrappedRequest().login("invalid", "credentials");
            fail("Expected Exception");
        } catch (ServletException success) {
            assertThat(success.getCause()).isEqualTo(authException);
        }
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyZeroInteractions(this.authenticationEntryPoint, this.logoutHandler);
        Mockito.verify(this.request, Mockito.times(0)).login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void loginNullAuthenticationManager() throws Exception {
        this.filter.setAuthenticationManager(null);
        this.filter.afterPropertiesSet();
        String username = "username";
        String password = "password";
        wrappedRequest().login(username, password);
        Mockito.verify(this.request).login(username, password);
        verifyZeroInteractions(this.authenticationEntryPoint, this.authenticationManager, this.logoutHandler);
    }

    @Test
    public void loginNullAuthenticationManagerFail() throws Exception {
        this.filter.setAuthenticationManager(null);
        this.filter.afterPropertiesSet();
        String username = "username";
        String password = "password";
        ServletException authException = new ServletException("Failed Login");
        doThrow(authException).when(this.request).login(username, password);
        try {
            wrappedRequest().login(username, password);
            fail("Expected Exception");
        } catch (ServletException success) {
            assertThat(success).isEqualTo(authException);
        }
        verifyZeroInteractions(this.authenticationEntryPoint, this.authenticationManager, this.logoutHandler);
    }

    @Test
    public void logout() throws Exception {
        TestingAuthenticationToken expectedAuth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(expectedAuth);
        HttpServletRequest wrappedRequest = wrappedRequest();
        wrappedRequest.logout();
        Mockito.verify(this.logoutHandler).logout(wrappedRequest, this.response, expectedAuth);
        verifyZeroInteractions(this.authenticationManager, this.logoutHandler);
        Mockito.verify(this.request, Mockito.times(0)).logout();
    }

    @Test
    public void logoutNullLogoutHandler() throws Exception {
        this.filter.setLogoutHandlers(null);
        this.filter.afterPropertiesSet();
        wrappedRequest().logout();
        Mockito.verify(this.request).logout();
        verifyZeroInteractions(this.authenticationEntryPoint, this.authenticationManager, this.logoutHandler);
    }

    // gh-3780
    @Test
    public void getAsyncContextNullFromSuper() throws Exception {
        assertThat(wrappedRequest().getAsyncContext()).isNull();
    }

    @Test
    public void getAsyncContextStart() throws Exception {
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken expectedAuth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        context.setAuthentication(expectedAuth);
        SecurityContextHolder.setContext(context);
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(this.request.getAsyncContext()).thenReturn(asyncContext);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        wrappedRequest().getAsyncContext().start(runnable);
        verifyZeroInteractions(this.authenticationManager, this.logoutHandler);
        Mockito.verify(asyncContext).start(runnableCaptor.capture());
        DelegatingSecurityContextRunnable wrappedRunnable = ((DelegatingSecurityContextRunnable) (runnableCaptor.getValue()));
        assertThat(ReflectionTestUtils.getField(wrappedRunnable, "delegateSecurityContext")).isEqualTo(context);
        assertThat(ReflectionTestUtils.getField(wrappedRunnable, "delegate"));
    }

    @Test
    public void startAsyncStart() throws Exception {
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken expectedAuth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        context.setAuthentication(expectedAuth);
        SecurityContextHolder.setContext(context);
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(this.request.startAsync()).thenReturn(asyncContext);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        wrappedRequest().startAsync().start(runnable);
        verifyZeroInteractions(this.authenticationManager, this.logoutHandler);
        Mockito.verify(asyncContext).start(runnableCaptor.capture());
        DelegatingSecurityContextRunnable wrappedRunnable = ((DelegatingSecurityContextRunnable) (runnableCaptor.getValue()));
        assertThat(ReflectionTestUtils.getField(wrappedRunnable, "delegateSecurityContext")).isEqualTo(context);
        assertThat(ReflectionTestUtils.getField(wrappedRunnable, "delegate"));
    }

    @Test
    public void startAsyncWithRequestResponseStart() throws Exception {
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken expectedAuth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        context.setAuthentication(expectedAuth);
        SecurityContextHolder.setContext(context);
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(this.request.startAsync(this.request, this.response)).thenReturn(asyncContext);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        wrappedRequest().startAsync(this.request, this.response).start(runnable);
        verifyZeroInteractions(this.authenticationManager, this.logoutHandler);
        Mockito.verify(asyncContext).start(runnableCaptor.capture());
        DelegatingSecurityContextRunnable wrappedRunnable = ((DelegatingSecurityContextRunnable) (runnableCaptor.getValue()));
        assertThat(ReflectionTestUtils.getField(wrappedRunnable, "delegateSecurityContext")).isEqualTo(context);
        assertThat(ReflectionTestUtils.getField(wrappedRunnable, "delegate"));
    }

    // SEC-3047
    @Test
    public void updateRequestFactory() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "password", "PREFIX_USER"));
        this.filter.setRolePrefix("PREFIX_");
        assertThat(wrappedRequest().isUserInRole("PREFIX_USER")).isTrue();
    }
}

