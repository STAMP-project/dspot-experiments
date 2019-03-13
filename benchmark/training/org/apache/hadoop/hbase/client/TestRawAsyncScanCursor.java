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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestRawAsyncScanCursor extends AbstractTestScanCursor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRawAsyncScanCursor.class);

    private static AsyncConnection CONN;

    @Test
    public void testHeartbeatWithSparseFilter() throws IOException, InterruptedException, ExecutionException {
        doTest(false);
    }

    @Test
    public void testHeartbeatWithSparseFilterReversed() throws IOException, InterruptedException, ExecutionException {
        doTest(true);
    }

    @Test
    public void testSizeLimit() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        AsyncTable<AdvancedScanResultConsumer> table = TestRawAsyncScanCursor.CONN.getTable(AbstractTestScanCursor.TABLE_NAME);
        table.scan(createScanWithSizeLimit(), new AdvancedScanResultConsumer() {
            private int count;

            @Override
            public void onHeartbeat(ScanController controller) {
                try {
                    Assert.assertArrayEquals(AbstractTestScanCursor.ROWS[(((count) / (AbstractTestScanCursor.NUM_FAMILIES)) / (AbstractTestScanCursor.NUM_QUALIFIERS))], controller.cursor().get().getRow());
                    (count)++;
                } catch (Throwable e) {
                    future.completeExceptionally(e);
                    throw e;
                }
            }

            @Override
            public void onNext(Result[] results, ScanController controller) {
                try {
                    Assert.assertFalse(controller.cursor().isPresent());
                    Assert.assertEquals(1, results.length);
                    Assert.assertArrayEquals(AbstractTestScanCursor.ROWS[(((count) / (AbstractTestScanCursor.NUM_FAMILIES)) / (AbstractTestScanCursor.NUM_QUALIFIERS))], results[0].getRow());
                    (count)++;
                } catch (Throwable e) {
                    future.completeExceptionally(e);
                    throw e;
                }
            }

            @Override
            public void onError(Throwable error) {
                future.completeExceptionally(error);
            }

            @Override
            public void onComplete() {
                future.complete(null);
            }
        });
        future.get();
    }
}

