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
package org.apache.ignite.jdbc.thin;


import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import javax.cache.configuration.Factory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.ignite.IgniteException;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * SSL connection test.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinConnectionSSLTest extends JdbcThinAbstractSelfTest {
    /**
     * Client key store path.
     */
    private static final String CLI_KEY_STORE_PATH = (U.getIgniteHome()) + "/modules/clients/src/test/keystore/client.jks";

    /**
     * Server key store path.
     */
    private static final String SRV_KEY_STORE_PATH = (U.getIgniteHome()) + "/modules/clients/src/test/keystore/server.jks";

    /**
     * Trust key store path.
     */
    private static final String TRUST_KEY_STORE_PATH = (U.getIgniteHome()) + "/modules/clients/src/test/keystore/trust.jks";

    /**
     * SSL context factory.
     */
    private static Factory<SSLContext> sslCtxFactory;

    /**
     * Set SSL context factory to client listener.
     */
    private static boolean setSslCtxFactoryToCli;

    /**
     * Set SSL context factory to ignite.
     */
    private static boolean setSslCtxFactoryToIgnite;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnection() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            try (Connection conn = DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"))) {
                checkConnection(conn);
            }
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionTrustAll() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            try (Connection conn = DriverManager.getConnection((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustAll=true"))) {
                checkConnection(conn);
            }
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionUseIgniteFactory() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToIgnite = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            try (Connection conn = DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"))) {
                checkConnection(conn);
            }
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaultContext() throws Exception {
        // Store exists default SSL context to restore after test.
        final SSLContext dfltSslCtx = SSLContext.getDefault();
        // Setup default context
        SSLContext.setDefault(JdbcThinConnectionSSLTest.getTestSslContextFactory().create());
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        // Factory return default SSL context
        JdbcThinConnectionSSLTest.sslCtxFactory = new Factory<SSLContext>() {
            @Override
            public SSLContext create() {
                try {
                    return SSLContext.getDefault();
                } catch (NoSuchAlgorithmException e) {
                    throw new IgniteException(e);
                }
            }
        };
        startGrids(1);
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1/?sslMode=require")) {
            checkConnection(conn);
        } finally {
            stopAllGrids();
            // Restore SSL context.
            SSLContext.setDefault(dfltSslCtx);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testContextFactory() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try (Connection conn = DriverManager.getConnection((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslFactory=") + (JdbcThinConnectionSSLTest.TestSSLFactory.class.getName())))) {
            checkConnection(conn);
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSslServerAndPlainClient() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1");
                    return null;
                }
            }, SQLException.class, "Failed to connect to Ignite cluster");
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInvalidKeystoreConfig() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        startGrids(1);
        try {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection(((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + (("&sslClientCertificateKeyStoreUrl=invalid_client_keystore_path" + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=")) + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Could not open client key store");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=invalid_cli_passwd") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Could not open client key store");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection(((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=invalid_trust_keystore_path") + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Could not open trusted key store");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=invalid_trust_passwd"));
                    return null;
                }
            }, SQLException.class, "Could not open trusted key store");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection(((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslClientCertificateKeyStoreType=INVALID") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Could not create client KeyStore instance");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    DriverManager.getConnection(((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStoreType=INVALID") + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Could not create trust KeyStore instance");
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUnknownClientCertificate() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Connection c = DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Failed to SSL connect to server");
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUnsupportedSslProtocol() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Connection c = DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + ("&sslProtocol=TLSv1.3" + "&sslClientCertificateKeyStoreUrl=")) + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "TLSv1.3 is not a valid SSL protocol");
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInvalidKeyAlgorithm() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Connection c = DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + ("&sslKeyAlgorithm=INVALID" + "&sslClientCertificateKeyStoreUrl=")) + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Default algorithm definitions for TrustManager and/or KeyManager are invalid");
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInvalidKeyStoreType() throws Exception {
        JdbcThinConnectionSSLTest.setSslCtxFactoryToCli = true;
        JdbcThinConnectionSSLTest.sslCtxFactory = JdbcThinConnectionSSLTest.getTestSslContextFactory();
        startGrids(1);
        try {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Connection c = DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + ("&sslClientCertificateKeyStoreType=PKCS12" + "&sslClientCertificateKeyStoreUrl=")) + (JdbcThinConnectionSSLTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSSLTest.TRUST_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                    return null;
                }
            }, SQLException.class, "Could not open client key store");
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     */
    public static class TestSSLFactory implements Factory<SSLSocketFactory> {
        /**
         * {@inheritDoc }
         */
        @Override
        public SSLSocketFactory create() {
            return JdbcThinConnectionSSLTest.getTestSslContextFactory().create().getSocketFactory();
        }
    }
}

