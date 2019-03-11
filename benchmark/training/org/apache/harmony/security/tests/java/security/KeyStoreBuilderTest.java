/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.KeyStoreTestSupport;
import org.apache.harmony.security.tests.support.tmpCallbackHandler;

import static java.security.KeyStore.Builder.newInstance;


public class KeyStoreBuilderTest extends TestCase {
    private static char[] pass = new char[]{ 's', 't', 'o', 'r', 'e', 'p', 'w', 'd' };

    private KeyStore.PasswordProtection protPass = new KeyStore.PasswordProtection(KeyStoreBuilderTest.pass);

    private tmpCallbackHandler tmpCall = new tmpCallbackHandler();

    private KeyStore.CallbackHandlerProtection callbackHand = new KeyStore.CallbackHandlerProtection(tmpCall);

    private KeyStoreBuilderTest.MyProtectionParameter myProtParam = new KeyStoreBuilderTest.MyProtectionParameter(new byte[5]);

    public static String[] validValues = KeyStoreTestSupport.validValues;

    private static String defaultType = KeyStoreTestSupport.defaultType;

    private static Provider defaultProvider = null;

    static {
        KeyStoreBuilderTest.defaultProvider = Security.getProviders(("KeyStore." + (KeyStore.getDefaultType())))[0];
    }

    /* test for constructor KeyStoreBuilder */
    public void testConstructor() {
        KeyStoreBuilderTest.KeyStoreBuilder ksb;
        try {
            ksb = new KeyStoreBuilderTest.KeyStoreBuilder();
            TestCase.assertNotNull(ksb);
            ksb.getKeyStore();
            ksb.getProtectionParameter("test");
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.getMessage())));
        }
    }

    /* test for method newInstance(KeyStore, KeyStore.ProtectionParameter) */
    public void testNewInstanceKeyStoreProtectionParameter() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        // exceptions verification
        try {
            KeyStore.Builder.newInstance(null, null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            KeyStore.Builder.newInstance(null, protPass);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
            // expected
        }
        KeyStore.Builder ksB;
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            KeyStore.Builder.newInstance(ks, null);
            TestCase.fail("NullPointerException must be thrown when ProtectionParameter is null");
        } catch (NullPointerException e) {
            // expected
        }
        KeyStore.PasswordProtection protPass1 = new KeyStore.PasswordProtection(KeyStoreBuilderTest.pass);
        KeyStore.ProtectionParameter[] pp = new KeyStore.ProtectionParameter[]{ protPass, protPass1, callbackHand, myProtParam };
        for (int i = 0; i < (pp.length); i++) {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            try {
                KeyStore.Builder.newInstance(ks, pp[i]);
                TestCase.fail("IllegalArgumentException must be thrown because KeyStore was not initialized");
            } catch (IllegalArgumentException e) {
                // expected
            }
            ks.load(null, KeyStoreBuilderTest.pass);
            ksB = KeyStore.Builder.newInstance(ks, pp[i]);
            TestCase.assertEquals("Incorrect KeyStore", ksB.getKeyStore().size(), 0);
            ksB = KeyStore.Builder.newInstance(ks, pp[i]);
            // verification getKeyStore() and getProtectionParameter(String
            // alias)
            TestCase.assertEquals("Incorrect KeyStore", ks, ksB.getKeyStore());
            try {
                ksB.getProtectionParameter(null);
                TestCase.fail("NullPointerException must be thrown");
            } catch (NullPointerException e) {
            }
            try {
                TestCase.assertEquals(ksB.getProtectionParameter("aaa"), pp[i]);
            } catch (KeyStoreException e) {
                TestCase.fail((("Unexpected: " + (e.toString())) + " was thrown"));
            }
            try {
                TestCase.assertEquals(ksB.getProtectionParameter("Bad alias"), pp[i]);
            } catch (KeyStoreException e) {
                // KeyStoreException might be thrown because there is no entry
                // with such alias
            }
            try {
                TestCase.assertEquals(ksB.getProtectionParameter(""), pp[i]);
            } catch (KeyStoreException e) {
                // KeyStoreException might be thrown because there is no entry
                // with such alias
            }
            KeyStore.ProtectionParameter pPar = ksB.getProtectionParameter("aaa");
            switch (i) {
                case 0 :
                    TestCase.assertTrue((pPar instanceof KeyStore.PasswordProtection));
                    break;
                case 1 :
                    TestCase.assertTrue((pPar instanceof KeyStore.PasswordProtection));
                    break;
                case 2 :
                    TestCase.assertTrue((pPar instanceof KeyStore.CallbackHandlerProtection));
                    break;
                case 3 :
                    TestCase.assertTrue((pPar instanceof KeyStoreBuilderTest.MyProtectionParameter));
                    break;
                default :
                    TestCase.fail("Incorrect protection parameter");
            }
            TestCase.assertEquals(pPar, pp[i]);
        }
    }

    /* Test for methods: <code>newInstance(String type, Provider provider, File
    file, ProtectionParameter protectionParameter)</code> <code>getKeyStore()</code>
    <code>getProtectionParameter(String alias)</code> Assertions: throws
    NullPointerException if type, file or protectionParameter is null; throws
    IllegalArgumentException if file does not exist or is not file; throws
    IllegalArgumentException if ProtectionParameter is not PasswordProtection
    or CallbackHandlerProtection; returns new object

    getKeyStore() returns specified keystore; getProtectionParameter(String
    alias) throws NullPointerException when alias is null; throws
    KeyStoreException when alias is not available; returns
    ProtectionParameter which is used in newInstance(...)
     */
    public void testNewInstanceStringProviderFileProtectionParameter() throws Exception {
        File fl = File.createTempFile("KSBuilder_ImplTest", "keystore");
        fl.deleteOnExit();
        KeyStore.Builder ksB;
        KeyStore.Builder ksB1;
        KeyStore ks = null;
        KeyStore ks1 = null;
        KeyStoreBuilderTest.MyProtectionParameter myPP = new KeyStoreBuilderTest.MyProtectionParameter(new byte[5]);
        // check exceptions
        try {
            KeyStore.Builder.newInstance(null, KeyStoreBuilderTest.defaultProvider, fl, protPass);
            TestCase.fail("NullPointerException must be thrown when type is null");
        } catch (NullPointerException e) {
        }
        try {
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, null, protPass);
            TestCase.fail("NullPointerException must be thrown when file is null");
        } catch (NullPointerException e) {
        }
        try {
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl, null);
            TestCase.fail("NullPointerException must be thrown when ProtectionParameter is null");
        } catch (NullPointerException e) {
        }
        try {
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl, myPP);
            TestCase.fail("IllegalArgumentException must be thrown when ProtectionParameter is not correct");
        } catch (IllegalArgumentException e) {
        }
        try {
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, new File(fl.getAbsolutePath().concat("should_absent")), protPass);
            TestCase.fail("IllegalArgumentException must be thrown when file does not exist");
        } catch (IllegalArgumentException e) {
        }
        try {
            // 'file' param points to directory
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl.getParentFile(), protPass);
            TestCase.fail("IllegalArgumentException must be thrown when file does not exist");
        } catch (IllegalArgumentException e) {
        }
        ksB = KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl, protPass);
        try {
            ksB.getKeyStore();
            TestCase.fail("KeyStoreException must be throw because file is empty");
        } catch (KeyStoreException e) {
        }
        fl = createKS();
        // Exception Tests with custom ProtectionParameter
        try {
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), null, fl, myPP);
            TestCase.fail(("IllegalArgumentException must be " + "thrown for incorrect ProtectionParameter"));
        } catch (IllegalArgumentException e) {
        }
        try {
            KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl, myPP);
            TestCase.fail(("IllegalArgumentException must be " + "thrown for incorrect ProtectionParameter"));
        } catch (IllegalArgumentException e) {
        }
        // Tests with PasswordProtection
        ksB = KeyStore.Builder.newInstance(KeyStore.getDefaultType(), null, fl, protPass);
        ksB1 = KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl, protPass);
        try {
            ks = ksB.getKeyStore();
        } catch (KeyStoreException e) {
            TestCase.fail("Unexpected KeyException was thrown");
        }
        try {
            ks1 = ksB1.getKeyStore();
        } catch (KeyStoreException e) {
            TestCase.fail(("Unexpected KeyException was thrown: " + (e.getMessage())));
        }
        TestCase.assertEquals("Incorrect KeyStore size", ks.size(), ks1.size());
        for (Enumeration<String> aliases = ks.aliases(); aliases.hasMoreElements();) {
            String aName = aliases.nextElement();
            try {
                TestCase.assertEquals("Incorrect ProtectionParameter", ksB.getProtectionParameter(aName), protPass);
            } catch (Exception e) {
                TestCase.fail(((("Unexpected: " + (e.toString())) + " was thrown for alias: ") + aName));
            }
        }
        ksB.getKeyStore();
        try {
            TestCase.assertEquals(ksB.getProtectionParameter("Bad alias"), protPass);
        } catch (KeyStoreException e) {
            // KeyStoreException might be thrown because there is no entry
            // with such alias
        }
        for (Enumeration<String> aliases = ks1.aliases(); aliases.hasMoreElements();) {
            String aName = aliases.nextElement();
            TestCase.assertEquals("Incorrect ProtectionParameter", ksB1.getProtectionParameter(aName), protPass);
        }
        try {
            TestCase.assertEquals(ksB1.getProtectionParameter("Bad alias"), protPass);
        } catch (KeyStoreException e) {
            // KeyStoreException might be thrown because there is no entry
            // with such alias
        }
        // Tests with CallbackHandlerProtection
        ksB = KeyStore.Builder.newInstance(KeyStore.getDefaultType(), null, fl, callbackHand);
        ksB1 = KeyStore.Builder.newInstance(KeyStore.getDefaultType(), KeyStoreBuilderTest.defaultProvider, fl, callbackHand);
        try {
            ks = ksB.getKeyStore();
            TestCase.fail(("KeyStoreException must be thrown for incorrect " + "ProtectionParameter"));
        } catch (KeyStoreException e) {
        }
        try {
            ks1 = ksB1.getKeyStore();
            TestCase.fail(("KeyStoreException must be thrown for incorrect " + "ProtectionParameter"));
        } catch (KeyStoreException e) {
        }
        TestCase.assertEquals("Incorrect KeyStore size", ks.size(), ks1.size());
        for (Enumeration<String> aliases = ks.aliases(); aliases.hasMoreElements();) {
            String aName = aliases.nextElement();
            try {
                TestCase.assertEquals("Incorrect ProtectionParameter", ksB.getProtectionParameter(aName), callbackHand);
            } catch (Exception e) {
                TestCase.fail(((("Unexpected: " + (e.toString())) + " was thrown for alias: ") + aName));
            }
        }
        for (Enumeration<String> iter = ks1.aliases(); iter.hasMoreElements();) {
            String aName = iter.nextElement();
            TestCase.assertEquals("Incorrect ProtectionParameter", ksB1.getProtectionParameter(aName), callbackHand);
        }
    }

    /* Test for method: <code>newInstance(String type, Provider provider,
    ProtectionParameter protectionParameter)</code> <code>getKeyStore()</code>
    <code>getProtectionParameter(String alias)</code> Assertions: throws
    NullPointerException if type, or protectionParameter is null; returns new
    object

    getKeyStore() returns empty keystore getProtectionParameter(String alias)
    throws NullPointerException when alias is null; throws KeyStoreException
    when alias is not available
     */
    public void testNewInstanceStringProviderProtectionParameter() throws KeyStoreException {
        try {
            newInstance(null, KeyStoreBuilderTest.defaultProvider, protPass);
            TestCase.fail("NullPointerException must be thrown when type is null");
        } catch (NullPointerException e) {
        }
        try {
            newInstance(KeyStoreBuilderTest.defaultType, KeyStoreBuilderTest.defaultProvider, null);
            TestCase.fail("NullPointerException must be thrown when ProtectionParameter is null");
        } catch (NullPointerException e) {
        }
        KeyStoreBuilderTest.MyProtectionParameter myPP = new KeyStoreBuilderTest.MyProtectionParameter(new byte[5]);
        KeyStore.ProtectionParameter[] pp = new KeyStore.ProtectionParameter[]{ protPass, myPP, callbackHand };
        KeyStore.Builder ksB;
        KeyStore.Builder ksB1;
        KeyStore ks = null;
        for (int i = 0; i < (pp.length); i++) {
            ksB = newInstance(KeyStoreBuilderTest.defaultType, KeyStoreBuilderTest.defaultProvider, pp[i]);
            ksB1 = newInstance(KeyStoreBuilderTest.defaultType, null, pp[i]);
            switch (i) {
                case 0 :
                    try {
                        ks = ksB.getKeyStore();
                        TestCase.assertNotNull("KeyStore is null", ks);
                        try {
                            TestCase.assertEquals(ksB.getProtectionParameter("Bad alias"), pp[i]);
                        } catch (KeyStoreException e) {
                            // KeyStoreException might be thrown because there is no
                            // entry with such alias
                        }
                        ks = ksB1.getKeyStore();
                        TestCase.assertNotNull("KeyStore is null", ks);
                        try {
                            TestCase.assertEquals(ksB1.getProtectionParameter("Bad alias"), pp[i]);
                        } catch (KeyStoreException e) {
                            // KeyStoreException might be thrown because there is no
                            // entry with such alias
                        }
                    } catch (KeyStoreException e) {
                        try {
                            ks = ksB.getKeyStore();
                        } catch (KeyStoreException e1) {
                            TestCase.assertEquals("Incorrect exception", e.getMessage(), e1.getMessage());
                        }
                    }
                    break;
                case 1 :
                case 2 :
                    Exception ex1 = null;
                    Exception ex2 = null;
                    try {
                        ks = ksB.getKeyStore();
                    } catch (KeyStoreException e) {
                        ex1 = e;
                    }
                    try {
                        ks = ksB.getKeyStore();
                    } catch (KeyStoreException e) {
                        ex2 = e;
                    }
                    TestCase.assertEquals("Incorrect exception", ex1.getMessage(), ex2.getMessage());
                    try {
                        ksB.getProtectionParameter("aaa");
                        TestCase.fail("IllegalStateException must be thrown because getKeyStore() was not invoked");
                    } catch (IllegalStateException e) {
                    }
                    try {
                        ks = ksB1.getKeyStore();
                    } catch (KeyStoreException e) {
                        ex1 = e;
                    }
                    try {
                        ks = ksB1.getKeyStore();
                    } catch (KeyStoreException e) {
                        ex2 = e;
                    }
                    TestCase.assertEquals("Incorrect exception", ex1.getMessage(), ex2.getMessage());
                    try {
                        ksB1.getProtectionParameter("aaa");
                        TestCase.fail("IllegalStateException must be thrown because getKeyStore() was not invoked");
                    } catch (IllegalStateException e) {
                    }
                    break;
            }
        }
    }

    /**
     * Additional class for creating KeyStoreBuilder
     */
    class MyProtectionParameter implements KeyStore.ProtectionParameter {
        public MyProtectionParameter(byte[] param) {
            if (param == null) {
                throw new NullPointerException("param is null");
            }
        }
    }

    class KeyStoreBuilder extends KeyStore.Builder {
        public KeyStoreBuilder() {
            super();
        }

        public KeyStore getKeyStore() {
            return null;
        }

        public KeyStore.ProtectionParameter getProtectionParameter(String alias) {
            return null;
        }
    }
}

