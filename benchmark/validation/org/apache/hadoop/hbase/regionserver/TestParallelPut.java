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


import OperationStatusCode.SUCCESS;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing of multiPut in parallel.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestParallelPut {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestParallelPut.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestParallelPut.class);

    @Rule
    public TestName name = new TestName();

    private HRegion region = null;

    private static HBaseTestingUtility HBTU = new HBaseTestingUtility();

    private static final int THREADS100 = 100;

    // Test names
    static byte[] tableName;

    static final byte[] qual1 = Bytes.toBytes("qual1");

    static final byte[] qual2 = Bytes.toBytes("qual2");

    static final byte[] qual3 = Bytes.toBytes("qual3");

    static final byte[] value1 = Bytes.toBytes("value1");

    static final byte[] value2 = Bytes.toBytes("value2");

    static final byte[] row = Bytes.toBytes("rowA");

    static final byte[] row2 = Bytes.toBytes("rowB");

    // ////////////////////////////////////////////////////////////////////////////
    // New tests that don't spin up a mini cluster but rather just test the
    // individual code pieces in the HRegion.
    // ////////////////////////////////////////////////////////////////////////////
    /**
     * Test one put command.
     */
    @Test
    public void testPut() throws IOException {
        TestParallelPut.LOG.info("Starting testPut");
        this.region = initHRegion(TestParallelPut.tableName, getName(), HBaseTestingUtility.fam1);
        long value = 1L;
        Put put = new Put(TestParallelPut.row);
        put.addColumn(HBaseTestingUtility.fam1, TestParallelPut.qual1, Bytes.toBytes(value));
        region.put(put);
        TestParallelPut.assertGet(this.region, TestParallelPut.row, HBaseTestingUtility.fam1, TestParallelPut.qual1, Bytes.toBytes(value));
    }

    /**
     * Test multi-threaded Puts.
     */
    @Test
    public void testParallelPuts() throws IOException {
        TestParallelPut.LOG.info("Starting testParallelPuts");
        this.region = initHRegion(TestParallelPut.tableName, getName(), HBaseTestingUtility.fam1);
        int numOps = 1000;// these many operations per thread

        // create 100 threads, each will do its own puts
        TestParallelPut.Putter[] all = new TestParallelPut.Putter[TestParallelPut.THREADS100];
        // create all threads
        for (int i = 0; i < (TestParallelPut.THREADS100); i++) {
            all[i] = new TestParallelPut.Putter(region, i, numOps);
        }
        // run all threads
        for (int i = 0; i < (TestParallelPut.THREADS100); i++) {
            all[i].start();
        }
        // wait for all threads to finish
        for (int i = 0; i < (TestParallelPut.THREADS100); i++) {
            try {
                all[i].join();
            } catch (InterruptedException e) {
                TestParallelPut.LOG.warn(("testParallelPuts encountered InterruptedException." + " Ignoring...."), e);
            }
        }
        TestParallelPut.LOG.info((("testParallelPuts successfully verified " + (numOps * (TestParallelPut.THREADS100))) + " put operations."));
    }

    /**
     * A thread that makes a few put calls
     */
    public static class Putter extends Thread {
        private final HRegion region;

        private final int threadNumber;

        private final int numOps;

        private final Random rand = new Random();

        byte[] rowkey = null;

        public Putter(HRegion region, int threadNumber, int numOps) {
            this.region = region;
            this.threadNumber = threadNumber;
            this.numOps = numOps;
            this.rowkey = Bytes.toBytes(((long) (threadNumber)));// unique rowid per thread

            setDaemon(true);
        }

        @Override
        public void run() {
            byte[] value = new byte[100];
            Put[] in = new Put[1];
            // iterate for the specified number of operations
            for (int i = 0; i < (numOps); i++) {
                // generate random bytes
                rand.nextBytes(value);
                // put the randombytes and verify that we can read it. This is one
                // way of ensuring that rwcc manipulation in HRegion.put() is fine.
                Put put = new Put(rowkey);
                put.addColumn(HBaseTestingUtility.fam1, TestParallelPut.qual1, value);
                in[0] = put;
                try {
                    OperationStatus[] ret = region.batchMutate(in);
                    Assert.assertEquals(1, ret.length);
                    Assert.assertEquals(SUCCESS, ret[0].getOperationStatusCode());
                    TestParallelPut.assertGet(this.region, rowkey, HBaseTestingUtility.fam1, TestParallelPut.qual1, value);
                } catch (IOException e) {
                    Assert.assertTrue((((("Thread id " + (threadNumber)) + " operation ") + i) + " failed."), false);
                }
            }
        }
    }
}

