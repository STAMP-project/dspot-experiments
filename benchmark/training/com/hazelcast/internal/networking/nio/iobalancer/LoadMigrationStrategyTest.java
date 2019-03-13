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
package com.hazelcast.internal.networking.nio.iobalancer;


import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ItemCounter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LoadMigrationStrategyTest extends HazelcastTestSupport {
    private Map<NioThread, Set<MigratablePipeline>> ownerToPipelines;

    private ItemCounter<MigratablePipeline> loadCounter;

    private LoadImbalance imbalance;

    private LoadMigrationStrategy strategy;

    @Test
    public void testImbalanceDetected_shouldReturnFalseWhenNoKnownMinimum() throws Exception {
        imbalance.minimumLoad = Long.MIN_VALUE;
        boolean imbalanceDetected = strategy.imbalanceDetected(imbalance);
        Assert.assertFalse(imbalanceDetected);
    }

    @Test
    public void testImbalanceDetected_shouldReturnFalseWhenNoKnownMaximum() throws Exception {
        imbalance.maximumLoad = Long.MAX_VALUE;
        boolean imbalanceDetected = strategy.imbalanceDetected(imbalance);
        Assert.assertFalse(imbalanceDetected);
    }

    @Test
    public void testImbalanceDetected_shouldReturnFalseWhenBalanced() throws Exception {
        imbalance.maximumLoad = 1000;
        imbalance.minimumLoad = ((long) (1000 * 0.8));
        boolean imbalanceDetected = strategy.imbalanceDetected(imbalance);
        Assert.assertFalse(imbalanceDetected);
    }

    @Test
    public void testImbalanceDetected_shouldReturnTrueWhenNotBalanced() throws Exception {
        imbalance.maximumLoad = 1000;
        imbalance.minimumLoad = ((long) (1000 * 0.8)) - 1;
        boolean imbalanceDetected = strategy.imbalanceDetected(imbalance);
        Assert.assertTrue(imbalanceDetected);
    }

    @Test
    public void testFindPipelineToMigrate() throws Exception {
        NioThread srcOwner = Mockito.mock(NioThread.class);
        NioThread dstOwner = Mockito.mock(NioThread.class);
        imbalance.srcOwner = srcOwner;
        imbalance.dstOwner = dstOwner;
        imbalance.minimumLoad = 100;
        MigratablePipeline pipeline1 = Mockito.mock(MigratablePipeline.class);
        loadCounter.set(pipeline1, 100L);
        ownerToPipelines.put(dstOwner, Collections.singleton(pipeline1));
        imbalance.maximumLoad = 300;
        MigratablePipeline pipeline2 = Mockito.mock(MigratablePipeline.class);
        MigratablePipeline pipeline3 = Mockito.mock(MigratablePipeline.class);
        loadCounter.set(pipeline2, 200L);
        loadCounter.set(pipeline3, 100L);
        ownerToPipelines.put(srcOwner, TestCollectionUtils.setOf(pipeline2, pipeline3));
        MigratablePipeline pipelineToMigrate = strategy.findPipelineToMigrate(imbalance);
        Assert.assertEquals(pipeline3, pipelineToMigrate);
    }
}

