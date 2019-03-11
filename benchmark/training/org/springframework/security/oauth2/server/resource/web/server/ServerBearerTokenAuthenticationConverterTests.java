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
package org.springframework.security.oauth2.server.resource.web.server;


import HttpHeaders.AUTHORIZATION;
import java.util.Base64;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
public class ServerBearerTokenAuthenticationConverterTests {
    private static final String TEST_TOKEN = "test-token";

    private ServerBearerTokenAuthenticationConverter converter;

    @Test
    public void resolveWhenValidHeaderIsPresentThenTokenIsResolved() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").header(AUTHORIZATION, ("Bearer " + (ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN)));
        assertThat(convertToToken(request).getToken()).isEqualTo(ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN);
    }

    @Test
    public void resolveWhenLowercaseHeaderIsPresentThenTokenIsResolved() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").header(AUTHORIZATION, ("bearer " + (ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN)));
        assertThat(convertToToken(request).getToken()).isEqualTo(ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN);
    }

    @Test
    public void resolveWhenNoHeaderIsPresentThenTokenIsNotResolved() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/");
        assertThat(convertToToken(request)).isNull();
    }

    @Test
    public void resolveWhenHeaderWithWrongSchemeIsPresentThenTokenIsNotResolved() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").header(AUTHORIZATION, ("Basic " + (Base64.getEncoder().encodeToString("test:test".getBytes()))));
        assertThat(convertToToken(request)).isNull();
    }

    @Test
    public void resolveWhenHeaderWithMissingTokenIsPresentThenAuthenticationExceptionIsThrown() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").header(AUTHORIZATION, "Bearer ");
        assertThatCode(() -> convertToToken(request)).isInstanceOf(OAuth2AuthenticationException.class).hasMessageContaining("Bearer token is malformed");
    }

    @Test
    public void resolveWhenHeaderWithInvalidCharactersIsPresentThenAuthenticationExceptionIsThrown() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").header(AUTHORIZATION, "Bearer an\"invalid\"token");
        assertThatCode(() -> convertToToken(request)).isInstanceOf(OAuth2AuthenticationException.class).hasMessageContaining("Bearer token is malformed");
    }

    @Test
    public void resolveWhenValidHeaderIsPresentTogetherWithQueryParameterThenAuthenticationExceptionIsThrown() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").queryParam("access_token", ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN).header(AUTHORIZATION, ("Bearer " + (ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN)));
        assertThatCode(() -> convertToToken(request)).isInstanceOf(OAuth2AuthenticationException.class).hasMessageContaining("Found multiple bearer tokens in the request");
    }

    @Test
    public void resolveWhenQueryParameterIsPresentAndSupportedThenTokenIsResolved() {
        this.converter.setAllowUriQueryParameter(true);
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").queryParam("access_token", ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN);
        assertThat(convertToToken(request).getToken()).isEqualTo(ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN);
    }

    @Test
    public void resolveWhenQueryParameterIsPresentAndNotSupportedThenTokenIsNotResolved() {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get("/").queryParam("access_token", ServerBearerTokenAuthenticationConverterTests.TEST_TOKEN);
        assertThat(convertToToken(request)).isNull();
    }
}

