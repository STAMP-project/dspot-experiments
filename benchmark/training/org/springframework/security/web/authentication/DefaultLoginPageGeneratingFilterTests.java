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
package org.springframework.security.web.authentication;


import DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL;
import WebAttributes.AUTHENTICATION_EXCEPTION;
import java.util.Collections;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;


/**
 *
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class DefaultLoginPageGeneratingFilterTests {
    private FilterChain chain = Mockito.mock(FilterChain.class);

    @Test
    public void generatingPageWithAuthenticationProcessingFilterOnlyIsSuccessFul() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        filter.doFilter(new MockHttpServletRequest("GET", "/login"), new MockHttpServletResponse(), chain);
        filter.doFilter(new MockHttpServletRequest("GET", "/login;pathparam=unused"), new MockHttpServletResponse(), chain);
    }

    @Test
    public void generatesForGetLogin() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/login"), response, chain);
        assertThat(response.getContentAsString()).isNotEmpty();
    }

    @Test
    public void generatesForPostLogin() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        filter.doFilter(request, response, chain);
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void generatesForNotEmptyContextLogin() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context/login");
        request.setContextPath("/context");
        filter.doFilter(request, response, chain);
        assertThat(response.getContentAsString()).isNotEmpty();
    }

    @Test
    public void generatesForGetApiLogin() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/login"), response, chain);
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void generatesForWithQueryMatch() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.setQueryString("error");
        filter.doFilter(request, response, chain);
        assertThat(response.getContentAsString()).isNotEmpty();
    }

    @Test
    public void generatesForWithContentLength() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        filter.setOauth2LoginEnabled(true);
        filter.setOauth2AuthenticationUrlToClientName(Collections.singletonMap("XYUU", "\u8109\u640f\u7f51\u5e10\u6237\u767b\u5f55"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        filter.doFilter(request, response, chain);
        assertThat(((response.getContentLength()) == (response.getContentAsString().getBytes(response.getCharacterEncoding()).length))).isTrue();
    }

    @Test
    public void generatesForWithQueryNoMatch() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.setQueryString("not");
        filter.doFilter(request, response, chain);
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void generatingPageWithOpenIdFilterOnlyIsSuccessFul() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new DefaultLoginPageGeneratingFilterTests.MockProcessingFilter());
        filter.doFilter(new MockHttpServletRequest("GET", "/login"), new MockHttpServletResponse(), chain);
    }

    // Fake OpenID filter (since it's not in this module
    @SuppressWarnings("unused")
    private static class MockProcessingFilter extends AbstractAuthenticationProcessingFilter {
        MockProcessingFilter() {
            super("/someurl");
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
            return null;
        }

        public String getClaimedIdentityFieldName() {
            return "unused";
        }
    }

    /* SEC-1111 */
    @Test
    public void handlesNonIso8859CharsInErrorMessage() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter(new UsernamePasswordAuthenticationFilter());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        request.addParameter("login_error", "true");
        MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
        String message = messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials", Locale.KOREA);
        request.getSession().setAttribute(AUTHENTICATION_EXCEPTION, new BadCredentialsException(message));
        filter.doFilter(request, new MockHttpServletResponse(), chain);
    }

    // gh-5394
    @Test
    public void generatesForOAuth2LoginAndEscapesClientName() throws Exception {
        DefaultLoginPageGeneratingFilter filter = new DefaultLoginPageGeneratingFilter();
        filter.setLoginPageUrl(DEFAULT_LOGIN_PAGE_URL);
        filter.setOauth2LoginEnabled(true);
        String clientName = "Google < > \" \' &";
        filter.setOauth2AuthenticationUrlToClientName(Collections.singletonMap("/oauth2/authorization/google", clientName));
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/login"), response, chain);
        assertThat(response.getContentAsString()).contains("<a href=\"/oauth2/authorization/google\">Google &lt; &gt; &quot; &#39; &amp;</a>");
    }
}

