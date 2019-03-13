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


import AuthorizationGrantType.IMPLICIT;
import ClientRegistration.Builder;
import IdTokenClaimNames.SUB;
import OAuth2AccessToken.TokenType;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
public class OidcUserRequestUtilsTests {
    private Builder registration = TestClientRegistrations.clientRegistration();

    OidcIdToken idToken = new OidcIdToken("token123", Instant.now(), Instant.now().plusSeconds(3600), Collections.singletonMap(SUB, "sub123"));

    OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, "token", Instant.now(), Instant.now().plus(Duration.ofDays(1)), Collections.singleton("read:user"));

    @Test
    public void shouldRetrieveUserInfoWhenEndpointDefinedAndScopesOverlapThenTrue() {
        assertThat(OidcUserRequestUtils.shouldRetrieveUserInfo(userRequest())).isTrue();
    }

    @Test
    public void shouldRetrieveUserInfoWhenNoUserInfoUriThenFalse() {
        this.registration.userInfoUri(null);
        assertThat(OidcUserRequestUtils.shouldRetrieveUserInfo(userRequest())).isFalse();
    }

    @Test
    public void shouldRetrieveUserInfoWhenDifferentScopesThenFalse() {
        this.registration.scope("notintoken");
        assertThat(OidcUserRequestUtils.shouldRetrieveUserInfo(userRequest())).isFalse();
    }

    @Test
    public void shouldRetrieveUserInfoWhenNotAuthorizationCodeThenFalse() {
        this.registration.authorizationGrantType(IMPLICIT);
        assertThat(OidcUserRequestUtils.shouldRetrieveUserInfo(userRequest())).isFalse();
    }
}

