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


import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests cases when node connects to cluster with different set of cipher suites.
 */
public class ClientSslParametersTest extends GridCommonAbstractTest {
    /**
     *
     */
    public static final String TEST_CACHE_NAME = "TEST";

    /**
     *
     */
    private volatile String[] cipherSuites;

    /**
     *
     */
    private volatile String[] protocols;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSameCipherSuite() throws Exception {
        cipherSuites = new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256" };
        startGrid();
        checkSuccessfulClientStart(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256" }, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOneCommonCipherSuite() throws Exception {
        cipherSuites = new String[]{ "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256" };
        startGrid();
        checkSuccessfulClientStart(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256" }, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoCommonCipherSuite() throws Exception {
        cipherSuites = new String[]{ "TLS_RSA_WITH_AES_128_GCM_SHA256" };
        startGrid();
        checkClientStartFailure(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256" }, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNoCommonProtocols() throws Exception {
        protocols = new String[]{ "TLSv1.1", "SSLv3" };
        startGrid();
        checkClientStartFailure(null, new String[]{ "TLSv1", "TLSv1.2" });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSameProtocols() throws Exception {
        protocols = new String[]{ "TLSv1.1", "TLSv1.2" };
        startGrid();
        checkSuccessfulClientStart(null, new String[]{ "TLSv1.1", "TLSv1.2" });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOneCommonProtocol() throws Exception {
        protocols = new String[]{ "TLSv1", "TLSv1.1", "TLSv1.2" };
        startGrid();
        checkSuccessfulClientStart(null, new String[]{ "TLSv1.1", "SSLv3" });
    }
}

