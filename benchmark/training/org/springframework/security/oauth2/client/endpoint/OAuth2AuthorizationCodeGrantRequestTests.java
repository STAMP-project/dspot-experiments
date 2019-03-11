/**
 * Copyright 2002-2017 the original author or authors.
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
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;


/**
 * Tests for {@link OAuth2AuthorizationCodeGrantRequest}.
 *
 * @author Joe Grandja
 */
public class OAuth2AuthorizationCodeGrantRequestTests {
    private ClientRegistration clientRegistration;

    private OAuth2AuthorizationExchange authorizationExchange;

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenClientRegistrationIsNullThenThrowIllegalArgumentException() {
        new OAuth2AuthorizationCodeGrantRequest(null, this.authorizationExchange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthorizationExchangeIsNullThenThrowIllegalArgumentException() {
        new OAuth2AuthorizationCodeGrantRequest(this.clientRegistration, null);
    }

    @Test
    public void constructorWhenAllParametersProvidedAndValidThenCreated() {
        OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest = new OAuth2AuthorizationCodeGrantRequest(this.clientRegistration, this.authorizationExchange);
        assertThat(authorizationCodeGrantRequest.getClientRegistration()).isEqualTo(this.clientRegistration);
        assertThat(authorizationCodeGrantRequest.getAuthorizationExchange()).isEqualTo(this.authorizationExchange);
        assertThat(authorizationCodeGrantRequest.getGrantType()).isEqualTo(AUTHORIZATION_CODE);
    }
}

