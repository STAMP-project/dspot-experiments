/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.timeout;


import IdleStateEvent.ALL_IDLE_STATE_EVENT;
import IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT;
import IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT;
import IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT;
import IdleStateEvent.READER_IDLE_STATE_EVENT;
import IdleStateEvent.WRITER_IDLE_STATE_EVENT;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class IdleStateHandlerTest {
    @Test
    public void testReaderIdle() throws Exception {
        IdleStateHandlerTest.TestableIdleStateHandler idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 1L, 0L, 0L, TimeUnit.SECONDS);
        // We start with one FIRST_READER_IDLE_STATE_EVENT, followed by an infinite number of READER_IDLE_STATE_EVENTs
        IdleStateHandlerTest.anyIdle(idleStateHandler, FIRST_READER_IDLE_STATE_EVENT, READER_IDLE_STATE_EVENT, READER_IDLE_STATE_EVENT);
    }

    @Test
    public void testWriterIdle() throws Exception {
        IdleStateHandlerTest.TestableIdleStateHandler idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 0L, 1L, 0L, TimeUnit.SECONDS);
        IdleStateHandlerTest.anyIdle(idleStateHandler, FIRST_WRITER_IDLE_STATE_EVENT, WRITER_IDLE_STATE_EVENT, WRITER_IDLE_STATE_EVENT);
    }

    @Test
    public void testAllIdle() throws Exception {
        IdleStateHandlerTest.TestableIdleStateHandler idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 0L, 0L, 1L, TimeUnit.SECONDS);
        IdleStateHandlerTest.anyIdle(idleStateHandler, FIRST_ALL_IDLE_STATE_EVENT, ALL_IDLE_STATE_EVENT, ALL_IDLE_STATE_EVENT);
    }

    @Test
    public void testReaderNotIdle() throws Exception {
        IdleStateHandlerTest.TestableIdleStateHandler idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 1L, 0L, 0L, TimeUnit.SECONDS);
        IdleStateHandlerTest.Action action = new IdleStateHandlerTest.Action() {
            @Override
            public void run(EmbeddedChannel channel) throws Exception {
                channel.writeInbound("Hello, World!");
            }
        };
        IdleStateHandlerTest.anyNotIdle(idleStateHandler, action, FIRST_READER_IDLE_STATE_EVENT);
    }

    @Test
    public void testWriterNotIdle() throws Exception {
        IdleStateHandlerTest.TestableIdleStateHandler idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 0L, 1L, 0L, TimeUnit.SECONDS);
        IdleStateHandlerTest.Action action = new IdleStateHandlerTest.Action() {
            @Override
            public void run(EmbeddedChannel channel) throws Exception {
                channel.writeAndFlush("Hello, World!");
            }
        };
        IdleStateHandlerTest.anyNotIdle(idleStateHandler, action, FIRST_WRITER_IDLE_STATE_EVENT);
    }

    @Test
    public void testAllNotIdle() throws Exception {
        // Reader...
        IdleStateHandlerTest.TestableIdleStateHandler idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 0L, 0L, 1L, TimeUnit.SECONDS);
        IdleStateHandlerTest.Action reader = new IdleStateHandlerTest.Action() {
            @Override
            public void run(EmbeddedChannel channel) throws Exception {
                channel.writeInbound("Hello, World!");
            }
        };
        IdleStateHandlerTest.anyNotIdle(idleStateHandler, reader, FIRST_ALL_IDLE_STATE_EVENT);
        // Writer...
        idleStateHandler = new IdleStateHandlerTest.TestableIdleStateHandler(false, 0L, 0L, 1L, TimeUnit.SECONDS);
        IdleStateHandlerTest.Action writer = new IdleStateHandlerTest.Action() {
            @Override
            public void run(EmbeddedChannel channel) throws Exception {
                channel.writeAndFlush("Hello, World!");
            }
        };
        IdleStateHandlerTest.anyNotIdle(idleStateHandler, writer, FIRST_ALL_IDLE_STATE_EVENT);
    }

    @Test
    public void testObserveWriterIdle() throws Exception {
        IdleStateHandlerTest.observeOutputIdle(true);
    }

    @Test
    public void testObserveAllIdle() throws Exception {
        IdleStateHandlerTest.observeOutputIdle(false);
    }

    private interface Action {
        void run(EmbeddedChannel channel) throws Exception;
    }

    private static class TestableIdleStateHandler extends IdleStateHandler {
        private Runnable task;

        private long delayInNanos;

        private long ticksInNanos;

        TestableIdleStateHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
            super(observeOutput, readerIdleTime, writerIdleTime, allIdleTime, unit);
        }

        public long delay(TimeUnit unit) {
            return unit.convert(delayInNanos, TimeUnit.NANOSECONDS);
        }

        public void run() {
            task.run();
        }

        public void tickRun() {
            tickRun(delayInNanos, TimeUnit.NANOSECONDS);
        }

        public void tickRun(long delay, TimeUnit unit) {
            tick(delay, unit);
            run();
        }

        /**
         * Advances the current ticker by the given amount.
         */
        public void tick(long delay, TimeUnit unit) {
            ticksInNanos += unit.toNanos(delay);
        }

        /**
         * Returns {@link #ticksInNanos()} in the given {@link TimeUnit}.
         */
        public long tick(TimeUnit unit) {
            return unit.convert(ticksInNanos(), TimeUnit.NANOSECONDS);
        }

        @Override
        long ticksInNanos() {
            return ticksInNanos;
        }

        @Override
        ScheduledFuture<?> schedule(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit unit) {
            this.task = task;
            this.delayInNanos = unit.toNanos(delay);
            return null;
        }
    }

    private static class ObservableChannel extends EmbeddedChannel {
        ObservableChannel(ChannelHandler... handlers) {
            super(handlers);
        }

        @Override
        protected void doWrite(ChannelOutboundBuffer in) throws Exception {
            // Overridden to change EmbeddedChannel's default behavior. We went to keep
            // the messages in the ChannelOutboundBuffer.
        }

        public Object consume() {
            ChannelOutboundBuffer buf = unsafe().outboundBuffer();
            if (buf != null) {
                Object msg = buf.current();
                if (msg != null) {
                    ReferenceCountUtil.retain(msg);
                    buf.remove();
                    return msg;
                }
            }
            return null;
        }
    }
}

