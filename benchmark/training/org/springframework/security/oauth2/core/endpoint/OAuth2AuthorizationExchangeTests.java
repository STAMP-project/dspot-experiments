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
package org.springframework.security.oauth2.core.endpoint;


import org.junit.Test;


/**
 * Tests for {@link OAuth2AuthorizationExchange}.
 *
 * @author Joe Grandja
 */
public class OAuth2AuthorizationExchangeTests {
    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthorizationRequestIsNullThenThrowIllegalArgumentException() {
        new OAuth2AuthorizationExchange(null, TestOAuth2AuthorizationResponses.success().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthorizationResponseIsNullThenThrowIllegalArgumentException() {
        new OAuth2AuthorizationExchange(TestOAuth2AuthorizationRequests.request().build(), null);
    }

    @Test
    public void constructorWhenRequiredArgsProvidedThenCreated() {
        OAuth2AuthorizationRequest authorizationRequest = TestOAuth2AuthorizationRequests.request().build();
        OAuth2AuthorizationResponse authorizationResponse = TestOAuth2AuthorizationResponses.success().build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        assertThat(authorizationExchange.getAuthorizationRequest()).isEqualTo(authorizationRequest);
        assertThat(authorizationExchange.getAuthorizationResponse()).isEqualTo(authorizationResponse);
    }
}

