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
package org.ehcache.impl.internal.statistics;


import java.util.Optional;
import java.util.Set;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.core.statistics.CacheOperationOutcomes;
import org.ehcache.core.statistics.StoreOperationOutcomes;
import org.ehcache.core.statistics.TierOperationOutcomes;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.terracotta.context.TreeNode;
import org.terracotta.statistics.OperationStatistic;


public class StatsUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    CacheManager cacheManager;

    Cache<Long, String> cache;

    @Test
    public void testHasTag_found() throws Exception {
        Set<TreeNode> statResult = queryProperty("cache");
        assertThat(statResult.size()).isEqualTo(1);
    }

    @Test
    public void testHasTag_notfound() throws Exception {
        Set<TreeNode> statResult = queryProperty("xxx");
        assertThat(statResult.size()).isZero();
    }

    @Test
    public void testHasProperty_found() throws Exception {
        Set<TreeNode> statResult = queryProperty("myproperty", "myvalue");
        assertThat(statResult.size()).isEqualTo(1);
    }

    @Test
    public void testHasProperty_notfoundKey() throws Exception {
        Set<TreeNode> statResult = queryProperty("xxx");
        assertThat(statResult.size()).isZero();
    }

    @Test
    public void testHasProperty_valueDoesntMatch() throws Exception {
        Set<TreeNode> statResult = queryProperty("myproperty", "xxx");
        assertThat(statResult.size()).isZero();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindStatisticOnDescendantsWithDiscriminator() throws Exception {
        Optional<OperationStatistic<TierOperationOutcomes.GetOutcome>> stat = StatsUtils.findStatisticOnDescendants(cache, "OnHeap", "tier", "get");
        assertThat(stat.get().sum()).isEqualTo(1L);
        stat = StatsUtils.findStatisticOnDescendants(cache, "OnHeap", "tier", "xxx");
        assertThat(stat.isPresent()).isFalse();
        stat = StatsUtils.findStatisticOnDescendants(cache, "xxx", "tier", "xxx");
        assertThat(stat.isPresent()).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindStatisticOnDescendants() throws Exception {
        Optional<OperationStatistic<TierOperationOutcomes.GetOutcome>> stat = StatsUtils.findStatisticOnDescendants(cache, "OnHeap", "get");
        assertThat(stat.get().sum()).isEqualTo(1L);
        stat = StatsUtils.findStatisticOnDescendants(cache, "OnHeap", "xxx");
        assertThat(stat.isPresent()).isFalse();
        stat = StatsUtils.findStatisticOnDescendants(cache, "xxx", "xxx");
        assertThat(stat.isPresent()).isFalse();
    }

    @Test
    public void testFindCacheStatistic() {
        OperationStatistic<CacheOperationOutcomes.GetOutcome> stat = StatsUtils.findOperationStatisticOnChildren(cache, CacheOperationOutcomes.GetOutcome.class, "get");
        assertThat(stat.sum()).isEqualTo(1L);
    }

    @Test
    public void testFindCacheStatistic_notExisting() {
        expectedException.expect(RuntimeException.class);
        StatsUtils.findOperationStatisticOnChildren(cache, CacheOperationOutcomes.GetOutcome.class, "xxx");
    }

    @Test
    public void testFindTiers() {
        String[] tiers = StatsUtils.findTiers(cache);
        assertThat(tiers).containsOnly("OnHeap");
    }

    @Test
    public void testFindLowerTier_one() {
        String tier = StatsUtils.findLowestTier(new String[]{ "OnHeap" });
        assertThat(tier).isEqualTo("OnHeap");
    }

    @Test
    public void testFindLowerTier_two() {
        String tier = StatsUtils.findLowestTier(new String[]{ "OnHeap", "Offheap" });
        assertThat(tier).isEqualTo("Offheap");
    }

    @Test
    public void testFindLowerTier_three() {
        String tier = StatsUtils.findLowestTier(new String[]{ "OnHeap", "Offheap", "Disk" });
        assertThat(tier).isEqualTo("Disk");
    }

    @Test
    public void testFindLowerTier_none() {
        expectedException.expect(RuntimeException.class);
        StatsUtils.findLowestTier(new String[0]);
    }

    @Test
    public void testHasOperationStatistic_found() {
        assertThat(StatsUtils.hasOperationStat(cache, StoreOperationOutcomes.GetOutcome.class, "get")).isTrue();
    }

    @Test
    public void testHasOperationStatistic_notFound() {
        assertThat(StatsUtils.hasOperationStat(cache, StoreOperationOutcomes.GetOutcome.class, "xxx")).isFalse();
    }
}

