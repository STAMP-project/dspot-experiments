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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.regionserver.MemStoreFlusher.FlushRegionEntry;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;

import static FlushLifeCycleTracker.DUMMY;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestFlushRegionEntry {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFlushRegionEntry.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testFlushRegionEntryEquality() {
        RegionInfo hri = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setRegionId(1).setReplicaId(0).build();
        HRegion r = Mockito.mock(HRegion.class);
        Mockito.doReturn(hri).when(r).getRegionInfo();
        FlushRegionEntry entry = new FlushRegionEntry(r, true, DUMMY);
        FlushRegionEntry other = new FlushRegionEntry(r, true, DUMMY);
        Assert.assertEquals(entry.hashCode(), other.hashCode());
        Assert.assertEquals(entry, other);
    }
}

