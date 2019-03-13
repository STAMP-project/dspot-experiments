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


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, LargeTests.class })
public class TestTimestampFilterSeekHint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTimestampFilterSeekHint.class);

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private static final String RK = "myRK";

    private static final byte[] RK_BYTES = Bytes.toBytes(TestTimestampFilterSeekHint.RK);

    private static final String FAMILY = "D";

    private static final byte[] FAMILY_BYTES = Bytes.toBytes(TestTimestampFilterSeekHint.FAMILY);

    private static final String QUAL = "0";

    private static final byte[] QUAL_BYTES = Bytes.toBytes(TestTimestampFilterSeekHint.QUAL);

    public static final int MAX_VERSIONS = 50000;

    private HRegion region;

    private int regionCount = 0;

    @Test
    public void testGetSeek() throws IOException {
        StoreFileScanner.instrument();
        prepareRegion();
        Get g = new Get(TestTimestampFilterSeekHint.RK_BYTES);
        final TimestampsFilter timestampsFilter = new TimestampsFilter(ImmutableList.of(5L), true);
        g.setFilter(timestampsFilter);
        final long initialSeekCount = StoreFileScanner.getSeekCount();
        region.get(g);
        final long finalSeekCount = StoreFileScanner.getSeekCount();
        /* Make sure there's more than one.
        Aka one seek to get to the row, and one to get to the time.
         */
        Assert.assertTrue((finalSeekCount >= (initialSeekCount + 3)));
    }

    @Test
    public void testGetDoesntSeekWithNoHint() throws IOException {
        StoreFileScanner.instrument();
        prepareRegion();
        Get g = new Get(TestTimestampFilterSeekHint.RK_BYTES);
        g.setFilter(new TimestampsFilter(ImmutableList.of(5L)));
        final long initialSeekCount = StoreFileScanner.getSeekCount();
        region.get(g);
        final long finalSeekCount = StoreFileScanner.getSeekCount();
        Assert.assertTrue((finalSeekCount >= initialSeekCount));
        Assert.assertTrue((finalSeekCount < (initialSeekCount + 3)));
    }
}

