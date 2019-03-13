/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.javax.net.ssl;


import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import junit.framework.TestCase;


/**
 * Tests for <code>HttpsURLConnection</code> class constructors and methods.
 */
public class HttpsURLConnectionTest extends TestCase {
    /**
     * javax.net.ssl.HttpsURLConnection#HttpsURLConnection(java_net_URL)
     */
    public final void test_Constructor() throws Exception {
        new MyHttpsURLConnection(new URL("https://www.fortify.net/"));
        new MyHttpsURLConnection(null);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getCipherSuite()
     */
    public final void test_getCipherSuite() throws Exception {
        URL url = new URL("https://localhost:55555");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection()));
        try {
            connection.getCipherSuite();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException expected) {
        }
        HttpsURLConnection con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"));
        TestCase.assertEquals("CipherSuite", con.getCipherSuite());
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getLocalCertificates()
     */
    public final void test_getLocalCertificates() throws Exception {
        URL url = new URL("https://localhost:55555");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection()));
        try {
            connection.getLocalCertificates();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException expected) {
        }
        HttpsURLConnection con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.508");
        TestCase.assertNull(con.getLocalCertificates());
        con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.509");
        Certificate[] cert = con.getLocalCertificates();
        TestCase.assertNotNull(cert);
        TestCase.assertEquals(1, cert.length);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getDefaultHostnameVerifier()
     */
    public final void test_getDefaultHostnameVerifier() {
        HostnameVerifier verifyer = HttpsURLConnection.getDefaultHostnameVerifier();
        TestCase.assertNotNull("Default hostname verifyer is null", verifyer);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getDefaultSSLSocketFactory()
     */
    public final void test_getDefaultSSLSocketFactory() {
        SSLSocketFactory sf = HttpsURLConnection.getDefaultSSLSocketFactory();
        if (!(sf.equals(SSLSocketFactory.getDefault()))) {
            TestCase.fail("incorrect DefaultSSLSocketFactory");
        }
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getHostnameVerifier()
     */
    public final void test_getHostnameVerifier() throws Exception {
        HttpsURLConnection con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"));
        HostnameVerifier verifyer = con.getHostnameVerifier();
        TestCase.assertNotNull("Hostname verifyer is null", verifyer);
        TestCase.assertEquals("Incorrect value of hostname verirfyer", HttpsURLConnection.getDefaultHostnameVerifier(), verifyer);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getLocalPrincipal()
     */
    public final void test_getLocalPrincipal() throws Exception {
        URL url = new URL("https://localhost:55555");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection()));
        try {
            connection.getLocalPrincipal();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException expected) {
        }
        HttpsURLConnection con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.508");
        TestCase.assertNull(con.getLocalPrincipal());
        con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.509");
        TestCase.assertNotNull("Local principal is null", con.getLocalPrincipal());
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getPeerPrincipal()
     */
    public final void test_getPeerPrincipal() throws Exception {
        URL url = new URL("https://localhost:55555");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection()));
        try {
            connection.getPeerPrincipal();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException expected) {
        }
        HttpsURLConnection con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.508");
        try {
            Principal p = con.getPeerPrincipal();
            TestCase.fail("SSLPeerUnverifiedException wasn't thrown");
        } catch (SSLPeerUnverifiedException expected) {
        }
        con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.509");
        Principal p = con.getPeerPrincipal();
        TestCase.assertNotNull(p);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getServerCertificates()
     */
    public final void test_getServerCertificates() throws Exception {
        URL url = new URL("https://localhost:55555");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection()));
        try {
            connection.getServerCertificates();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException expected) {
        }
        HttpsURLConnection con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.508");
        try {
            con.getServerCertificates();
            TestCase.fail("SSLPeerUnverifiedException wasn't thrown");
        } catch (SSLPeerUnverifiedException expected) {
        }
        con = new MyHttpsURLConnection(new URL("https://www.fortify.net/"), "X.509");
        Certificate[] cert = con.getServerCertificates();
        TestCase.assertNotNull(cert);
        TestCase.assertEquals(1, cert.length);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#getSSLSocketFactory()
     */
    public final void test_getSSLSocketFactory() {
        HttpsURLConnection con = new MyHttpsURLConnection(null);
        SSLSocketFactory sf = con.getSSLSocketFactory();
        if (!(sf.equals(SSLSocketFactory.getDefault()))) {
            TestCase.fail("incorrect DefaultSSLSocketFactory");
        }
    }

    /**
     * javax.net.ssl.HttpsURLConnection#setDefaultHostnameVerifier()
     */
    public final void test_setDefaultHostnameVerifier() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(null);
            TestCase.fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        HostnameVerifier def = HttpsURLConnection.getDefaultHostnameVerifier();
        try {
            myHostnameVerifier hnv = new myHostnameVerifier();
            HttpsURLConnection.setDefaultHostnameVerifier(hnv);
            TestCase.assertEquals(hnv, HttpsURLConnection.getDefaultHostnameVerifier());
        } finally {
            HttpsURLConnection.setDefaultHostnameVerifier(def);
        }
    }

    /**
     * javax.net.ssl.HttpsURLConnection#setHostnameVerifier()
     */
    public final void test_setHostnameVerifier() {
        HttpsURLConnection con = new MyHttpsURLConnection(null);
        try {
            con.setHostnameVerifier(null);
            TestCase.fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        myHostnameVerifier hnv = new myHostnameVerifier();
        con.setHostnameVerifier(hnv);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#setDefaultSSLSocketFactory()
     */
    public final void test_setDefaultSSLSocketFactory() {
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(null);
            TestCase.fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        SSLSocketFactory ssf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
    }

    /**
     * javax.net.ssl.HttpsURLConnection#setSSLSocketFactory()
     */
    public final void test_setSSLSocketFactory() {
        HttpsURLConnection con = new MyHttpsURLConnection(null);
        try {
            con.setSSLSocketFactory(null);
            TestCase.fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        SSLSocketFactory ssf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        con.setSSLSocketFactory(ssf);
    }
}

