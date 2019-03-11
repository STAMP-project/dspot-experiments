/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.oauth2.client.authentication;


import OAuth2ErrorCodes.INVALID_REQUEST;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;


/**
 * Tests for {@link OAuth2LoginAuthenticationProvider}.
 *
 * @author Joe Grandja
 */
public class OAuth2LoginAuthenticationProviderTests {
    private ClientRegistration clientRegistration;

    private OAuth2AuthorizationRequest authorizationRequest;

    private OAuth2AuthorizationResponse authorizationResponse;

    private OAuth2AuthorizationExchange authorizationExchange;

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

    private OAuth2LoginAuthenticationProvider authenticationProvider;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void constructorWhenAccessTokenResponseClientIsNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        new OAuth2LoginAuthenticationProvider(null, this.userService);
    }

    @Test
    public void constructorWhenUserServiceIsNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        new OAuth2LoginAuthenticationProvider(this.accessTokenResponseClient, null);
    }

    @Test
    public void setAuthoritiesMapperWhenAuthoritiesMapperIsNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        this.authenticationProvider.setAuthoritiesMapper(null);
    }

    @Test
    public void supportsWhenTypeOAuth2LoginAuthenticationTokenThenReturnTrue() {
        assertThat(this.authenticationProvider.supports(OAuth2LoginAuthenticationToken.class)).isTrue();
    }

    @Test
    public void authenticateWhenAuthorizationRequestContainsOpenidScopeThenReturnNull() {
        OAuth2AuthorizationRequest authorizationRequest = request().scope("openid").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, this.authorizationResponse);
        OAuth2LoginAuthenticationToken authentication = ((OAuth2LoginAuthenticationToken) (this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, authorizationExchange))));
        assertThat(authentication).isNull();
    }

    @Test
    public void authenticateWhenAuthorizationErrorResponseThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString(INVALID_REQUEST));
        OAuth2AuthorizationResponse authorizationResponse = error().errorCode(INVALID_REQUEST).build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, authorizationResponse);
        this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, authorizationExchange));
    }

    @Test
    public void authenticateWhenAuthorizationResponseStateNotEqualAuthorizationRequestStateThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("invalid_state_parameter"));
        OAuth2AuthorizationResponse authorizationResponse = success().state("67890").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, authorizationResponse);
        this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, authorizationExchange));
    }

    @Test
    public void authenticateWhenAuthorizationResponseRedirectUriNotEqualAuthorizationRequestRedirectUriThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("invalid_redirect_uri_parameter"));
        OAuth2AuthorizationResponse authorizationResponse = success().redirectUri("http://example2.com").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, authorizationResponse);
        this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, authorizationExchange));
    }

    @Test
    public void authenticateWhenLoginSuccessThenReturnAuthentication() {
        OAuth2AccessTokenResponse accessTokenResponse = this.accessTokenSuccessResponse();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(accessTokenResponse);
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        Mockito.when(principal.getAuthorities()).thenAnswer(((Answer<List<GrantedAuthority>>) (( invocation) -> authorities)));
        Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenReturn(principal);
        OAuth2LoginAuthenticationToken authentication = ((OAuth2LoginAuthenticationToken) (this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, this.authorizationExchange))));
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(principal);
        assertThat(authentication.getCredentials()).isEqualTo("");
        assertThat(authentication.getAuthorities()).isEqualTo(authorities);
        assertThat(authentication.getClientRegistration()).isEqualTo(this.clientRegistration);
        assertThat(authentication.getAuthorizationExchange()).isEqualTo(this.authorizationExchange);
        assertThat(authentication.getAccessToken()).isEqualTo(accessTokenResponse.getAccessToken());
        assertThat(authentication.getRefreshToken()).isEqualTo(accessTokenResponse.getRefreshToken());
    }

    @Test
    public void authenticateWhenAuthoritiesMapperSetThenReturnMappedAuthorities() {
        OAuth2AccessTokenResponse accessTokenResponse = this.accessTokenSuccessResponse();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(accessTokenResponse);
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        Mockito.when(principal.getAuthorities()).thenAnswer(((Answer<List<GrantedAuthority>>) (( invocation) -> authorities)));
        Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenReturn(principal);
        List<GrantedAuthority> mappedAuthorities = AuthorityUtils.createAuthorityList("ROLE_OAUTH2_USER");
        GrantedAuthoritiesMapper authoritiesMapper = Mockito.mock(GrantedAuthoritiesMapper.class);
        Mockito.when(authoritiesMapper.mapAuthorities(ArgumentMatchers.anyCollection())).thenAnswer(((Answer<List<GrantedAuthority>>) (( invocation) -> mappedAuthorities)));
        this.authenticationProvider.setAuthoritiesMapper(authoritiesMapper);
        OAuth2LoginAuthenticationToken authentication = ((OAuth2LoginAuthenticationToken) (this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, this.authorizationExchange))));
        assertThat(authentication.getAuthorities()).isEqualTo(mappedAuthorities);
    }

    // gh-5368
    @Test
    public void authenticateWhenTokenSuccessResponseThenAdditionalParametersAddedToUserRequest() {
        OAuth2AccessTokenResponse accessTokenResponse = this.accessTokenSuccessResponse();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(accessTokenResponse);
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        Mockito.when(principal.getAuthorities()).thenAnswer(((Answer<List<GrantedAuthority>>) (( invocation) -> authorities)));
        ArgumentCaptor<OAuth2UserRequest> userRequestArgCaptor = ArgumentCaptor.forClass(OAuth2UserRequest.class);
        Mockito.when(this.userService.loadUser(userRequestArgCaptor.capture())).thenReturn(principal);
        this.authenticationProvider.authenticate(new OAuth2LoginAuthenticationToken(this.clientRegistration, this.authorizationExchange));
        assertThat(userRequestArgCaptor.getValue().getAdditionalParameters()).containsAllEntriesOf(accessTokenResponse.getAdditionalParameters());
    }
}

