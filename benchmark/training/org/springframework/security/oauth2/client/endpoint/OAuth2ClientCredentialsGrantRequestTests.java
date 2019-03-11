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
import AuthorizationGrantType.IMPLICIT;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;


/**
 * Tests for {@link OAuth2ClientCredentialsGrantRequest}.
 *
 * @author Joe Grandja
 */
public class OAuth2ClientCredentialsGrantRequestTests {
    private ClientRegistration clientRegistration;

    @Test
    public void constructorWhenClientRegistrationIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2ClientCredentialsGrantRequest(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenClientRegistrationInvalidGrantTypeThenThrowIllegalArgumentException() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("registration-1").clientId("client-1").authorizationGrantType(IMPLICIT).redirectUriTemplate("https://localhost:8080/redirect-uri").authorizationUri("https://provider.com/oauth2/auth").clientName("Client 1").build();
        assertThatThrownBy(() -> new OAuth2ClientCredentialsGrantRequest(clientRegistration)).isInstanceOf(IllegalArgumentException.class).hasMessage("clientRegistration.authorizationGrantType must be AuthorizationGrantType.CLIENT_CREDENTIALS");
    }

    @Test
    public void constructorWhenValidParametersProvidedThenCreated() {
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        assertThat(clientCredentialsGrantRequest.getClientRegistration()).isEqualTo(this.clientRegistration);
        assertThat(clientCredentialsGrantRequest.getGrantType()).isEqualTo(CLIENT_CREDENTIALS);
    }
}

