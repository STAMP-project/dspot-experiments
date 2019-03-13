/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.health;


import Status.DOWN;
import Status.UP;
import java.util.Collections;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Tests for {@link ReactiveHealthIndicatorRegistryFactory}.
 *
 * @author Stephane Nicoll
 */
public class ReactiveHealthIndicatorRegistryFactoryTests {
    private static final Health UP = new Health.Builder().status(Status.UP).build();

    private static final Health DOWN = new Health.Builder().status(Status.DOWN).build();

    private final ReactiveHealthIndicatorRegistryFactory factory = new ReactiveHealthIndicatorRegistryFactory();

    @Test
    public void defaultHealthIndicatorNameFactory() {
        ReactiveHealthIndicatorRegistry registry = this.factory.createReactiveHealthIndicatorRegistry(Collections.singletonMap("myHealthIndicator", () -> Mono.just(UP)), null);
        assertThat(registry.getAll()).containsOnlyKeys("my");
    }

    @Test
    public void healthIndicatorIsAdapted() {
        ReactiveHealthIndicatorRegistry registry = this.factory.createReactiveHealthIndicatorRegistry(Collections.singletonMap("test", () -> Mono.just(UP)), Collections.singletonMap("regular", () -> DOWN));
        assertThat(registry.getAll()).containsOnlyKeys("test", "regular");
        StepVerifier.create(registry.get("regular").health()).consumeNextWith(( h) -> {
            assertThat(h.getStatus()).isEqualTo(Status.DOWN);
            assertThat(h.getDetails()).isEmpty();
        }).verifyComplete();
    }
}

