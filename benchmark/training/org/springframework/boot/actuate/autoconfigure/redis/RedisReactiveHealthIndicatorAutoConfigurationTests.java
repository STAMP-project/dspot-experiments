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
package org.springframework.boot.actuate.autoconfigure.redis;


import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * Tests for {@link RedisReactiveHealthIndicatorAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class RedisReactiveHealthIndicatorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(RedisAutoConfiguration.class, RedisReactiveHealthIndicatorAutoConfiguration.class, HealthIndicatorAutoConfiguration.class));

    @Test
    public void runShouldCreateIndicator() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenDisabledShouldNotCreateIndicator() {
        this.contextRunner.withPropertyValues("management.health.redis.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).hasSingleBean(.class));
    }
}

