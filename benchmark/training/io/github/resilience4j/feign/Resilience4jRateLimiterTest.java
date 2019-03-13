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


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.FeignException;
import io.github.resilience4j.feign.test.TestService;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.time.Duration;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the integration of the {@link Resilience4jFeign} with {@link RateLimiter}
 */
public class Resilience4jRateLimiterTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private static final RateLimiterConfig config = RateLimiterConfig.custom().timeoutDuration(Duration.ofMillis(50)).limitRefreshPeriod(Duration.ofSeconds(5)).limitForPeriod(1).build();

    private TestService testService;

    @Test
    public void testSuccessfulCall() throws Exception {
        setupStub(200);
        testService.greeting();
        verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test(expected = RequestNotPermitted.class)
    public void testRatelimterLimiting() throws Exception {
        setupStub(200);
        testService.greeting();
        testService.greeting();
        verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test(expected = RequestNotPermitted.class)
    public void testRatelimterNotLimiting() throws Exception {
        setupStub(200);
        testService.greeting();
        Thread.sleep(1000);
        testService.greeting();
        Thread.sleep(1000);
        testService.greeting();
        verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test(expected = FeignException.class)
    public void testFailedHttpCall() throws Exception {
        setupStub(400);
        testService.greeting();
    }
}

