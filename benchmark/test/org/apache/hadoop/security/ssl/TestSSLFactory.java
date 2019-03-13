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
package org.apache.hadoop.security.ssl;


import SSLFactory.LOG;
import SSLFactory.Mode;
import SSLFactory.Mode.CLIENT;
import SSLFactory.Mode.SERVER;
import SSLFactory.SSL_HOSTNAME_VERIFIER_KEY;
import SSLFactory.SSL_REQUIRE_CLIENT_CERT_KEY;
import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestSSLFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TestSSLFactory.class);

    private static final String BASEDIR = GenericTestUtils.getTempPath(TestSSLFactory.class.getSimpleName());

    private static final String KEYSTORES_DIR = new File(TestSSLFactory.BASEDIR).getAbsolutePath();

    private String sslConfsDir;

    private static final String excludeCiphers = "TLS_ECDHE_RSA_WITH_RC4_128_SHA," + (((((("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA,  \n" + "SSL_RSA_WITH_DES_CBC_SHA,") + "SSL_DHE_RSA_WITH_DES_CBC_SHA,  ") + "SSL_RSA_EXPORT_WITH_RC4_40_MD5,\t \n") + "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,") + "SSL_RSA_WITH_RC4_128_MD5,") + "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");

    @Test(expected = IllegalStateException.class)
    public void clientMode() throws Exception {
        Configuration conf = createConfiguration(false, true);
        SSLFactory sslFactory = new SSLFactory(Mode.CLIENT, conf);
        try {
            sslFactory.init();
            Assert.assertNotNull(sslFactory.createSSLSocketFactory());
            Assert.assertNotNull(sslFactory.getHostnameVerifier());
            sslFactory.createSSLServerSocketFactory();
        } finally {
            sslFactory.destroy();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void serverModeWithoutClientCertsSocket() throws Exception {
        serverMode(false, true);
    }

    @Test(expected = IllegalStateException.class)
    public void serverModeWithClientCertsSocket() throws Exception {
        serverMode(true, true);
    }

    @Test(expected = IllegalStateException.class)
    public void serverModeWithoutClientCertsVerifier() throws Exception {
        serverMode(false, false);
    }

    @Test(expected = IllegalStateException.class)
    public void serverModeWithClientCertsVerifier() throws Exception {
        serverMode(true, false);
    }

    @Test
    public void testServerWeakCiphers() throws Exception {
        // a simple test case to verify that SSL server rejects weak cipher suites,
        // inspired by https://docs.oracle.com/javase/8/docs/technotes/guides/
        // security/jsse/samples/sslengine/SSLEngineSimpleDemo.java
        // set up a client and a server SSLEngine object, and let them exchange
        // data over ByteBuffer instead of network socket.
        GenericTestUtils.setLogLevel(SSLFactory.LOG, Level.DEBUG);
        final Configuration conf = createConfiguration(true, true);
        SSLFactory serverSSLFactory = new SSLFactory(Mode.SERVER, conf);
        SSLFactory clientSSLFactory = new SSLFactory(Mode.CLIENT, conf);
        serverSSLFactory.init();
        clientSSLFactory.init();
        SSLEngine serverSSLEngine = serverSSLFactory.createSSLEngine();
        SSLEngine clientSSLEngine = clientSSLFactory.createSSLEngine();
        // client selects cipher suites excluded by server
        clientSSLEngine.setEnabledCipherSuites(StringUtils.getTrimmedStrings(TestSSLFactory.excludeCiphers));
        // use the same buffer size for server and client.
        SSLSession session = clientSSLEngine.getSession();
        int appBufferMax = session.getApplicationBufferSize();
        int netBufferMax = session.getPacketBufferSize();
        ByteBuffer clientOut = ByteBuffer.wrap("client".getBytes());
        ByteBuffer clientIn = ByteBuffer.allocate(appBufferMax);
        ByteBuffer serverOut = ByteBuffer.wrap("server".getBytes());
        ByteBuffer serverIn = ByteBuffer.allocate(appBufferMax);
        // send data from client to server
        ByteBuffer cTOs = ByteBuffer.allocateDirect(netBufferMax);
        // send data from server to client
        ByteBuffer sTOc = ByteBuffer.allocateDirect(netBufferMax);
        boolean dataDone = false;
        try {
            /**
             * Server and client engines call wrap()/unwrap() to perform handshaking,
             * until both engines are closed.
             */
            while ((!(TestSSLFactory.isEngineClosed(clientSSLEngine))) || (!(TestSSLFactory.isEngineClosed(serverSSLEngine)))) {
                TestSSLFactory.LOG.info(("client wrap " + (wrap(clientSSLEngine, clientOut, cTOs))));
                TestSSLFactory.LOG.info(("server wrap " + (wrap(serverSSLEngine, serverOut, sTOc))));
                cTOs.flip();
                sTOc.flip();
                TestSSLFactory.LOG.info(("client unwrap " + (unwrap(clientSSLEngine, sTOc, clientIn))));
                TestSSLFactory.LOG.info(("server unwrap " + (unwrap(serverSSLEngine, cTOs, serverIn))));
                cTOs.compact();
                sTOc.compact();
                if (((!dataDone) && ((clientOut.limit()) == (serverIn.position()))) && ((serverOut.limit()) == (clientIn.position()))) {
                    TestSSLFactory.checkTransfer(serverOut, clientIn);
                    TestSSLFactory.checkTransfer(clientOut, serverIn);
                    TestSSLFactory.LOG.info("closing client");
                    clientSSLEngine.closeOutbound();
                    dataDone = true;
                }
            } 
            Assert.fail("The exception was not thrown");
        } catch (SSLHandshakeException e) {
            GenericTestUtils.assertExceptionContains("no cipher suites in common", e);
        }
    }

    @Test
    public void validHostnameVerifier() throws Exception {
        Configuration conf = createConfiguration(false, true);
        conf.unset(SSL_HOSTNAME_VERIFIER_KEY);
        SSLFactory sslFactory = new SSLFactory(Mode.CLIENT, conf);
        sslFactory.init();
        Assert.assertEquals("DEFAULT", sslFactory.getHostnameVerifier().toString());
        sslFactory.destroy();
        conf.set(SSL_HOSTNAME_VERIFIER_KEY, "ALLOW_ALL");
        sslFactory = new SSLFactory(Mode.CLIENT, conf);
        sslFactory.init();
        Assert.assertEquals("ALLOW_ALL", sslFactory.getHostnameVerifier().toString());
        sslFactory.destroy();
        conf.set(SSL_HOSTNAME_VERIFIER_KEY, "DEFAULT_AND_LOCALHOST");
        sslFactory = new SSLFactory(Mode.CLIENT, conf);
        sslFactory.init();
        Assert.assertEquals("DEFAULT_AND_LOCALHOST", sslFactory.getHostnameVerifier().toString());
        sslFactory.destroy();
        conf.set(SSL_HOSTNAME_VERIFIER_KEY, "STRICT");
        sslFactory = new SSLFactory(Mode.CLIENT, conf);
        sslFactory.init();
        Assert.assertEquals("STRICT", sslFactory.getHostnameVerifier().toString());
        sslFactory.destroy();
        conf.set(SSL_HOSTNAME_VERIFIER_KEY, "STRICT_IE6");
        sslFactory = new SSLFactory(Mode.CLIENT, conf);
        sslFactory.init();
        Assert.assertEquals("STRICT_IE6", sslFactory.getHostnameVerifier().toString());
        sslFactory.destroy();
    }

    @Test(expected = GeneralSecurityException.class)
    public void invalidHostnameVerifier() throws Exception {
        Configuration conf = createConfiguration(false, true);
        conf.set(SSL_HOSTNAME_VERIFIER_KEY, "foo");
        SSLFactory sslFactory = new SSLFactory(Mode.CLIENT, conf);
        try {
            sslFactory.init();
        } finally {
            sslFactory.destroy();
        }
    }

    @Test
    public void testConnectionConfigurator() throws Exception {
        Configuration conf = createConfiguration(false, true);
        conf.set(SSL_HOSTNAME_VERIFIER_KEY, "STRICT_IE6");
        SSLFactory sslFactory = new SSLFactory(Mode.CLIENT, conf);
        try {
            sslFactory.init();
            HttpsURLConnection sslConn = ((HttpsURLConnection) (new URL("https://foo").openConnection()));
            Assert.assertNotSame("STRICT_IE6", sslConn.getHostnameVerifier().toString());
            sslFactory.configure(sslConn);
            Assert.assertEquals("STRICT_IE6", sslConn.getHostnameVerifier().toString());
        } finally {
            sslFactory.destroy();
        }
    }

    @Test
    public void testServerDifferentPasswordAndKeyPassword() throws Exception {
        checkSSLFactoryInitWithPasswords(SERVER, "password", "keyPassword", "password", "keyPassword");
    }

    @Test
    public void testServerKeyPasswordDefaultsToPassword() throws Exception {
        checkSSLFactoryInitWithPasswords(SERVER, "password", "password", "password", null);
    }

    @Test
    public void testClientDifferentPasswordAndKeyPassword() throws Exception {
        checkSSLFactoryInitWithPasswords(CLIENT, "password", "keyPassword", "password", "keyPassword");
    }

    @Test
    public void testClientKeyPasswordDefaultsToPassword() throws Exception {
        checkSSLFactoryInitWithPasswords(CLIENT, "password", "password", "password", null);
    }

    @Test
    public void testServerCredProviderPasswords() throws Exception {
        KeyStoreTestUtil.provisionPasswordsToCredentialProvider();
        checkSSLFactoryInitWithPasswords(SERVER, "storepass", "keypass", null, null, true);
    }

    @Test
    public void testNoClientCertsInitialization() throws Exception {
        Configuration conf = createConfiguration(false, true);
        conf.unset(SSL_REQUIRE_CLIENT_CERT_KEY);
        SSLFactory sslFactory = new SSLFactory(Mode.CLIENT, conf);
        try {
            sslFactory.init();
        } finally {
            sslFactory.destroy();
        }
    }

    @Test
    public void testNoTrustStore() throws Exception {
        Configuration conf = createConfiguration(false, false);
        conf.unset(SSL_REQUIRE_CLIENT_CERT_KEY);
        SSLFactory sslFactory = new SSLFactory(Mode.SERVER, conf);
        try {
            sslFactory.init();
        } finally {
            sslFactory.destroy();
        }
    }
}

