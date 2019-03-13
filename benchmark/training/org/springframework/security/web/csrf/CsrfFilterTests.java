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


import HttpServletResponse.SC_FORBIDDEN;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class CsrfFilterTests {
    @Mock
    private RequestMatcher requestMatcher;

    @Mock
    private CsrfTokenRepository tokenRepository;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AccessDeniedHandler deniedHandler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private CsrfToken token;

    private CsrfFilter filter;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullRepository() {
        new CsrfFilter(null);
    }

    // SEC-2276
    @Test
    public void doFilterDoesNotSaveCsrfTokenUntilAccessed() throws IOException, ServletException {
        this.filter = createCsrfFilter(new LazyCsrfTokenRepository(this.tokenRepository));
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(false);
        Mockito.when(this.tokenRepository.generateToken(this.request)).thenReturn(this.token);
        this.filter.doFilter(this.request, this.response, this.filterChain);
        CsrfToken attrToken = ((CsrfToken) (this.request.getAttribute(this.token.getParameterName())));
        // no CsrfToken should have been saved yet
        Mockito.verify(this.tokenRepository, Mockito.times(0)).saveToken(ArgumentMatchers.any(CsrfToken.class), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        // access the token
        attrToken.getToken();
        // now the CsrfToken should have been saved
        Mockito.verify(this.tokenRepository).saveToken(ArgumentMatchers.eq(this.token), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void doFilterAccessDeniedNoTokenPresent() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.deniedHandler).handle(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(InvalidCsrfTokenException.class));
        Mockito.verifyZeroInteractions(this.filterChain);
    }

    @Test
    public void doFilterAccessDeniedIncorrectTokenPresent() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.request.setParameter(this.token.getParameterName(), ((this.token.getToken()) + " INVALID"));
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.deniedHandler).handle(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(InvalidCsrfTokenException.class));
        Mockito.verifyZeroInteractions(this.filterChain);
    }

    @Test
    public void doFilterAccessDeniedIncorrectTokenPresentHeader() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.request.addHeader(this.token.getHeaderName(), ((this.token.getToken()) + " INVALID"));
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.deniedHandler).handle(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(InvalidCsrfTokenException.class));
        Mockito.verifyZeroInteractions(this.filterChain);
    }

    @Test
    public void doFilterAccessDeniedIncorrectTokenPresentHeaderPreferredOverParameter() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.request.setParameter(this.token.getParameterName(), this.token.getToken());
        this.request.addHeader(this.token.getHeaderName(), ((this.token.getToken()) + " INVALID"));
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.deniedHandler).handle(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(InvalidCsrfTokenException.class));
        Mockito.verifyZeroInteractions(this.filterChain);
    }

    @Test
    public void doFilterNotCsrfRequestExistingToken() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(false);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        Mockito.verifyZeroInteractions(this.deniedHandler);
    }

    @Test
    public void doFilterNotCsrfRequestGenerateToken() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(false);
        Mockito.when(this.tokenRepository.generateToken(this.request)).thenReturn(this.token);
        this.filter.doFilter(this.request, this.response, this.filterChain);
        CsrfFilterTests.assertToken(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        CsrfFilterTests.assertToken(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        Mockito.verifyZeroInteractions(this.deniedHandler);
    }

    @Test
    public void doFilterIsCsrfRequestExistingTokenHeader() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.request.addHeader(this.token.getHeaderName(), this.token.getToken());
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        Mockito.verifyZeroInteractions(this.deniedHandler);
    }

    @Test
    public void doFilterIsCsrfRequestExistingTokenHeaderPreferredOverInvalidParam() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.request.setParameter(this.token.getParameterName(), ((this.token.getToken()) + " INVALID"));
        this.request.addHeader(this.token.getHeaderName(), this.token.getToken());
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        Mockito.verifyZeroInteractions(this.deniedHandler);
    }

    @Test
    public void doFilterIsCsrfRequestExistingToken() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.request.setParameter(this.token.getParameterName(), this.token.getToken());
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        Mockito.verifyZeroInteractions(this.deniedHandler);
        Mockito.verify(this.tokenRepository, Mockito.never()).saveToken(ArgumentMatchers.any(CsrfToken.class), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void doFilterIsCsrfRequestGenerateToken() throws IOException, ServletException {
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.generateToken(this.request)).thenReturn(this.token);
        this.request.setParameter(this.token.getParameterName(), this.token.getToken());
        this.filter.doFilter(this.request, this.response, this.filterChain);
        CsrfFilterTests.assertToken(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        CsrfFilterTests.assertToken(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        // LazyCsrfTokenRepository requires the response as an attribute
        assertThat(this.request.getAttribute(HttpServletResponse.class.getName())).isEqualTo(this.response);
        Mockito.verify(this.filterChain).doFilter(this.request, this.response);
        Mockito.verify(this.tokenRepository).saveToken(this.token, this.request, this.response);
        Mockito.verifyZeroInteractions(this.deniedHandler);
    }

    @Test
    public void doFilterDefaultRequireCsrfProtectionMatcherAllowedMethods() throws IOException, ServletException {
        this.filter = new CsrfFilter(this.tokenRepository);
        this.filter.setAccessDeniedHandler(this.deniedHandler);
        for (String method : Arrays.asList("GET", "TRACE", "OPTIONS", "HEAD")) {
            resetRequestResponse();
            Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
            this.request.setMethod(method);
            this.filter.doFilter(this.request, this.response, this.filterChain);
            Mockito.verify(this.filterChain).doFilter(this.request, this.response);
            Mockito.verifyZeroInteractions(this.deniedHandler);
        }
    }

    /**
     * SEC-2292 Should not allow other cases through since spec states HTTP method is case
     * sensitive http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1
     *
     * @throws Exception
     * 		if an error occurs
     */
    @Test
    public void doFilterDefaultRequireCsrfProtectionMatcherAllowedMethodsCaseSensitive() throws Exception {
        this.filter = new CsrfFilter(this.tokenRepository);
        this.filter.setAccessDeniedHandler(this.deniedHandler);
        for (String method : Arrays.asList("get", "TrAcE", "oPTIOnS", "hEaD")) {
            resetRequestResponse();
            Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
            this.request.setMethod(method);
            this.filter.doFilter(this.request, this.response, this.filterChain);
            Mockito.verify(this.deniedHandler).handle(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(InvalidCsrfTokenException.class));
            Mockito.verifyZeroInteractions(this.filterChain);
        }
    }

    @Test
    public void doFilterDefaultRequireCsrfProtectionMatcherDeniedMethods() throws IOException, ServletException {
        this.filter = new CsrfFilter(this.tokenRepository);
        this.filter.setAccessDeniedHandler(this.deniedHandler);
        for (String method : Arrays.asList("POST", "PUT", "PATCH", "DELETE", "INVALID")) {
            resetRequestResponse();
            Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
            this.request.setMethod(method);
            this.filter.doFilter(this.request, this.response, this.filterChain);
            Mockito.verify(this.deniedHandler).handle(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.response), ArgumentMatchers.any(InvalidCsrfTokenException.class));
            Mockito.verifyZeroInteractions(this.filterChain);
        }
    }

    @Test
    public void doFilterDefaultAccessDenied() throws IOException, ServletException {
        this.filter = new CsrfFilter(this.tokenRepository);
        this.filter.setRequireCsrfProtectionMatcher(this.requestMatcher);
        Mockito.when(this.requestMatcher.matches(this.request)).thenReturn(true);
        Mockito.when(this.tokenRepository.loadToken(this.request)).thenReturn(this.token);
        this.filter.doFilter(this.request, this.response, this.filterChain);
        assertThat(this.request.getAttribute(this.token.getParameterName())).isEqualTo(this.token);
        assertThat(this.request.getAttribute(CsrfToken.class.getName())).isEqualTo(this.token);
        assertThat(this.response.getStatus()).isEqualTo(SC_FORBIDDEN);
        Mockito.verifyZeroInteractions(this.filterChain);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRequireCsrfProtectionMatcherNull() {
        this.filter.setRequireCsrfProtectionMatcher(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAccessDeniedHandlerNull() {
        this.filter.setAccessDeniedHandler(null);
    }

    private static class CsrfTokenAssert extends AbstractObjectAssert<CsrfFilterTests.CsrfTokenAssert, CsrfToken> {
        /**
         * Creates a new </code>{@link ObjectAssert}</code>.
         *
         * @param actual
         * 		the target to verify.
         */
        protected CsrfTokenAssert(CsrfToken actual) {
            super(actual, CsrfFilterTests.CsrfTokenAssert.class);
        }

        public CsrfFilterTests.CsrfTokenAssert isEqualTo(CsrfToken expected) {
            assertThat(this.actual.getHeaderName()).isEqualTo(expected.getHeaderName());
            assertThat(this.actual.getParameterName()).isEqualTo(expected.getParameterName());
            assertThat(this.actual.getToken()).isEqualTo(expected.getToken());
            return this;
        }
    }
}

