/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.client;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.X509TrustManager;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests
 */
public class ClientTcpSslAuthenticationSelfTest extends GridCommonAbstractTest {
    /**
     * REST TCP port.
     */
    private static final int REST_TCP_PORT = 12121;

    /**
     * Test trust manager for server.
     */
    private ClientTcpSslAuthenticationSelfTest.MockX509TrustManager srvTrustMgr = new ClientTcpSslAuthenticationSelfTest.MockX509TrustManager();

    /**
     * Test trust manager for client.
     */
    private ClientTcpSslAuthenticationSelfTest.MockX509TrustManager clientTrustMgr = new ClientTcpSslAuthenticationSelfTest.MockX509TrustManager();

    /**
     * Whether server should check clients.
     */
    private volatile boolean checkClient;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testServerAuthenticated() throws Exception {
        checkServerAuthenticatedByClient(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testServerNotAuthenticatedByClient() throws Exception {
        try {
            checkServerAuthenticatedByClient(true);
        } catch (GridClientDisconnectedException e) {
            assertTrue(X.hasCause(e, GridServerUnreachableException.class));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientAuthenticated() throws Exception {
        checkClientAuthenticatedByServer(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientNotAuthenticated() throws Exception {
        try {
            checkServerAuthenticatedByClient(true);
        } catch (GridClientDisconnectedException e) {
            assertTrue(X.hasCause(e, GridServerUnreachableException.class));
        }
    }

    /**
     * Test trust manager to emulate certificate check failures.
     */
    private static class MockX509TrustManager implements X509TrustManager {
        /**
         * Empty array.
         */
        private static final X509Certificate[] EMPTY = new X509Certificate[0];

        /**
         * Whether checks should fail.
         */
        private volatile boolean shouldFail;

        /**
         * Client check call count.
         */
        private AtomicInteger clientCheckCallCnt = new AtomicInteger();

        /**
         * Server check call count.
         */
        private AtomicInteger srvCheckCallCnt = new AtomicInteger();

        /**
         *
         *
         * @param shouldFail
         * 		Whether checks should fail.
         */
        private void shouldFail(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            clientCheckCallCnt.incrementAndGet();
            if (shouldFail)
                throw new CertificateException("Client check failed.");

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            srvCheckCallCnt.incrementAndGet();
            if (shouldFail)
                throw new CertificateException("Server check failed.");

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return ClientTcpSslAuthenticationSelfTest.MockX509TrustManager.EMPTY;
        }

        /**
         *
         *
         * @return Call count to checkClientTrusted method.
         */
        public int clientCheckCallCount() {
            return clientCheckCallCnt.get();
        }

        /**
         *
         *
         * @return Call count to checkServerTrusted method.
         */
        public int serverCheckCallCount() {
            return srvCheckCallCnt.get();
        }

        /**
         * Clears should fail flag and resets call counters.
         */
        public void reset() {
            shouldFail = false;
            clientCheckCallCnt.set(0);
            srvCheckCallCnt.set(0);
        }
    }
}

