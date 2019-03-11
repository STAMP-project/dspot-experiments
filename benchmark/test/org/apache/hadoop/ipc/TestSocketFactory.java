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
package org.apache.hadoop.ipc;


import CommonConfigurationKeys.HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.net.SocksSocketFactory;
import org.apache.hadoop.net.StandardSocketFactory;
import org.junit.Assert;
import org.junit.Test;

import static java.net.Proxy.Type.SOCKS;


/**
 * test StandardSocketFactory and SocksSocketFactory NetUtils
 */
public class TestSocketFactory {
    private static final int START_STOP_TIMEOUT_SEC = 30;

    private TestSocketFactory.ServerRunnable serverRunnable;

    private Thread serverThread;

    private int port;

    @Test
    public void testSocketFactoryAsKeyInMap() {
        Map<SocketFactory, Integer> dummyCache = new HashMap<SocketFactory, Integer>();
        int toBeCached1 = 1;
        int toBeCached2 = 2;
        Configuration conf = new Configuration();
        conf.set(HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY, "org.apache.hadoop.ipc.TestSocketFactory$DummySocketFactory");
        final SocketFactory dummySocketFactory = NetUtils.getDefaultSocketFactory(conf);
        dummyCache.put(dummySocketFactory, toBeCached1);
        conf.set(HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY, "org.apache.hadoop.net.StandardSocketFactory");
        final SocketFactory defaultSocketFactory = NetUtils.getDefaultSocketFactory(conf);
        dummyCache.put(defaultSocketFactory, toBeCached2);
        Assert.assertEquals("The cache contains two elements", 2, dummyCache.size());
        Assert.assertEquals("Equals of both socket factory shouldn't be same", defaultSocketFactory.equals(dummySocketFactory), false);
        Assert.assertSame(toBeCached2, dummyCache.remove(defaultSocketFactory));
        dummyCache.put(defaultSocketFactory, toBeCached2);
        Assert.assertSame(toBeCached1, dummyCache.remove(dummySocketFactory));
    }

    /**
     * A dummy socket factory class that extends the StandardSocketFactory.
     */
    static class DummySocketFactory extends StandardSocketFactory {}

    /**
     * Test SocksSocketFactory.
     */
    @Test(timeout = 5000)
    public void testSocksSocketFactory() throws Exception {
        startTestServer();
        testSocketFactory(new SocksSocketFactory());
    }

    /**
     * Test StandardSocketFactory.
     */
    @Test(timeout = 5000)
    public void testStandardSocketFactory() throws Exception {
        startTestServer();
        testSocketFactory(new StandardSocketFactory());
    }

    /**
     * test proxy methods
     */
    @Test(timeout = 5000)
    public void testProxy() throws Exception {
        SocksSocketFactory templateWithoutProxy = new SocksSocketFactory();
        Proxy proxy = new Proxy(SOCKS, InetSocketAddress.createUnresolved("localhost", 0));
        SocksSocketFactory templateWithProxy = new SocksSocketFactory(proxy);
        Assert.assertFalse(templateWithoutProxy.equals(templateWithProxy));
        Configuration configuration = new Configuration();
        configuration.set("hadoop.socks.server", "localhost:0");
        templateWithoutProxy.setConf(configuration);
        Assert.assertTrue(templateWithoutProxy.equals(templateWithProxy));
    }

    /**
     * Simple tcp server. Server gets a string, transforms it to upper case and returns it.
     */
    private static class ServerRunnable implements Runnable {
        private volatile boolean works = true;

        private ServerSocket testSocket;

        private volatile boolean ready = false;

        private volatile Throwable throwable;

        private int port0;

        @Override
        public void run() {
            try {
                testSocket = new ServerSocket(0);
                port0 = testSocket.getLocalPort();
                ready = true;
                while (works) {
                    try {
                        Socket connectionSocket = testSocket.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                        String inData = input.readLine();
                        String outData = (inData.toUpperCase()) + "\n";
                        out.writeBytes(outData);
                    } catch (SocketException ignored) {
                    }
                } 
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throwable = ioe;
            }
        }

        public void stop() {
            works = false;
            try {
                testSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isReady() {
            return ready;
        }

        public int getPort() {
            return port0;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}

