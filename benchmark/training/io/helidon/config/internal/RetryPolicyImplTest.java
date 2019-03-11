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
package io.helidon.config.internal;


import io.helidon.config.ConfigException;
import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link RetryPolicyImpl}.
 */
public class RetryPolicyImplTest {
    private static final Duration RETRY_TIMEOUT = Duration.ofMillis(250);

    private static final Duration OVERALL_TIMEOUT = Duration.ofMillis((250 * 10));

    @Test
    public void testCallOnce() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ofMillis(10), 1, RetryPolicyImplTest.RETRY_TIMEOUT, RetryPolicyImplTest.OVERALL_TIMEOUT, Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.SuccessSupplier sup = Mockito.spy(new RetryPolicyImplTest.SuccessSupplier());
        retryPolicy.execute(sup::get);
        Mockito.verify(sup, Mockito.times(1)).get();
    }

    @Test
    public void testRetryTwiceButFirstCallSucceeded() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(2, Duration.ofMillis(10), 1, RetryPolicyImplTest.RETRY_TIMEOUT, RetryPolicyImplTest.OVERALL_TIMEOUT, Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.SuccessSupplier sup = Mockito.spy(new RetryPolicyImplTest.SuccessSupplier());
        retryPolicy.execute(sup::get);
        Mockito.verify(sup, Mockito.times(1)).get();
    }

    @Test
    public void testRetryTwiceCheckException() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(2, Duration.ofMillis(10), 1, RetryPolicyImplTest.RETRY_TIMEOUT, RetryPolicyImplTest.OVERALL_TIMEOUT, Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = Mockito.spy(new RetryPolicyImplTest.UniversalSupplier(2, false, 0));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            retryPolicy.execute(sup::get);
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("All repeated calls failed."));
    }

    @Test
    public void testRetryTwiceCheckRetries() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(2, Duration.ZERO, 1, RetryPolicyImplTest.RETRY_TIMEOUT, RetryPolicyImplTest.OVERALL_TIMEOUT, Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = Mockito.spy(new RetryPolicyImplTest.UniversalSupplier(2, false, 0));
        try {
            retryPolicy.execute(sup::get);
        } catch (ConfigException e) {
        }
        Mockito.verify(sup, Mockito.times(3)).get();
    }

    @Test
    public void testRetryTwicePolledTwice() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(2, Duration.ofMillis(10), 1, RetryPolicyImplTest.RETRY_TIMEOUT, RetryPolicyImplTest.OVERALL_TIMEOUT, Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = Mockito.spy(new RetryPolicyImplTest.UniversalSupplier(2, false, 0));
        try {
            retryPolicy.execute(sup::get);
        } catch (ConfigException e) {
        }
        sup.reset();
        try {
            retryPolicy.execute(sup::get);
        } catch (ConfigException e) {
        }
        Mockito.verify(sup, Mockito.times(6)).get();
    }

    @Test
    public void testRetryThirdAttemptSucceed() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(2, Duration.ZERO, 1, RetryPolicyImplTest.RETRY_TIMEOUT, RetryPolicyImplTest.OVERALL_TIMEOUT, Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = Mockito.spy(new RetryPolicyImplTest.UniversalSupplier(2, true, 0));
        retryPolicy.execute(sup::get);
        Mockito.verify(sup, Mockito.times(3)).get();
    }

    @Test
    public void testRetryCannotScheduleNextCall() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(2, Duration.ofMillis(50), 1, Duration.ofMillis(60), Duration.ofMillis(100), Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = new RetryPolicyImplTest.UniversalSupplier(1, false, 50);
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            retryPolicy.execute(sup::get);
        });
        MatcherAssert.assertThat(ex.getCause(), CoreMatchers.instanceOf(TimeoutException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("A timeout has been reached."));
    }

    @Test
    public void testRetryOverallTimeoutReached() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ofMillis(1), 1, Duration.ofMillis(110), Duration.ofMillis(100), Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = new RetryPolicyImplTest.UniversalSupplier(0, true, 300);
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            retryPolicy.execute(sup::get);
        });
        MatcherAssert.assertThat(ex.getCause(), CoreMatchers.instanceOf(TimeoutException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("A timeout has been reached."));
    }

    @Test
    public void testRetryCallTimeoutReached() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ZERO, 1, Duration.ofMillis(10), Duration.ofMillis(100), Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = new RetryPolicyImplTest.UniversalSupplier(0, true, 100);
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            String result = retryPolicy.execute(sup::get);
            System.out.println(result);
        });
        MatcherAssert.assertThat(ex.getCause(), CoreMatchers.instanceOf(TimeoutException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("A timeout has been reached."));
    }

    @Test
    public void testRetryCancel() throws Exception {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ZERO, 1, Duration.ofMillis(500), Duration.ofMillis(550), Executors.newSingleThreadScheduledExecutor());
        RetryPolicyImplTest.UniversalSupplier sup = new RetryPolicyImplTest.UniversalSupplier(0, true, 500);
        new Thread(() -> {
            try {
                Thread.sleep(250);
                retryPolicy.cancel(true);
            } catch (InterruptedException e) {
            }
        }).start();
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            Object execute = retryPolicy.execute(sup::get);
            System.out.println(execute);
        });
        MatcherAssert.assertThat(ex.getCause(), CoreMatchers.instanceOf(CancellationException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("An invocation has been canceled."));
    }

    @Test
    public void testDelayFactor1Delay0() {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ZERO, 1, Duration.ofMillis(500), Duration.ofMillis(550), Executors.newSingleThreadScheduledExecutor());
        MatcherAssert.assertThat(retryPolicy.nextDelay(0, Duration.ZERO), CoreMatchers.is(Duration.ofMillis(0)));
        MatcherAssert.assertThat(retryPolicy.nextDelay(1, Duration.ZERO), CoreMatchers.is(Duration.ofMillis(0)));
    }

    @Test
    public void testDelayFactor1Delay1s() {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ofSeconds(1), 1, Duration.ofMillis(500), Duration.ofMillis(550), Executors.newSingleThreadScheduledExecutor());
        MatcherAssert.assertThat(retryPolicy.nextDelay(0, Duration.ZERO), CoreMatchers.is(Duration.ofSeconds(1)));
        MatcherAssert.assertThat(retryPolicy.nextDelay(1, Duration.ofSeconds(1)), CoreMatchers.is(Duration.ofSeconds(1)));
    }

    @Test
    public void testDelayFactor15Delay1s() {
        RetryPolicyImpl retryPolicy = new RetryPolicyImpl(0, Duration.ofSeconds(1), 1.5, Duration.ofMillis(500), Duration.ofMillis(550), Executors.newSingleThreadScheduledExecutor());
        MatcherAssert.assertThat(retryPolicy.nextDelay(0, Duration.ZERO), CoreMatchers.is(Duration.ofMillis(1000)));
        MatcherAssert.assertThat(retryPolicy.nextDelay(1, Duration.ofSeconds(1)), CoreMatchers.is(Duration.ofMillis(1500)));
    }

    private class SuccessSupplier extends RetryPolicyImplTest.UniversalSupplier {
        SuccessSupplier() {
            super(0, true, 0);
        }
    }

    private class UniversalSupplier {
        private final int retries;

        private final boolean lastSucceed;

        private final long callDuration;

        private Supplier<? extends RuntimeException> exceptionSupplier;

        private int counter = 0;

        private UniversalSupplier(int retries, boolean lastSucceed, long callDuration) {
            this.retries = retries;
            this.lastSucceed = lastSucceed;
            this.callDuration = callDuration;
            this.exceptionSupplier = RuntimeException::new;
        }

        private UniversalSupplier(int retries, boolean lastSucceed, long callDuration, Supplier<? extends RuntimeException> exceptionSupplier) {
            this.retries = retries;
            this.lastSucceed = lastSucceed;
            this.callDuration = callDuration;
            this.exceptionSupplier = exceptionSupplier;
        }

        public String get() throws RuntimeException {
            (counter)++;
            try {
                Thread.sleep(callDuration);
            } catch (InterruptedException e) {
            }
            if (((counter) > (retries)) && (lastSucceed)) {
                return "something";
            } else {
                throw exceptionSupplier.get();
            }
        }

        void reset() {
            counter = 0;
        }
    }
}

