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
package org.apache.hadoop.hbase.mapred;


import java.io.IOException;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CompareOperator.EQUAL;


/**
 * This tests the TableInputFormat and its recovery semantics
 */
@Category({ MapReduceTests.class, LargeTests.class })
public class TestTableInputFormat {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableInputFormat.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableInputFormat.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    static final byte[] FAMILY = Bytes.toBytes("family");

    private static final byte[][] columns = new byte[][]{ TestTableInputFormat.FAMILY };

    /**
     * Run test assuming no errors using mapred api.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTableRecordReader() throws IOException {
        Table table = TestTableInputFormat.createTable(Bytes.toBytes("table1"));
        TestTableInputFormat.runTestMapred(table);
    }

    /**
     * Run test assuming Scanner IOException failure using mapred api,
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTableRecordReaderScannerFail() throws IOException {
        Table htable = TestTableInputFormat.createIOEScannerTable(Bytes.toBytes("table2"), 1);
        TestTableInputFormat.runTestMapred(htable);
    }

    /**
     * Run test assuming Scanner IOException failure using mapred api,
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testTableRecordReaderScannerFailTwice() throws IOException {
        Table htable = TestTableInputFormat.createIOEScannerTable(Bytes.toBytes("table3"), 2);
        TestTableInputFormat.runTestMapred(htable);
    }

    /**
     * Run test assuming NotServingRegionException using mapred api.
     *
     * @throws org.apache.hadoop.hbase.DoNotRetryIOException
     * 		
     */
    @Test
    public void testTableRecordReaderScannerTimeout() throws IOException {
        Table htable = TestTableInputFormat.createDNRIOEScannerTable(Bytes.toBytes("table4"), 1);
        TestTableInputFormat.runTestMapred(htable);
    }

    /**
     * Run test assuming NotServingRegionException using mapred api.
     *
     * @throws org.apache.hadoop.hbase.DoNotRetryIOException
     * 		
     */
    @Test(expected = NotServingRegionException.class)
    public void testTableRecordReaderScannerTimeoutTwice() throws IOException {
        Table htable = TestTableInputFormat.createDNRIOEScannerTable(Bytes.toBytes("table5"), 2);
        TestTableInputFormat.runTestMapred(htable);
    }

    /**
     * Verify the example we present in javadocs on TableInputFormatBase
     */
    @Test
    public void testExtensionOfTableInputFormatBase() throws IOException {
        TestTableInputFormat.LOG.info("testing use of an InputFormat taht extends InputFormatBase");
        final Table table = TestTableInputFormat.createTable(Bytes.toBytes("exampleTable"), new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") });
        testInputFormat(TestTableInputFormat.ExampleTIF.class);
    }

    @Test
    public void testDeprecatedExtensionOfTableInputFormatBase() throws IOException {
        TestTableInputFormat.LOG.info(("testing use of an InputFormat taht extends InputFormatBase, " + "as it was given in 0.98."));
        final Table table = TestTableInputFormat.createTable(Bytes.toBytes("exampleDeprecatedTable"), new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") });
        testInputFormat(TestTableInputFormat.ExampleDeprecatedTIF.class);
    }

    @Test
    public void testJobConfigurableExtensionOfTableInputFormatBase() throws IOException {
        TestTableInputFormat.LOG.info(("testing use of an InputFormat taht extends InputFormatBase, " + "using JobConfigurable."));
        final Table table = TestTableInputFormat.createTable(Bytes.toBytes("exampleJobConfigurableTable"), new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") });
        testInputFormat(TestTableInputFormat.ExampleJobConfigurableTIF.class);
    }

    public static class ExampleVerifier implements TableMap<NullWritable, NullWritable> {
        @Override
        public void configure(JobConf conf) {
        }

        @Override
        public void map(ImmutableBytesWritable key, Result value, OutputCollector<NullWritable, NullWritable> output, Reporter reporter) throws IOException {
            for (Cell cell : value.listCells()) {
                reporter.getCounter(((TestTableInputFormat.class.getName()) + ":row"), Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength())).increment(1L);
                reporter.getCounter(((TestTableInputFormat.class.getName()) + ":family"), Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength())).increment(1L);
                reporter.getCounter(((TestTableInputFormat.class.getName()) + ":value"), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())).increment(1L);
            }
        }

        @Override
        public void close() {
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
                // mandatory
                setInputColumns(inputColumns);
                Filter exampleFilter = new org.apache.hadoop.hbase.filter.RowFilter(EQUAL, new RegexStringComparator("aa.*"));
                // optional
                setRowFilter(exampleFilter);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to configure for job.", exception);
            }
        }
    }

    public static class ExampleJobConfigurableTIF extends TestTableInputFormat.ExampleTIF implements JobConfigurable {
        @Override
        public void configure(JobConf job) {
            try {
                initialize(job);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to initialize.", exception);
            }
        }

        @Override
        protected void initialize(JobConf job) throws IOException {
            initialize(job, "exampleJobConfigurableTable");
        }
    }

    public static class ExampleTIF extends TableInputFormatBase {
        @Override
        protected void initialize(JobConf job) throws IOException {
            initialize(job, "exampleTable");
        }

        protected void initialize(JobConf job, String table) throws IOException {
            Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create(job));
            TableName tableName = TableName.valueOf(table);
            // mandatory
            initializeTable(connection, tableName);
            byte[][] inputColumns = new byte[][]{ Bytes.toBytes("columnA"), Bytes.toBytes("columnB") };
            // mandatory
            setInputColumns(inputColumns);
            Filter exampleFilter = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator("aa.*"));
            // optional
            setRowFilter(exampleFilter);
        }
    }
}

