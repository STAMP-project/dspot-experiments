/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http.conn;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.apache.utils.ApacheUtils;
import com.amazonaws.http.settings.HttpClientSettings;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpException;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integrations tests of proxy behavior of {@link ConnectionSocketFactory} implementations.
 */
public abstract class AbstractConnectionSocketFactoryProxyIntegrationTestBase {
    private static final AbstractConnectionSocketFactoryProxyIntegrationTestBase.MockProxy proxy = new AbstractConnectionSocketFactoryProxyIntegrationTestBase.MockProxy();

    @Test
    public void testDisableProxyConfiguration_SettingTrue_DoesNotConnectToProxy() throws IOException, InterruptedException, ExecutionException, HttpException {
        Socket s = null;
        try {
            HttpClientSettings settings = HttpClientSettings.adapt(new ClientConfiguration().withDisableSocketProxy(true));
            HttpContext ctx = ApacheUtils.newClientContext(settings, new HashMap<String, String>());
            s = getFactory().createSocket(ctx);
            s.connect(new InetSocketAddress("s3.amazonaws.com", 80));
        } finally {
            if (s != null) {
                s.close();
            }
        }
        Assert.assertEquals(0, AbstractConnectionSocketFactoryProxyIntegrationTestBase.proxy.getAcceptCount());
    }

    @Test
    public void tesDisableProxyConfiguration_SettingFalse_ConnectsToProxy() throws IOException, InterruptedException, ExecutionException, HttpException {
        Socket s = null;
        try {
            HttpClientSettings settings = HttpClientSettings.adapt(new ClientConfiguration().withDisableSocketProxy(false));
            HttpContext ctx = ApacheUtils.newClientContext(settings, new HashMap<String, String>());
            s = getFactory().createSocket(ctx);
            s.connect(new InetSocketAddress("s3.amazonaws.com", 80));
        } catch (IOException ignored) {
            ignored.printStackTrace();
            // The Socket will throw an exception when it connects because the mock doesn't implement the protocol.
        } finally {
            if (s != null) {
                s.close();
            }
        }
        Assert.assertEquals(1, AbstractConnectionSocketFactoryProxyIntegrationTestBase.proxy.getAcceptCount());
    }

    private static class MockProxy {
        private ExecutorService exec;

        private ServerSocket ss = null;

        private AtomicLong accepts = new AtomicLong(0);

        public int getPort() {
            return ss.getLocalPort();
        }

        public void init() throws IOException {
            exec = Executors.newSingleThreadExecutor();
            ss = new ServerSocket(0);
        }

        public void run() {
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket s = ss.accept();
                            accepts.incrementAndGet();
                            s.close();
                        } catch (IOException ignored) {
                        }
                    } 
                }
            });
        }

        public long getAcceptCount() {
            return accepts.get();
        }

        public void resetAcceptCount() {
            accepts.set(0);
        }

        public void stop() {
            exec.shutdownNow();
            exec = null;
        }
    }
}

