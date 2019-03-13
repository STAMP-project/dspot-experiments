/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.epoll;


import DefaultSelectStrategyFactory.INSTANCE;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class EpollEventLoopTest {
    @Test
    public void testScheduleBigDelayNotOverflow() {
        final AtomicReference<Throwable> capture = new AtomicReference<Throwable>();
        final EventLoopGroup group = new EpollEventLoop(null, new io.netty.util.concurrent.ThreadPerTaskExecutor(new DefaultThreadFactory(getClass())), 0, INSTANCE.newSelectStrategy(), RejectedExecutionHandlers.reject()) {
            @Override
            void handleLoopException(Throwable t) {
                capture.set(t);
                super.handleLoopException(t);
            }
        };
        try {
            final EventLoop eventLoop = group.next();
            Future<?> future = eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    // NOOP
                }
            }, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            Assert.assertFalse(future.awaitUninterruptibly(1000));
            Assert.assertTrue(future.cancel(true));
            Assert.assertNull(capture.get());
        } finally {
            group.shutdownGracefully();
        }
    }
}

