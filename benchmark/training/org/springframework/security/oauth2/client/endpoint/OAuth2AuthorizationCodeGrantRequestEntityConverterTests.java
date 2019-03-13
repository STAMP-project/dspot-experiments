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
package org.springframework.security.oauth2.client.endpoint;


import AuthorizationGrantType.AUTHORIZATION_CODE;
import ClientAuthenticationMethod.BASIC;
import ClientRegistration.Builder;
import HttpHeaders.AUTHORIZATION;
import HttpMethod.POST;
import MediaType.APPLICATION_JSON_UTF8;
import OAuth2ParameterNames.CLIENT_ID;
import OAuth2ParameterNames.CODE;
import OAuth2ParameterNames.GRANT_TYPE;
import OAuth2ParameterNames.REDIRECT_URI;
import PkceParameterNames.CODE_CHALLENGE;
import PkceParameterNames.CODE_CHALLENGE_METHOD;
import PkceParameterNames.CODE_VERIFIER;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.util.MultiValueMap;


/**
 * Tests for {@link OAuth2AuthorizationCodeGrantRequestEntityConverter}.
 *
 * @author Joe Grandja
 */
public class OAuth2AuthorizationCodeGrantRequestEntityConverterTests {
    private OAuth2AuthorizationCodeGrantRequestEntityConverter converter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    private Builder clientRegistrationBuilder = ClientRegistration.withRegistrationId("registration-1").clientId("client-1").clientSecret("secret").clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate("https://client.com/callback/client-1").scope("read", "write").authorizationUri("https://provider.com/oauth2/authorize").tokenUri("https://provider.com/oauth2/token").userInfoUri("https://provider.com/user").userNameAttributeName("id").clientName("client-1");

    private OAuth2AuthorizationRequest.Builder authorizationRequestBuilder = OAuth2AuthorizationRequest.authorizationCode().clientId("client-1").state("state-1234").authorizationUri("https://provider.com/oauth2/authorize").redirectUri("https://client.com/callback/client-1").scopes(new HashSet(Arrays.asList("read", "write")));

    private OAuth2AuthorizationResponse.Builder authorizationResponseBuilder = OAuth2AuthorizationResponse.success("code-1234").state("state-1234").redirectUri("https://client.com/callback/client-1");

    @SuppressWarnings("unchecked")
    @Test
    public void convertWhenGrantRequestValidThenConverts() {
        ClientRegistration clientRegistration = clientRegistrationBuilder.build();
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestBuilder.build();
        OAuth2AuthorizationResponse authorizationResponse = authorizationResponseBuilder.build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, authorizationExchange);
        RequestEntity<?> requestEntity = this.converter.convert(authorizationCodeGrantRequest);
        assertThat(requestEntity.getMethod()).isEqualTo(POST);
        assertThat(requestEntity.getUrl().toASCIIString()).isEqualTo(clientRegistration.getProviderDetails().getTokenUri());
        HttpHeaders headers = requestEntity.getHeaders();
        assertThat(headers.getAccept()).contains(APPLICATION_JSON_UTF8);
        assertThat(headers.getContentType()).isEqualTo(MediaType.valueOf(((APPLICATION_FORM_URLENCODED_VALUE) + ";charset=UTF-8")));
        assertThat(headers.getFirst(AUTHORIZATION)).startsWith("Basic ");
        MultiValueMap<String, String> formParameters = ((MultiValueMap<String, String>) (requestEntity.getBody()));
        assertThat(formParameters.getFirst(GRANT_TYPE)).isEqualTo(AUTHORIZATION_CODE.getValue());
        assertThat(formParameters.getFirst(CODE)).isEqualTo("code-1234");
        assertThat(formParameters.getFirst(CLIENT_ID)).isNull();
        assertThat(formParameters.getFirst(REDIRECT_URI)).isEqualTo(clientRegistration.getRedirectUriTemplate());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertWhenPkceGrantRequestValidThenConverts() {
        ClientRegistration clientRegistration = clientRegistrationBuilder.clientSecret(null).build();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CODE_VERIFIER, "code-verifier-1234");
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put(CODE_CHALLENGE, "code-challenge-1234");
        additionalParameters.put(CODE_CHALLENGE_METHOD, "S256");
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestBuilder.attributes(attributes).additionalParameters(additionalParameters).build();
        OAuth2AuthorizationResponse authorizationResponse = authorizationResponseBuilder.build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, authorizationExchange);
        RequestEntity<?> requestEntity = this.converter.convert(authorizationCodeGrantRequest);
        assertThat(requestEntity.getMethod()).isEqualTo(POST);
        assertThat(requestEntity.getUrl().toASCIIString()).isEqualTo(clientRegistration.getProviderDetails().getTokenUri());
        HttpHeaders headers = requestEntity.getHeaders();
        assertThat(headers.getAccept()).contains(APPLICATION_JSON_UTF8);
        assertThat(headers.getContentType()).isEqualTo(MediaType.valueOf(((APPLICATION_FORM_URLENCODED_VALUE) + ";charset=UTF-8")));
        assertThat(headers.getFirst(AUTHORIZATION)).isNull();
        MultiValueMap<String, String> formParameters = ((MultiValueMap<String, String>) (requestEntity.getBody()));
        assertThat(formParameters.getFirst(GRANT_TYPE)).isEqualTo(AUTHORIZATION_CODE.getValue());
        assertThat(formParameters.getFirst(CODE)).isEqualTo("code-1234");
        assertThat(formParameters.getFirst(REDIRECT_URI)).isEqualTo(clientRegistration.getRedirectUriTemplate());
        assertThat(formParameters.getFirst(CLIENT_ID)).isEqualTo("client-1");
        assertThat(formParameters.getFirst(CODE_VERIFIER)).isEqualTo("code-verifier-1234");
    }
}

