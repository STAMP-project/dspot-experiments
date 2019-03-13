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
package org.apache.hadoop.hbase.wal;


import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.util.SortedMap;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.shaded.protobuf.RequestConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.GetLastFlushedSequenceIdRequest;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestWALFiltering {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALFiltering.class);

    private static final int NUM_RS = 4;

    private static final TableName TABLE_NAME = TableName.valueOf("TestWALFiltering");

    private static final byte[] CF1 = Bytes.toBytes("MyCF1");

    private static final byte[] CF2 = Bytes.toBytes("MyCF2");

    private static final byte[][] FAMILIES = new byte[][]{ TestWALFiltering.CF1, TestWALFiltering.CF2 };

    private HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testFlushedSequenceIdsSentToHMaster() throws ServiceException, IOException, InterruptedException {
        SortedMap<byte[], Long> allFlushedSequenceIds = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (int i = 0; i < (TestWALFiltering.NUM_RS); ++i) {
            flushAllRegions(i);
        }
        Thread.sleep(10000);
        HMaster master = TEST_UTIL.getMiniHBaseCluster().getMaster();
        for (int i = 0; i < (TestWALFiltering.NUM_RS); ++i) {
            for (byte[] regionName : getRegionsByServer(i)) {
                if (allFlushedSequenceIds.containsKey(regionName)) {
                    GetLastFlushedSequenceIdRequest req = RequestConverter.buildGetLastFlushedSequenceIdRequest(regionName);
                    Assert.assertEquals(((long) (allFlushedSequenceIds.get(regionName))), master.getMasterRpcServices().getLastFlushedSequenceId(null, req).getLastFlushedSequenceId());
                }
            }
        }
    }
}

