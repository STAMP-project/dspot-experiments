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


import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public class TestAsyncTableBatchRetryImmediately {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableBatchRetryImmediately.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUAL = Bytes.toBytes("cq");

    private static byte[] VALUE_PREFIX = new byte[768];

    private static int COUNT = 1000;

    private static AsyncConnection CONN;

    @Test
    public void test() {
        AsyncTable<?> table = TestAsyncTableBatchRetryImmediately.CONN.getTable(TestAsyncTableBatchRetryImmediately.TABLE_NAME);
        // if we do not deal with RetryImmediatelyException, we will timeout here since we need to retry
        // hundreds times.
        List<Get> gets = IntStream.range(0, TestAsyncTableBatchRetryImmediately.COUNT).mapToObj(( i) -> new Get(Bytes.toBytes(i))).collect(Collectors.toList());
        List<Result> results = table.getAll(gets).join();
        for (int i = 0; i < (TestAsyncTableBatchRetryImmediately.COUNT); i++) {
            byte[] value = results.get(i).getValue(TestAsyncTableBatchRetryImmediately.FAMILY, TestAsyncTableBatchRetryImmediately.QUAL);
            Assert.assertEquals(((TestAsyncTableBatchRetryImmediately.VALUE_PREFIX.length) + 4), value.length);
            Assert.assertArrayEquals(TestAsyncTableBatchRetryImmediately.VALUE_PREFIX, Arrays.copyOf(value, TestAsyncTableBatchRetryImmediately.VALUE_PREFIX.length));
            Assert.assertEquals(i, Bytes.toInt(value, TestAsyncTableBatchRetryImmediately.VALUE_PREFIX.length));
        }
    }
}

