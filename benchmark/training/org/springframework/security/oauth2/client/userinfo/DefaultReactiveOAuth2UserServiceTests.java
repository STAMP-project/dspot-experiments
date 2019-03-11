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
package org.springframework.security.oauth2.client.userinfo;


import AuthenticationMethod.FORM;
import AuthenticationMethod.HEADER;
import ClientRegistration.Builder;
import HttpHeaders.ACCEPT;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.CONTENT_TYPE;
import HttpMethod.GET;
import HttpMethod.POST;
import MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import MediaType.APPLICATION_JSON_VALUE;
import OAuth2AccessToken.TokenType;
import java.time.Duration;
import java.time.Instant;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
public class DefaultReactiveOAuth2UserServiceTests {
    private Builder clientRegistration;

    private DefaultReactiveOAuth2UserService userService = new DefaultReactiveOAuth2UserService();

    private OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, "access-token", Instant.now(), Instant.now().plus(Duration.ofDays(1)));

    private MockWebServer server;

    @Test
    public void loadUserWhenUserRequestIsNullThenThrowIllegalArgumentException() {
        OAuth2UserRequest request = null;
        StepVerifier.create(this.userService.loadUser(request)).expectError(IllegalArgumentException.class).verify();
    }

    @Test
    public void loadUserWhenUserInfoUriIsNullThenThrowOAuth2AuthenticationException() {
        this.clientRegistration.userInfoUri(null);
        StepVerifier.create(this.userService.loadUser(oauth2UserRequest())).expectErrorSatisfies(( t) -> assertThat(t).isInstanceOf(.class).hasMessageContaining("missing_user_info_uri")).verify();
    }

    @Test
    public void loadUserWhenUserNameAttributeNameIsNullThenThrowOAuth2AuthenticationException() {
        this.clientRegistration.userNameAttributeName(null);
        StepVerifier.create(this.userService.loadUser(oauth2UserRequest())).expectErrorSatisfies(( t) -> assertThat(t).isInstanceOf(.class).hasMessageContaining("missing_user_name_attribute")).verify();
    }

    @Test
    public void loadUserWhenUserInfoSuccessResponseThenReturnUser() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"id\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        enqueueApplicationJsonBody(userInfoResponse);
        OAuth2User user = this.userService.loadUser(oauth2UserRequest()).block();
        assertThat(user.getName()).isEqualTo("user1");
        assertThat(user.getAttributes().size()).isEqualTo(6);
        assertThat(user.getAttributes().get("id")).isEqualTo("user1");
        assertThat(user.getAttributes().get("first-name")).isEqualTo("first");
        assertThat(user.getAttributes().get("last-name")).isEqualTo("last");
        assertThat(user.getAttributes().get("middle-name")).isEqualTo("middle");
        assertThat(user.getAttributes().get("address")).isEqualTo("address");
        assertThat(user.getAttributes().get("email")).isEqualTo("user1@example.com");
        assertThat(user.getAuthorities().size()).isEqualTo(1);
        assertThat(user.getAuthorities().iterator().next()).isInstanceOf(OAuth2UserAuthority.class);
        OAuth2UserAuthority userAuthority = ((OAuth2UserAuthority) (user.getAuthorities().iterator().next()));
        assertThat(userAuthority.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(userAuthority.getAttributes()).isEqualTo(user.getAttributes());
    }

    // gh-5500
    @Test
    public void loadUserWhenAuthenticationMethodHeaderSuccessResponseThenHttpMethodGet() throws Exception {
        this.clientRegistration.userInfoAuthenticationMethod(HEADER);
        String userInfoResponse = "{\n" + (((((("\t\"id\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        enqueueApplicationJsonBody(userInfoResponse);
        this.userService.loadUser(oauth2UserRequest()).block();
        RecordedRequest request = this.server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(GET.name());
        assertThat(request.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(request.getHeader(AUTHORIZATION)).isEqualTo(("Bearer " + (this.accessToken.getTokenValue())));
    }

    // gh-5500
    @Test
    public void loadUserWhenAuthenticationMethodFormSuccessResponseThenHttpMethodPost() throws Exception {
        this.clientRegistration.userInfoAuthenticationMethod(FORM);
        String userInfoResponse = "{\n" + (((((("\t\"id\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        enqueueApplicationJsonBody(userInfoResponse);
        this.userService.loadUser(oauth2UserRequest()).block();
        RecordedRequest request = this.server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(POST.name());
        assertThat(request.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(request.getHeader(CONTENT_TYPE)).contains(APPLICATION_FORM_URLENCODED_VALUE);
        assertThat(request.getBody().readUtf8()).isEqualTo(("access_token=" + (this.accessToken.getTokenValue())));
    }

    @Test
    public void loadUserWhenUserInfoSuccessResponseInvalidThenThrowOAuth2AuthenticationException() throws Exception {
        String userInfoResponse = "{\n" + ((((("\t\"id\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n");
        // "}\n";		// Make the JSON invalid/malformed
        enqueueApplicationJsonBody(userInfoResponse);
        assertThatThrownBy(() -> this.userService.loadUser(oauth2UserRequest()).block()).isInstanceOf(OAuth2AuthenticationException.class).hasMessageContaining("invalid_user_info_response");
    }

    @Test
    public void loadUserWhenUserInfoErrorResponseThenThrowOAuth2AuthenticationException() throws Exception {
        this.server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setResponseCode(500).setBody("{}"));
        assertThatThrownBy(() -> this.userService.loadUser(oauth2UserRequest()).block()).isInstanceOf(OAuth2AuthenticationException.class).hasMessageContaining("invalid_user_info_response");
    }

    @Test
    public void loadUserWhenUserInfoUriInvalidThenThrowAuthenticationServiceException() throws Exception {
        this.clientRegistration.userInfoUri("http://invalid-provider.com/user");
        assertThatThrownBy(() -> this.userService.loadUser(oauth2UserRequest()).block()).isInstanceOf(AuthenticationServiceException.class);
    }
}

