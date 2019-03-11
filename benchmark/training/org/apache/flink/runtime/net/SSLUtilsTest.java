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
package org.apache.flink.runtime.net;


import SecurityOptions.SSL_ALGORITHMS;
import SecurityOptions.SSL_ENABLED;
import SecurityOptions.SSL_INTERNAL_ENABLED;
import SecurityOptions.SSL_INTERNAL_KEYSTORE_PASSWORD;
import SecurityOptions.SSL_INTERNAL_KEY_PASSWORD;
import SecurityOptions.SSL_INTERNAL_TRUSTSTORE_PASSWORD;
import SecurityOptions.SSL_PROTOCOL;
import SecurityOptions.SSL_REST_AUTHENTICATION_ENABLED;
import SecurityOptions.SSL_REST_ENABLED;
import SecurityOptions.SSL_REST_KEYSTORE_PASSWORD;
import SecurityOptions.SSL_REST_KEY_PASSWORD;
import SecurityOptions.SSL_REST_TRUSTSTORE;
import SecurityOptions.SSL_REST_TRUSTSTORE_PASSWORD;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocket;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.IllegalConfigurationException;
import org.apache.flink.runtime.io.network.netty.SSLHandlerFactory;
import org.apache.flink.shaded.netty4.io.netty.handler.ssl.SslHandler;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link SSLUtils}.
 */
public class SSLUtilsTest extends TestLogger {
    private static final String TRUST_STORE_PATH = SSLUtilsTest.class.getResource("/local127.truststore").getFile();

    private static final String KEY_STORE_PATH = SSLUtilsTest.class.getResource("/local127.keystore").getFile();

    private static final String TRUST_STORE_PASSWORD = "password";

    private static final String KEY_STORE_PASSWORD = "password";

    private static final String KEY_PASSWORD = "password";

    /**
     * Tests whether activation of internal / REST SSL evaluates the config flags correctly.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void checkEnableSSL() {
        // backwards compatibility
        Configuration oldConf = new Configuration();
        oldConf.setBoolean(SSL_ENABLED, true);
        Assert.assertTrue(SSLUtils.isInternalSSLEnabled(oldConf));
        Assert.assertTrue(SSLUtils.isRestSSLEnabled(oldConf));
        // new options take precedence
        Configuration newOptions = new Configuration();
        newOptions.setBoolean(SSL_INTERNAL_ENABLED, true);
        newOptions.setBoolean(SSL_REST_ENABLED, false);
        Assert.assertTrue(SSLUtils.isInternalSSLEnabled(newOptions));
        Assert.assertFalse(SSLUtils.isRestSSLEnabled(newOptions));
        // new options take precedence
        Configuration precedence = new Configuration();
        precedence.setBoolean(SSL_ENABLED, true);
        precedence.setBoolean(SSL_INTERNAL_ENABLED, false);
        precedence.setBoolean(SSL_REST_ENABLED, false);
        Assert.assertFalse(SSLUtils.isInternalSSLEnabled(precedence));
        Assert.assertFalse(SSLUtils.isRestSSLEnabled(precedence));
    }

    /**
     * Tests whether activation of REST mutual SSL authentication evaluates the config flags correctly.
     */
    @Test
    public void checkEnableRestSSLAuthentication() {
        // SSL has to be enabled
        Configuration noSSLOptions = new Configuration();
        noSSLOptions.setBoolean(SSL_REST_ENABLED, false);
        noSSLOptions.setBoolean(SSL_REST_AUTHENTICATION_ENABLED, true);
        Assert.assertFalse(SSLUtils.isRestSSLAuthenticationEnabled(noSSLOptions));
        // authentication is disabled by default
        Configuration defaultOptions = new Configuration();
        defaultOptions.setBoolean(SSL_REST_ENABLED, true);
        Assert.assertFalse(SSLUtils.isRestSSLAuthenticationEnabled(defaultOptions));
        Configuration options = new Configuration();
        noSSLOptions.setBoolean(SSL_REST_ENABLED, true);
        noSSLOptions.setBoolean(SSL_REST_AUTHENTICATION_ENABLED, true);
        Assert.assertTrue(SSLUtils.isRestSSLAuthenticationEnabled(noSSLOptions));
    }

    @Test
    public void testSocketFactoriesWhenSslDisabled() throws Exception {
        Configuration config = new Configuration();
        try {
            SSLUtils.createSSLServerSocketFactory(config);
            Assert.fail("exception expected");
        } catch (IllegalConfigurationException ignored) {
        }
        try {
            SSLUtils.createSSLClientSocketFactory(config);
            Assert.fail("exception expected");
        } catch (IllegalConfigurationException ignored) {
        }
    }

    // ------------------------ REST client --------------------------
    /**
     * Tests if REST Client SSL is created given a valid SSL configuration.
     */
    @Test
    public void testRESTClientSSL() throws Exception {
        Configuration clientConfig = SSLUtilsTest.createRestSslConfigWithTrustStore();
        SSLHandlerFactory ssl = SSLUtils.createRestClientSSLEngineFactory(clientConfig);
        Assert.assertNotNull(ssl);
    }

    /**
     * Tests that REST Client SSL Client is not created if SSL is not configured.
     */
    @Test
    public void testRESTClientSSLDisabled() throws Exception {
        Configuration clientConfig = SSLUtilsTest.createRestSslConfigWithTrustStore();
        clientConfig.setBoolean(SSL_REST_ENABLED, false);
        try {
            SSLUtils.createRestClientSSLEngineFactory(clientConfig);
            Assert.fail("exception expected");
        } catch (IllegalConfigurationException ignored) {
        }
    }

    /**
     * Tests that REST Client SSL creation fails with bad SSL configuration.
     */
    @Test
    public void testRESTClientSSLMissingTrustStore() throws Exception {
        Configuration config = new Configuration();
        config.setBoolean(SSL_REST_ENABLED, true);
        config.setString(SSL_REST_TRUSTSTORE_PASSWORD, "some password");
        try {
            SSLUtils.createRestClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (IllegalConfigurationException ignored) {
        }
    }

    /**
     * Tests that REST Client SSL creation fails with bad SSL configuration.
     */
    @Test
    public void testRESTClientSSLMissingPassword() throws Exception {
        Configuration config = new Configuration();
        config.setBoolean(SSL_REST_ENABLED, true);
        config.setString(SSL_REST_TRUSTSTORE, SSLUtilsTest.TRUST_STORE_PATH);
        try {
            SSLUtils.createRestClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (IllegalConfigurationException ignored) {
        }
    }

    /**
     * Tests that REST Client SSL creation fails with bad SSL configuration.
     */
    @Test
    public void testRESTClientSSLWrongPassword() throws Exception {
        Configuration clientConfig = SSLUtilsTest.createRestSslConfigWithTrustStore();
        clientConfig.setString(SSL_REST_TRUSTSTORE_PASSWORD, "badpassword");
        try {
            SSLUtils.createRestClientSSLEngineFactory(clientConfig);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    // ------------------------ server --------------------------
    /**
     * Tests that REST Server SSL Engine is created given a valid SSL configuration.
     */
    @Test
    public void testRESTServerSSL() throws Exception {
        Configuration serverConfig = SSLUtilsTest.createRestSslConfigWithKeyStore();
        SSLHandlerFactory ssl = SSLUtils.createRestServerSSLEngineFactory(serverConfig);
        Assert.assertNotNull(ssl);
    }

    /**
     * Tests that REST Server SSL Engine is not created if SSL is disabled.
     */
    @Test
    public void testRESTServerSSLDisabled() throws Exception {
        Configuration serverConfig = SSLUtilsTest.createRestSslConfigWithKeyStore();
        serverConfig.setBoolean(SSL_REST_ENABLED, false);
        try {
            SSLUtils.createRestServerSSLEngineFactory(serverConfig);
            Assert.fail("exception expected");
        } catch (IllegalConfigurationException ignored) {
        }
    }

    /**
     * Tests that REST Server SSL Engine creation fails with bad SSL configuration.
     */
    @Test
    public void testRESTServerSSLBadKeystorePassword() {
        Configuration serverConfig = SSLUtilsTest.createRestSslConfigWithKeyStore();
        serverConfig.setString(SSL_REST_KEYSTORE_PASSWORD, "badpassword");
        try {
            SSLUtils.createRestServerSSLEngineFactory(serverConfig);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    /**
     * Tests that REST Server SSL Engine creation fails with bad SSL configuration.
     */
    @Test
    public void testRESTServerSSLBadKeyPassword() {
        Configuration serverConfig = SSLUtilsTest.createRestSslConfigWithKeyStore();
        serverConfig.setString(SSL_REST_KEY_PASSWORD, "badpassword");
        try {
            SSLUtils.createRestServerSSLEngineFactory(serverConfig);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    // ----------------------- mutual auth contexts --------------------------
    @Test
    public void testInternalSSL() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        Assert.assertNotNull(SSLUtils.createInternalServerSSLEngineFactory(config));
        Assert.assertNotNull(SSLUtils.createInternalClientSSLEngineFactory(config));
    }

    @Test
    public void testInternalSSLDisables() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        config.setBoolean(SSL_INTERNAL_ENABLED, false);
        try {
            SSLUtils.createInternalServerSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
        try {
            SSLUtils.createInternalClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testInternalSSLKeyStoreOnly() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithKeyStore();
        try {
            SSLUtils.createInternalServerSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
        try {
            SSLUtils.createInternalClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testInternalSSLTrustStoreOnly() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithTrustStore();
        try {
            SSLUtils.createInternalServerSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
        try {
            SSLUtils.createInternalClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testInternalSSLWrongKeystorePassword() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        config.setString(SSL_INTERNAL_KEYSTORE_PASSWORD, "badpw");
        try {
            SSLUtils.createInternalServerSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
        try {
            SSLUtils.createInternalClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testInternalSSLWrongTruststorePassword() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        config.setString(SSL_INTERNAL_TRUSTSTORE_PASSWORD, "badpw");
        try {
            SSLUtils.createInternalServerSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
        try {
            SSLUtils.createInternalClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testInternalSSLWrongKeyPassword() throws Exception {
        final Configuration config = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        config.setString(SSL_INTERNAL_KEY_PASSWORD, "badpw");
        try {
            SSLUtils.createInternalServerSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
        try {
            SSLUtils.createInternalClientSSLEngineFactory(config);
            Assert.fail("exception expected");
        } catch (Exception ignored) {
        }
    }

    // -------------------- protocols and cipher suites -----------------------
    /**
     * Tests if SSLUtils set the right ssl version and cipher suites for SSLServerSocket.
     */
    @Test
    public void testSetSSLVersionAndCipherSuitesForSSLServerSocket() throws Exception {
        Configuration serverConfig = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        // set custom protocol and cipher suites
        serverConfig.setString(SSL_PROTOCOL, "TLSv1.1");
        serverConfig.setString(SSL_ALGORITHMS, "TLS_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA256");
        try (ServerSocket socket = SSLUtils.createSSLServerSocketFactory(serverConfig).createServerSocket(0)) {
            Assert.assertTrue((socket instanceof SSLServerSocket));
            final SSLServerSocket sslSocket = ((SSLServerSocket) (socket));
            String[] protocols = sslSocket.getEnabledProtocols();
            String[] algorithms = sslSocket.getEnabledCipherSuites();
            Assert.assertEquals(1, protocols.length);
            Assert.assertEquals("TLSv1.1", protocols[0]);
            Assert.assertEquals(2, algorithms.length);
            Assert.assertThat(algorithms, Matchers.arrayContainingInAnyOrder("TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA256"));
        }
    }

    /**
     * Tests that {@link SSLHandlerFactory} is created correctly.
     */
    @Test
    public void testCreateSSLEngineFactory() throws Exception {
        Configuration serverConfig = SSLUtilsTest.createInternalSslConfigWithKeyAndTrustStores();
        // set custom protocol and cipher suites
        serverConfig.setString(SSL_PROTOCOL, "TLSv1");
        serverConfig.setString(SSL_ALGORITHMS, "TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_DHE_RSA_WITH_AES_128_CBC_SHA256");
        final SSLHandlerFactory serverSSLHandlerFactory = SSLUtils.createInternalServerSSLEngineFactory(serverConfig);
        final SslHandler sslHandler = serverSSLHandlerFactory.createNettySSLHandler();
        Assert.assertEquals(1, sslHandler.engine().getEnabledProtocols().length);
        Assert.assertEquals("TLSv1", sslHandler.engine().getEnabledProtocols()[0]);
        Assert.assertEquals(2, sslHandler.engine().getEnabledCipherSuites().length);
        Assert.assertThat(sslHandler.engine().getEnabledCipherSuites(), Matchers.arrayContainingInAnyOrder("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"));
    }
}

