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
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.exceptions.TimeoutIOException;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncRegionLocator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncRegionLocator.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static AsyncConnectionImpl CONN;

    private static AsyncRegionLocator LOCATOR;

    private static volatile long SLEEP_MS = 0L;

    public static class SleepRegionObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preScannerOpen(ObserverContext<RegionCoprocessorEnvironment> e, Scan scan) throws IOException {
            if ((TestAsyncRegionLocator.SLEEP_MS) > 0) {
                Threads.sleepWithoutInterrupt(TestAsyncRegionLocator.SLEEP_MS);
            }
        }
    }

    @Test
    public void testTimeout() throws InterruptedException, ExecutionException {
        TestAsyncRegionLocator.SLEEP_MS = 1000;
        long startNs = System.nanoTime();
        try {
            TestAsyncRegionLocator.LOCATOR.getRegionLocation(TestAsyncRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, TimeUnit.MILLISECONDS.toNanos(500)).get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(TimeoutIOException.class));
        }
        long costMs = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - startNs));
        Assert.assertTrue((costMs >= 500));
        Assert.assertTrue((costMs < 1000));
        // wait for the background task finish
        Thread.sleep(2000);
        // Now the location should be in cache, so we will not visit meta again.
        HRegionLocation loc = TestAsyncRegionLocator.LOCATOR.getRegionLocation(TestAsyncRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, TimeUnit.MILLISECONDS.toNanos(500)).get();
        Assert.assertEquals(loc.getServerName(), TestAsyncRegionLocator.TEST_UTIL.getHBaseCluster().getRegionServer(0).getServerName());
    }

    @Test
    public void testNoCompletionException() {
        // make sure that we do not get CompletionException
        TestAsyncRegionLocator.SLEEP_MS = 0;
        AtomicReference<Throwable> errorHolder = new AtomicReference<>();
        try {
            TestAsyncRegionLocator.LOCATOR.getRegionLocation(TableName.valueOf("NotExist"), HConstants.EMPTY_START_ROW, CURRENT, TimeUnit.SECONDS.toNanos(1)).whenComplete(( r, e) -> errorHolder.set(e)).join();
            Assert.fail();
        } catch (CompletionException e) {
            // join will return a CompletionException, which is OK
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(TableNotFoundException.class));
        }
        // but we need to make sure that we do not get a CompletionException in the callback
        Assert.assertThat(errorHolder.get(), CoreMatchers.instanceOf(TableNotFoundException.class));
    }
}

