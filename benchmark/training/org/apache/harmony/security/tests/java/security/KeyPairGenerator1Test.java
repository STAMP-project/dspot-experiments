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
package org.apache.harmony.security.tests.java.security;


import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MyKeyPairGenerator1;
import org.apache.harmony.security.tests.support.MyKeyPairGenerator2;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>KeyPairGenerator</code> class constructors and methods.
 */
public class KeyPairGenerator1Test extends TestCase {
    private static String[] invalidValues = SpiEngUtils.invalidValues;

    public static final String srvKeyPairGenerator = "KeyPairGenerator";

    public static String[] algs = new String[]{ "DSA", "dsa", "Dsa", "DsA", "dsA" };

    public static String validAlgName = "DSA";

    private static String validProviderName = null;

    public static Provider validProvider = null;

    private static boolean DSASupported = false;

    public static String NotSupportMsg = "";

    static {
        KeyPairGenerator1Test.validProvider = SpiEngUtils.isSupport(KeyPairGenerator1Test.validAlgName, KeyPairGenerator1Test.srvKeyPairGenerator);
        KeyPairGenerator1Test.DSASupported = (KeyPairGenerator1Test.validProvider) != null;
        if (!(KeyPairGenerator1Test.DSASupported)) {
            KeyPairGenerator1Test.NotSupportMsg = (KeyPairGenerator1Test.validAlgName) + " algorithm is not supported";
        }
        KeyPairGenerator1Test.validProviderName = (KeyPairGenerator1Test.DSASupported) ? KeyPairGenerator1Test.validProvider.getName() : null;
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException  when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is incorrect;
     */
    public void testKeyPairGenerator01() throws NoSuchAlgorithmException {
        try {
            KeyPairGenerator.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown  when algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (KeyPairGenerator1Test.invalidValues.length); i++) {
            try {
                KeyPairGenerator.getInstance(KeyPairGenerator1Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown when algorithm is not available: ".concat(KeyPairGenerator1Test.invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns KeyPairGenerator object
     */
    public void testKeyPairGenerator02() throws NoSuchAlgorithmException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        KeyPairGenerator kpg;
        for (int i = 0; i < (KeyPairGenerator1Test.algs.length); i++) {
            kpg = KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i]);
            TestCase.assertEquals("Incorrect algorithm ", kpg.getAlgorithm().toUpperCase(), KeyPairGenerator1Test.algs[i].toUpperCase());
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null or empty
     */
    public void testKeyPairGenerator03() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (KeyPairGenerator1Test.algs.length); i++) {
            try {
                KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws NoSuchProviderException when provider is not available
     */
    public void testKeyPairGenerator04() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (KeyPairGenerator1Test.algs.length); i++) {
            for (int j = 1; j < (KeyPairGenerator1Test.invalidValues.length); j++) {
                try {
                    KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i], KeyPairGenerator1Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(KeyPairGenerator1Test.algs[i]).concat(" provider: ").concat(KeyPairGenerator1Test.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws NoSuchAlgorithmException when algorithm is not
     * available
     * throws NullPointerException  when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is incorrect;
     */
    public void testKeyPairGenerator05() throws IllegalArgumentException, NoSuchProviderException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        try {
            KeyPairGenerator.getInstance(null, KeyPairGenerator1Test.validProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown  when algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (KeyPairGenerator1Test.invalidValues.length); i++) {
            try {
                KeyPairGenerator.getInstance(KeyPairGenerator1Test.invalidValues[i], KeyPairGenerator1Test.validProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(KeyPairGenerator1Test.algs[i]).concat(" provider: ").concat(KeyPairGenerator1Test.validProviderName).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: returns KeyPairGenerator object
     */
    public void testKeyPairGenerator06() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        KeyPairGenerator kpg;
        for (int i = 0; i < (KeyPairGenerator1Test.algs.length); i++) {
            kpg = KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i], KeyPairGenerator1Test.validProviderName);
            TestCase.assertEquals("Incorrect algorithm", kpg.getAlgorithm().toUpperCase(), KeyPairGenerator1Test.algs[i].toUpperCase());
            TestCase.assertEquals("Incorrect provider", kpg.getProvider().getName(), KeyPairGenerator1Test.validProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testKeyPairGenerator07() throws NoSuchAlgorithmException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (KeyPairGenerator1Test.algs.length); i++) {
            try {
                KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion:
     * throws NullPointerException  when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is incorrect;
     */
    public void testKeyPairGenerator08() throws IllegalArgumentException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        try {
            KeyPairGenerator.getInstance(null, KeyPairGenerator1Test.validProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown  when algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (KeyPairGenerator1Test.invalidValues.length); i++) {
            try {
                KeyPairGenerator.getInstance(KeyPairGenerator1Test.invalidValues[i], KeyPairGenerator1Test.validProvider);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(KeyPairGenerator1Test.algs[i]).concat(" provider: ").concat(KeyPairGenerator1Test.validProviderName).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: returns KeyPairGenerator object
     */
    public void testKeyPairGenerator09() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        KeyPairGenerator kpg;
        for (int i = 0; i < (KeyPairGenerator1Test.algs.length); i++) {
            kpg = KeyPairGenerator.getInstance(KeyPairGenerator1Test.algs[i], KeyPairGenerator1Test.validProvider);
            TestCase.assertEquals("Incorrect algorithm", kpg.getAlgorithm().toUpperCase(), KeyPairGenerator1Test.algs[i].toUpperCase());
            TestCase.assertEquals("Incorrect provider", kpg.getProvider(), KeyPairGenerator1Test.validProvider);
        }
    }

    /**
     * Test for <code>generateKeyPair()</code> and <code>genKeyPair()</code>
     * methods
     * Assertion: KeyPairGenerator was initialized before the invocation
     * of these methods
     */
    public void testKeyPairGenerator10() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        KeyPairGenerator[] kpg = createKPGen();
        TestCase.assertNotNull("KeyPairGenerator objects were not created", kpg);
        KeyPair kp;
        KeyPair kp1;
        for (int i = 0; i < (kpg.length); i++) {
            kpg[i].initialize(512);
            kp = kpg[i].generateKeyPair();
            kp1 = kpg[i].genKeyPair();
            TestCase.assertFalse("Incorrect private key", kp.getPrivate().equals(kp1.getPrivate()));
            TestCase.assertFalse("Incorrect public key", kp.getPublic().equals(kp1.getPublic()));
        }
    }

    /**
     * Test for methods:
     * <code>initialize(int keysize)</code>
     * <code>initialize(int keysize, SecureRandom random)</code>
     * <code>initialize(AlgorithmParameterSpec param)</code>
     * <code>initialize(AlgorithmParameterSpec param, SecureRandom random)</code>
     * Assertion: throws InvalidParameterException or
     * InvalidAlgorithmParameterException when parameters keysize or param are
     * incorrect
     */
    public void testKeyPairGenerator11() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyPairGenerator1Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator1Test.NotSupportMsg);
            return;
        }
        int[] keys = new int[]{ -10000, -1024, -1, 0, 10000 };
        KeyPairGenerator[] kpg = createKPGen();
        TestCase.assertNotNull("KeyPairGenerator objects were not created", kpg);
        SecureRandom random = new SecureRandom();
        AlgorithmParameterSpec aps = null;
        for (int i = 0; i < (kpg.length); i++) {
            for (int j = 0; j < (keys.length); j++) {
                try {
                    kpg[i].initialize(keys[j]);
                    kpg[i].initialize(keys[j], random);
                } catch (InvalidParameterException e) {
                }
            }
            try {
                kpg[i].initialize(aps);
                kpg[i].initialize(aps, random);
            } catch (InvalidAlgorithmParameterException e) {
            }
        }
    }

    /**
     * Test for methods: <code>initialize(int keysize)</code>
     * <code>initialize(int keysize, SecureRandom random)</code>
     * <code>initialize(AlgorithmParameterSpec param)</code>
     * <code>initialize(AlgorithmParameterSpec param, SecureRandom random)</code>
     * <code>generateKeyPair()</code>
     * <code>genKeyPair()</code>
     * Assertion: throws InvalidParameterException or
     * InvalidAlgorithmParameterException when parameters keysize or param are
     * incorrect Assertion: generateKeyPair() and genKeyPair() return null
     * KeyPair Additional class MyKeyPairGenerator1 is used
     */
    public void testKeyPairGenerator12() {
        int[] keys = new int[]{ -1, -250, 1, 64, 512, 1024 };
        SecureRandom random = new SecureRandom();
        AlgorithmParameterSpec aps;
        KeyPairGenerator mKPG = new MyKeyPairGenerator1("");
        TestCase.assertEquals("Incorrect algorithm", mKPG.getAlgorithm(), MyKeyPairGenerator1.getResAlgorithm());
        mKPG.generateKeyPair();
        mKPG.genKeyPair();
        for (int i = 0; i < (keys.length); i++) {
            try {
                mKPG.initialize(keys[i]);
                TestCase.fail((("InvalidParameterException must be thrown (key: " + (Integer.toString(keys[i]))) + ")"));
            } catch (InvalidParameterException e) {
            }
            try {
                mKPG.initialize(keys[i], random);
                TestCase.fail((("InvalidParameterException must be thrown (key: " + (Integer.toString(keys[i]))) + ")"));
            } catch (InvalidParameterException e) {
            }
        }
        try {
            mKPG.initialize(100, null);
            TestCase.fail("InvalidParameterException must be thrown when random is null");
        } catch (InvalidParameterException e) {
        }
        mKPG.initialize(100, random);
        TestCase.assertEquals("Incorrect random", random, ((MyKeyPairGenerator1) (mKPG)).secureRandom);
        TestCase.assertEquals("Incorrect keysize", 100, ((MyKeyPairGenerator1) (mKPG)).keySize);
        try {
            mKPG.initialize(null, random);
            TestCase.fail("InvalidAlgorithmParameterException must be thrown when param is null");
        } catch (InvalidAlgorithmParameterException e) {
        }
        if (KeyPairGenerator1Test.DSASupported) {
            BigInteger bInt = new BigInteger("1");
            aps = new DSAParameterSpec(bInt, bInt, bInt);
            try {
                mKPG.initialize(aps, null);
                TestCase.fail("InvalidParameterException must be thrown when random is null");
            } catch (InvalidParameterException e) {
            } catch (InvalidAlgorithmParameterException e) {
                TestCase.fail("Unexpected InvalidAlgorithmParameterException was thrown");
            }
            try {
                mKPG.initialize(aps, random);
                TestCase.assertEquals("Incorrect random", random, ((MyKeyPairGenerator1) (mKPG)).secureRandom);
                TestCase.assertEquals("Incorrect params", aps, ((MyKeyPairGenerator1) (mKPG)).paramSpec);
            } catch (InvalidAlgorithmParameterException e) {
                TestCase.fail("Unexpected InvalidAlgorithmParameterException was thrown");
            }
        }
    }

    /**
     * Test for methods: <code>initialize(int keysize)</code>
     * <code>initialize(int keysize, SecureRandom random)</code>
     * <code>initialize(AlgorithmParameterSpec param)</code>
     * <code>initialize(AlgorithmParameterSpec param, SecureRandom random)</code>
     * <code>generateKeyPair()</code>
     * <code>genKeyPair()</code>
     * Assertion: initialize(int ...) throws InvalidParameterException when
     * keysize in incorrect Assertion: initialize(AlgorithmParameterSpec
     * ...)throws UnsupportedOperationException Assertion: generateKeyPair() and
     * genKeyPair() return not null KeyPair Additional class MyKeyPairGenerator2
     * is used
     */
    public void testKeyPairGenerator13() {
        int[] keys = new int[]{ -1, -250, 1, 63, -512, -1024 };
        SecureRandom random = new SecureRandom();
        KeyPairGenerator mKPG = new MyKeyPairGenerator2(null);
        TestCase.assertEquals("Algorithm must be null", mKPG.getAlgorithm(), MyKeyPairGenerator2.getResAlgorithm());
        TestCase.assertNull("genKeyPair() must return null", mKPG.genKeyPair());
        TestCase.assertNull("generateKeyPair() mut return null", mKPG.generateKeyPair());
        for (int i = 0; i < (keys.length); i++) {
            try {
                mKPG.initialize(keys[i]);
                TestCase.fail((("InvalidParameterException must be thrown (key: " + (Integer.toString(keys[i]))) + ")"));
            } catch (InvalidParameterException e) {
            }
            try {
                mKPG.initialize(keys[i], random);
                TestCase.fail((("InvalidParameterException must be thrown (key: " + (Integer.toString(keys[i]))) + ")"));
            } catch (InvalidParameterException e) {
            }
        }
        try {
            mKPG.initialize(64);
        } catch (InvalidParameterException e) {
            TestCase.fail("Unexpected InvalidParameterException was thrown");
        }
        try {
            mKPG.initialize(64, null);
        } catch (InvalidParameterException e) {
            TestCase.fail("Unexpected InvalidParameterException was thrown");
        }
        try {
            mKPG.initialize(null, random);
        } catch (UnsupportedOperationException e) {
            // on j2se1.4 this exception is not thrown
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail("Unexpected InvalidAlgorithmParameterException was thrown");
        }
    }
}

