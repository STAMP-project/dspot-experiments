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
import HttpHeaders.WWW_AUTHENTICATE;
import HttpMethod.GET;
import HttpMethod.POST;
import MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import MediaType.APPLICATION_JSON_VALUE;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;


/**
 * Tests for {@link DefaultOAuth2UserService}.
 *
 * @author Joe Grandja
 */
public class DefaultOAuth2UserServiceTests {
    private Builder clientRegistrationBuilder;

    private OAuth2AccessToken accessToken;

    private DefaultOAuth2UserService userService = new DefaultOAuth2UserService();

    private MockWebServer server;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void setRequestEntityConverterWhenNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        this.userService.setRequestEntityConverter(null);
    }

    @Test
    public void setRestOperationsWhenNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        this.userService.setRestOperations(null);
    }

    @Test
    public void loadUserWhenUserRequestIsNullThenThrowIllegalArgumentException() {
        this.exception.expect(IllegalArgumentException.class);
        this.userService.loadUser(null);
    }

    @Test
    public void loadUserWhenUserInfoUriIsNullThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("missing_user_info_uri"));
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    @Test
    public void loadUserWhenUserNameAttributeNameIsNullThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("missing_user_name_attribute"));
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri("http://provider.com/user").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    @Test
    public void loadUserWhenUserInfoSuccessResponseThenReturnUser() {
        String userInfoResponse = "{\n" + (((((("\t\"user-name\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        OAuth2User user = this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
        assertThat(user.getName()).isEqualTo("user1");
        assertThat(user.getAttributes().size()).isEqualTo(6);
        assertThat(user.getAttributes().get("user-name")).isEqualTo("user1");
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

    @Test
    public void loadUserWhenUserInfoSuccessResponseInvalidThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource"));
        String userInfoResponse = "{\n" + ((((("\t\"user-name\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n");
        // "}\n";		// Make the JSON invalid/malformed
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    @Test
    public void loadUserWhenUserInfoErrorResponseWwwAuthenticateHeaderThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource"));
        this.exception.expectMessage(CoreMatchers.containsString("Error Code: insufficient_scope, Error Description: The access token expired"));
        String wwwAuthenticateHeader = "Bearer realm=\"auth-realm\" error=\"insufficient_scope\" error_description=\"The access token expired\"";
        MockResponse response = new MockResponse();
        response.setHeader(WWW_AUTHENTICATE, wwwAuthenticateHeader);
        response.setResponseCode(400);
        this.server.enqueue(response);
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    @Test
    public void loadUserWhenUserInfoErrorResponseThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource"));
        this.exception.expectMessage(CoreMatchers.containsString("Error Code: invalid_token"));
        String userInfoErrorResponse = "{\n" + ("   \"error\": \"invalid_token\"\n" + "}\n");
        this.server.enqueue(jsonResponse(userInfoErrorResponse).setResponseCode(400));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    @Test
    public void loadUserWhenServerErrorThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource: 500 Server Error"));
        this.server.enqueue(new MockResponse().setResponseCode(500));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    @Test
    public void loadUserWhenUserInfoUriInvalidThenThrowOAuth2AuthenticationException() {
        this.exception.expect(OAuth2AuthenticationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("[invalid_user_info_response] An error occurred while attempting to retrieve the UserInfo Resource"));
        String userInfoUri = "http://invalid-provider.com/user";
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
    }

    // gh-5294
    @Test
    public void loadUserWhenUserInfoSuccessResponseThenAcceptHeaderJson() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"user-name\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
        assertThat(this.server.takeRequest(1, TimeUnit.SECONDS).getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
    }

    // gh-5500
    @Test
    public void loadUserWhenAuthenticationMethodHeaderSuccessResponseThenHttpMethodGet() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"user-name\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(HEADER).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
        RecordedRequest request = this.server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(GET.name());
        assertThat(request.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(request.getHeader(AUTHORIZATION)).isEqualTo(("Bearer " + (this.accessToken.getTokenValue())));
    }

    // gh-5500
    @Test
    public void loadUserWhenAuthenticationMethodFormSuccessResponseThenHttpMethodPost() throws Exception {
        String userInfoResponse = "{\n" + (((((("\t\"user-name\": \"user1\",\n" + "   \"first-name\": \"first\",\n") + "   \"last-name\": \"last\",\n") + "   \"middle-name\": \"middle\",\n") + "   \"address\": \"address\",\n") + "   \"email\": \"user1@example.com\"\n") + "}\n");
        this.server.enqueue(jsonResponse(userInfoResponse));
        String userInfoUri = this.server.url("/user").toString();
        ClientRegistration clientRegistration = this.clientRegistrationBuilder.userInfoUri(userInfoUri).userInfoAuthenticationMethod(FORM).userNameAttributeName("user-name").build();
        this.userService.loadUser(new OAuth2UserRequest(clientRegistration, this.accessToken));
        RecordedRequest request = this.server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(POST.name());
        assertThat(request.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_VALUE);
        assertThat(request.getHeader(CONTENT_TYPE)).contains(APPLICATION_FORM_URLENCODED_VALUE);
        assertThat(request.getBody().readUtf8()).isEqualTo(("access_token=" + (this.accessToken.getTokenValue())));
    }
}

