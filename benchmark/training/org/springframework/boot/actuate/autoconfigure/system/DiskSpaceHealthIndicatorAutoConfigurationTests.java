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
package org.springframework.boot.actuate.autoconfigure.system;


import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.util.unit.DataSize;


/**
 * Tests for {@link DiskSpaceHealthIndicatorAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class DiskSpaceHealthIndicatorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DiskSpaceHealthIndicatorAutoConfiguration.class, HealthIndicatorAutoConfiguration.class));

    @Test
    public void runShouldCreateIndicator() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void thresholdMustBePositive() {
        this.contextRunner.withPropertyValues("management.health.diskspace.threshold=-10MB").run(( context) -> assertThat(context).hasFailed().getFailure().hasMessageContaining("Failed to bind properties under 'management.health.diskspace'"));
    }

    @Test
    public void thresholdCanBeCustomized() {
        this.contextRunner.withPropertyValues("management.health.diskspace.threshold=20MB").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).hasFieldOrPropertyWithValue("threshold", DataSize.ofMegabytes(20));
        });
    }

    @Test
    public void runWhenDisabledShouldNotCreateIndicator() {
        this.contextRunner.withPropertyValues("management.health.diskspace.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class).hasSingleBean(.class));
    }
}

