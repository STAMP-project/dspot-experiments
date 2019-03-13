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
package org.springframework.boot.docs.context.properties.bind;


import java.time.Duration;
import java.util.function.Consumer;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link AppSystemProperties}.
 *
 * @author Stephane Nicoll
 */
public class AppSystemPropertiesTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(AppSystemPropertiesTests.Config.class);

    @Test
    public void bindWithDefaultUnit() {
        this.contextRunner.withPropertyValues("app.system.session-timeout=40", "app.system.read-timeout=5000").run(assertBinding(( properties) -> {
            assertThat(properties.getSessionTimeout()).isEqualTo(Duration.ofSeconds(40));
            assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofMillis(5000));
        }));
    }

    @Test
    public void bindWithExplicitUnit() {
        this.contextRunner.withPropertyValues("app.system.session-timeout=1h", "app.system.read-timeout=5s").run(assertBinding(( properties) -> {
            assertThat(properties.getSessionTimeout()).isEqualTo(Duration.ofMinutes(60));
            assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofMillis(5000));
        }));
    }

    @Test
    public void bindWithIso8601Format() {
        this.contextRunner.withPropertyValues("app.system.session-timeout=PT15S", "app.system.read-timeout=PT0.5S").run(assertBinding(( properties) -> {
            assertThat(properties.getSessionTimeout()).isEqualTo(Duration.ofSeconds(15));
            assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofMillis(500));
        }));
    }

    @Configuration
    @EnableConfigurationProperties(AppSystemProperties.class)
    static class Config {}
}

