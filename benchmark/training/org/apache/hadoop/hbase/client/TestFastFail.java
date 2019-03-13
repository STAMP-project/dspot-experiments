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


import HConstants.HBASE_CLIENT_FAST_FAIL_INTERCEPTOR_IMPL;
import HConstants.HBASE_CLIENT_FAST_FAIL_MODE_ENABLED;
import HConstants.HBASE_CLIENT_FAST_FAIL_THREASHOLD_MS;
import HConstants.HBASE_CLIENT_OPERATION_TIMEOUT;
import HConstants.HBASE_CLIENT_PAUSE;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ipc.SimpleRpcScheduler;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class, ClientTests.class })
public class TestFastFail {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFastFail.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFastFail.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static final Random random = new Random();

    private static int SLAVES = 1;

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static final int SLEEPTIME = 5000;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCallQueueTooBigExceptionDoesntTriggerPffe() throws Exception {
        Admin admin = TestFastFail.TEST_UTIL.getAdmin();
        final String tableName = name.getMethodName();
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(Bytes.toBytes(tableName)));
        desc.addFamily(new HColumnDescriptor(TestFastFail.FAMILY));
        admin.createTable(desc, Bytes.toBytes("aaaa"), Bytes.toBytes("zzzz"), 3);
        Configuration conf = TestFastFail.TEST_UTIL.getConfiguration();
        conf.setLong(HBASE_CLIENT_OPERATION_TIMEOUT, 100);
        conf.setInt(HBASE_CLIENT_PAUSE, 500);
        conf.setInt(HBASE_CLIENT_RETRIES_NUMBER, 1);
        conf.setBoolean(HBASE_CLIENT_FAST_FAIL_MODE_ENABLED, true);
        conf.setLong(HBASE_CLIENT_FAST_FAIL_THREASHOLD_MS, 0);
        conf.setClass(HBASE_CLIENT_FAST_FAIL_INTERCEPTOR_IMPL, TestFastFail.CallQueueTooBigPffeInterceptor.class, PreemptiveFastFailInterceptor.class);
        final Connection connection = ConnectionFactory.createConnection(conf);
        // Set max call queues size to 0
        SimpleRpcScheduler srs = ((SimpleRpcScheduler) (TestFastFail.TEST_UTIL.getHBaseCluster().getRegionServer(0).getRpcServer().getScheduler()));
        Configuration newConf = HBaseConfiguration.create(TestFastFail.TEST_UTIL.getConfiguration());
        newConf.setInt("hbase.ipc.server.max.callqueue.length", 0);
        srs.onConfigurationChange(newConf);
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get get = new Get(new byte[1]);
            table.get(get);
        } catch (Throwable ex) {
        }
        Assert.assertEquals(("We should have not entered PFFE mode on CQTBE, but we did;" + " number of times this mode should have been entered:"), 0, TestFastFail.CallQueueTooBigPffeInterceptor.numCallQueueTooBig.get());
        newConf = HBaseConfiguration.create(TestFastFail.TEST_UTIL.getConfiguration());
        newConf.setInt("hbase.ipc.server.max.callqueue.length", 250);
        srs.onConfigurationChange(newConf);
    }

    public static class MyPreemptiveFastFailInterceptor extends PreemptiveFastFailInterceptor {
        public static AtomicInteger numBraveSouls = new AtomicInteger();

        @Override
        protected boolean shouldRetryInspiteOfFastFail(FailureInfo fInfo) {
            boolean ret = super.shouldRetryInspiteOfFastFail(fInfo);
            if (ret)
                TestFastFail.MyPreemptiveFastFailInterceptor.numBraveSouls.addAndGet(1);

            return ret;
        }

        public MyPreemptiveFastFailInterceptor(Configuration conf) {
            super(conf);
        }
    }

    public static class CallQueueTooBigPffeInterceptor extends PreemptiveFastFailInterceptor {
        public static AtomicInteger numCallQueueTooBig = new AtomicInteger();

        @Override
        protected void handleFailureToServer(ServerName serverName, Throwable t) {
            super.handleFailureToServer(serverName, t);
            TestFastFail.CallQueueTooBigPffeInterceptor.numCallQueueTooBig.incrementAndGet();
        }

        public CallQueueTooBigPffeInterceptor(Configuration conf) {
            super(conf);
        }
    }
}

