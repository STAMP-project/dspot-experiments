/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PartitionedRegionDataStoreTest {
    @Mock
    private PartitionedRegion partitionedRegion;

    @Mock
    private PartitionedRegion childRegion1;

    @Mock
    private PartitionedRegion childRegion2;

    @Mock
    private PartitionedRegion grandChildRegion1_1;

    @Mock
    private PartitionedRegion grandChildRegion1_2;

    @Mock
    private PartitionedRegion grandChildRegion2_1;

    @Mock
    private PartitionedRegion grandChildRegion2_2;

    @Mock
    private PartitionedRegion grandChildRegion2_3;

    @Mock
    private PartitionedRegionDataStore colocatedRegionDateStore;

    @Mock
    private PartitionedRegionDataStore grandChildRegionDateStore2_3;

    private final int bucketId = 29;

    @Test
    public void initializedPartitionedRegionWithoutColocationReturnsRegionReady() {
        PartitionedRegionDataStore partitionedRegionDataStore = Mockito.spy(new PartitionedRegionDataStore());
        List<PartitionedRegion> colocatedChildRegions = new ArrayList<PartitionedRegion>();
        Mockito.doReturn(colocatedChildRegions).when(partitionedRegionDataStore).getColocatedChildRegions(partitionedRegion);
        assertThat(partitionedRegionDataStore.isPartitionedRegionReady(partitionedRegion, bucketId)).isTrue();
    }

    @Test
    public void notInitializedPartitionedRegionWithoutColocationReturnsRegionNotReady() {
        PartitionedRegionDataStore partitionedRegionDataStore = Mockito.spy(new PartitionedRegionDataStore());
        List<PartitionedRegion> colocatedChildRegions = new ArrayList<PartitionedRegion>();
        Mockito.doReturn(colocatedChildRegions).when(partitionedRegionDataStore).getColocatedChildRegions(partitionedRegion);
        Mockito.when(partitionedRegion.isInitialized()).thenReturn(false);
        assertThat(partitionedRegionDataStore.isPartitionedRegionReady(partitionedRegion, bucketId)).isFalse();
    }

    @Test
    public void returnRegionReadyIfAllColocatedRegionsAreReady() {
        PartitionedRegionDataStore partitionedRegionDataStore = Mockito.spy(new PartitionedRegionDataStore());
        setupColocatedRegions(partitionedRegionDataStore);
        assertThat(partitionedRegionDataStore.isPartitionedRegionReady(partitionedRegion, bucketId)).isTrue();
    }

    @Test
    public void returnRegionNotReadyIfColocationNotCompletedForAColocatedRegion() {
        PartitionedRegionDataStore partitionedRegionDataStore = Mockito.spy(new PartitionedRegionDataStore());
        setupColocatedRegions(partitionedRegionDataStore);
        Mockito.when(grandChildRegionDateStore2_3.isColocationComplete(bucketId)).thenReturn(false);
        assertThat(partitionedRegionDataStore.isPartitionedRegionReady(partitionedRegion, bucketId)).isFalse();
    }

    @Test
    public void returnRegionNotReadyIfAColocatedRegionIsNotInitialized() {
        PartitionedRegionDataStore partitionedRegionDataStore = Mockito.spy(new PartitionedRegionDataStore());
        setupColocatedRegions(partitionedRegionDataStore);
        Mockito.when(grandChildRegion2_2.isInitialized()).thenReturn(false);
        assertThat(partitionedRegionDataStore.isPartitionedRegionReady(partitionedRegion, bucketId)).isFalse();
    }
}

