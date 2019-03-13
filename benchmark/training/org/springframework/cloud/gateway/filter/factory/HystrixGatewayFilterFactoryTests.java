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


import HttpStatus.GATEWAY_TIMEOUT;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "debug=true")
@ContextConfiguration(classes = HystrixTestConfig.class)
@DirtiesContext
public class HystrixGatewayFilterFactoryTests extends BaseWebClientTests {
    @Test
    public void hystrixFilterWorks() {
        testClient.get().uri("/get").header("Host", "www.hystrixsuccess.org").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "hystrix_success_test");
    }

    @Test
    public void hystrixFilterTimesout() {
        testClient.get().uri("/delay/3").header("Host", "www.hystrixfailure.org").exchange().expectStatus().isEqualTo(GATEWAY_TIMEOUT).expectBody().jsonPath("$.status").isEqualTo(String.valueOf(GATEWAY_TIMEOUT.value()));
    }

    /* Tests that timeouts bubbling from the underpinning WebClient are treated the same
    as Hystrix timeouts in terms of outside response. (Internally, timeouts from the
    WebClient are seen as command failures and trigger the opening of circuit breakers
    the same way timeouts do; it may be confusing in terms of the Hystrix metrics
    though)
     */
    @Test
    public void hystrixTimeoutFromWebClient() {
        testClient.get().uri("/delay/10").header("Host", "www.hystrixresponsestall.org").exchange().expectStatus().isEqualTo(GATEWAY_TIMEOUT);
    }

    @Test
    public void hystrixFilterFallback() {
        testClient.get().uri("/delay/3?a=b").header("Host", "www.hystrixfallback.org").exchange().expectStatus().isOk().expectBody().json("{\"from\":\"fallbackcontroller\"}");
    }

    @Test
    public void hystrixFilterExceptionFallback() {
        testClient.get().uri("/delay/3").header("Host", "www.hystrixexceptionfallback.org").exchange().expectStatus().isOk().expectHeader().value(ExceptionFallbackHandler.RETRIEVED_EXCEPTION, StringContains.containsString("HystrixTimeoutException"));
    }

    @Test
    public void hystrixFilterWorksJavaDsl() {
        testClient.get().uri("/get").header("Host", "www.hystrixjava.org").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "hystrix_java");
    }

    @Test
    public void hystrixFilterFallbackJavaDsl() {
        testClient.get().uri("/delay/3").header("Host", "www.hystrixjava.org").exchange().expectStatus().isOk().expectBody().json("{\"from\":\"fallbackcontroller2\"}");
    }

    @Test
    public void hystrixFilterConnectFailure() {
        testClient.get().uri("/delay/3").header("Host", "www.hystrixconnectfail.org").exchange().expectStatus().is5xxServerError();
    }

    @Test
    public void hystrixFilterErrorPage() {
        testClient.get().uri("/delay/3").header("Host", "www.hystrixconnectfail.org").accept(TEXT_HTML).exchange().expectStatus().is5xxServerError().expectBody().consumeWith(( res) -> {
            final String body = new String(res.getResponseBody(), StandardCharsets.UTF_8);
            Assert.isTrue(body.contains("<h1>Whitelabel Error Page</h1>"), "Cannot find the expected white-label error page title in the response");
            Assert.isTrue(body.contains("(type=Internal Server Error, status=500)"), "Cannot find the expected error status report in the response");
        });
    }
}

