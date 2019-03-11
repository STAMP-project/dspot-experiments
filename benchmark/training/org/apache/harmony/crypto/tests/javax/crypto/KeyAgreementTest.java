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


import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for KeyAgreement constructor and methods
 */
public class KeyAgreementTest extends TestCase {
    public static final String srvKeyAgreement = "KeyAgreement";

    private static String defaultAlgorithm = "DH";

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static boolean DEFSupported = false;

    private static final String NotSupportMsg = "There is no suitable provider for KeyAgreement";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String[] validValues = new String[]{ "DH", "dH", "Dh", "dh" };

    private static PrivateKey privKey = null;

    private static PublicKey publKey = null;

    private static boolean initKeys = false;

    static {
        KeyAgreementTest.defaultProvider = SpiEngUtils.isSupport(KeyAgreementTest.defaultAlgorithm, KeyAgreementTest.srvKeyAgreement);
        KeyAgreementTest.DEFSupported = (KeyAgreementTest.defaultProvider) != null;
        KeyAgreementTest.defaultProviderName = (KeyAgreementTest.DEFSupported) ? KeyAgreementTest.defaultProvider.getName() : null;
    }

    /**
     * Test for <code> getInstance(String algorithm) </code> method Assertions:
     * throws NullPointerException when algorithm is null throws
     * NoSuchAlgorithmException when algorithm isnot available
     */
    public void testGetInstanceString01() throws NoSuchAlgorithmException {
        try {
            KeyAgreement.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (KeyAgreementTest.invalidValues.length); i++) {
            try {
                KeyAgreement.getInstance(KeyAgreementTest.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code> getInstance(String algorithm) </code> method Assertions:
     * returns KeyAgreement object
     */
    public void testGetInstanceString02() throws NoSuchAlgorithmException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        KeyAgreement keyA;
        for (int i = 0; i < (KeyAgreementTest.validValues.length); i++) {
            keyA = KeyAgreement.getInstance(KeyAgreementTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", keyA.getAlgorithm(), KeyAgreementTest.validValues[i]);
        }
    }

    /**
     * Test for <code> getInstance(String algorithm, String provider)</code>
     * method Assertions: throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testGetInstanceStringString01() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        try {
            KeyAgreement.getInstance(null, KeyAgreementTest.defaultProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (KeyAgreementTest.invalidValues.length); i++) {
            try {
                KeyAgreement.getInstance(KeyAgreementTest.invalidValues[i], KeyAgreementTest.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code> getInstance(String algorithm, String provider)</code>
     * method Assertions: throws IllegalArgumentException when provider is null
     * or empty throws NoSuchProviderException when provider has not be
     * configured
     */
    public void testGetInstanceStringString02() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (KeyAgreementTest.validValues.length); i++) {
            try {
                KeyAgreement.getInstance(KeyAgreementTest.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                KeyAgreement.getInstance(KeyAgreementTest.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
            for (int j = 1; j < (KeyAgreementTest.invalidValues.length); j++) {
                try {
                    KeyAgreement.getInstance(KeyAgreementTest.validValues[i], KeyAgreementTest.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(KeyAgreementTest.validValues[i]).concat(" provider: ").concat(KeyAgreementTest.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code> getInstance(String algorithm, String provider)</code>
     * method Assertions: returns KeyAgreement object
     */
    public void testGetInstanceStringString03() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        KeyAgreement keyA;
        for (int i = 0; i < (KeyAgreementTest.validValues.length); i++) {
            keyA = KeyAgreement.getInstance(KeyAgreementTest.validValues[i], KeyAgreementTest.defaultProviderName);
            TestCase.assertEquals("Incorrect algorithm", keyA.getAlgorithm(), KeyAgreementTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", keyA.getProvider().getName(), KeyAgreementTest.defaultProviderName);
        }
    }

    /**
     * Test for <code> getInstance(String algorithm, Provider provider)</code>
     * method Assertions: throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm isnot available
     */
    public void testGetInstanceStringProvider01() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        try {
            KeyAgreement.getInstance(null, KeyAgreementTest.defaultProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown if algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (KeyAgreementTest.invalidValues.length); i++) {
            try {
                KeyAgreement.getInstance(KeyAgreementTest.invalidValues[i], KeyAgreementTest.defaultProvider);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code> getInstance(String algorithm, Provider provider)</code>
     * method Assertions: throws IllegalArgumentException when provider is null
     */
    public void testGetInstanceStringProvider02() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (KeyAgreementTest.invalidValues.length); i++) {
            try {
                KeyAgreement.getInstance(KeyAgreementTest.invalidValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code> getInstance(String algorithm, Provider provider)</code>
     * method Assertions: returns KeyAgreement object
     */
    public void testGetInstanceStringProvider03() throws IllegalArgumentException, NoSuchAlgorithmException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        KeyAgreement keyA;
        for (int i = 0; i < (KeyAgreementTest.validValues.length); i++) {
            keyA = KeyAgreement.getInstance(KeyAgreementTest.validValues[i], KeyAgreementTest.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", keyA.getAlgorithm(), KeyAgreementTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", keyA.getProvider(), KeyAgreementTest.defaultProvider);
        }
    }

    /**
     * Test for the methods: <code>init(Key key)</code>
     * <code>generateSecret()</code>
     * <code>generateSecret(byte[] sharedsecret, int offset)</code>
     * <code>generateSecret(String algorithm)</code>
     * Assertions: initializes KeyAgreement; returns sharedSecret; puts
     * sharedsecret in buffer and return numbers of bytes; returns SecretKey
     * object
     */
    public void testGenerateSecret03() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        byte[] bb;
        byte[] bb1 = new byte[10];
        for (int i = 0; i < (kAgs.length); i++) {
            kAgs[i].init(KeyAgreementTest.privKey);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bb = kAgs[i].generateSecret();
            kAgs[i].init(KeyAgreementTest.privKey);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bb1 = new byte[(bb.length) + 10];
            kAgs[i].generateSecret(bb1, 9);
            kAgs[i].init(KeyAgreementTest.privKey);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            kAgs[i].generateSecret("DES");
        }
    }

    /**
     * Test for <code>doPhase(Key key, boolean lastPhase)</code> method
     * Assertion: throws InvalidKeyException if key is not appropriate
     */
    public void testDoPhase() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        DHParameterSpec dhPs = ((DHPrivateKey) (KeyAgreementTest.privKey)).getParams();
        SecureRandom randomNull = null;
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < (kAgs.length); i++) {
            try {
                kAgs[i].doPhase(KeyAgreementTest.publKey, true);
                TestCase.fail("IllegalStateException expected");
            } catch (IllegalStateException e) {
                // expected
            }
            kAgs[i].init(KeyAgreementTest.privKey);
            try {
                kAgs[i].doPhase(KeyAgreementTest.privKey, false);
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].doPhase(KeyAgreementTest.privKey, true);
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            kAgs[i].init(KeyAgreementTest.privKey, dhPs);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            kAgs[i].init(KeyAgreementTest.privKey, dhPs, random);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
        }
    }

    /**
     * Test for the methods <code>init(Key key)</code>
     * <code>init(Key key, SecureRandom random)</code>
     * <code>init(Key key, AlgorithmParameterSpec params)</code>
     * <code>init(Key key, AlgorithmParameterSpec params, SecureRandom random)</code>
     * Assertion: throws InvalidKeyException when key is inappropriate
     */
    public void testInit01() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        SecureRandom random = null;
        AlgorithmParameterSpec aps = null;
        DHParameterSpec dhPs = new DHParameterSpec(new BigInteger("56"), new BigInteger("56"));
        for (int i = 0; i < (kAgs.length); i++) {
            try {
                kAgs[i].init(KeyAgreementTest.publKey);
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.publKey, new SecureRandom());
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.publKey, random);
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.publKey, dhPs);
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.publKey, aps);
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.publKey, dhPs, new SecureRandom());
                TestCase.fail("InvalidKeyException must be throw");
            } catch (InvalidKeyException e) {
            }
        }
    }

    /**
     * Test for the methods
     * <code>init(Key key, AlgorithmParameterSpec params)</code>
     * <code>init(Key key, AlgorithmParameterSpec params, SecureRandom random)</code>
     * Assertion: throws AlgorithmParameterException when params are
     * inappropriate
     */
    public void testInit02() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        SecureRandom random = null;
        DSAParameterSpec dsa = new DSAParameterSpec(new BigInteger("56"), new BigInteger("56"), new BigInteger("56"));
        for (int i = 0; i < (kAgs.length); i++) {
            try {
                kAgs[i].init(KeyAgreementTest.privKey, dsa);
                TestCase.fail("InvalidAlgorithmParameterException or InvalidKeyException must be throw");
            } catch (InvalidAlgorithmParameterException e) {
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.privKey, dsa, new SecureRandom());
                TestCase.fail("InvalidAlgorithmParameterException or InvalidKeyException must be throw");
            } catch (InvalidAlgorithmParameterException e) {
            } catch (InvalidKeyException e) {
            }
            try {
                kAgs[i].init(KeyAgreementTest.privKey, dsa, random);
                TestCase.fail("InvalidAlgorithmParameterException or InvalidKeyException must be throw");
            } catch (InvalidAlgorithmParameterException e) {
            } catch (InvalidKeyException e) {
            }
        }
    }

    /**
     * Test for the methods: <code>init(Key key)</code>
     * <code>init(Key key, SecureRandom random)</code>
     * <code>generateSecret()</code>
     * Assertions: initializes KeyAgreement and returns byte array
     */
    public void testInit03() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        byte[] bbRes1;
        byte[] bbRes2;
        byte[] bbRes3;
        SecureRandom randomNull = null;
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < (kAgs.length); i++) {
            kAgs[i].init(KeyAgreementTest.privKey);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bbRes1 = kAgs[i].generateSecret();
            kAgs[i].init(KeyAgreementTest.privKey, random);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bbRes2 = kAgs[i].generateSecret();
            TestCase.assertEquals("Incorrect byte array length", bbRes1.length, bbRes2.length);
            for (int j = 0; j < (bbRes1.length); j++) {
                TestCase.assertEquals("Incorrect byte (index: ".concat(Integer.toString(i)).concat(")"), bbRes1[j], bbRes2[j]);
            }
            kAgs[i].init(KeyAgreementTest.privKey, randomNull);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bbRes3 = kAgs[i].generateSecret();
            TestCase.assertEquals("Incorrect byte array length", bbRes1.length, bbRes3.length);
            for (int j = 0; j < (bbRes1.length); j++) {
                TestCase.assertEquals("Incorrect byte (index: ".concat(Integer.toString(i)).concat(")"), bbRes1[j], bbRes3[j]);
            }
        }
    }

    /**
     * Test for the methods:
     * <code>init(Key key, AlgorithmParameterSpec params)</code>
     * <code>init(Key key, AlgorithmParameterSpec params, SecureRandom random)</code>
     * <code>generateSecret()</code>
     * Assertions: initializes KeyAgreement and returns byte array
     */
    public void testInit04() throws Exception, InvalidAlgorithmParameterException {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        DHParameterSpec dhPs = ((DHPrivateKey) (KeyAgreementTest.privKey)).getParams();
        AlgorithmParameterSpec aps = new RSAKeyGenParameterSpec(10, new BigInteger("10"));
        byte[] bbRes1;
        byte[] bbRes2;
        byte[] bbRes3;
        SecureRandom randomNull = null;
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < (kAgs.length); i++) {
            kAgs[i].init(KeyAgreementTest.privKey, dhPs);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bbRes1 = kAgs[i].generateSecret();
            kAgs[i].init(KeyAgreementTest.privKey, dhPs, random);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bbRes2 = kAgs[i].generateSecret();
            TestCase.assertEquals("Incorrect byte array length", bbRes1.length, bbRes2.length);
            for (int j = 0; j < (bbRes1.length); j++) {
                TestCase.assertEquals("Incorrect byte (index: ".concat(Integer.toString(i)).concat(")"), bbRes1[j], bbRes2[j]);
            }
            kAgs[i].init(KeyAgreementTest.privKey, dhPs, randomNull);
            kAgs[i].doPhase(KeyAgreementTest.publKey, true);
            bbRes3 = kAgs[i].generateSecret();
            TestCase.assertEquals("Incorrect byte array length", bbRes1.length, bbRes3.length);
            for (int j = 0; j < (bbRes1.length); j++) {
                TestCase.assertEquals("Incorrect byte (index: ".concat(Integer.toString(i)).concat(")"), bbRes1[j], bbRes3[j]);
            }
            try {
                kAgs[i].init(KeyAgreementTest.publKey, dhPs, random);
                TestCase.fail("InvalidKeyException expected");
            } catch (InvalidKeyException e) {
                // expected
            }
            try {
                kAgs[i].init(KeyAgreementTest.privKey, aps, random);
                TestCase.fail("InvalidAlgorithmParameterException expected");
            } catch (InvalidAlgorithmParameterException e) {
                // expected
            }
        }
    }

    class Mock_KeyAgreement extends KeyAgreement {
        protected Mock_KeyAgreement(KeyAgreementSpi arg0, Provider arg1, String arg2) {
            super(arg0, arg1, arg2);
        }
    }

    public void test_constructor() {
        TestCase.assertNotNull(new KeyAgreementTest.Mock_KeyAgreement(null, null, null));
    }

    public void test_getAlgorithm() throws NoSuchAlgorithmException {
        KeyAgreementTest.Mock_KeyAgreement mka = new KeyAgreementTest.Mock_KeyAgreement(null, null, null);
        TestCase.assertNull(mka.getAlgorithm());
        KeyAgreement keyA;
        for (int i = 0; i < (KeyAgreementTest.validValues.length); i++) {
            keyA = KeyAgreement.getInstance(KeyAgreementTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", keyA.getAlgorithm(), KeyAgreementTest.validValues[i]);
        }
    }

    public void test_getProvider() throws NoSuchAlgorithmException {
        KeyAgreement keyA;
        for (int i = 0; i < (KeyAgreementTest.validValues.length); i++) {
            keyA = KeyAgreement.getInstance(KeyAgreementTest.validValues[i]);
            TestCase.assertNotNull(keyA.getProvider());
        }
    }

    public void test_generateSecret$BI() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        byte[] bb1 = new byte[1];
        try {
            ka.generateSecret(bb1, 0);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        ka.init(KeyAgreementTest.privKey);
        ka.doPhase(KeyAgreementTest.publKey, true);
        try {
            ka.generateSecret(bb1, 0);
            TestCase.fail("ShortBufferException expected");
        } catch (ShortBufferException e) {
            // expected
        }
    }

    public void test_generateSecretLjava_lang_String() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        byte[] bb1 = new byte[1];
        try {
            ka.generateSecret("dh");
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        ka.init(KeyAgreementTest.privKey);
        ka.doPhase(KeyAgreementTest.publKey, true);
        try {
            ka.generateSecret("Wrong alg name");
            TestCase.fail("NoSuchAlgorithmException expected");
        } catch (NoSuchAlgorithmException e) {
            // expected
        }
    }

    public void test_initLjava_security_KeyLjava_security_SecureRandom() throws Exception {
        if (!(KeyAgreementTest.DEFSupported)) {
            TestCase.fail(KeyAgreementTest.NotSupportMsg);
            return;
        }
        createKeys();
        KeyAgreement[] kAgs = createKAs();
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(KeyAgreementTest.privKey, new SecureRandom());
        try {
            ka.init(KeyAgreementTest.publKey, new SecureRandom());
            TestCase.fail("InvalidKeyException expected");
        } catch (InvalidKeyException e) {
            // expected
        }
    }
}

