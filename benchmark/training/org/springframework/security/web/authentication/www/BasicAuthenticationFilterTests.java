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
package org.springframework.security.web.authentication.www;


import WebUtils.ERROR_REQUEST_URI_ATTRIBUTE;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Tests {@link BasicAuthenticationFilter}.
 *
 * @author Ben Alex
 */
public class BasicAuthenticationFilterTests {
    // ~ Instance fields
    // ================================================================================================
    private BasicAuthenticationFilter filter;

    private AuthenticationManager manager;

    @Test
    public void testFilterIgnoresRequestsContainingNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/some_file.html");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        // Test
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void testGettersSetters() {
        assertThat(filter.getAuthenticationManager()).isNotNull();
        assertThat(filter.getAuthenticationEntryPoint()).isNotNull();
    }

    @Test
    public void testInvalidBasicAuthorizationTokenIsIgnored() throws Exception {
        String token = "NOT_A_VALID_TOKEN_AS_MISSING_COLON";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        request.setSession(new MockHttpSession());
        final MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void invalidBase64IsIgnored() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic NOT_VALID_BASE64");
        request.setServletPath("/some_file.html");
        request.setSession(new MockHttpSession());
        final MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        // The filter chain shouldn't proceed
        Mockito.verify(chain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testNormalOperation() throws Exception {
        String token = "rod:koala";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        // Test
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("rod");
    }

    // gh-5586
    @Test
    public void doFilterWhenSchemeLowercaseThenCaseInsensitveMatchWorks() throws Exception {
        String token = "rod:koala";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        // Test
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("rod");
    }

    @Test
    public void testOtherAuthorizationSchemeIsIgnored() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "SOME_OTHER_AUTHENTICATION_SCHEME");
        request.setServletPath("/some_file.html");
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartupDetectsMissingAuthenticationEntryPoint() throws Exception {
        new BasicAuthenticationFilter(manager, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartupDetectsMissingAuthenticationManager() throws Exception {
        BasicAuthenticationFilter filter = new BasicAuthenticationFilter(null);
    }

    @Test
    public void testSuccessLoginThenFailureLoginResultsInSessionLosingToken() throws Exception {
        String token = "rod:koala";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        final MockHttpServletResponse response1 = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response1, chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        // Test
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("rod");
        // NOW PERFORM FAILED AUTHENTICATION
        token = "otherUser:WRONG_PASSWORD";
        request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        final MockHttpServletResponse response2 = new MockHttpServletResponse();
        chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response2, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        request.setServletPath("/some_file.html");
        // Test - the filter chain will not be invoked, as we get a 401 forbidden response
        MockHttpServletResponse response = response2;
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testWrongPasswordContinuesFilterChainIfIgnoreFailureIsTrue() throws Exception {
        String token = "rod:WRONG_PASSWORD";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        request.setSession(new MockHttpSession());
        filter = new BasicAuthenticationFilter(manager);
        assertThat(filter.isIgnoreFailure()).isTrue();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        // Test - the filter chain will be invoked, as we've set ignoreFailure = true
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void testWrongPasswordReturnsForbiddenIfIgnoreFailureIsFalse() throws Exception {
        String token = "rod:WRONG_PASSWORD";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        request.setSession(new MockHttpSession());
        assertThat(filter.isIgnoreFailure()).isFalse();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        // Test - the filter chain will not be invoked, as we get a 401 forbidden response
        Mockito.verify(chain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    // SEC-2054
    @Test
    public void skippedOnErrorDispatch() throws Exception {
        String token = "bad:credentials";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBase64(token.getBytes())))));
        request.setServletPath("/some_file.html");
        request.setAttribute(ERROR_REQUEST_URI_ATTRIBUTE, "/error");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(200);
    }
}

