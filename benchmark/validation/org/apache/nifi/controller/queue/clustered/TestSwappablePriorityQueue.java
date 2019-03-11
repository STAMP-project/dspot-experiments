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
package org.apache.nifi.controller.queue.clustered;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.nifi.controller.MockFlowFileRecord;
import org.apache.nifi.controller.MockSwapManager;
import org.apache.nifi.controller.queue.DropFlowFileAction;
import org.apache.nifi.controller.queue.DropFlowFileRequest;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.controller.queue.QueueSize;
import org.apache.nifi.controller.queue.SwappablePriorityQueue;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.flowfile.FlowFilePrioritizer;
import org.apache.nifi.util.MockFlowFile;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestSwappablePriorityQueue {
    private MockSwapManager swapManager;

    private final List<String> events = new ArrayList<>();

    private EventReporter eventReporter;

    private final FlowFileQueue flowFileQueue = Mockito.mock(FlowFileQueue.class);

    private final DropFlowFileAction dropAction = ( flowFiles, requestor) -> {
        return new QueueSize(flowFiles.size(), flowFiles.stream().mapToLong(FlowFileRecord::getSize).sum());
    };

    private SwappablePriorityQueue queue;

    @Test
    public void testSwapOutFailureLeavesCorrectQueueSize() {
        swapManager.setSwapOutFailureOnNthIteration(1, null);
        for (int i = 0; i < 19999; i++) {
            queue.put(new MockFlowFile(i));
        }
        Assert.assertEquals(19999, queue.size().getObjectCount());
        Assert.assertEquals(0, events.size());
        queue.put(new MockFlowFile(20000));
        Assert.assertEquals(20000, queue.size().getObjectCount());
        Assert.assertEquals(1, events.size());// Expect a single failure event to be emitted

    }

    @Test
    public void testPrioritizer() {
        final FlowFilePrioritizer prioritizer = ( o1, o2) -> Long.compare(o1.getId(), o2.getId());
        queue.setPriorities(Collections.singletonList(prioritizer));
        for (int i = 0; i < 5000; i++) {
            queue.put(new MockFlowFile(i));
        }
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        for (int i = 0; i < 5000; i++) {
            final FlowFileRecord polled = queue.poll(expiredRecords, 500000L);
            Assert.assertEquals(i, polled.getId());
        }
        // We can add flowfiles in reverse order (highest ID first) and we should still get the same order back when polling
        for (int i = 0; i < 5000; i++) {
            queue.put(new MockFlowFile((5000 - i)));
        }
        for (int i = 0; i < 5000; i++) {
            final FlowFileRecord polled = queue.poll(expiredRecords, 500000L);
            // ID's will start at 1, since the last FlowFile added will have ID of 5000 - 4999
            Assert.assertEquals((i + 1), polled.getId());
        }
        // Add FlowFiles again, then change prioritizer and ensure that the order is updated
        for (int i = 0; i < 5000; i++) {
            queue.put(new MockFlowFile(i));
        }
        final FlowFilePrioritizer reversePrioritizer = ( o1, o2) -> Long.compare(o2.getId(), o1.getId());
        queue.setPriorities(Collections.singletonList(reversePrioritizer));
        for (int i = 0; i < 5000; i++) {
            final FlowFileRecord polled = queue.poll(expiredRecords, 500000L);
            // ID's will start at 4999, since the last FlowFile added will have ID of 4999 (i < 5000, not i <= 5000).
            Assert.assertEquals(((5000 - i) - 1), polled.getId());
        }
    }

    @Test
    public void testPollWithOnlyExpiredFlowFile() {
        final FlowFileRecord expiredFlowFile = Mockito.mock(FlowFileRecord.class);
        Mockito.when(expiredFlowFile.getEntryDate()).thenReturn(((System.currentTimeMillis()) - 5000L));
        queue.put(expiredFlowFile);
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final FlowFileRecord polled = queue.poll(expiredRecords, 4999);
        Assert.assertNull(polled);
        Assert.assertEquals(1, expiredRecords.size());
        final FlowFileRecord expired = expiredRecords.iterator().next();
        Assert.assertSame(expiredFlowFile, expired);
    }

    @Test
    public void testPollWithExpiredAndUnexpired() {
        final SwappablePriorityQueue queue = new SwappablePriorityQueue(swapManager, 100, eventReporter, flowFileQueue, dropAction, "local");
        final FlowFileRecord expiredFlowFile = Mockito.mock(FlowFileRecord.class);
        Mockito.when(expiredFlowFile.getEntryDate()).thenReturn(((System.currentTimeMillis()) - 5000L));
        queue.put(expiredFlowFile);
        final FlowFileRecord unexpiredFlowFile = Mockito.mock(FlowFileRecord.class);
        Mockito.when(unexpiredFlowFile.getEntryDate()).thenReturn(((System.currentTimeMillis()) + 500000L));
        queue.put(unexpiredFlowFile);
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final FlowFileRecord polled = queue.poll(expiredRecords, 4999);
        Assert.assertSame(unexpiredFlowFile, polled);
        Assert.assertEquals(1, expiredRecords.size());
        final FlowFileRecord expired = expiredRecords.iterator().next();
        Assert.assertSame(expiredFlowFile, expired);
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.isActiveQueueEmpty());
        for (int i = 0; i < 9; i++) {
            queue.put(new MockFlowFileRecord());
            Assert.assertFalse(queue.isEmpty());
            Assert.assertFalse(queue.isActiveQueueEmpty());
        }
        queue.put(new MockFlowFileRecord());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertFalse(queue.isActiveQueueEmpty());
        final Set<FlowFileRecord> expiredRecords = new HashSet<>();
        final FlowFileRecord polled = queue.poll(expiredRecords, 500000);
        Assert.assertNotNull(polled);
        Assert.assertTrue(expiredRecords.isEmpty());
        Assert.assertFalse(queue.isEmpty());
        Assert.assertFalse(queue.isActiveQueueEmpty());
        // queue is still full because FlowFile has not yet been acknowledged.
        queue.acknowledge(polled);
        // FlowFile has been acknowledged; queue should no longer be full.
        Assert.assertFalse(queue.isEmpty());
        Assert.assertFalse(queue.isActiveQueueEmpty());
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
        Assert.assertEquals(10000, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
    }

    @Test
    public void testLowestPrioritySwappedOutFirst() {
        final List<FlowFilePrioritizer> prioritizers = new ArrayList<>();
        prioritizers.add(( o1, o2) -> Long.compare(o1.getSize(), o2.getSize()));
        queue.setPriorities(prioritizers);
        long maxSize = 20000;
        for (int i = 1; i <= 20000; i++) {
            queue.put(new MockFlowFileRecord((maxSize - i)));
        }
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertEquals(20000, queue.size().getObjectCount());
        Assert.assertEquals(10000, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        final List<FlowFileRecord> flowFiles = queue.poll(Integer.MAX_VALUE, new HashSet<FlowFileRecord>(), 500000);
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
            final FlowFileRecord flowFile = queue.poll(exp, 500000);
            Assert.assertNotNull(flowFile);
            Assert.assertEquals(1, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(1, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getByteCount());
            queue.acknowledge(Collections.singleton(flowFile));
            Assert.assertEquals(0, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(0, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getByteCount());
        }
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(1, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertNotNull(queue.poll(exp, 500000));
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(0, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertNotNull(queue.poll(exp, 500000));// this should trigger a swap-in of 10,000 records, and then pull 1 off the top.

        Assert.assertEquals(1, swapManager.swapInCalledCount);
        Assert.assertEquals(9999, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertTrue(swapManager.swappedOut.isEmpty());
        queue.poll(exp, 500000);
    }

    @Test
    public void testSwapInWhenThresholdIsLessThanSwapSize() {
        // create a queue where the swap threshold is less than 10k
        queue = new SwappablePriorityQueue(swapManager, 1000, eventReporter, flowFileQueue, dropAction, null);
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
            final FlowFileRecord flowFile = queue.poll(exp, 500000);
            Assert.assertNotNull(flowFile);
            Assert.assertEquals(1, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(1, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getByteCount());
            queue.acknowledge(Collections.singleton(flowFile));
            Assert.assertEquals(0, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getObjectCount());
            Assert.assertEquals(0, queue.getQueueDiagnostics().getUnacknowledgedQueueSize().getByteCount());
        }
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(1, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertNotNull(queue.poll(exp, 500000));
        Assert.assertEquals(0, swapManager.swapInCalledCount);
        Assert.assertEquals(0, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertEquals(1, swapManager.swapOutCalledCount);
        Assert.assertNotNull(queue.poll(exp, 500000));// this should trigger a swap-in of 10,000 records, and then pull 1 off the top.

        Assert.assertEquals(1, swapManager.swapInCalledCount);
        Assert.assertEquals(9999, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertTrue(swapManager.swappedOut.isEmpty());
        queue.poll(exp, 500000);
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
            flowFile = queue.poll(expired, 500000);
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
        flowFile = queue.poll(expired, 500000);
        Assert.assertNotNull(flowFile);
        queue.acknowledge(Collections.singleton(flowFile));
        // size should be 9,998 because we lost 1 on Swap In, and then we pulled one above.
        Assert.assertEquals(9998, queue.size().getObjectCount());
        Assert.assertEquals(9998, queue.size().getByteCount());
        Assert.assertEquals(0, swapManager.swappedOut.size());
        Assert.assertEquals(1, swapManager.swapInCalledCount);
        for (int i = 0; i < 9998; i++) {
            flowFile = queue.poll(expired, 500000);
            Assert.assertNotNull(("Null FlowFile when i = " + i), flowFile);
            queue.acknowledge(Collections.singleton(flowFile));
            final QueueSize queueSize = queue.size();
            Assert.assertEquals(((9998 - i) - 1), queueSize.getObjectCount());
            Assert.assertEquals(((9998 - i) - 1), queueSize.getByteCount());
        }
        final QueueSize queueSize = queue.size();
        Assert.assertEquals(0, queueSize.getObjectCount());
        Assert.assertEquals(0L, queueSize.getByteCount());
        flowFile = queue.poll(expired, 500000);
        Assert.assertNull(flowFile);
    }

    @Test(timeout = 120000)
    public void testDropSwappedFlowFiles() {
        for (int i = 1; i <= 30000; i++) {
            queue.put(new MockFlowFileRecord());
        }
        Assert.assertEquals(2, swapManager.swappedOut.size());
        final DropFlowFileRequest request = new DropFlowFileRequest("Unit Test");
        queue.dropFlowFiles(request, "Unit Test");
        Assert.assertEquals(0, queue.size().getObjectCount());
        Assert.assertEquals(0, queue.size().getByteCount());
        Assert.assertEquals(0, swapManager.swappedOut.size());
        Assert.assertEquals(2, swapManager.swapInCalledCount);
    }

    @Test(timeout = 5000)
    public void testGetActiveFlowFilesReturnsAllActiveFlowFiles() throws InterruptedException {
        for (int i = 0; i < 9999; i++) {
            queue.put(new MockFlowFileRecord());
        }
        final List<FlowFileRecord> active = queue.getActiveFlowFiles();
        Assert.assertNotNull(active);
        Assert.assertEquals(9999, active.size());
    }

    @Test(timeout = 5000)
    public void testListFlowFilesResultsLimited() throws InterruptedException {
        for (int i = 0; i < 30050; i++) {
            queue.put(new MockFlowFileRecord());
        }
        final List<FlowFileRecord> activeFlowFiles = queue.getActiveFlowFiles();
        Assert.assertNotNull(activeFlowFiles);
        Assert.assertEquals(10000, activeFlowFiles.size());
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
            final FlowFileRecord polled = queue.poll(expiredRecords, 500000);
            Assert.assertNotNull(polled);
        }
        // verify that unexpected ERROR's are handled in such a way that we keep retrying
        for (int i = 0; i < 3; i++) {
            try {
                queue.poll(expiredRecords, 500000);
                Assert.fail("Expected OOME to be thrown");
            } catch (final OutOfMemoryError oome) {
                // expected
            }
        }
        // verify that unexpected Runtime Exceptions are handled in such a way that we keep retrying
        swapManager.setSwapInFailure(new NullPointerException("Intentional OOME for unit test"));
        for (int i = 0; i < 3; i++) {
            try {
                queue.poll(expiredRecords, 500000);
                Assert.fail("Expected NPE to be thrown");
            } catch (final NullPointerException npe) {
                // expected
            }
        }
        swapManager.failSwapInAfterN = -1;
        for (int i = 0; i < 20000; i++) {
            final FlowFileRecord polled = queue.poll(expiredRecords, 500000);
            Assert.assertNotNull(polled);
        }
        queue.acknowledge(flowFiles);
        Assert.assertNull(queue.poll(expiredRecords, 500000));
        Assert.assertEquals(0, queue.getQueueDiagnostics().getActiveQueueSize().getObjectCount());
        Assert.assertEquals(0, queue.size().getObjectCount());
        Assert.assertTrue(swapManager.swappedOut.isEmpty());
    }
}

