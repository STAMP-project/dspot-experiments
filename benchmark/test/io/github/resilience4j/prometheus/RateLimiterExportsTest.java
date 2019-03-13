/**
 * Copyright 2017 Oleksandr Goldobin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.prometheus;


import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import java.util.Collections;
import java.util.function.Supplier;
import org.junit.Test;


public class RateLimiterExportsTest {
    @Test
    public void testExportsRateLimiterMetrics() {
        // Given
        final CollectorRegistry registry = new CollectorRegistry();
        final RateLimiter rateLimiter = RateLimiter.ofDefaults("foo");
        RateLimiterExports.ofIterable("boo_rate_limiter", Collections.singletonList(rateLimiter)).register(registry);
        final Supplier<Map<String, Double>> values = () -> HashSet.of("available_permissions", "waiting_threads").map(( param) -> Tuple.of(param, registry.getSampleValue("boo_rate_limiter", new String[]{ "name", "param" }, new String[]{ "foo", param }))).toMap(( t) -> t);
        // When
        final Map<String, Double> initialValues = values.get();
        // Then
        assertThat(initialValues).isEqualTo(HashMap.of("available_permissions", 50.0, "waiting_threads", 0.0));
    }

    @Test
    public void testConstructors() {
        final RateLimiterRegistry registry = new io.github.resilience4j.ratelimiter.internal.InMemoryRateLimiterRegistry(RateLimiterConfig.ofDefaults());
        RateLimiterExports.ofIterable("boo_limiters", Collections.singleton(RateLimiter.ofDefaults("foo")));
        RateLimiterExports.ofRateLimiterRegistry("boo_limiters", registry);
        RateLimiterExports.ofSupplier("boo_limiters", () -> singleton(RateLimiter.ofDefaults("foo")));
        RateLimiterExports.ofIterable(Collections.singleton(RateLimiter.ofDefaults("foo")));
        RateLimiterExports.ofRateLimiterRegistry(registry);
        RateLimiterExports.ofSupplier(() -> singleton(RateLimiter.ofDefaults("foo")));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullName() {
        RateLimiterExports.ofSupplier(null, () -> singleton(RateLimiter.ofDefaults("foo")));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullSupplier() {
        RateLimiterExports.ofSupplier("boo_limiters", null);
    }
}

