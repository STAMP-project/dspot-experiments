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
package org.apache.hadoop.hbase.io.hfile.bucket;


import BucketCache.WriterThread;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.io.hfile.BlockCacheKey;
import org.apache.hadoop.hbase.io.hfile.Cacheable;
import org.apache.hadoop.hbase.io.hfile.bucket.BucketCache.BucketEntry;
import org.apache.hadoop.hbase.io.hfile.bucket.BucketCache.RAMQueueEntry;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ IOTests.class, SmallTests.class })
public class TestBucketWriterThread {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBucketWriterThread.class);

    private BucketCache bc;

    private WriterThread wt;

    private BlockingQueue<RAMQueueEntry> q;

    private Cacheable plainCacheable;

    private BlockCacheKey plainKey;

    /**
     * A BucketCache that does not start its writer threads.
     */
    private static class MockBucketCache extends BucketCache {
        public MockBucketCache(String ioEngineName, long capacity, int blockSize, int[] bucketSizes, int writerThreadNum, int writerQLen, String persistencePath, int ioErrorsTolerationDuration) throws FileNotFoundException, IOException {
            super(ioEngineName, capacity, blockSize, bucketSizes, writerThreadNum, writerQLen, persistencePath, ioErrorsTolerationDuration, HBaseConfiguration.create());
        }

        @Override
        protected void startWriterThreads() {
            // intentional noop
        }
    }

    /**
     * Test non-error case just works.
     *
     * @throws FileNotFoundException
     * 		
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testNonErrorCase() throws IOException, InterruptedException {
        bc.cacheBlock(this.plainKey, this.plainCacheable);
        TestBucketWriterThread.doDrainOfOneEntry(this.bc, this.wt, this.q);
    }

    /**
     * Pass through a too big entry and ensure it is cleared from queues and ramCache.
     * Manually run the WriterThread.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTooBigEntry() throws InterruptedException {
        Cacheable tooBigCacheable = Mockito.mock(Cacheable.class);
        Mockito.when(tooBigCacheable.getSerializedLength()).thenReturn(Integer.MAX_VALUE);
        this.bc.cacheBlock(this.plainKey, tooBigCacheable);
        TestBucketWriterThread.doDrainOfOneEntry(this.bc, this.wt, this.q);
    }

    /**
     * Do IOE. Take the RAMQueueEntry that was on the queue, doctor it to throw exception, then
     * put it back and process it.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIOE() throws IOException, InterruptedException {
        this.bc.cacheBlock(this.plainKey, plainCacheable);
        RAMQueueEntry rqe = q.remove();
        RAMQueueEntry spiedRqe = Mockito.spy(rqe);
        Mockito.doThrow(new IOException("Mocked!")).when(spiedRqe).writeToCache(Mockito.any(), Mockito.any(), Mockito.any());
        this.q.add(spiedRqe);
        TestBucketWriterThread.doDrainOfOneEntry(bc, wt, q);
        // Cache disabled when ioes w/o ever healing.
        Assert.assertTrue((!(bc.isCacheEnabled())));
    }

    /**
     * Do Cache full exception
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testCacheFullException() throws IOException, InterruptedException {
        this.bc.cacheBlock(this.plainKey, plainCacheable);
        RAMQueueEntry rqe = q.remove();
        RAMQueueEntry spiedRqe = Mockito.spy(rqe);
        final CacheFullException cfe = new CacheFullException(0, 0);
        BucketEntry mockedBucketEntry = Mockito.mock(BucketEntry.class);
        Mockito.doThrow(cfe).doReturn(mockedBucketEntry).when(spiedRqe).writeToCache(Mockito.any(), Mockito.any(), Mockito.any());
        this.q.add(spiedRqe);
        TestBucketWriterThread.doDrainOfOneEntry(bc, wt, q);
    }
}

