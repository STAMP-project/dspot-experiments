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
package io.netty.handler.flush;


import io.netty.channel.embedded.EmbeddedChannel;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class FlushConsolidationHandlerTest {
    private static final int EXPLICIT_FLUSH_AFTER_FLUSHES = 3;

    @Test
    public void testFlushViaScheduledTask() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, true);
        // Flushes should not go through immediately, as they're scheduled as an async task
        channel.flush();
        Assert.assertEquals(0, flushCount.get());
        channel.flush();
        Assert.assertEquals(0, flushCount.get());
        // Trigger the execution of the async task
        channel.runPendingTasks();
        Assert.assertEquals(1, flushCount.get());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testFlushViaThresholdOutsideOfReadLoop() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, true);
        // After a given threshold, the async task should be bypassed and a flush should be triggered immediately
        for (int i = 0; i < (FlushConsolidationHandlerTest.EXPLICIT_FLUSH_AFTER_FLUSHES); i++) {
            channel.flush();
        }
        Assert.assertEquals(1, flushCount.get());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testImmediateFlushOutsideOfReadLoop() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, false);
        channel.flush();
        Assert.assertEquals(1, flushCount.get());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testFlushViaReadComplete() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, false);
        // Flush should go through as there is no read loop in progress.
        channel.flush();
        channel.runPendingTasks();
        Assert.assertEquals(1, flushCount.get());
        // Simulate read loop;
        channel.pipeline().fireChannelRead(1L);
        Assert.assertEquals(1, flushCount.get());
        channel.pipeline().fireChannelRead(2L);
        Assert.assertEquals(1, flushCount.get());
        Assert.assertNull(channel.readOutbound());
        channel.pipeline().fireChannelReadComplete();
        Assert.assertEquals(2, flushCount.get());
        // Now flush again as the read loop is complete.
        channel.flush();
        channel.runPendingTasks();
        Assert.assertEquals(3, flushCount.get());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertEquals(2L, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testFlushViaClose() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, false);
        // Simulate read loop;
        channel.pipeline().fireChannelRead(1L);
        Assert.assertEquals(0, flushCount.get());
        Assert.assertNull(channel.readOutbound());
        channel.close();
        Assert.assertEquals(1, flushCount.get());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testFlushViaDisconnect() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, false);
        // Simulate read loop;
        channel.pipeline().fireChannelRead(1L);
        Assert.assertEquals(0, flushCount.get());
        Assert.assertNull(channel.readOutbound());
        channel.disconnect();
        Assert.assertEquals(1, flushCount.get());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
        Assert.assertFalse(channel.finish());
    }

    @Test(expected = IllegalStateException.class)
    public void testFlushViaException() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, false);
        // Simulate read loop;
        channel.pipeline().fireChannelRead(1L);
        Assert.assertEquals(0, flushCount.get());
        Assert.assertNull(channel.readOutbound());
        channel.pipeline().fireExceptionCaught(new IllegalStateException());
        Assert.assertEquals(1, flushCount.get());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
        channel.finish();
    }

    @Test
    public void testFlushViaRemoval() {
        final AtomicInteger flushCount = new AtomicInteger();
        EmbeddedChannel channel = FlushConsolidationHandlerTest.newChannel(flushCount, false);
        // Simulate read loop;
        channel.pipeline().fireChannelRead(1L);
        Assert.assertEquals(0, flushCount.get());
        Assert.assertNull(channel.readOutbound());
        channel.pipeline().remove(FlushConsolidationHandler.class);
        Assert.assertEquals(1, flushCount.get());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
        Assert.assertFalse(channel.finish());
    }
}

