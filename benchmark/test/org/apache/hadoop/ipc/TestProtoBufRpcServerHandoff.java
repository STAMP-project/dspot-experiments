/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ipc;


import RPC.Server;
import TestProtos.SleepRequestProto2;
import TestProtos.SleepResponseProto2;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.protobuf.TestProtos;
import org.apache.hadoop.ipc.protobuf.TestRpcServiceProtos.TestProtobufRpcHandoffProto;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestProtoBufRpcServerHandoff {
    public static final Logger LOG = LoggerFactory.getLogger(TestProtoBufRpcServerHandoff.class);

    @Test(timeout = 20000)
    public void test() throws Exception {
        Configuration conf = new Configuration();
        TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffServer serverImpl = new TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffServer();
        BlockingService blockingService = TestProtobufRpcHandoffProto.newReflectiveBlockingService(serverImpl);
        RPC.setProtocolEngine(conf, TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol.class, ProtobufRpcEngine.class);
        RPC.Server server = // Num Handlers explicitly set to 1 for test.
        setProtocol(TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol.class).setInstance(blockingService).setVerbose(true).setNumHandlers(1).build();
        server.start();
        InetSocketAddress address = server.getListenerAddress();
        long serverStartTime = System.currentTimeMillis();
        TestProtoBufRpcServerHandoff.LOG.info(((("Server started at: " + address) + " at time: ") + serverStartTime));
        final TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol client = RPC.getProxy(TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol.class, 1, address, conf);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletionService<TestProtoBufRpcServerHandoff.ClientInvocationCallable> completionService = new ExecutorCompletionService<TestProtoBufRpcServerHandoff.ClientInvocationCallable>(executorService);
        completionService.submit(new TestProtoBufRpcServerHandoff.ClientInvocationCallable(client, 5000L));
        completionService.submit(new TestProtoBufRpcServerHandoff.ClientInvocationCallable(client, 5000L));
        long submitTime = System.currentTimeMillis();
        Future<TestProtoBufRpcServerHandoff.ClientInvocationCallable> future1 = completionService.take();
        Future<TestProtoBufRpcServerHandoff.ClientInvocationCallable> future2 = completionService.take();
        TestProtoBufRpcServerHandoff.ClientInvocationCallable callable1 = future1.get();
        TestProtoBufRpcServerHandoff.ClientInvocationCallable callable2 = future2.get();
        TestProtoBufRpcServerHandoff.LOG.info(callable1.toString());
        TestProtoBufRpcServerHandoff.LOG.info(callable2.toString());
        // Ensure the 5 second sleep responses are within a reasonable time of each
        // other.
        Assert.assertTrue(((Math.abs(((callable1.endTime) - (callable2.endTime)))) < 2000L));
        Assert.assertTrue((((System.currentTimeMillis()) - submitTime) < 7000L));
    }

    private static class ClientInvocationCallable implements Callable<TestProtoBufRpcServerHandoff.ClientInvocationCallable> {
        final TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol client;

        final long sleepTime;

        SleepResponseProto2 result;

        long startTime;

        long endTime;

        private ClientInvocationCallable(TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol client, long sleepTime) {
            this.client = client;
            this.sleepTime = sleepTime;
        }

        @Override
        public TestProtoBufRpcServerHandoff.ClientInvocationCallable call() throws Exception {
            startTime = System.currentTimeMillis();
            result = client.sleep(null, SleepRequestProto2.newBuilder().setSleepTime(sleepTime).build());
            endTime = System.currentTimeMillis();
            return this;
        }

        @Override
        public String toString() {
            return ((("startTime=" + (startTime)) + ", endTime=") + (endTime)) + ((result) != null ? ((", result.receiveTime=" + (result.getReceiveTime())) + ", result.responseTime=") + (result.getResponseTime()) : "");
        }
    }

    @ProtocolInfo(protocolName = "org.apache.hadoop.ipc.TestProtoBufRpcServerHandoff$TestProtoBufRpcServerHandoffProtocol", protocolVersion = 1)
    public interface TestProtoBufRpcServerHandoffProtocol extends TestProtobufRpcHandoffProto.BlockingInterface {}

    public static class TestProtoBufRpcServerHandoffServer implements TestProtoBufRpcServerHandoff.TestProtoBufRpcServerHandoffProtocol {
        @Override
        public SleepResponseProto2 sleep(RpcController controller, TestProtos.SleepRequestProto2 request) throws ServiceException {
            final long startTime = System.currentTimeMillis();
            final ProtobufRpcEngineCallback callback = ProtobufRpcEngine.Server.registerForDeferredResponse();
            final long sleepTime = request.getSleepTime();
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    callback.setResponse(SleepResponseProto2.newBuilder().setReceiveTime(startTime).setResponseTime(System.currentTimeMillis()).build());
                }
            }.start();
            return null;
        }
    }
}

