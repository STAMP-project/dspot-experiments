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
package com.alipay.remoting.inner.connection;


import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventHandler;
import com.alipay.remoting.ConnectionEventListener;
import com.alipay.remoting.ConnectionSelectStrategy;
import com.alipay.remoting.DefaultConnectionManager;
import com.alipay.remoting.RandomSelectStrategy;
import com.alipay.remoting.RemotingAddressParser;
import com.alipay.remoting.Url;
import com.alipay.remoting.connection.ConnectionFactory;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcAddressParser;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcConnectionEventHandler;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Concurrent create connection test
 *
 * @author xiaomin.cxm
 * @version $Id: ConcurrentTest.java, v 0.1 Mar 10, 2016 9:50:00 AM xiaomin.cxm Exp $
 */
public class ConcurrentCreateConnectionTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConnectionManagerTest.class);

    private ConcurrentHashMap<String, UserProcessor<?>> userProcessors = new ConcurrentHashMap<String, UserProcessor<?>>();

    private DefaultConnectionManager cm;

    private ConnectionSelectStrategy connectionSelectStrategy = new RandomSelectStrategy();

    private RemotingAddressParser addressParser = new RpcAddressParser();

    private ConnectionFactory connectionFactory = new com.alipay.remoting.rpc.RpcConnectionFactory(userProcessors, new RpcClient());

    private ConnectionEventHandler connectionEventHandler = new RpcConnectionEventHandler();

    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();

    private BoltServer server;

    private String ip = "127.0.0.1";

    private int port = 1111;

    CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    @Test
    public void testGetAndCheckConnection() throws InterruptedException {
        final Url addr = new Url(ip, port);
        final int connNum = 1;
        final boolean warmup = false;
        for (int i = 0; i < 10; ++i) {
            ConcurrentCreateConnectionTest.MyThread thread = new ConcurrentCreateConnectionTest.MyThread(addr, connNum, warmup);
            new Thread(thread).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            ConcurrentCreateConnectionTest.logger.error("", e);
        }
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
    }

    @Test
    public void testGetAndCheckConnectionMulti() throws InterruptedException {
        final Url addr = new Url(ip, port);
        final int connNum = 10;
        final boolean warmup = true;
        for (int i = 0; i < 10; ++i) {
            ConcurrentCreateConnectionTest.MyThread thread = new ConcurrentCreateConnectionTest.MyThread(addr, connNum, warmup);// warmup in one thread, the other threads will try lock failed.

            new Thread(thread).start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            ConcurrentCreateConnectionTest.logger.error("", e);
        }
        Assert.assertEquals(10, serverConnectProcessor.getConnectTimes());
    }

    class MyThread implements Runnable {
        Url addr;

        int connNum;

        boolean warmup;

        RpcAddressParser parser;

        public MyThread(Url addr, int connNum, boolean warmup) {
            this.addr = addr;
            this.connNum = connNum;
            this.warmup = warmup;
            this.parser = new RpcAddressParser();
        }

        @Override
        public void run() {
            try {
                this.parser.initUrlArgs(addr);
                addr.setConnNum(connNum);
                addr.setConnWarmup(warmup);
                Connection conn = cm.getAndCreateIfAbsent(addr);
                Assert.assertNotNull(conn);
                Assert.assertTrue(conn.isFine());
            } catch (RemotingException e) {
                ConcurrentCreateConnectionTest.logger.error("error!", e);
                Assert.assertTrue(false);
            } catch (Exception e) {
                ConcurrentCreateConnectionTest.logger.error("error!", e);
                Assert.assertTrue(false);
            }
        }
    }
}

