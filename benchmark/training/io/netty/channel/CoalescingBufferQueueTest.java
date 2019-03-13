/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.channel;


import CharsetUtil.US_ASCII;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CoalescingBufferQueue}.
 */
public class CoalescingBufferQueueTest {
    private ByteBuf cat;

    private ByteBuf mouse;

    private ChannelPromise catPromise;

    private ChannelPromise emptyPromise;

    private ChannelPromise voidPromise;

    private ChannelFutureListener mouseListener;

    private boolean mouseDone;

    private boolean mouseSuccess;

    private EmbeddedChannel channel;

    private CoalescingBufferQueue writeQueue;

    @Test
    public void testAddFirstPromiseRetained() {
        writeQueue.add(cat, catPromise);
        assertQueueSize(3, false);
        writeQueue.add(mouse, mouseListener);
        assertQueueSize(8, false);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("catmous", dequeue(7, aggregatePromise));
        ByteBuf remainder = Unpooled.wrappedBuffer("mous".getBytes(US_ASCII));
        writeQueue.addFirst(remainder, aggregatePromise);
        ChannelPromise aggregatePromise2 = newPromise();
        Assert.assertEquals("mouse", dequeue(5, aggregatePromise2));
        aggregatePromise2.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertTrue(mouseSuccess);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
    }

    @Test
    public void testAddFirstVoidPromise() {
        writeQueue.add(cat, catPromise);
        assertQueueSize(3, false);
        writeQueue.add(mouse, mouseListener);
        assertQueueSize(8, false);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("catmous", dequeue(7, aggregatePromise));
        ByteBuf remainder = Unpooled.wrappedBuffer("mous".getBytes(US_ASCII));
        writeQueue.addFirst(remainder, voidPromise);
        ChannelPromise aggregatePromise2 = newPromise();
        Assert.assertEquals("mouse", dequeue(5, aggregatePromise2));
        aggregatePromise2.setSuccess();
        // Because we used a void promise above, we shouldn't complete catPromise until aggregatePromise is completed.
        Assert.assertFalse(catPromise.isSuccess());
        Assert.assertTrue(mouseSuccess);
        aggregatePromise.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertTrue(mouseSuccess);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
    }

    @Test
    public void testAggregateWithFullRead() {
        writeQueue.add(cat, catPromise);
        assertQueueSize(3, false);
        writeQueue.add(mouse, mouseListener);
        assertQueueSize(8, false);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("catmouse", dequeue(8, aggregatePromise));
        assertQueueSize(0, true);
        Assert.assertFalse(catPromise.isSuccess());
        Assert.assertFalse(mouseDone);
        aggregatePromise.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertTrue(mouseSuccess);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
    }

    @Test
    public void testWithVoidPromise() {
        writeQueue.add(cat, voidPromise);
        writeQueue.add(mouse, voidPromise);
        assertQueueSize(8, false);
        Assert.assertEquals("catm", dequeue(4, newPromise()));
        assertQueueSize(4, false);
        Assert.assertEquals("ouse", dequeue(4, newPromise()));
        assertQueueSize(0, true);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
    }

    @Test
    public void testAggregateWithPartialRead() {
        writeQueue.add(cat, catPromise);
        writeQueue.add(mouse, mouseListener);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("catm", dequeue(4, aggregatePromise));
        assertQueueSize(4, false);
        Assert.assertFalse(catPromise.isSuccess());
        Assert.assertFalse(mouseDone);
        aggregatePromise.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertFalse(mouseDone);
        aggregatePromise = newPromise();
        Assert.assertEquals("ouse", dequeue(Integer.MAX_VALUE, aggregatePromise));
        assertQueueSize(0, true);
        Assert.assertFalse(mouseDone);
        aggregatePromise.setSuccess();
        Assert.assertTrue(mouseSuccess);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
    }

    @Test
    public void testReadExactAddedBufferSizeReturnsOriginal() {
        writeQueue.add(cat, catPromise);
        writeQueue.add(mouse, mouseListener);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertSame(cat, writeQueue.remove(3, aggregatePromise));
        Assert.assertFalse(catPromise.isSuccess());
        aggregatePromise.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertEquals(1, cat.refCnt());
        cat.release();
        aggregatePromise = newPromise();
        Assert.assertSame(mouse, writeQueue.remove(5, aggregatePromise));
        Assert.assertFalse(mouseDone);
        aggregatePromise.setSuccess();
        Assert.assertTrue(mouseSuccess);
        Assert.assertEquals(1, mouse.refCnt());
        mouse.release();
    }

    @Test
    public void testReadEmptyQueueReturnsEmptyBuffer() {
        // Not used in this test.
        cat.release();
        mouse.release();
        assertQueueSize(0, true);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("", dequeue(Integer.MAX_VALUE, aggregatePromise));
        assertQueueSize(0, true);
    }

    @Test
    public void testReleaseAndFailAll() {
        writeQueue.add(cat, catPromise);
        writeQueue.add(mouse, mouseListener);
        RuntimeException cause = new RuntimeException("ooops");
        writeQueue.releaseAndFailAll(cause);
        ChannelPromise aggregatePromise = newPromise();
        assertQueueSize(0, true);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
        Assert.assertSame(cause, catPromise.cause());
        Assert.assertEquals("", dequeue(Integer.MAX_VALUE, aggregatePromise));
        assertQueueSize(0, true);
    }

    @Test
    public void testEmptyBuffersAreCoalesced() {
        ByteBuf empty = Unpooled.buffer(0, 1);
        assertQueueSize(0, true);
        writeQueue.add(cat, catPromise);
        writeQueue.add(empty, emptyPromise);
        assertQueueSize(3, false);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("cat", dequeue(3, aggregatePromise));
        assertQueueSize(0, true);
        Assert.assertFalse(catPromise.isSuccess());
        Assert.assertFalse(emptyPromise.isSuccess());
        aggregatePromise.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertTrue(emptyPromise.isSuccess());
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, empty.refCnt());
    }

    @Test
    public void testMerge() {
        writeQueue.add(cat, catPromise);
        CoalescingBufferQueue otherQueue = new CoalescingBufferQueue(channel);
        otherQueue.add(mouse, mouseListener);
        otherQueue.copyTo(writeQueue);
        assertQueueSize(8, false);
        ChannelPromise aggregatePromise = newPromise();
        Assert.assertEquals("catmouse", dequeue(8, aggregatePromise));
        assertQueueSize(0, true);
        Assert.assertFalse(catPromise.isSuccess());
        Assert.assertFalse(mouseDone);
        aggregatePromise.setSuccess();
        Assert.assertTrue(catPromise.isSuccess());
        Assert.assertTrue(mouseSuccess);
        Assert.assertEquals(0, cat.refCnt());
        Assert.assertEquals(0, mouse.refCnt());
    }

    @Test
    public void testWritabilityChanged() {
        testWritabilityChanged0(false);
    }

    @Test
    public void testWritabilityChangedFailAll() {
        testWritabilityChanged0(true);
    }
}

