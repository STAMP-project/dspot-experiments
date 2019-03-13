/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.regionserver;


import NoLimitThroughputController.INSTANCE;
import StoreEngine.STORE_ENGINE_CLASS_KEY;
import StripeStoreConfig.INITIAL_STRIPE_COUNT_KEY;
import StripeStoreConfig.MIN_FILES_L0_KEY;
import StripeStoreFileManager.OPEN_KEY;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionContext;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequestImpl;
import org.apache.hadoop.hbase.regionserver.compactions.StripeCompactionPolicy;
import org.apache.hadoop.hbase.regionserver.compactions.StripeCompactor;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestStripeStoreEngine {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStripeStoreEngine.class);

    @Test
    public void testCreateBasedOnConfig() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set(STORE_ENGINE_CLASS_KEY, TestStripeStoreEngine.TestStoreEngine.class.getName());
        StripeStoreEngine se = TestStripeStoreEngine.createEngine(conf);
        Assert.assertTrue(((se.getCompactionPolicy()) instanceof StripeCompactionPolicy));
    }

    public static class TestStoreEngine extends StripeStoreEngine {
        public void setCompactorOverride(StripeCompactor compactorOverride) {
            this.compactor = compactorOverride;
        }
    }

    @Test
    public void testCompactionContextForceSelect() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        int targetCount = 2;
        conf.setInt(INITIAL_STRIPE_COUNT_KEY, targetCount);
        conf.setInt(MIN_FILES_L0_KEY, 2);
        conf.set(STORE_ENGINE_CLASS_KEY, TestStripeStoreEngine.TestStoreEngine.class.getName());
        TestStripeStoreEngine.TestStoreEngine se = TestStripeStoreEngine.createEngine(conf);
        StripeCompactor mockCompactor = Mockito.mock(StripeCompactor.class);
        se.setCompactorOverride(mockCompactor);
        Mockito.when(mockCompactor.compact(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new ArrayList());
        // Produce 3 L0 files.
        HStoreFile sf = TestStripeStoreEngine.createFile();
        ArrayList<HStoreFile> compactUs = TestStripeStoreEngine.al(sf, TestStripeStoreEngine.createFile(), TestStripeStoreEngine.createFile());
        getStoreFileManager().loadFiles(compactUs);
        // Create a compaction that would want to split the stripe.
        CompactionContext compaction = createCompaction();
        compaction.select(TestStripeStoreEngine.al(), false, false, false);
        Assert.assertEquals(3, compaction.getRequest().getFiles().size());
        // Override the file list. Granted, overriding this compaction in this manner will
        // break things in real world, but we only want to verify the override.
        compactUs.remove(sf);
        CompactionRequestImpl req = new CompactionRequestImpl(compactUs);
        compaction.forceSelect(req);
        Assert.assertEquals(2, compaction.getRequest().getFiles().size());
        Assert.assertFalse(compaction.getRequest().getFiles().contains(sf));
        // Make sure the correct method it called on compactor.
        compaction.compact(INSTANCE, null);
        Mockito.verify(mockCompactor, Mockito.times(1)).compact(compaction.getRequest(), targetCount, 0L, OPEN_KEY, OPEN_KEY, null, null, INSTANCE, null);
    }
}

