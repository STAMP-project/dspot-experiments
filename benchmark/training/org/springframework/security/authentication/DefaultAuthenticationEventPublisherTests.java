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
package org.springframework.security.authentication;


import java.util.Properties;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 *
 *
 * @author Luke Taylor
 */
public class DefaultAuthenticationEventPublisherTests {
    DefaultAuthenticationEventPublisher publisher;

    @Test
    public void expectedDefaultMappingsAreSatisfied() throws Exception {
        publisher = new DefaultAuthenticationEventPublisher();
        ApplicationEventPublisher appPublisher = Mockito.mock(ApplicationEventPublisher.class);
        publisher.setApplicationEventPublisher(appPublisher);
        Authentication a = Mockito.mock(Authentication.class);
        Exception cause = new Exception();
        Object extraInfo = new Object();
        publisher.publishAuthenticationFailure(new BadCredentialsException(""), a);
        publisher.publishAuthenticationFailure(new BadCredentialsException("", cause), a);
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureBadCredentialsEvent.class));
        Mockito.reset(appPublisher);
        publisher.publishAuthenticationFailure(new UsernameNotFoundException(""), a);
        publisher.publishAuthenticationFailure(new UsernameNotFoundException("", cause), a);
        publisher.publishAuthenticationFailure(new AccountExpiredException(""), a);
        publisher.publishAuthenticationFailure(new AccountExpiredException("", cause), a);
        publisher.publishAuthenticationFailure(new ProviderNotFoundException(""), a);
        publisher.publishAuthenticationFailure(new DisabledException(""), a);
        publisher.publishAuthenticationFailure(new DisabledException("", cause), a);
        publisher.publishAuthenticationFailure(new LockedException(""), a);
        publisher.publishAuthenticationFailure(new LockedException("", cause), a);
        publisher.publishAuthenticationFailure(new AuthenticationServiceException(""), a);
        publisher.publishAuthenticationFailure(new AuthenticationServiceException("", cause), a);
        publisher.publishAuthenticationFailure(new CredentialsExpiredException(""), a);
        publisher.publishAuthenticationFailure(new CredentialsExpiredException("", cause), a);
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureBadCredentialsEvent.class));
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureExpiredEvent.class));
        Mockito.verify(appPublisher).publishEvent(ArgumentMatchers.isA(AuthenticationFailureProviderNotFoundEvent.class));
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureDisabledEvent.class));
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureLockedEvent.class));
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureServiceExceptionEvent.class));
        Mockito.verify(appPublisher, Mockito.times(2)).publishEvent(ArgumentMatchers.isA(AuthenticationFailureCredentialsExpiredEvent.class));
        Mockito.verifyNoMoreInteractions(appPublisher);
    }

    @Test
    public void authenticationSuccessIsPublished() {
        publisher = new DefaultAuthenticationEventPublisher();
        ApplicationEventPublisher appPublisher = Mockito.mock(ApplicationEventPublisher.class);
        publisher.setApplicationEventPublisher(appPublisher);
        publisher.publishAuthenticationSuccess(Mockito.mock(Authentication.class));
        Mockito.verify(appPublisher).publishEvent(ArgumentMatchers.isA(AuthenticationSuccessEvent.class));
        publisher.setApplicationEventPublisher(null);
        // Should be ignored with null app publisher
        publisher.publishAuthenticationSuccess(Mockito.mock(Authentication.class));
    }

    @Test
    public void additionalExceptionMappingsAreSupported() {
        publisher = new DefaultAuthenticationEventPublisher();
        Properties p = new Properties();
        p.put(DefaultAuthenticationEventPublisherTests.MockAuthenticationException.class.getName(), AuthenticationFailureDisabledEvent.class.getName());
        publisher.setAdditionalExceptionMappings(p);
        ApplicationEventPublisher appPublisher = Mockito.mock(ApplicationEventPublisher.class);
        publisher.setApplicationEventPublisher(appPublisher);
        publisher.publishAuthenticationFailure(new DefaultAuthenticationEventPublisherTests.MockAuthenticationException("test"), Mockito.mock(Authentication.class));
        Mockito.verify(appPublisher).publishEvent(ArgumentMatchers.isA(AuthenticationFailureDisabledEvent.class));
    }

    @Test(expected = RuntimeException.class)
    public void missingEventClassExceptionCausesException() {
        publisher = new DefaultAuthenticationEventPublisher();
        Properties p = new Properties();
        p.put(DefaultAuthenticationEventPublisherTests.MockAuthenticationException.class.getName(), "NoSuchClass");
        publisher.setAdditionalExceptionMappings(p);
    }

    @Test
    public void unknownFailureExceptionIsIgnored() throws Exception {
        publisher = new DefaultAuthenticationEventPublisher();
        Properties p = new Properties();
        p.put(DefaultAuthenticationEventPublisherTests.MockAuthenticationException.class.getName(), AuthenticationFailureDisabledEvent.class.getName());
        publisher.setAdditionalExceptionMappings(p);
        ApplicationEventPublisher appPublisher = Mockito.mock(ApplicationEventPublisher.class);
        publisher.setApplicationEventPublisher(appPublisher);
        publisher.publishAuthenticationFailure(new AuthenticationException("") {}, Mockito.mock(Authentication.class));
        Mockito.verifyZeroInteractions(appPublisher);
    }

    private static final class MockAuthenticationException extends AuthenticationException {
        public MockAuthenticationException(String msg) {
            super(msg);
        }
    }
}

