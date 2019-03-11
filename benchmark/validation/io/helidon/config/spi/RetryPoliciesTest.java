/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.spi;


import io.helidon.config.ConfigException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static java.time.Duration.ZERO;


/**
 * Tests {@link RetryPolicies}.
 */
public class RetryPoliciesTest {
    @Test
    public void repeat() throws Exception {
        CountDownLatch loadLatch = new CountDownLatch(3);
        AbstractSource source = RetryPoliciesTest.TestingSource.builder().retryPolicy(io.helidon.config.RetryPolicies.repeat(2).delay(ZERO)).loadLatch(loadLatch).dataSupplier(() -> {
            throw new RuntimeException();
        }).build();
        Assertions.assertThrows(ConfigException.class, () -> {
            source.reload();
        });
        MatcherAssert.assertThat(loadLatch.await(50, TimeUnit.MILLISECONDS), Is.is(true));
    }

    @Test
    public void testDefaultRetryPolicy() throws Exception {
        CountDownLatch loadLatch = new CountDownLatch(1);
        AbstractSource source = RetryPoliciesTest.TestingSource.builder().loadLatch(loadLatch).build();
        source.reload();
        MatcherAssert.assertThat(loadLatch.await(50, TimeUnit.MILLISECONDS), Is.is(true));
    }

    @Test
    public void testJustCallRetryPolicy() throws Exception {
        CountDownLatch loadLatch = new CountDownLatch(1);
        AbstractSource source = RetryPoliciesTest.TestingSource.builder().retryPolicy(io.helidon.config.RetryPolicies.justCall()).loadLatch(loadLatch).build();
        source.reload();
        MatcherAssert.assertThat(loadLatch.await(50, TimeUnit.MILLISECONDS), Is.is(true));
    }

    private static class TestingSource extends AbstractSource<String, Instant> {
        private final Supplier<Data<String, Instant>> dataSupplier;

        private CountDownLatch loadLatch;

        TestingSource(RetryPoliciesTest.TestingSource.TestingBuilder builder) {
            super(builder);
            this.loadLatch = builder.loadLatch;
            this.dataSupplier = builder.dataSupplier;
        }

        static RetryPoliciesTest.TestingSource.TestingBuilder builder() {
            return new RetryPoliciesTest.TestingSource.TestingBuilder();
        }

        @Override
        protected Optional<Instant> dataStamp() {
            return Optional.empty();
        }

        @Override
        protected Data<String, Instant> loadData() throws ConfigException {
            loadLatch.countDown();
            return dataSupplier.get();
        }

        private static class TestingBuilder extends Builder<RetryPoliciesTest.TestingSource.TestingBuilder, Void, RetryPoliciesTest.TestingSource> {
            public CountDownLatch loadLatch;

            private Supplier<Data<String, Instant>> dataSupplier = () -> new Data(Optional.of("nothing"), Optional.of(Instant.now()));

            TestingBuilder() {
                super(Void.class);
            }

            RetryPoliciesTest.TestingSource.TestingBuilder loadLatch(CountDownLatch loadLatch) {
                this.loadLatch = loadLatch;
                return this;
            }

            RetryPoliciesTest.TestingSource.TestingBuilder dataSupplier(Supplier<Data<String, Instant>> dataSupplier) {
                this.dataSupplier = dataSupplier;
                return this;
            }

            @Override
            public RetryPoliciesTest.TestingSource build() {
                return new RetryPoliciesTest.TestingSource(this);
            }
        }
    }
}

