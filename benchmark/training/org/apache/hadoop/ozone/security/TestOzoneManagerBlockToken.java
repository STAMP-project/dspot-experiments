/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.security;


import HddsProtos.BlockTokenSecretProto.AccessModeProto;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hdds.security.token.OzoneBlockTokenIdentifier;
import org.apache.hadoop.security.ssl.KeyStoreTestUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for OzoneManagerDelegationToken.
 */
public class TestOzoneManagerBlockToken {
    private static final Logger LOG = LoggerFactory.getLogger(TestOzoneManagerBlockToken.class);

    private static final String BASEDIR = GenericTestUtils.getTempPath(TestOzoneManagerBlockToken.class.getSimpleName());

    private static final String KEYSTORES_DIR = new File(TestOzoneManagerBlockToken.BASEDIR).getAbsolutePath();

    private static long expiryTime;

    private static KeyPair keyPair;

    private static X509Certificate cert;

    private static final long MAX_LEN = 1000;

    @Test
    public void testSignToken() throws IOException, GeneralSecurityException {
        String keystore = new File(TestOzoneManagerBlockToken.KEYSTORES_DIR, "keystore.jks").getAbsolutePath();
        String truststore = new File(TestOzoneManagerBlockToken.KEYSTORES_DIR, "truststore.jks").getAbsolutePath();
        String trustPassword = "trustPass";
        String keyStorePassword = "keyStorePass";
        String keyPassword = "keyPass";
        KeyStoreTestUtil.createKeyStore(keystore, keyStorePassword, keyPassword, "OzoneMaster", TestOzoneManagerBlockToken.keyPair.getPrivate(), TestOzoneManagerBlockToken.cert);
        // Create trust store and put the certificate in the trust store
        Map<String, X509Certificate> certs = Collections.singletonMap("server", TestOzoneManagerBlockToken.cert);
        KeyStoreTestUtil.createTrustStore(truststore, trustPassword, certs);
        // Sign the OzoneMaster Token with Ozone Master private key
        PrivateKey privateKey = TestOzoneManagerBlockToken.keyPair.getPrivate();
        OzoneBlockTokenIdentifier tokenId = new OzoneBlockTokenIdentifier("testUser", "84940", EnumSet.allOf(AccessModeProto.class), TestOzoneManagerBlockToken.expiryTime, TestOzoneManagerBlockToken.cert.getSerialNumber().toString(), TestOzoneManagerBlockToken.MAX_LEN);
        byte[] signedToken = signTokenAsymmetric(tokenId, privateKey);
        // Verify a valid signed OzoneMaster Token with Ozone Master
        // public key(certificate)
        boolean isValidToken = verifyTokenAsymmetric(tokenId, signedToken, TestOzoneManagerBlockToken.cert);
        TestOzoneManagerBlockToken.LOG.info("{} is {}", tokenId, (isValidToken ? "valid." : "invalid."));
        // Verify an invalid signed OzoneMaster Token with Ozone Master
        // public key(certificate)
        tokenId = new OzoneBlockTokenIdentifier("", "", EnumSet.allOf(AccessModeProto.class), TestOzoneManagerBlockToken.expiryTime, TestOzoneManagerBlockToken.cert.getSerialNumber().toString(), TestOzoneManagerBlockToken.MAX_LEN);
        TestOzoneManagerBlockToken.LOG.info("Unsigned token {} is {}", tokenId, verifyTokenAsymmetric(tokenId, RandomUtils.nextBytes(128), TestOzoneManagerBlockToken.cert));
    }

    @Test
    public void testAsymmetricTokenPerf() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CertificateEncodingException {
        final int testTokenCount = 1000;
        List<OzoneBlockTokenIdentifier> tokenIds = new ArrayList<>();
        List<byte[]> tokenPasswordAsym = new ArrayList<>();
        for (int i = 0; i < testTokenCount; i++) {
            tokenIds.add(generateTestToken());
        }
        KeyPair kp = KeyStoreTestUtil.generateKeyPair("RSA");
        // Create Ozone Master certificate (SCM CA issued cert) and key store
        X509Certificate omCert;
        omCert = KeyStoreTestUtil.generateCertificate("CN=OzoneMaster", kp, 30, "SHA256withRSA");
        long startTime = Time.monotonicNowNanos();
        for (int i = 0; i < testTokenCount; i++) {
            tokenPasswordAsym.add(signTokenAsymmetric(tokenIds.get(i), kp.getPrivate()));
        }
        long duration = (Time.monotonicNowNanos()) - startTime;
        TestOzoneManagerBlockToken.LOG.info("Average token sign time with HmacSha256(RSA/1024 key) is {} ns", (duration / testTokenCount));
        startTime = Time.monotonicNowNanos();
        for (int i = 0; i < testTokenCount; i++) {
            verifyTokenAsymmetric(tokenIds.get(i), tokenPasswordAsym.get(i), omCert);
        }
        duration = (Time.monotonicNowNanos()) - startTime;
        TestOzoneManagerBlockToken.LOG.info(("Average token verify time with HmacSha256(RSA/1024 key) " + "is {} ns"), (duration / testTokenCount));
    }

    @Test
    public void testSymmetricTokenPerf() {
        String hmacSHA1 = "HmacSHA1";
        String hmacSHA256 = "HmacSHA256";
        testSymmetricTokenPerfHelper(hmacSHA1, 64);
        testSymmetricTokenPerfHelper(hmacSHA256, 1024);
    }
}

