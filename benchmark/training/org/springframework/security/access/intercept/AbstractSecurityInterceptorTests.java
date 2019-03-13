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
package org.springframework.security.access.intercept;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.util.SimpleMethodInvocation;


/**
 * Tests some {@link AbstractSecurityInterceptor} methods. Most of the testing for this
 * class is found in the {@code MethodSecurityInterceptorTests} class.
 *
 * @author Ben Alex
 */
public class AbstractSecurityInterceptorTests {
    // ~ Methods
    // ========================================================================================================
    @Test(expected = IllegalArgumentException.class)
    public void detectsIfInvocationPassedIncompatibleSecureObject() throws Exception {
        AbstractSecurityInterceptorTests.MockSecurityInterceptorWhichOnlySupportsStrings si = new AbstractSecurityInterceptorTests.MockSecurityInterceptorWhichOnlySupportsStrings();
        setRunAsManager(Mockito.mock(RunAsManager.class));
        setAuthenticationManager(Mockito.mock(AuthenticationManager.class));
        setAfterInvocationManager(Mockito.mock(AfterInvocationManager.class));
        setAccessDecisionManager(Mockito.mock(AccessDecisionManager.class));
        si.setSecurityMetadataSource(Mockito.mock(SecurityMetadataSource.class));
        si.beforeInvocation(new SimpleMethodInvocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void detectsViolationOfGetSecureObjectClassMethod() throws Exception {
        AbstractSecurityInterceptorTests.MockSecurityInterceptorReturnsNull si = new AbstractSecurityInterceptorTests.MockSecurityInterceptorReturnsNull();
        setRunAsManager(Mockito.mock(RunAsManager.class));
        setAuthenticationManager(Mockito.mock(AuthenticationManager.class));
        setAfterInvocationManager(Mockito.mock(AfterInvocationManager.class));
        setAccessDecisionManager(Mockito.mock(AccessDecisionManager.class));
        si.setSecurityMetadataSource(Mockito.mock(SecurityMetadataSource.class));
        afterPropertiesSet();
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockSecurityInterceptorReturnsNull extends AbstractSecurityInterceptor {
        private SecurityMetadataSource securityMetadataSource;

        public Class<?> getSecureObjectClass() {
            return null;
        }

        public SecurityMetadataSource obtainSecurityMetadataSource() {
            return securityMetadataSource;
        }

        public void setSecurityMetadataSource(SecurityMetadataSource securityMetadataSource) {
            this.securityMetadataSource = securityMetadataSource;
        }
    }

    private class MockSecurityInterceptorWhichOnlySupportsStrings extends AbstractSecurityInterceptor {
        private SecurityMetadataSource securityMetadataSource;

        public Class<?> getSecureObjectClass() {
            return String.class;
        }

        public SecurityMetadataSource obtainSecurityMetadataSource() {
            return securityMetadataSource;
        }

        public void setSecurityMetadataSource(SecurityMetadataSource securityMetadataSource) {
            this.securityMetadataSource = securityMetadataSource;
        }
    }
}

