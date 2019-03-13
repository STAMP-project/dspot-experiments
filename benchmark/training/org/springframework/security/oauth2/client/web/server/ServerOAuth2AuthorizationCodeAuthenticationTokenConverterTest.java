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
package org.springframework.security.oauth2.client.web.server;


import AuthorizationGrantType.AUTHORIZATION_CODE;
import ClientAuthenticationMethod.BASIC;
import OAuth2AuthorizationRequest.Builder;
import OAuth2ParameterNames.CODE;
import OAuth2ParameterNames.ERROR;
import OAuth2ParameterNames.REGISTRATION_ID;
import ServerOAuth2AuthorizationCodeAuthenticationTokenConverter.CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerOAuth2AuthorizationCodeAuthenticationTokenConverterTest {
    @Mock
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private ServerAuthorizationRequestRepository authorizationRequestRepository;

    private String clientRegistrationId = "github";

    private ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(this.clientRegistrationId).redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}").clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).scope("read:user").authorizationUri("https://github.com/login/oauth/authorize").tokenUri("https://github.com/login/oauth/access_token").userInfoUri("https://api.github.com/user").userNameAttributeName("id").clientName("GitHub").clientId("clientId").clientSecret("clientSecret").build();

    private Builder authorizationRequest = OAuth2AuthorizationRequest.authorizationCode().authorizationUri("https://example.com/oauth2/authorize").clientId("client-id").redirectUri("http://localhost/client-1").state("state").attributes(Collections.singletonMap(REGISTRATION_ID, this.clientRegistrationId));

    private final MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/");

    private ServerOAuth2AuthorizationCodeAuthenticationTokenConverter converter;

    @Test
    public void applyWhenAuthorizationRequestEmptyThenOAuth2AuthorizationException() {
        Mockito.when(this.authorizationRequestRepository.removeAuthorizationRequest(ArgumentMatchers.any())).thenReturn(Mono.empty());
        assertThatThrownBy(() -> applyConverter()).isInstanceOf(OAuth2AuthorizationException.class);
    }

    @Test
    public void applyWhenAttributesMissingThenOAuth2AuthorizationException() {
        this.authorizationRequest.attributes(Collections.emptyMap());
        Mockito.when(this.authorizationRequestRepository.removeAuthorizationRequest(ArgumentMatchers.any())).thenReturn(Mono.just(this.authorizationRequest.build()));
        assertThatThrownBy(() -> applyConverter()).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE);
    }

    @Test
    public void applyWhenClientRegistrationMissingThenOAuth2AuthorizationException() {
        Mockito.when(this.authorizationRequestRepository.removeAuthorizationRequest(ArgumentMatchers.any())).thenReturn(Mono.just(this.authorizationRequest.build()));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.empty());
        assertThatThrownBy(() -> applyConverter()).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE);
    }

    @Test
    public void applyWhenCodeParameterNotFoundThenErrorCode() {
        this.request.queryParam(ERROR, "error");
        Mockito.when(this.authorizationRequestRepository.removeAuthorizationRequest(ArgumentMatchers.any())).thenReturn(Mono.just(this.authorizationRequest.build()));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.clientRegistration));
        assertThat(applyConverter().getAuthorizationExchange().getAuthorizationResponse().getError().getErrorCode()).isEqualTo("error");
    }

    @Test
    public void applyWhenCodeParameterFoundThenCode() {
        this.request.queryParam(CODE, "code");
        Mockito.when(this.authorizationRequestRepository.removeAuthorizationRequest(ArgumentMatchers.any())).thenReturn(Mono.just(this.authorizationRequest.build()));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.clientRegistration));
        OAuth2AuthorizationCodeAuthenticationToken result = applyConverter();
        OAuth2AuthorizationResponse exchange = result.getAuthorizationExchange().getAuthorizationResponse();
        assertThat(exchange.getError()).isNull();
        assertThat(exchange.getCode()).isEqualTo("code");
    }
}

