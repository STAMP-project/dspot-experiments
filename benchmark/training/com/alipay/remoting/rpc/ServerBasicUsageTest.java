/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.remoting.rpc;


import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.PortScan;
import com.alipay.remoting.rpc.common.RequestBody;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;
import com.alipay.remoting.rpc.common.SimpleServerUserProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic usage test of Rpc Server invoke apis
 *
 * @author xiaomin.cxm
 * @version $Id: ServerBasicUsageTest.java, v 0.1 Apr 14, 2016 10:00:48 AM xiaomin.cxm Exp $
 */
public class ServerBasicUsageTest {
    static Logger logger = LoggerFactory.getLogger(BasicUsageTest.class);

    BoltServer server;

    RpcClient client;

    int port = PortScan.select();

    String ip = "127.0.0.1";

    String addr = "127.0.0.1:" + (port);

    int invokeTimes = 5;

    SimpleServerUserProcessor serverUserProcessor = new SimpleServerUserProcessor();

    SimpleClientUserProcessor clientUserProcessor = new SimpleClientUserProcessor();

    CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

    CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

    DISCONNECTEventProcessor serverDisConnectProcessor = new DISCONNECTEventProcessor();

    @Test
    public void testOneway() throws RemotingException, InterruptedException {
        client.getConnection(addr, 1000);
        RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                // only when client invoked, the remote address can be get by UserProcessor
                // otherwise, please use ConnectionEventProcessor
                String remoteAddr = serverUserProcessor.getRemoteAddr();
                Assert.assertNull(remoteAddr);
                remoteAddr = serverConnectProcessor.getRemoteAddr();
                Assert.assertNotNull(remoteAddr);
                server.getRpcServer().oneway(remoteAddr, req);
                Thread.sleep(100);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in oneway!";
                ServerBasicUsageTest.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(0, serverUserProcessor.getInvokeTimes());
        Assert.assertEquals(invokeTimes, clientUserProcessor.getInvokeTimes());
    }

    @Test
    public void testSync() throws RemotingException, InterruptedException {
        client.getConnection(addr, 1000);
        RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                // only when client invoked, the remote address can be get by UserProcessor
                // otherwise, please use ConnectionEventProcessor
                String remoteAddr = serverUserProcessor.getRemoteAddr();
                Assert.assertNull(remoteAddr);
                remoteAddr = serverConnectProcessor.getRemoteAddr();
                Assert.assertNotNull(remoteAddr);
                String clientres = ((String) (server.getRpcServer().invokeSync(remoteAddr, req, 1000)));
                Assert.assertEquals(clientres, RequestBody.DEFAULT_CLIENT_RETURN_STR);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in sync!";
                ServerBasicUsageTest.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(0, serverUserProcessor.getInvokeTimes());
        Assert.assertEquals(invokeTimes, clientUserProcessor.getInvokeTimes());
    }

    @Test
    public void testFuture() throws RemotingException, InterruptedException {
        client.getConnection(addr, 1000);
        RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                // only when client invoked, the remote address can be get by UserProcessor
                // otherwise, please use ConnectionEventProcessor
                String remoteAddr = serverUserProcessor.getRemoteAddr();
                Assert.assertNull(remoteAddr);
                remoteAddr = serverConnectProcessor.getRemoteAddr();
                Assert.assertNotNull(remoteAddr);
                RpcResponseFuture future = server.getRpcServer().invokeWithFuture(remoteAddr, req, 1000);
                String clientres = ((String) (future.get(1000)));
                Assert.assertEquals(clientres, RequestBody.DEFAULT_CLIENT_RETURN_STR);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in future!";
                ServerBasicUsageTest.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(0, serverUserProcessor.getInvokeTimes());
        Assert.assertEquals(invokeTimes, clientUserProcessor.getInvokeTimes());
    }

    @Test
    public void testCallback() throws RemotingException, InterruptedException {
        client.getConnection(addr, 1000);
        RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                // only when client invoked, the remote address can be get by UserProcessor
                // otherwise, please use ConnectionEventProcessor
                String remoteAddr = serverUserProcessor.getRemoteAddr();
                Assert.assertNull(remoteAddr);
                remoteAddr = serverConnectProcessor.getRemoteAddr();
                Assert.assertNotNull(remoteAddr);
                final List<String> rets = new ArrayList<String>(1);
                final CountDownLatch latch = new CountDownLatch(1);
                server.getRpcServer().invokeWithCallback(remoteAddr, req, new InvokeCallback() {
                    Executor executor = Executors.newCachedThreadPool();

                    @Override
                    public void onResponse(Object result) {
                        ServerBasicUsageTest.logger.warn(("Result received in callback: " + result));
                        rets.add(((String) (result)));
                        latch.countDown();
                    }

                    @Override
                    public void onException(Throwable e) {
                        ServerBasicUsageTest.logger.error("Process exception in callback.", e);
                        latch.countDown();
                    }

                    @Override
                    public Executor getExecutor() {
                        return executor;
                    }
                }, 1000);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    String errMsg = "InterruptedException caught in callback!";
                    ServerBasicUsageTest.logger.error(errMsg, e);
                    Assert.fail(errMsg);
                }
                if ((rets.size()) == 0) {
                    Assert.fail("No result! Maybe exception caught!");
                }
                Assert.assertEquals(RequestBody.DEFAULT_CLIENT_RETURN_STR, rets.get(0));
                rets.clear();
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in oneway!";
                ServerBasicUsageTest.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(0, serverUserProcessor.getInvokeTimes());
        Assert.assertEquals(invokeTimes, clientUserProcessor.getInvokeTimes());
    }
}

