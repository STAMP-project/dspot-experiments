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
/**
 *
 *
 * @author Vera Y. Petrashkova
 * @version $Revision$
 */
package org.apache.harmony.crypto.tests.javax.crypto;


import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MySecretKeyFactorySpi;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>SecretKeyFactory</code> class constructors and methods.
 */
public class SecretKeyFactoryTest extends TestCase {
    public static final String srvSecretKeyFactory = "SecretKeyFactory";

    private static String defaultAlgorithm1 = "DESede";

    private static String defaultAlgorithm2 = "DES";

    public static String defaultAlgorithm = null;

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    public static final String[] validValues = new String[2];

    private static boolean DEFSupported = false;

    private static final String NotSupportMsg = "Default algorithm is not supported";

    static {
        SecretKeyFactoryTest.defaultProvider = SpiEngUtils.isSupport(SecretKeyFactoryTest.defaultAlgorithm1, SecretKeyFactoryTest.srvSecretKeyFactory);
        SecretKeyFactoryTest.DEFSupported = (SecretKeyFactoryTest.defaultProvider) != null;
        if (SecretKeyFactoryTest.DEFSupported) {
            SecretKeyFactoryTest.defaultAlgorithm = SecretKeyFactoryTest.defaultAlgorithm1;
            SecretKeyFactoryTest.validValues[0] = SecretKeyFactoryTest.defaultAlgorithm.toUpperCase();
            SecretKeyFactoryTest.validValues[1] = SecretKeyFactoryTest.defaultAlgorithm.toLowerCase();
            SecretKeyFactoryTest.defaultProviderName = SecretKeyFactoryTest.defaultProvider.getName();
        } else {
            SecretKeyFactoryTest.defaultProvider = SpiEngUtils.isSupport(SecretKeyFactoryTest.defaultAlgorithm2, SecretKeyFactoryTest.srvSecretKeyFactory);
            SecretKeyFactoryTest.DEFSupported = (SecretKeyFactoryTest.defaultProvider) != null;
            if (SecretKeyFactoryTest.DEFSupported) {
                SecretKeyFactoryTest.defaultAlgorithm = SecretKeyFactoryTest.defaultAlgorithm2;
                SecretKeyFactoryTest.validValues[0] = SecretKeyFactoryTest.defaultAlgorithm.toUpperCase();
                SecretKeyFactoryTest.validValues[2] = SecretKeyFactoryTest.defaultAlgorithm.toLowerCase();
                SecretKeyFactoryTest.defaultProviderName = SecretKeyFactoryTest.defaultProvider.getName();
            } else {
                SecretKeyFactoryTest.defaultAlgorithm = null;
                SecretKeyFactoryTest.defaultProviderName = null;
                SecretKeyFactoryTest.defaultProvider = null;
            }
        }
    }

    /**
     * Test for <code>SecretKeyFactory</code> constructor
     * Assertion: returns SecretKeyFactory object
     */
    public void testSecretKeyFactory01() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        SecretKeyFactorySpi spi = new MySecretKeyFactorySpi();
        SecretKeyFactory secKF = new mySecretKeyFactory(spi, SecretKeyFactoryTest.defaultProvider, SecretKeyFactoryTest.defaultAlgorithm);
        TestCase.assertEquals("Incorrect algorithm", secKF.getAlgorithm(), SecretKeyFactoryTest.defaultAlgorithm);
        TestCase.assertEquals("Incorrect provider", secKF.getProvider(), SecretKeyFactoryTest.defaultProvider);
        TestCase.assertNull("Incorrect result", secKF.generateSecret(null));
        TestCase.assertNull("Incorrect result", secKF.getKeySpec(null, null));
        TestCase.assertNull("Incorrect result", secKF.translateKey(null));
        secKF = new mySecretKeyFactory(null, null, null);
        TestCase.assertNull("Algorithm must be null", secKF.getAlgorithm());
        TestCase.assertNull("Provider must be null", secKF.getProvider());
        try {
            secKF.translateKey(null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertions:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm has invalid value
     */
    public void testSecretKeyFactory02() throws NoSuchAlgorithmException {
        try {
            SecretKeyFactory.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (SecretKeyFactoryTest.invalidValues.length); i++) {
            try {
                SecretKeyFactory.getInstance(SecretKeyFactoryTest.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException was not thrown as expected");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns SecretKeyObject
     */
    public void testSecretKeyFactory03() throws NoSuchAlgorithmException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            SecretKeyFactory secKF = SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", secKF.getAlgorithm(), SecretKeyFactoryTest.validValues[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is invalid
     */
    public void testSecretKeyFactory04() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        try {
            SecretKeyFactory.getInstance(null, SecretKeyFactoryTest.defaultProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (SecretKeyFactoryTest.invalidValues.length); i++) {
            try {
                SecretKeyFactory.getInstance(SecretKeyFactoryTest.invalidValues[i], SecretKeyFactoryTest.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException was not thrown as expected (algorithm: ".concat(SecretKeyFactoryTest.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws IllegalArgumentException when provider is null or empty;
     * throws NoSuchProviderException when provider has invalid value
     */
    public void testSecretKeyFactory05() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        String prov = null;
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            try {
                SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i], prov);
                TestCase.fail("IllegalArgumentException was not thrown as expected (algorithm: ".concat(SecretKeyFactoryTest.validValues[i]).concat(" provider: null"));
            } catch (IllegalArgumentException e) {
            }
            try {
                SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i], "");
                TestCase.fail("IllegalArgumentException was not thrown as expected (algorithm: ".concat(SecretKeyFactoryTest.validValues[i]).concat(" provider: empty"));
            } catch (IllegalArgumentException e) {
            }
            for (int j = 1; j < (SecretKeyFactoryTest.invalidValues.length); j++) {
                try {
                    SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i], SecretKeyFactoryTest.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException was not thrown as expected (algorithm: ".concat(SecretKeyFactoryTest.validValues[i]).concat(" provider: ").concat(SecretKeyFactoryTest.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: returns SecretKeyFactory object
     */
    public void testSecretKeyFactory06() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            SecretKeyFactory secKF = SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i], SecretKeyFactoryTest.defaultProviderName);
            TestCase.assertEquals("Incorrect algorithm", secKF.getAlgorithm(), SecretKeyFactoryTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", secKF.getProvider().getName(), SecretKeyFactoryTest.defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is invalid
     */
    public void testSecretKeyFactory07() throws NoSuchAlgorithmException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        try {
            SecretKeyFactory.getInstance(null, SecretKeyFactoryTest.defaultProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (SecretKeyFactoryTest.invalidValues.length); i++) {
            try {
                SecretKeyFactory.getInstance(SecretKeyFactoryTest.invalidValues[i], SecretKeyFactoryTest.defaultProvider);
                TestCase.fail("NoSuchAlgorithmException was not thrown as expected (algorithm: ".concat(SecretKeyFactoryTest.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testSecretKeyFactory08() throws NoSuchAlgorithmException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        Provider prov = null;
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            try {
                SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i], prov);
                TestCase.fail("IllegalArgumentException was not thrown as expected (provider is null, algorithm: ".concat(SecretKeyFactoryTest.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: returns SecretKeyFactory object
     */
    public void testSecretKeyFactory09() throws NoSuchAlgorithmException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            SecretKeyFactory secKF = SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i], SecretKeyFactoryTest.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", secKF.getAlgorithm(), SecretKeyFactoryTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", secKF.getProvider(), SecretKeyFactoryTest.defaultProvider);
        }
    }

    /**
     * Test for <code>generateSecret(KeySpec keySpec)</code> and
     * <code>getKeySpec(SecretKey key, Class keySpec)
     * methods
     * Assertion:
     * throw InvalidKeySpecException if parameter is inappropriate
     */
    public void testSecretKeyFactory10() throws InvalidKeyException, InvalidKeySpecException {
        if (!(SecretKeyFactoryTest.DEFSupported)) {
            TestCase.fail(SecretKeyFactoryTest.NotSupportMsg);
            return;
        }
        byte[] bb = new byte[24];
        KeySpec ks = (SecretKeyFactoryTest.defaultAlgorithm.equals(SecretKeyFactoryTest.defaultAlgorithm2)) ? ((KeySpec) (new DESKeySpec(bb))) : ((KeySpec) (new DESedeKeySpec(bb)));
        KeySpec rks = null;
        SecretKeySpec secKeySpec = new SecretKeySpec(bb, SecretKeyFactoryTest.defaultAlgorithm);
        SecretKey secKey = null;
        SecretKeyFactory[] skF = createSKFac();
        TestCase.assertNotNull("SecretKeyFactory object were not created", skF);
        for (int i = 0; i < (skF.length); i++) {
            try {
                skF[i].generateSecret(null);
                TestCase.fail("generateSecret(null): InvalidKeySpecException must be thrown");
            } catch (InvalidKeySpecException e) {
            }
            secKey = skF[i].generateSecret(ks);
            try {
                skF[i].getKeySpec(null, null);
                TestCase.fail("getKeySpec(null,null): InvalidKeySpecException must be thrown");
            } catch (InvalidKeySpecException e) {
            }
            try {
                skF[i].getKeySpec(null, ks.getClass());
                TestCase.fail("getKeySpec(null, Class): InvalidKeySpecException must be thrown");
            } catch (InvalidKeySpecException e) {
            }
            try {
                skF[i].getKeySpec(secKey, null);
                TestCase.fail("getKeySpec(secKey, null): NullPointerException or InvalidKeySpecException must be thrown");
            } catch (InvalidKeySpecException e) {
                // Expected
            } catch (NullPointerException e) {
                // Expected
            }
            try {
                Class c;
                if (SecretKeyFactoryTest.defaultAlgorithm.equals(SecretKeyFactoryTest.defaultAlgorithm2)) {
                    c = DESedeKeySpec.class;
                } else {
                    c = DESKeySpec.class;
                }
                skF[i].getKeySpec(secKeySpec, c);
                TestCase.fail("getKeySpec(secKey, Class): InvalidKeySpecException must be thrown");
            } catch (InvalidKeySpecException e) {
            }
            rks = skF[i].getKeySpec(secKeySpec, ks.getClass());
            if (SecretKeyFactoryTest.defaultAlgorithm.equals(SecretKeyFactoryTest.defaultAlgorithm1)) {
                TestCase.assertTrue("Incorrect getKeySpec() result 1", (rks instanceof DESedeKeySpec));
            } else {
                TestCase.assertTrue("Incorrect getKeySpec() result 1", (rks instanceof DESKeySpec));
            }
            rks = skF[i].getKeySpec(secKey, ks.getClass());
            if (SecretKeyFactoryTest.defaultAlgorithm.equals(SecretKeyFactoryTest.defaultAlgorithm1)) {
                TestCase.assertTrue("Incorrect getKeySpec() result 2", (rks instanceof DESedeKeySpec));
            } else {
                TestCase.assertTrue("Incorrect getKeySpec() result 2", (rks instanceof DESKeySpec));
            }
        }
    }

    public void test_getAlgorithm() throws NoSuchAlgorithmException {
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            SecretKeyFactory secKF = SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", secKF.getAlgorithm(), SecretKeyFactoryTest.validValues[i]);
        }
        SecretKeyFactoryTest.Mock_SecretKeyFactory msf = new SecretKeyFactoryTest.Mock_SecretKeyFactory(null, null, null);
        TestCase.assertNull(msf.getAlgorithm());
    }

    class Mock_SecretKeyFactory extends SecretKeyFactory {
        protected Mock_SecretKeyFactory(SecretKeyFactorySpi arg0, Provider arg1, String arg2) {
            super(arg0, arg1, arg2);
        }
    }

    public void test_getProvider() throws NoSuchAlgorithmException {
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            SecretKeyFactory secKF = SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i]);
            TestCase.assertNotNull(secKF.getProvider());
        }
        SecretKeyFactoryTest.Mock_SecretKeyFactory msf = new SecretKeyFactoryTest.Mock_SecretKeyFactory(null, null, null);
        TestCase.assertNull(msf.getProvider());
    }

    public void test_translateKeyLjavax_crypto_SecretKey() throws InvalidKeyException, NoSuchAlgorithmException {
        KeyGenerator kg = null;
        Key key = null;
        SecretKeyFactory secKF = null;
        for (int i = 0; i < (SecretKeyFactoryTest.validValues.length); i++) {
            secKF = SecretKeyFactory.getInstance(SecretKeyFactoryTest.validValues[i]);
            TestCase.assertNotNull(secKF.getProvider());
            kg = KeyGenerator.getInstance(secKF.getAlgorithm());
            kg.init(new SecureRandom());
            key = kg.generateKey();
            secKF.translateKey(((SecretKey) (key)));
        }
        try {
            secKF.translateKey(null);
            TestCase.fail("InvalidKeyException expected");
        } catch (InvalidKeyException e) {
            // expected
        }
    }
}

