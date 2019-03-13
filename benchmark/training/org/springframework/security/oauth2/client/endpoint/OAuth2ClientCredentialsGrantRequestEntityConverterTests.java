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
package org.springframework.security.oauth2.client.endpoint;


import AuthorizationGrantType.CLIENT_CREDENTIALS;
import HttpHeaders.AUTHORIZATION;
import HttpMethod.POST;
import MediaType.APPLICATION_JSON_UTF8;
import OAuth2ParameterNames.GRANT_TYPE;
import OAuth2ParameterNames.SCOPE;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.MultiValueMap;


/**
 * Tests for {@link OAuth2ClientCredentialsGrantRequestEntityConverter}.
 *
 * @author Joe Grandja
 */
public class OAuth2ClientCredentialsGrantRequestEntityConverterTests {
    private OAuth2ClientCredentialsGrantRequestEntityConverter converter = new OAuth2ClientCredentialsGrantRequestEntityConverter();

    private OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest;

    @SuppressWarnings("unchecked")
    @Test
    public void convertWhenGrantRequestValidThenConverts() {
        RequestEntity<?> requestEntity = this.converter.convert(this.clientCredentialsGrantRequest);
        ClientRegistration clientRegistration = this.clientCredentialsGrantRequest.getClientRegistration();
        assertThat(requestEntity.getMethod()).isEqualTo(POST);
        assertThat(requestEntity.getUrl().toASCIIString()).isEqualTo(clientRegistration.getProviderDetails().getTokenUri());
        HttpHeaders headers = requestEntity.getHeaders();
        assertThat(headers.getAccept()).contains(APPLICATION_JSON_UTF8);
        assertThat(headers.getContentType()).isEqualTo(MediaType.valueOf(((APPLICATION_FORM_URLENCODED_VALUE) + ";charset=UTF-8")));
        assertThat(headers.getFirst(AUTHORIZATION)).startsWith("Basic ");
        MultiValueMap<String, String> formParameters = ((MultiValueMap<String, String>) (requestEntity.getBody()));
        assertThat(formParameters.getFirst(GRANT_TYPE)).isEqualTo(CLIENT_CREDENTIALS.getValue());
        assertThat(formParameters.getFirst(SCOPE)).isEqualTo("read write");
    }
}

