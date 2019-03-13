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


import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestSecureBulkLoadManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSecureBulkLoadManager.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSecureBulkLoadManager.class);

    private static TableName TABLE = TableName.valueOf(Bytes.toBytes("TestSecureBulkLoadManager"));

    private static byte[] FAMILY = Bytes.toBytes("family");

    private static byte[] COLUMN = Bytes.toBytes("column");

    private static byte[] key1 = Bytes.toBytes("row1");

    private static byte[] key2 = Bytes.toBytes("row2");

    private static byte[] key3 = Bytes.toBytes("row3");

    private static byte[] value1 = Bytes.toBytes("t1");

    private static byte[] value3 = Bytes.toBytes("t3");

    private static byte[] SPLIT_ROWKEY = TestSecureBulkLoadManager.key2;

    private Thread ealierBulkload;

    private Thread laterBulkload;

    protected static final HBaseTestingUtility testUtil = new HBaseTestingUtility();

    private static Configuration conf = TestSecureBulkLoadManager.testUtil.getConfiguration();

    /**
     * After a secure bulkload finished , there is a clean-up for FileSystems used in the bulkload.
     * Sometimes, FileSystems used in the finished bulkload might also be used in other bulkload
     * calls, or there are other FileSystems created by the same user, they could be closed by a
     * FileSystem.closeAllForUGI call. So during the clean-up, those FileSystems need to be used
     * later can not get closed ,or else a race condition occurs.
     *
     * testForRaceCondition tests the case that two secure bulkload calls from the same UGI go
     * into two different regions and one bulkload finishes earlier when the other bulkload still
     * needs its FileSystems, checks that both bulkloads succeed.
     */
    @Test
    public void testForRaceCondition() throws Exception {
        Consumer<HRegion> fsCreatedListener = new Consumer<HRegion>() {
            @Override
            public void accept(HRegion hRegion) {
                if (hRegion.getRegionInfo().containsRow(TestSecureBulkLoadManager.key3)) {
                    Threads.shutdown(ealierBulkload);// / wait util the other bulkload finished

                }
            }
        };
        TestSecureBulkLoadManager.testUtil.getMiniHBaseCluster().getRegionServerThreads().get(0).getRegionServer().secureBulkLoadManager.setFsCreatedListener(fsCreatedListener);
        // / create table
        TestSecureBulkLoadManager.testUtil.createTable(TestSecureBulkLoadManager.TABLE, TestSecureBulkLoadManager.FAMILY, Bytes.toByteArrays(TestSecureBulkLoadManager.SPLIT_ROWKEY));
        // / prepare files
        Path rootdir = TestSecureBulkLoadManager.testUtil.getMiniHBaseCluster().getRegionServerThreads().get(0).getRegionServer().getRootDir();
        Path dir1 = new Path(rootdir, "dir1");
        prepareHFile(dir1, TestSecureBulkLoadManager.key1, TestSecureBulkLoadManager.value1);
        Path dir2 = new Path(rootdir, "dir2");
        prepareHFile(dir2, TestSecureBulkLoadManager.key3, TestSecureBulkLoadManager.value3);
        // / do bulkload
        final AtomicReference<Throwable> t1Exception = new AtomicReference<>();
        final AtomicReference<Throwable> t2Exception = new AtomicReference<>();
        ealierBulkload = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doBulkloadWithoutRetry(dir1);
                } catch (Exception e) {
                    TestSecureBulkLoadManager.LOG.error("bulk load failed .", e);
                    t1Exception.set(e);
                }
            }
        });
        laterBulkload = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doBulkloadWithoutRetry(dir2);
                } catch (Exception e) {
                    TestSecureBulkLoadManager.LOG.error("bulk load failed .", e);
                    t2Exception.set(e);
                }
            }
        });
        ealierBulkload.start();
        laterBulkload.start();
        Threads.shutdown(ealierBulkload);
        Threads.shutdown(laterBulkload);
        Assert.assertNull(t1Exception.get());
        Assert.assertNull(t2Exception.get());
        // / check bulkload ok
        Get get1 = new Get(TestSecureBulkLoadManager.key1);
        Get get3 = new Get(TestSecureBulkLoadManager.key3);
        Table t = TestSecureBulkLoadManager.testUtil.getConnection().getTable(TestSecureBulkLoadManager.TABLE);
        Result r = t.get(get1);
        Assert.assertArrayEquals(r.getValue(TestSecureBulkLoadManager.FAMILY, TestSecureBulkLoadManager.COLUMN), TestSecureBulkLoadManager.value1);
        r = t.get(get3);
        Assert.assertArrayEquals(r.getValue(TestSecureBulkLoadManager.FAMILY, TestSecureBulkLoadManager.COLUMN), TestSecureBulkLoadManager.value3);
    }

    /**
     * A trick is used to make sure server-side failures( if any ) not being covered up by a client
     * retry. Since LoadIncrementalHFiles.doBulkLoad keeps performing bulkload calls as long as the
     * HFile queue is not empty, while server-side exceptions in the doAs block do not lead
     * to a client exception, a bulkload will always succeed in this case by default, thus client
     * will never be aware that failures have ever happened . To avoid this kind of retry ,
     * a MyExceptionToAvoidRetry exception is thrown after bulkLoadPhase finished and caught
     * silently outside the doBulkLoad call, so that the bulkLoadPhase would be called exactly
     * once, and server-side failures, if any ,can be checked via data.
     */
    class MyExceptionToAvoidRetry extends DoNotRetryIOException {}
}

