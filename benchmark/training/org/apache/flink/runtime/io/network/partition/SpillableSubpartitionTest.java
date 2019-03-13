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


import FileIOChannel.ID;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.flink.runtime.io.disk.iomanager.AsynchronousBufferFileWriter;
import org.apache.flink.runtime.io.disk.iomanager.BufferFileWriter;
import org.apache.flink.runtime.io.disk.iomanager.FileIOChannel;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.disk.iomanager.IOManagerAsync;
import org.apache.flink.runtime.io.disk.iomanager.IOManagerAsyncWithNoOpBufferFileWriter;
import org.apache.flink.runtime.io.network.api.CancelCheckpointMarker;
import org.apache.flink.runtime.io.network.api.EndOfPartitionEvent;
import org.apache.flink.runtime.io.network.api.serialization.EventSerializer;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferBuilder;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.io.network.buffer.BufferConsumer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link SpillableSubpartition}.
 */
public class SpillableSubpartitionTest extends SubpartitionTestBase {
    private static final int BUFFER_DATA_SIZE = 4096;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Executor service for concurrent produce/consume tests.
     */
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Asynchronous I/O manager.
     */
    private static IOManager ioManager;

    /**
     * Tests a fix for FLINK-2384.
     *
     * @see <a href="https://issues.apache.org/jira/browse/FLINK-2384">FLINK-2384</a>
     */
    @Test
    public void testConcurrentFinishAndReleaseMemory() throws Exception {
        // Latches to blocking
        final CountDownLatch doneLatch = new CountDownLatch(1);
        final CountDownLatch blockLatch = new CountDownLatch(1);
        // Blocking spill writer (blocks on the close call)
        AsynchronousBufferFileWriter spillWriter = Mockito.mock(AsynchronousBufferFileWriter.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                blockLatch.countDown();
                doneLatch.await();
                return null;
            }
        }).when(spillWriter).close();
        // Mock I/O manager returning the blocking spill writer
        IOManager ioManager = Mockito.mock(IOManager.class);
        Mockito.when(ioManager.createBufferFileWriter(ArgumentMatchers.nullable(ID.class))).thenReturn(spillWriter);
        // The partition
        final SpillableSubpartition partition = new SpillableSubpartition(0, Mockito.mock(ResultPartition.class), ioManager);
        // Spill the partition initially (creates the spill writer)
        Assert.assertEquals(0, partition.releaseMemory());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Finish the partition (this blocks because of the mock blocking writer)
        Future<Void> blockingFinish = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                partition.finish();
                return null;
            }
        });
        // Ensure that the blocking call has been made
        blockLatch.await();
        // This call needs to go through. FLINK-2384 discovered a bug, in
        // which the finish call was holding a lock, which was leading to a
        // deadlock when another operation on the partition was happening.
        partition.releaseMemory();
        // Check that the finish call succeeded w/o problems as well to avoid
        // false test successes.
        doneLatch.countDown();
        blockingFinish.get();
    }

    /**
     * Tests a fix for FLINK-2412.
     *
     * @see <a href="https://issues.apache.org/jira/browse/FLINK-2412">FLINK-2412</a>
     */
    @Test
    public void testReleasePartitionAndGetNext() throws Exception {
        // Create partition and add some buffers
        SpillableSubpartition partition = createSubpartition();
        partition.finish();
        // Create the read view
        ResultSubpartitionView readView = Mockito.spy(partition.createReadView(new NoOpBufferAvailablityListener()));
        // The released state check (of the parent) needs to be independent
        // of the released state of the view.
        Mockito.doNothing().when(readView).releaseAllResources();
        // Release the partition, but the view does not notice yet.
        partition.release();
        Assert.assertNull(readView.getNextBuffer());
    }

    /**
     * Tests that a spilled partition is correctly read back in via a spilled
     * read view.
     */
    @Test
    public void testConsumeSpilledPartition() throws Exception {
        SpillableSubpartition partition = createSubpartition();
        BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(SpillableSubpartitionTest.BUFFER_DATA_SIZE, SpillableSubpartitionTest.BUFFER_DATA_SIZE);
        BufferConsumer eventBufferConsumer = EventSerializer.toBufferConsumer(new CancelCheckpointMarker(1));
        final int eventSize = eventBufferConsumer.getWrittenBytes();
        partition.add(bufferConsumer.copy());
        partition.add(bufferConsumer.copy());
        partition.add(eventBufferConsumer);
        partition.add(bufferConsumer);
        Assert.assertEquals(4, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(3, partition.getBuffersInBacklog());
        Assert.assertEquals(0, partition.getTotalNumberOfBytes());// only updated when getting/releasing the buffers

        Assert.assertFalse(bufferConsumer.isRecycled());
        Assert.assertEquals(4, partition.releaseMemory());
        // now the bufferConsumer may be freed, depending on the timing of the write operation
        // -> let's do this check at the end of the test (to save some time)
        // still same statistics
        Assert.assertEquals(4, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(3, partition.getBuffersInBacklog());
        Assert.assertEquals((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 3) + eventSize), partition.getTotalNumberOfBytes());
        partition.finish();
        // + one EndOfPartitionEvent
        Assert.assertEquals(5, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(3, partition.getBuffersInBacklog());
        Assert.assertEquals(((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 3) + eventSize) + 4), partition.getTotalNumberOfBytes());
        AwaitableBufferAvailablityListener listener = new AwaitableBufferAvailablityListener();
        SpilledSubpartitionView reader = ((SpilledSubpartitionView) (partition.createReadView(listener)));
        Assert.assertEquals(1, listener.getNumNotifications());
        Assert.assertFalse(reader.nextBufferIsEvent());// buffer

        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 2, false, true);
        Assert.assertEquals(2, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 1, true, true);
        Assert.assertEquals(1, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextEvent(reader, eventSize, CancelCheckpointMarker.class, true, 1, false, true);
        Assert.assertEquals(1, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 0, true, true);
        Assert.assertEquals(0, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextEvent(reader, 4, EndOfPartitionEvent.class, false, 0, false, true);
        Assert.assertEquals(0, partition.getBuffersInBacklog());
        // finally check that the bufferConsumer has been freed after a successful (or failed) write
        final long deadline = (System.currentTimeMillis()) + 30000L;// 30 secs

        while ((!(bufferConsumer.isRecycled())) && ((System.currentTimeMillis()) < deadline)) {
            Thread.sleep(1);
        } 
        Assert.assertTrue(bufferConsumer.isRecycled());
    }

    /**
     * Tests that a spilled partition is correctly read back in via a spilled read view. The
     * partition went into spilled state before adding buffers and the access pattern resembles
     * the actual use of {@link org.apache.flink.runtime.io.network.api.writer.RecordWriter}.
     */
    @Test
    public void testConsumeSpilledPartitionSpilledBeforeAdd() throws Exception {
        SpillableSubpartition partition = createSubpartition();
        Assert.assertEquals(0, partition.releaseMemory());// <---- SPILL to disk

        BufferBuilder[] bufferBuilders = new BufferBuilder[]{ BufferBuilderTestUtils.createBufferBuilder(SpillableSubpartitionTest.BUFFER_DATA_SIZE), BufferBuilderTestUtils.createBufferBuilder(SpillableSubpartitionTest.BUFFER_DATA_SIZE), BufferBuilderTestUtils.createBufferBuilder(SpillableSubpartitionTest.BUFFER_DATA_SIZE), BufferBuilderTestUtils.createBufferBuilder(SpillableSubpartitionTest.BUFFER_DATA_SIZE) };
        BufferConsumer[] bufferConsumers = Arrays.stream(bufferBuilders).map(BufferBuilder::createBufferConsumer).toArray(BufferConsumer[]::new);
        BufferConsumer eventBufferConsumer = EventSerializer.toBufferConsumer(new CancelCheckpointMarker(1));
        final int eventSize = eventBufferConsumer.getWrittenBytes();
        // note: only the newest buffer may be unfinished!
        partition.add(bufferConsumers[0]);
        BufferBuilderTestUtils.fillBufferBuilder(bufferBuilders[0], SpillableSubpartitionTest.BUFFER_DATA_SIZE).finish();
        partition.add(bufferConsumers[1]);
        BufferBuilderTestUtils.fillBufferBuilder(bufferBuilders[1], SpillableSubpartitionTest.BUFFER_DATA_SIZE).finish();
        partition.add(eventBufferConsumer);
        partition.add(bufferConsumers[2]);
        bufferBuilders[2].finish();// remains empty

        partition.add(bufferConsumers[3]);
        // last one: partially filled, unfinished
        BufferBuilderTestUtils.fillBufferBuilder(bufferBuilders[3], ((SpillableSubpartitionTest.BUFFER_DATA_SIZE) / 2));
        // finished buffers only:
        int expectedSize = ((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 2) + eventSize;
        // now the bufferConsumer may be freed, depending on the timing of the write operation
        // -> let's do this check at the end of the test (to save some time)
        // still same statistics
        Assert.assertEquals(5, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(3, partition.getBuffersInBacklog());
        Assert.assertEquals(expectedSize, partition.getTotalNumberOfBytes());
        partition.finish();
        expectedSize += (SpillableSubpartitionTest.BUFFER_DATA_SIZE) / 2;// previously unfinished buffer

        expectedSize += 4;// + one EndOfPartitionEvent

        Assert.assertEquals(6, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(3, partition.getBuffersInBacklog());
        Assert.assertEquals(expectedSize, partition.getTotalNumberOfBytes());
        Arrays.stream(bufferConsumers).forEach(( bufferConsumer) -> assertTrue(bufferConsumer.isRecycled()));
        AwaitableBufferAvailablityListener listener = new AwaitableBufferAvailablityListener();
        SpilledSubpartitionView reader = ((SpilledSubpartitionView) (partition.createReadView(listener)));
        Assert.assertEquals(1, listener.getNumNotifications());
        Assert.assertFalse(reader.nextBufferIsEvent());// full buffer

        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 2, false, true);
        Assert.assertEquals(2, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 1, true, true);
        Assert.assertEquals(1, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextEvent(reader, eventSize, CancelCheckpointMarker.class, true, 1, false, true);
        Assert.assertEquals(1, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextBuffer(reader, ((SpillableSubpartitionTest.BUFFER_DATA_SIZE) / 2), true, 0, true, true);
        Assert.assertEquals(0, partition.getBuffersInBacklog());
        SubpartitionTestBase.assertNextEvent(reader, 4, EndOfPartitionEvent.class, false, 0, false, true);
        Assert.assertEquals(0, partition.getBuffersInBacklog());
        // close buffer consumers
        Arrays.stream(bufferConsumers).forEach(( bufferConsumer) -> bufferConsumer.close());
    }

    /**
     * Tests that a spilled partition is correctly read back in via a spilled
     * read view.
     */
    @Test
    public void testConsumeSpillablePartitionSpilledDuringConsume() throws Exception {
        SpillableSubpartition partition = createSubpartition();
        BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(SpillableSubpartitionTest.BUFFER_DATA_SIZE, SpillableSubpartitionTest.BUFFER_DATA_SIZE);
        BufferConsumer eventBufferConsumer = EventSerializer.toBufferConsumer(new CancelCheckpointMarker(1));
        final int eventSize = eventBufferConsumer.getWrittenBytes();
        partition.add(bufferConsumer.copy());
        partition.add(bufferConsumer.copy());
        partition.add(eventBufferConsumer);
        partition.add(bufferConsumer);
        partition.finish();
        Assert.assertEquals(5, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(3, partition.getBuffersInBacklog());
        Assert.assertEquals(0, partition.getTotalNumberOfBytes());// only updated when getting/spilling the buffers

        AwaitableBufferAvailablityListener listener = new AwaitableBufferAvailablityListener();
        SpillableSubpartitionView reader = ((SpillableSubpartitionView) (partition.createReadView(listener)));
        // Initial notification
        Assert.assertEquals(1, listener.getNumNotifications());
        Assert.assertFalse(bufferConsumer.isRecycled());
        Assert.assertFalse(reader.nextBufferIsEvent());
        // first buffer (non-spilled)
        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 2, false, false);
        Assert.assertEquals(SpillableSubpartitionTest.BUFFER_DATA_SIZE, partition.getTotalNumberOfBytes());// only updated when getting/spilling the buffers

        Assert.assertEquals(2, partition.getBuffersInBacklog());
        Assert.assertEquals(1, listener.getNumNotifications());// since isMoreAvailable is set to true, no need for notification

        Assert.assertFalse(bufferConsumer.isRecycled());
        // Spill now
        Assert.assertEquals(3, partition.releaseMemory());
        Assert.assertFalse(bufferConsumer.isRecycled());// still one in the reader!

        // still same statistics:
        Assert.assertEquals(5, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(2, partition.getBuffersInBacklog());
        // only updated when getting/spilling the buffers but without the nextBuffer (kept in memory)
        Assert.assertEquals(((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 2) + eventSize) + 4), partition.getTotalNumberOfBytes());
        // wait for successfully spilling all buffers (before that we may not access any spilled buffer and cannot rely on isMoreAvailable!)
        listener.awaitNotifications(2, 30000);
        // Spiller finished
        Assert.assertEquals(2, listener.getNumNotifications());
        // after consuming and releasing the next buffer, the bufferConsumer may be freed,
        // depending on the timing of the last write operation
        // -> retain once so that we can check below
        Buffer buffer = bufferConsumer.build();
        buffer.retainBuffer();
        // second buffer (retained in SpillableSubpartition#nextBuffer)
        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 1, true, false);
        Assert.assertEquals(((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 3) + eventSize) + 4), partition.getTotalNumberOfBytes());// finally integrates the nextBuffer statistics

        Assert.assertEquals(1, partition.getBuffersInBacklog());
        bufferConsumer.close();// recycle the retained buffer from above (should be the last reference!)

        // the event (spilled)
        SubpartitionTestBase.assertNextEvent(reader, eventSize, CancelCheckpointMarker.class, true, 1, false, true);
        Assert.assertEquals(((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 3) + eventSize) + 4), partition.getTotalNumberOfBytes());// already updated during spilling

        Assert.assertEquals(1, partition.getBuffersInBacklog());
        // last buffer (spilled)
        SubpartitionTestBase.assertNextBuffer(reader, SpillableSubpartitionTest.BUFFER_DATA_SIZE, true, 0, true, true);
        Assert.assertEquals(((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 3) + eventSize) + 4), partition.getTotalNumberOfBytes());// already updated during spilling

        Assert.assertEquals(0, partition.getBuffersInBacklog());
        buffer.recycleBuffer();
        Assert.assertTrue(buffer.isRecycled());
        // End of partition
        SubpartitionTestBase.assertNextEvent(reader, 4, EndOfPartitionEvent.class, false, 0, false, true);
        Assert.assertEquals(((((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 3) + eventSize) + 4), partition.getTotalNumberOfBytes());// already updated during spilling

        Assert.assertEquals(0, partition.getBuffersInBacklog());
        // finally check that the bufferConsumer has been freed after a successful (or failed) write
        final long deadline = (System.currentTimeMillis()) + 30000L;// 30 secs

        while ((!(bufferConsumer.isRecycled())) && ((System.currentTimeMillis()) < deadline)) {
            Thread.sleep(1);
        } 
        Assert.assertTrue(bufferConsumer.isRecycled());
    }

    /**
     * Tests {@link SpillableSubpartition#add(BufferConsumer)} with a spillable finished partition.
     */
    @Test
    public void testAddOnFinishedSpillablePartition() throws Exception {
        testAddOnFinishedPartition(false);
    }

    /**
     * Tests {@link SpillableSubpartition#add(BufferConsumer)} with a spilled finished partition.
     */
    @Test
    public void testAddOnFinishedSpilledPartition() throws Exception {
        testAddOnFinishedPartition(true);
    }

    @Test
    public void testAddOnReleasedSpillablePartition() throws Exception {
        testAddOnReleasedPartition(false);
    }

    @Test
    public void testAddOnReleasedSpilledPartition() throws Exception {
        testAddOnReleasedPartition(true);
    }

    /**
     * Tests {@link SpillableSubpartition#add(BufferConsumer)} with a spilled partition where adding the
     * write request fails with an exception.
     */
    @Test
    public void testAddOnSpilledPartitionWithSlowWriter() throws Exception {
        // simulate slow writer by a no-op write operation
        IOManager ioManager = new IOManagerAsyncWithNoOpBufferFileWriter();
        SpillableSubpartition partition = SpillableSubpartitionTest.createSubpartition(ioManager);
        Assert.assertEquals(0, partition.releaseMemory());
        BufferConsumer buffer = BufferBuilderTestUtils.createFilledBufferConsumer(SpillableSubpartitionTest.BUFFER_DATA_SIZE, SpillableSubpartitionTest.BUFFER_DATA_SIZE);
        boolean bufferRecycled;
        try {
            partition.add(buffer);
        } finally {
            ioManager.shutdown();
            bufferRecycled = buffer.isRecycled();
            if (!bufferRecycled) {
                buffer.close();
            }
        }
        if (bufferRecycled) {
            Assert.fail("buffer recycled before the write operation completed");
        }
        Assert.assertEquals(1, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(SpillableSubpartitionTest.BUFFER_DATA_SIZE, partition.getTotalNumberOfBytes());
    }

    /**
     * Tests {@link SpillableSubpartition#releaseMemory()} with a spillable partition without a view
     * but with a writer that does not do any write to check for correct buffer recycling.
     */
    @Test
    public void testReleaseOnSpillablePartitionWithoutViewWithSlowWriter() throws Exception {
        testReleaseOnSpillablePartitionWithSlowWriter(false);
    }

    /**
     * Tests {@link SpillableSubpartition#releaseMemory()} with a spillable partition which has a
     * view associated with it and a writer that does not do any write to check for correct buffer
     * recycling.
     */
    @Test
    public void testReleaseOnSpillablePartitionWithViewWithSlowWriter() throws Exception {
        testReleaseOnSpillablePartitionWithSlowWriter(true);
    }

    /**
     * Tests {@link SpillableSubpartition#add(BufferConsumer)} with a spilled partition where adding the
     * write request fails with an exception.
     */
    @Test
    public void testAddOnSpilledPartitionWithFailingWriter() throws Exception {
        IOManager ioManager = new SpillableSubpartitionTest.IOManagerAsyncWithClosedBufferFileWriter();
        SpillableSubpartition partition = SpillableSubpartitionTest.createSubpartition(ioManager);
        Assert.assertEquals(0, partition.releaseMemory());
        exception.expect(IOException.class);
        BufferConsumer buffer = BufferBuilderTestUtils.createFilledBufferConsumer(SpillableSubpartitionTest.BUFFER_DATA_SIZE, SpillableSubpartitionTest.BUFFER_DATA_SIZE);
        boolean bufferRecycled;
        try {
            partition.add(buffer);
        } finally {
            ioManager.shutdown();
            bufferRecycled = buffer.isRecycled();
            if (!bufferRecycled) {
                buffer.close();
            }
        }
        if (!bufferRecycled) {
            Assert.fail("buffer not recycled");
        }
        Assert.assertEquals(0, partition.getTotalNumberOfBuffers());
        Assert.assertEquals(0, partition.getTotalNumberOfBytes());
    }

    /**
     * Tests cleanup of {@link SpillableSubpartition#release()} with a spillable partition and no
     * read view attached.
     */
    @Test
    public void testCleanupReleasedSpillablePartitionNoView() throws Exception {
        testCleanupReleasedPartition(false, false);
    }

    /**
     * Tests cleanup of {@link SpillableSubpartition#release()} with a spillable partition and a
     * read view attached - [FLINK-8371].
     */
    @Test
    public void testCleanupReleasedSpillablePartitionWithView() throws Exception {
        testCleanupReleasedPartition(false, true);
    }

    /**
     * Tests cleanup of {@link SpillableSubpartition#release()} with a spilled partition and no
     * read view attached.
     */
    @Test
    public void testCleanupReleasedSpilledPartitionNoView() throws Exception {
        testCleanupReleasedPartition(true, false);
    }

    /**
     * Tests cleanup of {@link SpillableSubpartition#release()} with a spilled partition and a
     * read view attached.
     */
    @Test
    public void testCleanupReleasedSpilledPartitionWithView() throws Exception {
        testCleanupReleasedPartition(true, true);
    }

    /**
     * Tests {@link SpillableSubpartition#spillFinishedBufferConsumers} spilled bytes and
     * buffers counting.
     */
    @Test
    public void testSpillFinishedBufferConsumersFull() throws Exception {
        SpillableSubpartition partition = createSubpartition();
        BufferBuilder bufferBuilder = BufferBuilderTestUtils.createBufferBuilder(SpillableSubpartitionTest.BUFFER_DATA_SIZE);
        partition.add(bufferBuilder.createBufferConsumer());
        Assert.assertEquals(0, partition.releaseMemory());
        Assert.assertEquals(1, partition.getBuffersInBacklog());
        // finally fill the buffer with some bytes
        BufferBuilderTestUtils.fillBufferBuilder(bufferBuilder, SpillableSubpartitionTest.BUFFER_DATA_SIZE).finish();
        Assert.assertEquals(SpillableSubpartitionTest.BUFFER_DATA_SIZE, partition.spillFinishedBufferConsumers(false));
        Assert.assertEquals(1, partition.getBuffersInBacklog());
    }

    /**
     * Tests {@link SpillableSubpartition#spillFinishedBufferConsumers} spilled bytes and
     * buffers counting with partially filled buffers.
     */
    @Test
    public void testSpillFinishedBufferConsumersPartial() throws Exception {
        SpillableSubpartition partition = createSubpartition();
        BufferBuilder bufferBuilder = BufferBuilderTestUtils.createBufferBuilder(((SpillableSubpartitionTest.BUFFER_DATA_SIZE) * 2));
        partition.add(bufferBuilder.createBufferConsumer());
        BufferBuilderTestUtils.fillBufferBuilder(bufferBuilder, SpillableSubpartitionTest.BUFFER_DATA_SIZE);
        Assert.assertEquals(0, partition.releaseMemory());
        Assert.assertEquals(2, partition.getBuffersInBacklog());// partial one spilled, buffer consumer still enqueued

        // finally fill the buffer with some bytes
        BufferBuilderTestUtils.fillBufferBuilder(bufferBuilder, SpillableSubpartitionTest.BUFFER_DATA_SIZE).finish();
        Assert.assertEquals(SpillableSubpartitionTest.BUFFER_DATA_SIZE, partition.spillFinishedBufferConsumers(false));
        Assert.assertEquals(2, partition.getBuffersInBacklog());
    }

    /**
     * An {@link IOManagerAsync} that creates closed {@link BufferFileWriter} instances in its
     * {@link #createBufferFileWriter(FileIOChannel.ID)} method.
     *
     * <p>These {@link BufferFileWriter} objects will thus throw an exception when trying to add
     * write requests, e.g. by calling {@link BufferFileWriter#writeBlock(Object)}.
     */
    private static class IOManagerAsyncWithClosedBufferFileWriter extends IOManagerAsync {
        @Override
        public BufferFileWriter createBufferFileWriter(FileIOChannel.ID channelID) throws IOException {
            BufferFileWriter bufferFileWriter = super.createBufferFileWriter(channelID);
            bufferFileWriter.close();
            return bufferFileWriter;
        }
    }
}

