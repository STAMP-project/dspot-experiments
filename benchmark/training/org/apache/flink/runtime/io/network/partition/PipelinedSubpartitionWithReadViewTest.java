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
package org.apache.flink.runtime.io.network.partition;


import java.nio.ByteBuffer;
import org.apache.flink.runtime.io.network.buffer.BufferBuilder;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.io.network.util.TestBufferFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Additional tests for {@link PipelinedSubpartition} which require an availability listener and a
 * read view.
 *
 * @see PipelinedSubpartitionTest
 */
public class PipelinedSubpartitionWithReadViewTest {
    private PipelinedSubpartition subpartition;

    private AwaitableBufferAvailablityListener availablityListener;

    private PipelinedSubpartitionView readView;

    @Test(expected = IllegalStateException.class)
    public void testAddTwoNonFinishedBuffer() {
        subpartition.add(BufferBuilderTestUtils.createBufferBuilder().createBufferConsumer());
        subpartition.add(BufferBuilderTestUtils.createBufferBuilder().createBufferConsumer());
        Assert.assertNull(readView.getNextBuffer());
    }

    @Test
    public void testAddEmptyNonFinishedBuffer() {
        Assert.assertEquals(0, availablityListener.getNumNotifications());
        BufferBuilder bufferBuilder = BufferBuilderTestUtils.createBufferBuilder();
        subpartition.add(bufferBuilder.createBufferConsumer());
        Assert.assertEquals(0, availablityListener.getNumNotifications());
        Assert.assertNull(readView.getNextBuffer());
        bufferBuilder.finish();
        bufferBuilder = BufferBuilderTestUtils.createBufferBuilder();
        subpartition.add(bufferBuilder.createBufferConsumer());
        Assert.assertEquals(1, availablityListener.getNumNotifications());// notification from finishing previous buffer.

        Assert.assertNull(readView.getNextBuffer());
        Assert.assertEquals(1, subpartition.getBuffersInBacklog());
    }

    @Test
    public void testAddNonEmptyNotFinishedBuffer() throws Exception {
        Assert.assertEquals(0, availablityListener.getNumNotifications());
        BufferBuilder bufferBuilder = BufferBuilderTestUtils.createBufferBuilder();
        bufferBuilder.appendAndCommit(ByteBuffer.allocate(1024));
        subpartition.add(bufferBuilder.createBufferConsumer());
        // note that since the buffer builder is not finished, there is still a retained instance!
        SubpartitionTestBase.assertNextBuffer(readView, 1024, false, 1, false, false);
        Assert.assertEquals(1, subpartition.getBuffersInBacklog());
    }

    /**
     * Normally moreAvailable flag from InputChannel should ignore non finished BufferConsumers, otherwise we would
     * busy loop on the unfinished BufferConsumers.
     */
    @Test
    public void testUnfinishedBufferBehindFinished() throws Exception {
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(1025));// finished

        subpartition.add(BufferBuilderTestUtils.createFilledBufferBuilder(1024).createBufferConsumer());// not finished

        MatcherAssert.assertThat(availablityListener.getNumNotifications(), Matchers.greaterThan(0L));
        SubpartitionTestBase.assertNextBuffer(readView, 1025, false, 1, false, true);
        // not notified, but we could still access the unfinished buffer
        SubpartitionTestBase.assertNextBuffer(readView, 1024, false, 1, false, false);
        SubpartitionTestBase.assertNoNextBuffer(readView);
    }

    /**
     * After flush call unfinished BufferConsumers should be reported as available, otherwise we might not flush some
     * of the data.
     */
    @Test
    public void testFlushWithUnfinishedBufferBehindFinished() throws Exception {
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(1025));// finished

        subpartition.add(BufferBuilderTestUtils.createFilledBufferBuilder(1024).createBufferConsumer());// not finished

        long oldNumNotifications = availablityListener.getNumNotifications();
        subpartition.flush();
        // buffer queue is > 1, should already be notified, no further notification necessary
        MatcherAssert.assertThat(oldNumNotifications, Matchers.greaterThan(0L));
        Assert.assertEquals(oldNumNotifications, availablityListener.getNumNotifications());
        SubpartitionTestBase.assertNextBuffer(readView, 1025, true, 1, false, true);
        SubpartitionTestBase.assertNextBuffer(readView, 1024, false, 1, false, false);
        SubpartitionTestBase.assertNoNextBuffer(readView);
    }

    /**
     * A flush call with a buffer size of 1 should always notify consumers (unless already flushed).
     */
    @Test
    public void testFlushWithUnfinishedBufferBehindFinished2() throws Exception {
        // no buffers -> no notification or any other effects
        subpartition.flush();
        Assert.assertEquals(0, availablityListener.getNumNotifications());
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(1025));// finished

        subpartition.add(BufferBuilderTestUtils.createFilledBufferBuilder(1024).createBufferConsumer());// not finished

        SubpartitionTestBase.assertNextBuffer(readView, 1025, false, 1, false, true);
        long oldNumNotifications = availablityListener.getNumNotifications();
        subpartition.flush();
        // buffer queue is 1 again -> need to flush
        Assert.assertEquals((oldNumNotifications + 1), availablityListener.getNumNotifications());
        subpartition.flush();
        // calling again should not flush again
        Assert.assertEquals((oldNumNotifications + 1), availablityListener.getNumNotifications());
        SubpartitionTestBase.assertNextBuffer(readView, 1024, false, 1, false, false);
        SubpartitionTestBase.assertNoNextBuffer(readView);
    }

    @Test
    public void testMultipleEmptyBuffers() throws Exception {
        Assert.assertEquals(0, availablityListener.getNumNotifications());
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(0));
        Assert.assertEquals(1, availablityListener.getNumNotifications());
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(0));
        Assert.assertEquals(2, availablityListener.getNumNotifications());
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(0));
        Assert.assertEquals(2, availablityListener.getNumNotifications());
        Assert.assertEquals(3, subpartition.getBuffersInBacklog());
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(1024));
        Assert.assertEquals(2, availablityListener.getNumNotifications());
        SubpartitionTestBase.assertNextBuffer(readView, 1024, false, 0, false, true);
    }

    @Test
    public void testEmptyFlush() {
        subpartition.flush();
        Assert.assertEquals(0, availablityListener.getNumNotifications());
    }

    @Test
    public void testBasicPipelinedProduceConsumeLogic() throws Exception {
        // Empty => should return null
        Assert.assertFalse(readView.nextBufferIsEvent());
        SubpartitionTestBase.assertNoNextBuffer(readView);
        Assert.assertFalse(readView.nextBufferIsEvent());// also after getNextBuffer()

        Assert.assertEquals(0, availablityListener.getNumNotifications());
        // Add data to the queue...
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(TestBufferFactory.BUFFER_SIZE));
        Assert.assertFalse(readView.nextBufferIsEvent());
        Assert.assertEquals(1, subpartition.getTotalNumberOfBuffers());
        Assert.assertEquals(1, subpartition.getBuffersInBacklog());
        Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        // ...should have resulted in a notification
        Assert.assertEquals(1, availablityListener.getNumNotifications());
        // ...and one available result
        SubpartitionTestBase.assertNextBuffer(readView, TestBufferFactory.BUFFER_SIZE, false, ((subpartition.getBuffersInBacklog()) - 1), false, true);
        Assert.assertEquals(TestBufferFactory.BUFFER_SIZE, subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(0, subpartition.getBuffersInBacklog());
        SubpartitionTestBase.assertNoNextBuffer(readView);
        Assert.assertEquals(0, subpartition.getBuffersInBacklog());
        // Add data to the queue...
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(TestBufferFactory.BUFFER_SIZE));
        Assert.assertFalse(readView.nextBufferIsEvent());
        Assert.assertEquals(2, subpartition.getTotalNumberOfBuffers());
        Assert.assertEquals(1, subpartition.getBuffersInBacklog());
        Assert.assertEquals(TestBufferFactory.BUFFER_SIZE, subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(2, availablityListener.getNumNotifications());
        SubpartitionTestBase.assertNextBuffer(readView, TestBufferFactory.BUFFER_SIZE, false, ((subpartition.getBuffersInBacklog()) - 1), false, true);
        Assert.assertEquals((2 * (TestBufferFactory.BUFFER_SIZE)), subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(0, subpartition.getBuffersInBacklog());
        SubpartitionTestBase.assertNoNextBuffer(readView);
        Assert.assertEquals(0, subpartition.getBuffersInBacklog());
        // some tests with events
        // fill with: buffer, event, and buffer
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(TestBufferFactory.BUFFER_SIZE));
        Assert.assertFalse(readView.nextBufferIsEvent());
        subpartition.add(BufferBuilderTestUtils.createEventBufferConsumer(TestBufferFactory.BUFFER_SIZE));
        Assert.assertFalse(readView.nextBufferIsEvent());
        subpartition.add(BufferBuilderTestUtils.createFilledBufferConsumer(TestBufferFactory.BUFFER_SIZE));
        Assert.assertFalse(readView.nextBufferIsEvent());
        Assert.assertEquals(5, subpartition.getTotalNumberOfBuffers());
        Assert.assertEquals(2, subpartition.getBuffersInBacklog());// two buffers (events don't count)

        Assert.assertEquals((2 * (TestBufferFactory.BUFFER_SIZE)), subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(4, availablityListener.getNumNotifications());
        // the first buffer
        SubpartitionTestBase.assertNextBuffer(readView, TestBufferFactory.BUFFER_SIZE, true, ((subpartition.getBuffersInBacklog()) - 1), true, true);
        Assert.assertEquals((3 * (TestBufferFactory.BUFFER_SIZE)), subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(1, subpartition.getBuffersInBacklog());
        // the event
        SubpartitionTestBase.assertNextEvent(readView, TestBufferFactory.BUFFER_SIZE, null, true, subpartition.getBuffersInBacklog(), false, true);
        Assert.assertEquals((4 * (TestBufferFactory.BUFFER_SIZE)), subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(1, subpartition.getBuffersInBacklog());
        // the remaining buffer
        SubpartitionTestBase.assertNextBuffer(readView, TestBufferFactory.BUFFER_SIZE, false, ((subpartition.getBuffersInBacklog()) - 1), false, true);
        Assert.assertEquals((5 * (TestBufferFactory.BUFFER_SIZE)), subpartition.getTotalNumberOfBytes());// only updated when getting the buffer

        Assert.assertEquals(0, subpartition.getBuffersInBacklog());
        // nothing more
        SubpartitionTestBase.assertNoNextBuffer(readView);
        Assert.assertEquals(0, subpartition.getBuffersInBacklog());
        Assert.assertEquals(5, subpartition.getTotalNumberOfBuffers());
        Assert.assertEquals((5 * (TestBufferFactory.BUFFER_SIZE)), subpartition.getTotalNumberOfBytes());
        Assert.assertEquals(4, availablityListener.getNumNotifications());
    }
}

