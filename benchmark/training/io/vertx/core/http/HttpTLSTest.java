/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.http;


import ClientAuth.REQUIRED;
import HttpMethod.CONNECT;
import HttpMethod.POST;
import ProxyType.HTTP;
import ProxyType.SOCKS5;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.ProxyType;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.tls.Cert;
import io.vertx.test.tls.Trust;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import javax.security.cert.X509Certificate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static HttpVersion.HTTP_1_1;
import static ProxyType.SOCKS5;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public abstract class HttpTLSTest extends HttpTestBase {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    // Client trusts all server certs
    @Test
    public void testTLSClientTrustAll() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll().pass();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustServerCert() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustServerCertPKCS12() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_PKCS12, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustServerCertPEM() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_PEM, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertJKSRootCAWithJKSRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS_ROOT_CA, Cert.SERVER_JKS_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertJKSRootCAWithPKCS12RootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PKCS12_ROOT_CA, Cert.SERVER_JKS_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertJKSRootRootCAWithPEMRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA, Cert.SERVER_JKS_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPKCS12RootCAWithJKSRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS_ROOT_CA, Cert.SERVER_PKCS12_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPKCS12RootCAWithPKCS12RootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PKCS12_ROOT_CA, Cert.SERVER_PKCS12_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPKCS12RootCAWithPEMRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA, Cert.SERVER_PKCS12_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPEMRootCAWithJKSRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS_ROOT_CA, Cert.SERVER_PEM_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPEMRootCAWithPKCS12RootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PKCS12_ROOT_CA, Cert.SERVER_PEM_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a root CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPEMRootCAWithPEMRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA, Cert.SERVER_PEM_ROOT_CA, Trust.NONE).pass();
    }

    // These two tests should be grouped in same method - todo later
    // Server specifies cert that the client trusts via a root CA that is in a multi pem store (not trust all)
    @Test
    public void testTLSClientTrustServerCertMultiPemWithPEMRootCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA_AND_OTHER_CA, Cert.SERVER_PEM_ROOT_CA, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts via a other CA that is in a multi pem store (not trust all)
    @Test
    public void testTLSClientTrustServerCertMultiPemWithPEMOtherCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA_AND_OTHER_CA, Cert.SERVER_PEM_OTHER_CA, Trust.NONE).pass();
    }

    // Server specifies cert chain that the client trusts via a CA (not trust all)
    @Test
    public void testTLSClientTrustServerCertPEMRootCAWithPEMCAChain() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA, Cert.SERVER_PEM_CA_CHAIN, Trust.NONE).pass();
    }

    // Server specifies intermediate cert that the client doesn't trust because it is missing the intermediate CA signed by the root CA
    @Test
    public void testTLSClientUntrustedServerCertPEMRootCAWithPEMCA() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA, Cert.SERVER_PEM_INT_CA, Trust.NONE).fail();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustPKCS12ServerCert() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PKCS12, Cert.SERVER_JKS, Trust.NONE).pass();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustPEMServerCert() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM, Cert.SERVER_JKS, Trust.NONE).pass();
    }

    // Server specifies cert that the client doesn't trust
    @Test
    public void testTLSClientUntrustedServer() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).fail();
    }

    // Server specifies cert that the client doesn't trust
    @Test
    public void testTLSClientUntrustedServerPEM() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_PEM, Trust.NONE).fail();
    }

    // Client specifies cert even though it's not required
    @Test
    public void testTLSClientCertNotRequired() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).pass();
    }

    // Client specifies cert even though it's not required
    @Test
    public void testTLSClientCertNotRequiredPEM() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_PEM, Trust.CLIENT_JKS).pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertRequired() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).requiresClientAuth().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertRequiredPKCS12() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_PKCS12).requiresClientAuth().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertRequiredPEM() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_PEM).requiresClientAuth().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertPKCS12Required() throws Exception {
        testTLS(Cert.CLIENT_PKCS12, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).requiresClientAuth().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertPEMRequired() throws Exception {
        testTLS(Cert.CLIENT_PEM, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).requiresClientAuth().pass();
    }

    // Client specifies cert by CA and it is required
    @Test
    public void testTLSClientCertPEM_CARequired() throws Exception {
        testTLS(Cert.CLIENT_PEM_ROOT_CA, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_PEM_ROOT_CA).requiresClientAuth().pass();
    }

    // Client doesn't specify cert but it's required
    @Test
    public void testTLSClientCertRequiredNoClientCert() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).requiresClientAuth().fail();
    }

    // Client specifies cert but it's not trusted
    @Test
    public void testTLSClientCertClientNotTrusted() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).requiresClientAuth().fail();
    }

    // Server specifies cert that the client does not trust via a revoked certificate of the CA
    @Test
    public void testTLSClientRevokedServerCert() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM_ROOT_CA, Cert.SERVER_PEM_ROOT_CA, Trust.NONE).clientUsesCrl().fail();
    }

    // Client specifies cert that the server does not trust via a revoked certificate of the CA
    @Test
    public void testTLSRevokedClientCertServer() throws Exception {
        testTLS(Cert.CLIENT_PEM_ROOT_CA, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_PEM_ROOT_CA).requiresClientAuth().serverUsesCrl().fail();
    }

    // Specify some matching cipher suites
    @Test
    public void testTLSMatchingCipherSuites() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll().serverEnabledCipherSuites(VertxTestBase.ENABLED_CIPHER_SUITES).pass();
    }

    // Specify some non matching cipher suites
    @Test
    public void testTLSNonMatchingCipherSuites() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll().serverEnabledCipherSuites(new String[]{ "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256" }).clientEnabledCipherSuites(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256" }).fail();
    }

    // Specify some matching TLS protocols
    @Test
    public void testTLSMatchingProtocolVersions() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll().serverEnabledSecureTransportProtocol(new String[]{ "SSLv2Hello", "TLSv1", "TLSv1.1", "TLSv1.2" }).pass();
    }

    /* checks that we can enable algorithms

    static
    {
    Security.setProperty("jdk.tls.disabledAlgorithms", "");
    }

    @Test
    public void testEnableProtocols() throws Exception {
    testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll()
    .clientEnabledSecureTransportProtocol(new String[]{"SSLv3"})
    .serverEnabledSecureTransportProtocol(new String[]{"SSLv3"})
    .pass();
    }
     */
    // Specify some matching TLS protocols
    @Test
    public void testTLSInvalidProtocolVersion() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll().serverEnabledSecureTransportProtocol(new String[]{ "HelloWorld" }).fail();
    }

    // Specify some non matching TLS protocols
    @Test
    public void testTLSNonMatchingProtocolVersions() throws Exception {
        testTLS(Cert.NONE, Trust.NONE, Cert.SERVER_JKS, Trust.NONE).clientTrustAll().serverEnabledSecureTransportProtocol(new String[]{ "TLSv1.2" }).clientEnabledSecureTransportProtocol(new String[]{ "SSLv2Hello", "TLSv1.1" }).fail();
    }

    // Test host verification with a CN matching localhost
    @Test
    public void testTLSVerifyMatchingHost() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).clientVerifyHost().pass();
    }

    // Test host verification with a CN NOT matching localhost
    @Test
    public void testTLSVerifyNonMatchingHost() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_MIM, Trust.NONE).clientVerifyHost().fail();
    }

    // Test host verification with a CN matching localhost
    @Test
    public void testTLSVerifyMatchingHostOpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).clientVerifyHost().clientOpenSSL().pass();
    }

    // Test host verification with a CN NOT matching localhost
    @Test
    public void testTLSVerifyNonMatchingHostOpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_MIM, Trust.NONE).clientVerifyHost().clientOpenSSL().fail();
    }

    // OpenSSL tests
    // Server uses OpenSSL with JKS
    @Test
    public void testTLSClientTrustServerCertJKSOpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).serverOpenSSL().pass();
    }

    // Server uses OpenSSL with PKCS12
    @Test
    public void testTLSClientTrustServerCertPKCS12OpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_PKCS12, Trust.NONE).serverOpenSSL().pass();
    }

    // Server uses OpenSSL with PEM
    @Test
    public void testTLSClientTrustServerCertPEMOpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_PEM, Trust.NONE).serverOpenSSL().pass();
    }

    // Client trusts OpenSSL with PEM
    @Test
    public void testTLSClientTrustServerCertWithJKSOpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).clientOpenSSL().pass();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustServerCertWithPKCS12OpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PKCS12, Cert.SERVER_JKS, Trust.NONE).clientOpenSSL().pass();
    }

    // Server specifies cert that the client trusts (not trust all)
    @Test
    public void testTLSClientTrustServerCertWithPEMOpenSSL() throws Exception {
        testTLS(Cert.NONE, Trust.SERVER_PEM, Cert.SERVER_JKS, Trust.NONE).clientOpenSSL().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertRequiredOpenSSL() throws Exception {
        testTLS(Cert.CLIENT_JKS, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).clientOpenSSL().requiresClientAuth().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertPKCS12RequiredOpenSSL() throws Exception {
        testTLS(Cert.CLIENT_PKCS12, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).clientOpenSSL().requiresClientAuth().pass();
    }

    // Client specifies cert and it is required
    @Test
    public void testTLSClientCertPEMRequiredOpenSSL() throws Exception {
        testTLS(Cert.CLIENT_PEM, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.CLIENT_JKS).clientOpenSSL().requiresClientAuth().pass();
    }

    // SNI tests
    // Client provides SNI and server responds with a matching certificate for the indicated server name
    @Test
    public void testSNITrust() throws Exception {
        HttpTLSTest.TLSTest test = testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).pass();
        assertEquals("host2.com", TestUtils.cnOf(test.clientPeerCert()));
        assertEquals("host2.com", test.indicatedServerName);
    }

    // Client provides SNI and server responds with a matching certificate for the indicated server name
    @Test
    public void testSNITrustPKCS12() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_PKCS12, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).pass().clientPeerCert();
        assertEquals("host2.com", TestUtils.cnOf(cert));
    }

    // Client provides SNI and server responds with a matching certificate for the indicated server name
    @Test
    public void testSNITrustPEM() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_PEM, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).pass().clientPeerCert();
        assertEquals("host2.com", TestUtils.cnOf(cert));
    }

    // Client provides SNI but server ignores it and provides a different cerficate
    @Test
    public void testSNIServerIgnoresExtension1() throws Exception {
        testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.NONE).requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).fail();
    }

    // Client provides SNI but server ignores it and provides a different cerficate - check we get a certificate
    @Test
    public void testSNIServerIgnoresExtension2() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SNI_JKS, Trust.NONE).clientVerifyHost(false).requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).pass().clientPeerCert();
        assertEquals("localhost", TestUtils.cnOf(cert));
    }

    // Client provides SNI unknown to the server and server responds with the default certificate (first)
    @Test
    public void testSNIUnknownServerName1() throws Exception {
        testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("unknown.com")).fail();
    }

    // Client provides SNI unknown to the server and server responds with the default certificate (first)
    @Test
    public void testSNIUnknownServerName2() throws Exception {
        HttpTLSTest.TLSTest test = testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SNI_JKS, Trust.NONE).serverSni().clientVerifyHost(false).requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("unknown.com")).pass();
        assertEquals("localhost", TestUtils.cnOf(test.clientPeerCert()));
        assertEquals("unknown.com", test.indicatedServerName);
    }

    // Client provides SNI matched on the server by a wildcard certificate
    @Test
    public void testSNIWildcardMatch() throws Exception {
        HttpTLSTest.TLSTest test = testTLS(Cert.NONE, Trust.SNI_JKS_HOST3, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("sub.host3.com")).pass();
        assertEquals("*.host3.com", TestUtils.cnOf(test.clientPeerCert()));
        assertEquals("sub.host3.com", test.indicatedServerName);
    }

    // Client provides SNI matched on the server by a wildcard certificate
    @Test
    public void testSNIWildcardMatchPKCS12() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST3, Cert.SNI_PKCS12, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("sub.host3.com")).pass().clientPeerCert();
        assertEquals("*.host3.com", TestUtils.cnOf(cert));
    }

    // Client provides SNI matched on the server by a wildcard certificate
    @Test
    public void testSNIWildcardMatchPEM() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST3, Cert.SNI_PEM, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("sub.host3.com")).pass().clientPeerCert();
        assertEquals("*.host3.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameMatch1() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST4, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host4.com")).pass().clientPeerCert();
        assertEquals("host4.com certificate", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameMatch1PKCS12() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST4, Cert.SNI_PKCS12, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host4.com")).pass().clientPeerCert();
        assertEquals("host4.com certificate", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameMatch1PEM() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST4, Cert.SNI_PEM, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host4.com")).pass().clientPeerCert();
        assertEquals("host4.com certificate", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameMatch2() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST4, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("www.host4.com")).pass().clientPeerCert();
        assertEquals("host4.com certificate", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameMatch2PKCS12() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST4, Cert.SNI_PKCS12, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("www.host4.com")).pass().clientPeerCert();
        assertEquals("host4.com certificate", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameMatch2PEM() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST4, Cert.SNI_PEM, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("www.host4.com")).pass().clientPeerCert();
        assertEquals("host4.com certificate", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameWildcardMatch() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("www.host5.com")).pass().clientPeerCert();
        assertEquals("host5.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameWildcardMatchPKCS12() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_PKCS12, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("www.host5.com")).pass().clientPeerCert();
        assertEquals("host5.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAlternativeNameWildcardMatchPEM() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_PEM, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("www.host5.com")).pass().clientPeerCert();
        assertEquals("host5.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAltenativeNameCNMatch1() throws Exception {
        testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host5.com")).fail().clientPeerCert();
    }

    @Test
    public void testSNISubjectAltenativeNameCNMatch1PKCS12() throws Exception {
        testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_PKCS12, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host5.com")).fail().clientPeerCert();
    }

    @Test
    public void testSNISubjectAltenativeNameCNMatch1PEM() throws Exception {
        testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_PEM, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host5.com")).fail().clientPeerCert();
    }

    @Test
    public void testSNISubjectAltenativeNameCNMatch2() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_JKS, Trust.NONE).serverSni().clientVerifyHost(false).requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host5.com")).pass().clientPeerCert();
        assertEquals("host5.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAltenativeNameCNMatch2PKCS12() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_PKCS12, Trust.NONE).serverSni().clientVerifyHost(false).requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host5.com")).pass().clientPeerCert();
        assertEquals("host5.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNISubjectAltenativeNameCNMatch2PEM() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST5, Cert.SNI_PEM, Trust.NONE).serverSni().clientVerifyHost(false).requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host5.com")).pass().clientPeerCert();
        assertEquals("host5.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNIWithALPN() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.NONE).serverSni().clientUsesAlpn().serverUsesAlpn().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).pass().clientPeerCert();
        assertEquals("host2.com", TestUtils.cnOf(cert));
    }

    // Client provides SNI and server responds with a matching certificate for the indicated server name
    @Test
    public void testSNIWithHostHeader() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.NONE).serverSni().requestProvider(( client, handler) -> client.post(4043, "localhost", "/somepath", handler).setHost("host2.com:4043")).pass().clientPeerCert();
        assertEquals("host2.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNIWithOpenSSL() throws Exception {
        X509Certificate cert = testTLS(Cert.NONE, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.NONE).clientOpenSSL().serverOpenSSL().serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).pass().clientPeerCert();
        assertEquals("host2.com", TestUtils.cnOf(cert));
    }

    @Test
    public void testSNIDontSendServerNameForShortnames1() throws Exception {
        testTLS(Cert.NONE, Trust.SNI_JKS_HOST1, Cert.SNI_JKS, Trust.NONE).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host1")).fail();
    }

    @Test
    public void testSNIDontSendServerNameForShortnames2() throws Exception {
        HttpTLSTest.TLSTest test = testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SNI_JKS, Trust.NONE).clientVerifyHost(false).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host1")).pass();
        assertEquals(null, test.indicatedServerName);
    }

    @Test
    public void testSNIForceSend() throws Exception {
        HttpTLSTest.TLSTest test = testTLS(Cert.NONE, Trust.SNI_JKS_HOST1, Cert.SNI_JKS, Trust.NONE).clientForceSni().serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host1")).pass();
        assertEquals("host1", test.indicatedServerName);
    }

    @Test
    public void testSNIWithServerNameTrust() throws Exception {
        testTLS(Cert.CLIENT_PEM_ROOT_CA, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.SNI_SERVER_ROOT_CA_AND_OTHER_CA_1).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).requiresClientAuth().pass();
    }

    @Test
    public void testSNIWithServerNameTrustFallback() throws Exception {
        testTLS(Cert.CLIENT_PEM_ROOT_CA, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.SNI_SERVER_ROOT_CA_FALLBACK).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).requiresClientAuth().pass();
    }

    @Test
    public void testSNIWithServerNameTrustFallbackFail() throws Exception {
        testTLS(Cert.CLIENT_PEM_ROOT_CA, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.SNI_SERVER_OTHER_CA_FALLBACK).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).requiresClientAuth().fail();
    }

    @Test
    public void testSNIWithServerNameTrustFail() throws Exception {
        testTLS(Cert.CLIENT_PEM_ROOT_CA, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, Trust.SNI_SERVER_ROOT_CA_AND_OTHER_CA_2).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).requiresClientAuth().fail();
    }

    @Test
    public void testSNICustomTrustManagerFactoryMapper() throws Exception {
        testTLS(Cert.CLIENT_PEM, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, () -> new TrustOptions() {
            @Override
            public JsonObject toJson() {
                throw new UnsupportedOperationException();
            }

            @Override
            public TrustManagerFactory getTrustManagerFactory(Vertx v) throws Exception {
                return new TrustManagerFactory(new TrustManagerFactorySpi() {
                    @Override
                    protected void engineInit(KeyStore keyStore) throws KeyStoreException {
                    }

                    @Override
                    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
                    }

                    @Override
                    protected TrustManager[] engineGetTrustManagers() {
                        return new TrustManager[]{ TrustAllTrustManager.INSTANCE };
                    }
                }, KeyPairGenerator.getInstance("RSA").getProvider(), KeyPairGenerator.getInstance("RSA").getAlgorithm()) {};
            }

            @Override
            public TrustOptions copy() {
                return this;
            }
        }).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).requiresClientAuth().pass();
    }

    @Test
    public void testSNICustomTrustManagerFactoryMapper2() throws Exception {
        testTLS(Cert.CLIENT_PEM, Trust.SNI_JKS_HOST2, Cert.SNI_JKS, () -> new TrustOptions() {
            @Override
            public JsonObject toJson() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Function<String, TrustManager[]> trustManagerMapper(Vertx v) throws Exception {
                return ( serverName) -> new TrustManager[]{ TrustAllTrustManager.INSTANCE };
            }

            @Override
            public TrustManagerFactory getTrustManagerFactory(Vertx v) throws Exception {
                return new TrustManagerFactory(new TrustManagerFactorySpi() {
                    @Override
                    protected void engineInit(KeyStore keyStore) throws KeyStoreException {
                    }

                    @Override
                    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
                    }

                    @Override
                    protected TrustManager[] engineGetTrustManagers() {
                        return new TrustManager[]{ TrustAllTrustManager.INSTANCE };
                    }
                }, KeyPairGenerator.getInstance("RSA").getProvider(), KeyPairGenerator.getInstance("RSA").getAlgorithm()) {};
            }

            @Override
            public TrustOptions copy() {
                return this;
            }
        }).serverSni().requestOptions(new RequestOptions().setSsl(true).setPort(4043).setHost("host2.com")).requiresClientAuth().pass();
    }

    // Other tests
    // Test custom trust manager factory
    @Test
    public void testCustomTrustManagerFactory() throws Exception {
        testTLS(Cert.NONE, () -> new TrustOptions() {
            @Override
            public JsonObject toJson() {
                throw new UnsupportedOperationException();
            }

            @Override
            public TrustManagerFactory getTrustManagerFactory(Vertx v) throws Exception {
                return new TrustManagerFactory(new TrustManagerFactorySpi() {
                    @Override
                    protected void engineInit(KeyStore keyStore) throws KeyStoreException {
                    }

                    @Override
                    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
                    }

                    @Override
                    protected TrustManager[] engineGetTrustManagers() {
                        return new TrustManager[]{ TrustAllTrustManager.INSTANCE };
                    }
                }, KeyPairGenerator.getInstance("RSA").getProvider(), KeyPairGenerator.getInstance("RSA").getAlgorithm()) {};
            }

            @Override
            public TrustOptions copy() {
                return this;
            }
        }, Cert.SERVER_JKS, Trust.NONE).pass();
    }

    class TLSTest {
        HttpVersion version;

        KeyCertOptions clientCert;

        io.vertx.core.net.TrustOptions clientTrust;

        boolean clientTrustAll;

        boolean clientUsesCrl;

        boolean clientUsesAlpn;

        boolean clientOpenSSL;

        boolean clientVerifyHost = true;

        boolean clientSSL = true;

        boolean requiresClientAuth;

        KeyCertOptions serverCert;

        io.vertx.core.net.TrustOptions serverTrust;

        boolean serverUsesCrl;

        boolean serverOpenSSL;

        boolean serverUsesAlpn;

        boolean serverSSL = true;

        ProxyType proxyType;

        boolean useProxyAuth;

        String[] clientEnabledCipherSuites = new String[0];

        String[] serverEnabledCipherSuites = new String[0];

        String[] clientEnabledSecureTransportProtocol = new String[0];

        String[] serverEnabledSecureTransportProtocol = new String[0];

        private String connectHostname;

        private boolean followRedirects;

        private boolean serverSNI;

        private boolean clientForceSNI;

        private BiFunction<HttpClient, Handler<AsyncResult<HttpClientResponse>>, HttpClientRequest> requestProvider = ( client, handler) -> {
            String httpHost;
            if ((connectHostname) != null) {
                httpHost = connectHostname;
            } else {
                httpHost = HttpTestBase.DEFAULT_HTTP_HOST;
            }
            return client.request(POST, 4043, httpHost, HttpTestBase.DEFAULT_TEST_URI, handler);
        };

        X509Certificate clientPeerCert;

        String indicatedServerName;

        public TLSTest(Cert<?> clientCert, Trust<?> clientTrust, Cert<?> serverCert, Trust<?> serverTrust) {
            this.version = HTTP_1_1;
            this.clientCert = clientCert.get();
            this.clientTrust = clientTrust.get();
            this.serverCert = serverCert.get();
            this.serverTrust = serverTrust.get();
        }

        HttpTLSTest.TLSTest version(HttpVersion version) {
            this.version = version;
            return this;
        }

        HttpTLSTest.TLSTest requiresClientAuth() {
            requiresClientAuth = true;
            return this;
        }

        HttpTLSTest.TLSTest serverUsesCrl() {
            serverUsesCrl = true;
            return this;
        }

        HttpTLSTest.TLSTest serverOpenSSL() {
            serverOpenSSL = true;
            return this;
        }

        HttpTLSTest.TLSTest clientOpenSSL() {
            clientOpenSSL = true;
            return this;
        }

        HttpTLSTest.TLSTest clientUsesCrl() {
            clientUsesCrl = true;
            return this;
        }

        HttpTLSTest.TLSTest clientTrustAll() {
            clientTrustAll = true;
            return this;
        }

        HttpTLSTest.TLSTest clientVerifyHost() {
            clientVerifyHost = true;
            return this;
        }

        HttpTLSTest.TLSTest clientVerifyHost(boolean verify) {
            clientVerifyHost = verify;
            return this;
        }

        HttpTLSTest.TLSTest clientEnabledCipherSuites(String[] value) {
            clientEnabledCipherSuites = value;
            return this;
        }

        HttpTLSTest.TLSTest serverEnabledCipherSuites(String[] value) {
            serverEnabledCipherSuites = value;
            return this;
        }

        HttpTLSTest.TLSTest clientEnabledSecureTransportProtocol(String[] value) {
            clientEnabledSecureTransportProtocol = value;
            return this;
        }

        HttpTLSTest.TLSTest serverEnabledSecureTransportProtocol(String[] value) {
            serverEnabledSecureTransportProtocol = value;
            return this;
        }

        HttpTLSTest.TLSTest serverSni() {
            serverSNI = true;
            return this;
        }

        HttpTLSTest.TLSTest clientForceSni() {
            clientForceSNI = true;
            return this;
        }

        HttpTLSTest.TLSTest clientUsesAlpn() {
            clientUsesAlpn = true;
            return this;
        }

        HttpTLSTest.TLSTest serverUsesAlpn() {
            serverUsesAlpn = true;
            return this;
        }

        HttpTLSTest.TLSTest useProxy(ProxyType type) {
            proxyType = type;
            return this;
        }

        HttpTLSTest.TLSTest useProxyAuth() {
            useProxyAuth = true;
            return this;
        }

        HttpTLSTest.TLSTest connectHostname(String connectHostname) {
            this.connectHostname = connectHostname;
            return this;
        }

        HttpTLSTest.TLSTest requestOptions(RequestOptions requestOptions) {
            this.requestProvider = ( client, handler) -> client.request(HttpMethod.POST, requestOptions, handler);
            return this;
        }

        HttpTLSTest.TLSTest requestProvider(BiFunction<HttpClient, Handler<AsyncResult<HttpClientResponse>>, HttpClientRequest> requestProvider) {
            this.requestProvider = requestProvider;
            return this;
        }

        HttpTLSTest.TLSTest clientSSL(boolean ssl) {
            clientSSL = ssl;
            return this;
        }

        HttpTLSTest.TLSTest serverSSL(boolean ssl) {
            serverSSL = ssl;
            return this;
        }

        HttpTLSTest.TLSTest followRedirects(boolean follow) {
            followRedirects = follow;
            return this;
        }

        public X509Certificate clientPeerCert() {
            return clientPeerCert;
        }

        HttpTLSTest.TLSTest pass() {
            return run(true);
        }

        HttpTLSTest.TLSTest fail() {
            return run(false);
        }

        HttpTLSTest.TLSTest run(boolean shouldPass) {
            if (((proxyType) == null) || shouldPass) {
                // The test with proxy that fails will not connect
                waitFor(2);
            }
            server.close();
            HttpClientOptions options = new HttpClientOptions();
            options.setProtocolVersion(version);
            options.setSsl(clientSSL);
            options.setForceSni(clientForceSNI);
            if (clientTrustAll) {
                options.setTrustAll(true);
            }
            if (clientUsesCrl) {
                options.addCrlPath("tls/root-ca/crl.pem");
            }
            if (clientOpenSSL) {
                options.setOpenSslEngineOptions(new OpenSSLEngineOptions());
            }
            if (clientUsesAlpn) {
                options.setUseAlpn(true);
            }
            options.setVerifyHost(clientVerifyHost);
            options.setTrustOptions(clientTrust);
            options.setKeyCertOptions(clientCert);
            for (String suite : clientEnabledCipherSuites) {
                options.addEnabledCipherSuite(suite);
            }
            if ((clientEnabledSecureTransportProtocol.length) > 0) {
                options.getEnabledSecureTransportProtocols().forEach(options::removeEnabledSecureTransportProtocol);
            }
            for (String protocol : clientEnabledSecureTransportProtocol) {
                options.addEnabledSecureTransportProtocol(protocol);
            }
            if ((proxyType) != null) {
                ProxyOptions proxyOptions;
                if ((proxyType) == (SOCKS5)) {
                    proxyOptions = new ProxyOptions().setHost("localhost").setPort(11080).setType(SOCKS5);
                } else {
                    proxyOptions = new ProxyOptions().setHost("localhost").setPort(13128).setType(HTTP);
                }
                if (useProxyAuth) {
                    proxyOptions.setUsername("username").setPassword("username");
                }
                options.setProxyOptions(proxyOptions);
            }
            client = createHttpClient(options);
            HttpServerOptions serverOptions = new HttpServerOptions();
            serverOptions.setTrustOptions(serverTrust);
            serverOptions.setKeyCertOptions(serverCert);
            if (requiresClientAuth) {
                serverOptions.setClientAuth(REQUIRED);
            }
            if (serverUsesCrl) {
                serverOptions.addCrlPath("tls/root-ca/crl.pem");
            }
            if (serverOpenSSL) {
                serverOptions.setOpenSslEngineOptions(new OpenSSLEngineOptions());
            }
            serverOptions.setUseAlpn(serverUsesAlpn);
            serverOptions.setSsl(serverSSL);
            serverOptions.setSni(serverSNI);
            for (String suite : serverEnabledCipherSuites) {
                serverOptions.addEnabledCipherSuite(suite);
            }
            if ((serverEnabledSecureTransportProtocol.length) > 0) {
                serverOptions.getEnabledSecureTransportProtocols().forEach(serverOptions::removeEnabledSecureTransportProtocol);
            }
            for (String protocol : serverEnabledSecureTransportProtocol) {
                serverOptions.addEnabledSecureTransportProtocol(protocol);
            }
            server = createHttpServer(serverOptions.setPort(4043));
            server.connectionHandler(( conn) -> complete());
            AtomicInteger count = new AtomicInteger();
            server.exceptionHandler(( err) -> {
                if (shouldPass) {
                    HttpTLSTest.this.fail(err);
                } else {
                    if ((count.incrementAndGet()) == 1) {
                        complete();
                    }
                }
            });
            server.requestHandler(( req) -> {
                indicatedServerName = req.connection().indicatedServerName();
                assertEquals(version, req.version());
                assertEquals(serverSSL, req.isSSL());
                if (((req.method()) == HttpMethod.GET) || ((req.method()) == HttpMethod.HEAD)) {
                    req.response().end();
                } else {
                    req.bodyHandler(( buffer) -> {
                        assertEquals("foo", buffer.toString());
                        req.response().end("bar");
                    });
                }
            });
            server.listen(( ar) -> {
                assertTrue(ar.succeeded());
                String httpHost;
                if ((connectHostname) != null) {
                    httpHost = connectHostname;
                } else {
                    httpHost = HttpTestBase.DEFAULT_HTTP_HOST;
                }
                HttpClientRequest req = requestProvider.apply(client, ( ar2) -> {
                    if (ar2.succeeded()) {
                        HttpClientResponse response = ar2.result();
                        HttpConnection conn = response.request().connection();
                        if (conn.isSsl()) {
                            try {
                                clientPeerCert = conn.peerCertificateChain()[0];
                            } catch ( ignore) {
                            }
                        }
                        if (shouldPass) {
                            response.version();
                            HttpMethod method = response.request().method();
                            if ((method == HttpMethod.GET) || (method == HttpMethod.HEAD)) {
                                complete();
                            } else {
                                response.bodyHandler(( data) -> {
                                    assertEquals("bar", data.toString());
                                    complete();
                                });
                            }
                        } else {
                            HttpTLSTest.this.fail("Should not get a response");
                        }
                    } else {
                        Throwable t = ar.cause();
                        if (shouldPass) {
                            t.printStackTrace();
                            HttpTLSTest.this.fail("Should not throw exception");
                        } else {
                            complete();
                        }
                    }
                });
                req.setFollowRedirects(followRedirects);
                req.end("foo");
            });
            await();
            return this;
        }
    }

    @Test
    public void testJKSInvalidPath() {
        testInvalidKeyStore(Cert.SERVER_JKS.get().setPath("/invalid.jks"), "java.nio.file.NoSuchFileException: ", "invalid.jks");
    }

    @Test
    public void testJKSMissingPassword() {
        testInvalidKeyStore(Cert.SERVER_JKS.get().setPassword(null), "Password must not be null", null);
    }

    @Test
    public void testJKSInvalidPassword() {
        testInvalidKeyStore(Cert.SERVER_JKS.get().setPassword("wrongpassword"), "Keystore was tampered with, or password was incorrect", null);
    }

    @Test
    public void testPKCS12InvalidPath() {
        testInvalidKeyStore(Cert.SERVER_PKCS12.get().setPath("/invalid.p12"), "java.nio.file.NoSuchFileException: ", "invalid.p12");
    }

    @Test
    public void testPKCS12MissingPassword() {
        testInvalidKeyStore(Cert.SERVER_PKCS12.get().setPassword(null), "Get Key failed: null", null);
    }

    @Test
    public void testPKCS12InvalidPassword() {
        testInvalidKeyStore(Cert.SERVER_PKCS12.get().setPassword("wrongpassword"), Arrays.asList("failed to decrypt safe contents entry: javax.crypto.BadPaddingException: Given final block not properly padded", "keystore password was incorrect"), null);
    }

    @Test
    public void testKeyCertMissingKeyPath() {
        testInvalidKeyStore(Cert.SERVER_PEM.get().setKeyPath(null), "Missing private key", null);
    }

    @Test
    public void testKeyCertInvalidKeyPath() {
        testInvalidKeyStore(Cert.SERVER_PEM.get().setKeyPath("/invalid.pem"), "java.nio.file.NoSuchFileException: ", "invalid.pem");
    }

    @Test
    public void testKeyCertMissingCertPath() {
        testInvalidKeyStore(Cert.SERVER_PEM.get().setCertPath(null), "Missing X.509 certificate", null);
    }

    @Test
    public void testKeyCertInvalidCertPath() {
        testInvalidKeyStore(Cert.SERVER_PEM.get().setCertPath("/invalid.pem"), "java.nio.file.NoSuchFileException: ", "invalid.pem");
    }

    @Test
    public void testKeyCertInvalidPem() throws IOException {
        String[] contents = new String[]{ "", "-----BEGIN PRIVATE KEY-----", "-----BEGIN RSA PRIVATE KEY-----", "-----BEGIN PRIVATE KEY-----\n-----END PRIVATE KEY-----", "-----BEGIN RSA PRIVATE KEY-----\n-----END RSA PRIVATE KEY-----", "-----BEGIN PRIVATE KEY-----\n*\n-----END PRIVATE KEY-----", "-----BEGIN RSA PRIVATE KEY-----\n*\n-----END RSA PRIVATE KEY-----" };
        String[] messages = new String[]{ "Missing -----BEGIN PRIVATE KEY----- or -----BEGIN RSA PRIVATE KEY----- delimiter", "Missing -----END PRIVATE KEY----- delimiter", "Missing -----END RSA PRIVATE KEY----- delimiter", "Empty pem file", "Empty pem file", "Input byte[] should at least have 2 bytes for base64 bytes", "Input byte[] should at least have 2 bytes for base64 bytes" };
        for (int i = 0; i < (contents.length); i++) {
            Path file = testFolder.newFile((("vertx" + (UUID.randomUUID().toString())) + ".pem")).toPath();
            Files.write(file, Collections.singleton(contents[i]));
            String expectedMessage = messages[i];
            testInvalidKeyStore(Cert.SERVER_PEM.get().setKeyPath(file.toString()), expectedMessage, null);
        }
    }

    @Test
    public void testNoKeyCert() {
        testInvalidKeyStore(null, "Key/certificate is mandatory for SSL", null);
    }

    @Test
    public void testCaInvalidPath() {
        testInvalidTrustStore(new PemTrustOptions().addCertPath("/invalid.pem"), "java.nio.file.NoSuchFileException: ", "invalid.pem");
    }

    @Test
    public void testCaInvalidPem() throws IOException {
        String[] contents = new String[]{ "", "-----BEGIN CERTIFICATE-----", "-----BEGIN CERTIFICATE-----\n-----END CERTIFICATE-----", "-----BEGIN CERTIFICATE-----\n*\n-----END CERTIFICATE-----" };
        String[] messages = new String[]{ "Missing -----BEGIN CERTIFICATE----- delimiter", "Missing -----END CERTIFICATE----- delimiter", "Empty pem file", "Input byte[] should at least have 2 bytes for base64 bytes" };
        for (int i = 0; i < (contents.length); i++) {
            Path file = testFolder.newFile((("vertx" + (UUID.randomUUID().toString())) + ".pem")).toPath();
            Files.write(file, Collections.singleton(contents[i]));
            String expectedMessage = messages[i];
            testInvalidTrustStore(new PemTrustOptions().addCertPath(file.toString()), expectedMessage, null);
        }
    }

    @Test
    public void testCrlInvalidPath() throws Exception {
        HttpClientOptions clientOptions = new HttpClientOptions();
        clientOptions.setTrustOptions(Trust.SERVER_PEM_ROOT_CA.get());
        clientOptions.setSsl(true);
        clientOptions.addCrlPath("/invalid.pem");
        try {
            vertx.createHttpClient(clientOptions);
            fail("Was expecting a failure");
        } catch (VertxException e) {
            assertNotNull(e.getCause());
            assertEquals(NoSuchFileException.class, e.getCause().getCause().getClass());
        }
    }

    // Proxy tests
    // Access https server via connect proxy
    @Test
    public void testHttpsProxy() throws Exception {
        testProxy(HTTP);
        assertEquals("Host header doesn't contain target host", "localhost:4043", proxy.getLastRequestHeaders().get("Host"));
        assertEquals("Host header doesn't contain target host", CONNECT, proxy.getLastMethod());
    }

    // Access https server via connect proxy
    @Test
    public void testHttpsProxyWithSNI() throws Exception {
        testProxyWithSNI(HTTP);
        assertEquals("Host header doesn't contain target host", "host2.com:4043", proxy.getLastRequestHeaders().get("Host"));
        assertEquals("Host header doesn't contain target host", CONNECT, proxy.getLastMethod());
    }

    // Check that proxy auth fails if it is missing
    @Test
    public void testHttpsProxyAuthFail() throws Exception {
        startProxy("username", HTTP);
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).useProxy(HTTP).fail();
    }

    // Access https server via connect proxy with proxy auth required
    @Test
    public void testHttpsProxyAuth() throws Exception {
        startProxy("username", HTTP);
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).useProxy(HTTP).useProxyAuth().pass();
        assertNotNull("connection didn't access the proxy", proxy.getLastUri());
        assertEquals("hostname resolved but it shouldn't be", "localhost:4043", proxy.getLastUri());
        assertEquals("Host header doesn't contain target host", "localhost:4043", proxy.getLastRequestHeaders().get("Host"));
        assertEquals("Host header doesn't contain target host", CONNECT, proxy.getLastMethod());
    }

    // Access https server via connect proxy with a hostname that doesn't resolve
    // the hostname may resolve at the proxy if that is accessing another DNS
    // we simulate this by mapping the hostname to localhost:xxx in the test proxy code
    @Test
    public void testHttpsProxyUnknownHost() throws Exception {
        startProxy(null, HTTP);
        proxy.setForceUri("localhost:4043");
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).useProxy(HTTP).connectHostname("doesnt-resolve.host-name").clientTrustAll().clientVerifyHost(false).pass();
        assertNotNull("connection didn't access the proxy", proxy.getLastUri());
        assertEquals("hostname resolved but it shouldn't be", "doesnt-resolve.host-name:4043", proxy.getLastUri());
        assertEquals("Host header doesn't contain target host", "doesnt-resolve.host-name:4043", proxy.getLastRequestHeaders().get("Host"));
        assertEquals("Host header doesn't contain target host", CONNECT, proxy.getLastMethod());
    }

    // Access https server via socks5 proxy
    @Test
    public void testHttpsSocks() throws Exception {
        testProxy(SOCKS5);
    }

    // Access https server via socks5 proxy
    @Test
    public void testHttpsSocksWithSNI() throws Exception {
        testProxyWithSNI(SOCKS5);
    }

    // Access https server via socks5 proxy with authentication
    @Test
    public void testHttpsSocksAuth() throws Exception {
        startProxy("username", SOCKS5);
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).useProxy(SOCKS5).useProxyAuth().pass();
        assertNotNull("connection didn't access the proxy", proxy.getLastUri());
        assertEquals("hostname resolved but it shouldn't be", "localhost:4043", proxy.getLastUri());
    }

    // Access https server via socks proxy with a hostname that doesn't resolve
    // the hostname may resolve at the proxy if that is accessing another DNS
    // we simulate this by mapping the hostname to localhost:xxx in the test proxy code
    @Test
    public void testSocksProxyUnknownHost() throws Exception {
        startProxy(null, SOCKS5);
        proxy.setForceUri("localhost:4043");
        testTLS(Cert.NONE, Trust.SERVER_JKS, Cert.SERVER_JKS, Trust.NONE).useProxy(SOCKS5).connectHostname("doesnt-resolve.host-name").clientTrustAll().clientVerifyHost(false).pass();
        assertNotNull("connection didn't access the proxy", proxy.getLastUri());
        assertEquals("hostname resolved but it shouldn't be", "doesnt-resolve.host-name:4043", proxy.getLastUri());
    }
}

