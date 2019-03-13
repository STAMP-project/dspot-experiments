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
import HttpHeaders.AUTHORIZATION;
import HttpMethod.GET;
import HttpMethod.POST;
import MediaType.APPLICATION_JSON;
import OAuth2ParameterNames.ACCESS_TOKEN;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.util.MultiValueMap;


/**
 * Tests for {@link OAuth2UserRequestEntityConverter}.
 *
 * @author Joe Grandja
 */
public class OAuth2UserRequestEntityConverterTests {
    private OAuth2UserRequestEntityConverter converter = new OAuth2UserRequestEntityConverter();

    @SuppressWarnings("unchecked")
    @Test
    public void convertWhenAuthenticationMethodHeaderThenGetRequest() {
        ClientRegistration clientRegistration = TestClientRegistrations.clientRegistration().build();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, this.createAccessToken());
        RequestEntity<?> requestEntity = this.converter.convert(userRequest);
        assertThat(requestEntity.getMethod()).isEqualTo(GET);
        assertThat(requestEntity.getUrl().toASCIIString()).isEqualTo(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri());
        HttpHeaders headers = requestEntity.getHeaders();
        assertThat(headers.getAccept()).contains(APPLICATION_JSON);
        assertThat(headers.getFirst(AUTHORIZATION)).isEqualTo(("Bearer " + (userRequest.getAccessToken().getTokenValue())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertWhenAuthenticationMethodFormThenPostRequest() {
        ClientRegistration clientRegistration = TestClientRegistrations.clientRegistration().userInfoAuthenticationMethod(FORM).build();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, this.createAccessToken());
        RequestEntity<?> requestEntity = this.converter.convert(userRequest);
        assertThat(requestEntity.getMethod()).isEqualTo(POST);
        assertThat(requestEntity.getUrl().toASCIIString()).isEqualTo(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri());
        HttpHeaders headers = requestEntity.getHeaders();
        assertThat(headers.getAccept()).contains(APPLICATION_JSON);
        assertThat(headers.getContentType()).isEqualTo(MediaType.valueOf(((APPLICATION_FORM_URLENCODED_VALUE) + ";charset=UTF-8")));
        MultiValueMap<String, String> formParameters = ((MultiValueMap<String, String>) (requestEntity.getBody()));
        assertThat(formParameters.getFirst(ACCESS_TOKEN)).isEqualTo(userRequest.getAccessToken().getTokenValue());
    }
}

