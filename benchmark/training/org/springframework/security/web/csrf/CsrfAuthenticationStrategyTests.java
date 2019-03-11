/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.security.web.csrf;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class CsrfAuthenticationStrategyTests {
    @Mock
    private CsrfTokenRepository csrfTokenRepository;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private CsrfAuthenticationStrategy strategy;

    private CsrfToken existingToken;

    private CsrfToken generatedToken;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullCsrfTokenRepository() {
        new CsrfAuthenticationStrategy(null);
    }

    @Test
    public void logoutRemovesCsrfTokenAndSavesNew() {
        Mockito.when(this.csrfTokenRepository.loadToken(this.request)).thenReturn(this.existingToken);
        Mockito.when(this.csrfTokenRepository.generateToken(this.request)).thenReturn(this.generatedToken);
        this.strategy.onAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"), this.request, this.response);
        Mockito.verify(this.csrfTokenRepository).saveToken(null, this.request, this.response);
        Mockito.verify(this.csrfTokenRepository).saveToken(ArgumentMatchers.eq(this.generatedToken), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        // SEC-2404, SEC-2832
        CsrfToken tokenInRequest = ((CsrfToken) (this.request.getAttribute(CsrfToken.class.getName())));
        assertThat(tokenInRequest.getToken()).isSameAs(this.generatedToken.getToken());
        assertThat(tokenInRequest.getHeaderName()).isSameAs(this.generatedToken.getHeaderName());
        assertThat(tokenInRequest.getParameterName()).isSameAs(this.generatedToken.getParameterName());
        assertThat(this.request.getAttribute(this.generatedToken.getParameterName())).isSameAs(tokenInRequest);
    }

    // SEC-2872
    @Test
    public void delaySavingCsrf() {
        this.strategy = new CsrfAuthenticationStrategy(new LazyCsrfTokenRepository(this.csrfTokenRepository));
        Mockito.when(this.csrfTokenRepository.loadToken(this.request)).thenReturn(this.existingToken);
        Mockito.when(this.csrfTokenRepository.generateToken(this.request)).thenReturn(this.generatedToken);
        this.strategy.onAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"), this.request, this.response);
        Mockito.verify(this.csrfTokenRepository).saveToken(null, this.request, this.response);
        Mockito.verify(this.csrfTokenRepository, Mockito.never()).saveToken(ArgumentMatchers.eq(this.generatedToken), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        CsrfToken tokenInRequest = ((CsrfToken) (this.request.getAttribute(CsrfToken.class.getName())));
        tokenInRequest.getToken();
        Mockito.verify(this.csrfTokenRepository).saveToken(ArgumentMatchers.eq(this.generatedToken), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void logoutRemovesNoActionIfNullToken() {
        this.strategy.onAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"), this.request, this.response);
        Mockito.verify(this.csrfTokenRepository, Mockito.never()).saveToken(ArgumentMatchers.any(CsrfToken.class), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }
}

