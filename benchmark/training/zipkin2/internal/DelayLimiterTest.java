/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.internal;


import DelayLimiter.Ticker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import org.junit.Test;


public class DelayLimiterTest {
    static final long NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);

    long nanoTime;

    Ticker ticker = new DelayLimiter.Ticker() {
        long read() {
            return nanoTime;
        }
    };

    @Test
    public void mutesDuringDelayPeriod() {
        DelayLimiter<Long> delayLimiter = DelayLimiter.newBuilder().ticker(ticker).ttl(3000).build();
        nanoTime = DelayLimiterTest.NANOS_PER_SECOND;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
        nanoTime = (DelayLimiterTest.NANOS_PER_SECOND) * 2;
        assertThat(delayLimiter.shouldInvoke(0L)).isFalse();
        nanoTime = (DelayLimiterTest.NANOS_PER_SECOND) * 4;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
    }

    @Test
    public void contextsAreIndependent() {
        DelayLimiter<Long> delayLimiter = DelayLimiter.newBuilder().ticker(ticker).ttl(3000).build();
        nanoTime = DelayLimiterTest.NANOS_PER_SECOND;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
        nanoTime = (DelayLimiterTest.NANOS_PER_SECOND) * 2;
        assertThat(delayLimiter.shouldInvoke(0L)).isFalse();
        assertThat(delayLimiter.shouldInvoke(1L)).isTrue();
        nanoTime = (DelayLimiterTest.NANOS_PER_SECOND) * 4;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
        assertThat(delayLimiter.shouldInvoke(1L)).isFalse();
    }

    @Test
    public void worksOnRollover() {
        DelayLimiter<Long> delayLimiter = DelayLimiter.newBuilder().ticker(ticker).ttl(3000).build();
        nanoTime = -(DelayLimiterTest.NANOS_PER_SECOND);
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
        nanoTime = 0L;
        assertThat(delayLimiter.shouldInvoke(0L)).isFalse();
        nanoTime = (DelayLimiterTest.NANOS_PER_SECOND) * 2;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
    }

    @Test
    public void worksOnSameNanos() {
        DelayLimiter<Long> delayLimiter = DelayLimiter.newBuilder().ticker(ticker).ttl(3000).build();
        nanoTime = DelayLimiterTest.NANOS_PER_SECOND;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
        nanoTime = (DelayLimiterTest.NANOS_PER_SECOND) * 4;
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();
        assertThat(delayLimiter.shouldInvoke(0L)).isFalse();
    }

    @Test(timeout = 1000L)
    public void cardinality() {
        DelayLimiter<Long> delayLimiter = DelayLimiter.newBuilder().ttl(1000).cardinality(1000).build();
        for (long i = 0; i < 10000L; i++) {
            assertThat(delayLimiter.shouldInvoke(i)).isTrue();
        }
        assertThat(delayLimiter.shouldInvoke(0L)).isTrue();// evicted

        assertThat(delayLimiter.shouldInvoke(9999L)).isFalse();// not evicted

        // verify internal state
        assertThat(delayLimiter.cache).hasSameSizeAs(delayLimiter.suppressions).hasSize(1000);
    }

    @Test(timeout = 2000L)
    public void cardinality_parallel() throws InterruptedException {
        DelayLimiter<Long> delayLimiter = DelayLimiter.newBuilder().ttl(1000).cardinality(1000).build();
        AtomicInteger trueCount = new AtomicInteger();
        ExecutorService exec = Executors.newFixedThreadPool(4);
        int count = 10000;
        LongStream.range(0, count).forEach(( i) -> exec.execute(() -> {
            if (delayLimiter.shouldInvoke(i))
                trueCount.incrementAndGet();

        }));
        exec.shutdown();
        assertThat(exec.awaitTermination(1, TimeUnit.SECONDS)).isTrue();
        assertThat(trueCount).hasValue(count);
        // verify internal state
        assertThat(delayLimiter.cache).hasSameSizeAs(delayLimiter.suppressions).hasSize(1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ttl_cantBeNegative() {
        DelayLimiter.newBuilder().ttl((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ttl_cantBeZero() {
        DelayLimiter.newBuilder().ttl(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cardinality_cantBeNegative() {
        DelayLimiter.newBuilder().cardinality((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cardinality_cantBeZero() {
        DelayLimiter.newBuilder().cardinality(0);
    }
}

