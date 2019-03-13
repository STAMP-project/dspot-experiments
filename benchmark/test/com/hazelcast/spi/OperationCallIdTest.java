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
package com.hazelcast.spi;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationCallIdTest {
    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    private final Operation op = new OperationCallIdTest.MockOperation();

    @Test
    public void when_callIdNotSet_thenIsZero() {
        Assert.assertEquals(0, op.getCallId());
    }

    @Test
    public void when_setCallIdInitial_thenActive() {
        op.setCallId(1);
        Assert.assertEquals(1, op.getCallId());
        Assert.assertTrue(op.isActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_setZeroCallId_thenFail() {
        op.setCallId(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_setNegativeCallId_thenFail() {
        op.setCallId((-1));
    }

    @Test
    public void when_setCallIdOnActiveOp_thenFail() {
        // Given
        op.setCallId(1);
        Assert.assertTrue(op.isActive());
        // Then
        exceptionRule.expect(IllegalStateException.class);
        // When
        op.setCallId(1);
    }

    @Test
    public void when_deactivateActiveOp_thenInactive() {
        // Given
        op.setCallId(1);
        Assert.assertTrue(op.isActive());
        // When
        op.deactivate();
        // Then
        Assert.assertFalse(op.isActive());
    }

    @Test
    public void when_deactivateInitialStateOp_thenNothingHappens() {
        // Given
        Assert.assertFalse(op.isActive());
        // When
        op.deactivate();
        // Then
        Assert.assertFalse(op.isActive());
    }

    @Test
    public void when_deactivateDeactivatedOp_thenNothingHappens() {
        // Given
        op.setCallId(1);
        op.deactivate();
        Assert.assertFalse(op.isActive());
        // When
        op.deactivate();
        // Then
        Assert.assertFalse(op.isActive());
    }

    @Test
    public void when_getCallIdOnDeactivatedOp_thenCallIdPreserved() {
        // Given
        final int mockCallId = 1;
        op.setCallId(mockCallId);
        op.deactivate();
        // When
        long callId = op.getCallId();
        // Then
        Assert.assertEquals(callId, mockCallId);
    }

    @Test
    public void when_setCallIdOnDeactivatedOp_thenReactivated() {
        // Given
        op.setCallId(1);
        op.deactivate();
        // When
        int newCallId = 2;
        op.setCallId(newCallId);
        // Then
        Assert.assertTrue(op.isActive());
        Assert.assertEquals(newCallId, op.getCallId());
    }

    @Test
    public void when_concurrentlySetCallId_thenOnlyOneSucceeds() {
        new OperationCallIdTest.ConcurrentExcerciser().run();
    }

    class MockOperation extends Operation {
        @Override
        public void run() throws Exception {
        }
    }

    class ConcurrentExcerciser {
        final Operation op = new OperationCallIdTest.MockOperation();

        volatile AssertionFailedError testFailure;

        volatile int activationCount;

        void run() {
            final Thread reader = new Thread() {
                @Override
                public void run() {
                    try {
                        long previousCallId = 0;
                        while (!(Thread.interrupted())) {
                            long callId;
                            while ((callId = op.getCallId()) == previousCallId) {
                                // Wait until a writer thread sets a call ID
                            } 
                            (activationCount)++;
                            long deadline = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(1));
                            while ((System.currentTimeMillis()) < deadline) {
                                Assert.assertEquals(callId, op.getCallId());
                            } 
                            op.deactivate();
                            previousCallId = callId;
                        } 
                    } catch (AssertionFailedError e) {
                        testFailure = e;
                    }
                }
            };
            final int writerCount = 4;
            final Thread[] writers = new Thread[writerCount];
            for (int i = 0; i < (writers.length); i++) {
                final int initialCallId = i + 1;
                writers[i] = new Thread() {
                    @Override
                    public void run() {
                        long nextCallId = initialCallId;
                        while (!(Thread.interrupted())) {
                            try {
                                op.setCallId(nextCallId);
                                nextCallId += writerCount;
                            } catch (IllegalStateException e) {
                                // Things are working as expected
                            }
                        } 
                    }
                };
            }
            try {
                reader.start();
                for (Thread t : writers) {
                    t.start();
                }
                final long deadline = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(5));
                while ((System.currentTimeMillis()) < deadline) {
                    if ((testFailure) != null) {
                        throw testFailure;
                    }
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
                } 
            } finally {
                reader.interrupt();
                for (Thread t : writers) {
                    t.interrupt();
                }
            }
            Assert.assertTrue("Failed to activate the operation at least twice", ((activationCount) >= 2));
        }
    }
}

