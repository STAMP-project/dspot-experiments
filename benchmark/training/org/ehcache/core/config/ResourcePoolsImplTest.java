/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.core.config;


import java.util.Collection;
import java.util.Collections;
import org.ehcache.config.ResourcePool;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


/**
 *
 *
 * @author cdennis
 */
public class ResourcePoolsImplTest {
    private static class ArbitraryType implements ResourceType<SizedResourcePool> {
        private final int tierHeight;

        public ArbitraryType(int tierHeight) {
            this.tierHeight = tierHeight;
        }

        @Override
        public Class<SizedResourcePool> getResourcePoolClass() {
            return SizedResourcePool.class;
        }

        @Override
        public boolean isPersistable() {
            return false;
        }

        @Override
        public boolean requiresSerialization() {
            return false;
        }

        @Override
        public int getTierHeight() {
            return tierHeight;
        }

        @Override
        public String toString() {
            return "arbitrary";
        }
    }

    @Test
    public void testMismatchedUnits() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, Integer.MAX_VALUE, EntryUnit.ENTRIES, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        ResourcePoolsImpl.validateResourcePools(pools);
    }

    @Test
    public void testMatchingEqualUnitsWellTiered() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 9, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        ResourcePoolsImpl.validateResourcePools(pools);
    }

    @Test
    public void testMatchingUnequalUnitsWellTiered() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 9, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10240, MemoryUnit.KB, false));
        ResourcePoolsImpl.validateResourcePools(pools);
    }

    @Test
    public void testArbitraryPoolWellTieredHeap() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 9, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(new ResourcePoolsImplTest.ArbitraryType(((HEAP.getTierHeight()) - 1)), 10, MemoryUnit.MB, false));
        ResourcePoolsImpl.validateResourcePools(pools);
    }

    @Test
    public void testArbitraryPoolWellTieredOffHeap() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(new ResourcePoolsImplTest.ArbitraryType(((OFFHEAP.getTierHeight()) + 1)), 9, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        ResourcePoolsImpl.validateResourcePools(pools);
    }

    @Test
    public void testArbitraryPoolInversionHeap() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 10, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(new ResourcePoolsImplTest.ArbitraryType(((HEAP.getTierHeight()) - 1)), 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {10 MB heap}' is not smaller than 'Pool {10 MB arbitrary}'"));
        }
    }

    @Test
    public void testArbitraryPoolInversionOffHeap() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(new ResourcePoolsImplTest.ArbitraryType(((OFFHEAP.getTierHeight()) + 1)), 10, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {10 MB arbitrary}' is not smaller than 'Pool {10 MB offheap}'"));
        }
    }

    @Test
    public void testArbitraryPoolAmbiguity() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(new ResourcePoolsImplTest.ArbitraryType(OFFHEAP.getTierHeight()), 10, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Ambiguity: 'Pool {10 MB arbitrary}' has the same tier height as 'Pool {10 MB offheap}'"));
        }
    }

    @Test
    public void testEntryResourceMatch() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 10, EntryUnit.ENTRIES, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, EntryUnit.ENTRIES, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {10 entries heap}' is not smaller than 'Pool {10 entries offheap}'"));
        }
    }

    @Test
    public void testEntryResourceInversion() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 11, EntryUnit.ENTRIES, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, EntryUnit.ENTRIES, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {11 entries heap}' is not smaller than 'Pool {10 entries offheap}'"));
        }
    }

    @Test
    public void testMemoryResourceEqualUnitMatch() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 10, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {10 MB heap}' is not smaller than 'Pool {10 MB offheap}'"));
        }
    }

    @Test
    public void testMemoryResourceEqualUnitInversion() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 11, MemoryUnit.MB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {11 MB heap}' is not smaller than 'Pool {10 MB offheap}'"));
        }
    }

    @Test
    public void testMemoryResourceUnequalUnitMatch() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 10240, MemoryUnit.KB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {10240 kB heap}' is not smaller than 'Pool {10 MB offheap}'"));
        }
    }

    @Test
    public void testMemoryResourceUnequalUnitInversion() {
        Collection<SizedResourcePoolImpl<SizedResourcePool>> pools = asList(new SizedResourcePoolImpl<>(HEAP, 10241, MemoryUnit.KB, false), new SizedResourcePoolImpl<>(OFFHEAP, 10, MemoryUnit.MB, false));
        try {
            ResourcePoolsImpl.validateResourcePools(pools);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Is.is("Tiering Inversion: 'Pool {10241 kB heap}' is not smaller than 'Pool {10 MB offheap}'"));
        }
    }

    @Test
    public void testAddingNewTierWhileUpdating() {
        ResourcePools existing = new ResourcePoolsImpl(Collections.<ResourceType<?>, ResourcePool>singletonMap(HEAP, new SizedResourcePoolImpl<>(HEAP, 10L, EntryUnit.ENTRIES, false)));
        ResourcePools toBeUpdated = new ResourcePoolsImpl(Collections.<ResourceType<?>, ResourcePool>singletonMap(DISK, new SizedResourcePoolImpl<>(DISK, 10L, MemoryUnit.MB, false)));
        try {
            existing.validateAndMerge(toBeUpdated);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            Assert.assertThat(iae.getMessage(), Matchers.is("Pools to be updated cannot contain previously undefined resources pools"));
        }
    }

    @Test
    public void testUpdatingOffHeap() {
        ResourcePools existing = ResourcePoolsHelper.createOffheapOnlyPools(10);
        ResourcePools toBeUpdated = ResourcePoolsHelper.createOffheapOnlyPools(50);
        try {
            existing.validateAndMerge(toBeUpdated);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            Assert.assertThat(uoe.getMessage(), Matchers.is("Updating OFFHEAP resource is not supported"));
        }
    }

    @Test
    public void testUpdatingDisk() {
        ResourcePools existing = ResourcePoolsHelper.createDiskOnlyPools(10, MemoryUnit.MB);
        ResourcePools toBeUpdated = ResourcePoolsHelper.createDiskOnlyPools(50, MemoryUnit.MB);
        try {
            existing.validateAndMerge(toBeUpdated);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            Assert.assertThat(uoe.getMessage(), Matchers.is("Updating DISK resource is not supported"));
        }
    }

    @Test
    public void testUpdateResourceUnitSuccess() {
        ResourcePools existing = ResourcePoolsHelper.createHeapDiskPools(200, MemoryUnit.MB, 4096);
        ResourcePools toBeUpdated = ResourcePoolsHelper.createHeapOnlyPools(2, MemoryUnit.GB);
        existing = existing.validateAndMerge(toBeUpdated);
        Assert.assertThat(existing.getPoolForResource(HEAP).getSize(), Matchers.is(2L));
        Assert.assertThat(existing.getPoolForResource(HEAP).getUnit(), Matchers.<ResourceUnit>is(MemoryUnit.GB));
    }

    @Test
    public void testUpdateResourceUnitFailure() {
        ResourcePools existing = ResourcePoolsHelper.createHeapDiskPools(20, MemoryUnit.MB, 200);
        ResourcePools toBeUpdated = ResourcePoolsHelper.createHeapOnlyPools(500, EntryUnit.ENTRIES);
        try {
            existing = existing.validateAndMerge(toBeUpdated);
            Assert.fail();
        } catch (IllegalArgumentException uoe) {
            Assert.assertThat(uoe.getMessage(), Matchers.is("ResourcePool for heap with ResourceUnit 'entries' can not replace 'MB'"));
        }
        Assert.assertThat(existing.getPoolForResource(HEAP).getSize(), Matchers.is(20L));
        Assert.assertThat(existing.getPoolForResource(HEAP).getUnit(), Matchers.<ResourceUnit>is(MemoryUnit.MB));
    }
}

