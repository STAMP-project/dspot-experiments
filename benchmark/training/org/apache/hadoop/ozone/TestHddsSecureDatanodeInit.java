/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone;


import GenericTestUtils.LogCapturer;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.security.x509.SecurityConfig;
import org.apache.hadoop.hdds.security.x509.certificate.client.CertificateClient;
import org.apache.hadoop.hdds.security.x509.certificate.utils.CertificateCodec;
import org.apache.hadoop.hdds.security.x509.keys.KeyCodec;
import org.apache.hadoop.test.LambdaTestUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link HddsDatanodeService}.
 */
public class TestHddsSecureDatanodeInit {
    private static File testDir;

    private static OzoneConfiguration conf;

    private static HddsDatanodeService service;

    private static String[] args = new String[]{  };

    private static PrivateKey privateKey;

    private static PublicKey publicKey;

    private static LogCapturer dnLogs;

    private static CertificateClient client;

    private static SecurityConfig securityConfig;

    private static KeyCodec keyCodec;

    private static CertificateCodec certCodec;

    private static X509CertificateHolder certHolder;

    @Test
    public void testSecureDnStartupCase0() throws Exception {
        // Case 0: When keypair as well as certificate is missing. Initial keypair
        // boot-up. Get certificate will fail as no SCM is not running.
        LambdaTestUtils.intercept(Exception.class, "", () -> TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf));
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: GETCERT"));
    }

    @Test
    public void testSecureDnStartupCase1() throws Exception {
        // Case 1: When only certificate is present.
        TestHddsSecureDatanodeInit.certCodec.writeCertificate(TestHddsSecureDatanodeInit.certHolder);
        LambdaTestUtils.intercept(RuntimeException.class, ("DN security" + " initialization failed"), () -> TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf));
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: FAILURE"));
    }

    @Test
    public void testSecureDnStartupCase2() throws Exception {
        // Case 2: When private key and certificate is missing.
        TestHddsSecureDatanodeInit.keyCodec.writePublicKey(TestHddsSecureDatanodeInit.publicKey);
        LambdaTestUtils.intercept(RuntimeException.class, ("DN security" + " initialization failed"), () -> TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf));
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: FAILURE"));
    }

    @Test
    public void testSecureDnStartupCase3() throws Exception {
        // Case 3: When only public key and certificate is present.
        TestHddsSecureDatanodeInit.keyCodec.writePublicKey(TestHddsSecureDatanodeInit.publicKey);
        TestHddsSecureDatanodeInit.certCodec.writeCertificate(TestHddsSecureDatanodeInit.certHolder);
        LambdaTestUtils.intercept(RuntimeException.class, ("DN security" + " initialization failed"), () -> TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf));
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: FAILURE"));
    }

    @Test
    public void testSecureDnStartupCase4() throws Exception {
        // Case 4: When public key as well as certificate is missing.
        TestHddsSecureDatanodeInit.keyCodec.writePrivateKey(TestHddsSecureDatanodeInit.privateKey);
        LambdaTestUtils.intercept(RuntimeException.class, (" DN security" + " initialization failed"), () -> TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf));
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: FAILURE"));
        TestHddsSecureDatanodeInit.dnLogs.clearOutput();
    }

    @Test
    public void testSecureDnStartupCase5() throws Exception {
        // Case 5: If private key and certificate is present.
        TestHddsSecureDatanodeInit.certCodec.writeCertificate(TestHddsSecureDatanodeInit.certHolder);
        TestHddsSecureDatanodeInit.keyCodec.writePrivateKey(TestHddsSecureDatanodeInit.privateKey);
        TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf);
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: SUCCESS"));
    }

    @Test
    public void testSecureDnStartupCase6() throws Exception {
        // Case 6: If key pair already exist than response should be GETCERT.
        TestHddsSecureDatanodeInit.keyCodec.writePublicKey(TestHddsSecureDatanodeInit.publicKey);
        TestHddsSecureDatanodeInit.keyCodec.writePrivateKey(TestHddsSecureDatanodeInit.privateKey);
        LambdaTestUtils.intercept(Exception.class, "", () -> TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf));
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: GETCERT"));
    }

    @Test
    public void testSecureDnStartupCase7() throws Exception {
        // Case 7 When keypair and certificate is present.
        TestHddsSecureDatanodeInit.keyCodec.writePublicKey(TestHddsSecureDatanodeInit.publicKey);
        TestHddsSecureDatanodeInit.keyCodec.writePrivateKey(TestHddsSecureDatanodeInit.privateKey);
        TestHddsSecureDatanodeInit.certCodec.writeCertificate(TestHddsSecureDatanodeInit.certHolder);
        TestHddsSecureDatanodeInit.service.initializeCertificateClient(TestHddsSecureDatanodeInit.conf);
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPrivateKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getPublicKey());
        Assert.assertNotNull(TestHddsSecureDatanodeInit.client.getCertificate());
        Assert.assertTrue(TestHddsSecureDatanodeInit.dnLogs.getOutput().contains("Init response: SUCCESS"));
    }

    @Test
    public void testGetCSR() throws Exception {
        TestHddsSecureDatanodeInit.keyCodec.writePublicKey(TestHddsSecureDatanodeInit.publicKey);
        TestHddsSecureDatanodeInit.keyCodec.writePrivateKey(TestHddsSecureDatanodeInit.privateKey);
        TestHddsSecureDatanodeInit.service.setCertificateClient(TestHddsSecureDatanodeInit.client);
        PKCS10CertificationRequest csr = TestHddsSecureDatanodeInit.service.getCSR(TestHddsSecureDatanodeInit.conf);
        Assert.assertNotNull(csr);
        csr = TestHddsSecureDatanodeInit.service.getCSR(TestHddsSecureDatanodeInit.conf);
        Assert.assertNotNull(csr);
        csr = TestHddsSecureDatanodeInit.service.getCSR(TestHddsSecureDatanodeInit.conf);
        Assert.assertNotNull(csr);
        csr = TestHddsSecureDatanodeInit.service.getCSR(TestHddsSecureDatanodeInit.conf);
        Assert.assertNotNull(csr);
    }
}

