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


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGenerator;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MyKeyGeneratorSpi;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for KeyGenerator constructor and methods
 */
public class KeyGeneratorTest extends TestCase {
    public static final String srvKeyGenerator = "KeyGenerator";

    public static final String[] validAlgorithmsKeyGenerator = new String[]{ "DESede", "DES", "AES", "HmacMD5" };

    private static final int[] validKeySizes = new int[]{ 168, 56, 256, 56 };

    private static int defaultKeySize = -1;

    private static String defaultAlgorithm = null;

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static boolean DEFSupported = false;

    private static final String NotSupportMsg = "There is no suitable provider for KeyGenerator";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String[] validValues = new String[3];

    static {
        for (int i = 0; i < (KeyGeneratorTest.validAlgorithmsKeyGenerator.length); i++) {
            KeyGeneratorTest.defaultProvider = SpiEngUtils.isSupport(KeyGeneratorTest.validAlgorithmsKeyGenerator[i], KeyGeneratorTest.srvKeyGenerator);
            KeyGeneratorTest.DEFSupported = (KeyGeneratorTest.defaultProvider) != null;
            if (KeyGeneratorTest.DEFSupported) {
                KeyGeneratorTest.defaultAlgorithm = KeyGeneratorTest.validAlgorithmsKeyGenerator[i];
                KeyGeneratorTest.defaultKeySize = KeyGeneratorTest.validKeySizes[i];
                KeyGeneratorTest.defaultProviderName = KeyGeneratorTest.defaultProvider.getName();
                KeyGeneratorTest.validValues[0] = KeyGeneratorTest.defaultAlgorithm;
                KeyGeneratorTest.validValues[1] = KeyGeneratorTest.defaultAlgorithm.toUpperCase();
                KeyGeneratorTest.validValues[2] = KeyGeneratorTest.defaultAlgorithm.toLowerCase();
                break;
            }
        }
    }

    /**
     * Test for <code>KeyGenerator</code> constructor Assertion: returns
     * KeyGenerator object
     */
    public void testKeyGenerator() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        KeyGeneratorSpi spi = new MyKeyGeneratorSpi();
        KeyGenerator keyG = new myKeyGenerator(spi, KeyGeneratorTest.defaultProvider, KeyGeneratorTest.defaultAlgorithm);
        TestCase.assertEquals("Incorrect algorithm", keyG.getAlgorithm(), KeyGeneratorTest.defaultAlgorithm);
        TestCase.assertEquals("Incorrect provider", keyG.getProvider(), KeyGeneratorTest.defaultProvider);
        AlgorithmParameterSpec params = null;
        int keysize = 0;
        try {
            keyG.init(params, null);
            TestCase.fail("InvalidAlgorithmParameterException must be thrown");
        } catch (InvalidAlgorithmParameterException e) {
        }
        try {
            keyG.init(keysize, null);
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        keyG = new myKeyGenerator(null, null, null);
        TestCase.assertNull("Algorithm must be null", keyG.getAlgorithm());
        TestCase.assertNull("Provider must be null", keyG.getProvider());
        try {
            keyG.init(params, null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        try {
            keyG.init(keysize, null);
            TestCase.fail("NullPointerException or InvalidParameterException must be thrown");
        } catch (InvalidParameterException e) {
        } catch (NullPointerException e) {
        }
    }

    /* Test for <code> getInstance(String algorithm) </code> method Assertions:
    throws NullPointerException when algorithm is null throws
    NoSuchAlgorithmException when algorithm isnot available
     */
    public void testGetInstanceString01() throws NoSuchAlgorithmException {
        try {
            KeyGenerator.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (KeyGeneratorTest.invalidValues.length); i++) {
            try {
                KeyGenerator.getInstance(KeyGeneratorTest.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException should be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /* Test for <code> getInstance(String algorithm) </code> method
    Assertions: returns KeyGenerator object
     */
    public void testGetInstanceString02() throws NoSuchAlgorithmException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        KeyGenerator keyG;
        for (int i = 0; i < (KeyGeneratorTest.validValues.length); i++) {
            keyG = KeyGenerator.getInstance(KeyGeneratorTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", keyG.getAlgorithm(), KeyGeneratorTest.validValues[i]);
        }
    }

    /* Test for <code> getInstance(String algorithm, String provider)</code> method
    Assertions:
    throws NullPointerException when algorithm is null
    throws NoSuchAlgorithmException when algorithm isnot available
     */
    public void testGetInstanceStringString01() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        try {
            KeyGenerator.getInstance(null, KeyGeneratorTest.defaultProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (KeyGeneratorTest.invalidValues.length); i++) {
            try {
                KeyGenerator.getInstance(KeyGeneratorTest.invalidValues[i], KeyGeneratorTest.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /* Test for <code> getInstance(String algorithm, String provider)</code> method
    Assertions:
    throws IllegalArgumentException when provider is null or empty
    throws NoSuchProviderException when provider has not be configured
     */
    public void testGetInstanceStringString02() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (KeyGeneratorTest.validValues.length); i++) {
            try {
                KeyGenerator.getInstance(KeyGeneratorTest.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                KeyGenerator.getInstance(KeyGeneratorTest.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
            for (int j = 1; j < (KeyGeneratorTest.invalidValues.length); j++) {
                try {
                    KeyGenerator.getInstance(KeyGeneratorTest.validValues[i], KeyGeneratorTest.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(KeyGeneratorTest.validValues[i]).concat(" provider: ").concat(KeyGeneratorTest.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /* Test for <code> getInstance(String algorithm, String provider)</code> method
    Assertions: returns KeyGenerator object
     */
    public void testGetInstanceStringString03() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        KeyGenerator keyG;
        for (int i = 0; i < (KeyGeneratorTest.validValues.length); i++) {
            keyG = KeyGenerator.getInstance(KeyGeneratorTest.validValues[i], KeyGeneratorTest.defaultProviderName);
            TestCase.assertEquals("Incorrect algorithm", keyG.getAlgorithm(), KeyGeneratorTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", keyG.getProvider().getName(), KeyGeneratorTest.defaultProviderName);
        }
    }

    /* Test for <code> getInstance(String algorithm, Provider provider)</code> method
    Assertions:
    throws NullPointerException when algorithm is null
    throws NoSuchAlgorithmException when algorithm isnot available
     */
    public void testGetInstanceStringProvider01() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        try {
            KeyGenerator.getInstance(null, KeyGeneratorTest.defaultProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (KeyGeneratorTest.invalidValues.length); i++) {
            try {
                KeyGenerator.getInstance(KeyGeneratorTest.invalidValues[i], KeyGeneratorTest.defaultProvider);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /* Test for <code> getInstance(String algorithm, Provider provider)</code> method
    Assertions:
    throws IllegalArgumentException when provider is null
     */
    public void testGetInstanceStringProvider02() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (KeyGeneratorTest.invalidValues.length); i++) {
            try {
                KeyGenerator.getInstance(KeyGeneratorTest.invalidValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /* Test for <code> getInstance(String algorithm, Provider provider)</code> method
    Assertions: returns KeyGenerator object
     */
    public void testGetInstanceStringProvider03() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        KeyGenerator keyA;
        for (int i = 0; i < (KeyGeneratorTest.validValues.length); i++) {
            keyA = KeyGenerator.getInstance(KeyGeneratorTest.validValues[i], KeyGeneratorTest.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", keyA.getAlgorithm(), KeyGeneratorTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", keyA.getProvider(), KeyGeneratorTest.defaultProvider);
        }
    }

    /* Test for <code>init(int keysize)</code> and
    <code>init(int keysize, SecureRandom random)</code> methods
    Assertion: throws InvalidParameterException if keysize is wrong
     */
    public void testInitKey() throws Exception {
        byte flag = 15;
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        if (KeyGeneratorTest.defaultAlgorithm.equals(KeyGeneratorTest.validAlgorithmsKeyGenerator[((KeyGeneratorTest.validAlgorithmsKeyGenerator.length) - 1)])) {
            return;
        }
        int[] size = new int[]{ Integer.MIN_VALUE, -1, 0, 112, 168, Integer.MAX_VALUE };
        KeyGenerator[] kgs = createKGs();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < (kgs.length); i++) {
            for (int j = 0; j < (size.length); j++) {
                try {
                    kgs[i].init(size[j]);
                    flag &= 14;
                } catch (InvalidParameterException ignore) {
                    flag &= 13;
                }
                try {
                    kgs[i].init(size[j], random);
                    flag &= 11;
                } catch (InvalidParameterException ignore) {
                    flag &= 7;
                }
            }
        }
        TestCase.assertTrue((flag == 0));
    }

    /* Test for <code>init(AlgorithmParameterSpec params)</code> and
    <code>init(AlgorithmParameterSpec params, SecureRandom random)</code> methods
    Assertion: throws InvalidAlgorithmParameterException when params is null
     */
    public void testInitParams() throws Exception {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        KeyGenerator[] kgs = createKGs();
        AlgorithmParameterSpec aps = null;
        for (int i = 0; i < (kgs.length); i++) {
            try {
                kgs[i].init(aps);
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
            try {
                kgs[i].init(aps, new SecureRandom());
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
        }
    }

    /* Test for <code>generateKey()</code> and
    <code>init(SecureRandom random)</code> methods
    <code>init(int keysize, SecureRandom random)</code> methods
    <code>init(int keysize)</code> methods
    <code>init(AlgorithmParameterSpec params, SecureRandom random)</code> methods
    <code>init(AlgorithmParameterSpec params)</code> methods
    Assertions:
    initializes KeyGenerator;
    returns SecretKey object
     */
    public void testGenerateKey() throws Exception {
        if (!(KeyGeneratorTest.DEFSupported)) {
            TestCase.fail(KeyGeneratorTest.NotSupportMsg);
            return;
        }
        SecretKey sKey;
        String dAl = KeyGeneratorTest.defaultAlgorithm.toUpperCase();
        KeyGenerator[] kgs = createKGs();
        for (int i = 0; i < (kgs.length); i++) {
            sKey = kgs[i].generateKey();
            TestCase.assertEquals("Incorrect algorithm", sKey.getAlgorithm().toUpperCase(), dAl);
            kgs[i].init(new SecureRandom());
            sKey = kgs[i].generateKey();
            TestCase.assertEquals("Incorrect algorithm", sKey.getAlgorithm().toUpperCase(), dAl);
            kgs[i].init(KeyGeneratorTest.defaultKeySize);
            sKey = kgs[i].generateKey();
            TestCase.assertEquals("Incorrect algorithm", sKey.getAlgorithm().toUpperCase(), dAl);
            kgs[i].init(KeyGeneratorTest.defaultKeySize, new SecureRandom());
            sKey = kgs[i].generateKey();
            TestCase.assertEquals("Incorrect algorithm", sKey.getAlgorithm().toUpperCase(), dAl);
        }
    }

    public void test_getAlgorithm() throws NoSuchAlgorithmException {
        KeyGenerator kg = null;
        for (int i = 0; i < (KeyGeneratorTest.validAlgorithmsKeyGenerator.length); i++) {
            kg = KeyGenerator.getInstance(KeyGeneratorTest.validAlgorithmsKeyGenerator[i]);
            TestCase.assertEquals(KeyGeneratorTest.validAlgorithmsKeyGenerator[i], kg.getAlgorithm());
        }
        kg = new myKeyGenerator(null, null, null);
        TestCase.assertNull(kg.getAlgorithm());
    }

    public void test_getProvider() throws NoSuchAlgorithmException {
        KeyGenerator kg = null;
        for (int i = 0; i < (KeyGeneratorTest.validAlgorithmsKeyGenerator.length); i++) {
            kg = KeyGenerator.getInstance(KeyGeneratorTest.validAlgorithmsKeyGenerator[i]);
            TestCase.assertNotNull(kg.getProvider());
        }
        kg = new myKeyGenerator(null, null, null);
        TestCase.assertNull(kg.getProvider());
    }

    public void test_initILjava_security_SecureRandom() throws NoSuchAlgorithmException {
        SecureRandom random = null;
        KeyGenerator kg = null;
        for (int i = 0; i < (KeyGeneratorTest.validAlgorithmsKeyGenerator.length); i++) {
            kg = KeyGenerator.getInstance(KeyGeneratorTest.validAlgorithmsKeyGenerator[i]);
            random = new SecureRandom();
            kg.init(KeyGeneratorTest.validKeySizes[i], random);
            TestCase.assertNotNull(kg.getProvider());
        }
        kg = KeyGenerator.getInstance(KeyGeneratorTest.validAlgorithmsKeyGenerator[0]);
        try {
            kg.init(5, random);
            TestCase.fail("InvalidParameterException expected");
        } catch (InvalidParameterException e) {
            // expected
        }
    }

    public void test_Ljava_security_SecureRandom() throws NoSuchAlgorithmException {
        SecureRandom random = null;
        KeyGenerator kg = null;
        for (int i = 0; i < (KeyGeneratorTest.validAlgorithmsKeyGenerator.length); i++) {
            kg = KeyGenerator.getInstance(KeyGeneratorTest.validAlgorithmsKeyGenerator[i]);
            random = new SecureRandom();
            kg.init(random);
            TestCase.assertNotNull(kg.getProvider());
        }
    }

    public void test_initLjava_security_spec_AlgorithmParameterSpec() throws Exception {
        KeyGenerator kg = null;
        IvParameterSpec aps = null;
        SecureRandom sr = new SecureRandom();
        byte[] iv = null;
        iv = new byte[8];
        sr.nextBytes(iv);
        aps = new IvParameterSpec(iv);
        for (int i = 0; i < (KeyGeneratorTest.validAlgorithmsKeyGenerator.length); i++) {
            kg = KeyGenerator.getInstance(KeyGeneratorTest.validAlgorithmsKeyGenerator[i]);
            try {
                kg.init(aps);
            } catch (InvalidAlgorithmParameterException e) {
            }
            TestCase.assertNotNull(kg.getProvider());
        }
    }
}

