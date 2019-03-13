/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.apache.hadoop.hbase.coprocessor;


import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestMetaTableMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetaTableMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMetaTableMetrics.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName NAME1 = TableName.valueOf("TestExampleMetaTableMetricsOne");

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static final ColumnFamilyDescriptor CFD = ColumnFamilyDescriptorBuilder.newBuilder(TestMetaTableMetrics.FAMILY).build();

    private static final int NUM_ROWS = 5;

    private static final String value = "foo";

    private static Configuration conf = null;

    private static int connectorPort = 61120;

    // verifies meta table metrics exist from jmx
    // for one table, there should be 5 MetaTable_table_<TableName> metrics.
    // such as:
    // [Time-limited test] example.TestMetaTableMetrics(204): ==
    // MetaTable_table_TestExampleMetaTableMetricsOne_request_count
    // [Time-limited test] example.TestMetaTableMetrics(204): ==
    // MetaTable_table_TestExampleMetaTableMetricsOne_request_mean_rate
    // [Time-limited test] example.TestMetaTableMetrics(204): ==
    // MetaTable_table_TestExampleMetaTableMetricsOne_request_1min_rate
    // [Time-limited test] example.TestMetaTableMetrics(204): ==
    // MetaTable_table_TestExampleMetaTableMetricsOne_request_5min_rate
    // [Time-limited test] example.TestMetaTableMetrics(204): ==
    // MetaTable_table_TestExampleMetaTableMetricsOne_request_15min_rate
    @Test
    public void test() throws IOException, InterruptedException {
        try (Table t = TestMetaTableMetrics.UTIL.getConnection().getTable(TestMetaTableMetrics.NAME1)) {
            writeData(t);
            // Flush the data
            TestMetaTableMetrics.UTIL.flush(TestMetaTableMetrics.NAME1);
            // Issue a compaction
            TestMetaTableMetrics.UTIL.compact(TestMetaTableMetrics.NAME1, true);
            Thread.sleep(2000);
        }
        Set<String> jmxMetrics = readJmxMetricsWithRetry();
        Assert.assertNotNull(jmxMetrics);
        long name1TableMetricsCount = jmxMetrics.stream().filter(( metric) -> metric.contains(("MetaTable_table_" + (TestMetaTableMetrics.NAME1)))).count();
        Assert.assertEquals(5L, name1TableMetricsCount);
        String putWithClientMetricNameRegex = "MetaTable_client_.+_put_request.*";
        long putWithClientMetricsCount = jmxMetrics.stream().filter(( metric) -> metric.matches(putWithClientMetricNameRegex)).count();
        Assert.assertEquals(5L, putWithClientMetricsCount);
    }
}

