/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum;


import CertificateID.HASH_SHA1;
import OCSPRespBuilder.SUCCESSFUL;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLServerSocketFactory;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.common.QuorumX509Util;
import org.apache.zookeeper.test.ClientBase;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPRespBuilder;
import org.bouncycastle.cert.ocsp.Req;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class QuorumSSLTest extends QuorumPeerTestBase {
    private static final String SSL_QUORUM_ENABLED = "sslQuorum=true\n";

    private static final String PORT_UNIFICATION_ENABLED = "portUnification=true\n";

    private static final String PORT_UNIFICATION_DISABLED = "portUnification=false\n";

    private static final char[] PASSWORD = "testpass".toCharArray();

    private static final String HOSTNAME = "localhost";

    private QuorumX509Util quorumX509Util;

    private QuorumPeerTestBase.MainThread q1;

    private QuorumPeerTestBase.MainThread q2;

    private QuorumPeerTestBase.MainThread q3;

    private int clientPortQp1;

    private int clientPortQp2;

    private int clientPortQp3;

    private String tmpDir;

    private String quorumConfiguration;

    private String validKeystorePath;

    private String truststorePath;

    private KeyPair rootKeyPair;

    private X509Certificate rootCertificate;

    private KeyPair defaultKeyPair;

    private ContentSigner contentSigner;

    private Date certStartTime;

    private Date certEndTime;

    @Rule
    public Timeout timeout = Timeout.builder().withTimeout(5, TimeUnit.MINUTES).withLookingForStuckThread(true).build();

    private class OCSPHandler implements HttpHandler {
        private X509Certificate revokedCert;

        // Builds an OCSPHandler that responds with a good status for all certificates
        // except revokedCert.
        public OCSPHandler(X509Certificate revokedCert) {
            this.revokedCert = revokedCert;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            byte[] responseBytes;
            try {
                InputStream request = httpExchange.getRequestBody();
                byte[] requestBytes = new byte[10000];
                request.read(requestBytes);
                OCSPReq ocspRequest = new OCSPReq(requestBytes);
                Req[] requestList = ocspRequest.getRequestList();
                DigestCalculator digestCalculator = new JcaDigestCalculatorProviderBuilder().build().get(HASH_SHA1);
                BasicOCSPRespBuilder responseBuilder = new org.bouncycastle.cert.ocsp.jcajce.JcaBasicOCSPRespBuilder(rootKeyPair.getPublic(), digestCalculator);
                for (Req req : requestList) {
                    CertificateID certId = req.getCertID();
                    CertificateID revokedCertId = new org.bouncycastle.cert.ocsp.jcajce.JcaCertificateID(digestCalculator, rootCertificate, revokedCert.getSerialNumber());
                    CertificateStatus certificateStatus;
                    if (revokedCertId.equals(certId)) {
                        certificateStatus = new UnknownStatus();
                    } else {
                        certificateStatus = CertificateStatus.GOOD;
                    }
                    responseBuilder.addResponse(certId, certificateStatus, null);
                }
                X509CertificateHolder[] chain = new X509CertificateHolder[]{ new JcaX509CertificateHolder(rootCertificate) };
                ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(rootKeyPair.getPrivate());
                BasicOCSPResp ocspResponse = responseBuilder.build(signer, chain, Calendar.getInstance().getTime());
                responseBytes = getEncoded();
            } catch (OperatorException | CertificateEncodingException | OCSPException exception) {
                responseBytes = getEncoded();
            }
            Headers rh = httpExchange.getResponseHeaders();
            rh.set("Content-Type", "application/ocsp-response");
            httpExchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }

    @Test
    public void testQuorumSSL() throws Exception {
        q1 = new QuorumPeerTestBase.MainThread(1, clientPortQp1, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q2 = new QuorumPeerTestBase.MainThread(2, clientPortQp2, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q1.start();
        q2.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        clearSSLSystemProperties();
        // This server should fail to join the quorum as it is not using ssl.
        q3 = new QuorumPeerTestBase.MainThread(3, clientPortQp3, quorumConfiguration);
        q3.start();
        Assert.assertFalse(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
    }

    @Test
    public void testRollingUpgrade() throws Exception {
        // Form a quorum without ssl
        q1 = new QuorumPeerTestBase.MainThread(1, clientPortQp1, quorumConfiguration);
        q2 = new QuorumPeerTestBase.MainThread(2, clientPortQp2, quorumConfiguration);
        q3 = new QuorumPeerTestBase.MainThread(3, clientPortQp3, quorumConfiguration);
        Map<Integer, QuorumPeerTestBase.MainThread> members = new HashMap<>();
        members.put(clientPortQp1, q1);
        members.put(clientPortQp2, q2);
        members.put(clientPortQp3, q3);
        for (QuorumPeerTestBase.MainThread member : members.values()) {
            member.start();
        }
        for (int clientPort : members.keySet()) {
            Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + clientPort), ClientBase.CONNECTION_TIMEOUT));
        }
        // Set SSL system properties and port unification, begin restarting servers
        setSSLSystemProperties();
        stopAppendConfigRestartAll(members, QuorumSSLTest.PORT_UNIFICATION_ENABLED);
        stopAppendConfigRestartAll(members, QuorumSSLTest.SSL_QUORUM_ENABLED);
        stopAppendConfigRestartAll(members, QuorumSSLTest.PORT_UNIFICATION_DISABLED);
    }

    @Test
    public void testHostnameVerificationWithInvalidHostname() throws Exception {
        String badhostnameKeystorePath = (tmpDir) + "/badhost.jks";
        X509Certificate badHostCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), "bleepbloop", null, null, null);
        writeKeystore(badHostCert, defaultKeyPair, badhostnameKeystorePath);
        testHostnameVerification(badhostnameKeystorePath, false);
    }

    @Test
    public void testHostnameVerificationWithInvalidIPAddress() throws Exception {
        String badhostnameKeystorePath = (tmpDir) + "/badhost.jks";
        X509Certificate badHostCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), null, "140.211.11.105", null, null);
        writeKeystore(badHostCert, defaultKeyPair, badhostnameKeystorePath);
        testHostnameVerification(badhostnameKeystorePath, false);
    }

    @Test
    public void testHostnameVerificationWithInvalidIpAddressAndInvalidHostname() throws Exception {
        String badhostnameKeystorePath = (tmpDir) + "/badhost.jks";
        X509Certificate badHostCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), "bleepbloop", "140.211.11.105", null, null);
        writeKeystore(badHostCert, defaultKeyPair, badhostnameKeystorePath);
        testHostnameVerification(badhostnameKeystorePath, false);
    }

    @Test
    public void testHostnameVerificationWithInvalidIpAddressAndValidHostname() throws Exception {
        String badhostnameKeystorePath = (tmpDir) + "/badhost.jks";
        X509Certificate badHostCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), "localhost", "140.211.11.105", null, null);
        writeKeystore(badHostCert, defaultKeyPair, badhostnameKeystorePath);
        testHostnameVerification(badhostnameKeystorePath, true);
    }

    @Test
    public void testHostnameVerificationWithValidIpAddressAndInvalidHostname() throws Exception {
        String badhostnameKeystorePath = (tmpDir) + "/badhost.jks";
        X509Certificate badHostCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), "bleepbloop", "127.0.0.1", null, null);
        writeKeystore(badHostCert, defaultKeyPair, badhostnameKeystorePath);
        testHostnameVerification(badhostnameKeystorePath, true);
    }

    @Test
    public void testCertificateRevocationList() throws Exception {
        q1 = new QuorumPeerTestBase.MainThread(1, clientPortQp1, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q2 = new QuorumPeerTestBase.MainThread(2, clientPortQp2, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q1.start();
        q2.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        String revokedInCRLKeystorePath = (tmpDir) + "/crl_revoked.jks";
        String crlPath = (tmpDir) + "/crl.pem";
        X509Certificate revokedInCRLCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), QuorumSSLTest.HOSTNAME, null, crlPath, null);
        writeKeystore(revokedInCRLCert, defaultKeyPair, revokedInCRLKeystorePath);
        buildCRL(revokedInCRLCert, crlPath);
        System.setProperty(quorumX509Util.getSslKeystoreLocationProperty(), revokedInCRLKeystorePath);
        // This server should join successfully
        q3 = new QuorumPeerTestBase.MainThread(3, clientPortQp3, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q3.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
        q1.shutdown();
        q2.shutdown();
        q3.shutdown();
        Assert.assertTrue(ClientBase.waitForServerDown(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerDown(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerDown(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
        setSSLSystemProperties();
        System.setProperty(quorumX509Util.getSslCrlEnabledProperty(), "true");
        X509Certificate validCertificate = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), QuorumSSLTest.HOSTNAME, null, crlPath, null);
        writeKeystore(validCertificate, defaultKeyPair, validKeystorePath);
        q1.start();
        q2.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        System.setProperty(quorumX509Util.getSslKeystoreLocationProperty(), revokedInCRLKeystorePath);
        q3.start();
        Assert.assertFalse(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
    }

    @Test
    public void testOCSP() throws Exception {
        Integer ocspPort = PortAssignment.unique();
        q1 = new QuorumPeerTestBase.MainThread(1, clientPortQp1, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q2 = new QuorumPeerTestBase.MainThread(2, clientPortQp2, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q1.start();
        q2.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        String revokedInOCSPKeystorePath = (tmpDir) + "/ocsp_revoked.jks";
        X509Certificate revokedInOCSPCert = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), QuorumSSLTest.HOSTNAME, null, null, ocspPort);
        writeKeystore(revokedInOCSPCert, defaultKeyPair, revokedInOCSPKeystorePath);
        HttpServer ocspServer = HttpServer.create(new InetSocketAddress(ocspPort), 0);
        try {
            ocspServer.createContext("/", new QuorumSSLTest.OCSPHandler(revokedInOCSPCert));
            ocspServer.start();
            System.setProperty(quorumX509Util.getSslKeystoreLocationProperty(), revokedInOCSPKeystorePath);
            // This server should join successfully
            q3 = new QuorumPeerTestBase.MainThread(3, clientPortQp3, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
            q3.start();
            Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
            q1.shutdown();
            q2.shutdown();
            q3.shutdown();
            Assert.assertTrue(ClientBase.waitForServerDown(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
            Assert.assertTrue(ClientBase.waitForServerDown(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
            Assert.assertTrue(ClientBase.waitForServerDown(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
            setSSLSystemProperties();
            System.setProperty(quorumX509Util.getSslOcspEnabledProperty(), "true");
            X509Certificate validCertificate = buildEndEntityCert(defaultKeyPair, rootCertificate, rootKeyPair.getPrivate(), QuorumSSLTest.HOSTNAME, null, null, ocspPort);
            writeKeystore(validCertificate, defaultKeyPair, validKeystorePath);
            q1.start();
            q2.start();
            Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
            Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
            System.setProperty(quorumX509Util.getSslKeystoreLocationProperty(), revokedInOCSPKeystorePath);
            q3.start();
            Assert.assertFalse(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
        } finally {
            ocspServer.stop(0);
        }
    }

    @Test
    public void testCipherSuites() throws Exception {
        // Get default cipher suites from JDK
        SSLServerSocketFactory ssf = ((SSLServerSocketFactory) (SSLServerSocketFactory.getDefault()));
        List<String> defaultCiphers = new ArrayList<String>();
        for (String cipher : ssf.getDefaultCipherSuites()) {
            if (((!(cipher.matches(".*EMPTY.*"))) && (cipher.startsWith("TLS"))) && (cipher.contains("RSA"))) {
                defaultCiphers.add(cipher);
            }
        }
        if ((defaultCiphers.size()) < 2) {
            Assert.fail("JDK has to support at least 2 valid (RSA) cipher suites for this test to run");
        }
        // Use them all except one to build the ensemble
        String suitesOfEnsemble = String.join(",", defaultCiphers.subList(1, defaultCiphers.size()));
        System.setProperty(quorumX509Util.getCipherSuitesProperty(), suitesOfEnsemble);
        q1 = new QuorumPeerTestBase.MainThread(1, clientPortQp1, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q2 = new QuorumPeerTestBase.MainThread(2, clientPortQp2, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q1.start();
        q2.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        // Use the odd one out for the client
        String suiteOfClient = defaultCiphers.get(0);
        System.setProperty(quorumX509Util.getCipherSuitesProperty(), suiteOfClient);
        // This server should fail to join the quorum as it is not using one of the supported suites from the other
        // quorum members
        q3 = new QuorumPeerTestBase.MainThread(3, clientPortQp3, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q3.start();
        Assert.assertFalse(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
    }

    @Test
    public void testProtocolVersion() throws Exception {
        System.setProperty(quorumX509Util.getSslProtocolProperty(), "TLSv1.2");
        q1 = new QuorumPeerTestBase.MainThread(1, clientPortQp1, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q2 = new QuorumPeerTestBase.MainThread(2, clientPortQp2, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q1.start();
        q2.start();
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp1)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp2)), ClientBase.CONNECTION_TIMEOUT));
        System.setProperty(quorumX509Util.getSslProtocolProperty(), "TLSv1.1");
        // This server should fail to join the quorum as it is not using TLSv1.2
        q3 = new QuorumPeerTestBase.MainThread(3, clientPortQp3, quorumConfiguration, QuorumSSLTest.SSL_QUORUM_ENABLED);
        q3.start();
        Assert.assertFalse(ClientBase.waitForServerUp(("127.0.0.1:" + (clientPortQp3)), ClientBase.CONNECTION_TIMEOUT));
    }
}

