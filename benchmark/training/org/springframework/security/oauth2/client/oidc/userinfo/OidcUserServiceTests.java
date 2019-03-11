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


import AuthenticationMethod.FORM;
import ClientRegistration.Builder;
import HttpHeaders.ACCEPT;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.CONTENT_TYPE;
import HttpMethod.GET;
import HttpMethod.POST;
import MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import MediaType.APPLICATION_JSON_VALUE;
import OAuth2AccessToken.TokenType;
import StandardClaimNames.EMAIL;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;


/**
 * Tests for {@link OidcUserService}.
 *
 * @author Joe Grandja
 */
public class OidcUserServiceTests {
    private Builder clientRegistrationBuilder;

    private OAuth2AccessToken accessToken;

    private OidcIdToken idToken;

    private OidcUserService userService = new OidcUserService();

    private MockWebServer server;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void setOauth2UserServiceWhenNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.userService.setOauth2UserService(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loadUserWhenUserRequestIsNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        this.userService.loadUser(null);
    }

    @Test
    public void loadUserWhenUserInfoUriIsNullThenUserInfoEndpointNotRequested() {
        OidcUser user = this.userService.loadUser(new OidcUserRequest(this.clientRegistrationBuilder.build(), this.accessToken, this.idToken));
        assertThat(user.getUserInfo()).isNull();
    }

    @Test
    public void loadUserWhenAuthorizedScopesDoesNotContainUserInfoScopesThenUserInfoEndpointNotRequested() {
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri("http://provider.com/user").build();
        Set<String> authorizedScopes = new LinkedHashSet<>(Arrays.asList("scope1", "scope2"));
        OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, "access-token", Instant.MIN, Instant.MAX, authorizedScopes);
        OidcUser user = this.userService.loadUser(new OidcUserRequest(clientRegistration, accessToken, this.idToken));
        assertThat(user.getUserInfo()).isNull();
    }

    @Test
    public void loadUserWhenUserInfoSuccessResponseThenReturnUser() {
        String userInfoResponse = "{\n" + (((((("\t\"sub\": \"subject1\",\n" + "   \"name\": \"first last\",\n") + "   \"given_name\": \"first\",\n") + "   \"family_name\": \"last\",\n") + "   \"preferred_username\": \"user1\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        OidcUser user = this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
        assertThat(user.getIdToken()).isNotNull();
        assertThat(user.getUserInfo()).isNotNull();
        assertThat(user.getUserInfo().getClaims().size()).isEqualTo(6);
        assertThat(user.getIdToken()).isEqualTo(this.idToken);
        assertThat(user.getName()).isEqualTo("subject1");
        assertThat(user.getUserInfo().getSubject()).isEqualTo("subject1");
        assertThat(user.getUserInfo().getFullName()).isEqualTo("first last");
        assertThat(user.getUserInfo().getGivenName()).isEqualTo("first");
        assertThat(user.getUserInfo().getFamilyName()).isEqualTo("last");
        assertThat(user.getUserInfo().getPreferredUsername()).isEqualTo("user1");
        assertThat(user.getUserInfo().getEmail()).isEqualTo("user1@example.com");
        assertThat(user.getAuthorities().size()).isEqualTo(1);
        assertThat(user.getAuthorities().iterator().next()).isInstanceOf(OidcUserAuthority.class);
        OidcUserAuthority userAuthority = ((OidcUserAuthority) (user.getAuthorities().iterator().next()));
        assertThat(userAuthority.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(userAuthority.getIdToken()).isEqualTo(user.getIdToken());
        assertThat(userAuthority.getUserInfo()).isEqualTo(user.getUserInfo());
    }

    // gh-5447
    @Test
    public void loadUserWhenUserInfoSuccessResponseAndUserInfoSubjectIsNullThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("invalid_user_info_response"));
        String userInfoResponse = "{\n" + (("\t\"email\": \"full_name@provider.com\",\n" + "\t\"name\": \"full name\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userNameAttributeName(EMAIL).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
    }

    @Test
    public void loadUserWhenUserInfoSuccessResponseAndUserInfoSubjectNotSameAsIdTokenSubjectThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("invalid_user_info_response"));
        String userInfoResponse = "{\n" + ("\t\"sub\": \"other-subject\"\n" + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
    }

    @Test
    public void loadUserWhenUserInfoSuccessResponseInvalidThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource"));
        String userInfoResponse = "{\n" + ((((("\t\"sub\": \"subject1\",\n" + "   \"name\": \"first last\",\n") + "   \"given_name\": \"first\",\n") + "   \"family_name\": \"last\",\n") + "   \"preferred_username\": \"user1\",\n") + "   \"email\": \"user1@example.com\"\n");
        // "}\n";		// Make the JSON invalid/malformed
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
    }

    @Test
    public void loadUserWhenServerErrorThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource: 500 Server Error"));
        this.server.enqueue(new MockResponse().setResponseCode(500));
        String userInfoUri = server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
    }

    @Test
    public void loadUserWhenUserInfoUriInvalidThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource"));
        String userInfoUri = "http://invalid-provider.com/user";
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
    }

    @Test
    public void loadUserWhenCustomUserNameAttributeNameThenGetNameReturnsCustomUserName() {
        String userInfoResponse = "{\n" + (((((("\t\"sub\": \"subject1\",\n" + "   \"name\": \"first last\",\n") + "   \"given_name\": \"first\",\n") + "   \"family_name\": \"last\",\n") + "   \"preferred_username\": \"user1\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userNameAttributeName(EMAIL).build();
        OidcUser user = this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
        assertThat(user.getName()).isEqualTo("user1@example.com");
    }

    // gh-5294
    @Test
    public void loadUserWhenUserInfoSuccessResponseThenAcceptHeaderJson() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"sub\": \"subject1\",\n" + "   \"name\": \"first last\",\n") + "   \"given_name\": \"first\",\n") + "   \"family_name\": \"last\",\n") + "   \"preferred_username\": \"user1\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
        assertThat(this.server.takeRequest(1, TimeUnit.SECONDS).getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
    }

    // gh-5500
    @Test
    public void loadUserWhenAuthenticationMethodHeaderSuccessResponseThenHttpMethodGet() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"sub\": \"subject1\",\n" + "   \"name\": \"first last\",\n") + "   \"given_name\": \"first\",\n") + "   \"family_name\": \"last\",\n") + "   \"preferred_username\": \"user1\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
        RecordedRequest request = this.server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(GET.name());
        assertThat(request.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(request.getHeader(AUTHORIZATION)).isEqualTo(("Bearer " + (this.accessToken.getTokenValue())));
    }

    // gh-5500
    @Test
    public void loadUserWhenAuthenticationMethodFormSuccessResponseThenHttpMethodPost() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"sub\": \"subject1\",\n" + "   \"name\": \"first last\",\n") + "   \"given_name\": \"first\",\n") + "   \"family_name\": \"last\",\n") + "   \"preferred_username\": \"user1\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(FORM).build();
        this.userService.loadUser(new OidcUserRequest(clientRegistration, this.accessToken, this.idToken));
        RecordedRequest request = this.server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(POST.name());
        assertThat(request.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(request.getHeader(CONTENT_TYPE)).contains(APPLICATION_FORM_URLENCODED_VALUE);
        assertThat(request.getBody().readUtf8()).isEqualTo(("access_token=" + (this.accessToken.getTokenValue())));
    }
}

