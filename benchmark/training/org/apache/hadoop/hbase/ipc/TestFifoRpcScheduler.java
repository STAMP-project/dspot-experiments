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


import RpcScheduler.Context;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.monitoring.MonitoredRPCHandlerImpl;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RPCTests.class, LargeTests.class })
public class TestFifoRpcScheduler {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFifoRpcScheduler.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFifoRpcScheduler.class);

    private AtomicInteger callExecutionCount;

    private final Context CONTEXT = new RpcScheduler.Context() {
        @Override
        public InetSocketAddress getListenerAddress() {
            return InetSocketAddress.createUnresolved("127.0.0.1", 1000);
        }
    };

    private Configuration conf;

    @Test
    public void testCallQueueInfo() throws IOException, InterruptedException {
        ThreadPoolExecutor rpcExecutor;
        RpcScheduler scheduler = new FifoRpcScheduler(conf, 1);
        scheduler.init(CONTEXT);
        // Set number of handlers to a minimum value
        disableHandlers(scheduler);
        int totalCallMethods = 30;
        int unableToDispatch = 0;
        for (int i = totalCallMethods; i > 0; i--) {
            CallRunner task = createMockTask();
            task.setStatus(new MonitoredRPCHandlerImpl());
            if (!(scheduler.dispatch(task))) {
                unableToDispatch++;
            }
            Thread.sleep(10);
        }
        CallQueueInfo callQueueInfo = scheduler.getCallQueueInfo();
        int executionCount = callExecutionCount.get();
        int callQueueSize = 0;
        for (String callQueueName : callQueueInfo.getCallQueueNames()) {
            for (String calledMethod : callQueueInfo.getCalledMethodNames(callQueueName)) {
                callQueueSize += callQueueInfo.getCallMethodCount(callQueueName, calledMethod);
            }
        }
        Assert.assertEquals((totalCallMethods - unableToDispatch), (callQueueSize + executionCount));
        scheduler.stop();
    }
}

