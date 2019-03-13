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
package org.springframework.security.cas.web;


import javax.servlet.FilterChain;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * Tests {@link CasAuthenticationFilter}.
 *
 * @author Ben Alex
 * @author Rob Winch
 */
public class CasAuthenticationFilterTests {
    @Test
    public void testGettersSetters() {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setProxyGrantingTicketStorage(Mockito.mock(ProxyGrantingTicketStorage.class));
        filter.setProxyReceptorUrl("/someurl");
        filter.setServiceProperties(new ServiceProperties());
    }

    @Test
    public void testNormalOperation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/login/cas");
        request.addParameter("ticket", "ST-0-ER94xMJmn6pha35CQRoZ");
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(new AuthenticationManager() {
            public Authentication authenticate(Authentication a) {
                return a;
            }
        });
        assertThat(filter.requiresAuthentication(request, new MockHttpServletResponse())).isTrue();
        Authentication result = filter.attemptAuthentication(request, new MockHttpServletResponse());
        assertThat((result != null)).isTrue();
    }

    @Test(expected = AuthenticationException.class)
    public void testNullServiceTicketHandledGracefully() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(new AuthenticationManager() {
            public Authentication authenticate(Authentication a) {
                throw new BadCredentialsException("Rejected");
            }
        });
        filter.attemptAuthentication(new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test
    public void testRequiresAuthenticationFilterProcessUrl() {
        String url = "/login/cas";
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setFilterProcessesUrl(url);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath(url);
        assertThat(filter.requiresAuthentication(request, response)).isTrue();
    }

    @Test
    public void testRequiresAuthenticationProxyRequest() {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath("/pgtCallback");
        assertThat(filter.requiresAuthentication(request, response)).isFalse();
        filter.setProxyReceptorUrl(request.getServletPath());
        assertThat(filter.requiresAuthentication(request, response)).isFalse();
        filter.setProxyGrantingTicketStorage(Mockito.mock(ProxyGrantingTicketStorage.class));
        assertThat(filter.requiresAuthentication(request, response)).isTrue();
        request.setServletPath("/other");
        assertThat(filter.requiresAuthentication(request, response)).isFalse();
    }

    @Test
    public void testRequiresAuthenticationAuthAll() {
        ServiceProperties properties = new ServiceProperties();
        properties.setAuthenticateAllArtifacts(true);
        String url = "/login/cas";
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setFilterProcessesUrl(url);
        filter.setServiceProperties(properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath(url);
        assertThat(filter.requiresAuthentication(request, response)).isTrue();
        request.setServletPath("/other");
        assertThat(filter.requiresAuthentication(request, response)).isFalse();
        request.setParameter(properties.getArtifactParameter(), "value");
        assertThat(filter.requiresAuthentication(request, response)).isTrue();
        SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.AnonymousAuthenticationToken("key", "principal", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        assertThat(filter.requiresAuthentication(request, response)).isTrue();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("un", "principal"));
        assertThat(filter.requiresAuthentication(request, response)).isTrue();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("un", "principal", "ROLE_ANONYMOUS"));
        assertThat(filter.requiresAuthentication(request, response)).isFalse();
    }

    @Test
    public void testAuthenticateProxyUrl() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath("/pgtCallback");
        filter.setProxyGrantingTicketStorage(Mockito.mock(ProxyGrantingTicketStorage.class));
        filter.setProxyReceptorUrl(request.getServletPath());
        assertThat(filter.attemptAuthentication(request, response)).isNull();
    }

    @Test
    public void testDoFilterAuthenticateAll() throws Exception {
        AuthenticationSuccessHandler successHandler = Mockito.mock(AuthenticationSuccessHandler.class);
        AuthenticationManager manager = Mockito.mock(AuthenticationManager.class);
        Authentication authentication = new TestingAuthenticationToken("un", "pwd", "ROLE_USER");
        Mockito.when(manager.authenticate(ArgumentMatchers.any(Authentication.class))).thenReturn(authentication);
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setAuthenticateAllArtifacts(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("ticket", "ST-1-123");
        request.setServletPath("/authenticate");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(serviceProperties);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setProxyGrantingTicketStorage(Mockito.mock(ProxyGrantingTicketStorage.class));
        filter.setAuthenticationManager(manager);
        filter.afterPropertiesSet();
        filter.doFilter(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull().withFailMessage("Authentication should not be null");
        Mockito.verify(chain).doFilter(request, response);
        Mockito.verifyZeroInteractions(successHandler);
        // validate for when the filterProcessUrl matches
        filter.setFilterProcessesUrl(request.getServletPath());
        SecurityContextHolder.clearContext();
        filter.doFilter(request, response, chain);
        Mockito.verifyNoMoreInteractions(chain);
        Mockito.verify(successHandler).onAuthenticationSuccess(request, response, authentication);
    }

    // SEC-1592
    @Test
    public void testChainNotInvokedForProxyReceptor() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);
        request.setServletPath("/pgtCallback");
        filter.setProxyGrantingTicketStorage(Mockito.mock(ProxyGrantingTicketStorage.class));
        filter.setProxyReceptorUrl(request.getServletPath());
        filter.doFilter(request, response, chain);
        Mockito.verifyZeroInteractions(chain);
    }
}

