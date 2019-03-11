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
package org.ehcache.config.builders;


import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.SizedResourcePoolImpl;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.config.ResourceType.Core.HEAP;


public class ResourcePoolsBuilderTest {
    @Test
    public void testPreExistingWith() throws Exception {
        ResourcePoolsBuilder builder = ResourcePoolsBuilder.newResourcePoolsBuilder();
        builder = builder.heap(8, MemoryUnit.MB);
        try {
            builder.with(new SizedResourcePoolImpl<>(HEAP, 16, MemoryUnit.MB, false));
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Can not add 'Pool {16 MB heap}'; configuration already contains 'Pool {8 MB heap}'"));
        }
    }

    @Test
    public void testWithReplacing() throws Exception {
        long initialSize = 8;
        long newSize = 16;
        ResourceUnit mb = MemoryUnit.MB;
        ResourcePoolsBuilder builder = ResourcePoolsBuilder.newResourcePoolsBuilder();
        builder = builder.heap(initialSize, mb);
        ResourcePools initialPools = builder.build();
        SizedResourcePool newPool = new SizedResourcePoolImpl<>(HEAP, newSize, mb, false);
        builder = builder.withReplacing(newPool);
        ResourcePools replacedPools = builder.build();
        final SizedResourcePool heapPool = replacedPools.getPoolForResource(HEAP);
        Assert.assertThat(heapPool.isPersistent(), Matchers.is(Matchers.equalTo(newPool.isPersistent())));
        Assert.assertThat(initialPools.getPoolForResource(HEAP).getSize(), Matchers.is(initialSize));
        Assert.assertThat(heapPool.getSize(), Matchers.is(newSize));
        Assert.assertThat(heapPool.getUnit(), Matchers.is(mb));
    }

    @Test
    public void testWithReplacingNoInitial() throws Exception {
        long newSize = 16;
        ResourceUnit mb = MemoryUnit.MB;
        SizedResourcePool newPool = new SizedResourcePoolImpl<>(HEAP, newSize, mb, false);
        ResourcePoolsBuilder builder = ResourcePoolsBuilder.newResourcePoolsBuilder();
        ResourcePools resourcePools = builder.withReplacing(newPool).build();
        SizedResourcePool pool = resourcePools.getPoolForResource(HEAP);
        Assert.assertThat(pool.getSize(), Matchers.is(newSize));
        Assert.assertThat(pool.getUnit(), Matchers.is(mb));
    }

    @Test
    public void testHeap() throws Exception {
        ResourcePools pools = ResourcePoolsBuilder.heap(10).build();
        Assert.assertThat(pools.getPoolForResource(HEAP).getSize(), Matchers.is(10L));
    }
}

