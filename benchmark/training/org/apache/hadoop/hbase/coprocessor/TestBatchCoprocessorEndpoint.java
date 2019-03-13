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
package org.apache.hadoop.hbase.coprocessor;


import ColumnAggregationNullResponseSumRequest.Builder;
import ColumnAggregationWithErrorsProtos.ColumnAggregationServiceWithErrors;
import com.google.protobuf.ByteString;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.ColumnAggregationProtos.SumResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.ColumnAggregationWithErrorsProtos.ColumnAggregationWithErrorsSumRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.ColumnAggregationWithErrorsProtos.ColumnAggregationWithErrorsSumResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.ColumnAggregationWithNullResponseProtos.ColumnAggregationNullResponseSumRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.ColumnAggregationWithNullResponseProtos.ColumnAggregationNullResponseSumResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.ColumnAggregationWithNullResponseProtos.ColumnAggregationServiceNullResponse;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestEndpoint: test cases to verify the batch execution of coprocessor Endpoint
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestBatchCoprocessorEndpoint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBatchCoprocessorEndpoint.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBatchCoprocessorEndpoint.class);

    private static final TableName TEST_TABLE = TableName.valueOf("TestTable");

    private static final byte[] TEST_FAMILY = Bytes.toBytes("TestFamily");

    private static final byte[] TEST_QUALIFIER = Bytes.toBytes("TestQualifier");

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static final int ROWSIZE = 20;

    private static final int rowSeperator1 = 5;

    private static final int rowSeperator2 = 12;

    private static byte[][] ROWS = TestBatchCoprocessorEndpoint.makeN(TestBatchCoprocessorEndpoint.ROW, TestBatchCoprocessorEndpoint.ROWSIZE);

    private static HBaseTestingUtility util = new HBaseTestingUtility();

    @Test
    public void testAggregationNullResponse() throws Throwable {
        Table table = TestBatchCoprocessorEndpoint.util.getConnection().getTable(TestBatchCoprocessorEndpoint.TEST_TABLE);
        ColumnAggregationNullResponseSumRequest.Builder builder = ColumnAggregationNullResponseSumRequest.newBuilder();
        builder.setFamily(ByteString.copyFrom(TestBatchCoprocessorEndpoint.TEST_FAMILY));
        if (((TestBatchCoprocessorEndpoint.TEST_QUALIFIER) != null) && ((TestBatchCoprocessorEndpoint.TEST_QUALIFIER.length) > 0)) {
            builder.setQualifier(ByteString.copyFrom(TestBatchCoprocessorEndpoint.TEST_QUALIFIER));
        }
        Map<byte[], ColumnAggregationNullResponseSumResponse> results = table.batchCoprocessorService(ColumnAggregationServiceNullResponse.getDescriptor().findMethodByName("sum"), builder.build(), TestBatchCoprocessorEndpoint.ROWS[0], TestBatchCoprocessorEndpoint.ROWS[((TestBatchCoprocessorEndpoint.ROWS.length) - 1)], ColumnAggregationNullResponseSumResponse.getDefaultInstance());
        int sumResult = 0;
        int expectedResult = 0;
        for (Map.Entry<byte[], ColumnAggregationNullResponseSumResponse> e : results.entrySet()) {
            TestBatchCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue().getSum())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue().getSum();
        }
        for (int i = 0; i < (TestBatchCoprocessorEndpoint.rowSeperator2); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        table.close();
    }

    @Test
    public void testAggregationWithReturnValue() throws Throwable {
        Table table = TestBatchCoprocessorEndpoint.util.getConnection().getTable(TestBatchCoprocessorEndpoint.TEST_TABLE);
        Map<byte[], SumResponse> results = sum(table, TestBatchCoprocessorEndpoint.TEST_FAMILY, TestBatchCoprocessorEndpoint.TEST_QUALIFIER, TestBatchCoprocessorEndpoint.ROWS[0], TestBatchCoprocessorEndpoint.ROWS[((TestBatchCoprocessorEndpoint.ROWS.length) - 1)]);
        int sumResult = 0;
        int expectedResult = 0;
        for (Map.Entry<byte[], SumResponse> e : results.entrySet()) {
            TestBatchCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue().getSum())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue().getSum();
        }
        for (int i = 0; i < (TestBatchCoprocessorEndpoint.ROWSIZE); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        results.clear();
        // scan: for region 2 and region 3
        results = sum(table, TestBatchCoprocessorEndpoint.TEST_FAMILY, TestBatchCoprocessorEndpoint.TEST_QUALIFIER, TestBatchCoprocessorEndpoint.ROWS[TestBatchCoprocessorEndpoint.rowSeperator1], TestBatchCoprocessorEndpoint.ROWS[((TestBatchCoprocessorEndpoint.ROWS.length) - 1)]);
        sumResult = 0;
        expectedResult = 0;
        for (Map.Entry<byte[], SumResponse> e : results.entrySet()) {
            TestBatchCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue().getSum())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue().getSum();
        }
        for (int i = TestBatchCoprocessorEndpoint.rowSeperator1; i < (TestBatchCoprocessorEndpoint.ROWSIZE); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        table.close();
    }

    @Test
    public void testAggregation() throws Throwable {
        Table table = TestBatchCoprocessorEndpoint.util.getConnection().getTable(TestBatchCoprocessorEndpoint.TEST_TABLE);
        Map<byte[], SumResponse> results = sum(table, TestBatchCoprocessorEndpoint.TEST_FAMILY, TestBatchCoprocessorEndpoint.TEST_QUALIFIER, TestBatchCoprocessorEndpoint.ROWS[0], TestBatchCoprocessorEndpoint.ROWS[((TestBatchCoprocessorEndpoint.ROWS.length) - 1)]);
        int sumResult = 0;
        int expectedResult = 0;
        for (Map.Entry<byte[], SumResponse> e : results.entrySet()) {
            TestBatchCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue().getSum())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue().getSum();
        }
        for (int i = 0; i < (TestBatchCoprocessorEndpoint.ROWSIZE); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        // scan: for region 2 and region 3
        results = sum(table, TestBatchCoprocessorEndpoint.TEST_FAMILY, TestBatchCoprocessorEndpoint.TEST_QUALIFIER, TestBatchCoprocessorEndpoint.ROWS[TestBatchCoprocessorEndpoint.rowSeperator1], TestBatchCoprocessorEndpoint.ROWS[((TestBatchCoprocessorEndpoint.ROWS.length) - 1)]);
        sumResult = 0;
        expectedResult = 0;
        for (Map.Entry<byte[], SumResponse> e : results.entrySet()) {
            TestBatchCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue().getSum())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue().getSum();
        }
        for (int i = TestBatchCoprocessorEndpoint.rowSeperator1; i < (TestBatchCoprocessorEndpoint.ROWSIZE); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        table.close();
    }

    @Test
    public void testAggregationWithErrors() throws Throwable {
        Table table = TestBatchCoprocessorEndpoint.util.getConnection().getTable(TestBatchCoprocessorEndpoint.TEST_TABLE);
        final Map<byte[], ColumnAggregationWithErrorsSumResponse> results = Collections.synchronizedMap(new TreeMap<byte[], ColumnAggregationWithErrorsSumResponse>(Bytes.BYTES_COMPARATOR));
        ColumnAggregationWithErrorsSumRequest.Builder builder = ColumnAggregationWithErrorsSumRequest.newBuilder();
        builder.setFamily(ByteString.copyFrom(TestBatchCoprocessorEndpoint.TEST_FAMILY));
        if (((TestBatchCoprocessorEndpoint.TEST_QUALIFIER) != null) && ((TestBatchCoprocessorEndpoint.TEST_QUALIFIER.length) > 0)) {
            builder.setQualifier(ByteString.copyFrom(TestBatchCoprocessorEndpoint.TEST_QUALIFIER));
        }
        boolean hasError = false;
        try {
            table.batchCoprocessorService(ColumnAggregationServiceWithErrors.getDescriptor().findMethodByName("sum"), builder.build(), TestBatchCoprocessorEndpoint.ROWS[0], TestBatchCoprocessorEndpoint.ROWS[((TestBatchCoprocessorEndpoint.ROWS.length) - 1)], ColumnAggregationWithErrorsSumResponse.getDefaultInstance(), new Batch.Callback<ColumnAggregationWithErrorsSumResponse>() {
                @Override
                public void update(byte[] region, byte[] row, ColumnAggregationWithErrorsSumResponse result) {
                    results.put(region, result);
                }
            });
        } catch (Throwable t) {
            TestBatchCoprocessorEndpoint.LOG.info("Exceptions in coprocessor service", t);
            hasError = true;
        }
        int sumResult = 0;
        int expectedResult = 0;
        for (Map.Entry<byte[], ColumnAggregationWithErrorsSumResponse> e : results.entrySet()) {
            TestBatchCoprocessorEndpoint.LOG.info(((("Got value " + (e.getValue().getSum())) + " for region ") + (Bytes.toStringBinary(e.getKey()))));
            sumResult += e.getValue().getSum();
        }
        for (int i = 0; i < (TestBatchCoprocessorEndpoint.rowSeperator2); i++) {
            expectedResult += i;
        }
        Assert.assertEquals("Invalid result", expectedResult, sumResult);
        Assert.assertTrue(hasError);
        table.close();
    }
}

