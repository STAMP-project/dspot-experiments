/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network.netty;


import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.channel.Channel;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandlerContext;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies that high and low watermarks for {@link NettyServer} may be set to any (valid) values
 * given by the user.
 */
public class NettyServerLowAndHighWatermarkTest {
    /**
     * Verify low and high watermarks being set correctly for larger memory segment sizes which
     * trigger <a href="https://issues.apache.org/jira/browse/FLINK-7258">FLINK-7258</a>.
     */
    @Test
    public void testLargeLowAndHighWatermarks() throws Throwable {
        testLowAndHighWatermarks(65536);
    }

    /**
     * Verify low and high watermarks being set correctly for smaller memory segment sizes than
     * Netty's defaults.
     */
    @Test
    public void testSmallLowAndHighWatermarks() throws Throwable {
        testLowAndHighWatermarks(1024);
    }

    /**
     * This handler implements the test.
     *
     * <p>Verifies that the high and low watermark are set in relation to the page size.
     */
    private static class TestLowAndHighWatermarkHandler extends ChannelInboundHandlerAdapter {
        private final int pageSize;

        private final int expectedLowWatermark;

        private final int expectedHighWatermark;

        private final AtomicReference<Throwable> error;

        private boolean hasFlushed;

        public TestLowAndHighWatermarkHandler(int pageSize, int expectedLowWatermark, int expectedHighWatermark, AtomicReference<Throwable> error) {
            this.pageSize = pageSize;
            this.expectedLowWatermark = expectedLowWatermark;
            this.expectedHighWatermark = expectedHighWatermark;
            this.error = error;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final Channel ch = ctx.channel();
            Assert.assertEquals("Low watermark", expectedLowWatermark, ch.config().getWriteBufferLowWaterMark());
            Assert.assertEquals("High watermark", expectedHighWatermark, ch.config().getWriteBufferHighWaterMark());
            // Start with a writable channel
            Assert.assertTrue(ch.isWritable());
            // First buffer should not change writability
            ch.write(buffer());
            Assert.assertTrue(ch.isWritable());
            // ...second buffer should though
            ch.write(buffer());
            Assert.assertFalse(ch.isWritable());
            // Flush everything and close the channel after the writability changed event is fired.
            hasFlushed = true;
            ch.flush();
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            if (hasFlushed) {
                // After flushing the writability should change back to writable
                Assert.assertTrue(ctx.channel().isWritable());
                // Close the channel. This will terminate the main test Thread.
                ctx.close();
            }
            super.channelWritabilityChanged(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if ((error.get()) == null) {
                error.set(cause);
            }
            ctx.close();
            super.exceptionCaught(ctx, cause);
        }

        private ByteBuf buffer() {
            return NettyServerLowAndHighWatermarkTest.buffer(pageSize);
        }
    }
}

