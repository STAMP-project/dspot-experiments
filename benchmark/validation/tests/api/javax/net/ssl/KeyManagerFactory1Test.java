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
package tests.api.javax.net.ssl;


import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.KeyStoreBuilderParameters;
import javax.net.ssl.ManagerFactoryParameters;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.xnet.tests.support.MyKeyManagerFactorySpi;

import static java.security.KeyStore.Builder.newInstance;


/**
 * Tests for <code>KeyManagerFactory</code> class constructors and methods.
 */
public class KeyManagerFactory1Test extends TestCase {
    private static final String srvKeyManagerFactory = "KeyManagerFactory";

    private static String defaultAlgorithm = null;

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static boolean DEFSupported = false;

    private static final String NotSupportedMsg = "There is no suitable provider for KeyManagerFactory";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String[] validValues = new String[3];

    static {
        KeyManagerFactory1Test.defaultAlgorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if ((KeyManagerFactory1Test.defaultAlgorithm) != null) {
            KeyManagerFactory1Test.defaultProvider = SpiEngUtils.isSupport(KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory1Test.srvKeyManagerFactory);
            KeyManagerFactory1Test.DEFSupported = (KeyManagerFactory1Test.defaultProvider) != null;
            KeyManagerFactory1Test.defaultProviderName = (KeyManagerFactory1Test.DEFSupported) ? KeyManagerFactory1Test.defaultProvider.getName() : null;
            KeyManagerFactory1Test.validValues[0] = KeyManagerFactory1Test.defaultAlgorithm;
            KeyManagerFactory1Test.validValues[1] = KeyManagerFactory1Test.defaultAlgorithm.toUpperCase();
            KeyManagerFactory1Test.validValues[2] = KeyManagerFactory1Test.defaultAlgorithm.toLowerCase();
        }
    }

    /**
     * avax.net.ssl.KeyManagerFactory#getAlgorithm()
     */
    public void test_getAlgorithm() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyManagerFactory1Test.DEFSupported))
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);

        TestCase.assertEquals("Incorrect algorithm", KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm).getAlgorithm());
        TestCase.assertEquals("Incorrect algorithm", KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory1Test.defaultProviderName).getAlgorithm());
        TestCase.assertEquals("Incorrect algorithm", KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory1Test.defaultProvider).getAlgorithm());
    }

    /**
     * Test for <code>getDefaultAlgorithm()</code> method
     * Assertion: returns value which is specifoed in security property
     */
    public void test_getDefaultAlgorithm() {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        String def = KeyManagerFactory.getDefaultAlgorithm();
        if ((KeyManagerFactory1Test.defaultAlgorithm) == null) {
            TestCase.assertNull("DefaultAlgorithm must be null", def);
        } else {
            TestCase.assertEquals("Invalid default algorithm", def, KeyManagerFactory1Test.defaultAlgorithm);
        }
        String defA = "Proba.keymanagerfactory.defaul.type";
        Security.setProperty("ssl.KeyManagerFactory.algorithm", defA);
        TestCase.assertEquals("Incorrect defaultAlgorithm", KeyManagerFactory.getDefaultAlgorithm(), defA);
        if (def == null) {
            def = "";
        }
        Security.setProperty("ssl.KeyManagerFactory.algorithm", def);
        TestCase.assertEquals("Incorrect defaultAlgorithm", KeyManagerFactory.getDefaultAlgorithm(), def);
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertions:
     * returns security property "ssl.KeyManagerFactory.algorithm";
     * returns instance of KeyManagerFactory
     */
    public void test_getInstanceLjava_lang_String01() throws NoSuchAlgorithmException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        KeyManagerFactory keyMF;
        for (int i = 0; i < (KeyManagerFactory1Test.validValues.length); i++) {
            keyMF = KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i]);
            TestCase.assertNotNull("No KeyManagerFactory created", keyMF);
            TestCase.assertEquals("Invalid algorithm", keyMF.getAlgorithm(), KeyManagerFactory1Test.validValues[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     */
    public void test_getInstanceLjava_lang_String02() {
        try {
            KeyManagerFactory.getInstance(null);
            TestCase.fail("NoSuchAlgorithmException or NullPointerException should be thrown (algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (KeyManagerFactory1Test.invalidValues.length); i++) {
            try {
                KeyManagerFactory.getInstance(KeyManagerFactory1Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException was not thrown as expected for algorithm: ".concat(KeyManagerFactory1Test.invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null or empty
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String01() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (KeyManagerFactory1Test.validValues.length); i++) {
            try {
                KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i], provider);
                TestCase.fail("Expected IllegalArgumentException was not thrown for null provider");
            } catch (IllegalArgumentException e) {
            }
            try {
                KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i], "");
                TestCase.fail("Expected IllegalArgumentException was not thrown for empty provider");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String02() throws NoSuchProviderException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        try {
            KeyManagerFactory.getInstance(null, KeyManagerFactory1Test.defaultProviderName);
            TestCase.fail("NoSuchAlgorithmException or NullPointerException should be thrown (algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (KeyManagerFactory1Test.invalidValues.length); i++) {
            try {
                KeyManagerFactory.getInstance(KeyManagerFactory1Test.invalidValues[i], KeyManagerFactory1Test.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(KeyManagerFactory1Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws NoSuchProviderException when provider has
     * invalid value
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String03() throws NoSuchAlgorithmException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        for (int i = 0; i < (KeyManagerFactory1Test.validValues.length); i++) {
            for (int j = 1; j < (KeyManagerFactory1Test.invalidValues.length); j++) {
                try {
                    KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i], KeyManagerFactory1Test.invalidValues[j]);
                    TestCase.fail((((("NuSuchProviderException must be thrown (algorithm: " + (KeyManagerFactory1Test.validValues[i])) + " provider: ") + (KeyManagerFactory1Test.invalidValues[j])) + ")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method Assertion: returns instance of KeyManagerFactory
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String04() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        KeyManagerFactory kMF;
        for (int i = 0; i < (KeyManagerFactory1Test.validValues.length); i++) {
            kMF = KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i], KeyManagerFactory1Test.defaultProviderName);
            TestCase.assertNotNull("No KeyManagerFactory created", kMF);
            TestCase.assertEquals("Incorrect algorithm", kMF.getAlgorithm(), KeyManagerFactory1Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", kMF.getProvider().getName(), KeyManagerFactory1Test.defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider01() throws NoSuchAlgorithmException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (KeyManagerFactory1Test.validValues.length); i++) {
            try {
                KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i], provider);
                TestCase.fail("Expected IllegalArgumentException was not thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider02() {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        try {
            KeyManagerFactory.getInstance(null, KeyManagerFactory1Test.defaultProvider);
            TestCase.fail("NoSuchAlgorithmException or NullPointerException should be thrown (algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (KeyManagerFactory1Test.invalidValues.length); i++) {
            try {
                KeyManagerFactory.getInstance(KeyManagerFactory1Test.invalidValues[i], KeyManagerFactory1Test.defaultProvider);
                TestCase.fail("Expected NuSuchAlgorithmException was not thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: returns instance of KeyManagerFactory
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider03() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        KeyManagerFactory kMF;
        for (int i = 0; i < (KeyManagerFactory1Test.validValues.length); i++) {
            kMF = KeyManagerFactory.getInstance(KeyManagerFactory1Test.validValues[i], KeyManagerFactory1Test.defaultProvider);
            TestCase.assertNotNull("No KeyManagerFactory created", kMF);
            TestCase.assertEquals(kMF.getAlgorithm(), KeyManagerFactory1Test.validValues[i]);
            TestCase.assertEquals(kMF.getProvider(), KeyManagerFactory1Test.defaultProvider);
        }
    }

    /**
     * Test for <code>KeyManagerFactory</code> constructor
     * Assertion: returns KeyManagerFactory object
     */
    public void test_Constructor() throws NoSuchAlgorithmException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        KeyManagerFactorySpi spi = new MyKeyManagerFactorySpi();
        KeyManagerFactory keyMF = new myKeyManagerFactory(spi, KeyManagerFactory1Test.defaultProvider, KeyManagerFactory1Test.defaultAlgorithm);
        TestCase.assertEquals("Incorrect algorithm", keyMF.getAlgorithm(), KeyManagerFactory1Test.defaultAlgorithm);
        TestCase.assertEquals("Incorrect provider", keyMF.getProvider(), KeyManagerFactory1Test.defaultProvider);
        try {
            keyMF.init(null, new char[1]);
            TestCase.fail("UnrecoverableKeyException must be thrown");
        } catch (UnrecoverableKeyException e) {
        } catch (Exception e) {
            TestCase.fail((("Unexpected: " + (e.toString())) + " was thrown"));
        }
        keyMF = new myKeyManagerFactory(null, null, null);
        TestCase.assertNull("Aalgorithm must be null", keyMF.getAlgorithm());
        TestCase.assertNull("Provider must be null", keyMF.getProvider());
        try {
            keyMF.getKeyManagers();
        } catch (NullPointerException e) {
        }
    }

    /**
     * avax.net.ssl.KeyManagerFactory#getKeyManagers()
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws KeyStoreException
     * 		
     * @throws IOException
     * 		
     * @throws CertificateException
     * 		
     * @throws UnrecoverableKeyException
     * 		
     */
    public void test_getKeyManagers() throws Exception {
        if (!(KeyManagerFactory1Test.DEFSupported))
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm);
        char[] pass = "password".toCharArray();
        kmf.init(null, pass);
        TestCase.assertNotNull("Key manager array is null", kmf.getKeyManagers());
        TestCase.assertEquals("Incorrect size of array", 1, kmf.getKeyManagers().length);
    }

    /**
     * avax.net.ssl.KeyManagerFactory#getProvider()
     */
    public void test_getProvider() throws Exception {
        if (!(KeyManagerFactory1Test.DEFSupported))
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);

        TestCase.assertEquals("Incorrect provider", KeyManagerFactory1Test.defaultProvider, KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm).getProvider());
        TestCase.assertEquals("Incorrect provider", KeyManagerFactory1Test.defaultProvider, KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory1Test.defaultProviderName).getProvider());
        TestCase.assertEquals("Incorrect provider", KeyManagerFactory1Test.defaultProvider, KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm, KeyManagerFactory1Test.defaultProvider).getProvider());
    }

    /**
     * Test for <code>init(KeyStore keyStore, char[] password)</code> and
     * <code>getKeyManagers()</code>
     * Assertion: returns not empty KeyManager array
     */
    public void test_initLjava_security_KeyStore$C() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        KeyManagerFactory[] keyMF = createKMFac();
        TestCase.assertNotNull("KeyManagerFactory object were not created", keyMF);
        KeyStore ksNull = null;
        KeyManager[] km;
        for (int i = 0; i < (keyMF.length); i++) {
            keyMF[i].init(ksNull, new char[10]);
            km = keyMF[i].getKeyManagers();
            TestCase.assertNotNull("Result should not be null", km);
            TestCase.assertTrue("Length of result KeyManager array should not be 0", ((km.length) > 0));
        }
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
        } catch (KeyStoreException e) {
            TestCase.fail(((e.toString()) + "default KeyStore type is not supported"));
            return;
        } catch (Exception e) {
            TestCase.fail(("Unexpected: " + (e.toString())));
            return;
        }
        for (int i = 0; i < (keyMF.length); i++) {
            try {
                keyMF[i].init(ks, new char[10]);
            } catch (KeyStoreException e) {
            }
            km = keyMF[i].getKeyManagers();
            TestCase.assertNotNull("Result has not be null", km);
            TestCase.assertTrue("Length of result KeyManager array should not be 0", ((km.length) > 0));
        }
    }

    /**
     * Test for <code>init(ManagerFactoryParameters params)</code>
     * Assertion:
     * throws InvalidAlgorithmParameterException when params is null
     */
    public void test_initLjavax_net_ssl_ManagerFactoryParameters() throws NoSuchAlgorithmException {
        if (!(KeyManagerFactory1Test.DEFSupported)) {
            TestCase.fail(KeyManagerFactory1Test.NotSupportedMsg);
            return;
        }
        ManagerFactoryParameters par = null;
        KeyManagerFactory[] keyMF = createKMFac();
        TestCase.assertNotNull("KeyManagerFactory object were not created", keyMF);
        for (int i = 0; i < (keyMF.length); i++) {
            try {
                keyMF[i].init(par);
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
        }
        KeyStore.ProtectionParameter pp = new ProtectionParameterImpl();
        KeyStore.Builder bld = newInstance("testType", null, pp);
        TestCase.assertNotNull("Null object KeyStore.Builder", bld);
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory1Test.defaultAlgorithm);
            KeyStoreBuilderParameters ksp = new KeyStoreBuilderParameters(bld);
            TestCase.assertNotNull(ksp.getParameters());
            kmf.init(ksp);
            TestCase.fail("InvalidAlgorithmParameterException must be thrown");
        } catch (InvalidAlgorithmParameterException e) {
        }
    }
}

