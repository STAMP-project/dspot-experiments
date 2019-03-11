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
package org.apache.harmony.security.tests.java.security;


import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import javax.crypto.SecretKey;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MyKeyStoreSpi;
import org.apache.harmony.security.tests.support.MyLoadStoreParams;


public class KeyStoreSpiTest extends TestCase {
    /* java.security.KeyStore.engineEntryInstanceOf(String, Class<?
    extends Entry>)
     */
    public void test_engineEntryInstanceOf() throws Exception {
        KeyStoreSpi ksSpi = new MyKeyStoreSpi();
        TestCase.assertTrue(ksSpi.engineEntryInstanceOf("test_engineEntryInstanceOf_Alias1", KeyStore.PrivateKeyEntry.class));
        TestCase.assertFalse(ksSpi.engineEntryInstanceOf("test_engineEntryInstanceOf_Alias2", KeyStore.SecretKeyEntry.class));
        TestCase.assertFalse(ksSpi.engineEntryInstanceOf("test_engineEntryInstanceOf_Alias3", KeyStore.TrustedCertificateEntry.class));
        try {
            TestCase.assertFalse(ksSpi.engineEntryInstanceOf(null, KeyStore.TrustedCertificateEntry.class));
        } catch (NullPointerException expected) {
        }
        try {
            TestCase.assertFalse(ksSpi.engineEntryInstanceOf("test_engineEntryInstanceOf_Alias1", null));
        } catch (NullPointerException expected) {
        }
    }

    public void testKeyStoreSpi01() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException {
        final boolean[] keyEntryWasSet = new boolean[1];
        KeyStoreSpi ksSpi = new MyKeyStoreSpi() {
            @Override
            public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
                keyEntryWasSet[0] = true;
            }
        };
        BadKeyStoreEntry badEntry = new BadKeyStoreEntry();
        BadKeyStoreProtectionParameter badParameter = new BadKeyStoreProtectionParameter();
        KeyStore.SecretKeyEntry dummyEntry = new KeyStore.SecretKeyEntry(new SecretKey() {
            @Override
            public String getAlgorithm() {
                return null;
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return null;
            }
        });
        try {
            ksSpi.engineStore(null);
        } catch (UnsupportedOperationException expected) {
        }
        TestCase.assertNull("Not null entry", ksSpi.engineGetEntry("aaa", null));
        TestCase.assertNull("Not null entry", ksSpi.engineGetEntry(null, badParameter));
        TestCase.assertNull("Not null entry", ksSpi.engineGetEntry("aaa", badParameter));
        try {
            ksSpi.engineSetEntry("", null, null);
            TestCase.fail("KeyStoreException or NullPointerException must be thrown");
        } catch (KeyStoreException expected) {
        } catch (NullPointerException expected) {
        }
        try {
            ksSpi.engineSetEntry("", new KeyStore.TrustedCertificateEntry(new MyCertificate("type", new byte[0])), null);
            TestCase.fail("KeyStoreException must be thrown");
        } catch (KeyStoreException expected) {
        }
        try {
            ksSpi.engineSetEntry("aaa", badEntry, null);
            TestCase.fail("KeyStoreException must be thrown");
        } catch (KeyStoreException expected) {
        }
        ksSpi.engineSetEntry("aaa", dummyEntry, null);
        TestCase.assertTrue(keyEntryWasSet[0]);
    }

    /**
     * Test for <code>KeyStoreSpi()</code> constructor and abstract engine
     * methods. Assertion: creates new KeyStoreSpi object.
     */
    public void testKeyStoreSpi02() throws NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        KeyStoreSpi ksSpi = new MyKeyStoreSpi();
        TestCase.assertNull("engineGetKey(..) must return null", ksSpi.engineGetKey("", new char[0]));
        TestCase.assertNull("engineGetCertificateChain(..) must return null", ksSpi.engineGetCertificateChain(""));
        TestCase.assertNull("engineGetCertificate(..) must return null", ksSpi.engineGetCertificate(""));
        TestCase.assertEquals("engineGetCreationDate(..) must return Date(0)", new Date(0), ksSpi.engineGetCreationDate(""));
        try {
            ksSpi.engineSetKeyEntry("", null, new char[0], new Certificate[0]);
            TestCase.fail("KeyStoreException must be thrown from engineSetKeyEntry(..)");
        } catch (KeyStoreException expected) {
        }
        try {
            ksSpi.engineSetKeyEntry("", new byte[0], new Certificate[0]);
            TestCase.fail("KeyStoreException must be thrown from engineSetKeyEntry(..)");
        } catch (KeyStoreException expected) {
        }
        try {
            ksSpi.engineSetCertificateEntry("", null);
            TestCase.fail(("KeyStoreException must be thrown " + "from engineSetCertificateEntry(..)"));
        } catch (KeyStoreException expected) {
        }
        try {
            ksSpi.engineDeleteEntry("");
            TestCase.fail("KeyStoreException must be thrown from engineDeleteEntry(..)");
        } catch (KeyStoreException expected) {
        }
        TestCase.assertNull("engineAliases() must return null", ksSpi.engineAliases());
        TestCase.assertFalse("engineContainsAlias(..) must return false", ksSpi.engineContainsAlias(""));
        TestCase.assertEquals("engineSize() must return 0", 0, ksSpi.engineSize());
        try {
            ksSpi.engineStore(null, null);
            TestCase.fail("IOException must be thrown");
        } catch (IOException expected) {
        }
    }

    /**
     * java.security.KeyStoreSpi#engineLoad(KeyStore.LoadStoreParameter)
     */
    public void test_engineLoadLjava_security_KeyStore_LoadStoreParameter() throws Exception {
        final String msg = "error";
        KeyStoreSpi ksSpi = new MyKeyStoreSpi() {
            public void engineLoad(InputStream stream, char[] password) {
                TestCase.assertNull(stream);
                TestCase.assertNull(password);
                throw new RuntimeException(msg);
            }
        };
        try {
            ksSpi.engineLoad(null);
            TestCase.fail("Should throw exception");
        } catch (RuntimeException expected) {
            TestCase.assertSame(msg, expected.getMessage());
        }
        // test: protection parameter is null
        try {
            ksSpi.engineLoad(new MyLoadStoreParams(null));
            TestCase.fail("No expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        // test: protection parameter is not instanceof
        // PasswordProtection or CallbackHandlerProtection
        try {
            ksSpi.engineLoad(new MyLoadStoreParams(new BadKeyStoreProtectionParameter()));
            TestCase.fail("No expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }
}

