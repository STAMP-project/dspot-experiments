/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.remote;


import Status.Code.UNKNOWN;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.devtools.build.lib.remote.RemoteRetrier.ExponentialBackoff;
import com.google.devtools.build.lib.remote.Retrier.Backoff;
import com.google.devtools.build.lib.remote.Retrier.Sleeper;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.common.options.Options;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static Retrier.ALLOW_ALL_CALLS;


/**
 * Tests for {@link RemoteRetrier}.
 */
@RunWith(JUnit4.class)
public class RemoteRetrierTest {
    interface Foo {
        String foo();
    }

    private RemoteRetrierTest.Foo fooMock;

    private static ListeningScheduledExecutorService retryService;

    @Test
    public void testExponentialBackoff() throws Exception {
        Retrier.Backoff backoff = new ExponentialBackoff(Duration.ofSeconds(1), Duration.ofSeconds(10), 2, 0, 6);
        assertThat(backoff.nextDelayMillis()).isEqualTo(1000);
        assertThat(backoff.nextDelayMillis()).isEqualTo(2000);
        assertThat(backoff.nextDelayMillis()).isEqualTo(4000);
        assertThat(backoff.nextDelayMillis()).isEqualTo(8000);
        assertThat(backoff.nextDelayMillis()).isEqualTo(10000);
        assertThat(backoff.nextDelayMillis()).isEqualTo(10000);
        assertThat(backoff.nextDelayMillis()).isLessThan(0L);
    }

    @Test
    public void testExponentialBackoffJittered() throws Exception {
        Retrier.Backoff backoff = new ExponentialBackoff(Duration.ofSeconds(1), Duration.ofSeconds(10), 2, 0.1, 6);
        assertThat(backoff.nextDelayMillis()).isIn(Range.closedOpen(900L, 1100L));
        assertThat(backoff.nextDelayMillis()).isIn(Range.closedOpen(1800L, 2200L));
        assertThat(backoff.nextDelayMillis()).isIn(Range.closedOpen(3600L, 4400L));
        assertThat(backoff.nextDelayMillis()).isIn(Range.closedOpen(7200L, 8800L));
        assertThat(backoff.nextDelayMillis()).isIn(Range.closedOpen(9000L, 11000L));
        assertThat(backoff.nextDelayMillis()).isIn(Range.closedOpen(9000L, 11000L));
        assertThat(backoff.nextDelayMillis()).isLessThan(0L);
    }

    @Test
    public void testNoRetries() throws Exception {
        RemoteOptions options = Options.getDefaults(RemoteOptions.class);
        options.experimentalRemoteRetry = false;
        RemoteRetrier retrier = Mockito.spy(new RemoteRetrier(options, ( e) -> true, RemoteRetrierTest.retryService, ALLOW_ALL_CALLS));
        Mockito.when(fooMock.foo()).thenReturn("bla").thenThrow(UNKNOWN.toStatus().asRuntimeException());
        assertThat(retrier.execute(() -> fooMock.foo())).isEqualTo("bla");
        MoreAsserts.assertThrows(StatusRuntimeException.class, () -> retrier.execute(fooMock::foo));
        Mockito.verify(fooMock, Mockito.times(2)).foo();
    }

    @Test
    public void testNonRetriableError() throws Exception {
        Supplier<Backoff> s = () -> new ExponentialBackoff(Duration.ofSeconds(1), Duration.ofSeconds(10), 2.0, 0.0, 2);
        RemoteRetrier retrier = Mockito.spy(new RemoteRetrier(s, ( e) -> false, RemoteRetrierTest.retryService, ALLOW_ALL_CALLS, Mockito.mock(Sleeper.class)));
        Mockito.when(fooMock.foo()).thenThrow(UNKNOWN.toStatus().asRuntimeException());
        MoreAsserts.assertThrows(StatusRuntimeException.class, () -> retrier.execute(fooMock::foo));
        Mockito.verify(fooMock, Mockito.times(1)).foo();
    }

    @Test
    public void testRepeatedRetriesReset() throws Exception {
        Supplier<Backoff> s = () -> new ExponentialBackoff(Duration.ofSeconds(1), Duration.ofSeconds(10), 2.0, 0.0, 2);
        Sleeper sleeper = Mockito.mock(Sleeper.class);
        RemoteRetrier retrier = Mockito.spy(new RemoteRetrier(s, ( e) -> true, RemoteRetrierTest.retryService, ALLOW_ALL_CALLS, sleeper));
        Mockito.when(fooMock.foo()).thenThrow(UNKNOWN.toStatus().asRuntimeException());
        MoreAsserts.assertThrows(StatusRuntimeException.class, () -> retrier.execute(fooMock::foo));
        MoreAsserts.assertThrows(StatusRuntimeException.class, () -> retrier.execute(fooMock::foo));
        Mockito.verify(sleeper, Mockito.times(2)).sleep(1000);
        Mockito.verify(sleeper, Mockito.times(2)).sleep(2000);
        Mockito.verify(fooMock, Mockito.times(6)).foo();
    }

    @Test
    public void testInterruptedExceptionIsPassedThrough() throws Exception {
        InterruptedException thrown = new InterruptedException();
        RemoteOptions options = Options.getDefaults(RemoteOptions.class);
        options.experimentalRemoteRetry = false;
        RemoteRetrier retrier = new RemoteRetrier(options, ( e) -> true, RemoteRetrierTest.retryService, ALLOW_ALL_CALLS);
        try {
            retrier.execute(() -> {
                throw thrown;
            });
            Assert.fail();
        } catch (InterruptedException expected) {
            assertThat(expected).isSameAs(thrown);
        }
    }
}

