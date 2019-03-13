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
package org.apache.flink.runtime.io.network.buffer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the {@link LocalBufferPool}.
 */
public class LocalBufferPoolTest extends TestLogger {
    private static final int numBuffers = 1024;

    private static final int memorySegmentSize = 128;

    private NetworkBufferPool networkBufferPool;

    private BufferPool localBufferPool;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void testRequestMoreThanAvailable() throws IOException {
        localBufferPool.setNumBuffers(LocalBufferPoolTest.numBuffers);
        List<Buffer> requests = new ArrayList<Buffer>(LocalBufferPoolTest.numBuffers);
        for (int i = 1; i <= (LocalBufferPoolTest.numBuffers); i++) {
            Buffer buffer = localBufferPool.requestBuffer();
            Assert.assertEquals(i, getNumRequestedFromMemorySegmentPool());
            Assert.assertNotNull(buffer);
            requests.add(buffer);
        }
        {
            // One more...
            Buffer buffer = localBufferPool.requestBuffer();
            Assert.assertEquals(LocalBufferPoolTest.numBuffers, getNumRequestedFromMemorySegmentPool());
            Assert.assertNull(buffer);
        }
        for (Buffer buffer : requests) {
            buffer.recycleBuffer();
        }
    }

    @Test
    public void testRequestAfterDestroy() throws IOException {
        localBufferPool.lazyDestroy();
        try {
            localBufferPool.requestBuffer();
            Assert.fail("Call should have failed with an IllegalStateException");
        } catch (IllegalStateException e) {
            // we expect exactly that
        }
    }

    @Test
    public void testRecycleAfterDestroy() throws IOException {
        localBufferPool.setNumBuffers(LocalBufferPoolTest.numBuffers);
        List<Buffer> requests = new ArrayList<Buffer>(LocalBufferPoolTest.numBuffers);
        for (int i = 0; i < (LocalBufferPoolTest.numBuffers); i++) {
            requests.add(localBufferPool.requestBuffer());
        }
        localBufferPool.lazyDestroy();
        // All buffers have been requested, but can not be returned yet.
        Assert.assertEquals(LocalBufferPoolTest.numBuffers, getNumRequestedFromMemorySegmentPool());
        // Recycle should return buffers to memory segment pool
        for (Buffer buffer : requests) {
            buffer.recycleBuffer();
        }
    }

    @Test
    public void testRecycleExcessBuffersAfterRecycling() throws Exception {
        localBufferPool.setNumBuffers(LocalBufferPoolTest.numBuffers);
        List<Buffer> requests = new ArrayList<Buffer>(LocalBufferPoolTest.numBuffers);
        // Request all buffers
        for (int i = 1; i <= (LocalBufferPoolTest.numBuffers); i++) {
            requests.add(localBufferPool.requestBuffer());
        }
        Assert.assertEquals(LocalBufferPoolTest.numBuffers, getNumRequestedFromMemorySegmentPool());
        // Reduce the number of buffers in the local pool
        localBufferPool.setNumBuffers(((LocalBufferPoolTest.numBuffers) / 2));
        // Need to wait until we recycle the buffers
        Assert.assertEquals(LocalBufferPoolTest.numBuffers, getNumRequestedFromMemorySegmentPool());
        for (int i = 1; i < ((LocalBufferPoolTest.numBuffers) / 2); i++) {
            requests.remove(0).recycleBuffer();
            Assert.assertEquals(((LocalBufferPoolTest.numBuffers) - i), getNumRequestedFromMemorySegmentPool());
        }
        for (Buffer buffer : requests) {
            buffer.recycleBuffer();
        }
    }

    @Test
    public void testRecycleExcessBuffersAfterChangingNumBuffers() throws Exception {
        localBufferPool.setNumBuffers(LocalBufferPoolTest.numBuffers);
        List<Buffer> requests = new ArrayList<Buffer>(LocalBufferPoolTest.numBuffers);
        // Request all buffers
        for (int i = 1; i <= (LocalBufferPoolTest.numBuffers); i++) {
            requests.add(localBufferPool.requestBuffer());
        }
        // Recycle all
        for (Buffer buffer : requests) {
            buffer.recycleBuffer();
        }
        Assert.assertEquals(LocalBufferPoolTest.numBuffers, localBufferPool.getNumberOfAvailableMemorySegments());
        localBufferPool.setNumBuffers(((LocalBufferPoolTest.numBuffers) / 2));
        Assert.assertEquals(((LocalBufferPoolTest.numBuffers) / 2), localBufferPool.getNumberOfAvailableMemorySegments());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLessThanRequiredNumBuffers() throws IOException {
        localBufferPool.setNumBuffers(1);
        localBufferPool.setNumBuffers(0);
    }

    // ------------------------------------------------------------------------
    // Pending requests and integration with buffer futures
    // ------------------------------------------------------------------------
    @Test
    public void testPendingRequestWithListenersAfterRecycle() throws Exception {
        BufferListener twoTimesListener = createBufferListener(2);
        BufferListener oneTimeListener = createBufferListener(1);
        localBufferPool.setNumBuffers(2);
        Buffer available1 = localBufferPool.requestBuffer();
        Buffer available2 = localBufferPool.requestBuffer();
        Assert.assertNull(localBufferPool.requestBuffer());
        Assert.assertTrue(localBufferPool.addBufferListener(twoTimesListener));
        Assert.assertTrue(localBufferPool.addBufferListener(oneTimeListener));
        // Recycle the first buffer to notify both of the above listeners once
        // and the twoTimesListener will be added into the registeredListeners
        // queue of buffer pool again
        available1.recycleBuffer();
        Mockito.verify(oneTimeListener, Mockito.times(1)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
        Mockito.verify(twoTimesListener, Mockito.times(1)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
        // Recycle the second buffer to only notify the twoTimesListener
        available2.recycleBuffer();
        Mockito.verify(oneTimeListener, Mockito.times(1)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
        Mockito.verify(twoTimesListener, Mockito.times(2)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCancelPendingRequestsAfterDestroy() throws IOException {
        BufferListener listener = Mockito.mock(BufferListener.class);
        localBufferPool.setNumBuffers(1);
        Buffer available = localBufferPool.requestBuffer();
        Buffer unavailable = localBufferPool.requestBuffer();
        Assert.assertNull(unavailable);
        localBufferPool.addBufferListener(listener);
        localBufferPool.lazyDestroy();
        available.recycleBuffer();
        Mockito.verify(listener, Mockito.times(1)).notifyBufferDestroyed();
    }

    // ------------------------------------------------------------------------
    // Concurrent requests
    // ------------------------------------------------------------------------
    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentRequestRecycle() throws IOException, InterruptedException, ExecutionException {
        int numConcurrentTasks = 128;
        int numBuffersToRequestPerTask = 1024;
        localBufferPool.setNumBuffers(numConcurrentTasks);
        Future<Boolean>[] taskResults = new Future[numConcurrentTasks];
        for (int i = 0; i < numConcurrentTasks; i++) {
            taskResults[i] = LocalBufferPoolTest.executor.submit(new LocalBufferPoolTest.BufferRequesterTask(localBufferPool, numBuffersToRequestPerTask));
        }
        for (int i = 0; i < numConcurrentTasks; i++) {
            Assert.assertTrue(taskResults[i].get());
        }
    }

    @Test
    public void testDestroyDuringBlockingRequest() throws Exception {
        // Config
        final int numberOfBuffers = 1;
        localBufferPool.setNumBuffers(numberOfBuffers);
        final CountDownLatch sync = new CountDownLatch(1);
        final Callable<List<Buffer>> requester = new Callable<List<Buffer>>() {
            // Request all buffers in a blocking manner.
            @Override
            public List<Buffer> call() throws Exception {
                final List<Buffer> requested = Lists.newArrayList();
                // Request all available buffers
                for (int i = 0; i < numberOfBuffers; i++) {
                    requested.add(localBufferPool.requestBufferBlocking());
                }
                // Notify that we've requested all buffers
                sync.countDown();
                // Try to request the next buffer (but pool should be destroyed either right before
                // the request or more likely during the request).
                try {
                    localBufferPool.requestBufferBlocking();
                    Assert.fail("Call should have failed with an IllegalStateException");
                } catch (IllegalStateException e) {
                    // we expect exactly that
                }
                return requested;
            }
        };
        Future<List<Buffer>> f = LocalBufferPoolTest.executor.submit(requester);
        sync.await();
        localBufferPool.lazyDestroy();
        // Increase the likelihood that the requested is currently in the request call
        Thread.sleep(50);
        // This should return immediately if everything works as expected
        List<Buffer> requestedBuffers = f.get(60, TimeUnit.SECONDS);
        for (Buffer buffer : requestedBuffers) {
            buffer.recycleBuffer();
        }
    }

    @Test
    public void testBoundedBuffer() throws Exception {
        localBufferPool.lazyDestroy();
        localBufferPool = new LocalBufferPool(networkBufferPool, 1, 2);
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertEquals(2, localBufferPool.getMaxNumberOfMemorySegments());
        Buffer buffer1;
        Buffer buffer2;
        // check min number of buffers:
        localBufferPool.setNumBuffers(1);
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNotNull((buffer1 = localBufferPool.requestBuffer()));
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNull(localBufferPool.requestBuffer());
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        buffer1.recycleBuffer();
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
        // check max number of buffers:
        localBufferPool.setNumBuffers(2);
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNotNull((buffer1 = localBufferPool.requestBuffer()));
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNotNull((buffer2 = localBufferPool.requestBuffer()));
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNull(localBufferPool.requestBuffer());
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        buffer1.recycleBuffer();
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
        buffer2.recycleBuffer();
        Assert.assertEquals(2, localBufferPool.getNumberOfAvailableMemorySegments());
        // try to set too large buffer size:
        localBufferPool.setNumBuffers(3);
        Assert.assertEquals(2, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNotNull((buffer1 = localBufferPool.requestBuffer()));
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNotNull((buffer2 = localBufferPool.requestBuffer()));
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNull(localBufferPool.requestBuffer());
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        buffer1.recycleBuffer();
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
        buffer2.recycleBuffer();
        Assert.assertEquals(2, localBufferPool.getNumberOfAvailableMemorySegments());
        // decrease size again
        localBufferPool.setNumBuffers(1);
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNotNull((buffer1 = localBufferPool.requestBuffer()));
        Assert.assertEquals(0, localBufferPool.getNumberOfAvailableMemorySegments());
        Assert.assertNull(localBufferPool.requestBuffer());
        buffer1.recycleBuffer();
        Assert.assertEquals(1, localBufferPool.getNumberOfAvailableMemorySegments());
    }

    private static class BufferRequesterTask implements Callable<Boolean> {
        private final BufferProvider bufferProvider;

        private final int numBuffersToRequest;

        private BufferRequesterTask(BufferProvider bufferProvider, int numBuffersToRequest) {
            this.bufferProvider = bufferProvider;
            this.numBuffersToRequest = numBuffersToRequest;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                for (int i = 0; i < (numBuffersToRequest); i++) {
                    Buffer buffer = bufferProvider.requestBufferBlocking();
                    buffer.recycleBuffer();
                }
            } catch (Throwable t) {
                return false;
            }
            return true;
        }
    }
}

