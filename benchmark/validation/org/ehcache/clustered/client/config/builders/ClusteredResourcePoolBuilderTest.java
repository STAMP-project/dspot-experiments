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
package org.ehcache.clustered.client.config.builders;


import ClusteredResourceType.Types.DEDICATED;
import ClusteredResourceType.Types.SHARED;
import org.ehcache.clustered.client.config.DedicatedClusteredResourcePool;
import org.ehcache.clustered.client.config.SharedClusteredResourcePool;
import org.ehcache.config.ResourcePool;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ClusteredResourcePoolBuilderTest {
    @Test
    public void dedicated2Arg() throws Exception {
        ResourcePool pool = ClusteredResourcePoolBuilder.clusteredDedicated(16, MemoryUnit.GB);
        Assert.assertThat(pool, Matchers.is(Matchers.instanceOf(DedicatedClusteredResourcePool.class)));
        Assert.assertThat(pool.getType(), Matchers.is(DEDICATED));
        Assert.assertThat(pool.isPersistent(), Matchers.is(true));
        Assert.assertThat(getSize(), Matchers.is(16L));
        Assert.assertThat(getUnit(), Matchers.is(MemoryUnit.GB));
        Assert.assertThat(getFromResource(), Matchers.is(Matchers.nullValue()));
    }

    @Test(expected = NullPointerException.class)
    public void dedicated2ArgUnitNull() throws Exception {
        ClusteredResourcePoolBuilder.clusteredDedicated(16, null);
    }

    @Test
    public void dedicated3Arg() throws Exception {
        ResourcePool pool = ClusteredResourcePoolBuilder.clusteredDedicated("resourceId", 16, MemoryUnit.GB);
        Assert.assertThat(pool, Matchers.is(Matchers.instanceOf(DedicatedClusteredResourcePool.class)));
        Assert.assertThat(pool.getType(), Matchers.is(DEDICATED));
        Assert.assertThat(pool.isPersistent(), Matchers.is(true));
        Assert.assertThat(getSize(), Matchers.is(16L));
        Assert.assertThat(getUnit(), Matchers.is(MemoryUnit.GB));
        Assert.assertThat(getFromResource(), Matchers.is("resourceId"));
    }

    @Test
    public void dedicated3ArgFromNull() throws Exception {
        ResourcePool pool = ClusteredResourcePoolBuilder.clusteredDedicated(null, 16, MemoryUnit.GB);
        Assert.assertThat(pool, Matchers.is(Matchers.instanceOf(DedicatedClusteredResourcePool.class)));
        Assert.assertThat(pool.getType(), Matchers.is(DEDICATED));
        Assert.assertThat(pool.isPersistent(), Matchers.is(true));
        Assert.assertThat(getSize(), Matchers.is(16L));
        Assert.assertThat(getUnit(), Matchers.is(MemoryUnit.GB));
        Assert.assertThat(getFromResource(), Matchers.is(Matchers.nullValue()));
    }

    @Test(expected = NullPointerException.class)
    public void dedicated3ArgUnitNull() throws Exception {
        ClusteredResourcePoolBuilder.clusteredDedicated("resourceId", 16, null);
    }

    @Test
    public void shared() throws Exception {
        ResourcePool pool = ClusteredResourcePoolBuilder.clusteredShared("resourceId");
        Assert.assertThat(pool, Matchers.is(Matchers.instanceOf(SharedClusteredResourcePool.class)));
        Assert.assertThat(pool.getType(), Matchers.is(SHARED));
        Assert.assertThat(pool.isPersistent(), Matchers.is(true));
        Assert.assertThat(getSharedResourcePool(), Matchers.is("resourceId"));
    }

    @Test(expected = NullPointerException.class)
    public void sharedSharedResourceNull() throws Exception {
        ClusteredResourcePoolBuilder.clusteredShared(null);
    }
}

