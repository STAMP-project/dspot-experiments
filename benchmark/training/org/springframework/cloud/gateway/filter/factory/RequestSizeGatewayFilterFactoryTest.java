/**
 * Copyright 2018-2019 the original author or authors.
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


import HttpStatus.PAYLOAD_TOO_LARGE;
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


/**
 *
 *
 * @author Arpan Das
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class RequestSizeGatewayFilterFactoryTest extends BaseWebClientTests {
    private static final String responseMesssage = "Request size is larger than permissible limit. Request size is 6.0 MB " + "where permissible limit is 5.0 MB";

    @Test
    public void setRequestSizeFilterWorks() {
        testClient.get().uri("/headers").header("Host", "www.setrequestsize.org").header("content-length", "6000000").exchange().expectStatus().isEqualTo(PAYLOAD_TOO_LARGE).expectHeader().valueMatches("errorMessage", RequestSizeGatewayFilterFactoryTest.responseMesssage);
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Value("${test.uri}")
        String uri;

        @Bean
        public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("test_request_size", ( r) -> r.order((-1)).host("**.setrequestsize.org").filters(( f) -> f.setRequestSize(5000000L)).uri(uri)).build();
        }
    }
}

