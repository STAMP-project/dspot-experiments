/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.cors;


import HttpHeaders.HOST;
import HttpStatus.OK;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class CorsTests extends BaseWebClientTests {
    @Test
    public void testPreFlightCorsRequest() {
        ClientResponse clientResponse = webClient.options().uri("/abc/123/function").header("Origin", "domain.com").header("Access-Control-Request-Method", "GET").exchange().block();
        HttpHeaders asHttpHeaders = clientResponse.headers().asHttpHeaders();
        Mono<String> bodyToMono = clientResponse.bodyToMono(String.class);
        // pre-flight request shouldn't return the response body
        assertThat(bodyToMono.block()).isNull();
        assertThat(asHttpHeaders.getAccessControlAllowOrigin()).as(("Missing header value in response: " + (HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))).isEqualTo("*");
        assertThat(asHttpHeaders.getAccessControlAllowMethods()).as(("Missing header value in response: " + (HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))).isEqualTo(Arrays.asList(new HttpMethod[]{ HttpMethod.GET }));
        assertThat(clientResponse.statusCode()).as("Pre Flight call failed.").isEqualTo(OK);
    }

    @Test
    public void testCorsRequest() {
        ClientResponse clientResponse = webClient.get().uri("/abc/123/function").header("Origin", "domain.com").header(HOST, "www.path.org").exchange().block();
        HttpHeaders asHttpHeaders = clientResponse.headers().asHttpHeaders();
        Mono<String> bodyToMono = clientResponse.bodyToMono(String.class);
        assertThat(bodyToMono.block()).isNotNull();
        assertThat(asHttpHeaders.getAccessControlAllowOrigin()).as(("Missing header value in response: " + (HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))).isEqualTo("*");
        assertThat(clientResponse.statusCode()).as("CORS request failed.").isEqualTo(OK);
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {}
}

