/**
 * Copyright 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.resilience4j.feign;


import CircuitBreaker.Metrics;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.feign.test.TestService;
import java.time.Duration;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the integration of the {@link Resilience4jFeign} with {@link CircuitBreaker}
 */
public class Resilience4jFeignCircuitBreakerTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private static final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().ringBufferSizeInClosedState(3).waitDurationInOpenState(Duration.ofMillis(1000)).build();

    private CircuitBreaker circuitBreaker;

    private TestService testService;

    @Test
    public void testSuccessfulCall() throws Exception {
        final CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        setupStub(200);
        testService.greeting();
        verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
        assertThat(metrics.getNumberOfSuccessfulCalls()).describedAs("Successful Calls").isEqualTo(1);
    }

    @Test
    public void testFailedCall() throws Exception {
        final CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        boolean exceptionThrown = false;
        setupStub(400);
        try {
            testService.greeting();
        } catch (final FeignException ex) {
            exceptionThrown = true;
        }
        assertThat(exceptionThrown).describedAs("FeignException thrown").isTrue();
        assertThat(metrics.getNumberOfFailedCalls()).describedAs("Successful Calls").isEqualTo(1);
    }

    @Test
    public void testCircuitBreakerOpen() throws Exception {
        boolean exceptionThrown = false;
        final int threshold = (circuitBreaker.getCircuitBreakerConfig().getRingBufferSizeInClosedState()) + 1;
        setupStub(400);
        for (int i = 0; i < threshold; i++) {
            try {
                testService.greeting();
            } catch (final FeignException ex) {
                // ignore
            } catch (final CircuitBreakerOpenException ex) {
                exceptionThrown = true;
            }
        }
        assertThat(exceptionThrown).describedAs("CircuitBreakerOpenException thrown").isTrue();
        assertThat(circuitBreaker.isCallPermitted()).describedAs("CircuitBreaker Closed").isFalse();
    }

    @Test
    public void testCircuitBreakerClosed() throws Exception {
        boolean exceptionThrown = false;
        final int threshold = (circuitBreaker.getCircuitBreakerConfig().getRingBufferSizeInClosedState()) - 1;
        setupStub(400);
        for (int i = 0; i < threshold; i++) {
            try {
                testService.greeting();
            } catch (final FeignException ex) {
                // ignore
            } catch (final CircuitBreakerOpenException ex) {
                exceptionThrown = true;
            }
        }
        assertThat(exceptionThrown).describedAs("CircuitBreakerOpenException thrown").isFalse();
        assertThat(circuitBreaker.isCallPermitted()).describedAs("CircuitBreaker Closed").isTrue();
    }
}

