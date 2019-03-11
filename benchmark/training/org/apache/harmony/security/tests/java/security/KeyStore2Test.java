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
package org.apache.harmony.security.tests.java.security;


import StandardNames.IS_RI;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class KeyStore2Test extends TestCase {
    private static PrivateKey PRIVATE_KEY;

    final char[] pssWord = new char[]{ 'a', 'b', 'c' };

    final byte[] testEncoding = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };

    private Provider support_TestProvider;

    // creating a certificate
    String certificate = "-----BEGIN CERTIFICATE-----\n" + ((((((((((((("MIICZTCCAdICBQL3AAC2MA0GCSqGSIb3DQEBAgUAMF8xCzAJBgNVBAYTAlVTMSAw\n" + "HgYDVQQKExdSU0EgRGF0YSBTZWN1cml0eSwgSW5jLjEuMCwGA1UECxMlU2VjdXJl\n") + "IFNlcnZlciBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTAeFw05NzAyMjAwMDAwMDBa\n") + "Fw05ODAyMjAyMzU5NTlaMIGWMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZv\n") + "cm5pYTESMBAGA1UEBxMJUGFsbyBBbHRvMR8wHQYDVQQKExZTdW4gTWljcm9zeXN0\n") + "ZW1zLCBJbmMuMSEwHwYDVQQLExhUZXN0IGFuZCBFdmFsdWF0aW9uIE9ubHkxGjAY\n") + "BgNVBAMTEWFyZ29uLmVuZy5zdW4uY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCB\n") + "iQKBgQCofmdY+PiUWN01FOzEewf+GaG+lFf132UpzATmYJkA4AEA/juW7jSi+LJk\n") + "wJKi5GO4RyZoyimAL/5yIWDV6l1KlvxyKslr0REhMBaD/3Z3EsLTTEf5gVrQS6sT\n") + "WMoSZAyzB39kFfsB6oUXNtV8+UKKxSxKbxvhQn267PeCz5VX2QIDAQABMA0GCSqG\n") + "SIb3DQEBAgUAA34AXl3at6luiV/7I9MN5CXYoPJYI8Bcdc1hBagJvTMcmlqL2uOZ\n") + "H9T5hNMEL9Tk6aI7yZPXcw/xI2K6pOR/FrMp0UwJmdxX7ljV6ZtUZf7pY492UqwC\n") + "1777XQ9UEZyrKJvF5ntleeO0ayBqLGVKCWzWZX9YsXCpv47FNLZbupE=\n") + "-----END CERTIFICATE-----\n");

    ByteArrayInputStream certArray = new ByteArrayInputStream(certificate.getBytes());

    String certificate2 = "-----BEGIN CERTIFICATE-----\n" + ((((((((((((("MIICZzCCAdCgAwIBAgIBGzANBgkqhkiG9w0BAQUFADBhMQswCQYDVQQGEwJVUzEY\n" + "MBYGA1UEChMPVS5TLiBHb3Zlcm5tZW50MQwwCgYDVQQLEwNEb0QxDDAKBgNVBAsT\n") + "A1BLSTEcMBoGA1UEAxMTRG9EIFBLSSBNZWQgUm9vdCBDQTAeFw05ODA4MDMyMjAy\n") + "MjlaFw0wODA4MDQyMjAyMjlaMGExCzAJBgNVBAYTAlVTMRgwFgYDVQQKEw9VLlMu\n") + "IEdvdmVybm1lbnQxDDAKBgNVBAsTA0RvRDEMMAoGA1UECxMDUEtJMRwwGgYDVQQD\n") + "ExNEb0QgUEtJIE1lZCBSb290IENBMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n") + "gQDbrM/J9FrJSX+zxFUbsI9Vw5QbguVBIa95rwW/0M8+sM0r5gd+DY6iubm6wnXk\n") + "CSvbfQlFEDSKr4WYeeGp+d9WlDnQdtDFLdA45tCi5SHjnW+hGAmZnld0rz6wQekF\n") + "5xQaa5A6wjhMlLOjbh27zyscrorMJ1O5FBOWnEHcRv6xqQIDAQABoy8wLTAdBgNV\n") + "HQ4EFgQUVrmYR6m9701cHQ3r5kXyG7zsCN0wDAYDVR0TBAUwAwEB/zANBgkqhkiG\n") + "9w0BAQUFAAOBgQDVX1Y0YqC7vekeZjVxtyuC8Mnxbrz6D109AX07LEIRzNYzwZ0w\n") + "MTImSp9sEzWW+3FueBIU7AxGys2O7X0qmN3zgszPfSiocBuQuXIYQctJhKjF5KVc\n") + "VGQRYYlt+myhl2vy6yPzEVCjiKwMEb1Spu0irCf+lFW2hsdjvmSQMtZvOw==\n") + "-----END CERTIFICATE-----\n");

    ByteArrayInputStream certArray2 = new ByteArrayInputStream(certificate2.getBytes());

    String certificate3 = "-----BEGIN CERTIFICATE-----\n" + (((((((((((((((((("MIIDXDCCAsWgAwIBAgIBSjANBgkqhkiG9w0BAQUFADBWMQswCQYDVQQGEwJVUzEY\n" + "MBYGA1UEChMPVS5TLiBHb3Zlcm5tZW50MQwwCgYDVQQLEwNEb0QxDDAKBgNVBAsT\n") + "A1BLSTERMA8GA1UEAxMITWVkIENBLTEwHhcNOTgwODAyMTgwMjQwWhcNMDEwODAy\n") + "MTgwMjQwWjB0MQswCQYDVQQGEwJVUzEYMBYGA1UEChMPVS5TLiBHb3Zlcm5tZW50\n") + "MQwwCgYDVQQLEwNEb0QxDDAKBgNVBAsTA1BLSTENMAsGA1UECxMEVVNBRjEgMB4G\n") + "A1UEAxMXR3VtYnkuSm9zZXBoLjAwMDAwMDUwNDQwgZ8wDQYJKoZIhvcNAQEBBQAD\n") + "gY0AMIGJAoGBALT/R7bPqs1c1YqXAg5HNpZLgW2HuAc7RCaP06cE4R44GBLw/fQc\n") + "VRNLn5pgbTXsDnjiZVd8qEgYqjKFQka4/tNhaF7No2tBZB+oYL/eP0IWtP+h/W6D\n") + "KR5+UvIIdgmx7k3t9jp2Q51JpHhhKEb9WN54trCO9Yu7PYU+LI85jEIBAgMBAAGj\n") + "ggEaMIIBFjAWBgNVHSAEDzANMAsGCWCGSAFlAgELAzAfBgNVHSMEGDAWgBQzOhTo\n") + "CWdhiGUkIOx5cELXppMe9jAdBgNVHQ4EFgQUkLBJl+ayKgzOp/wwBX9M1lSkCg4w\n") + "DgYDVR0PAQH/BAQDAgbAMAwGA1UdEwEB/wQCMAAwgZ0GA1UdHwSBlTCBkjCBj6CB\n") + "jKCBiYaBhmxkYXA6Ly9kcy0xLmNoYW1iLmRpc2EubWlsL2NuJTNkTWVkJTIwQ0El\n") + "MmQxJTJjb3UlM2RQS0klMmNvdSUzZERvRCUyY28lM2RVLlMuJTIwR292ZXJubWVu\n") + "dCUyY2MlM2RVUz9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0JTNiYmluYXJ5MA0G\n") + "CSqGSIb3DQEBBQUAA4GBAFjapuDHMvIdUeYRyEYdShBR1JZC20tJ3MQnyBQveddz\n") + "LGFDGpIkRAQU7T/5/ne8lMexyxViC21xOlK9LdbJCbVyywvb9uEm/1je9wieQQtr\n") + "kjykuB+WB6qTCIslAO/eUmgzfzIENvnH8O+fH7QTr2PdkFkiPIqBJYHvw7F3XDqy\n") + "-----END CERTIFICATE-----\n");

    ByteArrayInputStream certArray3 = new ByteArrayInputStream(certificate3.getBytes());

    /**
     * java.security.KeyStore#aliases()
     */
    public void test_aliases() throws Exception {
        // Test for method java.util.Enumeration
        // java.security.KeyStore.aliases()
        // NOT COMPATIBLE WITH PCS#12
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.aliases();
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // KeyStore keyTest =
        // KeyStore.getInstance(KeyStore.getDefaultType());
        // alias 1
        keyTest.setCertificateEntry("alias1", cert[0]);
        // alias 2
        keyTest.setCertificateEntry("alias2", cert[0]);
        // alias 3
        keyTest.setCertificateEntry("alias3", cert[0]);
        // obtaining the aliase
        Enumeration<String> aliase = keyTest.aliases();
        Set<String> alia = new HashSet<String>();
        int i = 0;
        while (aliase.hasMoreElements()) {
            alia.add(aliase.nextElement());
            i++;
        } 
        TestCase.assertEquals("the wrong aliases were returned", i, 3);
        TestCase.assertTrue("the wrong aliases were returned", alia.contains("alias1"));
        TestCase.assertTrue("the wrong aliases were returned", alia.contains("alias2"));
        TestCase.assertTrue("the wrong aliases were returned", alia.contains("alias3"));
    }

    /**
     * java.security.KeyStore#containsAlias(java.lang.String)
     */
    public void test_containsAliasLjava_lang_String() throws Exception {
        // Test for method boolean
        // java.security.KeyStore.containsAlias(java.lang.String)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.containsAlias("alias1");
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // alias 1
        keyTest.setCertificateEntry("alias1", cert[0]);
        // alias 2
        keyTest.setCertificateEntry("alias2", cert[0]);
        TestCase.assertTrue("alias1 does not exist", keyTest.containsAlias("alias1"));
        TestCase.assertTrue("alias2 does not exist", keyTest.containsAlias("alias2"));
        TestCase.assertFalse("alias3 exists", keyTest.containsAlias("alias3"));
        try {
            keyTest.containsAlias(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     * java.security.KeyStore#getCertificate(java.lang.String)
     */
    public void test_getCertificateLjava_lang_String() throws Exception {
        // Test for method java.security.cert.Certificate
        // java.security.KeyStore.getCertificate(java.lang.String)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.getCertificate("anAlias");
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // alias 1
        PublicKey pub = cert[0].getPublicKey();
        keyTest.setCertificateEntry("alias1", cert[0]);
        Certificate certRes = keyTest.getCertificate("alias1");
        TestCase.assertEquals(("the public key of the certificate from getCertificate() " + "did not equal the original certificate"), pub, certRes.getPublicKey());
        // alias 2
        keyTest.setCertificateEntry("alias2", cert[0]);
        // testing for a certificate chain
        Certificate cert2 = keyTest.getCertificate("alias2");
        TestCase.assertEquals("the certificate for alias2 is supposed to exist", cert2, cert[0]);
    }

    /**
     * java.security.KeyStore#getCertificateAlias(java.security.cert.Certificate)
     */
    public void test_getCertificateAliasLjava_security_cert_Certificate() throws Exception {
        // Test for method java.lang.String
        // java.security.KeyStore.getCertificateAlias(java.security.cert.Certificate)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        keyTest.load(null, null);
        // certificate entry
        keyTest.setCertificateEntry("alias1", cert[1]);
        String alias = keyTest.getCertificateAlias(cert[1]);
        TestCase.assertEquals("certificate entry - the alias returned for this certificate was wrong", "alias1", alias);
        // key entry
        keyTest.setKeyEntry("alias2", KeyStore2Test.getPrivateKey(), pssWord, cert);
        alias = keyTest.getCertificateAlias(cert[0]);
        TestCase.assertEquals("key entry - the alias returned for this certificate was wrong", "alias2", alias);
        // testing case with a nonexistent certificate
        X509Certificate cert2 = ((X509Certificate) (cf.generateCertificate(certArray3)));
        String aliasNull = keyTest.getCertificateAlias(cert2);
        TestCase.assertNull("the alias returned for the nonexist certificate was NOT null", aliasNull);
    }

    /**
     * java.security.KeyStore#getCertificateChain(java.lang.String)
     */
    public void test_getCertificateChainLjava_lang_String() throws Exception {
        // Test for method java.security.cert.Certificate[]
        // java.security.KeyStore.getCertificateChain(java.lang.String)
        // creatCertificate();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.getCertificateChain("anAlias");
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // alias 1
        keyTest.setCertificateEntry("alias1", cert[0]);
        // alias 2
        keyTest.setKeyEntry("alias2", KeyStore2Test.getPrivateKey(), pssWord, cert);
        Certificate[] certRes = keyTest.getCertificateChain("alias2");
        TestCase.assertEquals("there are more than two certificate returned from getCertificateChain", 2, certRes.length);
        TestCase.assertEquals("the first certificate returned from getCertificateChain is not correct", cert[0].getPublicKey(), certRes[0].getPublicKey());
        TestCase.assertEquals("the second certificate returned from getCertificateChain is not correct", cert[1].getPublicKey(), certRes[1].getPublicKey());
        Certificate[] certResNull = keyTest.getCertificateChain("alias1");
        TestCase.assertNull("the certificate chain returned from getCertificateChain is NOT null", certResNull);
        try {
            keyTest.getCertificateChain(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     * java.security.KeyStore#getInstance(java.lang.String)
     */
    public void test_getInstanceLjava_lang_String() throws Exception {
        // Test for method java.security.KeyStore
        // java.security.KeyStore.getInstance(java.lang.String)
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        TestCase.assertEquals("the method getInstance did not obtain the correct type", KeyStore.getDefaultType(), keyTest.getType());
    }

    /**
     * java.security.KeyStore#getKey(java.lang.String, char[])
     */
    public void test_getKeyLjava_lang_String$C() throws Exception {
        // Test for method java.security.Key
        // java.security.KeyStore.getKey(java.lang.String, char[])
        // creatCertificate();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        keyTest.load(null, null);
        keyTest.setKeyEntry("alias2", KeyStore2Test.getPrivateKey(), pssWord, cert);
        PrivateKey returnedKey = ((PrivateKey) (keyTest.getKey("alias2", pssWord)));
        byte[] retB = returnedKey.getEncoded();
        byte[] priB = KeyStore2Test.getPrivateKey().getEncoded();
        TestCase.assertTrue(Arrays.equals(retB, priB));
        TestCase.assertEquals(KeyStore2Test.getPrivateKey().getAlgorithm(), returnedKey.getAlgorithm());
        TestCase.assertEquals(KeyStore2Test.getPrivateKey().getFormat(), returnedKey.getFormat());
        try {
            keyTest.getKey("alias2", "wrong".toCharArray());
            TestCase.fail();
        } catch (UnrecoverableKeyException expected) {
        }
        keyTest.setCertificateEntry("alias1", cert[1]);
        TestCase.assertNull("the private key returned from getKey for a certificate entry is not null", keyTest.getKey("alias1", pssWord));
    }

    /**
     * java.security.KeyStore#isCertificateEntry(java.lang.String)
     */
    public void test_isCertificateEntryLjava_lang_String() throws Exception {
        // Test for method boolean
        // java.security.KeyStore.isCertificateEntry(java.lang.String)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.isCertificateEntry("alias");
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // alias 1
        keyTest.setCertificateEntry("alias1", cert[0]);
        // alias 2
        keyTest.setKeyEntry("alias2", KeyStore2Test.getPrivateKey(), pssWord, cert);
        TestCase.assertTrue("isCertificateEntry method returns false for a certificate", keyTest.isCertificateEntry("alias1"));
        TestCase.assertFalse("isCertificateEntry method returns true for noncertificate", keyTest.isCertificateEntry("alias2"));
    }

    /**
     * java.security.KeyStore#isKeyEntry(java.lang.String)
     */
    public void test_isKeyEntryLjava_lang_String() throws Exception {
        // Test for method boolean
        // java.security.KeyStore.isKeyEntry(java.lang.String)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.isKeyEntry("alias");
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // alias 1
        keyTest.setCertificateEntry("alias1", cert[0]);
        // alias 2
        keyTest.setKeyEntry("alias2", KeyStore2Test.getPrivateKey(), pssWord, cert);
        TestCase.assertTrue("isKeyEntry method returns false for a certificate", keyTest.isKeyEntry("alias2"));
        TestCase.assertFalse("isKeyEntry method returns true for noncertificate", keyTest.isKeyEntry("alias1"));
    }

    /**
     * java.security.KeyStore#load(java.io.InputStream, char[])
     */
    public void test_loadLjava_io_InputStream$C() throws Exception {
        // Test for method void java.security.KeyStore.load(java.io.InputStream,
        // char[])
        byte[] keyStore = creatCertificate();
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream in = new ByteArrayInputStream(keyStore);
        keyTest.load(in, pssWord);
        in.close();
        TestCase.assertTrue("alias1 is not a certificate", keyTest.isCertificateEntry("alias1"));
        TestCase.assertTrue("alias2 is not a keyEntry", keyTest.isKeyEntry("alias2"));
        TestCase.assertTrue("alias3 is not a certificate", keyTest.isCertificateEntry("alias3"));
        // test with null password
        keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        in = new ByteArrayInputStream(keyStore);
        keyTest.load(in, null);
        in.close();
        TestCase.assertTrue("alias1 is not a certificate", keyTest.isCertificateEntry("alias1"));
        TestCase.assertTrue("alias2 is not a keyEntry", keyTest.isKeyEntry("alias2"));
        TestCase.assertTrue("alias3 is not a certificate", keyTest.isCertificateEntry("alias3"));
    }

    /**
     * java.security.KeyStore#load(KeyStore.LoadStoreParameter param)
     */
    public void test_loadLjava_security_KeyStoreLoadStoreParameter() throws Exception {
        KeyStore.getInstance(KeyStore.getDefaultType()).load(null);
    }

    /**
     * java.security.KeyStore#setCertificateEntry(java.lang.String,
     *        java.security.cert.Certificate)
     */
    public void test_setCertificateEntryLjava_lang_StringLjava_security_cert_Certificate() throws Exception {
        // Test for method void
        // java.security.KeyStore.setCertificateEntry(java.lang.String,
        // java.security.cert.Certificate)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = ((X509Certificate) (cf.generateCertificate(certArray)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.setCertificateEntry("alias", cert);
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        PublicKey pub = cert.getPublicKey();
        keyTest.setCertificateEntry("alias1", cert);
        TestCase.assertTrue("the entry specified by the alias alias1 is not a certificate", keyTest.isCertificateEntry("alias1"));
        Certificate resultCert = keyTest.getCertificate("alias1");
        TestCase.assertEquals(("the public key of the certificate from getCertificate() " + "did not equal the original certificate"), pub, resultCert.getPublicKey());
    }

    /**
     * java.security.KeyStore#setKeyEntry(java.lang.String,
     *        java.security.Key, char[], java.security.cert.Certificate[])
     */
    public void test_setKeyEntryLjava_lang_StringLjava_security_Key$C$Ljava_security_cert_Certificate() throws Exception {
        // Test for method void
        // java.security.KeyStore.setKeyEntry(java.lang.String,
        // java.security.Key, char[], java.security.cert.Certificate[])
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.setKeyEntry("alias3", KeyStore2Test.getPrivateKey(), pssWord, cert);
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        keyTest.setKeyEntry("alias3", KeyStore2Test.getPrivateKey(), pssWord, cert);
        TestCase.assertTrue("the entry specified by the alias alias3 is not a keyEntry", keyTest.isKeyEntry("alias3"));
        try {
            keyTest.setKeyEntry("alias4", KeyStore2Test.getPrivateKey(), pssWord, new Certificate[]{  });
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * java.security.KeyStore#size()
     */
    public void test_size() throws Exception {
        // Test for method int java.security.KeyStore.size()
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] cert = new X509Certificate[2];
        cert[0] = ((X509Certificate) (cf.generateCertificate(certArray)));
        cert[1] = ((X509Certificate) (cf.generateCertificate(certArray2)));
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.size();
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, null);
        // alias 1
        keyTest.setCertificateEntry("alias1", cert[0]);
        // alias 2
        keyTest.setKeyEntry("alias2", KeyStore2Test.getPrivateKey(), pssWord, cert);
        // alias 3
        keyTest.setCertificateEntry("alias3", cert[1]);
        TestCase.assertEquals("the size of the keyStore is not 3", 3, keyTest.size());
    }

    public void test_deleteEmptyEntryEmptyAlias() throws Exception {
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        keyTest.load(null, null);
        keyTest.deleteEntry("");
    }

    public void test_deleteEmptyEntryBogusAlias() throws Exception {
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        keyTest.load(null, null);
        keyTest.deleteEntry("bogus");
    }

    /**
     * java.security.KeyStore#deleteEntry(String)
     */
    public void test_deleteEntry() throws Exception {
        try {
            KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
            keyTest.load(null, null);
            keyTest.deleteEntry(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        keyTest.load(null, "password".toCharArray());
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pssWord);
        Certificate[] chain = new Certificate[]{ new KeyStore2Test.MyCertificate("DSA", testEncoding), new KeyStore2Test.MyCertificate("DSA", testEncoding) };
        KeyStore.PrivateKeyEntry pkEntry = new KeyStore.PrivateKeyEntry(KeyStore2Test.getPrivateKey(), chain);
        keyTest.setEntry("symKey", pkEntry, pp);
        keyTest.deleteEntry("symKey");
    }

    /**
     * java.security.KeyStore#getCreationDate(String)
     */
    public void test_getCreationDate() throws Exception {
        String type = "DSA";
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.getCreationDate("anAlias");
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, pssWord);
        TestCase.assertNull(keyTest.getCreationDate(""));
        try {
            keyTest.getCreationDate(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        Certificate[] chain = new Certificate[]{ new KeyStore2Test.MyCertificate(type, testEncoding), new KeyStore2Test.MyCertificate(type, testEncoding) };
        PrivateKey privateKey1 = KeyFactory.getInstance(type).generatePrivate(new DSAPrivateKeySpec(new BigInteger("0"), new BigInteger("0"), new BigInteger("0"), new BigInteger("0")));
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pssWord);
        KeyStore.PrivateKeyEntry pke = new KeyStore.PrivateKeyEntry(KeyStore2Test.getPrivateKey(), chain);
        KeyStore.PrivateKeyEntry pke1 = new KeyStore.PrivateKeyEntry(privateKey1, chain);
        keyTest.setEntry("alias1", pke, pp);
        keyTest.setEntry("alias2", pke1, pp);
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int dayExpected = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int monthExpected = Calendar.getInstance().get(Calendar.MONTH);
        int yearExpected = Calendar.getInstance().get(Calendar.YEAR);
        int hourExpected = Calendar.getInstance().get(Calendar.HOUR);
        int minuteExpected = Calendar.getInstance().get(Calendar.MINUTE);
        Calendar.getInstance().setTimeInMillis(keyTest.getCreationDate("alias1").getTime());
        int dayActual1 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int monthActual1 = Calendar.getInstance().get(Calendar.MONTH);
        int yearActual1 = Calendar.getInstance().get(Calendar.YEAR);
        int hourActual1 = Calendar.getInstance().get(Calendar.HOUR);
        int minuteActual1 = Calendar.getInstance().get(Calendar.MINUTE);
        TestCase.assertEquals(dayExpected, dayActual1);
        TestCase.assertEquals(monthExpected, monthActual1);
        TestCase.assertEquals(yearExpected, yearActual1);
        TestCase.assertEquals(hourExpected, hourActual1);
        TestCase.assertEquals(minuteExpected, minuteActual1);
        Calendar.getInstance().setTimeInMillis(keyTest.getCreationDate("alias2").getTime());
        int dayActual2 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int monthActual2 = Calendar.getInstance().get(Calendar.MONTH);
        int yearActual2 = Calendar.getInstance().get(Calendar.YEAR);
        int hourActual2 = Calendar.getInstance().get(Calendar.HOUR);
        int minuteActual2 = Calendar.getInstance().get(Calendar.MINUTE);
        TestCase.assertEquals(dayExpected, dayActual2);
        TestCase.assertEquals(monthExpected, monthActual2);
        TestCase.assertEquals(yearExpected, yearActual2);
        TestCase.assertEquals(hourExpected, hourActual2);
        TestCase.assertEquals(minuteExpected, minuteActual2);
        try {
            keyTest.getCreationDate(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     * java.security.KeyStore#getEntry(String,
     *        KeyStore.ProtectionParameter)
     */
    public void test_getEntry() throws Exception {
        String type = "DSA";
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyTest.getEntry("anAlias", new KeyStore.PasswordProtection(new char[]{  }));
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyTest.load(null, pssWord);
        try {
            keyTest.getEntry(null, new KeyStore.PasswordProtection(new char[]{  }));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        keyTest.getEntry("anAlias", null);
        try {
            keyTest.getEntry(null, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertNull(keyTest.getEntry("alias", null));
        Certificate[] chain = new Certificate[]{ new KeyStore2Test.MyCertificate(type, testEncoding), new KeyStore2Test.MyCertificate(type, testEncoding) };
        DSAPrivateKey privateKey1 = ((DSAPrivateKey) (KeyFactory.getInstance(type).generatePrivate(new DSAPrivateKeySpec(new BigInteger("1"), new BigInteger("2"), new BigInteger("3"), new BigInteger("4")))));
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pssWord);
        TestCase.assertNull(keyTest.getEntry("alias", pp));
        KeyStore.PrivateKeyEntry pke1 = new KeyStore.PrivateKeyEntry(KeyStore2Test.getPrivateKey(), chain);
        KeyStore.PrivateKeyEntry pke2 = new KeyStore.PrivateKeyEntry(privateKey1, chain);
        keyTest.setEntry("alias1", pke1, pp);
        keyTest.setEntry("alias2", pke2, pp);
        TestCase.assertNull(keyTest.getEntry("alias", pp));
        KeyStore.PrivateKeyEntry pkeActual1 = ((KeyStore.PrivateKeyEntry) (keyTest.getEntry("alias1", pp)));
        KeyStore.PrivateKeyEntry pkeActual2 = ((KeyStore.PrivateKeyEntry) (keyTest.getEntry("alias2", pp)));
        TestCase.assertTrue(Arrays.equals(chain, pkeActual1.getCertificateChain()));
        TestCase.assertEquals(KeyStore2Test.getPrivateKey(), pkeActual1.getPrivateKey());
        TestCase.assertEquals(new KeyStore2Test.MyCertificate(type, testEncoding), pkeActual1.getCertificate());
        TestCase.assertTrue(keyTest.entryInstanceOf("alias1", KeyStore.PrivateKeyEntry.class));
        TestCase.assertTrue(Arrays.equals(chain, pkeActual2.getCertificateChain()));
        DSAPrivateKey entryPrivateKey = ((DSAPrivateKey) (pkeActual2.getPrivateKey()));
        TestCase.assertEquals(privateKey1.getX(), entryPrivateKey.getX());
        TestCase.assertEquals(privateKey1.getParams().getG(), entryPrivateKey.getParams().getG());
        TestCase.assertEquals(privateKey1.getParams().getP(), entryPrivateKey.getParams().getP());
        TestCase.assertEquals(privateKey1.getParams().getQ(), entryPrivateKey.getParams().getQ());
        TestCase.assertEquals(new KeyStore2Test.MyCertificate(type, testEncoding), pkeActual2.getCertificate());
        TestCase.assertTrue(keyTest.entryInstanceOf("alias2", KeyStore.PrivateKeyEntry.class));
    }

    /**
     * java.security.KeyStore#setEntry(String, KeyStore.Entry,
     *        KeyStore.ProtectionParameter)
     */
    public void test_setEntry() throws Exception {
        String type = "DSA";
        KeyStore keyTest = KeyStore.getInstance(KeyStore.getDefaultType());
        keyTest.load(null, pssWord);
        Certificate[] chain = new Certificate[]{ new KeyStore2Test.MyCertificate(type, testEncoding), new KeyStore2Test.MyCertificate(type, testEncoding) };
        DSAPrivateKey privateKey1 = ((DSAPrivateKey) (KeyFactory.getInstance(type).generatePrivate(new DSAPrivateKeySpec(new BigInteger("1"), new BigInteger("2"), new BigInteger("3"), new BigInteger("4")))));
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pssWord);
        KeyStore.PrivateKeyEntry pke = new KeyStore.PrivateKeyEntry(KeyStore2Test.getPrivateKey(), chain);
        KeyStore.PrivateKeyEntry pke1 = new KeyStore.PrivateKeyEntry(privateKey1, chain);
        try {
            keyTest.setEntry("alias", pke, null);
            TestCase.assertFalse(IS_RI);// BKS KeyStore does not require a password

        } catch (KeyStoreException e) {
            TestCase.assertTrue(IS_RI);// JKS KeyStore requires a password

        }
        keyTest.setEntry("alias", pke, pp);
        KeyStore.PrivateKeyEntry pkeActual = ((KeyStore.PrivateKeyEntry) (keyTest.getEntry("alias", pp)));
        TestCase.assertTrue(Arrays.equals(chain, pkeActual.getCertificateChain()));
        TestCase.assertEquals(KeyStore2Test.getPrivateKey(), pkeActual.getPrivateKey());
        TestCase.assertEquals(new KeyStore2Test.MyCertificate(type, testEncoding), pkeActual.getCertificate());
        TestCase.assertTrue(keyTest.entryInstanceOf("alias", KeyStore.PrivateKeyEntry.class));
        keyTest.setEntry("alias", pke1, pp);
        pkeActual = ((KeyStore.PrivateKeyEntry) (keyTest.getEntry("alias", pp)));
        TestCase.assertTrue(Arrays.equals(chain, pkeActual.getCertificateChain()));
        DSAPrivateKey actualPrivateKey = ((DSAPrivateKey) (pkeActual.getPrivateKey()));
        TestCase.assertEquals(privateKey1.getX(), actualPrivateKey.getX());
        TestCase.assertEquals(privateKey1.getParams().getG(), actualPrivateKey.getParams().getG());
        TestCase.assertEquals(privateKey1.getParams().getP(), actualPrivateKey.getParams().getP());
        TestCase.assertEquals(privateKey1.getParams().getQ(), actualPrivateKey.getParams().getQ());
        TestCase.assertEquals(new KeyStore2Test.MyCertificate(type, testEncoding), pkeActual.getCertificate());
        TestCase.assertTrue(keyTest.entryInstanceOf("alias", KeyStore.PrivateKeyEntry.class));
        keyTest.setEntry("alias2", pke1, pp);
        pkeActual = ((KeyStore.PrivateKeyEntry) (keyTest.getEntry("alias2", pp)));
        TestCase.assertTrue(Arrays.equals(chain, pkeActual.getCertificateChain()));
        actualPrivateKey = ((DSAPrivateKey) (pkeActual.getPrivateKey()));
        TestCase.assertEquals(privateKey1.getX(), actualPrivateKey.getX());
        TestCase.assertEquals(privateKey1.getParams().getG(), actualPrivateKey.getParams().getG());
        TestCase.assertEquals(privateKey1.getParams().getP(), actualPrivateKey.getParams().getP());
        TestCase.assertEquals(privateKey1.getParams().getQ(), actualPrivateKey.getParams().getQ());
        TestCase.assertEquals(new KeyStore2Test.MyCertificate(type, testEncoding), pkeActual.getCertificate());
        TestCase.assertTrue(keyTest.entryInstanceOf("alias2", KeyStore.PrivateKeyEntry.class));
        try {
            keyTest.setEntry(null, null, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /* java.security.KeyStore.entryInstanceOf(String, Class<? extends
    Entry>)
     */
    public void test_entryInstanceOf() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.entryInstanceOf("anAlias", KeyStore.SecretKeyEntry.class);
            TestCase.fail();
        } catch (KeyStoreException expected) {
        }
        keyStore.load(null, "pwd".toCharArray());
        // put the key into keystore
        String alias = "alias";
        Certificate[] chain = new Certificate[]{ new KeyStore2Test.MyCertificate("DSA", testEncoding), new KeyStore2Test.MyCertificate("DSA", testEncoding) };
        keyStore.setKeyEntry(alias, KeyStore2Test.getPrivateKey(), "pwd".toCharArray(), chain);
        TestCase.assertTrue(keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class));
        TestCase.assertFalse(keyStore.entryInstanceOf(alias, KeyStore.SecretKeyEntry.class));
        TestCase.assertFalse(keyStore.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class));
        try {
            keyStore.entryInstanceOf(null, KeyStore.SecretKeyEntry.class);
        } catch (NullPointerException expected) {
        }
        try {
            keyStore.entryInstanceOf("anAlias", null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     * java.security.KeyStore#store(KeyStore.LoadStoreParameter)
     */
    public void test_store_java_securityKeyStore_LoadStoreParameter() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, "pwd".toCharArray());
        try {
            keyStore.store(null);
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    /**
     * java.security.KeyStore#store(OutputStream, char[])
     */
    public void test_store_java_io_OutputStream_char() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.store(new ByteArrayOutputStream(), "pwd".toCharArray());
        } catch (KeyStoreException expected) {
        }
        keyStore.load(null, "pwd".toCharArray());
        try {
            keyStore.store(null, "pwd".toCharArray());
            TestCase.fail();
        } catch (NullPointerException expected) {
        } catch (IOException expected) {
        }
    }

    class MyCertificate extends Certificate {
        // MyCertificate encoding
        private final byte[] encoding;

        public MyCertificate(String type, byte[] encoding) {
            super(type);
            // don't copy to allow null parameter in test
            this.encoding = encoding;
        }

        public byte[] getEncoded() throws CertificateEncodingException {
            // do copy to force NPE in test
            return encoding.clone();
        }

        public void verify(PublicKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CertificateException {
        }

        public void verify(PublicKey key, String sigProvider) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CertificateException {
        }

        public String toString() {
            return ("[My test Certificate, type: " + (getType())) + "]";
        }

        public PublicKey getPublicKey() {
            return new PublicKey() {
                public String getAlgorithm() {
                    return "DSA";
                }

                public byte[] getEncoded() {
                    return new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)) };
                }

                public String getFormat() {
                    return "TEST_FORMAT";
                }
            };
        }
    }
}

