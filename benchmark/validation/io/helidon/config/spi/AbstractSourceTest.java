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


import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.config.Config;
import io.helidon.config.ConfigException;
import io.helidon.config.ConfigSources;
import io.helidon.config.PollingStrategies;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link AbstractSource}.
 */
public class AbstractSourceTest {
    private static final Optional<String> LAST_DATA = Optional.of("last data");

    private static final Optional<String> NEW_DATA = Optional.of("new data");

    private static final int TEST_DELAY_MS = 1;

    @Test
    public void testHasChanged() {
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().build();
        MatcherAssert.assertThat(testingSource.hasChanged(AbstractSourceTest.LAST_DATA, AbstractSourceTest.NEW_DATA), Matchers.is(true));
    }

    @Test
    public void testHasChangedLastEmpty() {
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().build();
        MatcherAssert.assertThat(testingSource.hasChanged(Optional.empty(), AbstractSourceTest.NEW_DATA), Matchers.is(true));
    }

    @Test
    public void testHasChangedNewEmpty() {
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().build();
        MatcherAssert.assertThat(testingSource.hasChanged(AbstractSourceTest.LAST_DATA, Optional.empty()), Matchers.is(true));
    }

    @Test
    public void testHasChangedIsSame() {
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().build();
        MatcherAssert.assertThat(testingSource.hasChanged(AbstractSourceTest.LAST_DATA, AbstractSourceTest.LAST_DATA), Matchers.is(false));
    }

    @Test
    public void testHasChangedBothEmpty() {
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().build();
        MatcherAssert.assertThat(hasChanged(Optional.empty(), Optional.empty()), Matchers.is(false));
    }

    @Test
    public void testLoadDataChangedSinceLastLoad() throws InterruptedException {
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().dataSupplier(() -> new AbstractSource.Data<>(Optional.of(Instant.now().toString()), Optional.of(Instant.now()))).build();
        Optional<AbstractSource.Data<String, Instant>> stringData = loadDataChangedSinceLastLoad();
        TimeUnit.MILLISECONDS.sleep(AbstractSourceTest.TEST_DELAY_MS);// Make sure the timestamps will differ.

        MatcherAssert.assertThat(stringData.get().stamp(), Matchers.is(CoreMatchers.not(loadDataChangedSinceLastLoad().get().stamp())));
    }

    @Test
    public void testReload() throws InterruptedException {
        CountDownLatch eventFired = new CountDownLatch(2);
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().dataSupplier(() -> new AbstractSource.Data<>(Optional.of(Instant.now().toString()), Optional.of(Instant.now()))).fireEventRunnable(eventFired::countDown).build();
        reload();
        TimeUnit.MILLISECONDS.sleep(AbstractSourceTest.TEST_DELAY_MS);
        reload();
        MatcherAssert.assertThat(eventFired.await(50, TimeUnit.MILLISECONDS), Matchers.is(true));
    }

    @Test
    public void testRetryPolicy() throws InterruptedException {
        CountDownLatch methodCalled = new CountDownLatch(1);
        AbstractSourceTest.TestingSource testingSource = AbstractSourceTest.TestingSource.builder().dataSupplier(() -> new AbstractSource.Data<>(Optional.of(Instant.now().toString()), Optional.of(Instant.now()))).fireEventRunnable(() -> {
        }).retryPolicy(new RetryPolicy() {
            @Override
            public <T> T execute(Supplier<T> call) {
                methodCalled.countDown();
                return call.get();
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }
        }).build();
        TimeUnit.MILLISECONDS.sleep(AbstractSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        reload();
        MatcherAssert.assertThat(methodCalled.await(50, TimeUnit.MILLISECONDS), Matchers.is(true));
    }

    @Test
    public void testInitAll() {
        AbstractSourceTest.TestingSource.TestingBuilder builder = AbstractSourceTest.TestingSource.builder().init(Config.builder(ConfigSources.create(CollectionsHelper.mapOf("optional", "true", "polling-strategy.class", AbstractSourceTest.TestingPollingStrategy.class.getName(), "retry-policy.class", AbstractSourceTest.TestingRetryPolicy.class.getName()))).addMapper(AbstractSourceTest.TestingRetryPolicy.class, ( config) -> new io.helidon.config.spi.TestingRetryPolicy()).addMapper(AbstractSourceTest.TestingPollingStrategy.class, ( config) -> new io.helidon.config.spi.TestingPollingStrategy()).build());
        // optional
        MatcherAssert.assertThat(isMandatory(), Matchers.is(false));
        // polling-strategy
        MatcherAssert.assertThat(pollingStrategy(), Matchers.is(Matchers.instanceOf(AbstractSourceTest.TestingPollingStrategy.class)));
        // retry-policy
        MatcherAssert.assertThat(retryPolicy(), Matchers.is(Matchers.instanceOf(AbstractSourceTest.TestingRetryPolicy.class)));
    }

    @Test
    public void testInitNothing() {
        AbstractSourceTest.TestingSource.TestingBuilder builder = AbstractSourceTest.TestingSource.builder().init(Config.empty());
        // optional
        MatcherAssert.assertThat(isMandatory(), Matchers.is(true));
        // polling-strategy
        MatcherAssert.assertThat(pollingStrategy(), Matchers.is(PollingStrategies.nop()));
    }

    private static class TestingSource extends AbstractSource<String, Instant> {
        private final Supplier<Data<String, Instant>> dataSupplier;

        private Runnable fireEventRunnable;

        TestingSource(AbstractSourceTest.TestingSource.TestingBuilder builder) {
            super(builder);
            this.dataSupplier = builder.dataSupplier;
            this.fireEventRunnable = builder.fireEventRunnable;
        }

        static AbstractSourceTest.TestingSource.TestingBuilder builder() {
            return new AbstractSourceTest.TestingSource.TestingBuilder();
        }

        @Override
        protected void fireChangeEvent() {
            fireEventRunnable.run();
        }

        @Override
        protected Optional<Instant> dataStamp() {
            return Optional.of(Instant.MAX);
        }

        @Override
        protected Data<String, Instant> loadData() throws ConfigException {
            return dataSupplier.get();
        }

        private static class TestingBuilder extends Builder<AbstractSourceTest.TestingSource.TestingBuilder, Void, AbstractSourceTest.TestingSource> {
            private Supplier<Data<String, Instant>> dataSupplier;

            private Runnable fireEventRunnable;

            protected TestingBuilder() {
                super(Void.class);
            }

            AbstractSourceTest.TestingSource.TestingBuilder dataSupplier(Supplier<Data<String, Instant>> dataSupplier) {
                this.dataSupplier = dataSupplier;
                return this;
            }

            AbstractSourceTest.TestingSource.TestingBuilder fireEventRunnable(Runnable fireEventRunnable) {
                this.fireEventRunnable = fireEventRunnable;
                return this;
            }

            @Override
            public AbstractSourceTest.TestingSource build() {
                return new AbstractSourceTest.TestingSource(this);
            }
        }
    }

    public static class TestingPollingStrategy implements PollingStrategy {
        @Override
        public Flow.Publisher<PollingEvent> ticks() {
            return Flow.Subscriber::onComplete;
        }
    }

    public static class TestingRetryPolicy implements RetryPolicy {
        @Override
        public <T> T execute(Supplier<T> call) {
            return call.get();
        }
    }
}

