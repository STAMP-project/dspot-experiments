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
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestServerMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestServerMetrics.class);

    @Test
    public void testRegionLoadAggregation() {
        ServerMetrics metrics = ServerMetricsBuilder.toServerMetrics(ServerName.valueOf("localhost,1,1"), createServerLoadProto());
        Assert.assertEquals(13, metrics.getRegionMetrics().values().stream().mapToInt(( v) -> v.getStoreCount()).sum());
        Assert.assertEquals(114, metrics.getRegionMetrics().values().stream().mapToInt(( v) -> v.getStoreFileCount()).sum());
        Assert.assertEquals(129, metrics.getRegionMetrics().values().stream().mapToDouble(( v) -> v.getUncompressedStoreFileSize().get(Size.Unit.MEGABYTE)).sum(), 0);
        Assert.assertEquals(504, metrics.getRegionMetrics().values().stream().mapToDouble(( v) -> v.getStoreFileRootLevelIndexSize().get(Size.Unit.KILOBYTE)).sum(), 0);
        Assert.assertEquals(820, metrics.getRegionMetrics().values().stream().mapToDouble(( v) -> v.getStoreFileSize().get(Size.Unit.MEGABYTE)).sum(), 0);
        Assert.assertEquals(82, metrics.getRegionMetrics().values().stream().mapToDouble(( v) -> v.getStoreFileIndexSize().get(Size.Unit.KILOBYTE)).sum(), 0);
        Assert.assertEquals((((long) (Integer.MAX_VALUE)) * 2), metrics.getRegionMetrics().values().stream().mapToLong(( v) -> v.getReadRequestCount()).sum());
        Assert.assertEquals(100, metrics.getRegionMetrics().values().stream().mapToLong(( v) -> v.getCpRequestCount()).sum());
        Assert.assertEquals(300, metrics.getRegionMetrics().values().stream().mapToLong(( v) -> v.getFilteredReadRequestCount()).sum());
    }

    @Test
    public void testToString() {
        ServerMetrics metrics = ServerMetricsBuilder.toServerMetrics(ServerName.valueOf("localhost,1,1"), createServerLoadProto());
        String slToString = metrics.toString();
        Assert.assertTrue(slToString.contains("numberOfStores=13"));
        Assert.assertTrue(slToString.contains("numberOfStorefiles=114"));
        Assert.assertTrue(slToString.contains("storefileUncompressedSizeMB=129"));
        Assert.assertTrue(slToString.contains("storefileSizeMB=820"));
        Assert.assertTrue(slToString.contains("rootIndexSizeKB=504"));
        Assert.assertTrue(slToString.contains("coprocessors=[]"));
        Assert.assertTrue(slToString.contains("filteredReadRequestsCount=300"));
    }

    @Test
    public void testRegionLoadWrapAroundAggregation() {
        ServerMetrics metrics = ServerMetricsBuilder.toServerMetrics(ServerName.valueOf("localhost,1,1"), createServerLoadProto());
        long totalCount = ((long) (Integer.MAX_VALUE)) * 2;
        Assert.assertEquals(totalCount, metrics.getRegionMetrics().values().stream().mapToLong(( v) -> v.getReadRequestCount()).sum());
        Assert.assertEquals(totalCount, metrics.getRegionMetrics().values().stream().mapToLong(( v) -> v.getWriteRequestCount()).sum());
    }
}

