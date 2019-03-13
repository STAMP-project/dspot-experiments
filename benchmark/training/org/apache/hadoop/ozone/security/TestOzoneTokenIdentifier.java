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
package org.apache.hadoop.ozone.security;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.ssl.KeyStoreTestUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link OzoneTokenIdentifier}.
 */
public class TestOzoneTokenIdentifier {
    private static final Logger LOG = LoggerFactory.getLogger(TestOzoneTokenIdentifier.class);

    private static final String BASEDIR = GenericTestUtils.getTempPath(TestOzoneTokenIdentifier.class.getSimpleName());

    private static final String KEYSTORES_DIR = new File(TestOzoneTokenIdentifier.BASEDIR).getAbsolutePath();

    private static File base;

    private static String sslConfsDir;

    private static final String EXCLUDE_CIPHERS = "TLS_ECDHE_RSA_WITH_RC4_128_SHA," + ((((("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA,  \n" + "SSL_RSA_WITH_DES_CBC_SHA,") + "SSL_DHE_RSA_WITH_DES_CBC_SHA,  ") + "SSL_RSA_EXPORT_WITH_RC4_40_MD5,\t \n") + "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,") + "SSL_RSA_WITH_RC4_128_MD5");

    @Test
    public void testSignToken() throws IOException, GeneralSecurityException {
        String keystore = new File(TestOzoneTokenIdentifier.KEYSTORES_DIR, "keystore.jks").getAbsolutePath();
        String truststore = new File(TestOzoneTokenIdentifier.KEYSTORES_DIR, "truststore.jks").getAbsolutePath();
        String trustPassword = "trustPass";
        String keyStorePassword = "keyStorePass";
        String keyPassword = "keyPass";
        // Create Ozone Master key pair
        KeyPair keyPair = KeyStoreTestUtil.generateKeyPair("RSA");
        // Create Ozone Master certificate (SCM CA issued cert) and key store
        X509Certificate cert = KeyStoreTestUtil.generateCertificate("CN=OzoneMaster", keyPair, 30, "SHA256withRSA");
        KeyStoreTestUtil.createKeyStore(keystore, keyStorePassword, keyPassword, "OzoneMaster", keyPair.getPrivate(), cert);
        // Create trust store and put the certificate in the trust store
        Map<String, X509Certificate> certs = Collections.singletonMap("server", cert);
        KeyStoreTestUtil.createTrustStore(truststore, trustPassword, certs);
        // Sign the OzoneMaster Token with Ozone Master private key
        PrivateKey privateKey = keyPair.getPrivate();
        OzoneTokenIdentifier tokenId = new OzoneTokenIdentifier();
        tokenId.setOmCertSerialId("123");
        byte[] signedToken = signTokenAsymmetric(tokenId, privateKey);
        // Verify a valid signed OzoneMaster Token with Ozone Master
        // public key(certificate)
        boolean isValidToken = verifyTokenAsymmetric(tokenId, signedToken, cert);
        TestOzoneTokenIdentifier.LOG.info("{} is {}", tokenId, (isValidToken ? "valid." : "invalid."));
        // Verify an invalid signed OzoneMaster Token with Ozone Master
        // public key(certificate)
        tokenId = new OzoneTokenIdentifier(new Text("oozie"), new Text("rm"), new Text("client"));
        tokenId.setOmCertSerialId("123");
        TestOzoneTokenIdentifier.LOG.info("Unsigned token {} is {}", tokenId, verifyTokenAsymmetric(tokenId, RandomUtils.nextBytes(128), cert));
    }

    @Test
    public void testAsymmetricTokenPerf() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CertificateEncodingException {
        final int testTokenCount = 1000;
        List<OzoneTokenIdentifier> tokenIds = new ArrayList<>();
        List<byte[]> tokenPasswordAsym = new ArrayList<>();
        for (int i = 0; i < testTokenCount; i++) {
            tokenIds.add(generateTestToken());
        }
        KeyPair keyPair = KeyStoreTestUtil.generateKeyPair("RSA");
        // Create Ozone Master certificate (SCM CA issued cert) and key store
        X509Certificate cert;
        cert = KeyStoreTestUtil.generateCertificate("CN=OzoneMaster", keyPair, 30, "SHA256withRSA");
        long startTime = Time.monotonicNowNanos();
        for (int i = 0; i < testTokenCount; i++) {
            tokenPasswordAsym.add(signTokenAsymmetric(tokenIds.get(i), keyPair.getPrivate()));
        }
        long duration = (Time.monotonicNowNanos()) - startTime;
        TestOzoneTokenIdentifier.LOG.info("Average token sign time with HmacSha256(RSA/1024 key) is {} ns", (duration / testTokenCount));
        startTime = Time.monotonicNowNanos();
        for (int i = 0; i < testTokenCount; i++) {
            verifyTokenAsymmetric(tokenIds.get(i), tokenPasswordAsym.get(i), cert);
        }
        duration = (Time.monotonicNowNanos()) - startTime;
        TestOzoneTokenIdentifier.LOG.info(("Average token verify time with HmacSha256(RSA/1024 key) " + "is {} ns"), (duration / testTokenCount));
    }

    @Test
    public void testSymmetricTokenPerf() {
        String hmacSHA1 = "HmacSHA1";
        String hmacSHA256 = "HmacSHA256";
        testSymmetricTokenPerfHelper(hmacSHA1, 64);
        testSymmetricTokenPerfHelper(hmacSHA256, 1024);
    }

    /* Test serialization/deserialization of OzoneTokenIdentifier. */
    @Test
    public void testReadWriteInProtobuf() throws IOException {
        OzoneTokenIdentifier id = getIdentifierInst();
        File idFile = new File(((TestOzoneTokenIdentifier.BASEDIR) + "/tokenFile"));
        FileOutputStream fop = new FileOutputStream(idFile);
        DataOutputStream dataOutputStream = new DataOutputStream(fop);
        id.write(dataOutputStream);
        fop.close();
        FileInputStream fis = new FileInputStream(idFile);
        DataInputStream dis = new DataInputStream(fis);
        OzoneTokenIdentifier id2 = new OzoneTokenIdentifier();
        id2.readFields(dis);
        Assert.assertEquals(id, id2);
    }
}

