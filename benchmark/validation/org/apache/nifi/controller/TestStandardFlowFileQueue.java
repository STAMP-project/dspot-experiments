/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller;


import ProvenanceEventType.DROP;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.nifi.connectable.Connection;
import org.apache.nifi.controller.queue.DropFlowFileState;
import org.apache.nifi.controller.queue.DropFlowFileStatus;
import org.apache.nifi.controller.queue.ListFlowFileState;
import org.apache.nifi.controller.queue.ListFlowFileStatus;
import org.apache.nifi.controller.queue.NopConnectionEventListener;
import org.apache.nifi.controller.queue.QueueSize;
import org.apache.nifi.controller.queue.StandardFlowFileQueue;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.controller.repository.FlowFileRepository;
import org.apache.nifi.controller.repository.claim.ResourceClaimManager;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.FlowFilePrioritizer;
import org.apache.nifi.processor.FlowFileFilter;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceEventRepository;
import org.junit.Assert;
import org.junit.Test;

import static FlowFileFilterResult.REJECT_AND_CONTINUE;


public class TestStandardFlowFileQueue {
    private MockSwapManager swapManager = null;

    private StandardFlowFileQueue queue = null;

    private Connection connection = null;

    private FlowFileRepository flowFileRepo = null;

    private ProvenanceEventRepository provRepo = null;

    private ResourceClaimManager claimManager = null;

    private ProcessScheduler scheduler = null;

    private List<ProvenanceEventRecord> provRecords = new ArrayList<>();

    @Test
    public void testExpire() {
        queue.setFlowFileExpiration("1 ms");
        for (int i = 0; i < 100; i++) {
            queue.put(new MockFlowFileRecord());
        }
        // just make sure that the flowfiles have time to expire.
        try {
            Thread.sleep(100L);
        } catch (final InterruptedException ie) {
        }
        final Set<FlowFileRecord> expiredRecords = new HashSet<>(100);
        final FlowFileRecord pulled = queue.poll(expiredRecords);
        Assert.assertNull(pulled);
        Assert.assertEquals(100, expiredRecords.size());
        final QueueSize activeSize = queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize();
        Assert.assertEquals(0, activeSize.getObjectCount());
        Assert.assertEquals(0L, activeSize.getByteCount());
        final QueueSize unackSize = queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize();
        Assert.assertEquals(0, unackSize.getObjectCount());
        Assert.assertEquals(0L, unackSize.getByteCount());
    }

    @Test
    public void testBackPressure() {
        queue.setBackPressureObjectThreshold(10);
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.isActiveQueueEmpty());
        Assert.assertFalse(queue.isFull());
        for (int i = 0; i < 9; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertFalse(queue.isFull());
            Assert.assertFalse(queue.isEmpty());
            Assert.assertFalse(queue.isActiveQueueEmpty());
        }
        queue.put(new MockFlowFileRecord());
        Assert.assertTrue(queue.isFull());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertFalse(queue.isActiveQueueEmpty());
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final FlowFileRecord polled = queue.poll(expiredRecords);
        Assert.assertNotNull(polled);
        Assert.assertTrue(expiredRecords.isEmpty());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertFalse(queue.isActiveQueueEmpty());
        // queue is still full because FlowFile has not yet been acknowledged.
        Assert.assertTrue(queue.isFull());
        queue.acknowledge(polled);
        // FlowFile has been acknowledged; queue should no longer be full.
        Assert.assertFalse(queue.isFull());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertFalse(queue.isActiveQueueEmpty());
    }

    @Test
    public void testBackPressureAfterPollFilter() throws InterruptedException {
        queue.setBackPressureObjectThreshold(10);
        queue.setFlowFileExpiration("10 millis");
        for (int i = 0; i < 9; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertFalse(queue.isFull());
        }
        queue.put(new MockFlowFileRecord());
        Assert.assertTrue(queue.isFull());
        Thread.sleep(100L);
        final FlowFileFilter filter = new FlowFileFilter() {
            @Override
            public FlowFileFilterResult filter(final FlowFile flowFile) {
                return REJECT_AND_CONTINUE;
            }
        };
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final List<FlowFileRecord> polled = queue.poll(filter, expiredRecords);
        Assert.assertTrue(polled.isEmpty());
        Assert.assertEquals(10, expiredRecords.size());
        Assert.assertFalse(queue.isFull());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.isActiveQueueEmpty());
    }

    @Test(timeout = 10000)
    public void testBackPressureAfterDrop() throws InterruptedException {
        queue.setBackPressureObjectThreshold(10);
        queue.setFlowFileExpiration("10 millis");
        for (int i = 0; i < 9; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertFalse(queue.isFull());
        }
        queue.put(new MockFlowFileRecord());
        Assert.assertTrue(queue.isFull());
        Thread.sleep(100L);
        final String requestId = UUID.randomUUID().toString();
        final DropFlowFileStatus status = queue.dropFlowFiles(requestId, "Unit Test");
        while ((status.getState()) != (DropFlowFileState.COMPLETE)) {
            Thread.sleep(10L);
        } 
        Assert.assertFalse(queue.isFull());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.isActiveQueueEmpty());
        Assert.assertEquals(10, provRecords.size());
        for (final ProvenanceEventRecord event : provRecords) {
            Assert.assertNotNull(event);
            Assert.assertEquals(DROP, event.getEventType());
        }
    }

    @Test
    public void testBackPressureAfterPollSingle() throws InterruptedException {
        queue.setBackPressureObjectThreshold(10);
        queue.setFlowFileExpiration("10 millis");
        for (int i = 0; i < 9; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertFalse(queue.isFull());
        }
        queue.put(new MockFlowFileRecord());
        Assert.assertTrue(queue.isFull());
        Thread.sleep(100L);
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final FlowFileRecord polled = queue.poll(expiredRecords);
        Assert.assertNull(polled);
        Assert.assertEquals(10, expiredRecords.size());
        Assert.assertFalse(queue.isFull());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.isActiveQueueEmpty());
    }

    @Test
    public void testBackPressureAfterPollMultiple() throws InterruptedException {
        queue.setBackPressureObjectThreshold(10);
        queue.setFlowFileExpiration("10 millis");
        for (int i = 0; i < 9; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertFalse(queue.isFull());
        }
        queue.put(new MockFlowFileRecord());
        Assert.assertTrue(queue.isFull());
        Thread.sleep(100L);
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final List<FlowFileRecord> polled = queue.poll(10, expiredRecords);
        Assert.assertTrue(polled.isEmpty());
        Assert.assertEquals(10, expiredRecords.size());
        Assert.assertFalse(queue.isFull());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.isActiveQueueEmpty());
    }

    @Test
    public void testSwapOutOccurs() {
        for (int i = 0; i < 10000; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertEquals(0, swapManager.swapOutCalledCount);
            Assert.assertEquals((i + 1), queue.size().getObjectCount());
            Assert.assertEquals((i + 1), queue.size().getByteCount());
        }
        for (int i = 0; i < 9999; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertEquals(0, swapManager.swapOutCalledCount);
            Assert.assertEquals((i + 10001), queue.size().getObjectCount());
            Assert.assertEquals((i + 10001), queue.size().getByteCount());
        }
        queue.put(new MockFlowFileRecord(1000));
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertEquals(20000, queue.size().getObjectCount());
        Assert.assertEquals(20999, queue.size().getByteCount());
        Assert.assertEquals(10000, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
    }

    @Test
    public void testLowestPrioritySwappedOutFirst() {
        final List<FlowFilePrioritizer> prioritizers = new ArrayList<>();
        prioritizers.add(new TestStandardFlowFileQueue.FlowFileSizePrioritizer());
        queue.setPriorities(prioritizers);
        long maxSize = 20000;
        for (int i = 1; i <= 20000; i++) {
            queue.put(new MockFlowFileRecord((maxSize - i)));
        }
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertEquals(20000, queue.size().getObjectCount());
        Assert.assertEquals(10000, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        final List<FlowFileRecord> flowFiles = queue.poll(Integer.MAX_VALUE, new HashSet<FlowFileRecord>());
        Assert.assertEquals(10000, flowFiles.size());
        for (int i = 0; i < 10000; i++) {
            Assert.assertEquals(i, flowFiles.get(i).getSize());
        }
    }

    @Test
    public void testSwapIn() {
        for (int i = 1; i <= 20000; i++) {
            queue.put(new MockFlowFileRecord());
        }
        Assert.assertEquals(1, swapManager.swappedOut.size());
        queue.put(new MockFlowFileRecord());
        Assert.assertEquals(1, swapManager.swappedOut.size());
        final Set<FlowFileRecord> exp = new HashSet<>();
        for (int i = 0; i < 9999; i++) {
            final FlowFileRecord flowFile = queue.poll(exp);
            Assert.assertNotNull(flowFile);
            Assert.assertEquals(1, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(1, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getByteCount());
            queue.acknowledge(Collections.singleton(flowFile));
            Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getByteCount());
        }
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(1, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertNotNull(queue.poll(exp));
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertNotNull(queue.poll(exp));// this should trigger a swap-in of 10,000 records, and then pull 1 off the top.

        Assert.assertEquals(1, swapManager.swapInCalledCount);
        Assert.assertEquals(9999, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertTrue(swapManager.swappedOut.isEmpty());
        queue.poll(exp);
    }

    @Test
    public void testSwapInWhenThresholdIsLessThanSwapSize() {
        // create a queue where the swap threshold is less than 10k
        queue = new StandardFlowFileQueue("id", new NopConnectionEventListener(), flowFileRepo, provRepo, claimManager, scheduler, swapManager, null, 1000, 0L, "0 B");
        for (int i = 1; i <= 20000; i++) {
            queue.put(new MockFlowFileRecord());
        }
        Assert.assertEquals(1, swapManager.swappedOut.size());
        queue.put(new MockFlowFileRecord());
        Assert.assertEquals(1, swapManager.swappedOut.size());
        final Set<FlowFileRecord> exp = new HashSet<>();
        // At this point there should be:
        // 1k flow files in the active queue
        // 9,001 flow files in the swap queue
        // 10k flow files swapped to disk
        for (int i = 0; i < 999; i++) {
            // 
            final FlowFileRecord flowFile = queue.poll(exp);
            Assert.assertNotNull(flowFile);
            Assert.assertEquals(1, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(1, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getByteCount());
            queue.acknowledge(Collections.singleton(flowFile));
            Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getUnacknowledgedQueueSize().getByteCount());
        }
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(1, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertNotNull(queue.poll(exp));
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertNotNull(queue.poll(exp));// this should trigger a swap-in of 10,000 records, and then pull 1 off the top.

        Assert.assertEquals(1, swapManager.swapInCalledCount);
        Assert.assertEquals(9999, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertTrue(swapManager.swappedOut.isEmpty());
        queue.poll(exp);
    }

    @Test
    public void testQueueCountsUpdatedWhenIncompleteSwapFile() {
        for (int i = 1; i <= 20000; i++) {
            queue.put(new MockFlowFileRecord());
        }
        Assert.assertEquals(20000, queue.size().getObjectCount());
        Assert.assertEquals(20000, queue.size().getByteCount());
        Assert.assertEquals(1, swapManager.swappedOut.size());
        // when we swap in, cause an IncompleteSwapFileException to be
        // thrown and contain only 9,999 of the 10,000 FlowFiles
        swapManager.enableIncompleteSwapFileException(9999);
        final Set<FlowFileRecord> expired = Collections.emptySet();
        FlowFileRecord flowFile;
        for (int i = 0; i < 10000; i++) {
            flowFile = queue.poll(expired);
            Assert.assertNotNull(flowFile);
            queue.acknowledge(Collections.singleton(flowFile));
        }
        // 10,000 FlowFiles on queue - all swapped out
        Assert.assertEquals(10000, queue.size().getObjectCount());
        Assert.assertEquals(10000, queue.size().getByteCount());
        Assert.assertEquals(1, swapManager.swappedOut.size());
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        // Trigger swap in. This will remove 1 FlowFile from queue, leaving 9,999 but
        // on swap in, we will get only 9,999 FlowFiles put onto the queue, and the queue size will
        // be decremented by 10,000 (because the Swap File's header tells us that there are 10K
        // FlowFiles, even though only 9999 are in the swap file)
        flowFile = queue.poll(expired);
        Assert.assertNotNull(flowFile);
        queue.acknowledge(Collections.singleton(flowFile));
        // size should be 9,998 because we lost 1 on Swap In, and then we pulled one above.
        Assert.assertEquals(9998, queue.size().getObjectCount());
        Assert.assertEquals(9998, queue.size().getByteCount());
        Assert.assertEquals(0, swapManager.swappedOut.size());
        Assert.assertEquals(1, swapManager.swapInCalledCount);
        for (int i = 0; i < 9998; i++) {
            flowFile = queue.poll(expired);
            Assert.assertNotNull(("Null FlowFile when i = " + i), flowFile);
            queue.acknowledge(Collections.singleton(flowFile));
            final QueueSize queueSize = queue.size();
            Assert.assertEquals(((9998 - i) - 1), queueSize.getObjectCount());
            Assert.assertEquals(((9998 - i) - 1), queueSize.getByteCount());
        }
        final QueueSize queueSize = queue.size();
        Assert.assertEquals(0, queueSize.getObjectCount());
        Assert.assertEquals(0L, queueSize.getByteCount());
        flowFile = queue.poll(expired);
        Assert.assertNull(flowFile);
    }

    @Test(timeout = 120000)
    public void testDropSwappedFlowFiles() {
        for (int i = 1; i <= 30000; i++) {
            queue.put(new MockFlowFileRecord());
        }
        Assert.assertEquals(2, swapManager.swappedOut.size());
        final DropFlowFileStatus status = queue.dropFlowFiles("1", "Unit Test");
        while ((status.getState()) != (DropFlowFileState.COMPLETE)) {
            try {
                Thread.sleep(100L);
            } catch (final Exception e) {
            }
        } 
        Assert.assertEquals(0, queue.size().getObjectCount());
        Assert.assertEquals(0, queue.size().getByteCount());
        Assert.assertEquals(0, swapManager.swappedOut.size());
        Assert.assertEquals(2, swapManager.swapInCalledCount);
    }

    @Test(timeout = 5000)
    public void testListFlowFilesOnlyActiveQueue() throws InterruptedException {
        for (int i = 0; i < 9999; i++) {
            queue.put(new MockFlowFileRecord());
        }
        final ListFlowFileStatus status = queue.listFlowFiles(UUID.randomUUID().toString(), 10000);
        Assert.assertNotNull(status);
        Assert.assertEquals(9999, status.getQueueSize().getObjectCount());
        while ((status.getState()) != (ListFlowFileState.COMPLETE)) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(9999, status.getFlowFileSummaries().size());
        Assert.assertEquals(100, status.getCompletionPercentage());
        Assert.assertNull(status.getFailureReason());
    }

    @Test(timeout = 5000)
    public void testListFlowFilesResultsLimited() throws InterruptedException {
        for (int i = 0; i < 30050; i++) {
            queue.put(new MockFlowFileRecord());
        }
        final ListFlowFileStatus status = queue.listFlowFiles(UUID.randomUUID().toString(), 100);
        Assert.assertNotNull(status);
        Assert.assertEquals(30050, status.getQueueSize().getObjectCount());
        while ((status.getState()) != (ListFlowFileState.COMPLETE)) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(100, status.getFlowFileSummaries().size());
        Assert.assertEquals(100, status.getCompletionPercentage());
        Assert.assertNull(status.getFailureReason());
    }

    @Test(timeout = 5000)
    public void testListFlowFilesResultsLimitedCollection() throws InterruptedException {
        Collection<FlowFileRecord> tff = new ArrayList<>();
        // Swap Size is 10000 records, so 30000 is equal to 3 swap files.
        for (int i = 0; i < 30000; i++) {
            tff.add(new MockFlowFileRecord());
        }
        queue.putAll(tff);
        final ListFlowFileStatus status = queue.listFlowFiles(UUID.randomUUID().toString(), 100);
        Assert.assertNotNull(status);
        Assert.assertEquals(30000, status.getQueueSize().getObjectCount());
        while ((status.getState()) != (ListFlowFileState.COMPLETE)) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(100, status.getFlowFileSummaries().size());
        Assert.assertEquals(100, status.getCompletionPercentage());
        Assert.assertNull(status.getFailureReason());
    }

    @Test
    public void testOOMEFollowedBySuccessfulSwapIn() {
        final List<FlowFileRecord> flowFiles = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            flowFiles.add(new MockFlowFileRecord());
        }
        queue.putAll(flowFiles);
        swapManager.failSwapInAfterN = 2;
        swapManager.setSwapInFailure(new OutOfMemoryError("Intentional OOME for unit test"));
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        for (int i = 0; i < 30000; i++) {
            final FlowFileRecord polled = queue.poll(expiredRecords);
            Assert.assertNotNull(polled);
        }
        // verify that unexpected ERROR's are handled in such a way that we keep retrying
        for (int i = 0; i < 3; i++) {
            try {
                queue.poll(expiredRecords);
                Assert.fail("Expected OOME to be thrown");
            } catch (final OutOfMemoryError oome) {
                // expected
            }
        }
        // verify that unexpected Runtime Exceptions are handled in such a way that we keep retrying
        swapManager.setSwapInFailure(new NullPointerException("Intentional OOME for unit test"));
        for (int i = 0; i < 3; i++) {
            try {
                queue.poll(expiredRecords);
                Assert.fail("Expected NPE to be thrown");
            } catch (final NullPointerException npe) {
                // expected
            }
        }
        swapManager.failSwapInAfterN = -1;
        for (int i = 0; i < 20000; i++) {
            final FlowFileRecord polled = queue.poll(expiredRecords);
            Assert.assertNotNull(polled);
        }
        queue.acknowledge(flowFiles);
        Assert.assertNull(queue.poll(expiredRecords));
        Assert.assertEquals(0, queue.getQueueDiagnostics().getLocalQueuePartitionDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertEquals(0, queue.size().getObjectCount());
        Assert.assertTrue(swapManager.swappedOut.isEmpty());
    }

    private static class FlowFileSizePrioritizer implements FlowFilePrioritizer {
        @Override
        public int compare(final FlowFile o1, final FlowFile o2) {
            return Long.compare(o1.getSize(), o2.getSize());
        }
    }
}

