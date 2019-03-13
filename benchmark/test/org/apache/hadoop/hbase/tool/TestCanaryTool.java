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
package org.apache.hadoop.hbase.tool;


import Canary.RegionStdOutSink;
import HConstants.HBASE_CANARY_READ_RAW_SCAN_KEY;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@Category({ MediumTests.class })
public class TestCanaryTool {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCanaryTool.class);

    private HBaseTestingUtility testingUtility;

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final byte[] COLUMN = Bytes.toBytes("col");

    @Rule
    public TestName name = new TestName();

    @Mock
    Appender mockAppender;

    @Test
    public void testBasicZookeeperCanaryWorks() throws Exception {
        final String[] args = new String[]{ "-t", "10000", "-zookeeper" };
        testZookeeperCanaryWithArgs(args);
    }

    @Test
    public void testZookeeperCanaryPermittedFailuresArgumentWorks() throws Exception {
        final String[] args = new String[]{ "-t", "10000", "-zookeeper", "-treatFailureAsError", "-permittedZookeeperFailures", "1" };
        testZookeeperCanaryWithArgs(args);
    }

    @Test
    public void testBasicCanaryWorks() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = testingUtility.createTable(tableName, new byte[][]{ TestCanaryTool.FAMILY });
        // insert some test rows
        for (int i = 0; i < 1000; i++) {
            byte[] iBytes = Bytes.toBytes(i);
            Put p = new Put(iBytes);
            p.addColumn(TestCanaryTool.FAMILY, TestCanaryTool.COLUMN, iBytes);
            table.put(p);
        }
        ExecutorService executor = new ScheduledThreadPoolExecutor(1);
        Canary.RegionStdOutSink sink = Mockito.spy(new Canary.RegionStdOutSink());
        Canary canary = new Canary(executor, sink);
        String[] args = new String[]{ "-writeSniffing", "-t", "10000", tableName.getNameAsString() };
        Assert.assertEquals(0, ToolRunner.run(testingUtility.getConfiguration(), canary, args));
        Assert.assertEquals("verify no read error count", 0, canary.getReadFailures().size());
        Assert.assertEquals("verify no write error count", 0, canary.getWriteFailures().size());
        Mockito.verify(sink, Mockito.atLeastOnce()).publishReadTiming(ArgumentMatchers.isA(ServerName.class), ArgumentMatchers.isA(RegionInfo.class), ArgumentMatchers.isA(ColumnFamilyDescriptor.class), ArgumentMatchers.anyLong());
    }

    // no table created, so there should be no regions
    @Test
    public void testRegionserverNoRegions() throws Exception {
        runRegionserverCanary();
        Mockito.verify(mockAppender).doAppend(ArgumentMatchers.argThat(new ArgumentMatcher<LoggingEvent>() {
            @Override
            public boolean matches(LoggingEvent argument) {
                return argument.getRenderedMessage().contains("Regionserver not serving any regions");
            }
        }));
    }

    // by creating a table, there shouldn't be any region servers not serving any regions
    @Test
    public void testRegionserverWithRegions() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testingUtility.createTable(tableName, new byte[][]{ TestCanaryTool.FAMILY });
        runRegionserverCanary();
        Mockito.verify(mockAppender, Mockito.never()).doAppend(ArgumentMatchers.argThat(new ArgumentMatcher<LoggingEvent>() {
            @Override
            public boolean matches(LoggingEvent argument) {
                return argument.getRenderedMessage().contains("Regionserver not serving any regions");
            }
        }));
    }

    @Test
    public void testRawScanConfig() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = testingUtility.createTable(tableName, new byte[][]{ TestCanaryTool.FAMILY });
        // insert some test rows
        for (int i = 0; i < 1000; i++) {
            byte[] iBytes = Bytes.toBytes(i);
            Put p = new Put(iBytes);
            p.addColumn(TestCanaryTool.FAMILY, TestCanaryTool.COLUMN, iBytes);
            table.put(p);
        }
        ExecutorService executor = new ScheduledThreadPoolExecutor(1);
        Canary.RegionStdOutSink sink = Mockito.spy(new Canary.RegionStdOutSink());
        Canary canary = new Canary(executor, sink);
        String[] args = new String[]{ "-t", "10000", name.getMethodName() };
        Configuration conf = new Configuration(testingUtility.getConfiguration());
        conf.setBoolean(HBASE_CANARY_READ_RAW_SCAN_KEY, true);
        Assert.assertEquals(0, ToolRunner.run(conf, canary, args));
        Mockito.verify(sink, Mockito.atLeastOnce()).publishReadTiming(ArgumentMatchers.isA(ServerName.class), ArgumentMatchers.isA(RegionInfo.class), ArgumentMatchers.isA(ColumnFamilyDescriptor.class), ArgumentMatchers.anyLong());
        Assert.assertEquals("verify no read error count", 0, canary.getReadFailures().size());
    }
}

