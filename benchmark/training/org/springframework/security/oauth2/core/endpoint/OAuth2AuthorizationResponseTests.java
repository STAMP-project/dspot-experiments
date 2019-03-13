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
 * Tests for {@link OAuth2AuthorizationResponse}.
 *
 * @author Joe Grandja
 */
public class OAuth2AuthorizationResponseTests {
    private static final String AUTH_CODE = "auth-code";

    private static final String REDIRECT_URI = "http://example.com";

    private static final String STATE = "state";

    private static final String ERROR_CODE = "error-code";

    private static final String ERROR_DESCRIPTION = "error-description";

    private static final String ERROR_URI = "error-uri";

    @Test(expected = IllegalArgumentException.class)
    public void buildSuccessResponseWhenAuthCodeIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationResponse.success(null).redirectUri(OAuth2AuthorizationResponseTests.REDIRECT_URI).state(OAuth2AuthorizationResponseTests.STATE).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildSuccessResponseWhenRedirectUriIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationResponse.success(OAuth2AuthorizationResponseTests.AUTH_CODE).redirectUri(null).state(OAuth2AuthorizationResponseTests.STATE).build();
    }

    @Test
    public void buildSuccessResponseWhenStateIsNullThenDoesNotThrowAnyException() {
        assertThatCode(() -> OAuth2AuthorizationResponse.success(AUTH_CODE).redirectUri(REDIRECT_URI).state(null).build()).doesNotThrowAnyException();
    }

    @Test
    public void buildSuccessResponseWhenAllAttributesProvidedThenAllAttributesAreSet() {
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(OAuth2AuthorizationResponseTests.AUTH_CODE).redirectUri(OAuth2AuthorizationResponseTests.REDIRECT_URI).state(OAuth2AuthorizationResponseTests.STATE).build();
        assertThat(authorizationResponse.getCode()).isEqualTo(OAuth2AuthorizationResponseTests.AUTH_CODE);
        assertThat(authorizationResponse.getRedirectUri()).isEqualTo(OAuth2AuthorizationResponseTests.REDIRECT_URI);
        assertThat(authorizationResponse.getState()).isEqualTo(OAuth2AuthorizationResponseTests.STATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildSuccessResponseWhenErrorCodeIsSetThenThrowIllegalArgumentException() {
        OAuth2AuthorizationResponse.success(OAuth2AuthorizationResponseTests.AUTH_CODE).redirectUri(OAuth2AuthorizationResponseTests.REDIRECT_URI).state(OAuth2AuthorizationResponseTests.STATE).errorCode(OAuth2AuthorizationResponseTests.ERROR_CODE).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildErrorResponseWhenErrorCodeIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationResponse.error(null).redirectUri(OAuth2AuthorizationResponseTests.REDIRECT_URI).state(OAuth2AuthorizationResponseTests.STATE).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildErrorResponseWhenRedirectUriIsNullThenThrowIllegalArgumentException() {
        OAuth2AuthorizationResponse.error(OAuth2AuthorizationResponseTests.ERROR_CODE).redirectUri(null).state(OAuth2AuthorizationResponseTests.STATE).build();
    }

    @Test
    public void buildErrorResponseWhenStateIsNullThenDoesNotThrowAnyException() {
        assertThatCode(() -> OAuth2AuthorizationResponse.error(ERROR_CODE).redirectUri(REDIRECT_URI).state(null).build()).doesNotThrowAnyException();
    }

    @Test
    public void buildErrorResponseWhenAllAttributesProvidedThenAllAttributesAreSet() {
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.error(OAuth2AuthorizationResponseTests.ERROR_CODE).errorDescription(OAuth2AuthorizationResponseTests.ERROR_DESCRIPTION).errorUri(OAuth2AuthorizationResponseTests.ERROR_URI).redirectUri(OAuth2AuthorizationResponseTests.REDIRECT_URI).state(OAuth2AuthorizationResponseTests.STATE).build();
        assertThat(authorizationResponse.getError().getErrorCode()).isEqualTo(OAuth2AuthorizationResponseTests.ERROR_CODE);
        assertThat(authorizationResponse.getError().getDescription()).isEqualTo(OAuth2AuthorizationResponseTests.ERROR_DESCRIPTION);
        assertThat(authorizationResponse.getError().getUri()).isEqualTo(OAuth2AuthorizationResponseTests.ERROR_URI);
        assertThat(authorizationResponse.getRedirectUri()).isEqualTo(OAuth2AuthorizationResponseTests.REDIRECT_URI);
        assertThat(authorizationResponse.getState()).isEqualTo(OAuth2AuthorizationResponseTests.STATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildErrorResponseWhenAuthCodeIsSetThenThrowIllegalArgumentException() {
        OAuth2AuthorizationResponse.error(OAuth2AuthorizationResponseTests.ERROR_CODE).redirectUri(OAuth2AuthorizationResponseTests.REDIRECT_URI).state(OAuth2AuthorizationResponseTests.STATE).code(OAuth2AuthorizationResponseTests.AUTH_CODE).build();
    }
}

