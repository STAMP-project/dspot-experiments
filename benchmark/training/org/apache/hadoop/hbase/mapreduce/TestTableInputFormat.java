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
package org.apache.hadoop.hbase.mapreduce;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.mapreduce.JobContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CompareOperator.EQUAL;
import static org.apache.hadoop.hbase.TableMapper.<init>;


/**
 * This tests the TableInputFormat and its recovery semantics
 */
@Category(LargeTests.class)
public class TestTableInputFormat {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableInputFormat.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableInputFormat.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static MiniMRCluster mrCluster;

    static final byte[] FAMILY = Bytes.toBytes("family");

    private static final byte[][] columns = new byte[][]{ TestTableInputFormat.FAMILY };

    /**
     * Run test assuming no errors using newer mapreduce api
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTableRecordReaderMapreduce() throws IOException, InterruptedException {
        Table table = TestTableInputFormat.createTable(Bytes.toBytes("table1-mr"));
        TestTableInputFormat.runTestMapreduce(table);
    }

    /**
     * Run test assuming Scanner IOException failure using newer mapreduce api
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTableRecordReaderScannerFailMapreduce() throws IOException, InterruptedException {
        Table htable = TestTableInputFormat.createIOEScannerTable(Bytes.toBytes("table2-mr"), 1);
        TestTableInputFormat.runTestMapreduce(htable);
    }

    /**
     * Run test assuming Scanner IOException failure using newer mapreduce api
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(expected = IOException.class)
    public void testTableRecordReaderScannerFailMapreduceTwice() throws IOException, InterruptedException {
        Table htable = TestTableInputFormat.createIOEScannerTable(Bytes.toBytes("table3-mr"), 2);
        TestTableInputFormat.runTestMapreduce(htable);
    }

    /**
     * Run test assuming NotServingRegionException using newer mapreduce api
     *
     * @throws InterruptedException
     * 		
     * @throws org.apache.hadoop.hbase.DoNotRetryIOException
     * 		
     */
    @Test
    public void testTableRecordReaderScannerTimeoutMapreduce() throws IOException, InterruptedException {
        Table htable = TestTableInputFormat.createDNRIOEScannerTable(Bytes.toBytes("table4-mr"), 1);
        TestTableInputFormat.runTestMapreduce(htable);
    }

    /**
     * Run test assuming NotServingRegionException using newer mapreduce api
     *
     * @throws InterruptedException
     * 		
     * @throws org.apache.hadoop.hbase.NotServingRegionException
     * 		
     */
    @Test(expected = NotServingRegionException.class)
    public void testTableRecordReaderScannerTimeoutMapreduceTwice() throws IOException, InterruptedException {
        Table htable = TestTableInputFormat.createDNRIOEScannerTable(Bytes.toBytes("table5-mr"), 2);
        TestTableInputFormat.runTestMapreduce(htable);
    }

    /**
     * Verify the example we present in javadocs on TableInputFormatBase
     */
    @Test
    public void testExtensionOfTableInputFormatBase() throws IOException, ClassNotFoundException, InterruptedException {
        TestTableInputFormat.LOG.info("testing use of an InputFormat taht extends InputFormatBase");
        final Table htable = TestTableInputFormat.createTable(Bytes.toBytes("exampleTable"), new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") });
        testInputFormat(TestTableInputFormat.ExampleTIF.class);
    }

    @Test
    public void testJobConfigurableExtensionOfTableInputFormatBase() throws IOException, ClassNotFoundException, InterruptedException {
        TestTableInputFormat.LOG.info(("testing use of an InputFormat taht extends InputFormatBase, " + "using JobConfigurable."));
        final Table htable = TestTableInputFormat.createTable(Bytes.toBytes("exampleJobConfigurableTable"), new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") });
        testInputFormat(TestTableInputFormat.ExampleJobConfigurableTIF.class);
    }

    @Test
    public void testDeprecatedExtensionOfTableInputFormatBase() throws IOException, ClassNotFoundException, InterruptedException {
        TestTableInputFormat.LOG.info(("testing use of an InputFormat taht extends InputFormatBase, " + "using the approach documented in 0.98."));
        final Table htable = TestTableInputFormat.createTable(Bytes.toBytes("exampleDeprecatedTable"), new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") });
        testInputFormat(TestTableInputFormat.ExampleDeprecatedTIF.class);
    }

    public static class ExampleVerifier extends TableMapper<NullWritable, NullWritable> {
        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException {
            for (Cell cell : value.listCells()) {
                context.getCounter(((TestTableInputFormat.class.getName()) + ":row"), Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength())).increment(1L);
                context.getCounter(((TestTableInputFormat.class.getName()) + ":family"), Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength())).increment(1L);
                context.getCounter(((TestTableInputFormat.class.getName()) + ":value"), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())).increment(1L);
            }
        }
    }

    public static class ExampleDeprecatedTIF extends TableInputFormatBase implements JobConfigurable {
        @Override
        public void configure(JobConf job) {
            try {
                Connection connection = ConnectionFactory.createConnection(job);
                Table exampleTable = connection.getTable(TableName.valueOf("exampleDeprecatedTable"));
                // mandatory
                initializeTable(connection, exampleTable.getName());
                byte[][] inputColumns = new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") };
                // optional
                Scan scan = new Scan();
                for (byte[] family : inputColumns) {
                    scan.addFamily(family);
                }
                Filter exampleFilter = new org.apache.hadoop.hbase.filter.RowFilter(EQUAL, new RegexStringComparator("aa.*"));
                scan.setFilter(exampleFilter);
                setScan(scan);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to configure for job.", exception);
            }
        }
    }

    public static class ExampleJobConfigurableTIF extends TableInputFormatBase implements JobConfigurable {
        @Override
        public void configure(JobConf job) {
            try {
                Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create(job));
                TableName tableName = TableName.valueOf("exampleJobConfigurableTable");
                // mandatory
                initializeTable(connection, tableName);
                byte[][] inputColumns = new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") };
                // optional
                Scan scan = new Scan();
                for (byte[] family : inputColumns) {
                    scan.addFamily(family);
                }
                Filter exampleFilter = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator("aa.*"));
                scan.setFilter(exampleFilter);
                setScan(scan);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to initialize.", exception);
            }
        }
    }

    public static class ExampleTIF extends TableInputFormatBase {
        @Override
        protected void initialize(JobContext job) throws IOException {
            Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create(job.getConfiguration()));
            TableName tableName = TableName.valueOf("exampleTable");
            // mandatory
            initializeTable(connection, tableName);
            byte[][] inputColumns = new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") };
            // optional
            Scan scan = new Scan();
            for (byte[] family : inputColumns) {
                scan.addFamily(family);
            }
            Filter exampleFilter = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator("aa.*"));
            scan.setFilter(exampleFilter);
            setScan(scan);
        }
    }
}

