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
package org.apache.nifi.web.security.x509.ocsp;


import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OcspCertificateValidatorTest {
    private static final Logger logger = LoggerFactory.getLogger(OcspCertificateValidatorTest.class);

    private static final int KEY_SIZE = 2048;

    private static final long YESTERDAY = (System.currentTimeMillis()) - (((24 * 60) * 60) * 1000);

    private static final long ONE_YEAR_FROM_NOW = (System.currentTimeMillis()) + ((((365 * 24) * 60) * 60) * 1000);

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final String PROVIDER = "BC";

    private static final String ISSUER_DN = "CN=NiFi Test CA,OU=Security,O=Apache,ST=CA,C=US";

    private static X509Certificate ISSUER_CERTIFICATE;

    @Test
    public void testShouldGenerateCertificate() throws Exception {
        // Arrange
        final String testDn = "CN=This is a test";
        // Act
        X509Certificate certificate = OcspCertificateValidatorTest.generateCertificate(testDn);
        OcspCertificateValidatorTest.logger.info("Generated certificate: \n{}", certificate);
        // Assert
        assert certificate.getSubjectDN().getName().equals(testDn);
        assert certificate.getIssuerDN().getName().equals(testDn);
        certificate.verify(certificate.getPublicKey());
    }

    @Test
    public void testShouldGenerateCertificateFromKeyPair() throws Exception {
        // Arrange
        final String testDn = "CN=This is a test";
        final KeyPair keyPair = OcspCertificateValidatorTest.generateKeyPair();
        // Act
        X509Certificate certificate = OcspCertificateValidatorTest.generateCertificate(testDn, keyPair);
        OcspCertificateValidatorTest.logger.info("Generated certificate: \n{}", certificate);
        // Assert
        assert certificate.getPublicKey().equals(keyPair.getPublic());
        assert certificate.getSubjectDN().getName().equals(testDn);
        assert certificate.getIssuerDN().getName().equals(testDn);
        certificate.verify(certificate.getPublicKey());
    }

    @Test
    public void testShouldGenerateIssuedCertificate() throws Exception {
        // Arrange
        final String testDn = "CN=This is a signed test";
        final String issuerDn = "CN=Issuer CA";
        final KeyPair issuerKeyPair = OcspCertificateValidatorTest.generateKeyPair();
        final PrivateKey issuerPrivateKey = issuerKeyPair.getPrivate();
        final X509Certificate issuerCertificate = OcspCertificateValidatorTest.generateCertificate(issuerDn, issuerKeyPair);
        OcspCertificateValidatorTest.logger.info("Generated issuer certificate: \n{}", issuerCertificate);
        // Act
        X509Certificate certificate = OcspCertificateValidatorTest.generateIssuedCertificate(testDn, issuerDn, issuerPrivateKey);
        OcspCertificateValidatorTest.logger.info("Generated signed certificate: \n{}", certificate);
        // Assert
        assert issuerCertificate.getPublicKey().equals(issuerKeyPair.getPublic());
        assert certificate.getSubjectX500Principal().getName().equals(testDn);
        assert certificate.getIssuerX500Principal().getName().equals(issuerDn);
        certificate.verify(issuerCertificate.getPublicKey());
        try {
            certificate.verify(certificate.getPublicKey());
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            assert e instanceof SignatureException;
            assert e.getMessage().contains("certificate does not verify with supplied key");
        }
    }
}

