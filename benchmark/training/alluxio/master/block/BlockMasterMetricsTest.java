/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.block;


import Metrics.CAPACITY_FREE;
import Metrics.CAPACITY_TOTAL;
import Metrics.CAPACITY_USED;
import alluxio.master.block.DefaultBlockMaster.Metrics;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link DefaultBlockMaster.Metrics}.
 */
public final class BlockMasterMetricsTest {
    private static final String MEM = "MEM";

    private static final String HDD = "HDD";

    private BlockMaster mBlockMaster;

    @Test
    public void testMetricsCapacity() {
        Mockito.when(mBlockMaster.getCapacityBytes()).thenReturn(1000L);
        Assert.assertEquals(1000L, getGauge(CAPACITY_TOTAL));
        Mockito.when(mBlockMaster.getUsedBytes()).thenReturn(200L);
        Assert.assertEquals(200L, getGauge(CAPACITY_USED));
        Assert.assertEquals(800L, getGauge(CAPACITY_FREE));
    }

    @Test
    public void testMetricsTierCapacity() {
        Mockito.when(mBlockMaster.getTotalBytesOnTiers()).thenReturn(ImmutableMap.of(BlockMasterMetricsTest.MEM, 1000L, BlockMasterMetricsTest.HDD, 2000L));
        Mockito.when(mBlockMaster.getUsedBytesOnTiers()).thenReturn(ImmutableMap.of(BlockMasterMetricsTest.MEM, 100L, BlockMasterMetricsTest.HDD, 200L));
        Assert.assertEquals(1000L, getGauge((((Metrics.CAPACITY_TOTAL) + (Metrics.TIER)) + (BlockMasterMetricsTest.MEM))));
        Assert.assertEquals(2000L, getGauge((((Metrics.CAPACITY_TOTAL) + (Metrics.TIER)) + (BlockMasterMetricsTest.HDD))));
        Assert.assertEquals(100L, getGauge((((Metrics.CAPACITY_USED) + (Metrics.TIER)) + (BlockMasterMetricsTest.MEM))));
        Assert.assertEquals(200L, getGauge((((Metrics.CAPACITY_USED) + (Metrics.TIER)) + (BlockMasterMetricsTest.HDD))));
        Assert.assertEquals(900L, getGauge((((Metrics.CAPACITY_FREE) + (Metrics.TIER)) + (BlockMasterMetricsTest.MEM))));
        Assert.assertEquals(1800L, getGauge((((Metrics.CAPACITY_FREE) + (Metrics.TIER)) + (BlockMasterMetricsTest.HDD))));
    }
}

