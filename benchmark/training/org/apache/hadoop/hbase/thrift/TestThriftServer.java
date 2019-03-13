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
package org.apache.hadoop.hbase.thrift;


import Hbase.Iface;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.thrift.generated.Hbase;
import org.apache.hadoop.hbase.thrift.generated.IOError;
import org.apache.hadoop.hbase.thrift.generated.TCell;
import org.apache.hadoop.hbase.thrift.generated.TRowResult;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.thrift.ErrorThrowingGetObserver.ErrorType.values;


/**
 * Unit testing for ThriftServerRunner.HBaseServiceHandler, a part of the
 * org.apache.hadoop.hbase.thrift package.
 */
@Category({ ClientTests.class, LargeTests.class })
public class TestThriftServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftServer.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestThriftServer.class);

    private static final MetricsAssertHelper metricsHelper = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    protected static final int MAXVERSIONS = 3;

    // Static names for tables, columns, rows, and values
    private static ByteBuffer tableAname = TestThriftServer.asByteBuffer("tableA");

    private static ByteBuffer tableBname = TestThriftServer.asByteBuffer("tableB");

    private static ByteBuffer columnAname = TestThriftServer.asByteBuffer("columnA:");

    private static ByteBuffer columnAAname = TestThriftServer.asByteBuffer("columnA:A");

    private static ByteBuffer columnBname = TestThriftServer.asByteBuffer("columnB:");

    private static ByteBuffer rowAname = TestThriftServer.asByteBuffer("rowA");

    private static ByteBuffer rowBname = TestThriftServer.asByteBuffer("rowB");

    private static ByteBuffer valueAname = TestThriftServer.asByteBuffer("valueA");

    private static ByteBuffer valueBname = TestThriftServer.asByteBuffer("valueB");

    private static ByteBuffer valueCname = TestThriftServer.asByteBuffer("valueC");

    private static ByteBuffer valueDname = TestThriftServer.asByteBuffer("valueD");

    private static ByteBuffer valueEname = TestThriftServer.asByteBuffer(100L);

    @Rule
    public TestName name = new TestName();

    /**
     * Runs all of the tests under a single JUnit test method.  We
     * consolidate all testing to one method because HBaseClusterTestCase
     * is prone to OutOfMemoryExceptions when there are three or more
     * JUnit test methods.
     */
    @Test
    public void testAll() throws Exception {
        // Run all tests
        doTestTableCreateDrop();
        doTestThriftMetrics();
        doTestTableMutations();
        doTestTableTimestampsAndColumns();
        doTestTableScanners();
        doTestGetTableRegions();
        doTestFilterRegistration();
        doTestGetRegionInfo();
        doTestIncrements();
        TestThriftServer.doTestAppend();
        TestThriftServer.doTestCheckAndPut();
    }

    public static final class MySlowHBaseHandler extends ThriftHBaseServiceHandler implements Hbase.Iface {
        protected MySlowHBaseHandler(Configuration c) throws IOException {
            super(c, UserProvider.instantiate(c));
        }

        @Override
        public List<ByteBuffer> getTableNames() throws IOError {
            Threads.sleepWithoutInterrupt(3000);
            return super.getTableNames();
        }
    }

    @Test
    public void testMetricsWithException() throws Exception {
        String rowkey = "row1";
        String family = "f";
        String col = "c";
        // create a table which will throw exceptions for requests
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor tableDesc = new HTableDescriptor(tableName);
        tableDesc.addCoprocessor(ErrorThrowingGetObserver.class.getName());
        tableDesc.addFamily(new HColumnDescriptor(family));
        Table table = TestThriftServer.UTIL.createTable(tableDesc, null);
        long now = System.currentTimeMillis();
        table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(rowkey)).addColumn(Bytes.toBytes(family), Bytes.toBytes(col), now, Bytes.toBytes("val1")));
        Configuration conf = TestThriftServer.UTIL.getConfiguration();
        ThriftMetrics metrics = TestThriftServer.getMetrics(conf);
        ThriftHBaseServiceHandler hbaseHandler = new ThriftHBaseServiceHandler(TestThriftServer.UTIL.getConfiguration(), UserProvider.instantiate(TestThriftServer.UTIL.getConfiguration()));
        Hbase.Iface handler = HbaseHandlerMetricsProxy.newInstance(hbaseHandler, metrics, conf);
        ByteBuffer tTableName = TestThriftServer.asByteBuffer(tableName.getNameAsString());
        // check metrics increment with a successful get
        long preGetCounter = (TestThriftServer.metricsHelper.checkCounterExists("getRow_num_ops", metrics.getSource())) ? TestThriftServer.metricsHelper.getCounter("getRow_num_ops", metrics.getSource()) : 0;
        List<TRowResult> tRowResult = handler.getRow(tTableName, TestThriftServer.asByteBuffer(rowkey), null);
        Assert.assertEquals(1, tRowResult.size());
        TRowResult tResult = tRowResult.get(0);
        TCell expectedColumnValue = new TCell(TestThriftServer.asByteBuffer("val1"), now);
        Assert.assertArrayEquals(Bytes.toBytes(rowkey), tResult.getRow());
        Collection<TCell> returnedColumnValues = tResult.getColumns().values();
        Assert.assertEquals(1, returnedColumnValues.size());
        Assert.assertEquals(expectedColumnValue, returnedColumnValues.iterator().next());
        TestThriftServer.metricsHelper.assertCounter("getRow_num_ops", (preGetCounter + 1), metrics.getSource());
        // check metrics increment when the get throws each exception type
        for (ErrorThrowingGetObserver.ErrorType type : values()) {
            testExceptionType(handler, metrics, tTableName, rowkey, type);
        }
    }
}

