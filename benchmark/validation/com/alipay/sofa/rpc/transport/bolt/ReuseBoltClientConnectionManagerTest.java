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
package com.alipay.sofa.rpc.transport.bolt;


import com.alipay.remoting.Connection;
import com.alipay.remoting.Url;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.sofa.rpc.common.utils.CommonUtils;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.transport.ClientTransportConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class ReuseBoltClientConnectionManagerTest extends ActivelyDestroyTest {
    private RpcClient rpcClient = new RpcClient();

    ServerConfig serverConfig;

    ServerConfig serverConfig2;

    @Test
    public void testAll() throws Exception {
        ReuseBoltClientConnectionManager manager = new ReuseBoltClientConnectionManager(false);
        Connection connection = manager.getConnection(null, null, null);
        Assert.assertNull(connection);
        connection = manager.getConnection(rpcClient, null, null);
        Assert.assertNull(connection);
        ClientTransportConfig wrongConfig = buildConfig(12224);
        connection = manager.getConnection(rpcClient, wrongConfig, null);
        Assert.assertNull(connection);
        // ??????
        Connection result = manager.getConnection(rpcClient, wrongConfig, buildUrl(wrongConfig));
        Assert.assertNull(result);
        // ok
        final ClientTransportConfig config = buildConfig(12222);
        connection = manager.getConnection(rpcClient, config, buildUrl(config));
        Assert.assertNotNull(connection);
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 1));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 1));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection).get()) == 1));
        // ???config?get???????
        Connection connection1 = manager.getConnection(rpcClient, config, buildUrl(config));
        Assert.assertNotNull(connection1);
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 1));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 1));
        Assert.assertTrue((connection == connection1));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection).get()) == 1));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection1).get()) == 1));
        // ?????config?get??????
        final ClientTransportConfig config2 = buildConfig(12222);
        Connection connection2 = manager.getConnection(rpcClient, config2, buildUrl(config2));
        Assert.assertNotNull(connection2);
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 2));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 1));
        Assert.assertTrue((connection1 == connection2));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection).get()) == 2));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection2).get()) == 2));
        // ?????config?get????????1
        ClientTransportConfig config3 = buildConfig(12223);
        Connection connection3 = manager.getConnection(rpcClient, config3, buildUrl(config3));
        Assert.assertNotNull(connection3);
        Assert.assertFalse((connection == connection3));
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 3));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 2));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection).get()) == 2));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection3).get()) == 1));
        // ????
        manager.closeConnection(null, null, null);
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 3));
        manager.closeConnection(rpcClient, null, null);
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 3));
        manager.closeConnection(rpcClient, config, null);
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 3));
        // ????1
        manager.closeConnection(rpcClient, config, buildUrl(config));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection).get()) == 1));
        // ????1
        manager.closeConnection(rpcClient, config, buildUrl(config));
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 2));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 2));
        Assert.assertTrue(((manager.connectionRefCounter.get(connection).get()) == 1));
        // ????2
        manager.closeConnection(rpcClient, config2, buildUrl(config2));
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 1));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 1));
        // ????3
        manager.closeConnection(rpcClient, config3, buildUrl(config3));
        Assert.assertTrue(((manager.urlConnectionMap.size()) == 0));
        Assert.assertTrue(((manager.connectionRefCounter.size()) == 0));
        // ????
        manager.checkLeak();
        Assert.assertTrue(CommonUtils.isEmpty(manager.urlConnectionMap));
        Assert.assertTrue(CommonUtils.isEmpty(manager.connectionRefCounter));
    }

    @Test
    public void testConcurrentCreate() throws Exception {
        final ReuseBoltClientConnectionManager manager = new ReuseBoltClientConnectionManager(false);
        final ClientTransportConfig config = buildConfig(12222);
        // ????
        final CountDownLatch latch = new CountDownLatch(5);
        List<Thread> threads = new ArrayList<Thread>(5);
        final Url url = buildUrl(config);
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection innerConnection = manager.getConnection(rpcClient, config, url);
                        System.out.println(((("url=" + url) + ",connection=") + innerConnection));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            }, ("thread" + i));
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, manager.urlConnectionMap.size());
        Assert.assertEquals(1, manager.connectionRefCounter.size());
        Connection connection = manager.getConnection(rpcClient, config, url);
        Assert.assertNotNull(connection);
        final AtomicInteger atomicInteger = manager.connectionRefCounter.get(connection);
        Assert.assertEquals(1, atomicInteger.get());
        // ????
        manager.checkLeak();
        Assert.assertTrue(CommonUtils.isEmpty(manager.urlConnectionMap));
        Assert.assertTrue(CommonUtils.isEmpty(manager.connectionRefCounter));
    }
}

