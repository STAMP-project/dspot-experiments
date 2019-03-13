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
package org.apache.geode.internal.offheap;


import InternalLocator.FORCE_LOCATOR_DM_TYPE;
import MemoryAllocatorImpl.FREE_OFF_HEAP_MEMORY_PROPERTY;
import OffHeapStorage.MIN_SLAB_SIZE;
import org.apache.geode.OutOfOffHeapMemoryException;
import org.apache.geode.StatisticsFactory;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.DistributionStats;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.statistics.LocalStatisticsFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.mockito.Mockito;

import static OffHeapStorage.MIN_SLAB_SIZE;


public class OffHeapStorageJUnitTest {
    private static final long MEGABYTE = 1024 * 1024;

    private static final long GIGABYTE = (1024 * 1024) * 1024;

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void testParseOffHeapMemorySizeNegative() {
        Assert.assertEquals(0, OffHeapStorage.parseOffHeapMemorySize("-1"));
    }

    @Test
    public void testParseOffHeapMemorySizeNull() {
        Assert.assertEquals(0, OffHeapStorage.parseOffHeapMemorySize(null));
    }

    @Test
    public void testParseOffHeapMemorySizeEmpty() {
        Assert.assertEquals(0, OffHeapStorage.parseOffHeapMemorySize(""));
    }

    @Test
    public void testParseOffHeapMemorySizeBytes() {
        Assert.assertEquals(OffHeapStorageJUnitTest.MEGABYTE, OffHeapStorage.parseOffHeapMemorySize("1"));
        Assert.assertEquals(((Integer.MAX_VALUE) * (OffHeapStorageJUnitTest.MEGABYTE)), OffHeapStorage.parseOffHeapMemorySize(("" + (Integer.MAX_VALUE))));
    }

    @Test
    public void testParseOffHeapMemorySizeKiloBytes() {
        try {
            OffHeapStorage.parseOffHeapMemorySize("1k");
            Assert.fail("Did not receive expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
    }

    @Test
    public void testParseOffHeapMemorySizeMegaBytes() {
        Assert.assertEquals(OffHeapStorageJUnitTest.MEGABYTE, OffHeapStorage.parseOffHeapMemorySize("1m"));
        Assert.assertEquals(((Integer.MAX_VALUE) * (OffHeapStorageJUnitTest.MEGABYTE)), OffHeapStorage.parseOffHeapMemorySize((("" + (Integer.MAX_VALUE)) + "m")));
    }

    @Test
    public void testParseOffHeapMemorySizeGigaBytes() {
        Assert.assertEquals(OffHeapStorageJUnitTest.GIGABYTE, OffHeapStorage.parseOffHeapMemorySize("1g"));
        Assert.assertEquals(((Integer.MAX_VALUE) * (OffHeapStorageJUnitTest.GIGABYTE)), OffHeapStorage.parseOffHeapMemorySize((("" + (Integer.MAX_VALUE)) + "g")));
    }

    @Test
    public void testCalcMaxSlabSize() {
        Assert.assertEquals(100, OffHeapStorage.calcMaxSlabSize(100L));
        Assert.assertEquals(Integer.MAX_VALUE, OffHeapStorage.calcMaxSlabSize(Long.MAX_VALUE));
        try {
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "99");
            Assert.assertEquals(((99 * 1024) * 1024), OffHeapStorage.calcMaxSlabSize(((100L * 1024) * 1024)));
            Assert.assertEquals(88, OffHeapStorage.calcMaxSlabSize(88));
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "88m");
            Assert.assertEquals(((88 * 1024) * 1024), OffHeapStorage.calcMaxSlabSize(((100L * 1024) * 1024)));
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "77M");
            Assert.assertEquals(((77 * 1024) * 1024), OffHeapStorage.calcMaxSlabSize(((100L * 1024) * 1024)));
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "1g");
            Assert.assertEquals((((1 * 1024) * 1024) * 1024), OffHeapStorage.calcMaxSlabSize((((2L * 1024) * 1024) * 1024)));
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "1G");
            Assert.assertEquals((((1L * 1024) * 1024) * 1024), OffHeapStorage.calcMaxSlabSize(((((2L * 1024) * 1024) * 1024) + 1)));
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "foobarG");
            try {
                OffHeapStorage.calcMaxSlabSize(100);
                Assert.fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            System.setProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"), "");
            Assert.assertEquals(100, OffHeapStorage.calcMaxSlabSize(100L));
            Assert.assertEquals(Integer.MAX_VALUE, OffHeapStorage.calcMaxSlabSize(Long.MAX_VALUE));
        } finally {
            System.clearProperty(((DistributionConfig.GEMFIRE_PREFIX) + "OFF_HEAP_SLAB_SIZE"));
        }
    }

    @Test
    public void createOffHeapStorageReturnsNullIfForceLocator() {
        System.setProperty(FORCE_LOCATOR_DM_TYPE, "true");
        Assert.assertEquals(null, OffHeapStorage.createOffHeapStorage(null, 1, null));
    }

    @Test
    public void createOffHeapStorageReturnsNullIfMemorySizeIsZero() {
        Assert.assertEquals(null, OffHeapStorage.createOffHeapStorage(null, 0, null));
    }

    @Test
    public void exceptionIfSlabCountTooSmall() {
        StatisticsFactory statsFactory = Mockito.mock(StatisticsFactory.class);
        try {
            OffHeapStorage.createOffHeapStorage(statsFactory, ((MIN_SLAB_SIZE) - 1), null);
        } catch (IllegalArgumentException expected) {
            expected.getMessage().equals(((("The amount of off heap memory must be at least " + (MIN_SLAB_SIZE)) + " but it was set to ") + ((MIN_SLAB_SIZE) - 1)));
        }
    }

    @Test
    public void exceptionIfDistributedSystemNull() {
        StatisticsFactory statsFactory = Mockito.mock(StatisticsFactory.class);
        try {
            OffHeapStorage.createOffHeapStorage(statsFactory, MIN_SLAB_SIZE, ((DistributedSystem) (null)));
        } catch (IllegalArgumentException expected) {
            expected.getMessage().equals("InternalDistributedSystem is null");
        }
    }

    @Test
    public void createOffHeapStorageWorks() {
        StatisticsFactory localStatsFactory = new LocalStatisticsFactory(null);
        InternalDistributedSystem ids = Mockito.mock(InternalDistributedSystem.class);
        MemoryAllocator ma = OffHeapStorage.createOffHeapStorage(localStatsFactory, MIN_SLAB_SIZE, ids);
        System.setProperty(FREE_OFF_HEAP_MEMORY_PROPERTY, "true");
        ma.close();
    }

    @Test
    public void testCreateOffHeapStorage() {
        StatisticsFactory localStatsFactory = new LocalStatisticsFactory(null);
        OutOfOffHeapMemoryListener ooohml = Mockito.mock(OutOfOffHeapMemoryListener.class);
        MemoryAllocator ma = OffHeapStorage.basicCreateOffHeapStorage(localStatsFactory, (1024 * 1024), ooohml);
        try {
            OffHeapMemoryStats stats = ma.getStats();
            Assert.assertNotNull(stats.getStats());
            Assert.assertEquals((1024 * 1024), stats.getFreeMemory());
            Assert.assertEquals((1024 * 1024), stats.getMaxMemory());
            Assert.assertEquals(0, stats.getUsedMemory());
            Assert.assertEquals(0, stats.getDefragmentations());
            Assert.assertEquals(0, stats.getDefragmentationsInProgress());
            Assert.assertEquals(0, stats.getDefragmentationTime());
            Assert.assertEquals(0, stats.getFragmentation());
            Assert.assertEquals(1, stats.getFragments());
            Assert.assertEquals((1024 * 1024), stats.getLargestFragment());
            Assert.assertEquals(0, stats.getObjects());
            Assert.assertEquals(0, stats.getReads());
            stats.incFreeMemory(100);
            Assert.assertEquals(((1024 * 1024) + 100), stats.getFreeMemory());
            stats.incFreeMemory((-100));
            Assert.assertEquals((1024 * 1024), stats.getFreeMemory());
            stats.incMaxMemory(100);
            Assert.assertEquals(((1024 * 1024) + 100), stats.getMaxMemory());
            stats.incMaxMemory((-100));
            Assert.assertEquals((1024 * 1024), stats.getMaxMemory());
            stats.incUsedMemory(100);
            Assert.assertEquals(100, stats.getUsedMemory());
            stats.incUsedMemory((-100));
            Assert.assertEquals(0, stats.getUsedMemory());
            stats.incObjects(100);
            Assert.assertEquals(100, stats.getObjects());
            stats.incObjects((-100));
            Assert.assertEquals(0, stats.getObjects());
            stats.incReads();
            Assert.assertEquals(1, stats.getReads());
            stats.setFragmentation(100);
            Assert.assertEquals(100, stats.getFragmentation());
            stats.setFragmentation(0);
            Assert.assertEquals(0, stats.getFragmentation());
            stats.setFragments(2);
            Assert.assertEquals(2, stats.getFragments());
            stats.setFragments(1);
            Assert.assertEquals(1, stats.getFragments());
            stats.setLargestFragment(100);
            Assert.assertEquals(100, stats.getLargestFragment());
            stats.setLargestFragment((1024 * 1024));
            Assert.assertEquals((1024 * 1024), stats.getLargestFragment());
            boolean originalEnableClockStats = DistributionStats.enableClockStats;
            DistributionStats.enableClockStats = true;
            try {
                long start = stats.startDefragmentation();
                Assert.assertEquals(1, stats.getDefragmentationsInProgress());
                while ((DistributionStats.getStatTime()) == start) {
                    Thread.yield();
                } 
                stats.endDefragmentation(start);
                Assert.assertEquals(1, stats.getDefragmentations());
                Assert.assertEquals(0, stats.getDefragmentationsInProgress());
                Assert.assertTrue(((stats.getDefragmentationTime()) > 0));
            } finally {
                DistributionStats.enableClockStats = originalEnableClockStats;
            }
            stats.incObjects(100);
            stats.incUsedMemory(100);
            stats.setFragmentation(100);
            OffHeapStorage ohs = ((OffHeapStorage) (stats));
            ohs.initialize(new NullOffHeapMemoryStats());
            Assert.assertEquals(0, stats.getFreeMemory());
            Assert.assertEquals(0, stats.getMaxMemory());
            Assert.assertEquals(0, stats.getUsedMemory());
            Assert.assertEquals(0, stats.getDefragmentations());
            Assert.assertEquals(0, stats.getDefragmentationsInProgress());
            Assert.assertEquals(0, stats.getDefragmentationTime());
            Assert.assertEquals(0, stats.getFragmentation());
            Assert.assertEquals(0, stats.getFragments());
            Assert.assertEquals(0, stats.getLargestFragment());
            Assert.assertEquals(0, stats.getObjects());
            Assert.assertEquals(0, stats.getReads());
            OutOfOffHeapMemoryException ex = null;
            try {
                ma.allocate(((1024 * 1024) + 1));
                Assert.fail("expected OutOfOffHeapMemoryException");
            } catch (OutOfOffHeapMemoryException expected) {
                ex = expected;
            }
            Mockito.verify(ooohml).outOfOffHeapMemory(ex);
            try {
                ma.allocate(((1024 * 1024) + 1));
                Assert.fail("expected OutOfOffHeapMemoryException");
            } catch (OutOfOffHeapMemoryException expected) {
                ex = expected;
            }
            Mockito.verify(ooohml).outOfOffHeapMemory(ex);
        } finally {
            System.setProperty(FREE_OFF_HEAP_MEMORY_PROPERTY, "true");
            try {
                ma.close();
            } finally {
                System.clearProperty(FREE_OFF_HEAP_MEMORY_PROPERTY);
            }
        }
    }

    @Test
    public void testCalcSlabCount() {
        final long MSS = MIN_SLAB_SIZE;
        Assert.assertEquals(100, OffHeapStorage.calcSlabCount((MSS * 4), ((MSS * 4) * 100)));
        Assert.assertEquals(100, OffHeapStorage.calcSlabCount((MSS * 4), (((MSS * 4) * 100) + (MSS - 1))));
        Assert.assertEquals(101, OffHeapStorage.calcSlabCount((MSS * 4), (((MSS * 4) * 100) + MSS)));
        Assert.assertEquals(Integer.MAX_VALUE, OffHeapStorage.calcSlabCount(MSS, (MSS * (Integer.MAX_VALUE))));
        Assert.assertEquals(Integer.MAX_VALUE, OffHeapStorage.calcSlabCount(MSS, (((MSS * (Integer.MAX_VALUE)) + MSS) - 1)));
        try {
            OffHeapStorage.calcSlabCount(MSS, ((((long) (MSS)) * (Integer.MAX_VALUE)) + MSS));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }
}

