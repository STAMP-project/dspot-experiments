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
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LoadTrackerTest {
    private NioThread owner1;

    private NioThread owner2;

    private NioThread[] owner3;

    private LoadTracker loadTracker;

    @Test
    public void testUpdateImbalance() throws Exception {
        MigratablePipeline owner1Pipeline1 = Mockito.mock(MigratablePipeline.class);
        Mockito.when(owner1Pipeline1.load()).thenReturn(0L).thenReturn(100L);
        Mockito.when(owner1Pipeline1.owner()).thenReturn(owner1);
        loadTracker.addPipeline(owner1Pipeline1);
        MigratablePipeline owner2Pipeline1 = Mockito.mock(MigratablePipeline.class);
        Mockito.when(owner2Pipeline1.load()).thenReturn(0L).thenReturn(200L);
        Mockito.when(owner2Pipeline1.owner()).thenReturn(owner2);
        loadTracker.addPipeline(owner2Pipeline1);
        MigratablePipeline owner2Pipeline3 = Mockito.mock(MigratablePipeline.class);
        Mockito.when(owner2Pipeline3.load()).thenReturn(0L).thenReturn(100L);
        Mockito.when(owner2Pipeline3.owner()).thenReturn(owner2);
        loadTracker.addPipeline(owner2Pipeline3);
        LoadImbalance loadImbalance = loadTracker.updateImbalance();
        Assert.assertEquals(0, loadImbalance.minimumLoad);
        Assert.assertEquals(0, loadImbalance.maximumLoad);
        loadTracker.updateImbalance();
        Assert.assertEquals(100, loadImbalance.minimumLoad);
        Assert.assertEquals(300, loadImbalance.maximumLoad);
        Assert.assertEquals(owner1, loadImbalance.dstOwner);
        Assert.assertEquals(owner2, loadImbalance.srcOwner);
    }

    // there is no point in selecting a selector with a single handler as source.
    @Test
    public void testUpdateImbalance_notUsingSinglePipelineOwnerAsSource() throws Exception {
        MigratablePipeline owmer1Pipeline1 = Mockito.mock(MigratablePipeline.class);
        // the first selector has a handler with a large number of events
        Mockito.when(owmer1Pipeline1.load()).thenReturn(10000L);
        Mockito.when(owmer1Pipeline1.owner()).thenReturn(owner1);
        loadTracker.addPipeline(owmer1Pipeline1);
        MigratablePipeline owner2Pipeline = Mockito.mock(MigratablePipeline.class);
        Mockito.when(owner2Pipeline.load()).thenReturn(200L);
        Mockito.when(owner2Pipeline.owner()).thenReturn(owner2);
        loadTracker.addPipeline(owner2Pipeline);
        MigratablePipeline owner2Pipeline2 = Mockito.mock(MigratablePipeline.class);
        Mockito.when(owner2Pipeline2.load()).thenReturn(200L);
        Mockito.when(owner2Pipeline2.owner()).thenReturn(owner2);
        loadTracker.addPipeline(owner2Pipeline2);
        LoadImbalance loadImbalance = loadTracker.updateImbalance();
        Assert.assertEquals(400, loadImbalance.minimumLoad);
        Assert.assertEquals(400, loadImbalance.maximumLoad);
        Assert.assertEquals(owner2, loadImbalance.dstOwner);
        Assert.assertEquals(owner2, loadImbalance.srcOwner);
    }
}

