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


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link PipelinedSubpartition}.
 *
 * @see PipelinedSubpartitionWithReadViewTest
 */
public class PipelinedSubpartitionTest extends SubpartitionTestBase {
    /**
     * Executor service for concurrent produce/consume tests.
     */
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void testIllegalReadViewRequest() throws Exception {
        final PipelinedSubpartition subpartition = createSubpartition();
        // Successful request
        Assert.assertNotNull(subpartition.createReadView(new NoOpBufferAvailablityListener()));
        try {
            subpartition.createReadView(new NoOpBufferAvailablityListener());
            Assert.fail("Did not throw expected exception after duplicate notifyNonEmpty view request.");
        } catch (IllegalStateException expected) {
        }
    }

    /**
     * Verifies that the isReleased() check of the view checks the parent
     * subpartition.
     */
    @Test
    public void testIsReleasedChecksParent() {
        PipelinedSubpartition subpartition = Mockito.mock(PipelinedSubpartition.class);
        PipelinedSubpartitionView reader = new PipelinedSubpartitionView(subpartition, Mockito.mock(BufferAvailabilityListener.class));
        Assert.assertFalse(reader.isReleased());
        Mockito.verify(subpartition, Mockito.times(1)).isReleased();
        Mockito.when(subpartition.isReleased()).thenReturn(true);
        Assert.assertTrue(reader.isReleased());
        Mockito.verify(subpartition, Mockito.times(2)).isReleased();
    }

    @Test
    public void testConcurrentFastProduceAndFastConsume() throws Exception {
        testProduceConsume(false, false);
    }

    @Test
    public void testConcurrentFastProduceAndSlowConsume() throws Exception {
        testProduceConsume(false, true);
    }

    @Test
    public void testConcurrentSlowProduceAndFastConsume() throws Exception {
        testProduceConsume(true, false);
    }

    @Test
    public void testConcurrentSlowProduceAndSlowConsume() throws Exception {
        testProduceConsume(true, true);
    }

    /**
     * Tests cleanup of {@link PipelinedSubpartition#release()} with no read view attached.
     */
    @Test
    public void testCleanupReleasedPartitionNoView() throws Exception {
        testCleanupReleasedPartition(false);
    }

    /**
     * Tests cleanup of {@link PipelinedSubpartition#release()} with a read view attached.
     */
    @Test
    public void testCleanupReleasedPartitionWithView() throws Exception {
        testCleanupReleasedPartition(true);
    }
}

