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
package org.springframework.cloud.gateway.filter.factory;


import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class AddResponseParameterGatewayFilterFactoryTests extends BaseWebClientTests {
    @Test
    public void testResposneParameterFilter() {
        URI uri = UriComponentsBuilder.fromUriString(((this.baseUri) + "/get")).build(true).toUri();
        String host = "www.addresponseparamjava.org";
        String expectedValue = "myresponsevalue";
        testClient.get().uri(uri).header("Host", host).exchange().expectHeader().valueEquals("example", expectedValue);
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Value("${test.uri}")
        String uri;

        @Bean
        public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("add_response_param_java_test", ( r) -> r.path("/get").and().host("**.addresponseparamjava.org").filters(( f) -> f.prefixPath("/httpbin").addResponseHeader("example", "myresponsevalue")).uri(uri)).build();
        }
    }
}

