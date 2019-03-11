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
package org.springframework.security.access.intercept.aopalliance;


import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.ITargetObject;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Tests {@link MethodSecurityInterceptor}.
 *
 * @author Ben Alex
 * @author Rob Winch
 */
@SuppressWarnings("unchecked")
public class MethodSecurityInterceptorTests {
    private TestingAuthenticationToken token;

    private MethodSecurityInterceptor interceptor;

    private ITargetObject realTarget;

    private ITargetObject advisedTarget;

    private AccessDecisionManager adm;

    private MethodSecurityMetadataSource mds;

    private AuthenticationManager authman;

    private ApplicationEventPublisher eventPublisher;

    @Test
    public void gettersReturnExpectedData() {
        RunAsManager runAs = Mockito.mock(RunAsManager.class);
        AfterInvocationManager aim = Mockito.mock(AfterInvocationManager.class);
        interceptor.setRunAsManager(runAs);
        interceptor.setAfterInvocationManager(aim);
        assertThat(interceptor.getAccessDecisionManager()).isEqualTo(adm);
        assertThat(interceptor.getRunAsManager()).isEqualTo(runAs);
        assertThat(interceptor.getAuthenticationManager()).isEqualTo(authman);
        assertThat(interceptor.getSecurityMetadataSource()).isEqualTo(mds);
        assertThat(interceptor.getAfterInvocationManager()).isEqualTo(aim);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingAccessDecisionManagerIsDetected() throws Exception {
        interceptor.setAccessDecisionManager(null);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingAuthenticationManagerIsDetected() throws Exception {
        interceptor.setAuthenticationManager(null);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingMethodSecurityMetadataSourceIsRejected() throws Exception {
        interceptor.setSecurityMetadataSource(null);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingRunAsManagerIsRejected() throws Exception {
        interceptor.setRunAsManager(null);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationRejectsSecurityMetadataSourceThatDoesNotSupportMethodInvocation() throws Throwable {
        Mockito.when(mds.supports(MethodInvocation.class)).thenReturn(false);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationRejectsAccessDecisionManagerThatDoesNotSupportMethodInvocation() throws Exception {
        Mockito.when(mds.supports(MethodInvocation.class)).thenReturn(true);
        Mockito.when(adm.supports(MethodInvocation.class)).thenReturn(false);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void intitalizationRejectsRunAsManagerThatDoesNotSupportMethodInvocation() throws Exception {
        final RunAsManager ram = Mockito.mock(RunAsManager.class);
        Mockito.when(ram.supports(MethodInvocation.class)).thenReturn(false);
        interceptor.setRunAsManager(ram);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void intitalizationRejectsAfterInvocationManagerThatDoesNotSupportMethodInvocation() throws Exception {
        final AfterInvocationManager aim = Mockito.mock(AfterInvocationManager.class);
        Mockito.when(aim.supports(MethodInvocation.class)).thenReturn(false);
        interceptor.setAfterInvocationManager(aim);
        interceptor.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationFailsIfAccessDecisionManagerRejectsConfigAttributes() throws Exception {
        Mockito.when(adm.supports(ArgumentMatchers.any(ConfigAttribute.class))).thenReturn(false);
        interceptor.afterPropertiesSet();
    }

    @Test
    public void validationNotAttemptedIfIsValidateConfigAttributesSetToFalse() throws Exception {
        Mockito.when(adm.supports(MethodInvocation.class)).thenReturn(true);
        Mockito.when(mds.supports(MethodInvocation.class)).thenReturn(true);
        interceptor.setValidateConfigAttributes(false);
        interceptor.afterPropertiesSet();
        Mockito.verify(mds, Mockito.never()).getAllConfigAttributes();
        Mockito.verify(adm, Mockito.never()).supports(ArgumentMatchers.any(ConfigAttribute.class));
    }

    @Test
    public void validationNotAttemptedIfMethodSecurityMetadataSourceReturnsNullForAttributes() throws Exception {
        Mockito.when(adm.supports(MethodInvocation.class)).thenReturn(true);
        Mockito.when(mds.supports(MethodInvocation.class)).thenReturn(true);
        Mockito.when(mds.getAllConfigAttributes()).thenReturn(null);
        interceptor.setValidateConfigAttributes(true);
        interceptor.afterPropertiesSet();
        Mockito.verify(adm, Mockito.never()).supports(ArgumentMatchers.any(ConfigAttribute.class));
    }

    @Test
    public void callingAPublicMethodFacadeWillNotRepeatSecurityChecksWhenPassedToTheSecuredMethodItFronts() {
        mdsReturnsNull();
        String result = advisedTarget.publicMakeLowerCase("HELLO");
        assertThat(result).isEqualTo("hello Authentication empty");
    }

    @Test
    public void callingAPublicMethodWhenPresentingAnAuthenticationObjectDoesntChangeItsAuthenticatedProperty() {
        mdsReturnsNull();
        SecurityContextHolder.getContext().setAuthentication(token);
        assertThat(advisedTarget.publicMakeLowerCase("HELLO")).isEqualTo("hello org.springframework.security.authentication.TestingAuthenticationToken false");
        assertThat((!(token.isAuthenticated()))).isTrue();
    }

    @Test(expected = AuthenticationException.class)
    public void callIsntMadeWhenAuthenticationManagerRejectsAuthentication() throws Exception {
        final TestingAuthenticationToken token = new TestingAuthenticationToken("Test", "Password");
        SecurityContextHolder.getContext().setAuthentication(token);
        mdsReturnsUserRole();
        Mockito.when(authman.authenticate(token)).thenThrow(new BadCredentialsException("rejected"));
        advisedTarget.makeLowerCase("HELLO");
    }

    @Test
    public void callSucceedsIfAccessDecisionManagerGrantsAccess() throws Exception {
        token.setAuthenticated(true);
        interceptor.setPublishAuthorizationSuccess(true);
        SecurityContextHolder.getContext().setAuthentication(token);
        mdsReturnsUserRole();
        String result = advisedTarget.makeLowerCase("HELLO");
        // Note we check the isAuthenticated remained true in following line
        assertThat(result).isEqualTo("hello org.springframework.security.authentication.TestingAuthenticationToken true");
        Mockito.verify(eventPublisher).publishEvent(ArgumentMatchers.any(AuthorizedEvent.class));
    }

    @Test
    public void callIsntMadeWhenAccessDecisionManagerRejectsAccess() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(token);
        // Use mocked target to make sure invocation doesn't happen (not in expectations
        // so test would fail)
        createTarget(true);
        mdsReturnsUserRole();
        Mockito.when(authman.authenticate(token)).thenReturn(token);
        Mockito.doThrow(new AccessDeniedException("rejected")).when(adm).decide(ArgumentMatchers.any(Authentication.class), ArgumentMatchers.any(MethodInvocation.class), ArgumentMatchers.any(List.class));
        try {
            advisedTarget.makeUpperCase("HELLO");
            fail("Expected Exception");
        } catch (AccessDeniedException expected) {
        }
        Mockito.verify(eventPublisher).publishEvent(ArgumentMatchers.any(AuthorizationFailureEvent.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullSecuredObjects() throws Throwable {
        interceptor.invoke(null);
    }

    @Test
    public void runAsReplacementIsCorrectlySet() throws Exception {
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);
        token.setAuthenticated(true);
        final RunAsManager runAs = Mockito.mock(RunAsManager.class);
        final RunAsUserToken runAsToken = new RunAsUserToken("key", "someone", "creds", token.getAuthorities(), TestingAuthenticationToken.class);
        interceptor.setRunAsManager(runAs);
        mdsReturnsUserRole();
        Mockito.when(runAs.buildRunAs(ArgumentMatchers.eq(token), ArgumentMatchers.any(MethodInvocation.class), ArgumentMatchers.any(List.class))).thenReturn(runAsToken);
        String result = advisedTarget.makeUpperCase("hello");
        assertThat(result).isEqualTo("HELLO org.springframework.security.access.intercept.RunAsUserToken true");
        // Check we've changed back
        assertThat(SecurityContextHolder.getContext()).isSameAs(ctx);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(token);
    }

    // SEC-1967
    @Test
    public void runAsReplacementCleansAfterException() throws Exception {
        createTarget(true);
        Mockito.when(realTarget.makeUpperCase(ArgumentMatchers.anyString())).thenThrow(new RuntimeException());
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);
        token.setAuthenticated(true);
        final RunAsManager runAs = Mockito.mock(RunAsManager.class);
        final RunAsUserToken runAsToken = new RunAsUserToken("key", "someone", "creds", token.getAuthorities(), TestingAuthenticationToken.class);
        interceptor.setRunAsManager(runAs);
        mdsReturnsUserRole();
        Mockito.when(runAs.buildRunAs(ArgumentMatchers.eq(token), ArgumentMatchers.any(MethodInvocation.class), ArgumentMatchers.any(List.class))).thenReturn(runAsToken);
        try {
            advisedTarget.makeUpperCase("hello");
            fail("Expected Exception");
        } catch (RuntimeException success) {
        }
        // Check we've changed back
        assertThat(SecurityContextHolder.getContext()).isSameAs(ctx);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(token);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void emptySecurityContextIsRejected() throws Exception {
        mdsReturnsUserRole();
        advisedTarget.makeUpperCase("hello");
    }

    @Test
    public void afterInvocationManagerIsNotInvokedIfExceptionIsRaised() throws Throwable {
        MethodInvocation mi = Mockito.mock(MethodInvocation.class);
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);
        mdsReturnsUserRole();
        AfterInvocationManager aim = Mockito.mock(AfterInvocationManager.class);
        interceptor.setAfterInvocationManager(aim);
        Mockito.when(mi.proceed()).thenThrow(new Throwable());
        try {
            interceptor.invoke(mi);
            fail("Expected exception");
        } catch (Throwable expected) {
        }
        Mockito.verifyZeroInteractions(aim);
    }
}

