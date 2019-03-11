/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.toolkit.tls.util;


import TlsConfig.DEFAULT_SIGNING_ALGORITHM;
import TlsHelper.JCE_URL;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.security.util.CertificateUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TlsHelper.ILLEGAL_KEY_SIZE;
import static TlsHelper.JCE_URL;


@RunWith(MockitoJUnitRunner.class)
public class TlsHelperTest {
    public static final Logger logger = LoggerFactory.getLogger(TlsHelperTest.class);

    private static final boolean originalUnlimitedCrypto = TlsHelper.isUnlimitedStrengthCryptographyEnabled();

    private int days;

    private int keySize;

    private String keyPairAlgorithm;

    private String signingAlgorithm;

    private KeyPairGenerator keyPairGenerator;

    private KeyStore keyStore;

    @Mock
    KeyStoreSpi keyStoreSpi;

    @Mock
    Provider keyStoreProvider;

    @Mock
    OutputStreamFactory outputStreamFactory;

    private ByteArrayOutputStream tmpFileOutputStream;

    private File file;

    @Test
    public void testTokenLengthInCalculateHmac() throws NoSuchAlgorithmException, CertificateException {
        List<String> badTokens = new ArrayList<>();
        List<String> goodTokens = new ArrayList<>();
        badTokens.add(null);
        badTokens.add("");
        badTokens.add("123");
        goodTokens.add("0123456789abcdefghijklm");
        goodTokens.add("0123456789abcdef");
        String dn = "CN=testDN,O=testOrg";
        X509Certificate x509Certificate = CertificateUtils.generateSelfSignedX509Certificate(TlsHelper.generateKeyPair(keyPairAlgorithm, keySize), dn, signingAlgorithm, days);
        PublicKey pubKey = x509Certificate.getPublicKey();
        for (String token : badTokens) {
            try {
                TlsHelper.calculateHMac(token, pubKey);
                Assert.fail("HMAC was calculated with a token that was too short.");
            } catch (GeneralSecurityException e) {
                Assert.assertEquals("Token does not meet minimum size of 16 bytes.", e.getMessage());
            } catch (IllegalArgumentException e) {
                Assert.assertEquals("Token cannot be null", e.getMessage());
            }
        }
        for (String token : goodTokens) {
            try {
                byte[] hmac = TlsHelper.calculateHMac(token, pubKey);
                Assert.assertTrue("HMAC length ok", ((hmac.length) > 0));
            } catch (GeneralSecurityException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void testGenerateSelfSignedCert() throws IOException, GeneralSecurityException, OperatorCreationException {
        String dn = "CN=testDN,O=testOrg";
        X509Certificate x509Certificate = CertificateUtils.generateSelfSignedX509Certificate(TlsHelper.generateKeyPair(keyPairAlgorithm, keySize), dn, signingAlgorithm, days);
        Date notAfter = x509Certificate.getNotAfter();
        Assert.assertTrue(notAfter.after(inFuture(((days) - 1))));
        Assert.assertTrue(notAfter.before(inFuture(((days) + 1))));
        Date notBefore = x509Certificate.getNotBefore();
        Assert.assertTrue(notBefore.after(inFuture((-1))));
        Assert.assertTrue(notBefore.before(inFuture(1)));
        Assert.assertEquals(dn, x509Certificate.getIssuerX500Principal().getName());
        Assert.assertEquals(signingAlgorithm, x509Certificate.getSigAlgName());
        Assert.assertEquals(keyPairAlgorithm, x509Certificate.getPublicKey().getAlgorithm());
        x509Certificate.checkValidity();
    }

    @Test
    public void testIssueCert() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CertificateException, OperatorCreationException {
        X509Certificate issuer = TlsHelperTest.loadCertificate(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("rootCert.crt")));
        KeyPair issuerKeyPair = TlsHelperTest.loadKeyPair(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("rootCert.key")));
        String dn = "CN=testIssued, O=testOrg";
        KeyPair keyPair = TlsHelper.generateKeyPair(keyPairAlgorithm, keySize);
        X509Certificate x509Certificate = CertificateUtils.generateIssuedCertificate(dn, keyPair.getPublic(), issuer, issuerKeyPair, signingAlgorithm, days);
        Assert.assertEquals(dn, x509Certificate.getSubjectX500Principal().toString());
        Assert.assertEquals(issuer.getSubjectX500Principal().toString(), x509Certificate.getIssuerX500Principal().toString());
        Assert.assertEquals(keyPair.getPublic(), x509Certificate.getPublicKey());
        Date notAfter = x509Certificate.getNotAfter();
        Assert.assertTrue(notAfter.after(inFuture(((days) - 1))));
        Assert.assertTrue(notAfter.before(inFuture(((days) + 1))));
        Date notBefore = x509Certificate.getNotBefore();
        Assert.assertTrue(notBefore.after(inFuture((-1))));
        Assert.assertTrue(notBefore.before(inFuture(1)));
        Assert.assertEquals(signingAlgorithm, x509Certificate.getSigAlgName());
        Assert.assertEquals(keyPairAlgorithm, x509Certificate.getPublicKey().getAlgorithm());
        x509Certificate.verify(issuerKeyPair.getPublic());
    }

    @Test
    public void testWriteKeyStoreSuccess() throws IOException, GeneralSecurityException {
        TlsHelperTest.setUnlimitedCrypto(false);
        String testPassword = "testPassword";
        Assert.assertEquals(testPassword, TlsHelper.writeKeyStore(keyStore, outputStreamFactory, file, testPassword, false));
        Mockito.verify(keyStoreSpi, Mockito.times(1)).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
    }

    @Test
    public void testWriteKeyStoreFailure() throws IOException, GeneralSecurityException {
        TlsHelperTest.setUnlimitedCrypto(false);
        String testPassword = "testPassword";
        IOException ioException = new IOException("Fail");
        Mockito.doThrow(ioException).when(keyStoreSpi).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
        try {
            TlsHelper.writeKeyStore(keyStore, outputStreamFactory, file, testPassword, true);
            Assert.fail(("Expected " + ioException));
        } catch (IOException e) {
            Assert.assertEquals(ioException, e);
        }
    }

    @Test
    public void testWriteKeyStoreTruncate() throws IOException, GeneralSecurityException {
        TlsHelperTest.setUnlimitedCrypto(false);
        String testPassword = "testPassword";
        String truncatedPassword = testPassword.substring(0, 7);
        IOException ioException = new IOException(ILLEGAL_KEY_SIZE);
        Mockito.doThrow(ioException).when(keyStoreSpi).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
        Assert.assertEquals(truncatedPassword, TlsHelper.writeKeyStore(keyStore, outputStreamFactory, file, testPassword, true));
        Mockito.verify(keyStoreSpi, Mockito.times(1)).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
        Mockito.verify(keyStoreSpi, Mockito.times(1)).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(truncatedPassword.toCharArray()));
    }

    @Test
    public void testWriteKeyStoreUnlimitedWontTruncate() throws IOException, GeneralSecurityException {
        TlsHelperTest.setUnlimitedCrypto(true);
        String testPassword = "testPassword";
        IOException ioException = new IOException(ILLEGAL_KEY_SIZE);
        Mockito.doThrow(ioException).when(keyStoreSpi).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
        try {
            TlsHelper.writeKeyStore(keyStore, outputStreamFactory, file, testPassword, true);
            Assert.fail(("Expected " + ioException));
        } catch (IOException e) {
            Assert.assertEquals(ioException, e);
        }
    }

    @Test
    public void testWriteKeyStoreNoTruncate() throws IOException, GeneralSecurityException {
        TlsHelperTest.setUnlimitedCrypto(false);
        String testPassword = "testPassword";
        IOException ioException = new IOException(ILLEGAL_KEY_SIZE);
        Mockito.doThrow(ioException).when(keyStoreSpi).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
        try {
            TlsHelper.writeKeyStore(keyStore, outputStreamFactory, file, testPassword, false);
            Assert.fail(("Expected " + (GeneralSecurityException.class)));
        } catch (GeneralSecurityException e) {
            Assert.assertTrue(("Expected exception to contain " + (JCE_URL)), e.getMessage().contains(JCE_URL));
        }
    }

    @Test
    public void testWriteKeyStoreTruncateFailure() throws IOException, GeneralSecurityException {
        TlsHelperTest.setUnlimitedCrypto(false);
        String testPassword = "testPassword";
        String truncatedPassword = testPassword.substring(0, 7);
        IOException ioException = new IOException(ILLEGAL_KEY_SIZE);
        IOException ioException2 = new IOException(ILLEGAL_KEY_SIZE);
        Mockito.doThrow(ioException).when(keyStoreSpi).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(testPassword.toCharArray()));
        Mockito.doThrow(ioException2).when(keyStoreSpi).engineStore(ArgumentMatchers.eq(tmpFileOutputStream), AdditionalMatchers.aryEq(truncatedPassword.toCharArray()));
        try {
            TlsHelper.writeKeyStore(keyStore, outputStreamFactory, file, testPassword, true);
            Assert.fail(("Expected " + ioException2));
        } catch (IOException e) {
            Assert.assertEquals(ioException2, e);
        }
    }

    @Test
    public void testShouldIncludeSANFromCSR() throws Exception {
        // Arrange
        final List<String> SAN_ENTRIES = Arrays.asList("127.0.0.1", "nifi.nifi.apache.org");
        final String SAN = StringUtils.join(SAN_ENTRIES, ",");
        final int SAN_COUNT = SAN_ENTRIES.size();
        final String DN = "CN=localhost";
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        TlsHelperTest.logger.info(("Generating CSR with DN: " + DN));
        // Act
        JcaPKCS10CertificationRequest csrWithSan = TlsHelper.generateCertificationRequest(DN, SAN, keyPair, DEFAULT_SIGNING_ALGORITHM);
        TlsHelperTest.logger.info(("Created CSR with SAN: " + SAN));
        String testCsrPem = TlsHelper.pemEncodeJcaObject(csrWithSan);
        TlsHelperTest.logger.info(("Encoded CSR as PEM: " + testCsrPem));
        // Assert
        String subjectName = csrWithSan.getSubject().toString();
        TlsHelperTest.logger.info(("CSR Subject Name: " + subjectName));
        assert subjectName.equals(DN);
        List<String> extractedSans = extractSanFromCsr(csrWithSan);
        assert (extractedSans.size()) == (SAN_COUNT + 1);
        List<String> formattedSans = SAN_ENTRIES.stream().map(( s) -> "DNS: " + s).collect(Collectors.toList());
        assert extractedSans.containsAll(formattedSans);
        // We check that the SANs also contain the CN
        assert extractedSans.contains("DNS: localhost");
    }

    @Test
    public void testEscapeAliasFilenameWithForwardSlashes() {
        String result = TlsHelper.escapeFilename("my/silly/filename.pem");
        Assert.assertEquals("my_silly_filename.pem", result);
    }

    @Test
    public void testEscapeAliasFilenameWithBackSlashes() {
        String result = TlsHelper.escapeFilename("my\\silly\\filename.pem");
        Assert.assertEquals("my_silly_filename.pem", result);
    }

    @Test
    public void testEscapeAliasFilenameWithDollarSign() {
        String result = TlsHelper.escapeFilename("my$illyfilename.pem");
        Assert.assertEquals("my_illyfilename.pem", result);
    }

    @Test
    public void testEscapeAliasFilenameTwoSymbolsInARow() {
        String result = TlsHelper.escapeFilename("my!?sillyfilename.pem");
        Assert.assertEquals("my_sillyfilename.pem", result);
    }

    @Test
    public void testEscapeAliasFilenameKeepHyphens() {
        String result = TlsHelper.escapeFilename("my-silly-filename.pem");
        Assert.assertEquals("my-silly-filename.pem", result);
    }

    @Test
    public void testEscapeAliasFilenameDoubleSpaces() {
        String result = TlsHelper.escapeFilename("my  silly  filename.pem");
        Assert.assertEquals("my_silly_filename.pem", result);
    }

    @Test
    public void testEscapeAliasFilenameSymbols() {
        String result = TlsHelper.escapeFilename("./\\!@#$%^&*()_-+=.pem");
        Assert.assertEquals(".__-_=.pem", result);
    }

    @Test
    public void testClientDnFilenameSlashes() throws Exception {
        String clientDn = "OU=NiFi/Organisation,CN=testuser";
        String escapedClientDn = TlsHelper.escapeFilename(CertificateUtils.reorderDn(clientDn));
        Assert.assertEquals("CN=testuser_OU=NiFi_Organisation", escapedClientDn);
    }

    @Test
    public void testClientDnFilenameSpecialChars() throws Exception {
        String clientDn = "OU=NiFi#!Organisation,CN=testuser";
        String escapedClientDn = TlsHelper.escapeFilename(CertificateUtils.reorderDn(clientDn));
        Assert.assertEquals("CN=testuser_OU=NiFi_Organisation", escapedClientDn);
    }
}

