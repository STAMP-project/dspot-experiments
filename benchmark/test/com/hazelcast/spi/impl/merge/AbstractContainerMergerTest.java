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
package com.hazelcast.spi.impl.merge;


import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.spi.impl.merge.TestMergeOperation.OperationMode.BLOCKS;
import static com.hazelcast.spi.impl.merge.TestMergeOperation.OperationMode.THROWS_EXCEPTION;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractContainerMergerTest extends HazelcastTestSupport {
    private NodeEngineImpl nodeEngine;

    private TestContainerCollector collector;

    private TestContainerCollector emptyCollector;

    /**
     * Tests that the merger finished under normal conditions.
     */
    @Test
    @RequireAssertEnabled
    public void testMergerRun() {
        TestMergeOperation operation = new TestMergeOperation();
        TestContainerMerger merger = new TestContainerMerger(collector, nodeEngine, operation);
        run();
        Assert.assertTrue("Expected the merge operation to be invoked", operation.hasBeenInvoked);
        Assert.assertTrue("Expected collected containers to be destroyed", collector.onDestroyHasBeenCalled);
    }

    /**
     * Tests that the merger finishes, even if the merge operation throws an exception.
     */
    @Test
    @RequireAssertEnabled
    public void testMergerRun_whenMergeOperationThrowsException_thenMergerFinishesNormally() {
        TestMergeOperation operation = new TestMergeOperation(THROWS_EXCEPTION);
        TestContainerMerger merger = new TestContainerMerger(collector, nodeEngine, operation);
        run();
        Assert.assertTrue("Expected the merge operation to be invoked", operation.hasBeenInvoked);
        Assert.assertTrue("Expected collected containers to be destroyed", collector.onDestroyHasBeenCalled);
    }

    /**
     * Tests that the merger finishes eventually, when the merge operation blocks.
     */
    @Test
    @RequireAssertEnabled
    @Category(SlowTest.class)
    public void testMergerRun_whenMergeOperationBlocks_thenMergerFinishesEventually() {
        TestMergeOperation operation = new TestMergeOperation(BLOCKS);
        TestContainerMerger merger = new TestContainerMerger(collector, nodeEngine, operation);
        run();
        operation.unblock();
        Assert.assertTrue("Expected the merge operation to be invoked", operation.hasBeenInvoked);
        Assert.assertTrue("Expected collected containers to be destroyed", collector.onDestroyHasBeenCalled);
    }

    /**
     * Tests that the merger finishes, when it's interrupted.
     */
    @Test
    @RequireAssertEnabled
    public void testMergerRun_whenMergerIsInterrupted_thenMergerFinishesEventually() {
        TestMergeOperation operation = new TestMergeOperation(BLOCKS);
        final TestContainerMerger merger = new TestContainerMerger(collector, nodeEngine, operation);
        Thread thread = new Thread() {
            @Override
            public void run() {
                run();
            }
        };
        thread.start();
        thread.interrupt();
        HazelcastTestSupport.assertJoinable(thread);
        operation.unblock();
        // we cannot assert if the operation has been invoked, since the interruption could be faster
        Assert.assertTrue("Expected collected containers to be destroyed", collector.onDestroyHasBeenCalled);
    }

    /**
     * Tests that the merger finishes without invoking merge operations, if no containers have been collected.
     */
    @Test
    @RequireAssertEnabled
    public void testMergerRun_whenEmptyCollector_thenMergerDoesNotRun() {
        TestMergeOperation operation = new TestMergeOperation();
        TestContainerMerger merger = new TestContainerMerger(emptyCollector, nodeEngine, operation);
        run();
        Assert.assertFalse("Expected the merge operation not to be invoked", operation.hasBeenInvoked);
        Assert.assertFalse("Expected collected containers not to be destroyed", collector.onDestroyHasBeenCalled);
    }

    /**
     * Tests that an assertion is triggered, if the merger implementation doesn't call
     * {@link AbstractContainerMerger#invoke(String, Operation, int)}, when there are
     * collected containers.
     */
    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testMergerRun_whenMissingOperationInvocation_thenMergerThrowsAssertion() {
        TestContainerMerger merger = new TestContainerMerger(collector, nodeEngine, null);
        run();
    }
}

