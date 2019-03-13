/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http;


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpClientConnection;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class IdleConnectionReaperTest {
    @Test
    public void forceShutdown() throws Exception {
        Assert.assertEquals(0, IdleConnectionReaper.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(IdleConnectionReaper.registerConnectionManager(new IdleConnectionReaperTest.TestClientConnectionManager()));
            Assert.assertEquals(1, IdleConnectionReaper.size());
            Assert.assertTrue(IdleConnectionReaper.shutdown());
            Assert.assertEquals(0, IdleConnectionReaper.size());
            Assert.assertFalse(IdleConnectionReaper.shutdown());
        }
    }

    @Test
    public void autoShutdown() throws Exception {
        Assert.assertEquals(0, IdleConnectionReaper.size());
        for (int i = 0; i < 3; i++) {
            HttpClientConnectionManager m = new IdleConnectionReaperTest.TestClientConnectionManager();
            HttpClientConnectionManager m2 = new IdleConnectionReaperTest.TestClientConnectionManager();
            Assert.assertTrue(IdleConnectionReaper.registerConnectionManager(m));
            Assert.assertEquals(1, IdleConnectionReaper.size());
            Assert.assertTrue(IdleConnectionReaper.registerConnectionManager(m2));
            Assert.assertEquals(2, IdleConnectionReaper.size());
            Assert.assertTrue(IdleConnectionReaper.removeConnectionManager(m));
            Assert.assertEquals(1, IdleConnectionReaper.size());
            Assert.assertTrue(IdleConnectionReaper.removeConnectionManager(m2));
            Assert.assertEquals(0, IdleConnectionReaper.size());
            Assert.assertFalse(IdleConnectionReaper.shutdown());
        }
    }

    @Test
    public void maxIdle_HonoredOnClose() throws InterruptedException {
        HttpClientConnectionManager connectionManager = Mockito.mock(HttpClientConnectionManager.class);
        final long idleTime = 10 * 1000;
        IdleConnectionReaper.registerConnectionManager(connectionManager, idleTime);
        Mockito.verify(connectionManager, Mockito.timeout((90 * 1000))).closeIdleConnections(ArgumentMatchers.eq(idleTime), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
    }

    private static class TestClientConnectionManager implements HttpClientConnectionManager {
        @Override
        public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration, TimeUnit timeUnit) {
        }

        @Override
        public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {
        }

        @Override
        public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
        }

        @Override
        public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public void closeIdleConnections(long idletime, TimeUnit tunit) {
        }

        @Override
        public void closeExpiredConnections() {
        }

        @Override
        public ConnectionRequest requestConnection(HttpRoute route, Object state) {
            return null;
        }
    }
}

