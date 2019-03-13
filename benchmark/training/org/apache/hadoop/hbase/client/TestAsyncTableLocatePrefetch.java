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
package org.apache.hadoop.hbase.client;


import RegionLocateType.CURRENT;
import RegionReplicaUtil.DEFAULT_REPLICA_ID;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncTableLocatePrefetch {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableLocatePrefetch.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static AsyncConnection CONN;

    private static AsyncNonMetaRegionLocator LOCATOR;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        Assert.assertNotNull(TestAsyncTableLocatePrefetch.LOCATOR.getRegionLocations(TestAsyncTableLocatePrefetch.TABLE_NAME, Bytes.toBytes("zzz"), DEFAULT_REPLICA_ID, CURRENT, false).get());
        // we finish the request before we adding the remaining results to cache so sleep a bit here
        Thread.sleep(1000);
        // confirm that the locations of all the regions have been cached.
        Assert.assertNotNull(TestAsyncTableLocatePrefetch.LOCATOR.getRegionLocationInCache(TestAsyncTableLocatePrefetch.TABLE_NAME, Bytes.toBytes("aaa")));
        for (byte[] row : HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE) {
            Assert.assertNotNull(TestAsyncTableLocatePrefetch.LOCATOR.getRegionLocationInCache(TestAsyncTableLocatePrefetch.TABLE_NAME, row));
        }
    }
}

