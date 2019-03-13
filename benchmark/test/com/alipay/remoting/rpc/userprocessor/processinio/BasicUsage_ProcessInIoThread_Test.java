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
package com.alipay.remoting.rpc.userprocessor.processinio;


import com.alipay.remoting.Connection;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.PortScan;
import com.alipay.remoting.rpc.common.RequestBody;
import com.alipay.remoting.util.RemotingUtil;
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
 * basic usage test
 *
 * basic usage of rpc client and rpc server using specific user processor
 *
 * @author xiaomin.cxm
 * @version $Id: BasicUsage_ProcessInIoThread_Test.java, v 0.1 Apr 6, 2016 8:58:36 PM xiaomin.cxm Exp $
 */
public class BasicUsage_ProcessInIoThread_Test {
    static Logger logger = LoggerFactory.getLogger(BasicUsage_ProcessInIoThread_Test.class);

    BoltServer server;

    RpcClient client;

    int port = PortScan.select();

    String ip = "127.0.0.1";

    String addr = "127.0.0.1:" + (port);

    int invokeTimes = 5;

    SpecificServerUserProcessor serverUserProcessor = new SpecificServerUserProcessor();

    SpecificClientUserProcessor clientUserProcessor = new SpecificClientUserProcessor();

    CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

    CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

    DISCONNECTEventProcessor serverDisConnectProcessor = new DISCONNECTEventProcessor();

    @Test
    public void testOneway() throws InterruptedException {
        RequestBody req = new RequestBody(2, "hello world oneway");
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                client.oneway(addr, req);
                Thread.sleep(100);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in oneway!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void testSync() throws InterruptedException {
        RequestBody req = new RequestBody(1, "hello world sync");
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                String res = ((String) (client.invokeSync(addr, req, 3000)));
                BasicUsage_ProcessInIoThread_Test.logger.warn(("Result received in sync: " + res));
                Assert.assertEquals(RequestBody.DEFAULT_SERVER_RETURN_STR, res);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in sync!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            } catch (InterruptedException e) {
                String errMsg = "InterruptedException caught in sync!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void testFuture() throws InterruptedException {
        RequestBody req = new RequestBody(2, "hello world future");
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                RpcResponseFuture future = client.invokeWithFuture(addr, req, 3000);
                String res = ((String) (future.get()));
                Assert.assertEquals(RequestBody.DEFAULT_SERVER_RETURN_STR, res);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in future!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            } catch (InterruptedException e) {
                String errMsg = "InterruptedException caught in future!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void testCallback() throws InterruptedException {
        RequestBody req = new RequestBody(1, "hello world callback");
        final List<String> rets = new ArrayList<String>(1);
        for (int i = 0; i < (invokeTimes); i++) {
            final CountDownLatch latch = new CountDownLatch(1);
            try {
                client.invokeWithCallback(addr, req, new InvokeCallback() {
                    Executor executor = Executors.newCachedThreadPool();

                    @Override
                    public void onResponse(Object result) {
                        BasicUsage_ProcessInIoThread_Test.logger.warn(("Result received in callback: " + result));
                        rets.add(((String) (result)));
                        latch.countDown();
                    }

                    @Override
                    public void onException(Throwable e) {
                        BasicUsage_ProcessInIoThread_Test.logger.error("Process exception in callback.", e);
                        latch.countDown();
                    }

                    @Override
                    public Executor getExecutor() {
                        return executor;
                    }
                }, 1000);
            } catch (RemotingException e) {
                latch.countDown();
                String errMsg = "RemotingException caught in callback!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                String errMsg = "InterruptedException caught in callback!";
                BasicUsage_ProcessInIoThread_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
            if ((rets.size()) == 0) {
                Assert.fail("No result! Maybe exception caught!");
            }
            Assert.assertEquals(RequestBody.DEFAULT_SERVER_RETURN_STR, rets.get(0));
            rets.clear();
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void testServerSyncUsingConnection() throws Exception {
        Connection clientConn = client.createStandaloneConnection(ip, port, 1000);
        for (int i = 0; i < (invokeTimes); i++) {
            RequestBody req1 = new RequestBody(1, RequestBody.DEFAULT_CLIENT_STR);
            String serverres = ((String) (client.invokeSync(clientConn, req1, 1000)));
            Assert.assertEquals(serverres, RequestBody.DEFAULT_SERVER_RETURN_STR);
            Assert.assertNotNull(serverConnectProcessor.getConnection());
            Connection serverConn = serverConnectProcessor.getConnection();
            RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
            String clientres = ((String) (server.getRpcServer().invokeSync(serverConn, req, 1000)));
            Assert.assertEquals(clientres, RequestBody.DEFAULT_CLIENT_RETURN_STR);
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void testServerSyncUsingAddress() throws Exception {
        Connection clientConn = client.createStandaloneConnection(ip, port, 1000);
        String remote = RemotingUtil.parseRemoteAddress(clientConn.getChannel());
        String local = RemotingUtil.parseLocalAddress(clientConn.getChannel());
        BasicUsage_ProcessInIoThread_Test.logger.warn(("Client say local:" + local));
        BasicUsage_ProcessInIoThread_Test.logger.warn(("Client say remote:" + remote));
        for (int i = 0; i < (invokeTimes); i++) {
            RequestBody req1 = new RequestBody(1, RequestBody.DEFAULT_CLIENT_STR);
            String serverres = ((String) (client.invokeSync(clientConn, req1, 1000)));
            Assert.assertEquals(serverres, RequestBody.DEFAULT_SERVER_RETURN_STR);
            Assert.assertNotNull(serverConnectProcessor.getConnection());
            // only when client invoked, the remote address can be get by UserProcessor
            // otherwise, please use ConnectionEventProcessor
            String remoteAddr = serverUserProcessor.getRemoteAddr();
            RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
            String clientres = ((String) (server.getRpcServer().invokeSync(remoteAddr, req, 1000)));
            Assert.assertEquals(clientres, RequestBody.DEFAULT_CLIENT_RETURN_STR);
        }
        Assert.assertTrue(serverConnectProcessor.isConnected());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, serverUserProcessor.getInvokeTimes());
    }
}

