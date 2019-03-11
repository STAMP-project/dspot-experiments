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
package org.springframework.security.config.http;


import java.util.Collection;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultFilterChainValidatorTests {
    private DefaultFilterChainValidator validator;

    private FilterChainProxy fcp;

    @Mock
    private Log logger;

    @Mock
    private DefaultFilterInvocationSecurityMetadataSource metadataSource;

    @Mock
    private AccessDecisionManager accessDecisionManager;

    private FilterSecurityInterceptor fsi;

    // SEC-1878
    @SuppressWarnings("unchecked")
    @Test
    public void validateCheckLoginPageIsntProtectedThrowsIllegalArgumentException() {
        IllegalArgumentException toBeThrown = new IllegalArgumentException("failed to eval expression");
        Mockito.doThrow(toBeThrown).when(accessDecisionManager).decide(ArgumentMatchers.any(Authentication.class), ArgumentMatchers.anyObject(), ArgumentMatchers.any(Collection.class));
        validator.validate(fcp);
        Mockito.verify(logger).info("Unable to check access to the login page to determine if anonymous access is allowed. This might be an error, but can happen under normal circumstances.", toBeThrown);
    }

    // SEC-1957
    @Test
    public void validateCustomMetadataSource() {
        FilterInvocationSecurityMetadataSource customMetaDataSource = Mockito.mock(FilterInvocationSecurityMetadataSource.class);
        fsi.setSecurityMetadataSource(customMetaDataSource);
        validator.validate(fcp);
        Mockito.verify(customMetaDataSource).getAttributes(ArgumentMatchers.any());
    }
}

