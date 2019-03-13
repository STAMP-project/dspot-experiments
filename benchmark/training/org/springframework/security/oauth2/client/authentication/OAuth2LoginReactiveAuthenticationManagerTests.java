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


import ClientRegistration.Builder;
import OAuth2AccessToken.TokenType.BEARER;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2LoginReactiveAuthenticationManagerTests {
    @Mock
    private ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

    @Mock
    private ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    @Mock
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    private Builder registration = TestClientRegistrations.clientRegistration();

    OAuth2AuthorizationResponse.Builder authorizationResponseBldr = OAuth2AuthorizationResponse.success("code").state("state");

    private OAuth2LoginReactiveAuthenticationManager manager;

    @Test
    public void constructorWhenNullAccessTokenResponseClientThenIllegalArgumentException() {
        this.accessTokenResponseClient = null;
        assertThatThrownBy(() -> new OAuth2LoginReactiveAuthenticationManager(this.accessTokenResponseClient, this.userService)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenNullUserServiceThenIllegalArgumentException() {
        this.userService = null;
        assertThatThrownBy(() -> new OAuth2LoginReactiveAuthenticationManager(this.accessTokenResponseClient, this.userService)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void authenticateWhenNoSubscriptionThenDoesNothing() {
        // we didn't do anything because it should cause a ClassCastException (as verified below)
        TestingAuthenticationToken token = new TestingAuthenticationToken("a", "b");
        assertThatCode(() -> this.manager.authenticate(token)).doesNotThrowAnyException();
        assertThatThrownBy(() -> this.manager.authenticate(token).block()).isInstanceOf(Throwable.class);
    }

    @Test
    public void authenticationWhenErrorThenOAuth2AuthenticationException() {
        this.authorizationResponseBldr = OAuth2AuthorizationResponse.error("error").state("state");
        assertThatThrownBy(() -> this.manager.authenticate(loginToken()).block()).isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    public void authenticationWhenStateDoesNotMatchThenOAuth2AuthenticationException() {
        this.authorizationResponseBldr.state("notmatch");
        assertThatThrownBy(() -> this.manager.authenticate(loginToken()).block()).isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    public void authenticationWhenOAuth2UserNotFoundThenEmpty() {
        OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken("foo").tokenType(BEARER).build();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.just(accessTokenResponse));
        Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.empty());
        assertThat(this.manager.authenticate(loginToken()).block()).isNull();
    }

    @Test
    public void authenticationWhenOAuth2UserFoundThenSuccess() {
        OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken("foo").tokenType(BEARER).build();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.just(accessTokenResponse));
        DefaultOAuth2User user = new DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), Collections.singletonMap("user", "rob"), "user");
        Mockito.when(this.userService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.just(user));
        OAuth2LoginAuthenticationToken result = ((OAuth2LoginAuthenticationToken) (this.manager.authenticate(loginToken()).block()));
        assertThat(result.getPrincipal()).isEqualTo(user);
        assertThat(result.getAuthorities()).containsOnlyElementsOf(user.getAuthorities());
        assertThat(result.isAuthenticated()).isTrue();
    }

    // gh-5368
    @Test
    public void authenticateWhenTokenSuccessResponseThenAdditionalParametersAddedToUserRequest() {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("param1", "value1");
        additionalParameters.put("param2", "value2");
        OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken("foo").tokenType(BEARER).additionalParameters(additionalParameters).build();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.just(accessTokenResponse));
        DefaultOAuth2User user = new DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), Collections.singletonMap("user", "rob"), "user");
        ArgumentCaptor<OAuth2UserRequest> userRequestArgCaptor = ArgumentCaptor.forClass(OAuth2UserRequest.class);
        Mockito.when(this.userService.loadUser(userRequestArgCaptor.capture())).thenReturn(Mono.just(user));
        this.manager.authenticate(loginToken()).block();
        assertThat(userRequestArgCaptor.getValue().getAdditionalParameters()).containsAllEntriesOf(accessTokenResponse.getAdditionalParameters());
    }
}

