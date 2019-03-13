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
package org.apache.hadoop.hbase.ipc;


import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.MasterRpcServices;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RPCTests.class, LargeTests.class })
public class TestMasterFifoRpcScheduler {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterFifoRpcScheduler.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterFifoRpcScheduler.class);

    private static final String REGION_SERVER_REPORT = "RegionServerReport";

    private static final String OTHER = "Other";

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testMasterRpcScheduler() {
        HMaster master = TestMasterFifoRpcScheduler.TEST_UTIL.getHBaseCluster().getMaster();
        MasterRpcServices masterRpcServices = master.getMasterRpcServices();
        RpcScheduler masterRpcScheduler = masterRpcServices.getRpcScheduler();
        Assert.assertTrue((masterRpcScheduler instanceof MasterFifoRpcScheduler));
    }

    @Test
    public void testCallQueueInfo() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        AtomicInteger callExecutionCount = new AtomicInteger(0);
        RpcScheduler scheduler = new TestMasterFifoRpcScheduler.MockMasterFifoRpcScheduler(conf, 2, 1);
        scheduler.start();
        int totalCallMethods = 30;
        int unableToDispatch = 0;
        for (int i = totalCallMethods; i > 0; i--) {
            CallRunner task = createMockTask(callExecutionCount, (i < 20));
            if (!(scheduler.dispatch(task))) {
                unableToDispatch++;
            }
            Thread.sleep(10);
        }
        CallQueueInfo callQueueInfo = scheduler.getCallQueueInfo();
        int executionCount = callExecutionCount.get();
        String expectedQueueName = "Master Fifo Queue";
        Assert.assertEquals(1, callQueueInfo.getCallQueueNames().size());
        long callQueueSize = 0;
        for (String queueName : callQueueInfo.getCallQueueNames()) {
            Assert.assertEquals(expectedQueueName, queueName);
            Set<String> methodNames = callQueueInfo.getCalledMethodNames(queueName);
            if ((methodNames.size()) == 2) {
                Assert.assertTrue(methodNames.contains(TestMasterFifoRpcScheduler.REGION_SERVER_REPORT));
                Assert.assertTrue(methodNames.contains(TestMasterFifoRpcScheduler.OTHER));
            }
            for (String methodName : callQueueInfo.getCalledMethodNames(queueName)) {
                callQueueSize += callQueueInfo.getCallMethodCount(queueName, methodName);
            }
        }
        Assert.assertEquals((totalCallMethods - unableToDispatch), (callQueueSize + executionCount));
        scheduler.stop();
    }

    private static class MockMasterFifoRpcScheduler extends MasterFifoRpcScheduler {
        public MockMasterFifoRpcScheduler(Configuration conf, int callHandlerCount, int rsReportHandlerCount) {
            super(conf, callHandlerCount, rsReportHandlerCount);
        }

        /**
         * Override this method because we can't mock a Descriptors.MethodDescriptor
         */
        @Override
        protected String getCallMethod(final CallRunner task) {
            RpcCall call = task.getRpcCall();
            if ((call.getHeader()) != null) {
                return call.getHeader().getMethodName();
            }
            return null;
        }
    }
}

