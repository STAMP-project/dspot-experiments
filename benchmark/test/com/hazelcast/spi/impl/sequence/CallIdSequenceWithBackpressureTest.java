/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.sequence;


import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.impl.operationservice.impl.DummyBackupAwareOperation;
import com.hazelcast.spi.impl.operationservice.impl.DummyOperation;
import com.hazelcast.spi.impl.operationservice.impl.DummyPriorityOperation;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CallIdSequenceWithBackpressureTest extends HazelcastTestSupport {
    CallIdSequenceWithBackpressure sequence = new CallIdSequenceWithBackpressure(100, 60000);

    @Test
    public void test() {
        Assert.assertEquals(0, sequence.getLastCallId());
        Assert.assertEquals(100, sequence.getMaxConcurrentInvocations());
    }

    @Test
    public void whenNext_thenSequenceIncrements() {
        // regular operation
        testNext(new DummyOperation());
        // backup-aware operation
        testNext(new DummyBackupAwareOperation());
        // urgent operation
        testNext(new DummyPriorityOperation());
    }

    @Test
    public void next_whenNoCapacity_thenBlockTillCapacity() throws InterruptedException {
        sequence = new CallIdSequenceWithBackpressure(1, 60000);
        final long oldLastCallId = sequence.getLastCallId();
        final CountDownLatch nextCalledLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                DummyBackupAwareOperation op = new DummyBackupAwareOperation();
                long callId = CallIdSequenceWithBackpressureTest.nextCallId(sequence, isUrgent());
                OperationAccessor.setCallId(op, callId);
                nextCalledLatch.countDown();
                HazelcastTestSupport.sleepSeconds(3);
                sequence.complete();
            }
        });
        nextCalledLatch.await();
        long result = CallIdSequenceWithBackpressureTest.nextCallId(sequence, false);
        Assert.assertEquals((oldLastCallId + 2), result);
        Assert.assertEquals((oldLastCallId + 2), sequence.getLastCallId());
    }

    @Test
    public void next_whenNoCapacity_thenBlockTillTimeout() {
        sequence = new CallIdSequenceWithBackpressure(1, 2000);
        // first invocation consumes the available call ID
        CallIdSequenceWithBackpressureTest.nextCallId(sequence, false);
        long oldLastCallId = sequence.getLastCallId();
        try {
            sequence.next();
            Assert.fail();
        } catch (HazelcastOverloadException e) {
            // expected
        }
        Assert.assertEquals(oldLastCallId, sequence.getLastCallId());
    }

    @Test
    public void when_overCapacityButPriorityItem_then_noBackpressure() {
        final CallIdSequenceWithBackpressure sequence = new CallIdSequenceWithBackpressure(1, 60000);
        // occupy the single call ID slot
        CallIdSequenceWithBackpressureTest.nextCallId(sequence, true);
        long oldLastCallId = sequence.getLastCallId();
        long result = CallIdSequenceWithBackpressureTest.nextCallId(sequence, true);
        Assert.assertEquals((oldLastCallId + 1), result);
        Assert.assertEquals((oldLastCallId + 1), sequence.getLastCallId());
    }

    @Test
    public void whenComplete_thenTailIncrements() {
        CallIdSequenceWithBackpressureTest.nextCallId(sequence, false);
        long oldSequence = sequence.getLastCallId();
        long oldTail = sequence.getTail();
        sequence.complete();
        Assert.assertEquals(oldSequence, sequence.getLastCallId());
        Assert.assertEquals((oldTail + 1), sequence.getTail());
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void complete_whenNoMatchingNext() {
        CallIdSequenceWithBackpressureTest.nextCallId(sequence, false);
        sequence.complete();
        sequence.complete();
    }
}

