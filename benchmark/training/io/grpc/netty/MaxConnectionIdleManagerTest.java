/**
 * Copyright 2017 The gRPC Authors
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
package io.grpc.netty;


import io.grpc.internal.FakeClock;
import io.grpc.netty.MaxConnectionIdleManager.Ticker;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link MaxConnectionIdleManager}.
 */
@RunWith(JUnit4.class)
public class MaxConnectionIdleManagerTest {
    private final FakeClock fakeClock = new FakeClock();

    private final Ticker ticker = new Ticker() {
        @Override
        public long nanoTime() {
            return fakeClock.getTicker().read();
        }
    };

    @Mock
    private ChannelHandlerContext ctx;

    @Test
    public void maxIdleReached() {
        MaxConnectionIdleManager maxConnectionIdleManager = Mockito.spy(new MaxConnectionIdleManagerTest.TestMaxConnectionIdleManager(123L, ticker));
        maxConnectionIdleManager.start(ctx, fakeClock.getScheduledExecutorService());
        maxConnectionIdleManager.onTransportIdle();
        fakeClock.forwardNanos(123L);
        Mockito.verify(maxConnectionIdleManager).close(ArgumentMatchers.eq(ctx));
    }

    @Test
    public void maxIdleNotReachedAndReached() {
        MaxConnectionIdleManager maxConnectionIdleManager = Mockito.spy(new MaxConnectionIdleManagerTest.TestMaxConnectionIdleManager(123L, ticker));
        maxConnectionIdleManager.start(ctx, fakeClock.getScheduledExecutorService());
        maxConnectionIdleManager.onTransportIdle();
        fakeClock.forwardNanos(100L);
        // max idle not reached
        maxConnectionIdleManager.onTransportActive();
        maxConnectionIdleManager.onTransportIdle();
        fakeClock.forwardNanos(100L);
        // max idle not reached although accumulative idle time exceeds max idle time
        maxConnectionIdleManager.onTransportActive();
        fakeClock.forwardNanos(100L);
        Mockito.verify(maxConnectionIdleManager, Mockito.never()).close(ArgumentMatchers.any(ChannelHandlerContext.class));
        // max idle reached
        maxConnectionIdleManager.onTransportIdle();
        fakeClock.forwardNanos(123L);
        Mockito.verify(maxConnectionIdleManager).close(ArgumentMatchers.eq(ctx));
    }

    @Test
    public void shutdownThenMaxIdleReached() {
        MaxConnectionIdleManager maxConnectionIdleManager = Mockito.spy(new MaxConnectionIdleManagerTest.TestMaxConnectionIdleManager(123L, ticker));
        maxConnectionIdleManager.start(ctx, fakeClock.getScheduledExecutorService());
        maxConnectionIdleManager.onTransportIdle();
        maxConnectionIdleManager.onTransportTermination();
        fakeClock.forwardNanos(123L);
        Mockito.verify(maxConnectionIdleManager, Mockito.never()).close(ArgumentMatchers.any(ChannelHandlerContext.class));
    }

    private static class TestMaxConnectionIdleManager extends MaxConnectionIdleManager {
        TestMaxConnectionIdleManager(long maxConnectionIdleInNanos, Ticker ticker) {
            super(maxConnectionIdleInNanos, ticker);
        }

        @Override
        void close(ChannelHandlerContext ctx) {
        }
    }
}

