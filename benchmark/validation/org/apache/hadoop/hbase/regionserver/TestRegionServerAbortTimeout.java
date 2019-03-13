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
import java.util.Optional;
import java.util.TimerTask;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
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
public class TestRegionServerAbortTimeout {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerAbortTimeout.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerAbortTimeout.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("RSAbort");

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] CQ = Bytes.toBytes("cq");

    private static final int REGIONS_NUM = 5;

    private static final int SLEEP_TIME_WHEN_CLOSE_REGION = 1000;

    private static volatile boolean abortTimeoutTaskScheduled = false;

    @Test
    public void testAbortTimeout() throws Exception {
        Thread writer = new Thread(() -> {
            try {
                try (Table table = TestRegionServerAbortTimeout.UTIL.getConnection().getTable(TestRegionServerAbortTimeout.TABLE_NAME)) {
                    for (int i = 0; i < 10000; i++) {
                        table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(TestRegionServerAbortTimeout.CF, TestRegionServerAbortTimeout.CQ, Bytes.toBytes(i)));
                    }
                }
            } catch (IOException e) {
                TestRegionServerAbortTimeout.LOG.warn("Failed to load data");
            }
        });
        writer.setDaemon(true);
        writer.start();
        // Abort one region server
        TestRegionServerAbortTimeout.UTIL.getMiniHBaseCluster().getRegionServer(0).abort("Abort RS for test");
        long startTime = System.currentTimeMillis();
        long timeout = ((TestRegionServerAbortTimeout.REGIONS_NUM) * (TestRegionServerAbortTimeout.SLEEP_TIME_WHEN_CLOSE_REGION)) * 10;
        while (((System.currentTimeMillis()) - startTime) < timeout) {
            if ((TestRegionServerAbortTimeout.UTIL.getMiniHBaseCluster().getLiveRegionServerThreads().size()) == 1) {
                Assert.assertTrue("Abort timer task should be scheduled", TestRegionServerAbortTimeout.abortTimeoutTaskScheduled);
                return;
            }
            Threads.sleep(TestRegionServerAbortTimeout.SLEEP_TIME_WHEN_CLOSE_REGION);
        } 
        Assert.fail((("Failed to abort a region server in " + timeout) + " ms"));
    }

    static class TestAbortTimeoutTask extends TimerTask {
        public TestAbortTimeoutTask() {
        }

        @Override
        public void run() {
            TestRegionServerAbortTimeout.LOG.info("TestAbortTimeoutTask was scheduled");
            TestRegionServerAbortTimeout.abortTimeoutTaskScheduled = true;
        }
    }

    public static class SleepWhenCloseCoprocessor implements RegionCoprocessor , RegionObserver {
        public SleepWhenCloseCoprocessor() {
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preClose(ObserverContext<RegionCoprocessorEnvironment> c, boolean abortRequested) throws IOException {
            Threads.sleep(TestRegionServerAbortTimeout.SLEEP_TIME_WHEN_CLOSE_REGION);
        }
    }
}

