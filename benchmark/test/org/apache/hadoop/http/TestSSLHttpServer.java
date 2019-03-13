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
package org.apache.hadoop.http;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.hadoop.security.ssl.SSLFactory;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This testcase issues SSL certificates configures the HttpServer to serve
 * HTTPS using the created certificates and calls an echo servlet using the
 * corresponding HTTPS URL.
 */
public class TestSSLHttpServer extends HttpServerFunctionalTest {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestSSLHttpServer.class.getSimpleName());

    private static final Logger LOG = LoggerFactory.getLogger(TestSSLHttpServer.class);

    private static final String HTTPS_CIPHER_SUITES_KEY = "https.cipherSuites";

    private static final String JAVAX_NET_DEBUG_KEY = "javax.net.debug";

    private static final String SSL_SERVER_KEYSTORE_PROP_PREFIX = "ssl.server" + ".keystore";

    private static final String SSL_SERVER_TRUSTSTORE_PROP_PREFIX = "ssl.server" + ".truststore";

    private static final String SERVLET_NAME_LONGHEADER = "longheader";

    private static final String SERVLET_PATH_LONGHEADER = "/" + (TestSSLHttpServer.SERVLET_NAME_LONGHEADER);

    private static final String SERVLET_NAME_ECHO = "echo";

    private static final String SERVLET_PATH_ECHO = "/" + (TestSSLHttpServer.SERVLET_NAME_ECHO);

    private static HttpServer2 server;

    private static String keystoreDir;

    private static String sslConfDir;

    private static SSLFactory clientSslFactory;

    private static String cipherSuitesPropertyValue;

    private static String sslDebugPropertyValue;

    private static final String EXCLUDED_CIPHERS = "TLS_ECDHE_RSA_WITH_RC4_128_SHA," + ((((("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA, \n" + "SSL_RSA_WITH_DES_CBC_SHA,") + "SSL_DHE_RSA_WITH_DES_CBC_SHA,  ") + "SSL_RSA_EXPORT_WITH_RC4_40_MD5,\t \n") + "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,") + "SSL_RSA_WITH_RC4_128_MD5 \t");

    private static final String ONE_ENABLED_CIPHERS = (TestSSLHttpServer.EXCLUDED_CIPHERS) + ",TLS_RSA_WITH_AES_128_CBC_SHA";

    private static final String EXCLUSIVE_ENABLED_CIPHERS = "\tTLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, \n" + ((((("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA," + "TLS_RSA_WITH_AES_128_CBC_SHA,") + "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA,  ") + "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA,") + "TLS_DHE_RSA_WITH_AES_128_CBC_SHA,\t\n ") + "TLS_DHE_DSS_WITH_AES_128_CBC_SHA");

    @Test
    public void testEcho() throws Exception {
        Assert.assertEquals("a:b\nc:d\n", TestSSLHttpServer.readFromURL(new URL(HttpServerFunctionalTest.baseUrl, ((TestSSLHttpServer.SERVLET_PATH_ECHO) + "?a=b&c=d"))));
        Assert.assertEquals("a:b\nc&lt;:d\ne:&gt;\n", TestSSLHttpServer.readFromURL(new URL(HttpServerFunctionalTest.baseUrl, ((TestSSLHttpServer.SERVLET_PATH_ECHO) + "?a=b&c<=d&e=>"))));
    }

    /**
     * Test that verifies headers can be up to 64K long. The test adds a 63K
     * header leaving 1K for other headers. This is because the header buffer
     * setting is for ALL headers, names and values included.
     */
    @Test
    public void testLongHeader() throws Exception {
        URL url = new URL(HttpServerFunctionalTest.baseUrl, TestSSLHttpServer.SERVLET_PATH_LONGHEADER);
        HttpsURLConnection conn = ((HttpsURLConnection) (url.openConnection()));
        conn.setSSLSocketFactory(TestSSLHttpServer.clientSslFactory.createSSLSocketFactory());
        testLongHeader(conn);
    }

    /**
     * Test that verifies that excluded ciphers (SSL_RSA_WITH_RC4_128_SHA,
     * TLS_ECDH_ECDSA_WITH_RC4_128_SHA,TLS_ECDH_RSA_WITH_RC4_128_SHA,
     * TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,TLS_ECDHE_RSA_WITH_RC4_128_SHA) are not
     * available for negotiation during SSL connection.
     */
    @Test
    public void testExcludedCiphers() throws Exception {
        URL url = new URL(HttpServerFunctionalTest.baseUrl, ((TestSSLHttpServer.SERVLET_PATH_ECHO) + "?a=b&c=d"));
        HttpsURLConnection conn = getConnectionWithSSLSocketFactory(url, TestSSLHttpServer.EXCLUDED_CIPHERS);
        Assert.assertFalse("excludedCipher list is empty", TestSSLHttpServer.EXCLUDED_CIPHERS.isEmpty());
        try {
            TestSSLHttpServer.readFromConnection(conn);
            Assert.fail("No Ciphers in common, SSLHandshake must fail.");
        } catch (SSLHandshakeException ex) {
            TestSSLHttpServer.LOG.info("No Ciphers in common, expected successful test result.", ex);
        }
    }

    /**
     * Test that verified that additionally included cipher
     * TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA is only available cipher for working
     * TLS connection from client to server disabled for all other common ciphers.
     */
    @Test
    public void testOneEnabledCiphers() throws Exception {
        testEnabledCiphers(TestSSLHttpServer.ONE_ENABLED_CIPHERS);
    }

    /**
     * Test verifies that mutually exclusive server's disabled cipher suites and
     * client's enabled cipher suites can successfully establish TLS connection.
     */
    @Test
    public void testExclusiveEnabledCiphers() throws Exception {
        testEnabledCiphers(TestSSLHttpServer.EXCLUSIVE_ENABLED_CIPHERS);
    }

    private class PreferredCipherSSLSocketFactory extends SSLSocketFactory {
        private final SSLSocketFactory delegateSocketFactory;

        private final String[] enabledCipherSuites;

        PreferredCipherSSLSocketFactory(SSLSocketFactory sslSocketFactory, String[] pEnabledCipherSuites) {
            delegateSocketFactory = sslSocketFactory;
            if ((null != pEnabledCipherSuites) && ((pEnabledCipherSuites.length) > 0)) {
                enabledCipherSuites = pEnabledCipherSuites;
            } else {
                enabledCipherSuites = null;
            }
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegateSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegateSocketFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
            SSLSocket sslSocket = ((SSLSocket) (delegateSocketFactory.createSocket(socket, string, i, bln)));
            setEnabledCipherSuites(sslSocket);
            return sslSocket;
        }

        @Override
        public Socket createSocket(String string, int i) throws IOException {
            SSLSocket sslSocket = ((SSLSocket) (delegateSocketFactory.createSocket(string, i)));
            setEnabledCipherSuites(sslSocket);
            return sslSocket;
        }

        @Override
        public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException {
            SSLSocket sslSocket = ((SSLSocket) (delegateSocketFactory.createSocket(string, i, ia, i1)));
            setEnabledCipherSuites(sslSocket);
            return sslSocket;
        }

        @Override
        public Socket createSocket(InetAddress ia, int i) throws IOException {
            SSLSocket sslSocket = ((SSLSocket) (delegateSocketFactory.createSocket(ia, i)));
            setEnabledCipherSuites(sslSocket);
            return sslSocket;
        }

        @Override
        public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
            SSLSocket sslSocket = ((SSLSocket) (delegateSocketFactory.createSocket(ia, i, ia1, i1)));
            setEnabledCipherSuites(sslSocket);
            return sslSocket;
        }

        private void setEnabledCipherSuites(SSLSocket sslSocket) {
            if (null != (enabledCipherSuites)) {
                sslSocket.setEnabledCipherSuites(enabledCipherSuites);
            }
        }
    }
}

