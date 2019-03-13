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
package com.alipay.remoting.rpc.callback;


import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.rpc.BasicUsageTest;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.PortScan;
import com.alipay.remoting.rpc.common.RequestBody;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;
import com.alipay.remoting.rpc.common.SimpleServerUserProcessor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *?
 * Invoke call back test
 *
 * @author tsui?
 * @version $Id: InvokeCallbackTest.java, v 0.1 2016-08-31 17:14 tsui Exp $
 */
public class InvokeCallbackTest {
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
    public void testCallbackInvokeTimes() {
        RequestBody req = new RequestBody(1, "hello world sync");
        final AtomicInteger callbackInvokTimes = new AtomicInteger();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            client.invokeWithCallback(addr, req, new InvokeCallback() {
                Executor executor = Executors.newCachedThreadPool();

                @Override
                public void onResponse(Object result) {
                    callbackInvokTimes.getAndIncrement();
                    latch.countDown();
                    throw new RuntimeException("Hehe Exception");
                }

                @Override
                public void onException(Throwable e) {
                    callbackInvokTimes.getAndIncrement();
                    latch.countDown();
                    throw new RuntimeException("Hehe Exception");
                }

                @Override
                public Executor getExecutor() {
                    return executor;
                }
            }, 3000);
            Thread.sleep(500);// wait callback execution

            if (latch.await(3000, TimeUnit.MILLISECONDS)) {
                Assert.assertEquals(1, callbackInvokTimes.get());
            }
        } catch (Exception e) {
            InvokeCallbackTest.logger.error("Exception", e);
        }
    }
}

