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
package org.springframework.security.oauth2.core.web.reactive.function;


import BodyExtractor.Context;
import MediaType.APPLICATION_JSON;
import OAuth2AccessToken.TokenType.BEARER;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.mock.http.client.reactive.MockClientHttpResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
public class OAuth2BodyExtractorsTests {
    private Context context;

    private Map<String, Object> hints;

    @Test
    public void oauth2AccessTokenResponseWhenInvalidJsonThenException() {
        BodyExtractor<Mono<OAuth2AccessTokenResponse>, ReactiveHttpInputMessage> extractor = OAuth2BodyExtractors.oauth2AccessTokenResponse();
        MockClientHttpResponse response = new MockClientHttpResponse(HttpStatus.OK);
        response.getHeaders().setContentType(APPLICATION_JSON);
        response.setBody("{");
        Mono<OAuth2AccessTokenResponse> result = extractor.extract(response, this.context);
        assertThatCode(() -> result.block()).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void oauth2AccessTokenResponseWhenValidThenCreated() throws Exception {
        BodyExtractor<Mono<OAuth2AccessTokenResponse>, ReactiveHttpInputMessage> extractor = OAuth2BodyExtractors.oauth2AccessTokenResponse();
        MockClientHttpResponse response = new MockClientHttpResponse(HttpStatus.OK);
        response.getHeaders().setContentType(APPLICATION_JSON);
        response.setBody(("{\n" + ((((("       \"access_token\":\"2YotnFZFEjr1zCsicMWpAA\",\n" + "       \"token_type\":\"Bearer\",\n") + "       \"expires_in\":3600,\n") + "       \"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\",\n") + "       \"example_parameter\":\"example_value\"\n") + "     }")));
        Instant now = Instant.now();
        OAuth2AccessTokenResponse result = extractor.extract(response, this.context).block();
        assertThat(result.getAccessToken().getTokenValue()).isEqualTo("2YotnFZFEjr1zCsicMWpAA");
        assertThat(result.getAccessToken().getTokenType()).isEqualTo(BEARER);
        assertThat(result.getAccessToken().getExpiresAt()).isBetween(now.plusSeconds(3600), now.plusSeconds((3600 + 2)));
        assertThat(result.getRefreshToken().getTokenValue()).isEqualTo("tGzv3JOkF0XG5Qx2TlKWIA");
        assertThat(result.getAdditionalParameters()).containsEntry("example_parameter", "example_value");
    }

    // gh-6087
    @Test
    public void oauth2AccessTokenResponseWhenMultipleAttributeTypesThenCreated() throws Exception {
        BodyExtractor<Mono<OAuth2AccessTokenResponse>, ReactiveHttpInputMessage> extractor = OAuth2BodyExtractors.oauth2AccessTokenResponse();
        MockClientHttpResponse response = new MockClientHttpResponse(HttpStatus.OK);
        response.getHeaders().setContentType(APPLICATION_JSON);
        response.setBody(("{\n" + (((((("       \"access_token\":\"2YotnFZFEjr1zCsicMWpAA\",\n" + "       \"token_type\":\"Bearer\",\n") + "       \"expires_in\":3600,\n") + "       \"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\",\n") + "       \"subjson\":{}, \n") + "\t\t  \"list\":[]  \n") + "     }")));
        Instant now = Instant.now();
        OAuth2AccessTokenResponse result = extractor.extract(response, this.context).block();
        assertThat(result.getAccessToken().getTokenValue()).isEqualTo("2YotnFZFEjr1zCsicMWpAA");
        assertThat(result.getAccessToken().getTokenType()).isEqualTo(BEARER);
        assertThat(result.getAccessToken().getExpiresAt()).isBetween(now.plusSeconds(3600), now.plusSeconds((3600 + 2)));
        assertThat(result.getRefreshToken().getTokenValue()).isEqualTo("tGzv3JOkF0XG5Qx2TlKWIA");
        assertThat(result.getAdditionalParameters().get("subjson")).isInstanceOfAny(Map.class);
        assertThat(result.getAdditionalParameters().get("list")).isInstanceOfAny(List.class);
    }
}

