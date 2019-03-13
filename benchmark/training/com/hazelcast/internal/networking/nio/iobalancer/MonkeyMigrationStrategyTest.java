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
public class MonkeyMigrationStrategyTest extends HazelcastTestSupport {
    private MigrationStrategy strategy;

    private Map<NioThread, Set<MigratablePipeline>> ownerPipelines;

    private ItemCounter<MigratablePipeline> pipelineLoadCounter;

    private LoadImbalance imbalance;

    @Test
    public void imbalanceDetected_shouldReturnFalseWhenNoPipelineExist() {
        ownerPipelines.put(imbalance.srcOwner, Collections.<MigratablePipeline>emptySet());
        boolean imbalanceDetected = strategy.imbalanceDetected(imbalance);
        Assert.assertFalse(imbalanceDetected);
    }

    @Test
    public void imbalanceDetected_shouldReturnTrueWhenPipelineExist() {
        MigratablePipeline pipeline = Mockito.mock(MigratablePipeline.class);
        ownerPipelines.put(imbalance.srcOwner, TestCollectionUtils.setOf(pipeline));
        boolean imbalanceDetected = strategy.imbalanceDetected(imbalance);
        Assert.assertTrue(imbalanceDetected);
    }

    @Test
    public void findPipelineToMigrate_shouldWorkEvenWithASinglePipelineAvailable() {
        MigratablePipeline pipeline = Mockito.mock(MigratablePipeline.class);
        ownerPipelines.put(imbalance.srcOwner, TestCollectionUtils.setOf(pipeline));
        MigratablePipeline pipelineToMigrate = strategy.findPipelineToMigrate(imbalance);
        Assert.assertEquals(pipeline, pipelineToMigrate);
    }

    @Test
    public void findPipelineToMigrate_shouldBeFair() {
        int iterationCount = 10000;
        double toleranceFactor = 0.25;
        MigratablePipeline pipeline1 = Mockito.mock(MigratablePipeline.class);
        MigratablePipeline pipeline2 = Mockito.mock(MigratablePipeline.class);
        ownerPipelines.put(imbalance.srcOwner, TestCollectionUtils.setOf(pipeline1, pipeline2));
        assertFairSelection(iterationCount, toleranceFactor, pipeline1, pipeline2);
    }
}

