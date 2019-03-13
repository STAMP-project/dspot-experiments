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
package org.springframework.security.access.intercept.aspectj;


import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.TargetObject;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;


/**
 * Tests {@link AspectJMethodSecurityInterceptor}.
 *
 * @author Ben Alex
 * @author Luke Taylor
 * @author Rob Winch
 */
public class AspectJMethodSecurityInterceptorTests {
    private TestingAuthenticationToken token;

    private AspectJMethodSecurityInterceptor interceptor;

    @Mock
    private AccessDecisionManager adm;

    @Mock
    private MethodSecurityMetadataSource mds;

    @Mock
    private AuthenticationManager authman;

    @Mock
    private AspectJCallback aspectJCallback;

    private ProceedingJoinPoint joinPoint;

    @Test
    public void callbackIsInvokedWhenPermissionGranted() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(token);
        interceptor.invoke(joinPoint, aspectJCallback);
        Mockito.verify(aspectJCallback).proceedWithObject();
        // Just try the other method too
        interceptor.invoke(joinPoint);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void callbackIsNotInvokedWhenPermissionDenied() throws Exception {
        Mockito.doThrow(new AccessDeniedException("denied")).when(adm).decide(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        SecurityContextHolder.getContext().setAuthentication(token);
        try {
            interceptor.invoke(joinPoint, aspectJCallback);
            fail("Expected AccessDeniedException");
        } catch (AccessDeniedException expected) {
        }
        Mockito.verify(aspectJCallback, Mockito.never()).proceedWithObject();
    }

    @Test
    public void adapterHoldsCorrectData() throws Exception {
        TargetObject to = new TargetObject();
        Method m = ClassUtils.getMethodIfAvailable(TargetObject.class, "countLength", new Class[]{ String.class });
        Mockito.when(joinPoint.getTarget()).thenReturn(to);
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{ "Hi" });
        MethodInvocationAdapter mia = new MethodInvocationAdapter(joinPoint);
        assertThat(mia.getArguments()[0]).isEqualTo("Hi");
        assertThat(mia.getStaticPart()).isEqualTo(m);
        assertThat(mia.getMethod()).isEqualTo(m);
        assertThat(mia.getThis()).isSameAs(to);
    }

    @Test
    public void afterInvocationManagerIsNotInvokedIfExceptionIsRaised() throws Throwable {
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);
        AfterInvocationManager aim = Mockito.mock(AfterInvocationManager.class);
        interceptor.setAfterInvocationManager(aim);
        Mockito.when(aspectJCallback.proceedWithObject()).thenThrow(new RuntimeException());
        try {
            interceptor.invoke(joinPoint, aspectJCallback);
            fail("Expected exception");
        } catch (RuntimeException expected) {
        }
        Mockito.verifyZeroInteractions(aim);
    }

    // SEC-1967
    @Test
    @SuppressWarnings("unchecked")
    public void invokeWithAspectJCallbackRunAsReplacementCleansAfterException() throws Exception {
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);
        token.setAuthenticated(true);
        final RunAsManager runAs = Mockito.mock(RunAsManager.class);
        final RunAsUserToken runAsToken = new RunAsUserToken("key", "someone", "creds", token.getAuthorities(), TestingAuthenticationToken.class);
        interceptor.setRunAsManager(runAs);
        Mockito.when(runAs.buildRunAs(ArgumentMatchers.eq(token), ArgumentMatchers.any(MethodInvocation.class), ArgumentMatchers.any(List.class))).thenReturn(runAsToken);
        Mockito.when(aspectJCallback.proceedWithObject()).thenThrow(new RuntimeException());
        try {
            interceptor.invoke(joinPoint, aspectJCallback);
            fail("Expected Exception");
        } catch (RuntimeException success) {
        }
        // Check we've changed back
        assertThat(SecurityContextHolder.getContext()).isSameAs(ctx);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(token);
    }

    // SEC-1967
    @Test
    @SuppressWarnings("unchecked")
    public void invokeRunAsReplacementCleansAfterException() throws Throwable {
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);
        token.setAuthenticated(true);
        final RunAsManager runAs = Mockito.mock(RunAsManager.class);
        final RunAsUserToken runAsToken = new RunAsUserToken("key", "someone", "creds", token.getAuthorities(), TestingAuthenticationToken.class);
        interceptor.setRunAsManager(runAs);
        Mockito.when(runAs.buildRunAs(ArgumentMatchers.eq(token), ArgumentMatchers.any(MethodInvocation.class), ArgumentMatchers.any(List.class))).thenReturn(runAsToken);
        Mockito.when(joinPoint.proceed()).thenThrow(new RuntimeException());
        try {
            interceptor.invoke(joinPoint);
            fail("Expected Exception");
        } catch (RuntimeException success) {
        }
        // Check we've changed back
        assertThat(SecurityContextHolder.getContext()).isSameAs(ctx);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(token);
    }
}

