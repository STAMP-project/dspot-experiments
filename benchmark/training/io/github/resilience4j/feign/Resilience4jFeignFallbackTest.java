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
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.feign.test.TestService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests the integration of the {@link Resilience4jFeign} with a fallback.
 */
public class Resilience4jFeignFallbackTest {
    private static final String MOCK_URL = "http://localhost:8080/";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private TestService testService;

    private TestService testServiceFallback;

    @Test
    public void testSuccessful() throws Exception {
        setupStub(200);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isEqualTo("Hello, world!");
        Mockito.verify(testServiceFallback, Mockito.times(0)).greeting();
        Mockito.verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFallback() throws Throwable {
        final FeignDecorators decorators = FeignDecorators.builder().withFallback("not a fallback").build();
        Resilience4jFeign.builder(decorators).target(TestService.class, Resilience4jFeignFallbackTest.MOCK_URL);
    }

    @Test
    public void testFallback() throws Exception {
        setupStub(400);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isNotEqualTo("Hello, world!");
        assertThat(result).describedAs("Result").isEqualTo("fallback");
        Mockito.verify(testServiceFallback, Mockito.times(1)).greeting();
        Mockito.verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test
    public void testFallbackExceptionFilter() throws Exception {
        final TestService testServiceExceptionFallback = Mockito.mock(TestService.class);
        Mockito.when(testServiceExceptionFallback.greeting()).thenReturn("exception fallback");
        final FeignDecorators decorators = FeignDecorators.builder().withFallback(testServiceExceptionFallback, FeignException.class).withFallback(testServiceFallback).build();
        testService = Resilience4jFeign.builder(decorators).target(TestService.class, Resilience4jFeignFallbackTest.MOCK_URL);
        setupStub(400);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isNotEqualTo("Hello, world!");
        assertThat(result).describedAs("Result").isEqualTo("exception fallback");
        Mockito.verify(testServiceFallback, Mockito.times(0)).greeting();
        Mockito.verify(testServiceExceptionFallback, Mockito.times(1)).greeting();
        Mockito.verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test
    public void testFallbackExceptionFilterNotCalled() throws Exception {
        final TestService testServiceExceptionFallback = Mockito.mock(TestService.class);
        Mockito.when(testServiceExceptionFallback.greeting()).thenReturn("exception fallback");
        final FeignDecorators decorators = FeignDecorators.builder().withFallback(testServiceExceptionFallback, CircuitBreakerOpenException.class).withFallback(testServiceFallback).build();
        testService = Resilience4jFeign.builder(decorators).target(TestService.class, Resilience4jFeignFallbackTest.MOCK_URL);
        setupStub(400);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isNotEqualTo("Hello, world!");
        assertThat(result).describedAs("Result").isEqualTo("fallback");
        Mockito.verify(testServiceFallback, Mockito.times(1)).greeting();
        Mockito.verify(testServiceExceptionFallback, Mockito.times(0)).greeting();
        Mockito.verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test
    public void testFallbackFilter() throws Exception {
        final TestService testServiceFilterFallback = Mockito.mock(TestService.class);
        Mockito.when(testServiceFilterFallback.greeting()).thenReturn("filter fallback");
        final FeignDecorators decorators = FeignDecorators.builder().withFallback(testServiceFilterFallback, ( ex) -> true).withFallback(testServiceFallback).build();
        testService = Resilience4jFeign.builder(decorators).target(TestService.class, Resilience4jFeignFallbackTest.MOCK_URL);
        setupStub(400);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isNotEqualTo("Hello, world!");
        assertThat(result).describedAs("Result").isEqualTo("filter fallback");
        Mockito.verify(testServiceFallback, Mockito.times(0)).greeting();
        Mockito.verify(testServiceFilterFallback, Mockito.times(1)).greeting();
        Mockito.verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test
    public void testFallbackFilterNotCalled() throws Exception {
        final TestService testServiceFilterFallback = Mockito.mock(TestService.class);
        Mockito.when(testServiceFilterFallback.greeting()).thenReturn("filter fallback");
        final FeignDecorators decorators = FeignDecorators.builder().withFallback(testServiceFilterFallback, ( ex) -> false).withFallback(testServiceFallback).build();
        testService = Resilience4jFeign.builder(decorators).target(TestService.class, Resilience4jFeignFallbackTest.MOCK_URL);
        setupStub(400);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isNotEqualTo("Hello, world!");
        assertThat(result).describedAs("Result").isEqualTo("fallback");
        Mockito.verify(testServiceFallback, Mockito.times(1)).greeting();
        Mockito.verify(testServiceFilterFallback, Mockito.times(0)).greeting();
        Mockito.verify(1, getRequestedFor(urlPathEqualTo("/greeting")));
    }

    @Test
    public void testRevertFallback() throws Exception {
        setupStub(400);
        testService.greeting();
        setupStub(200);
        final String result = testService.greeting();
        assertThat(result).describedAs("Result").isEqualTo("Hello, world!");
        Mockito.verify(testServiceFallback, Mockito.times(1)).greeting();
        Mockito.verify(2, getRequestedFor(urlPathEqualTo("/greeting")));
    }
}

