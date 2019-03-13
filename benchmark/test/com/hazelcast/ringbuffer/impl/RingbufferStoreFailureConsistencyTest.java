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
package com.hazelcast.ringbuffer.impl;


import OverflowPolicy.FAIL;
import OverflowPolicy.OVERWRITE;
import com.google.common.collect.Lists;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

import static org.mockito.Mockito.doThrow;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferStoreFailureConsistencyTest extends HazelcastTestSupport {
    private static final String RINGBUFFER_NAME = "testRingbuffer";

    private static final String INIT_VALUE = "INIT";

    private static final String ONE = "One";

    private static final String TWO = "Two";

    private static final String THREE = "Three";

    @Mock
    private RingbufferStore<String> store;

    private Ringbuffer<String> ringbufferPrimary;

    private Ringbuffer<String> ringbufferBackup;

    private HazelcastInstance primaryInstance;

    @Test
    public void testAdd_PrimaryAndBackupIsConsistentAfterStoreFailure() throws Exception {
        long seqInit = ringbufferPrimary.add(RingbufferStoreFailureConsistencyTest.INIT_VALUE);
        long seqTwo = seqInit;
        doThrow(new IllegalStateException("Expected test exception")).when(store).store((seqInit + 2), RingbufferStoreFailureConsistencyTest.TWO);
        long seqOne = ringbufferPrimary.add(RingbufferStoreFailureConsistencyTest.ONE);
        try {
            seqTwo = ringbufferPrimary.add(RingbufferStoreFailureConsistencyTest.TWO);
        } catch (HazelcastException expected) {
            // do nothing
        }
        long seqThree = ringbufferPrimary.add(RingbufferStoreFailureConsistencyTest.THREE);
        verifySecondItemWasNotAdded(ringbufferPrimary, seqOne, seqTwo, seqThree);
        terminatePrimary();
        verifySecondItemWasNotAdded(ringbufferBackup, seqOne, seqTwo, seqThree);
    }

    @Test
    public void testAddAsync_PrimaryAndBackupIsConsistentAfterStoreFailure() throws Exception {
        long seqInit = ringbufferPrimary.add(RingbufferStoreFailureConsistencyTest.INIT_VALUE);
        long seqTwo = seqInit;
        doThrow(new IllegalStateException("Expected test exception")).when(store).store((seqInit + 2), RingbufferStoreFailureConsistencyTest.TWO);
        ICompletableFuture<Long> seqOneFuture = ringbufferPrimary.addAsync(RingbufferStoreFailureConsistencyTest.ONE, OVERWRITE);
        ICompletableFuture<Long> seqTwoFuture = ringbufferPrimary.addAsync(RingbufferStoreFailureConsistencyTest.TWO, OVERWRITE);
        ICompletableFuture<Long> seqThreeFuture = ringbufferPrimary.addAsync(RingbufferStoreFailureConsistencyTest.THREE, OVERWRITE);
        long seqOne = seqOneFuture.get();
        try {
            seqTwo = seqTwoFuture.get();
        } catch (ExecutionException expected) {
            // do nothing
        }
        long seqThree = seqThreeFuture.get();
        verifySecondItemWasNotAdded(ringbufferPrimary, seqOne, seqTwo, seqThree);
        terminatePrimary();
        verifySecondItemWasNotAdded(ringbufferBackup, seqOne, seqTwo, seqThree);
    }

    @Test
    public void testAddAllAsync_PrimaryAndBackupIsConsistentAfterStoreFailure() throws Exception {
        long primaryTailSequenceBeforeAddingAll = ringbufferPrimary.tailSequence();
        long seqFirstItem = (ringbufferPrimary.tailSequence()) + 1;
        doThrow(new IllegalStateException("Expected test exception")).when(store).storeAll(ArgumentMatchers.eq(seqFirstItem), ((String[]) (ArgumentMatchers.any(Object[].class))));
        ICompletableFuture<Long> result = ringbufferPrimary.addAllAsync(Lists.newArrayList(RingbufferStoreFailureConsistencyTest.ONE, RingbufferStoreFailureConsistencyTest.TWO, RingbufferStoreFailureConsistencyTest.THREE), FAIL);
        try {
            result.get();
        } catch (ExecutionException expected) {
            // do nothing
        }
        long primarySequenceAfterAddingAll = ringbufferPrimary.tailSequence();
        Assert.assertEquals(primaryTailSequenceBeforeAddingAll, primarySequenceAfterAddingAll);
        terminatePrimary();
        Assert.assertEquals(primarySequenceAfterAddingAll, ringbufferBackup.tailSequence());
    }
}

