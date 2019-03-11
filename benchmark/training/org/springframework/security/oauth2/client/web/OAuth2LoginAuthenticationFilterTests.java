/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.oauth2.client.web;


import AuthorizationGrantType.AUTHORIZATION_CODE;
import ClientAuthenticationMethod.BASIC;
import OAuth2ErrorCodes.INVALID_REQUEST;
import OAuth2ParameterNames.CODE;
import OAuth2ParameterNames.STATE;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


/**
 * Tests for {@link OAuth2LoginAuthenticationFilter}.
 *
 * @author Joe Grandja
 */
public class OAuth2LoginAuthenticationFilterTests {
    private ClientRegistration registration1;

    private ClientRegistration registration2;

    private String principalName1 = "principal-1";

    private ClientRegistrationRepository clientRegistrationRepository;

    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    private OAuth2AuthorizedClientService authorizedClientService;

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    private AuthenticationFailureHandler failureHandler;

    private AuthenticationManager authenticationManager;

    private OAuth2LoginAuthenticationToken loginAuthentication;

    private OAuth2LoginAuthenticationFilter filter;

    @Test
    public void constructorWhenClientRegistrationRepositoryIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2LoginAuthenticationFilter(null, this.authorizedClientService)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenAuthorizedClientServiceIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2LoginAuthenticationFilter(this.clientRegistrationRepository, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenAuthorizedClientRepositoryIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2LoginAuthenticationFilter(this.clientRegistrationRepository, ((OAuth2AuthorizedClientRepository) (null)), OAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenFilterProcessesUrlIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2LoginAuthenticationFilter(this.clientRegistrationRepository, this.authorizedClientRepository, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setAuthorizationRequestRepositoryWhenAuthorizationRequestRepositoryIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.filter.setAuthorizationRequestRepository(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void doFilterWhenNotAuthorizationResponseThenNextFilter() throws Exception {
        String requestUri = "/path";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        Mockito.verify(this.filter, Mockito.never()).attemptAuthentication(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void doFilterWhenAuthorizationResponseInvalidThenInvalidRequestError() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration1.getRegistrationId());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        // NOTE:
        // A valid Authorization Response contains either a 'code' or 'error' parameter.
        // Don't set it to force an invalid Authorization Response.
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.filter.doFilter(request, response, filterChain);
        ArgumentCaptor<AuthenticationException> authenticationExceptionArgCaptor = ArgumentCaptor.forClass(AuthenticationException.class);
        Mockito.verify(this.failureHandler).onAuthenticationFailure(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), authenticationExceptionArgCaptor.capture());
        assertThat(authenticationExceptionArgCaptor.getValue()).isInstanceOf(OAuth2AuthenticationException.class);
        OAuth2AuthenticationException authenticationException = ((OAuth2AuthenticationException) (authenticationExceptionArgCaptor.getValue()));
        assertThat(authenticationException.getError().getErrorCode()).isEqualTo(INVALID_REQUEST);
    }

    @Test
    public void doFilterWhenAuthorizationResponseAuthorizationRequestNotFoundThenAuthorizationRequestNotFoundError() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration2.getRegistrationId());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, "state");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.filter.doFilter(request, response, filterChain);
        ArgumentCaptor<AuthenticationException> authenticationExceptionArgCaptor = ArgumentCaptor.forClass(AuthenticationException.class);
        Mockito.verify(this.failureHandler).onAuthenticationFailure(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), authenticationExceptionArgCaptor.capture());
        assertThat(authenticationExceptionArgCaptor.getValue()).isInstanceOf(OAuth2AuthenticationException.class);
        OAuth2AuthenticationException authenticationException = ((OAuth2AuthenticationException) (authenticationExceptionArgCaptor.getValue()));
        assertThat(authenticationException.getError().getErrorCode()).isEqualTo("authorization_request_not_found");
    }

    // gh-5251
    @Test
    public void doFilterWhenAuthorizationResponseClientRegistrationNotFoundThenClientRegistrationNotFoundError() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration2.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, "state");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        ClientRegistration registrationNotFound = ClientRegistration.withRegistrationId("registration-not-found").clientId("client-1").clientSecret("secret").clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}").scope("user").authorizationUri("https://provider.com/oauth2/authorize").tokenUri("https://provider.com/oauth2/token").userInfoUri("https://provider.com/oauth2/user").userNameAttributeName("id").clientName("client-1").build();
        this.setUpAuthorizationRequest(request, response, registrationNotFound, state);
        this.filter.doFilter(request, response, filterChain);
        ArgumentCaptor<AuthenticationException> authenticationExceptionArgCaptor = ArgumentCaptor.forClass(AuthenticationException.class);
        Mockito.verify(this.failureHandler).onAuthenticationFailure(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), authenticationExceptionArgCaptor.capture());
        assertThat(authenticationExceptionArgCaptor.getValue()).isInstanceOf(OAuth2AuthenticationException.class);
        OAuth2AuthenticationException authenticationException = ((OAuth2AuthenticationException) (authenticationExceptionArgCaptor.getValue()));
        assertThat(authenticationException.getError().getErrorCode()).isEqualTo("client_registration_not_found");
    }

    @Test
    public void doFilterWhenAuthorizationResponseValidThenAuthorizationRequestRemoved() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration2.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, state);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.setUpAuthorizationRequest(request, response, this.registration2, state);
        this.setUpAuthenticationResult(this.registration2);
        this.filter.doFilter(request, response, filterChain);
        assertThat(this.authorizationRequestRepository.loadAuthorizationRequest(request)).isNull();
    }

    @Test
    public void doFilterWhenAuthorizationResponseValidThenAuthorizedClientSaved() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration1.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, state);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.setUpAuthorizationRequest(request, response, this.registration1, state);
        this.setUpAuthenticationResult(this.registration1);
        this.filter.doFilter(request, response, filterChain);
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientRepository.loadAuthorizedClient(this.registration1.getRegistrationId(), this.loginAuthentication, request);
        assertThat(authorizedClient).isNotNull();
        assertThat(authorizedClient.getClientRegistration()).isEqualTo(this.registration1);
        assertThat(authorizedClient.getPrincipalName()).isEqualTo(this.principalName1);
        assertThat(authorizedClient.getAccessToken()).isNotNull();
        assertThat(authorizedClient.getRefreshToken()).isNotNull();
    }

    @Test
    public void doFilterWhenCustomFilterProcessesUrlThenFilterProcesses() throws Exception {
        String filterProcessesUrl = "/login/oauth2/custom/*";
        this.filter = Mockito.spy(new OAuth2LoginAuthenticationFilter(this.clientRegistrationRepository, this.authorizedClientRepository, filterProcessesUrl));
        this.filter.setAuthenticationManager(this.authenticationManager);
        String requestUri = "/login/oauth2/custom/" + (this.registration2.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, state);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.setUpAuthorizationRequest(request, response, this.registration2, state);
        this.setUpAuthenticationResult(this.registration2);
        this.filter.doFilter(request, response, filterChain);
        Mockito.verifyZeroInteractions(filterChain);
        Mockito.verify(this.filter).attemptAuthentication(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    // gh-5890
    @Test
    public void doFilterWhenAuthorizationResponseHasDefaultPort80ThenRedirectUriMatchingExcludesPort() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration2.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setScheme("http");
        request.setServerName("example.com");
        request.setServerPort(80);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, "state");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.setUpAuthorizationRequest(request, response, this.registration2, state);
        this.setUpAuthenticationResult(this.registration2);
        this.filter.doFilter(request, response, filterChain);
        ArgumentCaptor<Authentication> authenticationArgCaptor = ArgumentCaptor.forClass(Authentication.class);
        Mockito.verify(this.authenticationManager).authenticate(authenticationArgCaptor.capture());
        OAuth2LoginAuthenticationToken authentication = ((OAuth2LoginAuthenticationToken) (authenticationArgCaptor.getValue()));
        OAuth2AuthorizationRequest authorizationRequest = authentication.getAuthorizationExchange().getAuthorizationRequest();
        OAuth2AuthorizationResponse authorizationResponse = authentication.getAuthorizationExchange().getAuthorizationResponse();
        String expectedRedirectUri = "http://example.com/login/oauth2/code/registration-id-2";
        assertThat(authorizationRequest.getRedirectUri()).isEqualTo(expectedRedirectUri);
        assertThat(authorizationResponse.getRedirectUri()).isEqualTo(expectedRedirectUri);
    }

    // gh-5890
    @Test
    public void doFilterWhenAuthorizationResponseHasDefaultPort443ThenRedirectUriMatchingExcludesPort() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration2.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setScheme("https");
        request.setServerName("example.com");
        request.setServerPort(443);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, "state");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.setUpAuthorizationRequest(request, response, this.registration2, state);
        this.setUpAuthenticationResult(this.registration2);
        this.filter.doFilter(request, response, filterChain);
        ArgumentCaptor<Authentication> authenticationArgCaptor = ArgumentCaptor.forClass(Authentication.class);
        Mockito.verify(this.authenticationManager).authenticate(authenticationArgCaptor.capture());
        OAuth2LoginAuthenticationToken authentication = ((OAuth2LoginAuthenticationToken) (authenticationArgCaptor.getValue()));
        OAuth2AuthorizationRequest authorizationRequest = authentication.getAuthorizationExchange().getAuthorizationRequest();
        OAuth2AuthorizationResponse authorizationResponse = authentication.getAuthorizationExchange().getAuthorizationResponse();
        String expectedRedirectUri = "https://example.com/login/oauth2/code/registration-id-2";
        assertThat(authorizationRequest.getRedirectUri()).isEqualTo(expectedRedirectUri);
        assertThat(authorizationResponse.getRedirectUri()).isEqualTo(expectedRedirectUri);
    }

    // gh-5890
    @Test
    public void doFilterWhenAuthorizationResponseHasNonDefaultPortThenRedirectUriMatchingIncludesPort() throws Exception {
        String requestUri = "/login/oauth2/code/" + (this.registration2.getRegistrationId());
        String state = "state";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setScheme("https");
        request.setServerName("example.com");
        request.setServerPort(9090);
        request.setServletPath(requestUri);
        request.addParameter(CODE, "code");
        request.addParameter(STATE, "state");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        this.setUpAuthorizationRequest(request, response, this.registration2, state);
        this.setUpAuthenticationResult(this.registration2);
        this.filter.doFilter(request, response, filterChain);
        ArgumentCaptor<Authentication> authenticationArgCaptor = ArgumentCaptor.forClass(Authentication.class);
        Mockito.verify(this.authenticationManager).authenticate(authenticationArgCaptor.capture());
        OAuth2LoginAuthenticationToken authentication = ((OAuth2LoginAuthenticationToken) (authenticationArgCaptor.getValue()));
        OAuth2AuthorizationRequest authorizationRequest = authentication.getAuthorizationExchange().getAuthorizationRequest();
        OAuth2AuthorizationResponse authorizationResponse = authentication.getAuthorizationExchange().getAuthorizationResponse();
        String expectedRedirectUri = "https://example.com:9090/login/oauth2/code/registration-id-2";
        assertThat(authorizationRequest.getRedirectUri()).isEqualTo(expectedRedirectUri);
        assertThat(authorizationResponse.getRedirectUri()).isEqualTo(expectedRedirectUri);
    }
}

