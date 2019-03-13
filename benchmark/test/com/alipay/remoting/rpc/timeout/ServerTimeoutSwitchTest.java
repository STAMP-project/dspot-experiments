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
package com.alipay.remoting.rpc.timeout;


import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.PortScan;
import com.alipay.remoting.rpc.common.RequestBody;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;
import com.alipay.remoting.rpc.common.SimpleServerUserProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alipay.remoting.rpc.common.RequestBody.InvokeType.CALLBACK;
import static com.alipay.remoting.rpc.common.RequestBody.InvokeType.FUTURE;
import static com.alipay.remoting.rpc.common.RequestBody.InvokeType.ONEWAY;
import static com.alipay.remoting.rpc.common.RequestBody.InvokeType.SYNC;


/**
 * server process timeout test (timeout check in biz thread)
 *
 * if already timeout waiting in work queue, then discard this request and return timeout exception.
 * Oneway will not do this.
 *
 * @author tsui
 * @version $Id: ServerTimeoutSwitchTest.java, v 0.1 2017-07-25 17:35 tsui Exp $
 */
public class ServerTimeoutSwitchTest {
    static Logger logger = LoggerFactory.getLogger(ServerTimeoutTest.class);

    BoltServer server;

    RpcClient client;

    int port = PortScan.select();

    String ip = "127.0.0.1";

    String addr = "127.0.0.1:" + (port);

    int invokeTimes = 5;

    int max_timeout = 500;

    int coreThread = 1;

    int maxThread = 1;

    int workQueue = 1;

    int concurrent = (maxThread) + (workQueue);

    SimpleServerUserProcessor serverUserProcessor = new SimpleServerUserProcessor(max_timeout, coreThread, maxThread, 60, workQueue);

    SimpleClientUserProcessor clientUserProcessor = new SimpleClientUserProcessor(max_timeout, coreThread, maxThread, 60, workQueue);

    CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

    CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

    DISCONNECTEventProcessor serverDisConnectProcessor = new DISCONNECTEventProcessor();

    // ~~~ client and server invoke test methods
    /**
     * the second request will not timeout in oneway process work queue
     */
    @Test
    public void testOneway() {
        for (int i = 0; i <= 1; ++i) {
            new Thread() {
                @Override
                public void run() {
                    oneway(client, null);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, serverUserProcessor.getInvokeTimesEachCallType(ONEWAY));
    }

    /**
     * the second request will not timeout in oneway process work queue
     */
    @Test
    public void testServerOneway() {
        for (int i = 0; i <= 1; ++i) {
            new Thread() {
                @Override
                public void run() {
                    oneway(client, server.getRpcServer());
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, clientUserProcessor.getInvokeTimesEachCallType(ONEWAY));
    }

    /**
     * the second request will timeout in work queue
     */
    @Test
    public void testSync() {
        final int[] timeout = new int[]{ (max_timeout) / 2, (max_timeout) / 3 };
        for (int i = 0; i <= 1; ++i) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    sync(client, null, timeout[j]);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        System.out.println(serverUserProcessor.getInvokeTimesEachCallType(SYNC));
        Assert.assertEquals(2, serverUserProcessor.getInvokeTimesEachCallType(SYNC));
    }

    /**
     * the second request will timeout in work queue
     */
    @Test
    public void testServerSync() {
        final int[] timeout = new int[]{ (max_timeout) / 2, (max_timeout) / 3 };
        for (int i = 0; i <= 1; ++i) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    sync(client, server.getRpcServer(), timeout[j]);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, clientUserProcessor.getInvokeTimesEachCallType(SYNC));
    }

    /**
     * the second request will timeout in work queue
     */
    @Test
    public void testFuture() {
        final int[] timeout = new int[]{ (max_timeout) / 2, (max_timeout) / 3 };
        for (int i = 0; i <= 1; ++i) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    future(client, null, timeout[j]);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, serverUserProcessor.getInvokeTimesEachCallType(FUTURE));
    }

    /**
     * the second request will timeout in work queue
     */
    @Test
    public void testServerFuture() {
        final int[] timeout = new int[]{ (max_timeout) / 2, (max_timeout) / 3 };
        for (int i = 0; i <= 1; ++i) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    future(client, server.getRpcServer(), timeout[j]);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, clientUserProcessor.getInvokeTimesEachCallType(FUTURE));
    }

    /**
     * the second request will timeout in work queue
     */
    @Test
    public void testCallBack() {
        final int[] timeout = new int[]{ (max_timeout) / 2, (max_timeout) / 3 };
        for (int i = 0; i <= 1; ++i) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    callback(client, null, timeout[j]);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, serverUserProcessor.getInvokeTimesEachCallType(CALLBACK));
    }

    /**
     * the second request will timeout in work queue
     */
    @Test
    public void testServerCallBack() {
        final int[] timeout = new int[]{ (max_timeout) / 2, (max_timeout) / 3 };
        for (int i = 0; i <= 1; ++i) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    callback(client, server.getRpcServer(), timeout[j]);
                }
            }.start();
        }
        try {
            Thread.sleep(((max_timeout) * 2));
        } catch (InterruptedException e) {
            ServerTimeoutSwitchTest.logger.error("", e);
        }
        Assert.assertEquals(2, clientUserProcessor.getInvokeTimesEachCallType(CALLBACK));
    }
}

