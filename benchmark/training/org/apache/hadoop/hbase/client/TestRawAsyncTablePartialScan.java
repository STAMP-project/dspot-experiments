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


import java.util.List;
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
public class TestRawAsyncTablePartialScan {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRawAsyncTablePartialScan.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[][] CQS = new byte[][]{ Bytes.toBytes("cq1"), Bytes.toBytes("cq2"), Bytes.toBytes("cq3") };

    private static int COUNT = 100;

    private static AsyncConnection CONN;

    private static AsyncTable<?> TABLE;

    @Test
    public void testBatchDoNotAllowPartial() throws InterruptedException, ExecutionException {
        // we set batch to 2 and max result size to 1, then server will only returns one result per call
        // but we should get 2 + 1 for every row.
        List<Result> results = TestRawAsyncTablePartialScan.TABLE.scanAll(new Scan().setBatch(2).setMaxResultSize(1)).get();
        Assert.assertEquals((2 * (TestRawAsyncTablePartialScan.COUNT)), results.size());
        for (int i = 0; i < (TestRawAsyncTablePartialScan.COUNT); i++) {
            Result firstTwo = results.get((2 * i));
            Assert.assertEquals(String.format("%02d", i), Bytes.toString(firstTwo.getRow()));
            Assert.assertEquals(2, firstTwo.size());
            Assert.assertEquals(i, Bytes.toInt(firstTwo.getValue(TestRawAsyncTablePartialScan.FAMILY, TestRawAsyncTablePartialScan.CQS[0])));
            Assert.assertEquals((2 * i), Bytes.toInt(firstTwo.getValue(TestRawAsyncTablePartialScan.FAMILY, TestRawAsyncTablePartialScan.CQS[1])));
            Result secondOne = results.get(((2 * i) + 1));
            Assert.assertEquals(String.format("%02d", i), Bytes.toString(secondOne.getRow()));
            Assert.assertEquals(1, secondOne.size());
            Assert.assertEquals((3 * i), Bytes.toInt(secondOne.getValue(TestRawAsyncTablePartialScan.FAMILY, TestRawAsyncTablePartialScan.CQS[2])));
        }
    }

    @Test
    public void testReversedBatchDoNotAllowPartial() throws InterruptedException, ExecutionException {
        // we set batch to 2 and max result size to 1, then server will only returns one result per call
        // but we should get 2 + 1 for every row.
        List<Result> results = TestRawAsyncTablePartialScan.TABLE.scanAll(new Scan().setBatch(2).setMaxResultSize(1).setReversed(true)).get();
        Assert.assertEquals((2 * (TestRawAsyncTablePartialScan.COUNT)), results.size());
        for (int i = 0; i < (TestRawAsyncTablePartialScan.COUNT); i++) {
            int row = ((TestRawAsyncTablePartialScan.COUNT) - i) - 1;
            Result firstTwo = results.get((2 * i));
            Assert.assertEquals(String.format("%02d", row), Bytes.toString(firstTwo.getRow()));
            Assert.assertEquals(2, firstTwo.size());
            Assert.assertEquals(row, Bytes.toInt(firstTwo.getValue(TestRawAsyncTablePartialScan.FAMILY, TestRawAsyncTablePartialScan.CQS[0])));
            Assert.assertEquals((2 * row), Bytes.toInt(firstTwo.getValue(TestRawAsyncTablePartialScan.FAMILY, TestRawAsyncTablePartialScan.CQS[1])));
            Result secondOne = results.get(((2 * i) + 1));
            Assert.assertEquals(String.format("%02d", row), Bytes.toString(secondOne.getRow()));
            Assert.assertEquals(1, secondOne.size());
            Assert.assertEquals((3 * row), Bytes.toInt(secondOne.getValue(TestRawAsyncTablePartialScan.FAMILY, TestRawAsyncTablePartialScan.CQS[2])));
        }
    }
}

