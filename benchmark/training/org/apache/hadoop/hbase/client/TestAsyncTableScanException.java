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


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.exceptions.ScannerResetException;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncTableScanException {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableScanException.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("scan");

    private static byte[] FAMILY = Bytes.toBytes("family");

    private static byte[] QUAL = Bytes.toBytes("qual");

    private static AsyncConnection CONN;

    private static AtomicInteger REQ_COUNT = new AtomicInteger();

    private static volatile int ERROR_AT;

    private static volatile boolean ERROR;

    private static volatile boolean DO_NOT_RETRY;

    public static final class ErrorCP implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public boolean postScannerNext(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s, List<Result> result, int limit, boolean hasNext) throws IOException {
            TestAsyncTableScanException.REQ_COUNT.incrementAndGet();
            if (((TestAsyncTableScanException.ERROR_AT) == (TestAsyncTableScanException.REQ_COUNT.get())) || (TestAsyncTableScanException.ERROR)) {
                if (TestAsyncTableScanException.DO_NOT_RETRY) {
                    throw new DoNotRetryIOException("Injected exception");
                } else {
                    throw new IOException("Injected exception");
                }
            }
            return RegionObserver.super.postScannerNext(c, s, result, limit, hasNext);
        }
    }

    @Test(expected = DoNotRetryIOException.class)
    public void testDoNotRetryIOException() throws IOException {
        TestAsyncTableScanException.ERROR_AT = 1;
        TestAsyncTableScanException.DO_NOT_RETRY = true;
        try (ResultScanner scanner = TestAsyncTableScanException.CONN.getTable(TestAsyncTableScanException.TABLE_NAME).getScanner(TestAsyncTableScanException.FAMILY)) {
            scanner.next();
        }
    }

    @Test
    public void testIOException() throws IOException {
        TestAsyncTableScanException.ERROR = true;
        try (ResultScanner scanner = TestAsyncTableScanException.CONN.getTableBuilder(TestAsyncTableScanException.TABLE_NAME).setMaxAttempts(3).build().getScanner(TestAsyncTableScanException.FAMILY)) {
            scanner.next();
            Assert.fail();
        } catch (RetriesExhaustedException e) {
            // expected
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(ScannerResetException.class));
        }
        Assert.assertTrue(((TestAsyncTableScanException.REQ_COUNT.get()) >= 3));
    }

    @Test
    public void testRecoveryFromScannerResetWhileOpening() throws IOException {
        TestAsyncTableScanException.ERROR_AT = 1;
        count();
        // we should at least request 1 time otherwise the error will not be triggered, and then we
        // need at least one more request to get the remaining results.
        Assert.assertTrue(((TestAsyncTableScanException.REQ_COUNT.get()) >= 2));
    }

    @Test
    public void testRecoveryFromScannerResetInTheMiddle() throws IOException {
        TestAsyncTableScanException.ERROR_AT = 2;
        count();
        // we should at least request 2 times otherwise the error will not be triggered, and then we
        // need at least one more request to get the remaining results.
        Assert.assertTrue(((TestAsyncTableScanException.REQ_COUNT.get()) >= 3));
    }
}

