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


import com.alipay.remoting.Connection;
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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * basic usage test
 *
 * each test shared the same server
 *
 * @author xiaomin.cxm
 * @version $Id: BasicUsage_SHARE_SAME_SERVER_Test.java, v 0.1 Apr 6, 2016 8:58:36 PM xiaomin.cxm Exp $
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicUsage_SHARE_SAME_SERVER_Test {
    static Logger logger = LoggerFactory.getLogger(BasicUsage_SHARE_SAME_SERVER_Test.class);

    static BoltServer server;

    RpcClient client;

    static int port = PortScan.select();

    static String ip = "127.0.0.1";

    static String addr = "127.0.0.1:" + (BasicUsage_SHARE_SAME_SERVER_Test.port);

    int invokeTimes = 5;

    static SimpleServerUserProcessor serverUserProcessor = new SimpleServerUserProcessor();

    SimpleClientUserProcessor clientUserProcessor = new SimpleClientUserProcessor();

    CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

    static CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

    DISCONNECTEventProcessor serverDisConnectProcessor = new DISCONNECTEventProcessor();

    @Test
    public void test_a_Oneway() throws InterruptedException {
        RequestBody req = new RequestBody(2, "hello world oneway");
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                client.oneway(BasicUsage_SHARE_SAME_SERVER_Test.addr, req);
                Thread.sleep(100);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in oneway!";
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.isConnected());
        Assert.assertEquals(1, BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(invokeTimes, BasicUsage_SHARE_SAME_SERVER_Test.serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void test_b_Sync() throws InterruptedException {
        RequestBody req = new RequestBody(1, "hello world sync");
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                String res = ((String) (client.invokeSync(BasicUsage_SHARE_SAME_SERVER_Test.addr, req, 3000)));
                BasicUsage_SHARE_SAME_SERVER_Test.logger.warn(("Result received in sync: " + res));
                Assert.assertEquals(RequestBody.DEFAULT_SERVER_RETURN_STR, res);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in sync!";
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            } catch (InterruptedException e) {
                String errMsg = "InterruptedException caught in sync!";
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.isConnected());
        Assert.assertEquals(2, BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(((invokeTimes) * 2), BasicUsage_SHARE_SAME_SERVER_Test.serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void test_c_Future() throws InterruptedException {
        RequestBody req = new RequestBody(2, "hello world future");
        for (int i = 0; i < (invokeTimes); i++) {
            try {
                RpcResponseFuture future = client.invokeWithFuture(BasicUsage_SHARE_SAME_SERVER_Test.addr, req, 3000);
                String res = ((String) (future.get()));
                Assert.assertEquals(RequestBody.DEFAULT_SERVER_RETURN_STR, res);
            } catch (RemotingException e) {
                String errMsg = "RemotingException caught in future!";
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            } catch (InterruptedException e) {
                String errMsg = "InterruptedException caught in future!";
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
        }
        Assert.assertTrue(BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.isConnected());
        Assert.assertEquals(3, BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(((invokeTimes) * 3), BasicUsage_SHARE_SAME_SERVER_Test.serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void test_d_Callback() throws InterruptedException {
        RequestBody req = new RequestBody(1, "hello world callback");
        final List<String> rets = new ArrayList<String>(1);
        for (int i = 0; i < (invokeTimes); i++) {
            final CountDownLatch latch = new CountDownLatch(1);
            try {
                client.invokeWithCallback(BasicUsage_SHARE_SAME_SERVER_Test.addr, req, new InvokeCallback() {
                    Executor executor = Executors.newCachedThreadPool();

                    @Override
                    public void onResponse(Object result) {
                        BasicUsage_SHARE_SAME_SERVER_Test.logger.warn(("Result received in callback: " + result));
                        rets.add(((String) (result)));
                        latch.countDown();
                    }

                    @Override
                    public void onException(Throwable e) {
                        BasicUsage_SHARE_SAME_SERVER_Test.logger.error("Process exception in callback.", e);
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
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                String errMsg = "InterruptedException caught in callback!";
                BasicUsage_SHARE_SAME_SERVER_Test.logger.error(errMsg, e);
                Assert.fail(errMsg);
            }
            if ((rets.size()) == 0) {
                Assert.fail("No result! Maybe exception caught!");
            }
            Assert.assertEquals(RequestBody.DEFAULT_SERVER_RETURN_STR, rets.get(0));
            rets.clear();
        }
        Assert.assertTrue(BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.isConnected());
        Assert.assertEquals(4, BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(((invokeTimes) * 4), BasicUsage_SHARE_SAME_SERVER_Test.serverUserProcessor.getInvokeTimes());
    }

    @Test
    public void test_e_ServerSync() throws Exception {
        Connection clientConn = client.createStandaloneConnection(BasicUsage_SHARE_SAME_SERVER_Test.ip, BasicUsage_SHARE_SAME_SERVER_Test.port, 1000);
        for (int i = 0; i < (invokeTimes); i++) {
            RequestBody req1 = new RequestBody(1, RequestBody.DEFAULT_CLIENT_STR);
            String serverres = ((String) (client.invokeSync(clientConn, req1, 1000)));
            Assert.assertEquals(serverres, RequestBody.DEFAULT_SERVER_RETURN_STR);
            Assert.assertNotNull(BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnection());
            Connection serverConn = BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnection();
            RequestBody req = new RequestBody(1, RequestBody.DEFAULT_SERVER_STR);
            String clientres = ((String) (BasicUsage_SHARE_SAME_SERVER_Test.server.getRpcServer().invokeSync(serverConn, req, 1000)));
            Assert.assertEquals(clientres, RequestBody.DEFAULT_CLIENT_RETURN_STR);
        }
        Assert.assertTrue(BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.isConnected());
        Assert.assertEquals(5, BasicUsage_SHARE_SAME_SERVER_Test.serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(((invokeTimes) * 5), BasicUsage_SHARE_SAME_SERVER_Test.serverUserProcessor.getInvokeTimes());
    }
}

