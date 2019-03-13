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
package org.springframework.security.oauth2.client.oidc.userinfo;


import ClientRegistration.Builder;
import IdTokenClaimNames.SUB;
import OAuth2AccessToken.TokenType;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class OidcReactiveOAuth2UserServiceTests {
    @Mock
    private ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    private Builder registration = TestClientRegistrations.clientRegistration().userNameAttributeName(SUB);

    private OidcIdToken idToken = new OidcIdToken("token123", Instant.now(), Instant.now().plusSeconds(3600), Collections.singletonMap(SUB, "sub123"));

    private OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, "token", Instant.now(), Instant.now().plus(Duration.ofDays(1)), Collections.singleton("read:user"));

    private OidcReactiveOAuth2UserService userService = new OidcReactiveOAuth2UserService();

    @Test
    public void loadUserWhenUserInfoUriNullThenUserInfoNotRetrieved() {
        this.registration.userInfoUri(null);
        OidcUser user = this.userService.loadUser(userRequest()).block();
        assertThat(user.getUserInfo()).isNull();
    }

    @Test
    public void loadUserWhenOAuth2UserEmptyThenNullUserInfo() {
        Mockito.when(this.oauth2UserService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.empty());
        OidcUser user = this.userService.loadUser(userRequest()).block();
        assertThat(user.getUserInfo()).isNull();
    }

    @Test
    public void loadUserWhenOAuth2UserSubjectNullThenOAuth2AuthenticationException() {
        OAuth2User oauth2User = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), Collections.singletonMap("user", "rob"), "user");
        Mockito.when(this.oauth2UserService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.just(oauth2User));
        assertThatCode(() -> this.userService.loadUser(userRequest()).block()).isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    public void loadUserWhenOAuth2UserSubjectNotEqualThenOAuth2AuthenticationException() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(StandardClaimNames.SUB, "not-equal");
        attributes.put("user", "rob");
        OAuth2User oauth2User = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), attributes, "user");
        Mockito.when(this.oauth2UserService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.just(oauth2User));
        assertThatCode(() -> this.userService.loadUser(userRequest()).block()).isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    public void loadUserWhenOAuth2UserThenUserInfoNotNull() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(StandardClaimNames.SUB, "sub123");
        attributes.put("user", "rob");
        OAuth2User oauth2User = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), attributes, "user");
        Mockito.when(this.oauth2UserService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.just(oauth2User));
        assertThat(this.userService.loadUser(userRequest()).block().getUserInfo()).isNotNull();
    }

    @Test
    public void loadUserWhenOAuth2UserAndUser() {
        this.registration.userNameAttributeName("user");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(StandardClaimNames.SUB, "sub123");
        attributes.put("user", "rob");
        OAuth2User oauth2User = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), attributes, "user");
        Mockito.when(this.oauth2UserService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.just(oauth2User));
        assertThat(this.userService.loadUser(userRequest()).block().getName()).isEqualTo("rob");
    }
}

