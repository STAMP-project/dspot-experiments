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


import MetricsConnection.CLIENT_SIDE_METRICS_ENABLED_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SmallTests.class, ClientTests.class })
public class TestMultiActionMetricsFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiActionMetricsFromClient.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("test_table");

    private static final byte[] FAMILY = Bytes.toBytes("fam1");

    private static final byte[] QUALIFIER = Bytes.toBytes("qual");

    @Test
    public void testMultiMetrics() throws Exception {
        Configuration conf = new Configuration(TestMultiActionMetricsFromClient.TEST_UTIL.getConfiguration());
        conf.set(CLIENT_SIDE_METRICS_ENABLED_KEY, "true");
        ConnectionImplementation conn = ((ConnectionImplementation) (ConnectionFactory.createConnection(conf)));
        try {
            BufferedMutator mutator = conn.getBufferedMutator(TestMultiActionMetricsFromClient.TABLE_NAME);
            byte[][] keys = new byte[][]{ Bytes.toBytes("aaa"), Bytes.toBytes("mmm"), Bytes.toBytes("zzz") };
            for (byte[] key : keys) {
                Put p = new Put(key);
                p.addColumn(TestMultiActionMetricsFromClient.FAMILY, TestMultiActionMetricsFromClient.QUALIFIER, Bytes.toBytes(10));
                mutator.mutate(p);
            }
            mutator.flush();
            mutator.close();
            MetricsConnection metrics = conn.getConnectionMetrics();
            Assert.assertEquals(1, metrics.multiTracker.reqHist.getCount());
            Assert.assertEquals(3, metrics.numActionsPerServerHist.getSnapshot().getMean(), 1.0E-15);
            Assert.assertEquals(1, metrics.numActionsPerServerHist.getCount());
        } finally {
            conn.close();
        }
    }
}

