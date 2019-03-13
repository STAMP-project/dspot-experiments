/**
 * Copyright 2017 Dan Maas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.resilience4j.retry;


import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class RetryRegistryTest {
    private RetryRegistry retryRegistry;

    @Test
    public void shouldReturnTheCorrectName() {
        Retry retry = retryRegistry.retry("testName");
        Assertions.assertThat(retry).isNotNull();
        Assertions.assertThat(retry.getName()).isEqualTo("testName");
    }

    @Test
    public void shouldBeTheSameRetry() {
        Retry retry = retryRegistry.retry("testName");
        Retry retry2 = retryRegistry.retry("testName");
        Assertions.assertThat(retry).isSameAs(retry2);
        Assertions.assertThat(retryRegistry.getAllRetries()).hasSize(1);
    }

    @Test
    public void shouldBeNotTheSameRetry() {
        Retry retry = retryRegistry.retry("testName");
        Retry retry2 = retryRegistry.retry("otherTestName");
        Assertions.assertThat(retry).isNotSameAs(retry2);
        Assertions.assertThat(retryRegistry.getAllRetries()).hasSize(2);
    }

    @Test
    public void canBuildRetryFromRegistryWithConfig() {
        RetryConfig config = RetryConfig.custom().maxAttempts(1000).waitDuration(Duration.ofSeconds(300)).build();
        Retry retry = retryRegistry.retry("testName", config);
        Assertions.assertThat(retry).isNotNull();
        Assertions.assertThat(retryRegistry.getAllRetries()).hasSize(1);
    }

    @Test
    public void canBuildRetryFromRegistryWithConfigSupplier() {
        RetryConfig config = RetryConfig.custom().maxAttempts(1000).waitDuration(Duration.ofSeconds(300)).build();
        Retry retry = retryRegistry.retry("testName", () -> config);
        Assertions.assertThat(retry).isNotNull();
        Assertions.assertThat(retryRegistry.getAllRetries()).hasSize(1);
    }

    @Test
    public void canBuildRetryRegistryWithConfig() {
        RetryConfig config = RetryConfig.custom().maxAttempts(1000).waitDuration(Duration.ofSeconds(300)).build();
        retryRegistry = RetryRegistry.of(config);
        Retry retry = retryRegistry.retry("testName", () -> config);
        Assertions.assertThat(retry).isNotNull();
        Assertions.assertThat(retryRegistry.getAllRetries()).hasSize(1);
    }
}

