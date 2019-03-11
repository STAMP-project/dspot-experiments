/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.access.intercept.aspectj.aspect;


import java.util.List;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.intercept.aspectj.AspectJMethodSecurityInterceptor;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 *
 *
 * @author Luke Taylor
 * @since 3.0.3
 */
public class AnnotationSecurityAspectTests {
    private AffirmativeBased adm;

    @Mock
    private AuthenticationManager authman;

    private TestingAuthenticationToken anne = new TestingAuthenticationToken("anne", "", "ROLE_A");

    // private TestingAuthenticationToken bob = new TestingAuthenticationToken("bob", "",
    // "ROLE_B");
    private AspectJMethodSecurityInterceptor interceptor;

    private SecuredImpl secured = new SecuredImpl();

    private SecuredImplSubclass securedSub = new SecuredImplSubclass();

    private PrePostSecured prePostSecured = new PrePostSecured();

    @Test
    public void securedInterfaceMethodAllowsAllAccess() throws Exception {
        secured.securedMethod();
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void securedClassMethodDeniesUnauthenticatedAccess() throws Exception {
        secured.securedClassMethod();
    }

    @Test
    public void securedClassMethodAllowsAccessToRoleA() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(anne);
        secured.securedClassMethod();
    }

    @Test(expected = AccessDeniedException.class)
    public void internalPrivateCallIsIntercepted() {
        SecurityContextHolder.getContext().setAuthentication(anne);
        try {
            secured.publicCallsPrivate();
            fail("Expected AccessDeniedException");
        } catch (AccessDeniedException expected) {
        }
        securedSub.publicCallsPrivate();
    }

    @Test(expected = AccessDeniedException.class)
    public void protectedMethodIsIntercepted() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(anne);
        secured.protectedMethod();
    }

    @Test
    public void overriddenProtectedMethodIsNotIntercepted() throws Exception {
        // AspectJ doesn't inherit annotations
        securedSub.protectedMethod();
    }

    // SEC-1262
    @Test(expected = AccessDeniedException.class)
    public void denyAllPreAuthorizeDeniesAccess() throws Exception {
        configureForElAnnotations();
        SecurityContextHolder.getContext().setAuthentication(anne);
        prePostSecured.denyAllMethod();
    }

    @Test
    public void postFilterIsApplied() throws Exception {
        configureForElAnnotations();
        SecurityContextHolder.getContext().setAuthentication(anne);
        List<String> objects = prePostSecured.postFilterMethod();
        assertThat(objects).hasSize(2);
        assertThat(objects.contains("apple")).isTrue();
        assertThat(objects.contains("aubergine")).isTrue();
    }
}

