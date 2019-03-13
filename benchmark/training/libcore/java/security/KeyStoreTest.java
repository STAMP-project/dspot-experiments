/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.security;


import StandardNames.KEY_STORE_ALGORITHM;
import StandardNames.SECURITY_PROVIDER_NAME;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import junit.framework.TestCase;

import static StandardNames.IS_RI;
import static StandardNames.SECURITY_PROVIDER_NAME;
import static java.security.KeyStore.Builder.newInstance;


public class KeyStoreTest extends TestCase {
    private static KeyStore.PrivateKeyEntry PRIVATE_KEY;

    private static KeyStore.PrivateKeyEntry PRIVATE_KEY_2;

    private static SecretKey SECRET_KEY;

    private static SecretKey SECRET_KEY_2;

    private static final String ALIAS_PRIVATE = "private";

    private static final String ALIAS_CERTIFICATE = "certificate";

    private static final String ALIAS_SECRET = "secret";

    private static final String ALIAS_ALT_CASE_PRIVATE = "pRiVaTe";

    private static final String ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE = "PrIvAtE-no-password";

    private static final String ALIAS_ALT_CASE_CERTIFICATE = "cErTiFiCaTe";

    private static final String ALIAS_ALT_CASE_SECRET = "sEcRet";

    private static final String ALIAS_UNICODE_PRIVATE = "\u6400\u7902\u3101\u8c02\u5002\u8702\udd01";

    private static final String ALIAS_UNICODE_NO_PASSWORD_PRIVATE = "\u926c\u0967\uc65b\ubc78";

    private static final String ALIAS_UNICODE_CERTIFICATE = "\u5402\udd01\u7902\u8702\u3101\u5f02\u3101\u5402\u5002\u8702\udd01";

    private static final String ALIAS_UNICODE_SECRET = "\ue224\ud424\ud224\ue124\ud424\ue324";

    private static final String ALIAS_NO_PASSWORD_PRIVATE = "private-no-password";

    private static final String ALIAS_NO_PASSWORD_SECRET = "secret-no-password";

    private static final char[] PASSWORD_STORE = "store password".toCharArray();

    private static final char[] PASSWORD_KEY = "key password".toCharArray();

    private static final char[] PASSWORD_BAD = "dummy".toCharArray();

    private static final KeyStore.ProtectionParameter PARAM_STORE = new KeyStore.PasswordProtection(KeyStoreTest.PASSWORD_STORE);

    private static final KeyStore.ProtectionParameter PARAM_KEY = new KeyStore.PasswordProtection(KeyStoreTest.PASSWORD_KEY);

    private static final KeyStore.ProtectionParameter PARAM_BAD = new KeyStore.PasswordProtection(KeyStoreTest.PASSWORD_BAD);

    public void test_KeyStore_create() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("KeyStore"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                KeyStore ks = KeyStore.getInstance(algorithm, provider);
                TestCase.assertEquals(provider, ks.getProvider());
                TestCase.assertEquals(algorithm, ks.getType());
            }
        }
    }

    public void test_KeyStore_getInstance() throws Exception {
        String type = KeyStore.getDefaultType();
        try {
            KeyStore.getInstance(null);
            TestCase.fail(type);
        } catch (NullPointerException expected) {
        }
        TestCase.assertNotNull(KeyStore.getInstance(type));
        String providerName = SECURITY_PROVIDER_NAME;
        try {
            KeyStore.getInstance(null, ((String) (null)));
            TestCase.fail(type);
        } catch (IllegalArgumentException expected) {
        }
        try {
            KeyStore.getInstance(null, providerName);
            TestCase.fail(type);
        } catch (Exception e) {
            if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                throw e;
            }
        }
        try {
            KeyStore.getInstance(type, ((String) (null)));
            TestCase.fail(type);
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertNotNull(KeyStore.getInstance(type, providerName));
        Provider provider = Security.getProvider(providerName);
        try {
            KeyStore.getInstance(null, ((Provider) (null)));
            TestCase.fail(type);
        } catch (IllegalArgumentException expected) {
        }
        try {
            KeyStore.getInstance(null, provider);
            TestCase.fail(type);
        } catch (NullPointerException expected) {
        }
        try {
            KeyStore.getInstance(type, ((Provider) (null)));
            TestCase.fail(type);
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertNotNull(KeyStore.getInstance(type, provider));
    }

    public void test_KeyStore_getDefaultType() throws Exception {
        String type = KeyStore.getDefaultType();
        TestCase.assertNotNull(type);
        KeyStore ks = KeyStore.getInstance(type);
        TestCase.assertNotNull(ks);
        TestCase.assertEquals(type, ks.getType());
    }

    public void test_KeyStore_getProvider() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        TestCase.assertNotNull(ks.getProvider());
        TestCase.assertNotNull(SECURITY_PROVIDER_NAME, ks.getProvider().getName());
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            TestCase.assertNotNull(keyStore.getProvider());
        }
    }

    public void test_KeyStore_getType() throws Exception {
        String type = KeyStore.getDefaultType();
        KeyStore ks = KeyStore.getInstance(type);
        TestCase.assertNotNull(ks.getType());
        TestCase.assertNotNull(type, ks.getType());
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            TestCase.assertNotNull(keyStore.getType());
        }
    }

    public void test_KeyStore_getKey() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.getKey(null, null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test odd inputs
            try {
                keyStore.getKey(null, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                    throw e;
                }
            }
            try {
                keyStore.getKey(null, KeyStoreTest.PASSWORD_KEY);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if ((((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            TestCase.assertNull(keyStore.getKey("", null));
            TestCase.assertNull(keyStore.getKey("", KeyStoreTest.PASSWORD_KEY));
            // test case sensitive
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
            } else {
                if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                }
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                }
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                } else {
                    TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                }
            }
            // test case insensitive
            if ((KeyStoreTest.isCaseSensitive(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else {
                if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                }
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                }
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                }
            }
            // test with null passwords
            if ((KeyStoreTest.isKeyPasswordSupported(keyStore)) && (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
            } else {
                if (KeyStoreTest.isReadOnly(keyStore)) {
                    TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
                } else
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        try {
                            keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null);
                            TestCase.fail(keyStore.getType());
                        } catch (Exception e) {
                            if (((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                                throw e;
                            }
                        }
                    }

            }
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, null));
            } else
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    try {
                        keyStore.getKey(KeyStoreTest.ALIAS_SECRET, null);
                        TestCase.fail(keyStore.getType());
                    } catch (Exception e) {
                        if (((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                            throw e;
                        }
                    }
                }

            // test with bad passwords
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
            } else
                if ((KeyStoreTest.isKeyPasswordSupported(keyStore)) && (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
                } else
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        try {
                            keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_BAD);
                            TestCase.fail(keyStore.getType());
                        } catch (UnrecoverableKeyException expected) {
                        }
                    }


            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_BAD));
            } else
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    try {
                        keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_BAD);
                        TestCase.fail(keyStore.getType());
                    } catch (UnrecoverableKeyException expected) {
                    }
                }

        }
    }

    public void test_KeyStore_getCertificateChain() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.getCertificateChain(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test odd inputs
            try {
                keyStore.getCertificateChain(null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                    throw e;
                }
            }
            TestCase.assertNull(keyStore.getCertificateChain(""));
            // test case sensitive
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getCertificateChain(KeyStoreTest.ALIAS_PRIVATE));
            } else
                if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                    KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_PRIVATE));
                } else
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
                    }


            // test case insensitive
            if ((KeyStoreTest.isReadOnly(keyStore)) || (KeyStoreTest.isCaseSensitive(keyStore))) {
                TestCase.assertNull(keyStore.getCertificateChain(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE));
            } else {
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE));
            }
        }
    }

    public void test_KeyStore_getCertificate() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.getCertificate(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test odd inputs
            try {
                keyStore.getCertificate(null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                    throw e;
                }
            }
            TestCase.assertNull(keyStore.getCertificate(""));
            // test case sensitive
            if ((!(KeyStoreTest.isReadOnly(keyStore))) && (KeyStoreTest.isCertificateEnabled(keyStore))) {
                KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
            } else {
                TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
            }
            // test case insensitive
            if ((KeyStoreTest.isReadOnly(keyStore)) || (KeyStoreTest.isCaseSensitive(keyStore))) {
                TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
            } else {
                if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                }
            }
        }
    }

    public void test_KeyStore_getCreationDate() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.getCreationDate(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        long before = System.currentTimeMillis();
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // add 1000 since some key stores round of time to nearest second
            long after = (System.currentTimeMillis()) + 1000;
            // test odd inputs
            try {
                keyStore.getCreationDate(null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertNull(keyStore.getCreationDate(""));
            // test case sensitive
            if ((!(KeyStoreTest.isReadOnly(keyStore))) && (KeyStoreTest.isCertificateEnabled(keyStore))) {
                Date date = keyStore.getCreationDate(KeyStoreTest.ALIAS_CERTIFICATE);
                TestCase.assertNotNull(date);
                TestCase.assertTrue(((("date should be after start time: " + (date.getTime())) + " >= ") + before), (before <= (date.getTime())));
                TestCase.assertTrue(((("date should be before expiry time: " + (date.getTime())) + " <= ") + after), ((date.getTime()) <= after));
            } else {
                TestCase.assertNull(keyStore.getCreationDate(KeyStoreTest.ALIAS_CERTIFICATE));
            }
            // test case insensitive
            if ((KeyStoreTest.isReadOnly(keyStore)) || (KeyStoreTest.isCaseSensitive(keyStore))) {
                TestCase.assertNull(keyStore.getCreationDate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
            } else {
                if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                    Date date = keyStore.getCreationDate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE);
                    TestCase.assertTrue((before <= (date.getTime())));
                    TestCase.assertTrue(((date.getTime()) <= after));
                }
            }
        }
    }

    public void test_KeyStore_setKeyEntry_Key() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.setKeyEntry(null, null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.setKeyEntry(null, null, null, null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            // test odd inputs
            try {
                keyStore.setKeyEntry(null, null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            try {
                keyStore.setKeyEntry(null, null, KeyStoreTest.PASSWORD_KEY, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            try {
                keyStore.setKeyEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey().getPrivateKey(), KeyStoreTest.PASSWORD_KEY, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (IllegalArgumentException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.clearKeyStore(keyStore);
            // test case sensitive
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
            }
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.getSecretKey(), KeyStoreTest.PASSWORD_KEY, null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                KeyStoreTest.setPrivateKey(keyStore);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_PRIVATE));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                KeyStoreTest.setPrivateKeyNoPassword(keyStore, KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey());
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.setSecretKey(keyStore);
                KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.getSecretKey(), KeyStoreTest.PASSWORD_KEY, null);
                    TestCase.fail(keyStore.getType());
                } catch (Exception e) {
                    if (((e.getClass()) != (KeyStoreException.class)) && ((e.getClass()) != (NullPointerException.class))) {
                        throw e;
                    }
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else
                if (KeyStoreTest.isCaseSensitive(keyStore)) {
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setPrivateKey(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.setPrivateKeyNoPassword(keyStore, KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                    }
                    if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setSecretKey(keyStore, KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.getSecretKey2());
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                    }
                } else {
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setPrivateKey(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.setPrivateKey(keyStore, KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                    }
                    if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setSecretKey(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getSecretKey2());
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                    }
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey().getPrivateKey(), null, KeyStoreTest.getPrivateKey().getCertificateChain());
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            // test with null passwords
            if ((KeyStoreTest.isNullPasswordAllowed(keyStore)) || (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                keyStore.setKeyEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey().getPrivateKey(), null, KeyStoreTest.getPrivateKey().getCertificateChain());
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
            } else {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey().getPrivateKey(), null, KeyStoreTest.getPrivateKey().getCertificateChain());
                    TestCase.fail(keyStore.getType());
                } catch (Exception e) {
                    if ((((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) && ((e.getClass()) != (KeyStoreException.class))) {
                        throw e;
                    }
                }
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                if ((KeyStoreTest.isNullPasswordAllowed(keyStore)) || (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.getSecretKey(), null, null);
                    KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, null));
                } else {
                    try {
                        keyStore.setKeyEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.getSecretKey(), null, null);
                        TestCase.fail(keyStore.getType());
                    } catch (Exception e) {
                        if ((((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) && ((e.getClass()) != (KeyStoreException.class))) {
                            throw e;
                        }
                    }
                }
            }
        }
    }

    public void test_KeyStore_setKeyEntry_array() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.setKeyEntry(null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.setKeyEntry(null, null, null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            // test odd inputs
            try {
                keyStore.setKeyEntry(null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) && ((e.getClass()) != (KeyStoreException.class))) && ((e.getClass()) != (RuntimeException.class))) {
                    throw e;
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            if (!(KeyStoreTest.isNullPasswordAllowed(keyStore))) {
                // TODO Use EncryptedPrivateKeyInfo to protect keys if
                // password is required.
                continue;
            }
            if (KeyStoreTest.isSetKeyByteArrayUnimplemented(keyStore)) {
                continue;
            }
            KeyStoreTest.clearKeyStore(keyStore);
            // test case sensitive
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
            }
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    KeyStoreTest.setPrivateKeyBytes(keyStore);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                KeyStoreTest.setPrivateKeyBytes(keyStore);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_PRIVATE));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                KeyStoreTest.setPrivateKeyNoPassword(keyStore, KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey());
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.setSecretKeyBytes(keyStore);
                KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.getSecretKey().getEncoded(), null);
                    TestCase.fail(keyStore.getType());
                } catch (KeyStoreException expected) {
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            if (!(KeyStoreTest.isNullPasswordAllowed(keyStore))) {
                // TODO Use EncryptedPrivateKeyInfo to protect keys if
                // password is required.
                continue;
            }
            if (KeyStoreTest.isSetKeyByteArrayUnimplemented(keyStore)) {
                continue;
            }
            KeyStoreTest.populate(keyStore);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else
                if (KeyStoreTest.isCaseSensitive(keyStore)) {
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setPrivateKeyBytes(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.setPrivateKeyNoPassword(keyStore, KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                    }
                    if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setSecretKeyBytes(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getSecretKey2());
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                    }
                } else {
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setPrivateKeyBytes(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.setPrivateKeyNoPassword(keyStore, KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey2());
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                    }
                    if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.setSecretKeyBytes(keyStore, KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getSecretKey2());
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                    }
                }

        }
    }

    public void test_KeyStore_setCertificateEntry() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.setCertificateEntry(null, null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test odd inputs
            try {
                keyStore.setCertificateEntry(null, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    keyStore.setCertificateEntry(KeyStoreTest.ALIAS_CERTIFICATE, null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            // Sort of delete by setting null.  Note that even though
            // certificate is null, size doesn't change,
            // isCertificateEntry returns true, and it is still listed in aliases.
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                try {
                    int size = keyStore.size();
                    keyStore.setCertificateEntry(KeyStoreTest.ALIAS_CERTIFICATE, null);
                    TestCase.assertNull(keyStore.getType(), keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    TestCase.assertEquals(keyStore.getType(), size, keyStore.size());
                    TestCase.assertTrue(keyStore.getType(), keyStore.isCertificateEntry(KeyStoreTest.ALIAS_CERTIFICATE));
                    TestCase.assertTrue(keyStore.getType(), Collections.list(keyStore.aliases()).contains(KeyStoreTest.ALIAS_CERTIFICATE));
                } catch (NullPointerException expectedSometimes) {
                    if ((!(("PKCS12".equalsIgnoreCase(keyStore.getType())) && ("BC".equalsIgnoreCase(keyStore.getProvider().getName())))) && (!("AndroidKeyStore".equalsIgnoreCase(keyStore.getType())))) {
                        throw expectedSometimes;
                    }
                }
            } else {
                try {
                    keyStore.setCertificateEntry(KeyStoreTest.ALIAS_CERTIFICATE, null);
                    TestCase.fail(keyStore.getType());
                } catch (KeyStoreException expected) {
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            if (!(KeyStoreTest.isCertificateEnabled(keyStore))) {
                continue;
            }
            KeyStoreTest.clearKeyStore(keyStore);
            TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    KeyStoreTest.setCertificate(keyStore);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            KeyStoreTest.setCertificate(keyStore);
            KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            if (!(KeyStoreTest.isCertificateEnabled(keyStore))) {
                continue;
            }
            KeyStoreTest.populate(keyStore);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
            } else
                if (KeyStoreTest.isCaseSensitive(keyStore)) {
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                    KeyStoreTest.setCertificate(keyStore, KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, KeyStoreTest.getPrivateKey2().getCertificate());
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    KeyStoreTest.assertCertificate2(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                } else {
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                    KeyStoreTest.setCertificate(keyStore, KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, KeyStoreTest.getPrivateKey2().getCertificate());
                    KeyStoreTest.assertCertificate2(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    KeyStoreTest.assertCertificate2(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                }

        }
    }

    public void test_KeyStore_deleteEntry() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.deleteEntry(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.deleteEntry(null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            // test odd inputs
            try {
                keyStore.deleteEntry(null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            keyStore.deleteEntry("");
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_PRIVATE);
                } catch (UnsupportedOperationException e) {
                }
                continue;
            }
            // test case sensitive
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_PRIVATE));
                keyStore.deleteEntry(KeyStoreTest.ALIAS_PRIVATE);
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
                keyStore.deleteEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE);
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                keyStore.deleteEntry(KeyStoreTest.ALIAS_SECRET);
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else {
                keyStore.deleteEntry(KeyStoreTest.ALIAS_SECRET);
            }
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                keyStore.deleteEntry(KeyStoreTest.ALIAS_CERTIFICATE);
                TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
            } else {
                keyStore.deleteEntry(KeyStoreTest.ALIAS_CERTIFICATE);
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test case insensitive
            if (KeyStoreTest.isCaseSensitive(keyStore)) {
                if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE);
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                }
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE);
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                }
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET);
                    KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                } else {
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_SECRET);
                }
                if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE);
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                } else {
                    keyStore.deleteEntry(KeyStoreTest.ALIAS_CERTIFICATE);
                }
            }
        }
    }

    public void test_KeyStore_aliases() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.aliases();
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isPersistentStorage(keyStore)) {
                TestCase.assertNotNull(("Should be able to query size: " + (keyStore.getType())), keyStore.aliases());
            } else
                if (KeyStoreTest.hasDefaultContents(keyStore)) {
                    TestCase.assertTrue(("Should have more than one alias already: " + (keyStore.getType())), keyStore.aliases().hasMoreElements());
                } else {
                    TestCase.assertEquals(("Should have no aliases:" + (keyStore.getType())), Collections.EMPTY_SET, new HashSet(Collections.list(keyStore.aliases())));
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            Set<String> expected = new HashSet<String>();
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_PRIVATE);
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE);
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_SECRET);
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    expected.add(KeyStoreTest.ALIAS_NO_PASSWORD_SECRET);
                }
            }
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_CERTIFICATE);
            }
            if (KeyStoreTest.isPersistentStorage(keyStore)) {
                TestCase.assertNotNull(("Should be able to query size: " + (keyStore.getType())), keyStore.aliases());
            } else
                if (KeyStoreTest.hasDefaultContents(keyStore)) {
                    TestCase.assertTrue(keyStore.aliases().hasMoreElements());
                } else {
                    TestCase.assertEquals(expected, new HashSet<String>(Collections.list(keyStore.aliases())));
                }

        }
    }

    public void test_KeyStore_containsAlias() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.containsAlias(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                keyStore.containsAlias(null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertFalse(keyStore.containsAlias(""));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            TestCase.assertFalse(keyStore.containsAlias(""));
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertFalse(keyStore.containsAlias(KeyStoreTest.ALIAS_PRIVATE));
                continue;
            }
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                TestCase.assertTrue(keyStore.containsAlias(KeyStoreTest.ALIAS_PRIVATE));
            } else
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    TestCase.assertTrue(keyStore.containsAlias(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
                }

            TestCase.assertEquals(KeyStoreTest.isSecretKeyEnabled(keyStore), keyStore.containsAlias(KeyStoreTest.ALIAS_SECRET));
            TestCase.assertEquals(KeyStoreTest.isCertificateEnabled(keyStore), keyStore.containsAlias(KeyStoreTest.ALIAS_CERTIFICATE));
            TestCase.assertEquals((!(KeyStoreTest.isCaseSensitive(keyStore))), keyStore.containsAlias(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE));
            TestCase.assertEquals(((!(KeyStoreTest.isCaseSensitive(keyStore))) && (KeyStoreTest.isSecretKeyEnabled(keyStore))), keyStore.containsAlias(KeyStoreTest.ALIAS_ALT_CASE_SECRET));
            TestCase.assertEquals(((!(KeyStoreTest.isCaseSensitive(keyStore))) && (KeyStoreTest.isCertificateEnabled(keyStore))), keyStore.containsAlias(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
        }
    }

    public void test_KeyStore_size() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.aliases();
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isPersistentStorage(keyStore)) {
                TestCase.assertTrue(("Should successfully query size: " + (keyStore.getType())), ((keyStore.size()) >= 0));
            } else
                if (KeyStoreTest.hasDefaultContents(keyStore)) {
                    TestCase.assertTrue(("Should have non-empty store: " + (keyStore.getType())), ((keyStore.size()) > 0));
                } else {
                    TestCase.assertEquals(("Should have empty store: " + (keyStore.getType())), 0, keyStore.size());
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            if (KeyStoreTest.hasDefaultContents(keyStore)) {
                TestCase.assertTrue(("Should have non-empty store: " + (keyStore.getType())), ((keyStore.size()) > 0));
                continue;
            }
            int expected = 0;
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                expected++;
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                expected++;
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                expected++;
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    expected++;
                }
            }
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                expected++;
            }
            TestCase.assertEquals(expected, keyStore.size());
        }
    }

    public void test_KeyStore_isKeyEntry() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.isKeyEntry(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                keyStore.isKeyEntry(null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertFalse(keyStore.isKeyEntry(""));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            TestCase.assertFalse(keyStore.isKeyEntry(""));
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertFalse(keyStore.isKeyEntry(KeyStoreTest.ALIAS_PRIVATE));
                continue;
            }
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                TestCase.assertTrue(keyStore.isKeyEntry(KeyStoreTest.ALIAS_PRIVATE));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                TestCase.assertTrue(keyStore.isKeyEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
            }
            TestCase.assertEquals(KeyStoreTest.isSecretKeyEnabled(keyStore), keyStore.isKeyEntry(KeyStoreTest.ALIAS_SECRET));
            TestCase.assertFalse(keyStore.isKeyEntry(KeyStoreTest.ALIAS_CERTIFICATE));
            TestCase.assertEquals((!(KeyStoreTest.isCaseSensitive(keyStore))), keyStore.isKeyEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE));
            TestCase.assertEquals(((!(KeyStoreTest.isCaseSensitive(keyStore))) && (KeyStoreTest.isSecretKeyEnabled(keyStore))), keyStore.isKeyEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET));
            TestCase.assertFalse(keyStore.isKeyEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
        }
    }

    public void test_KeyStore_isCertificateEntry() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.isCertificateEntry(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                try {
                    keyStore.isCertificateEntry(null);
                    TestCase.fail(keyStore.getType());
                } catch (NullPointerException expected) {
                }
            } else {
                TestCase.assertFalse(keyStore.isCertificateEntry(null));
            }
            TestCase.assertFalse(keyStore.isCertificateEntry(""));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            TestCase.assertFalse(keyStore.isCertificateEntry(""));
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                TestCase.assertFalse(keyStore.isCertificateEntry(KeyStoreTest.ALIAS_PRIVATE));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                TestCase.assertFalse(keyStore.isCertificateEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
            }
            TestCase.assertFalse(keyStore.isCertificateEntry(KeyStoreTest.ALIAS_SECRET));
            TestCase.assertEquals(((KeyStoreTest.isCertificateEnabled(keyStore)) && (!(KeyStoreTest.isReadOnly(keyStore)))), keyStore.isCertificateEntry(KeyStoreTest.ALIAS_CERTIFICATE));
            TestCase.assertFalse(keyStore.isCertificateEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE));
            TestCase.assertFalse(keyStore.isCertificateEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET));
            TestCase.assertEquals((((!(KeyStoreTest.isCaseSensitive(keyStore))) && (KeyStoreTest.isCertificateEnabled(keyStore))) && (!(KeyStoreTest.isReadOnly(keyStore)))), keyStore.isCertificateEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
        }
    }

    public void test_KeyStore_getCertificateAlias() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.getCertificateAlias(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            TestCase.assertNull(keyStore.getCertificateAlias(null));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            Set<String> expected = new HashSet<String>();
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_PRIVATE);
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE);
            }
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                expected.add(KeyStoreTest.ALIAS_CERTIFICATE);
            }
            String actual = keyStore.getCertificateAlias(KeyStoreTest.getPrivateKey().getCertificate());
            TestCase.assertEquals((!(KeyStoreTest.isReadOnly(keyStore))), expected.contains(actual));
            TestCase.assertNull(keyStore.getCertificateAlias(KeyStoreTest.getPrivateKey2().getCertificate()));
        }
    }

    public void test_KeyStore_store_OutputStream() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.store(null, null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if ((KeyStoreTest.isLoadStoreUnsupported(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                try {
                    keyStore.store(out, null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                keyStore.store(out, null);
                assertEqualsKeyStores(keyStore, out, null);
                continue;
            }
            try {
                keyStore.store(out, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (IllegalArgumentException.class)) && ((e.getClass()) != (NullPointerException.class))) {
                    throw e;
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if ((KeyStoreTest.isLoadStoreUnsupported(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                try {
                    keyStore.store(out, null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
            } else
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    keyStore.store(out, null);
                    assertEqualsKeyStores(keyStore, out, null);
                } else {
                    try {
                        keyStore.store(out, null);
                        TestCase.fail(keyStore.getType());
                    } catch (Exception e) {
                        if (((e.getClass()) != (IllegalArgumentException.class)) && ((e.getClass()) != (NullPointerException.class))) {
                            throw e;
                        }
                    }
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if ((KeyStoreTest.isLoadStoreUnsupported(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                try {
                    keyStore.store(out, KeyStoreTest.PASSWORD_STORE);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException e) {
                }
                continue;
            }
            keyStore.store(out, KeyStoreTest.PASSWORD_STORE);
            assertEqualsKeyStores(keyStore, out, KeyStoreTest.PASSWORD_STORE);
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if ((KeyStoreTest.isLoadStoreUnsupported(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                try {
                    keyStore.store(out, KeyStoreTest.PASSWORD_STORE);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException e) {
                }
                continue;
            }
            keyStore.store(out, KeyStoreTest.PASSWORD_STORE);
            assertEqualsKeyStores(keyStore, out, KeyStoreTest.PASSWORD_STORE);
        }
    }

    public void test_KeyStore_store_LoadStoreParameter() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.store(null);
                TestCase.fail(keyStore.getType());
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                keyStore.store(null);
                TestCase.fail(keyStore.getType());
            } catch (UnsupportedOperationException expected) {
                TestCase.assertFalse(KeyStoreTest.isLoadStoreParameterSupported(keyStore));
            } catch (IllegalArgumentException expected) {
                // its supported, but null causes an exception
                TestCase.assertTrue(KeyStoreTest.isLoadStoreParameterSupported(keyStore));
            }
        }
    }

    public void test_KeyStore_load_InputStream() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            if (KeyStoreTest.isPersistentStorage(keyStore)) {
                TestCase.assertTrue(("Should be able to query size: " + (keyStore.getType())), ((keyStore.size()) >= 0));
            } else
                if (KeyStoreTest.hasDefaultContents(keyStore)) {
                    TestCase.assertTrue(("Should have non-empty store: " + (keyStore.getType())), ((keyStore.size()) > 0));
                } else {
                    TestCase.assertEquals(("Should have empty store: " + (keyStore.getType())), 0, keyStore.size());
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            if (KeyStoreTest.isLoadStoreUnsupported(keyStore)) {
                continue;
            }
            keyStore.load(null, KeyStoreTest.PASSWORD_STORE);
            if (KeyStoreTest.isPersistentStorage(keyStore)) {
                TestCase.assertTrue(("Should be able to query size: " + (keyStore.getType())), ((keyStore.size()) >= 0));
            } else
                if (KeyStoreTest.hasDefaultContents(keyStore)) {
                    TestCase.assertTrue(("Should have non-empty store: " + (keyStore.getType())), ((keyStore.size()) > 0));
                } else {
                    TestCase.assertEquals(("Should have empty store: " + (keyStore.getType())), 0, keyStore.size());
                }

        }
        // test_KeyStore_store_OutputStream effectively tests load as well as store
    }

    public void test_KeyStore_load_LoadStoreParameter() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null);
            if (KeyStoreTest.isPersistentStorage(keyStore)) {
                TestCase.assertTrue(("Should be able to query size: " + (keyStore.getType())), ((keyStore.size()) >= 0));
            } else
                if (KeyStoreTest.hasDefaultContents(keyStore)) {
                    TestCase.assertTrue(("Should have non-empty store: " + (keyStore.getType())), ((keyStore.size()) > 0));
                } else {
                    TestCase.assertEquals(("Should have empty store: " + (keyStore.getType())), 0, keyStore.size());
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.load(new KeyStore.LoadStoreParameter() {
                    public KeyStore.ProtectionParameter getProtectionParameter() {
                        return null;
                    }
                });
                TestCase.fail(keyStore.getType());
            } catch (UnsupportedOperationException expected) {
            }
        }
    }

    public void test_KeyStore_getEntry() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.getEntry(null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test odd inputs
            try {
                keyStore.getEntry(null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            try {
                keyStore.getEntry(null, KeyStoreTest.PARAM_KEY);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertNull(keyStore.getEntry("", null));
            TestCase.assertNull(keyStore.getEntry("", KeyStoreTest.PARAM_KEY));
            // test case sensitive
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PARAM_KEY));
            } else {
                if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PARAM_KEY));
                } else
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                    }

                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    KeyStoreTest.assertSecretKey(keyStore.getEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PARAM_KEY));
                } else {
                    TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PARAM_KEY));
                }
                if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                    KeyStoreTest.assertCertificate(keyStore.getEntry(KeyStoreTest.ALIAS_CERTIFICATE, null));
                } else {
                    TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_CERTIFICATE, null));
                }
            }
            // test case insensitive
            if ((KeyStoreTest.isCaseSensitive(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PARAM_KEY));
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PARAM_KEY));
            } else {
                KeyStoreTest.assertPrivateKey(keyStore.getEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PARAM_KEY));
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    KeyStoreTest.assertSecretKey(keyStore.getEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PARAM_KEY));
                }
            }
            if ((KeyStoreTest.isCaseSensitive(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, null));
            } else {
                if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                    KeyStoreTest.assertCertificate(keyStore.getEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, null));
                }
            }
            // test with null passwords
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
            } else
                if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                    KeyStoreTest.assertPrivateKey(keyStore.getEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                } else
                    if ((KeyStoreTest.isKeyPasswordSupported(keyStore)) && (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                        KeyStoreTest.assertPrivateKey(keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, null));
                    } else
                        if (KeyStoreTest.isKeyPasswordIgnored(keyStore)) {
                            try {
                                keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, null);
                                TestCase.fail(keyStore.getType());
                            } catch (Exception e) {
                                if (((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                                    throw e;
                                }
                            }
                        }



            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_SECRET, null));
            } else
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    try {
                        keyStore.getEntry(KeyStoreTest.ALIAS_SECRET, null);
                        TestCase.fail(keyStore.getType());
                    } catch (Exception e) {
                        if (((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) {
                            throw e;
                        }
                    }
                }

            // test with bad passwords
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PARAM_BAD));
            } else
                if ((KeyStoreTest.isKeyPasswordSupported(keyStore)) && (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                    KeyStoreTest.assertPrivateKey(keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PARAM_BAD));
                } else
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        try {
                            keyStore.getEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PARAM_BAD);
                            TestCase.fail(keyStore.getType());
                        } catch (UnrecoverableKeyException expected) {
                        }
                    }


            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PARAM_BAD));
            } else
                if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                    try {
                        keyStore.getEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PARAM_BAD);
                        TestCase.fail(keyStore.getType());
                    } catch (UnrecoverableKeyException expected) {
                    }
                }

        }
    }

    public static class FakeProtectionParameter implements KeyStore.ProtectionParameter {}

    public void test_KeyStore_setEntry() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                keyStore.setEntry(null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                keyStore.setEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey(), new KeyStoreTest.FakeProtectionParameter());
                TestCase.fail(("Should not accept unknown ProtectionParameter: " + (keyStore.getProvider())));
            } catch (KeyStoreException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            // test odd inputs
            try {
                keyStore.setEntry(null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            try {
                keyStore.setEntry(null, null, KeyStoreTest.PARAM_KEY);
                TestCase.fail(keyStore.getType());
            } catch (Exception e) {
                if (((e.getClass()) != (NullPointerException.class)) && ((e.getClass()) != (KeyStoreException.class))) {
                    throw e;
                }
            }
            try {
                keyStore.setEntry("", null, KeyStoreTest.PARAM_KEY);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.clearKeyStore(keyStore);
            // test case sensitive
            TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.setEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey(), KeyStoreTest.PARAM_KEY);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                keyStore.setEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey(), KeyStoreTest.PARAM_KEY);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_PRIVATE));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                keyStore.setEntry(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey(), null);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE));
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                keyStore.setEntry(KeyStoreTest.ALIAS_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey()), KeyStoreTest.PARAM_KEY);
                KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.getSecretKey(), KeyStoreTest.PASSWORD_KEY, null);
                    TestCase.fail(keyStore.getType());
                } catch (KeyStoreException expected) {
                }
            }
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                keyStore.setEntry(KeyStoreTest.ALIAS_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), null);
                KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
            } else {
                try {
                    keyStore.setEntry(KeyStoreTest.ALIAS_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), null);
                    TestCase.fail(keyStore.getType());
                } catch (KeyStoreException expected) {
                }
            }
            if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                keyStore.setEntry(KeyStoreTest.ALIAS_UNICODE_PRIVATE, KeyStoreTest.getPrivateKey(), KeyStoreTest.PARAM_KEY);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_UNICODE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_UNICODE_PRIVATE));
            }
            if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                keyStore.setEntry(KeyStoreTest.ALIAS_UNICODE_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey(), null);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_UNICODE_NO_PASSWORD_PRIVATE, null));
                KeyStoreTest.assertCertificateChain(keyStore.getCertificateChain(KeyStoreTest.ALIAS_UNICODE_NO_PASSWORD_PRIVATE));
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_UNICODE_SECRET, KeyStoreTest.PASSWORD_KEY));
                keyStore.setEntry(KeyStoreTest.ALIAS_UNICODE_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey()), KeyStoreTest.PARAM_KEY);
                KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_UNICODE_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else {
                try {
                    keyStore.setKeyEntry(KeyStoreTest.ALIAS_UNICODE_SECRET, KeyStoreTest.getSecretKey(), KeyStoreTest.PASSWORD_KEY, null);
                    TestCase.fail(keyStore.getType());
                } catch (KeyStoreException expected) {
                }
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
            } else
                if (KeyStoreTest.isCaseSensitive(keyStore)) {
                    if (KeyStoreTest.isKeyPasswordSupported(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getPrivateKey2(), KeyStoreTest.PARAM_KEY);
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isNullPasswordAllowed(keyStore)) {
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                        keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, KeyStoreTest.getPrivateKey2(), null);
                        KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, null));
                        KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_NO_PASSWORD_PRIVATE, null));
                    }
                    if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        TestCase.assertNull(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                        keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey2()), KeyStoreTest.PARAM_KEY);
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                        KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                        TestCase.assertNull(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                        keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey2().getCertificate()), null);
                        KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                        KeyStoreTest.assertCertificate2(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                        keyStore.setEntry(KeyStoreTest.ALIAS_UNICODE_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), null);
                        KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_UNICODE_CERTIFICATE));
                    }
                } else {
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.getPrivateKey2(), KeyStoreTest.PARAM_KEY);
                    KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    KeyStoreTest.assertPrivateKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStoreTest.PASSWORD_KEY));
                    if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                        keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey2()), KeyStoreTest.PARAM_KEY);
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, KeyStoreTest.PASSWORD_KEY));
                        KeyStoreTest.assertSecretKey2(keyStore.getKey(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStoreTest.PASSWORD_KEY));
                    }
                    if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                        KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                        KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                        keyStore.setEntry(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey2().getCertificate()), null);
                        KeyStoreTest.assertCertificate2(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                        KeyStoreTest.assertCertificate2(keyStore.getCertificate(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE));
                        keyStore.setEntry(KeyStoreTest.ALIAS_UNICODE_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), null);
                        KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_UNICODE_CERTIFICATE));
                    }
                }

        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            // test with null/non-null passwords
            if (KeyStoreTest.isReadOnly(keyStore)) {
                try {
                    keyStore.setEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey(), null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                try {
                    keyStore.setEntry(KeyStoreTest.ALIAS_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey()), null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                try {
                    keyStore.setEntry(KeyStoreTest.ALIAS_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), null);
                    TestCase.fail(keyStore.getType());
                } catch (UnsupportedOperationException expected) {
                }
                continue;
            }
            if ((KeyStoreTest.isNullPasswordAllowed(keyStore)) || (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                keyStore.setEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey(), null);
                KeyStoreTest.assertPrivateKey(keyStore.getKey(KeyStoreTest.ALIAS_PRIVATE, null));
            } else {
                try {
                    keyStore.setEntry(KeyStoreTest.ALIAS_PRIVATE, KeyStoreTest.getPrivateKey(), null);
                    TestCase.fail(keyStore.getType());
                } catch (Exception e) {
                    if ((((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) && ((e.getClass()) != (KeyStoreException.class))) {
                        throw e;
                    }
                }
            }
            if (KeyStoreTest.isSecretKeyEnabled(keyStore)) {
                if ((KeyStoreTest.isNullPasswordAllowed(keyStore)) || (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                    keyStore.setEntry(KeyStoreTest.ALIAS_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey()), null);
                    KeyStoreTest.assertSecretKey(keyStore.getKey(KeyStoreTest.ALIAS_SECRET, null));
                } else {
                    try {
                        keyStore.setEntry(KeyStoreTest.ALIAS_SECRET, new KeyStore.SecretKeyEntry(KeyStoreTest.getSecretKey()), null);
                        TestCase.fail(keyStore.getType());
                    } catch (Exception e) {
                        if ((((e.getClass()) != (UnrecoverableKeyException.class)) && ((e.getClass()) != (IllegalArgumentException.class))) && ((e.getClass()) != (KeyStoreException.class))) {
                            throw e;
                        }
                    }
                }
            }
            if (KeyStoreTest.isCertificateEnabled(keyStore)) {
                if ((KeyStoreTest.isNullPasswordAllowed(keyStore)) || (KeyStoreTest.isKeyPasswordIgnored(keyStore))) {
                    keyStore.setEntry(KeyStoreTest.ALIAS_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), KeyStoreTest.PARAM_KEY);
                    KeyStoreTest.assertCertificate(keyStore.getCertificate(KeyStoreTest.ALIAS_CERTIFICATE));
                } else {
                    try {
                        keyStore.setEntry(KeyStoreTest.ALIAS_CERTIFICATE, new KeyStore.TrustedCertificateEntry(KeyStoreTest.getPrivateKey().getCertificate()), KeyStoreTest.PARAM_KEY);
                        TestCase.fail(keyStore.getType());
                    } catch (KeyStoreException expected) {
                    }
                }
            }
        }
    }

    public void test_KeyStore_entryInstanceOf() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                keyStore.entryInstanceOf(null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                keyStore.entryInstanceOf(null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            try {
                keyStore.entryInstanceOf(null, KeyStore.Entry.class);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            try {
                keyStore.entryInstanceOf("", null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertFalse(keyStore.entryInstanceOf("", KeyStore.Entry.class));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            // test odd inputs
            TestCase.assertFalse(keyStore.entryInstanceOf("", KeyStore.Entry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf("", KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf("", KeyStore.SecretKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf("", KeyStore.TrustedCertificateEntry.class));
            if (KeyStoreTest.isReadOnly(keyStore)) {
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_PRIVATE, KeyStore.PrivateKeyEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_PRIVATE, KeyStore.SecretKeyEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_PRIVATE, KeyStore.TrustedCertificateEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_SECRET, KeyStore.SecretKeyEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_SECRET, KeyStore.PrivateKeyEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_SECRET, KeyStore.TrustedCertificateEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_CERTIFICATE, KeyStore.TrustedCertificateEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_CERTIFICATE, KeyStore.PrivateKeyEntry.class));
                TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_CERTIFICATE, KeyStore.SecretKeyEntry.class));
                continue;
            }
            // test case sensitive
            TestCase.assertEquals(KeyStoreTest.isKeyPasswordSupported(keyStore), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_PRIVATE, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_PRIVATE, KeyStore.SecretKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_PRIVATE, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertEquals(KeyStoreTest.isNullPasswordAllowed(keyStore), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, KeyStore.SecretKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_NO_PASSWORD_PRIVATE, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertEquals(KeyStoreTest.isSecretKeyEnabled(keyStore), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_SECRET, KeyStore.SecretKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_SECRET, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_SECRET, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertEquals(KeyStoreTest.isCertificateEnabled(keyStore), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_CERTIFICATE, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_CERTIFICATE, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_CERTIFICATE, KeyStore.SecretKeyEntry.class));
            // test case insensitive
            TestCase.assertEquals((!(KeyStoreTest.isCaseSensitive(keyStore))), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStore.SecretKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_PRIVATE, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertEquals(((!(KeyStoreTest.isCaseSensitive(keyStore))) && (KeyStoreTest.isSecretKeyEnabled(keyStore))), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStore.SecretKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_SECRET, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertEquals(((!(KeyStoreTest.isCaseSensitive(keyStore))) && (KeyStoreTest.isCertificateEnabled(keyStore))), keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, KeyStore.TrustedCertificateEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, KeyStore.PrivateKeyEntry.class));
            TestCase.assertFalse(keyStore.entryInstanceOf(KeyStoreTest.ALIAS_ALT_CASE_CERTIFICATE, KeyStore.SecretKeyEntry.class));
        }
    }

    public void test_KeyStore_Builder() throws Exception {
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            try {
                KeyStore.Builder.newInstance(keyStore, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                newInstance(keyStore.getType(), keyStore.getProvider(), null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            try {
                KeyStore.Builder.newInstance(null, null, null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            try {
                KeyStore.Builder.newInstance(keyStore.getType(), keyStore.getProvider(), null, null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            keyStore.load(null, null);
            KeyStore.Builder builder = KeyStore.Builder.newInstance(keyStore, KeyStoreTest.PARAM_STORE);
            try {
                builder.getProtectionParameter(null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertEquals(keyStore, builder.getKeyStore());
            try {
                builder.getProtectionParameter(null);
                TestCase.fail(keyStore.getType());
            } catch (NullPointerException expected) {
            }
            TestCase.assertEquals(KeyStoreTest.PARAM_STORE, builder.getProtectionParameter(""));
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            KeyStoreTest.populate(keyStore);
            File file = File.createTempFile("keystore", keyStore.getProvider().getName());
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                if ((KeyStoreTest.isLoadStoreUnsupported(keyStore)) || (KeyStoreTest.isReadOnly(keyStore))) {
                    try {
                        keyStore.store(os, KeyStoreTest.PASSWORD_STORE);
                        TestCase.fail(keyStore.getType());
                    } catch (UnsupportedOperationException expected) {
                    }
                    continue;
                }
                keyStore.store(os, KeyStoreTest.PASSWORD_STORE);
                os.close();
                KeyStore.Builder builder = KeyStore.Builder.newInstance(keyStore.getType(), keyStore.getProvider(), file, KeyStoreTest.PARAM_STORE);
                TestCase.assertEquals(keyStore.getType(), builder.getKeyStore().getType());
                TestCase.assertEquals(keyStore.getProvider(), builder.getKeyStore().getProvider());
                TestCase.assertEquals(KeyStoreTest.PARAM_STORE, builder.getProtectionParameter(""));
                assertEqualsKeyStores(file, KeyStoreTest.PASSWORD_STORE, keyStore);
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException ignored) {
                }
                file.delete();
            }
        }
        for (KeyStore keyStore : KeyStoreTest.keyStores()) {
            if (KeyStoreTest.isLoadStoreUnsupported(keyStore)) {
                continue;
            }
            KeyStore.Builder builder = newInstance(keyStore.getType(), keyStore.getProvider(), KeyStoreTest.PARAM_STORE);
            TestCase.assertEquals(keyStore.getType(), builder.getKeyStore().getType());
            TestCase.assertEquals(keyStore.getProvider(), builder.getKeyStore().getProvider());
            TestCase.assertEquals(KeyStoreTest.PARAM_STORE, builder.getProtectionParameter(""));
        }
    }

    public void test_KeyStore_cacerts() throws Exception {
        if (IS_RI) {
            return;
        }
        KeyStore ks = KeyStore.getInstance("AndroidCAStore");
        TestCase.assertEquals("AndroidCAStore", ks.getType());
        TestCase.assertEquals("HarmonyJSSE", ks.getProvider().getName());
        ks.load(null, null);
        for (String alias : Collections.list(ks.aliases())) {
            Certificate c = null;
            try {
                c = ks.getCertificate(alias);
                TestCase.assertNotNull(c);
                TestCase.assertTrue(ks.isCertificateEntry(alias));
                TestCase.assertTrue(ks.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class));
                TestCase.assertEquals(alias, ks.getCertificateAlias(c));
                TestCase.assertTrue((c instanceof X509Certificate));
                X509Certificate cert = ((X509Certificate) (c));
                TestCase.assertEquals(cert.getSubjectUniqueID(), cert.getIssuerUniqueID());
                TestCase.assertNotNull(cert.getPublicKey());
                TestCase.assertTrue(ks.containsAlias(alias));
                TestCase.assertNotNull(ks.getCreationDate(alias));
                TestCase.assertNotNull(ks.getEntry(alias, null));
                TestCase.assertFalse(ks.isKeyEntry(alias));
                TestCase.assertNull(ks.getKey(alias, null));
                TestCase.assertNull(ks.getCertificateChain(alias));
            } catch (Throwable t) {
                throw new Exception(((("alias=" + alias) + " cert=") + c), t);
            }
        }
    }

    // http://b/857840: want JKS key store
    public void testDefaultKeystore() {
        String type = KeyStore.getDefaultType();
        TestCase.assertEquals(KEY_STORE_ALGORITHM, type);
        try {
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            TestCase.assertNotNull("Keystore must not be null", store);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            KeyStore store = KeyStore.getInstance(KEY_STORE_ALGORITHM);
            TestCase.assertNotNull("Keystore must not be null", store);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

