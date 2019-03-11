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
package org.apache.nifi.security.util;


import KeystoreType.JKS;
import KeystoreType.PKCS12;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import org.junit.Test;


public class KeyStoreUtilsTest {
    public static final String SIGNING_ALGORITHM = "SHA256withRSA";

    public static final int DURATION_DAYS = 365;

    public static final char[] BAD_TEST_PASSWORD_DONT_USE_THIS = "changek".toCharArray();

    public static final char[] BAD_KEY_STORE_TEST_PASSWORD_DONT_USE_THIS = "changes".toCharArray();

    public static final String ALIAS = "alias";

    private static KeyPair caCertKeyPair;

    private static X509Certificate caCertificate;

    private static KeyPair issuedCertificateKeyPair;

    private static X509Certificate issuedCertificate;

    @Test
    public void testJksKeyStoreRoundTrip() throws IOException, GeneralSecurityException {
        testKeyStoreRoundTrip(() -> KeyStoreUtils.getKeyStore(JKS.toString().toLowerCase()));
    }

    @Test
    public void testPkcs12KeyStoreBcRoundTrip() throws IOException, GeneralSecurityException {
        testKeyStoreRoundTrip(() -> KeyStoreUtils.getKeyStore(PKCS12.toString().toLowerCase()));
    }

    @Test
    public void testPkcs12KeyStoreRoundTripBcReload() throws IOException, GeneralSecurityException {
        // Pkcs12 Bouncy Castle needs same key and keystore password to interoperate with Java provider
        testKeyStoreRoundTrip(() -> KeyStore.getInstance(PKCS12.toString().toLowerCase()), () -> KeyStoreUtils.getKeyStore(PKCS12.toString().toLowerCase()), KeyStoreUtilsTest.BAD_KEY_STORE_TEST_PASSWORD_DONT_USE_THIS);
    }

    @Test
    public void testJksTrustStoreRoundTrip() throws IOException, GeneralSecurityException {
        testTrustStoreRoundTrip(() -> KeyStoreUtils.getTrustStore(JKS.toString().toLowerCase()));
    }

    @Test
    public void testPkcs12TrustStoreBcRoundTrip() throws IOException, GeneralSecurityException {
        testTrustStoreRoundTrip(() -> KeyStoreUtils.getTrustStore(PKCS12.toString().toLowerCase()));
    }

    @Test
    public void testPkcs12TrustStoreRoundTripBcReload() throws IOException, GeneralSecurityException {
        testTrustStoreRoundTrip(() -> KeyStore.getInstance(PKCS12.toString().toLowerCase()), () -> KeyStoreUtils.getTrustStore(PKCS12.toString().toLowerCase()));
    }

    private interface KeyStoreSupplier {
        KeyStore get() throws GeneralSecurityException;
    }
}

