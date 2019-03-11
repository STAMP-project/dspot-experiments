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


import java.util.Map;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;


/**
 * Tests for {@link OidcUserRequest}.
 *
 * @author Joe Grandja
 */
public class OidcUserRequestTests {
    private ClientRegistration clientRegistration;

    private OAuth2AccessToken accessToken;

    private OidcIdToken idToken;

    private Map<String, Object> additionalParameters;

    @Test
    public void constructorWhenClientRegistrationIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OidcUserRequest(null, this.accessToken, this.idToken)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenAccessTokenIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OidcUserRequest(this.clientRegistration, null, this.idToken)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenIdTokenIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OidcUserRequest(this.clientRegistration, this.accessToken, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenAllParametersProvidedAndValidThenCreated() {
        OidcUserRequest userRequest = new OidcUserRequest(this.clientRegistration, this.accessToken, this.idToken, this.additionalParameters);
        assertThat(userRequest.getClientRegistration()).isEqualTo(this.clientRegistration);
        assertThat(userRequest.getAccessToken()).isEqualTo(this.accessToken);
        assertThat(userRequest.getIdToken()).isEqualTo(this.idToken);
        assertThat(userRequest.getAdditionalParameters()).containsAllEntriesOf(this.additionalParameters);
    }
}

